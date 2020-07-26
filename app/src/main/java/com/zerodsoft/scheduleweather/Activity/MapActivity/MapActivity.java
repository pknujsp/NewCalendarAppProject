package com.zerodsoft.scheduleweather.Activity.MapActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.Activity.MapActivity.Fragment.SearchFragment;
import com.zerodsoft.scheduleweather.Activity.MapActivity.Fragment.SearchResultFragment;
import com.zerodsoft.scheduleweather.Fragment.MapBottomSheetFragment;
import com.zerodsoft.scheduleweather.Fragment.SearchResultHeaderFragment;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.RecyclerVIewAdapter.SearchResultViewAdapter;
import com.zerodsoft.scheduleweather.Retrofit.DownloadData;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceCategoryResponse.PlaceCategoryDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceKeywordResponse.PlaceKeywordDocuments;
import com.zerodsoft.scheduleweather.Room.DTO.LocationDTO;

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

    private TextView addressTextView;
    private ImageButton zoomInButton;
    private ImageButton zoomOutButton;
    private ImageButton gpsButton;

    private List<AddressResponseDocuments> addressList = null;
    private List<PlaceKeywordDocuments> placeKeywordList = null;
    private List<PlaceCategoryDocuments> placeCategoryList = null;

    private long downloadedTime;
    private int resultType;
    private int selectedItemPosition;
    public static boolean isMainMapActivity = true;

    private LocationDTO locationDTO;

    private MapBottomSheetFragment mapBottomSheetFragment;

    public static MapView mapView;
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
                onControlItemFragment.onShowItemInfo(poiTag);
                opendPOIInfo = true;
                clickedPOI = false;
                return true;
            }

            return false;
        }
    };

    @Override
    protected void onStart()
    {
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
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
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
        SearchResultHeaderFragment searchResultHeaderFragment = (SearchResultHeaderFragment) getSupportFragmentManager().findFragmentById(R.id.search_result_map_header);
        getSupportFragmentManager().beginTransaction().hide(searchResultHeaderFragment).commit();

        mapView = new MapView(this);

        gestureDetectorCompat = new GestureDetectorCompat(this, onGestureListener);

        if (isMainMapActivity)
        {
            mapView.setMapCenterPoint(currentMapPoint, true);
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
                MapPointBounds mapPointBounds = mapView.getMapPointBounds();

                final String rect = mapPointBounds.bottomLeft.getMapPointGeoCoord().longitude + "," +
                        mapPointBounds.bottomLeft.getMapPointGeoCoord().latitude + "," +
                        mapPointBounds.topRight.getMapPointGeoCoord().longitude + "," +
                        mapPointBounds.topRight.getMapPointGeoCoord().latitude;

                Bundle bundle = new Bundle();
                bundle.putString("rect", rect);

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
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                gpsButton.setClickable(false);

                TimeOutThread timeOutThread = new TimeOutThread();
                timeOutThread.start();
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

    @Override
    public void onItemClicked(Bundle bundle)
    {
        // Item Info Map Activity
        isMainMapActivity = false;
        String itemName = null;
        resultType = bundle.getInt("type");
        selectedItemPosition = bundle.getInt("position");
        downloadedTime = bundle.getLong("downloadedTime");

        if (resultType == DownloadData.ADDRESS)
        {
            addressList = bundle.getParcelableArrayList("itemsInfo");
            itemName = addressList.get(selectedItemPosition).getAddressName();
        } else if (resultType == DownloadData.PLACE_KEYWORD)
        {
            placeKeywordList = bundle.getParcelableArrayList("itemsInfo");
            itemName = placeKeywordList.get(selectedItemPosition).getPlaceName();
        } else if (resultType == DownloadData.PLACE_CATEGORY)
        {
            placeCategoryList = bundle.getParcelableArrayList("itemsInfo");
            itemName = placeCategoryList.get(selectedItemPosition).getPlaceName();
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.hide(SearchResultFragment.getInstance());
        fragmentTransaction.hide(SearchFragment.getInstance());

        SearchResultHeaderFragment searchResultHeaderFragment = (SearchResultHeaderFragment) getSupportFragmentManager().findFragmentById(R.id.search_result_map_header);
        searchResultHeaderFragment.setItemName(itemName);
        fragmentTransaction.show(searchResultHeaderFragment);

        fragmentTransaction.commit();


        displayItemBottomSheet(selectedItemPosition);
    }

    private void displayItemBottomSheet(int position)
    {
        double latitude = 0, longitude = 0;
        Bundle bundle = new Bundle();

        if (resultType == DownloadData.ADDRESS)
        {
            longitude = addressList.get(position).getX();
            latitude = addressList.get(position).getY();

            bundle.putParcelableArrayList("itemList", (ArrayList<? extends Parcelable>) addressList);
            bundle.putInt("type", DownloadData.ADDRESS);
        } else if (resultType == DownloadData.PLACE_KEYWORD)
        {
            longitude = placeKeywordList.get(position).getX();
            latitude = placeKeywordList.get(position).getY();

            bundle.putParcelableArrayList("itemList", (ArrayList<? extends Parcelable>) placeKeywordList);
            bundle.putInt("type", DownloadData.PLACE_KEYWORD);
        } else if (resultType == DownloadData.PLACE_CATEGORY)
        {
            longitude = Double.valueOf(placeCategoryList.get(position).getX());
            latitude = Double.valueOf(placeCategoryList.get(position).getY());

            bundle.putParcelableArrayList("itemList", (ArrayList<? extends Parcelable>) placeCategoryList);
            bundle.putInt("type", DownloadData.PLACE_CATEGORY);
        }
        bundle.putInt("position", position);

        onControlItemFragment.onChangeFragment(bundle);
        opendPOIInfo = true;

        setCenterPoint(latitude, longitude);
    }


    private void setCenterPoint(double latitude, double longitude)
    {
        int size = 0;

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
        currentMapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
        mapView.setMapCenterPoint(currentMapPoint, true);
    }

    public static void clearAllPoiItems()
    {
        mapView.removeAllPOIItems();
    }

    private void initItemFragment()
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        mapBottomSheetFragment = MapBottomSheetFragment.getInstance();
        onControlItemFragment = mapBottomSheetFragment;

        fragmentTransaction.add(mapBottomSheetFragment, MapBottomSheetFragment.TAG);
        fragmentTransaction.show(mapBottomSheetFragment).commit();
    }

    public void onFragmentChanged(int type, Bundle bundle)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (type)
        {
            case SEARCH_FRAGMENT:
                SearchFragment searchFragment = SearchFragment.getInstance();
                searchFragment.setData(bundle);

                fragmentTransaction.add(R.id.map_activity_root_layout, searchFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case SEARCH_RESULT_FRAGMENT:
                SearchResultFragment searchResultFragment = SearchResultFragment.getInstance();
                searchResultFragment.setData(bundle);

                fragmentTransaction.add(R.id.map_activity_root_layout, searchResultFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.hide(SearchFragment.getInstance());
                fragmentTransaction.commit();
                break;
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

        if (!isMainMapActivity)
        {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.show(SearchResultFragment.getInstance());

            fragmentTransaction.commit();
            return;
        }

        super.onBackPressed();
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
}