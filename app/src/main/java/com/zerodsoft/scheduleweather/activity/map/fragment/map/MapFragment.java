package com.zerodsoft.scheduleweather.activity.map.fragment.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.map.MapActivity;
import com.zerodsoft.scheduleweather.activity.map.fragment.interfaces.ICatchedLocation;
import com.zerodsoft.scheduleweather.activity.map.fragment.search.SearchFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.IMapInfo;
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

public class MapFragment extends Fragment implements MapView.POIItemEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.MapViewEventListener, IMapInfo
{
    // list에서 item클릭 시 poiitem이 선택되고 맵 중앙좌표가 해당item의 좌표로 변경되면서 하단 시트가 올라온다
    public static final String TAG = "MapFragment";

    private MapPoint currentMapPoint = MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633);
    private MapView mapView;
    private CoordinatorLayout mapViewContainer;
    private int dataType;
    private LocationManager locationManager;

    private MapPOIItem[] addressPoiItems;
    private MapPOIItem[] placePoiItems;

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

    public MapFragment(ICatchedLocation iCatchedLocation)
    {
        this.iCatchedLocation = iCatchedLocation;
    }

    private final LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            currentMapPoint = MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude());
            mapView.setMapCenterPoint(currentMapPoint, true);
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
        mapViewContainer = (CoordinatorLayout) view.findViewById(R.id.map_view);
        bottomSheet = (LinearLayout) view.findViewById(R.id.map_item_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
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

            }
        });

        headerBar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                SearchFragment searchFragment = new SearchFragment(MapFragment.this);
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.hide(MapFragment.this).add(R.id.map_activity_fragment_container, searchFragment, SearchFragment.TAG).addToBackStack(null).commit();
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
                if (locationManager == null)
                {
                    locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                }
                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                int fineLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
                int coarseLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

                if (isGpsEnabled || isNetworkEnabled)
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                }
            }
        });

        currentAddress = (TextView) view.findViewById(R.id.current_address);

        initMapView();
    }

    private void initMapView()
    {
        mapView = new MapView(getActivity());
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

        mapReverseGeoCoder = new MapReverseGeoCoder(ai.metaData.getString("com.kakao.sdk.AppKey"), currentMapPoint
                , this, getActivity());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

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
                        currentMapPoint.getMapPointGeoCoord().latitude = selectedAddressDocument.getY();
                        currentMapPoint.getMapPointGeoCoord().longitude = selectedAddressDocument.getX();

                        mapView.setMapCenterPoint(currentMapPoint, false);
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
                        currentMapPoint.getMapPointGeoCoord().latitude = selectedPlaceDocument.getY();
                        currentMapPoint.getMapPointGeoCoord().longitude = selectedPlaceDocument.getX();

                        mapView.setMapCenterPoint(currentMapPoint, false);
                        mapView.removeAllPOIItems();
                        createPoiItem(selectedPlaceDocument.getPlaceName());
                        mapView.selectPOIItem(mapView.getPOIItems()[0], false);
                    }
                });
            }
        } else
        {
            gpsButton.performClick();
        }
    }

    @Override
    public void onStart()
    {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        super.onStart();
    }

    private void createPoiItem(String itemName)
    {
        MapPOIItem poiItem = new MapPOIItem();
        poiItem.setItemName(itemName);
        poiItem.setMapPoint(currentMapPoint);
        poiItem.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
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

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i)
    {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint)
    {

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
        currentMapPoint.getMapPointGeoCoord().longitude = mapPoint.getMapPointGeoCoord().longitude;
        currentMapPoint.getMapPointGeoCoord().latitude = mapPoint.getMapPointGeoCoord().latitude;

        mapReverseGeoCoder.startFindingAddress(MapReverseGeoCoder.AddressType.ShortAddress);
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem)
    {
        // poiitem을 선택하였을 경우에 수행됨
        onShowItem(mapPOIItem.getTag());
        currentMapPoint = MapPoint.mapPointWithGeoCoord(mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude, mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude);
        mapView.setMapCenterPoint(currentMapPoint, true);
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


    public void removeAllPoiItems()
    {
        addressPoiItems = null;
        placePoiItems = null;
        mapView.removeAllPOIItems();
    }

    @Override
    public MapPoint.GeoCoordinate getMapCenterPoint()
    {
        return mapView.getMapCenterPoint().getMapPointGeoCoord();
    }


    public void onShowItem(int position)
    {

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}