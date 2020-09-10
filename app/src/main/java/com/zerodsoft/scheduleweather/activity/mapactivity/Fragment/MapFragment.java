package com.zerodsoft.scheduleweather.activity.mapactivity.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.mapactivity.MapActivity;
import com.zerodsoft.scheduleweather.databinding.FragmentMapBinding;
import com.zerodsoft.scheduleweather.recyclerviewadapter.SearchResultViewAdapter;
import com.zerodsoft.scheduleweather.retrofit.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.LocationSearchResult;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.utility.LonLat;
import com.zerodsoft.scheduleweather.utility.LonLatConverter;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.util.List;

public class MapFragment extends Fragment implements MapView.POIItemEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.MapViewEventListener, MapActivity.OnBackPressedListener, SearchResultViewAdapter.OnItemSelectedListener
{
    public static final String TAG = "MapFragment";
    private static MapFragment instance;
    private FragmentMapBinding binding;
    private MapView mapView;

    private int dataType;

    private LocationManager locationManager;
    private SearchResultController searchResultController;

    private MapPOIItem[] addressPoiItems;
    private MapPOIItem[] placePoiItems;

    private MapController.OnDownloadListener onDownloadListener;

    private boolean opendPOIInfo = false;
    private boolean clickedPOI = false;
    private int poiTag;
    private boolean isMain = true;

    private MapReverseGeoCoder mapReverseGeoCoder;
    private MapPoint currentMapPoint = MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633);

    private AddressDTO selectedAddress;
    private PlaceDTO selectedPlace;
    private LocationSearchResult locationSearchResult;

    private BottomSheetBehavior bottomSheetBehavior;
    private int selectedItemPosition;
    private int itemPositionMax;


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
        onDownloadListener = (MapController.OnDownloadListener) activity;
    }

    public void setInitialData(Bundle bundle)
    {
        // EDIT_LOCATION인 경우 데이터를 받아와서 화면에 표시
        if (bundle.isEmpty())
        {
            isMain = true;
        } else
        {
            isMain = false;
            selectedPlace = bundle.getParcelable("selectedPlace");
            selectedAddress = bundle.getParcelable("selectedAddress");

            LocalApiPlaceParameter parameter = new LocalApiPlaceParameter();

            if (selectedAddress != null)
            {
                // 주소 검색 순서 : 좌표로 주소 변환
                parameter.setX(Double.parseDouble(selectedAddress.getLongitude())).setY(Double.parseDouble(selectedAddress.getLatitude()));
                onDownloadListener.requestData(parameter, MapController.TYPE_COORD_TO_ADDRESS, TAG);
            } else if (selectedPlace != null)
            {
                // 장소 검색 순서 : 장소의 위경도 내 10M 반경에서 장소 이름 검색(여러개 나올 경우 장소ID와 일치하는 장소를 선택)
                parameter.setQuery(selectedPlace.getPlaceName()).setX(Double.parseDouble(selectedPlace.getLongitude())).setY(Double.parseDouble(selectedPlace.getLatitude()))
                        .setRadius("10").setPage("1").setSort(LocalApiPlaceParameter.SORT_ACCURACY);
                onDownloadListener.requestData(parameter, MapController.TYPE_PLACE_KEYWORD, TAG);
            }
        }
    }

    public void setSelectedLocationData(LocalApiPlaceParameter parameter, int dataType, LocationSearchResult locationSearchResult)
    {
        onChangeItems(dataType);
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

        // bottomsheet 초기화
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

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
                Bundle bundle = new Bundle();
                bundle.putInt("dataType", dataType);
                LonLat lonLat = null;
                double lon, lat;

                switch (dataType)
                {
                    case MapController.TYPE_ADDRESS:
                        lon = locationSearchResult.getAddressResponse().getAddressResponseDocumentsList().get(selectedItemPosition).getX();
                        lat = locationSearchResult.getAddressResponse().getAddressResponseDocumentsList().get(selectedItemPosition).getY();
                        lonLat = LonLatConverter.convertLonLat(lon, lat);

                        AddressDTO addressDTO = new AddressDTO();
                        addressDTO.setAddressName(locationSearchResult.getAddressResponse().getAddressResponseDocumentsList().get(selectedItemPosition).getAddressName());
                        addressDTO.setLongitude(Double.toString(lon));
                        addressDTO.setLatitude(Double.toString(lat));
                        addressDTO.setWeatherX(Integer.toString(lonLat.getX()));
                        addressDTO.setWeatherY(Integer.toString(lonLat.getY()));

                        try
                        {
                            bundle.putParcelable("address", (AddressDTO) addressDTO.clone());
                        } catch (CloneNotSupportedException e)
                        {
                            e.printStackTrace();
                        }
                        break;

                    case MapController.TYPE_PLACE_KEYWORD:
                        lon = locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordDocuments().get(selectedItemPosition).getX();
                        lat = locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordDocuments().get(selectedItemPosition).getY();
                        lonLat = LonLatConverter.convertLonLat(lon, lat);

                        PlaceDTO placeDTOKeyword = new PlaceDTO();
                        placeDTOKeyword.setPlaceId(locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordDocuments().get(selectedItemPosition).getId());
                        placeDTOKeyword.setPlaceName(locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordDocuments().get(selectedItemPosition).getPlaceName());
                        placeDTOKeyword.setLongitude(Double.toString(lon));
                        placeDTOKeyword.setLatitude(Double.toString(lat));
                        placeDTOKeyword.setWeatherX(Integer.toString(lonLat.getX()));
                        placeDTOKeyword.setWeatherY(Integer.toString(lonLat.getY()));

                        try
                        {
                            bundle.putParcelable("place", (PlaceDTO) placeDTOKeyword.clone());
                        } catch (CloneNotSupportedException e)
                        {
                            e.printStackTrace();
                        }
                        break;

                    case MapController.TYPE_PLACE_CATEGORY:
                        lon = Double.valueOf(locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(selectedItemPosition).getX());
                        lat = Double.valueOf(locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(selectedItemPosition).getY());
                        lonLat = LonLatConverter.convertLonLat(lon, lat);

                        PlaceDTO placeDTOCategory = new PlaceDTO();
                        placeDTOCategory.setPlaceId(locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(selectedItemPosition).getId());
                        placeDTOCategory.setPlaceName(locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(selectedItemPosition).getPlaceName());
                        placeDTOCategory.setLongitude(locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(selectedItemPosition).getX());
                        placeDTOCategory.setLatitude(locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(selectedItemPosition).getY());
                        placeDTOCategory.setWeatherX(Integer.toString(lonLat.getX()));
                        placeDTOCategory.setWeatherY(Integer.toString(lonLat.getY()));

                        try
                        {
                            bundle.putParcelable("place", (PlaceDTO) placeDTOCategory.clone());
                        } catch (CloneNotSupportedException e)
                        {
                            e.printStackTrace();
                        }
                        break;
                }
                ((MapActivity) getActivity()).onChoicedLocation(bundle);
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
                if (selectedItemPosition < itemPositionMax)
                {
                    ++selectedItemPosition;
                } else
                {
                    selectedItemPosition = 0;
                }
                //  ((MapActivity) getActivity()).onItemSelected(selectedItemPosition);
            }
        });

        binding.leftLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (selectedItemPosition == 0)
                {
                    selectedItemPosition = itemPositionMax;
                } else
                {
                    --selectedItemPosition;
                }
                //  ((MapActivity) getActivity()).onItemSelected(selectedItemPosition);
            }
        });


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

        binding.addressTextview.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Bundle bundle = new Bundle();

                MapPoint.GeoCoordinate mapPoint = mapView.getMapCenterPoint().getMapPointGeoCoord();

                bundle.putDouble("latitude", mapPoint.latitude);
                bundle.putDouble("longitude", mapPoint.longitude);

                ((MapActivity) getActivity()).onFragmentChanged(SearchFragment.TAG, bundle);
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
                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                int fineLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
                int coarseLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

                if (isGpsEnabled && isNetworkEnabled)
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                }
            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onPause()
    {
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
        // binding.setCurrentAddress(address);
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
        opendPOIInfo = true;
        onShowItem(mapPOIItem.getTag());
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

    @Override
    public void onItemSelected(int position, int dataType)
    {
        onChangeItems(dataType);
        addPoiItems(dataType);
        mapView.selectPOIItem(mapView.getPOIItems()[position], true);
    }

    public void setZoomGpsButtonVisibility(int value)
    {
        binding.zoomInButton.setVisibility(value);
        binding.zoomOutButton.setVisibility(value);
        binding.gpsButton.setVisibility(value);
    }

    public void onChangeButtonClicked(int type)
    {
        if (SearchResultController.isShowList)
        {
            searchResultController.setListVisibility(false);
            setZoomGpsButtonVisibility(View.VISIBLE);

            dataType = type;
            setPoiItems();
            mapView.fitMapViewAreaToShowAllPOIItems();
        } else
        {
            searchResultController.setListVisibility(true);
            setZoomGpsButtonVisibility(View.INVISIBLE);
        }
    }


    public void setLocationSearchResult(LocationSearchResult locationSearchResult)
    {
        this.locationSearchResult = locationSearchResult;
    }

    public void setPoiItems()
    {
        int addressSize = 0;
        int placeSize = 0;
        removeAllPoiItems();

        List<Integer> dataTypes = locationSearchResult.getResultTypes();
        for (int dataType : dataTypes)
        {
            if (dataType == MapController.TYPE_PLACE_KEYWORD)
            {
                placeSize = locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordDocuments().size();
            } else if (dataType == MapController.TYPE_PLACE_CATEGORY)
            {
                placeSize = locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().size();
            } else if (dataType == MapController.TYPE_ADDRESS)
            {
                addressSize = locationSearchResult.getAddressResponse().getAddressResponseDocumentsList().size();
            } else if (dataType == MapController.TYPE_COORD_TO_ADDRESS)
            {
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

                if (dataType == MapController.TYPE_PLACE_KEYWORD)
                {
                    placePoiItems[i].setItemName(locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordDocuments().get(i).getPlaceName());
                    mapPoint = MapPoint.mapPointWithGeoCoord(locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordDocuments().get(i).getY(),
                            locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordDocuments().get(i).getX());
                } else if (dataType == MapController.TYPE_PLACE_CATEGORY)
                {
                    placePoiItems[i].setItemName(locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(i).getPlaceName());
                    mapPoint = MapPoint.mapPointWithGeoCoord(Double.valueOf(locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(i).getY()),
                            Double.valueOf(locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(i).getX()));
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

                if (dataType == MapController.TYPE_ADDRESS)
                {
                    addressPoiItems[i].setItemName(locationSearchResult.getAddressResponse().getAddressResponseDocumentsList().get(i).getAddressName());
                    mapPoint = MapPoint.mapPointWithGeoCoord(locationSearchResult.getAddressResponse().getAddressResponseDocumentsList().get(i).getY(),
                            locationSearchResult.getAddressResponse().getAddressResponseDocumentsList().get(i).getX());
                } else if (dataType == MapController.TYPE_COORD_TO_ADDRESS)
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

    public void addPoiItems(int dataType)
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

    private void setCurrentCenterPoint(int position)
    {
        double latitude = 0, longitude = 0;

        if (dataType == MapController.TYPE_ADDRESS)
        {
            longitude = addressPoiItems[position].getMapPoint().getMapPointGeoCoord().longitude;
            latitude = addressPoiItems[position].getMapPoint().getMapPointGeoCoord().latitude;
        } else if (dataType == MapController.TYPE_PLACE_KEYWORD || dataType == MapController.TYPE_PLACE_CATEGORY)
        {
            longitude = placePoiItems[position].getMapPoint().getMapPointGeoCoord().longitude;
            latitude = placePoiItems[position].getMapPoint().getMapPointGeoCoord().latitude;
        }

        currentMapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
        mapView.setMapCenterPoint(currentMapPoint, true);
    }

    public void removeAllPoiItems()
    {
        addressPoiItems = null;
        placePoiItems = null;
        mapView.removeAllPOIItems();
    }


    @Override
    public void onBackPressed()
    {
        if (isFragmentExpanded())
        {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        if (mapView.getPOIItems() != null)
        {
            ((MapActivity) getActivity()).onFragmentChanged(SearchResultFragment.TAG, new Bundle());
        } else
        {
            ((MapActivity) getActivity()).getSupportFragmentManager().beginTransaction()
                    .remove(MapFragment.this).commit();
            ((MapActivity) getActivity()).onBackPressed();
        }
    }

    public MapPointBounds getMapPointBounds()
    {
        return mapView.getMapPointBounds();
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
            binding.choiceLocationButton.setVisibility(View.VISIBLE);
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
                binding.selectedAddressNameTextview.setText(locationSearchResult.getAddressResponse().getAddressResponseDocumentsList().get(selectedItemPosition).getAddressName());
                if (locationSearchResult.getAddressResponse().getAddressResponseDocumentsList().get(selectedItemPosition).getAddressTypeStr().equals("도로명"))
                {
                    binding.selectedAnotherAddressTextview.setText(locationSearchResult.getAddressResponse().getAddressResponseDocumentsList().get(selectedItemPosition).getAddressResponseRoadAddress().getAddressName());
                } else
                {
                    // 지번
                    binding.selectedAnotherAddressTextview.setText(locationSearchResult.getAddressResponse().getAddressResponseDocumentsList().get(selectedItemPosition).getAddressResponseAddress().getAddressName());
                }
                binding.selectedAnotherAddressTypeTextview.setText(locationSearchResult.getAddressResponse().getAddressResponseDocumentsList().get(selectedItemPosition).getAddressTypeStr());
                break;

            case MapController.TYPE_PLACE_KEYWORD:
                binding.selectedPlaceAddressTextview.setText(locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordDocuments().get(selectedItemPosition).getAddressName());
                binding.selectedPlaceCategoryTextview.setText(locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordDocuments().get(selectedItemPosition).getCategoryName());
                binding.selectedPlaceDescriptionTextview.setText(locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordDocuments().get(selectedItemPosition).getCategoryGroupName());
                binding.selectedPlaceNameTextview.setText(locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordDocuments().get(selectedItemPosition).getPlaceName());
                break;

            case MapController.TYPE_PLACE_CATEGORY:
                binding.selectedPlaceAddressTextview.setText(locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(selectedItemPosition).getAddressName());
                binding.selectedPlaceCategoryTextview.setText(locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(selectedItemPosition).getCategoryName());
                binding.selectedPlaceDescriptionTextview.setText(locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(selectedItemPosition).getCategoryGroupName());
                binding.selectedPlaceNameTextview.setText(locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(selectedItemPosition).getPlaceName());
                break;

            case MapController.TYPE_COORD_TO_ADDRESS:
                if (locationSearchResult.getCoordToAddressResponse().getCoordToAddressDocuments().get(selectedItemPosition).getCoordToAddressAddress() != null)
                {
                    // 지번
                    binding.selectedAddressNameTextview.setText(locationSearchResult.getCoordToAddressResponse().getCoordToAddressDocuments().get(selectedItemPosition).getCoordToAddressAddress().getAddressName());
                    binding.selectedAnotherAddressTextview.setText(locationSearchResult.getCoordToAddressResponse().getCoordToAddressDocuments().get(selectedItemPosition).getCoordToAddressAddress().getMainAddressNo());
                    binding.selectedAnotherAddressTypeTextview.setText(locationSearchResult.getCoordToAddressResponse().getCoordToAddressDocuments().get(selectedItemPosition).getCoordToAddressAddress().getRegion1DepthName());
                } else
                {
                    // 도로명
                    binding.selectedAddressNameTextview.setText(locationSearchResult.getCoordToAddressResponse().getCoordToAddressDocuments().get(selectedItemPosition).getCoordToAddressRoadAddress().getAddressName());
                    binding.selectedAnotherAddressTextview.setText(locationSearchResult.getCoordToAddressResponse().getCoordToAddressDocuments().get(selectedItemPosition).getCoordToAddressRoadAddress().getRoadName());
                    binding.selectedAnotherAddressTypeTextview.setText(locationSearchResult.getCoordToAddressResponse().getCoordToAddressDocuments().get(selectedItemPosition).getCoordToAddressRoadAddress().getRegion1DepthName());
                }
                break;
        }
    }

    public void onChangeItems(int dataType)
    {
        switch (dataType)
        {
            case MapController.TYPE_ADDRESS:
                itemPositionMax = locationSearchResult.getAddressResponse().getAddressResponseDocumentsList().size() - 1;
                break;
            case MapController.TYPE_PLACE_KEYWORD:
                itemPositionMax = locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordDocuments().size() - 1;
                break;
            case MapController.TYPE_PLACE_CATEGORY:
                itemPositionMax = locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().size() - 1;
                break;
            case MapController.TYPE_COORD_TO_ADDRESS:
                itemPositionMax = locationSearchResult.getCoordToAddressResponse().getCoordToAddressDocuments().size() - 1;
                break;
        }
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }


    public boolean isFragmentExpanded()
    {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
        {
            return true;
        } else
        {
            return false;
        }
    }


    public void onShowItem(int position)
    {
        selectedItemPosition = position;

        setLayoutVisibility();
        setViewData();

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}