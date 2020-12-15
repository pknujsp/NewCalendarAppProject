package com.zerodsoft.scheduleweather.activity.map.fragment.map;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.map.MapActivity;
import com.zerodsoft.scheduleweather.activity.map.fragment.interfaces.ICatchedLocation;
import com.zerodsoft.scheduleweather.activity.map.fragment.interfaces.IMapData;
import com.zerodsoft.scheduleweather.activity.map.fragment.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.activity.map.fragment.search.SearchFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.SearchResultFragmentController;
import com.zerodsoft.scheduleweather.activity.map.util.RequestLocationTimer;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.AddressViewModel;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.util.List;
import java.util.Timer;

public class MapFragment extends Fragment implements MapView.POIItemEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.MapViewEventListener, IMapPoint, IMapData
{
    // list에서 item클릭 시 poiitem이 선택되고 맵 중앙좌표가 해당item의 좌표로 변경되면서 하단 시트가 올라온다
    public static final String TAG = "MapFragment";
    private static MapFragment instance;

    private MapPoint currentMapPoint;
    private MapView mapView;
    private FrameLayout mapViewContainer;
    private LocationManager locationManager;

    private ImageButton gpsButton;
    private TextView currentAddress;

    private MapReverseGeoCoder mapReverseGeoCoder;
    private BottomSheetBehavior bottomSheetBehavior;

    private LinearLayout bottomSheet;
    private LinearLayout headerBar;

    private AddressViewModel addressViewModel;
    private PlacesViewModel placeViewModel;
    private ICatchedLocation iCatchedLocation;

    private AddressResponseDocuments selectedAddressDocument;
    private PlaceDocuments selectedPlaceDocument;

    private OnBackPressedCallback onBackPressedCallback;

    private ConnectivityManager.NetworkCallback networkCallback;
    private BottomSheetItemView bottomSheetPlaceItemView;
    private BottomSheetItemView bottomSheetAddressItemView;

    private ConnectivityManager connectivityManager;
    private String appKey;

    private static final int PLACE_ITEM = 0;
    private static final int ADDRESS_ITEM = 1;
    private int selectedPoiItemIndex;

    public MapFragment(ICatchedLocation iCatchedLocation)
    {
        this.iCatchedLocation = iCatchedLocation;
    }

    public static MapFragment getInstance()
    {
        return instance;
    }

    public static MapFragment newInstance(ICatchedLocation iCatchedLocation)
    {
        instance = new MapFragment(iCatchedLocation);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setNetworkCallback();
    }

    private final LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            setMapCenterPoint(location.getLatitude(), location.getLongitude());
            mapReverseGeoCoder = new MapReverseGeoCoder(appKey, mapView.getMapCenterPoint(), MapFragment.this, getActivity());
            mapReverseGeoCoder.startFindingAddress(MapReverseGeoCoder.AddressType.FullAddress);
            locationManager.removeUpdates(locationListener);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle)
        {

        }

        @Override
        public void onProviderEnabled(String s)
        {

        }

        @Override
        public void onProviderDisabled(String s)
        {

        }
    };

    private void setMapCenterPoint(double latitude, double longitude)
    {
        currentMapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
        mapView.setMapCenterPoint(currentMapPoint, true);
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        boolean isSearchResultState = false;
        for (Fragment fragment : fragments)
        {
            if (fragment instanceof SearchResultFragmentController)
            {
                //검색 결과를 보여주고 있는 경우
                isSearchResultState = true;
            }
        }

        onBackPressedCallback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                ((MapActivity) getActivity()).getOnBackPressedDispatcher().onBackPressed();
            }
        };

        if (!isSearchResultState)
        {
            requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        onBackPressedCallback.remove();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        headerBar = (LinearLayout) view.findViewById(R.id.map_header_bar);
        mapViewContainer = (FrameLayout) view.findViewById(R.id.map_view);
        bottomSheet = (LinearLayout) view.findViewById(R.id.map_item_bottom_sheet);

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        bottomSheetPlaceItemView = (BottomSheetItemView) LayoutInflater.from(getContext()).inflate(R.layout.map_bottom_sheet_place, bottomSheet, false);
        bottomSheetAddressItemView = (BottomSheetItemView) LayoutInflater.from(getContext()).inflate(R.layout.map_bottom_sheet_address, bottomSheet, false);
        bottomSheet.addView(bottomSheetPlaceItemView, PLACE_ITEM);
        bottomSheet.addView(bottomSheetAddressItemView, ADDRESS_ITEM);

        bottomSheetAddressItemView.setVisibility(View.GONE);
        bottomSheetPlaceItemView.setVisibility(View.GONE);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setDraggable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
             /*
                STATE_COLLAPSED: 기본적인 상태이며, 일부분의 레이아웃만 보여지고 있는 상태. 이 높이는 behavior_peekHeight속성을 통해 변경 가능
                STATE_DRAGGING: 드래그중인 상태
                STATE_SETTLING: 드래그후 완전히 고정된 상태
                STATE_EXPANDED: 확장된 상태
                STATE_HIDDEN: 기본적으로 비활성화 상태이며, app:behavior_hideable을 사용하는 경우 완전히 숨겨져 있는 상태
             */
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {
                if (slideOffset == 0)
                {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });

        headerBar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.map_activity_fragment_container, SearchFragment.newInstance(MapFragment.this), SearchFragment.TAG)
                        .hide(MapFragment.this).commit();
            }
        });

        ((ImageButton) view.findViewById(R.id.zoom_in_button)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mapView.zoomIn(true);
            }
        });

        ((ImageButton) view.findViewById(R.id.zoom_out_button)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mapView.zoomOut(true);
            }
        });

        gpsButton = (ImageButton) view.findViewById(R.id.gps_button);
        gpsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                int fineLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
                int coarseLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

                if (checkNetwork())
                {
                    if (isGpsEnabled && isNetworkEnabled)
                    {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        Timer timer = new Timer();
                        timer.schedule(new RequestLocationTimer()
                        {
                            @Override
                            public void run()
                            {
                                timer.cancel();
                                getActivity().runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        locationManager.removeUpdates(locationListener);
                                        int fineLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
                                        int coarseLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
                                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                                    }
                                });

                            }
                        }, 2000);
                    } else if (!isGpsEnabled)
                    {
                        showRequestDialog();
                    }
                } else
                {
                    Toast.makeText(getActivity(), getString(R.string.map_network_not_connected), Toast.LENGTH_SHORT).show();
                }
            }
        });

        currentAddress = (TextView) view.findViewById(R.id.current_address);
        initMapView();
    }

    private boolean checkNetwork()
    {
        if (connectivityManager.getActiveNetwork() == null)
        {
            return false;
        } else
        {
            NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

            if (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
            {
                return true;
            } else
            {
                return false;
            }
        }
    }

    private void setNetworkCallback()
    {
        connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkCallback = new ConnectivityManager.NetworkCallback()
        {
            @Override
            public void onAvailable(Network network)
            {
                super.onAvailable(network);
                Toast.makeText(getActivity(), "재 연결됨", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLost(Network network)
            {
                super.onLost(network);
                Toast.makeText(getActivity(), "연결 끊김", Toast.LENGTH_SHORT).show();
            }
        };
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
        builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
        connectivityManager.registerNetworkCallback(builder.build(), networkCallback);
    }


    private void initMapView()
    {
        mapView = new MapView(requireActivity());
        mapViewContainer.addView(mapView);

        mapView.setPOIItemEventListener(this);
        mapView.setMapViewEventListener(this);

        ApplicationInfo ai = null;
        try
        {
            ai = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        appKey = ai.metaData.getString("com.kakao.sdk.AppKey");
        mapReverseGeoCoder = new MapReverseGeoCoder(appKey, currentMapPoint, this, getActivity());
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        if (checkNetwork())
        {
            LocationDTO selectedLocation = iCatchedLocation.getLocation();

            if (selectedLocation != null)
            {
                if (selectedLocation instanceof AddressDTO)
                {
                    // 주소 검색 순서 : 좌표로 주소 변환
                    AddressDTO address = iCatchedLocation.getAddress();
                    addressViewModel = new ViewModelProvider(this).get(AddressViewModel.class);

                    LocalApiPlaceParameter parameter = new LocalApiPlaceParameter();
                    parameter.setX(address.getLongitude()).setY(address.getLatitude());
                    addressViewModel.init(parameter);

                    addressViewModel.getPagedListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<AddressResponseDocuments>>()
                    {
                        @Override
                        public void onChanged(PagedList<AddressResponseDocuments> addressResponseDocuments)
                        {
                            //주소는 바로 나온다, 해당 좌표를 설정
                            try
                            {
                                selectedAddressDocument = (AddressResponseDocuments) addressResponseDocuments.get(0).clone();
                            } catch (CloneNotSupportedException e)
                            {
                                e.printStackTrace();
                            }
                            setMapCenterPoint(selectedAddressDocument.getY(), selectedAddressDocument.getX());
                            mapView.removeAllPOIItems();
                            createPoiItem(selectedAddressDocument.getAddressName());
                            mapView.selectPOIItem(mapView.getPOIItems()[0], false);
                        }
                    });
                } else if (selectedLocation instanceof PlaceDTO)
                {
                    // 장소 검색 순서 : 장소의 위경도 내 10M 반경에서 장소 이름 검색(여러개 나올 경우 장소ID와 일치하는 장소를 선택)
                    PlaceDTO place = iCatchedLocation.getPlace();
                    placeViewModel = new ViewModelProvider(this).get(PlacesViewModel.class);

                    LocalApiPlaceParameter parameter = new LocalApiPlaceParameter();
                    parameter.setX(place.getLongitude()).setY(place.getLatitude()).setPage(LocalApiPlaceParameter.DEFAULT_PAGE)
                            .setSize(LocalApiPlaceParameter.DEFAULT_SIZE).setSort(LocalApiPlaceParameter.SORT_ACCURACY)
                            .setRadius("10").setQuery(place.getPlaceName());
                    placeViewModel.init(parameter);

                    placeViewModel.getPagedListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<PlaceDocuments>>()
                    {
                        @Override
                        public void onChanged(PagedList<PlaceDocuments> placeDocuments)
                        {
                            //찾는 장소의 ID와 일치하는 장소가 있는지 확인
                            List<PlaceDocuments> placeDocumentsList = placeDocuments.snapshot();
                            PlaceDTO place = iCatchedLocation.getPlace();

                            for (PlaceDocuments document : placeDocumentsList)
                            {
                                if (place.getId() == Integer.parseInt(document.getId()))
                                {
                                    try
                                    {
                                        selectedPlaceDocument = (PlaceDocuments) document.clone();
                                    } catch (CloneNotSupportedException e)
                                    {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                            }
                            setMapCenterPoint(selectedPlaceDocument.getY(), selectedPlaceDocument.getX());
                            mapView.removeAllPOIItems();
                            createPoiItem(selectedPlaceDocument.getPlaceName());
                            mapView.selectPOIItem(mapView.getPOIItems()[0], false);
                        }
                    });
                }
            } else
            {
                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                int fineLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
                int coarseLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

                if (isGpsEnabled && isNetworkEnabled)
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Timer timer = new Timer();
                    timer.schedule(new RequestLocationTimer()
                    {
                        @Override
                        public void run()
                        {
                            timer.cancel();
                            getActivity().runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    locationManager.removeUpdates(locationListener);
                                    int fineLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
                                    int coarseLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
                                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                                }
                            });

                        }
                    }, 2000);
                } else if (!isGpsEnabled)
                {
                    showRequestDialog();
                }
            }
        } else
        {
            Toast.makeText(getActivity(), getString(R.string.map_network_not_connected), Toast.LENGTH_SHORT).show();
        }

    }

    private void showRequestDialog()
    {
        new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.request_to_make_gps_on))
                .setPositiveButton(getString(R.string.check), new
                        DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt)
                            {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                    }
                })
                .setCancelable(false)
                .show();
    }

    private void createPoiItem(String itemName)
    {
        MapPOIItem poiItem = new MapPOIItem();
        poiItem.setItemName(itemName);
        poiItem.setMapPoint(currentMapPoint);
        poiItem.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        mapView.removeAllPOIItems();
        mapView.addPOIItem(poiItem);
    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String
            address)
    {
        currentAddress.setText(address);
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder)
    {

    }

    @Override
    public void onMapViewInitialized(MapView mapView)
    {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint)
    {
        //지도가 움직일 때 마다 호출된다
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i)
    {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint)
    {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
        {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint)
    {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint)
    {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint)
    {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint)
    {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint)
    {
        if (checkNetwork())
        {
            mapReverseGeoCoder = new MapReverseGeoCoder(appKey, mapPoint, this, getActivity());
            mapReverseGeoCoder.startFindingAddress(MapReverseGeoCoder.AddressType.FullAddress);
        } else
        {
            Toast.makeText(getActivity(), getString(R.string.map_network_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem)
    {
        // poiitem을 선택하였을 경우에 수행됨
        if (((CustomPoiItem) mapPOIItem).getAddressDocument() != null)
        {
            bottomSheetAddressItemView.setAddress(((CustomPoiItem) mapPOIItem).getAddressDocument());
            bottomSheetAddressItemView.setVisibility(View.VISIBLE);
            bottomSheetPlaceItemView.setVisibility(View.GONE);
        } else if (((CustomPoiItem) mapPOIItem).getPlaceDocument() != null)
        {
            bottomSheetPlaceItemView.setPlace(((CustomPoiItem) mapPOIItem).getPlaceDocument());
            bottomSheetAddressItemView.setVisibility(View.GONE);
            bottomSheetPlaceItemView.setVisibility(View.VISIBLE);
        }
        setMapCenterPoint(mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude, mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }


    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem)
    {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem
            mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType)
    {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint
            mapPoint)
    {

    }

    @Override
    public double getLatitude()
    {
        return mapView.getMapCenterPoint().getMapPointGeoCoord().latitude;
    }

    @Override
    public double getLongitude()
    {
        return mapView.getMapCenterPoint().getMapPointGeoCoord().longitude;
    }

    @Override
    public void createPlacesPoiItems(List<PlaceDocuments> placeDocuments)
    {
        mapView.removeAllPOIItems();

        if (!placeDocuments.isEmpty())
        {
            CustomPoiItem[] poiItems = new CustomPoiItem[placeDocuments.size()];

            int index = 0;
            for (PlaceDocuments document : placeDocuments)
            {
                poiItems[index] = new CustomPoiItem();
                poiItems[index].setItemName(document.getPlaceName());
                poiItems[index].setMapPoint(MapPoint.mapPointWithGeoCoord(document.getY(), document.getX()));
                poiItems[index].setPlaceDocument(document);
                poiItems[index].setTag(index);
                poiItems[index].setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                poiItems[index].setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                index++;
            }
            mapView.addPOIItems(poiItems);
        }
    }

    @Override
    public void createAddressesPoiItems(List<AddressResponseDocuments> addressDocuments)
    {
        mapView.removeAllPOIItems();

        if (!addressDocuments.isEmpty())
        {
            CustomPoiItem[] poiItems = new CustomPoiItem[addressDocuments.size()];

            int index = 0;
            for (AddressResponseDocuments document : addressDocuments)
            {
                poiItems[index] = new CustomPoiItem();
                poiItems[index].setItemName(document.getAddressName());
                poiItems[index].setMapPoint(MapPoint.mapPointWithGeoCoord(document.getY(), document.getX()));
                poiItems[index].setAddressDocument(document);
                poiItems[index].setTag(index);
                poiItems[index].setMarkerType(MapPOIItem.MarkerType.BluePin);
                poiItems[index].setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                index++;
            }
            mapView.addPOIItems(poiItems);
        }
    }

    @Override
    public void selectPoiItem(int index)
    {
        mapView.selectPOIItem(mapView.getPOIItems()[index], false);
        selectedPoiItemIndex = index;
    }

    @Override
    public void removeAllPoiItems()
    {
        mapView.removeAllPOIItems();
    }

    @Override
    public void showAllPoiItems()
    {
        mapView.fitMapViewAreaToShowAllPOIItems();
    }

    @Override
    public void deselectPoiItem()
    {
        mapView.deselectPOIItem(mapView.getPOIItems()[selectedPoiItemIndex]);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    class CustomPoiItem extends MapPOIItem
    {
        private AddressResponseDocuments addressDocument;
        private PlaceDocuments placeDocument;

        private void setAddressDocument(AddressResponseDocuments document)
        {
            this.addressDocument = document;
        }

        public void setPlaceDocument(PlaceDocuments placeDocument)
        {
            this.placeDocument = placeDocument;
        }

        public AddressResponseDocuments getAddressDocument()
        {
            return addressDocument;
        }

        public PlaceDocuments getPlaceDocument()
        {
            return placeDocument;
        }
    }

}