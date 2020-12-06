package com.zerodsoft.scheduleweather.activity.map.fragment.map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.activity.map.MapActivity;
import com.zerodsoft.scheduleweather.activity.map.fragment.search.SearchFragment;
import com.zerodsoft.scheduleweather.databinding.FragmentMapBinding;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.utility.LonLat;
import com.zerodsoft.scheduleweather.utility.LonLatConverter;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.util.List;

public class MapFragment extends Fragment implements MapView.POIItemEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.MapViewEventListener
{
    // list에서 item클릭 시 poiitem이 선택되고 맵 중앙좌표가 해당item의 좌표로 변경되면서 하단 시트가 올라온다
    public static final String TAG = "MapFragment";
    private static MapFragment instance;
    public static MapPoint currentMapPoint = MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633);

    private FragmentMapBinding binding;
    private MapView mapView;

    private int dataType;

    private LocationManager locationManager;

    private MapPOIItem[] addressPoiItems;
    private MapPOIItem[] placePoiItems;

    private boolean isOpendPoiInfo = false;
    private boolean isClickedPOI = false;
    public static boolean isMain = true;
    public static boolean isClickedChangeButton = false;
    public static boolean isClickedListItem = false;
    public static boolean isShowingPreviousItem = false;

    private String currentAddress = "";

    private MapReverseGeoCoder mapReverseGeoCoder;

    private BottomSheetBehavior bottomSheetBehavior;

    public static MapFragment getInstance(Activity activity)
    {
        if (instance == null)
        {
            instance = new MapFragment(activity);
        }
        return instance;
    }

    public MapFragment(Activity activity)
    {
    }

    public void setInitialData(Bundle bundle)
    {
        if (bundle.isEmpty())
        {

        } else
        {
            if (selectedAddress != null)
            {
                // 주소 검색 순서 : 좌표로 주소 변환
            } else if (selectedPlace != null)
            {
                // 장소 검색 순서 : 장소의 위경도 내 10M 반경에서 장소 이름 검색(여러개 나올 경우 장소ID와 일치하는 장소를 선택)
            }
        }
    }

    private final LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            currentMapPoint = MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude());
            mapView.setMapCenterPoint(currentMapPoint, true);
            // 자원해제
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
        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        bottomSheetBehavior = BottomSheetBehavior.from(binding.mapItemBottomSheet);
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

        binding.addFavoriteLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });

        binding.shareLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });


        binding.choiceLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });


        binding.cancelLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            }
        });


        binding.rightLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            }
        });

        binding.leftLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            }
        });


        binding.mapHeaderBar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            }
        });

        binding.zoomInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mapView.zoomIn(true);
            }
        });

        binding.zoomOutButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mapView.zoomOut(true);
            }
        });

        binding.gpsButton.setOnClickListener(new View.OnClickListener()
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

        binding.gpsButton.performClick();
        mapView = new MapView(getActivity());
        binding.mapView.addView(mapView);

        mapView.setPOIItemEventListener(this);
        mapView.setMapViewEventListener(this);
        mapView.setCurrentLocationEventListener(new MapView.CurrentLocationEventListener()
        {
            @Override
            public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v)
            {
                // 단말의 현위치 좌표값을 통보받을 수 있다.
                currentMapPoint = MapPoint.mapPointWithGeoCoord(mapPoint.getMapPointGeoCoord().latitude, mapPoint.getMapPointGeoCoord().longitude);
                mapView.setMapCenterPoint(currentMapPoint, true);
            }

            @Override
            public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v)
            {

            }

            @Override
            public void onCurrentLocationUpdateFailed(MapView mapView)
            {
                // 현위치 갱신 작업에 실패한 경우 호출된다.
            }

            @Override
            public void onCurrentLocationUpdateCancelled(MapView mapView)
            {
                // 현위치 트랙킹 기능이 사용자에 의해 취소된 경우 호출된다.
            }
        });
    }

    @Override
    public void onStart()
    {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        if (MapActivity.isSelectedLocation)
        {
            // edit인 경우
            // 하단 시트의 위치 변경(취소) 버튼을 클릭하지 않으면 변경이 불가능하도록 설정
            binding.mapHeaderBar.setClickable(false);
        } else
        {
        }

        if (isMain)
        {
            binding.gpsButton.performClick();
        } else
        {

        }
        super.onStart();
    }

    public void setMain()
    {
        binding.mapHeaderBar.setClickable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        isMain = true;
        isClickedListItem = false;
        isClickedChangeButton = false;
        isShowingPreviousItem = false;
        isClickedPOI = false;
        isOpendPoiInfo = false;
        clearData();
    }

    public void clickedMapButton()
    {
        // 검색 후 아이템을 선택 또는 지도 버튼을 클릭한 경우
        addPoiItems();
        binding.mapHeaderBar.setClickable(false);
        mapView.fitMapViewAreaToShowAllPOIItems();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        isClickedListItem = false;
        isClickedChangeButton = false;
        super.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }


    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String address)
    {
        currentAddress = address;
        binding.addressTextview.setText(currentAddress);
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
        currentMapPoint = MapPoint.mapPointWithGeoCoord(mapPoint.getMapPointGeoCoord().latitude, mapPoint.getMapPointGeoCoord().longitude);
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
        mapReverseGeoCoder.startFindingAddress(MapReverseGeoCoder.AddressType.ShortAddress);
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem)
    {
        // poiitem을 선택하였을 경우에 수행됨
        isOpendPoiInfo = true;
        onShowItem(mapPOIItem.getTag());
        currentMapPoint = MapPoint.mapPointWithGeoCoord(mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude, mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude);
        mapView.setMapCenterPoint(currentMapPoint, true);
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem)
    {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType)
    {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint)
    {

    }

    public void onItemSelected(int selectedItemPosition)
    {
        // 리스트에서 아이템을 선택하였을 때에 수행됨
        binding.mapHeaderBar.setClickable(false);
        this.selectedItemPosition = selectedItemPosition;
        onChangeItems();
        addPoiItems();
        mapView.selectPOIItem(mapView.getPOIItems()[selectedItemPosition], true);
        isOpendPoiInfo = true;
        onShowItem(selectedItemPosition);
        currentMapPoint = MapPoint.mapPointWithGeoCoord(mapView.getPOIItems()[selectedItemPosition].getMapPoint().getMapPointGeoCoord().latitude,
                mapView.getPOIItems()[selectedItemPosition].getMapPoint().getMapPointGeoCoord().longitude);
        mapView.setMapCenterPoint(currentMapPoint, true);
    }

    public void setZoomGpsButtonVisibility(int visibility)
    {
        binding.zoomInButton.setVisibility(visibility);
        binding.zoomOutButton.setVisibility(visibility);
        binding.gpsButton.setVisibility(visibility);
    }

    public void setPoiItems()
    {
        int addressSize = 0;
        int placeSize = 0;
        int placeDataType = 0;
        int addressDataType = 0;
        removeAllPoiItems();

        List<Integer> dataTypes = MapActivity.searchResult.getResultTypes();
        for (int dataType : dataTypes)
        {
            if (dataType == MapController.TYPE_PLACE_KEYWORD)
            {
                placeDataType = dataType;
                placeSize = MapActivity.searchResult.getPlaceResponseResponse().getPlaceDocuments().size();
            } else if (dataType == MapController.TYPE_PLACE_CATEGORY)
            {
                placeDataType = dataType;
                //
                // placeSize = MapActivity.searchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().size();
            } else if (dataType == MapController.TYPE_ADDRESS)
            {
                addressDataType = dataType;
                addressSize = MapActivity.searchResult.getAddressResponse().getAddressResponseDocumentsList().size();
            } else if (dataType == MapController.TYPE_COORD_TO_ADDRESS)
            {
                addressDataType = dataType;
                addressSize = 1;
            }
        }

        if (placeSize > 0)
        {
            placePoiItems = new MapPOIItem[placeSize];

            for (int i = 0; i < placeSize; i++)
            {
                MapPoint mapPoint = null;
                placePoiItems[i] = new MapPOIItem();

                if (placeDataType == MapController.TYPE_PLACE_KEYWORD)
                {
                    placePoiItems[i].setItemName(MapActivity.searchResult.getPlaceResponseResponse().getPlaceDocuments().get(i).getPlaceName());
                    mapPoint = MapPoint.mapPointWithGeoCoord(MapActivity.searchResult.getPlaceResponseResponse().getPlaceDocuments().get(i).getY(),
                            MapActivity.searchResult.getPlaceResponseResponse().getPlaceDocuments().get(i).getX());
                } else if (placeDataType == MapController.TYPE_PLACE_CATEGORY)
                {
                    /*
                    placePoiItems[i].setItemName(MapActivity.searchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(i).getPlaceName());
                    mapPoint = MapPoint.mapPointWithGeoCoord(Double.valueOf(MapActivity.searchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(i).getY()),
                            Double.valueOf(MapActivity.searchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(i).getX()));

                     */
                }

                placePoiItems[i].setTag(i);
                placePoiItems[i].setMapPoint(mapPoint);
                placePoiItems[i].setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                placePoiItems[i].setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            }
        }
        if (addressSize > 0)
        {
            addressPoiItems = new MapPOIItem[addressSize];

            for (int i = 0; i < addressSize; i++)
            {
                MapPoint mapPoint = null;
                addressPoiItems[i] = new MapPOIItem();

                if (addressDataType == MapController.TYPE_ADDRESS)
                {
                    addressPoiItems[i].setItemName(MapActivity.searchResult.getAddressResponse().getAddressResponseDocumentsList().get(i).getAddressName());
                    mapPoint = MapPoint.mapPointWithGeoCoord(MapActivity.searchResult.getAddressResponse().getAddressResponseDocumentsList().get(i).getY(),
                            MapActivity.searchResult.getAddressResponse().getAddressResponseDocumentsList().get(i).getX());
                } else if (addressDataType == MapController.TYPE_COORD_TO_ADDRESS)
                {
                    addressPoiItems[i].setItemName(selectedAddress.getAddressName());
                    mapPoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(selectedAddress.getLatitude()),
                            Double.parseDouble(selectedAddress.getLongitude()));
                }

                addressPoiItems[i].setTag(i);
                addressPoiItems[i].setMapPoint(mapPoint);
                addressPoiItems[i].setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                addressPoiItems[i].setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            }
        }
    }

    public void addPoiItems()
    {
        mapView.removeAllPOIItems();

        if (dataType == MapController.TYPE_COORD_TO_ADDRESS || dataType == MapController.TYPE_ADDRESS)
        {
            mapView.addPOIItems(addressPoiItems);
        } else if (dataType == MapController.TYPE_PLACE_CATEGORY || dataType == MapController.TYPE_PLACE_KEYWORD)
        {
            mapView.addPOIItems(placePoiItems);
        }
    }

    public void removeAllPoiItems()
    {
        addressPoiItems = null;
        placePoiItems = null;
        mapView.removeAllPOIItems();
    }

    public MapPoint.GeoCoordinate getMapCenterPoint()
    {
        return mapView.getMapCenterPoint().getMapPointGeoCoord();
    }

    private void setLayoutVisibility()
    {
        switch (dataType)
        {
            case MapController.TYPE_ADDRESS:
                binding.itemAddressLayout.setVisibility(View.VISIBLE);
                binding.itemPlaceLayout.setVisibility(View.GONE);
                break;
            case MapController.TYPE_PLACE_CATEGORY:
            case MapController.TYPE_PLACE_KEYWORD:
                // keyword, category
                binding.itemPlaceLayout.setVisibility(View.VISIBLE);
                binding.itemAddressLayout.setVisibility(View.GONE);
                break;
        }

        if (MapActivity.isSelectedLocation)
        {
            binding.choiceLocationButton.setVisibility(View.GONE);
            binding.cancelLocationButton.setVisibility(View.VISIBLE);
        } else
        {
            binding.choiceLocationButton.setVisibility(View.VISIBLE);
            binding.cancelLocationButton.setVisibility(View.GONE);
        }
    }

    private void setViewData()
    {
        switch (dataType)
        {
            case MapController.TYPE_ADDRESS:
                binding.selectedAddressNameTextview.setText(MapActivity.searchResult.getAddressResponse().getAddressResponseDocumentsList().get(selectedItemPosition).getAddressName());
                if (MapActivity.searchResult.getAddressResponse().getAddressResponseDocumentsList().get(selectedItemPosition).getAddressTypeStr().equals("도로명"))
                {
                    binding.selectedAnotherAddressTextview.setText(MapActivity.searchResult.getAddressResponse().getAddressResponseDocumentsList().get(selectedItemPosition).getAddressResponseRoadAddress().getAddressName());
                } else
                {
                    // 지번
                    binding.selectedAnotherAddressTextview.setText(MapActivity.searchResult.getAddressResponse().getAddressResponseDocumentsList().get(selectedItemPosition).getAddressResponseAddress().getAddressName());
                }
                binding.selectedAnotherAddressTypeTextview.setText(MapActivity.searchResult.getAddressResponse().getAddressResponseDocumentsList().get(selectedItemPosition).getAddressTypeStr());
                break;

            case MapController.TYPE_PLACE_KEYWORD:
                binding.selectedPlaceAddressTextview.setText(MapActivity.searchResult.getPlaceResponseResponse().getPlaceDocuments().get(selectedItemPosition).getAddressName());
                binding.selectedPlaceCategoryTextview.setText(MapActivity.searchResult.getPlaceResponseResponse().getPlaceDocuments().get(selectedItemPosition).getCategoryName());
                binding.selectedPlaceDescriptionTextview.setText(MapActivity.searchResult.getPlaceResponseResponse().getPlaceDocuments().get(selectedItemPosition).getCategoryGroupName());
                binding.selectedPlaceNameTextview.setText(MapActivity.searchResult.getPlaceResponseResponse().getPlaceDocuments().get(selectedItemPosition).getPlaceName());
                break;

            case MapController.TYPE_PLACE_CATEGORY:
                /*
                binding.selectedPlaceAddressTextview.setText(MapActivity.searchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(selectedItemPosition).getAddressName());
                binding.selectedPlaceCategoryTextview.setText(MapActivity.searchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(selectedItemPosition).getCategoryName());
                binding.selectedPlaceDescriptionTextview.setText(MapActivity.searchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(selectedItemPosition).getCategoryGroupName());
                binding.selectedPlaceNameTextview.setText(MapActivity.searchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(selectedItemPosition).getPlaceName());
                break;


                 */
            case MapController.TYPE_COORD_TO_ADDRESS:
                if (MapActivity.searchResult.getCoordToAddressResponse().getCoordToAddressDocuments().get(selectedItemPosition).getCoordToAddressAddress() != null)
                {
                    // 지번
                    binding.selectedAddressNameTextview.setText(MapActivity.searchResult.getCoordToAddressResponse().getCoordToAddressDocuments().get(selectedItemPosition).getCoordToAddressAddress().getAddressName());
                    binding.selectedAnotherAddressTextview.setText(MapActivity.searchResult.getCoordToAddressResponse().getCoordToAddressDocuments().get(selectedItemPosition).getCoordToAddressAddress().getMainAddressNo());
                    binding.selectedAnotherAddressTypeTextview.setText(MapActivity.searchResult.getCoordToAddressResponse().getCoordToAddressDocuments().get(selectedItemPosition).getCoordToAddressAddress().getRegion1DepthName());
                } else
                {
                    // 도로명
                    binding.selectedAddressNameTextview.setText(MapActivity.searchResult.getCoordToAddressResponse().getCoordToAddressDocuments().get(selectedItemPosition).getCoordToAddressRoadAddress().getAddressName());
                    binding.selectedAnotherAddressTextview.setText(MapActivity.searchResult.getCoordToAddressResponse().getCoordToAddressDocuments().get(selectedItemPosition).getCoordToAddressRoadAddress().getRoadName());
                    binding.selectedAnotherAddressTypeTextview.setText(MapActivity.searchResult.getCoordToAddressResponse().getCoordToAddressDocuments().get(selectedItemPosition).getCoordToAddressRoadAddress().getRegion1DepthName());
                }
                break;
        }
    }

    public void onChangeItems()
    {
        switch (dataType)
        {
            case MapController.TYPE_ADDRESS:
                itemPositionMax = MapActivity.searchResult.getAddressResponse().getAddressResponseDocumentsList().size() - 1;
                break;
            case MapController.TYPE_PLACE_KEYWORD:
                itemPositionMax = MapActivity.searchResult.getPlaceResponseResponse().getPlaceDocuments().size() - 1;
                break;
            case MapController.TYPE_PLACE_CATEGORY:
                // itemPositionMax = MapActivity.searchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().size() - 1;
                break;
            case MapController.TYPE_COORD_TO_ADDRESS:
                itemPositionMax = MapActivity.searchResult.getCoordToAddressResponse().getCoordToAddressDocuments().size() - 1;
                break;
        }
    }

    public void onShowItem(int position)
    {
        selectedItemPosition = position;

        setLayoutVisibility();
        setViewData();

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }


    public MapFragment setDataType(int dataType)
    {
        this.dataType = dataType;
        return this;
    }

    public MapFragment setSelectedItemPosition(int selectedItemPosition)
    {
        this.selectedItemPosition = selectedItemPosition;
        return this;
    }

    public void clearData()
    {
        removeAllPoiItems();
    }

}