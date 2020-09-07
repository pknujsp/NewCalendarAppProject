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
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.mapactivity.MapActivity;
import com.zerodsoft.scheduleweather.databinding.FragmentMapBinding;
import com.zerodsoft.scheduleweather.fragment.MapBottomSheetFragment;
import com.zerodsoft.scheduleweather.fragment.SearchResultController;
import com.zerodsoft.scheduleweather.retrofit.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.LocationSearchResult;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment implements MapView.POIItemEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.MapViewEventListener, MapActivity.OnBackPressedListener
{
    private static MapFragment instance;
    public static final String TAG = "MapFragment";

    private FragmentMapBinding binding;
    private MapView mapView;

    private int resultType;
    private boolean isMain = false;

    private LocationManager locationManager;
    private SearchResultController searchResultController;

    private MapController.OnDownloadListener onDownloadListener;
    private OnControlItemFragment onControlItemFragment;

    private boolean opendPOIInfo = false;
    private boolean clickedPOI = false;
    private int poiTag;

    private MapReverseGeoCoder mapReverseGeoCoder;
    private MapPoint currentMapPoint = MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633);

    private AddressDTO selectedAddress;
    private PlaceDTO selectedPlace;
    private LocationSearchResult locationSearchResult;

    public interface OnControlItemFragment
    {
        void onChangeItems(Bundle bundle);

        void onShowItem(int position);

        boolean isFragmentExpanded();

        void setBehaviorState(int state);
    }

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

        if (bundle == null)
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
        Bundle bundle = new Bundle();

        if (dataType == MapController.TYPE_COORD_TO_ADDRESS)
        {
            bundle.putInt("dataType", MapController.TYPE_COORD_TO_ADDRESS);
        } else if (dataType == MapController.TYPE_PLACE_KEYWORD)
        {
            bundle.putInt("dataType", MapController.TYPE_PLACE_KEYWORD);
        }

        bundle.putInt("position", 0);
        bundle.putParcelable("locationSearchResult", locationSearchResult);
        onControlItemFragment.onChangeItems(bundle);
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

        MapBottomSheetFragment bottomSheetFragment = (MapBottomSheetFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map_bottom_sheet);
        onControlItemFragment = (OnControlItemFragment) bottomSheetFragment;
        onControlItemFragment.setBehaviorState(BottomSheetBehavior.STATE_HIDDEN);

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
        binding.setCurrentAddress(address);
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

    public void onItemSelected(int position)
    {
        // 다른 아이템을 선택한 경우에 사용된다
        getActivity().frag.onShowItem(position);
        setCurrentCenterPoint(position);
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

            resultType = type;
            setPoiItems();
            mapView.fitMapViewAreaToShowAllPOIItems();
        } else
        {
            searchResultController.setListVisibility(true);
            setZoomGpsButtonVisibility(View.INVISIBLE);
        }
    }


    public void setResultData(Bundle bundle)
    {
        // 검색 완료 후 데이터를 저장
        searchResult = bundle.getParcelable("result");
        List<Integer> types = searchResult.getResultTypes();

        for (int type : types)
        {
            if (type == KakaoLocalApi.TYPE_ADDRESS)
            {
                addressList = searchResult.getAddressResponseDocuments();
            } else if (type == KakaoLocalApi.TYPE_PLACE_KEYWORD)
            {
                placeKeywordList = searchResult.getPlaceKeywordDocuments();
            } else if (type == KakaoLocalApi.TYPE_PLACE_CATEGORY)
            {
                placeCategoryList = searchResult.getPlaceCategoryDocuments();
                break;
            }
        }
    }

    public void onItemClicked(int position, int type)
    {
        // list에서 아이템을 선택한 경우에 사용된다
        setZoomGpsButtonVisibility(View.VISIBLE);
        selectedItemPosition = position;
        resultType = type;

        searchResultController.setListVisibility(false);
        displayItemBottomSheet(position);
    }

    private void displayItemBottomSheet(int position)
    {
        Bundle bundle = new Bundle();

        setCurrentCenterPoint(position);
        setPoiItems();

        if (resultType == KakaoLocalApi.TYPE_ADDRESS)
        {
            bundle.putParcelableArrayList("itemList", (ArrayList<? extends Parcelable>) addressList);
            bundle.putInt("type", KakaoLocalApi.TYPE_ADDRESS);
        } else if (resultType == KakaoLocalApi.TYPE_PLACE_KEYWORD)
        {
            bundle.putParcelableArrayList("itemList", (ArrayList<? extends Parcelable>) placeKeywordList);
            bundle.putInt("type", KakaoLocalApi.TYPE_PLACE_KEYWORD);
        } else if (resultType == KakaoLocalApi.TYPE_PLACE_CATEGORY)
        {
            bundle.putParcelableArrayList("itemList", (ArrayList<? extends Parcelable>) placeCategoryList);
            bundle.putInt("type", KakaoLocalApi.TYPE_PLACE_CATEGORY);
        }
        bundle.putInt("position", position);

        onControlItemFragment.onChangeItem(bundle);
        opendPOIInfo = true;
    }


    private void setPoiItems()
    {
        int size = 0;
        clearAllPoiItems();

        switch (resultType)
        {
            case KakaoLocalApi.TYPE_ADDRESS:
                size = addressList.size();
                break;
            case KakaoLocalApi.TYPE_PLACE_KEYWORD:
                size = placeKeywordList.size();
                break;
            case KakaoLocalApi.TYPE_PLACE_CATEGORY:
                size = placeCategoryList.size();
                break;
        }

        MapPOIItem[] mapPOIItems = new MapPOIItem[size];

        for (int i = 0; i < size; i++)
        {
            MapPoint mapPoint = null;
            mapPOIItems[i] = new MapPOIItem();

            if (resultType == KakaoLocalApi.TYPE_ADDRESS)
            {
                mapPOIItems[i].setItemName(addressList.get(i).getAddressName());
                mapPoint = MapPoint.mapPointWithGeoCoord(addressList.get(i).getY(), addressList.get(i).getX());
            } else if (resultType == KakaoLocalApi.TYPE_PLACE_KEYWORD)
            {
                mapPOIItems[i].setItemName(placeKeywordList.get(i).getPlaceName());
                mapPoint = MapPoint.mapPointWithGeoCoord(placeKeywordList.get(i).getY(), placeKeywordList.get(i).getX());
            } else if (resultType == KakaoLocalApi.TYPE_PLACE_CATEGORY)
            {
                mapPOIItems[i].setItemName(placeCategoryList.get(i).getPlaceName());
                mapPoint = MapPoint.mapPointWithGeoCoord(Double.valueOf(placeCategoryList.get(i).getY()), Double.valueOf(placeCategoryList.get(i).getX()));
            }

            mapPOIItems[i].setTag(i);
            mapPOIItems[i].setMapPoint(mapPoint);
            mapPOIItems[i].setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            mapPOIItems[i].setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        }
        mapView.addPOIItems(mapPOIItems);
        mapView.remove
    }

    private void setCurrentCenterPoint(int position)
    {
        double latitude = 0, longitude = 0;

        if (resultType == KakaoLocalApi.TYPE_ADDRESS)
        {
            longitude = addressList.get(position).getX();
            latitude = addressList.get(position).getY();
        } else if (resultType == KakaoLocalApi.TYPE_PLACE_KEYWORD)
        {
            longitude = placeKeywordList.get(position).getX();
            latitude = placeKeywordList.get(position).getY();
        } else if (resultType == KakaoLocalApi.TYPE_PLACE_CATEGORY)
        {
            longitude = Double.valueOf(placeCategoryList.get(position).getX());
            latitude = Double.valueOf(placeCategoryList.get(position).getY());
        }

        currentMapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
        mapView.setMapCenterPoint(currentMapPoint, true);
    }

    public void clearAllPoiItems()
    {
        mapView.removeAllPOIItems();
    }


    @Override
    public void onBackPressed()
    {
        if (onControlItemFragment.isFragmentExpanded())
        {
            onControlItemFragment.setBehaviorState(BottomSheetBehavior.STATE_HIDDEN);
        }
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();

        if (fragmentList != null)
        {
            for (Fragment fragment : fragmentList)
            {
                if (fragment.isVisible() && fragment instanceof MapActivity.OnBackPressedListener)
                {
                    ((MapActivity.OnBackPressedListener) fragment).onBackPressed();
                    return;
                }
            }
        }
        super.onBackPressed();
    }

    public void onChoicedLocation(Bundle bundle)
    {
        mapView = null;
        getIntent().putExtras(bundle);
        setResult(RESULT_OK, getIntent());
        finish();
    }

    public MapPointBounds getMapPointBounds()
    {
        return mapView.getMapPointBounds();
    }

    public MapPoint.GeoCoordinate getMapCenterPoint()
    {
        return mapView.getMapCenterPoint().getMapPointGeoCoord();
    }
}