package com.zerodsoft.scheduleweather.activity.mapactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.SearchFragment;
import com.zerodsoft.scheduleweather.fragment.MapBottomSheetFragment;
import com.zerodsoft.scheduleweather.fragment.SearchResultController;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.recyclerviewadapter.SearchResultViewAdapter;
import com.zerodsoft.scheduleweather.retrofit.DownloadData;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.AddressSearchResult;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placecategoryresponse.PlaceCategoryDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placekeywordresponse.PlaceKeywordDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements MapView.POIItemEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.MapViewEventListener,
        SearchResultViewAdapter.OnItemClickedListener
{
    public static final int SEARCH_FRAGMENT = 0;
    public static final int SEARCH_RESULT_FRAGMENT = 1;
    public static final int SEARCH_RESULT_FRAGMENT_UPDATE = 2;
    public static final int RESULT_DELETE = 10;

    private TextView addressTextView;
    private ImageButton zoomInButton;
    private ImageButton zoomOutButton;
    private ImageButton gpsButton;

    private List<AddressResponseDocuments> addressList = null;
    private List<PlaceKeywordDocuments> placeKeywordList = null;
    private List<PlaceCategoryDocuments> placeCategoryList = null;

    private LocationManager locationManager;

    private long downloadedTime;
    private int resultType;
    private int selectedItemPosition;
    public static boolean isMainMapActivity = true;

    private AddressSearchResult searchResult;

    private LocationDTO locationDTO;

    private MapBottomSheetFragment mapBottomSheetFragment;

    private SearchResultController searchResultController;

    public MapView mapView;
    private boolean opendPOIInfo = false;
    private boolean clickedPOI = false;
    private int poiTag;

    private MapReverseGeoCoder mapReverseGeoCoder;

    private static MapPoint currentMapPoint = MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633);

    private GestureDetectorCompat gestureDetectorCompat;
    private OnControlItemFragment onControlItemFragment;

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem)
    {
        poiTag = mapPOIItem.getTag();
        clickedPOI = true;
        // 하단 시트의 좌우 클릭시 poiitem의 풍선이 선택되도록
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
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s)
    {
        addressTextView.setText(s);
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
            ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        mapReverseGeoCoder = new MapReverseGeoCoder(ai.metaData.getString("com.kakao.sdk.AppKey"), currentMapPoint
                , this, MapActivity.this);
        mapReverseGeoCoder.startFindingAddress(MapReverseGeoCoder.AddressType.ShortAddress);
    }

    public interface OnControlItemFragment
    {
        void onChangeFragment(Bundle bundle);

        void onShowItemInfo(int position);

        boolean getBehaviorStateExpand();

        void setBehaviorState(int state);
    }

    public interface OnBackPressedListener
    {
        void onBackPressed();
    }


    private final GestureDetector.SimpleOnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener()
    {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e)
        {
            if (opendPOIInfo && !clickedPOI)
            {
                if (onControlItemFragment.getBehaviorStateExpand())
                {
                    onControlItemFragment.setBehaviorState(BottomSheetBehavior.STATE_HIDDEN);
                    opendPOIInfo = false;
                    return true;
                } else
                {
                    return false;
                }
            }

            if (clickedPOI)
            {
                onItemSelected(poiTag);

                opendPOIInfo = true;
                clickedPOI = false;
                return true;
            }

            return false;
        }
    };

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

    @Override
    protected void onStart()
    {
        gpsButton.performClick();
        super.onStart();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        addressTextView = (TextView) findViewById(R.id.address_textview);
        zoomInButton = (ImageButton) findViewById(R.id.zoom_in_button);
        zoomOutButton = (ImageButton) findViewById(R.id.zoom_out_button);
        gpsButton = (ImageButton) findViewById(R.id.gps_button);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mapView = new MapView(this);
        gestureDetectorCompat = new GestureDetectorCompat(this, onGestureListener);

        if (isMainMapActivity)
        {
            initItemFragment();
        }
        if (!MapView.isMapTilePersistentCacheEnabled())
        {
            MapView.setMapTilePersistentCacheEnabled(true);
        }

        ConstraintLayout mapViewContainer = (ConstraintLayout) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

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

                // 5초후 현위치를 잡으면 트랙킹 모드 종료
                //mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
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

        addressTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Bundle bundle = new Bundle();

                MapPoint.GeoCoordinate mapPoint = mapView.getMapCenterPoint().getMapPointGeoCoord();

                bundle.putDouble("latitude", mapPoint.latitude);
                bundle.putDouble("longitude", mapPoint.longitude);

                onFragmentChanged(SEARCH_FRAGMENT, bundle);
            }
        });

        zoomInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mapView.zoomIn(true);
            }
        });

        zoomOutButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mapView.zoomOut(true);
            }
        });

        gpsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //   TimeOutThread timeOutThread = new TimeOutThread();
                //    timeOutThread.start();

                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                int fineLocationPermission = ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                int coarseLocationPermission = ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);

                if (isGpsEnabled && isNetworkEnabled)
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                }
            }
        });


        mapView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                return gestureDetectorCompat.onTouchEvent(motionEvent);
            }
        });
    }


    public void onItemSelected(int position)
    {
        // 다른 아이템을 선택한 경우에 사용된다
        onControlItemFragment.onShowItemInfo(position);
        setCurrentCenterPoint(position);
        mapView.selectPOIItem(mapView.getPOIItems()[position], true);
    }

    public void setZoomGpsButtonVisibility(int value)
    {
        zoomInButton.setVisibility(value);
        zoomOutButton.setVisibility(value);
        gpsButton.setVisibility(value);
    }

    public void onChangeButtonClicked(int type)
    {
        if (SearchResultController.isShowList)
        {
            searchResultController.setListVisibility(false);
            setZoomGpsButtonVisibility(View.VISIBLE);
            isMainMapActivity = false;

            resultType = type;
            setPoiItems();
            mapView.fitMapViewAreaToShowAllPOIItems();
        } else
        {
            searchResultController.setListVisibility(true);
            setZoomGpsButtonVisibility(View.INVISIBLE);
            isMainMapActivity = true;
        }
    }


    public void setResultData(Bundle bundle)
    {
        // 검색 완료 후 데이터를 저장
        searchResult = bundle.getParcelable("result");
        List<Integer> types = searchResult.getResultTypes();

        for (int type : types)
        {
            if (type == DownloadData.ADDRESS)
            {
                addressList = searchResult.getAddressResponseDocuments();
            } else if (type == DownloadData.PLACE_KEYWORD)
            {
                placeKeywordList = searchResult.getPlaceKeywordDocuments();
            } else if (type == DownloadData.PLACE_CATEGORY)
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
        isMainMapActivity = false;
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

        if (resultType == DownloadData.ADDRESS)
        {
            bundle.putParcelableArrayList("itemList", (ArrayList<? extends Parcelable>) addressList);
            bundle.putInt("type", DownloadData.ADDRESS);
        } else if (resultType == DownloadData.PLACE_KEYWORD)
        {
            bundle.putParcelableArrayList("itemList", (ArrayList<? extends Parcelable>) placeKeywordList);
            bundle.putInt("type", DownloadData.PLACE_KEYWORD);
        } else if (resultType == DownloadData.PLACE_CATEGORY)
        {
            bundle.putParcelableArrayList("itemList", (ArrayList<? extends Parcelable>) placeCategoryList);
            bundle.putInt("type", DownloadData.PLACE_CATEGORY);
        }
        bundle.putInt("position", position);

        onControlItemFragment.onChangeFragment(bundle);
        opendPOIInfo = true;
    }


    private void setPoiItems()
    {
        int size = 0;
        clearAllPoiItems();

        switch (resultType)
        {
            case DownloadData.ADDRESS:
                size = addressList.size();
                break;
            case DownloadData.PLACE_KEYWORD:
                size = placeKeywordList.size();
                break;
            case DownloadData.PLACE_CATEGORY:
                size = placeCategoryList.size();
                break;
        }

        MapPOIItem[] mapPOIItems = new MapPOIItem[size];

        for (int i = 0; i < size; i++)
        {
            MapPoint mapPoint = null;
            mapPOIItems[i] = new MapPOIItem();

            if (resultType == DownloadData.ADDRESS)
            {
                mapPOIItems[i].setItemName(addressList.get(i).getAddressName());
                mapPoint = MapPoint.mapPointWithGeoCoord(addressList.get(i).getY(), addressList.get(i).getX());
            } else if (resultType == DownloadData.PLACE_KEYWORD)
            {
                mapPOIItems[i].setItemName(placeKeywordList.get(i).getPlaceName());
                mapPoint = MapPoint.mapPointWithGeoCoord(placeKeywordList.get(i).getY(), placeKeywordList.get(i).getX());
            } else if (resultType == DownloadData.PLACE_CATEGORY)
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
    }

    private void setCurrentCenterPoint(int position)
    {
        double latitude = 0, longitude = 0;

        if (resultType == DownloadData.ADDRESS)
        {
            longitude = addressList.get(position).getX();
            latitude = addressList.get(position).getY();
        } else if (resultType == DownloadData.PLACE_KEYWORD)
        {
            longitude = placeKeywordList.get(position).getX();
            latitude = placeKeywordList.get(position).getY();
        } else if (resultType == DownloadData.PLACE_CATEGORY)
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

    private void initItemFragment()
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        mapBottomSheetFragment = new MapBottomSheetFragment();
        onControlItemFragment = mapBottomSheetFragment;

        fragmentTransaction.add(mapBottomSheetFragment, MapBottomSheetFragment.TAG);
        fragmentTransaction.show(mapBottomSheetFragment).commit();
    }

    public void onFragmentChanged(int type, Bundle bundle)
    {
        if (type != SEARCH_RESULT_FRAGMENT_UPDATE)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (type)
            {
                case SEARCH_FRAGMENT:
                    SearchFragment searchFragment = new SearchFragment();
                    searchFragment.setData(bundle);

                    fragmentTransaction.add(R.id.map_activity_root_layout, searchFragment, SearchFragment.TAG);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    setZoomGpsButtonVisibility(View.GONE);

                    break;
                case SEARCH_RESULT_FRAGMENT:
                    // fragment_search_layout에 헤더/리스트 프래그먼트를 추가
                    setResultData(bundle);

                    if (searchResultController == null)
                    {
                        searchResultController = new SearchResultController();
                    }
                    searchResultController.setResultData(bundle);

                    List<Fragment> fragments = fragmentManager.getFragments();

                    int i = 0;
                    for (; i < fragments.size(); i++)
                    {
                        if (fragments.get(i) instanceof SearchFragment)
                        {
                            break;
                        }
                    }
                    fragmentTransaction.hide(fragments.get(i));
                    fragmentTransaction.add(R.id.map_activity_root_layout, searchResultController, SearchResultController.TAG);
                    fragmentTransaction.commit();
                    break;
            }
        } else
        {
            // fragment_search_layout에 헤더/리스트 프래그먼트를 추가
            setResultData(bundle);
        }
    }


    @Override
    public void onBackPressed()
    {
        if (onControlItemFragment.getBehaviorStateExpand())
        {
            onControlItemFragment.setBehaviorState(BottomSheetBehavior.STATE_HIDDEN);
        }
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();

        if (fragmentList != null)
        {
            for (Fragment fragment : fragmentList)
            {
                if (fragment.isVisible() && fragment instanceof OnBackPressedListener)
                {
                    ((OnBackPressedListener) fragment).onBackPressed();
                    return;
                }
            }
        }
        super.onBackPressed();
    }

    public void onChoicedLoc(Bundle bundle)
    {
        mapView = null;
        getIntent().putExtras(bundle);
        setResult(RESULT_OK, getIntent());
        finish();
    }

    private void deleteLoc()
    {
        mapView = null;
        setResult(RESULT_DELETE);
        finish();
    }

    class TimeOutThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                Thread.sleep(5000);

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                        gpsButton.setClickable(true);
                    }
                });
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
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
}