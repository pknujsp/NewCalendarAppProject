package com.zerodsoft.scheduleweather.activity.map.fragment.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.map.MapActivity;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.ICatchedLocation;
import com.zerodsoft.scheduleweather.activity.map.fragment.search.SearchFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.SearchResultFragmentController;
import com.zerodsoft.scheduleweather.activity.map.util.RequestLocationTimer;
import com.zerodsoft.scheduleweather.kakaomap.fragment.KakaoMapFragment;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class MapFragment extends KakaoMapFragment implements MapReverseGeoCoder.ReverseGeoCodingResultListener
{
    // list에서 item클릭 시 poiitem이 선택되고 맵 중앙좌표가 해당item의 좌표로 변경되면서 하단 시트가 올라온다
    public static final String TAG = "MapFragment";
    private static MapFragment instance;

    private TextView currentAddress;

    private LocationManager locationManager;
    private MapReverseGeoCoder mapReverseGeoCoder;

    private AddressViewModel addressViewModel;
    private PlacesViewModel placeViewModel;
    private final ICatchedLocation iCatchedLocation;

    private AddressResponseDocuments selectedAddressDocument;
    private PlaceDocuments selectedPlaceDocument;

    private OnBackPressedCallback onBackPressedCallback;

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
    }

    private final LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude()), true);
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
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        init();

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        bottomSheet.findViewById(R.id.choice_location_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                LocationDTO locationDTO = null;
                CustomPoiItem poiItem = (CustomPoiItem) mapView.getPOIItems()[selectedPoiItemIndex];
                if (poiItem.getAddressDocument() != null)
                {
                    AddressResponseDocuments document = poiItem.getAddressDocument();
                    AddressDTO addressDTO = new AddressDTO();
                    addressDTO.setAddressName(document.getAddressName());
                    addressDTO.setLatitude(Double.toString(document.getY()));
                    addressDTO.setLongitude(Double.toString(document.getX()));

                    locationDTO = addressDTO;
                } else if (poiItem.getPlaceDocument() != null)
                {
                    PlaceDocuments document = poiItem.getPlaceDocument();
                    PlaceDTO placeDTO = new PlaceDTO();
                    placeDTO.setPlaceName(document.getPlaceName());
                    placeDTO.setId(Integer.parseInt(document.getId()));
                    placeDTO.setLongitude(Double.toString(document.getX()));
                    placeDTO.setLatitude(Double.toString(document.getY()));

                    locationDTO = placeDTO;
                }
                iCatchedLocation.choiceLocation(locationDTO);
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
                        showRequestGpsDialog();
                    }
                } else
                {
                    Toast.makeText(getActivity(), getString(R.string.map_network_not_connected), Toast.LENGTH_SHORT).show();
                }
            }
        });

        currentAddress = (TextView) view.findViewById(R.id.current_address);
    }

    private void init()
    {
        mapView.setPOIItemEventListener(this);
        mapView.setMapViewEventListener(this);

        mapReverseGeoCoder = new MapReverseGeoCoder(appKey, mapView.getMapCenterPoint(), this, getActivity());
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
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
                            List<AddressResponseDocuments> document = new ArrayList<>();
                            document.add(selectedAddressDocument);
                            createAddressesPoiItems(document);
                            selectPoiItem(0);
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
                            List<PlaceDocuments> document = new ArrayList<>();
                            document.add(selectedPlaceDocument);
                            createPlacesPoiItems(document);
                            selectPoiItem(0);
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
                    showRequestGpsDialog();
                }
            }
        } else
        {
            Toast.makeText(getActivity(), getString(R.string.map_network_not_connected), Toast.LENGTH_SHORT).show();
        }

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
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint)
    {
        super.onMapViewSingleTapped(mapView, mapPoint);
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

}