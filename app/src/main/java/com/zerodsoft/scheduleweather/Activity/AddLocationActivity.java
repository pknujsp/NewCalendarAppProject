package com.zerodsoft.scheduleweather.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zerodsoft.scheduleweather.Fragment.MapBottomSheetFragment;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Retrofit.DownloadData;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceCategoryResponse.PlaceCategoryDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceKeywordResponse.PlaceKeywordDocuments;
import com.zerodsoft.scheduleweather.Room.DTO.LocationDTO;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.List;

public class AddLocationActivity extends AppCompatActivity
{
    private TextView addressTextView;
    private ImageButton checkButton;
    private ImageButton zoomInButton;
    private ImageButton zoomOutButton;
    private ImageButton gpsButton;

    private List<AddressResponseDocuments> addressList = null;
    private List<PlaceKeywordDocuments> placeKeywordList = null;
    private List<PlaceCategoryDocuments> placeCategoryList = null;

    private long downloadedTime;
    private int resultType;
    private int selectedItemPosition;

    private LocationDTO locationDTO;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        addressTextView = (TextView) findViewById(R.id.address_textview);
        checkButton = (ImageButton) findViewById(R.id.add_location_check_button);
        zoomInButton = (ImageButton) findViewById(R.id.zoom_in_button);
        zoomOutButton = (ImageButton) findViewById(R.id.zoom_out_button);
        gpsButton = (ImageButton) findViewById(R.id.gps_button);

        mapView = new MapView(this);
        if (!MapView.isMapTilePersistentCacheEnabled())
        {
            MapView.setMapTilePersistentCacheEnabled(true);
        }
        ConstraintLayout mapViewContainer = (ConstraintLayout) findViewById(R.id.add_location_map_view);
        mapViewContainer.addView(mapView);

        availableIntent();

        mapView.setCurrentLocationEventListener(new MapView.CurrentLocationEventListener()
        {
            @Override
            public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v)
            {
                // 단말의 현위치 좌표값을 통보받을 수 있다.
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(mapPoint.getMapPointGeoCoord().latitude, mapPoint.getMapPointGeoCoord().longitude), true);

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

        checkButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                setResult(RESULT_OK, getIntent());
                finish();
            }
        });

        addressTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AddLocationActivity.this, SearchAddressActivity.class);
                startActivity(intent);
            }
        });

        zoomInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // 줌 인
                mapView.zoomIn(true);

            }
        });

        zoomOutButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // 줌 인
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
    }

    private boolean availableIntent()
    {
        if (getIntent().getExtras() != null)
        {
            Bundle bundle = getIntent().getExtras();
            resultType = bundle.getInt("type");
            selectedItemPosition = bundle.getInt("position");
            downloadedTime = bundle.getLong("downloadedTime");

            if (resultType == DownloadData.ADDRESS)
            {
                addressList = bundle.getParcelableArrayList("itemsInfo");
            } else if (resultType == DownloadData.PLACE_KEYWORD || resultType == DownloadData.PLACE_CATEGORY)
            {
                placeKeywordList = bundle.getParcelableArrayList("itemsInfo");
            }
            displayItemBottomSheet(selectedItemPosition);
            return true;
        } else
        {
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true);
            return false;
        }
    }

    private void displayItemBottomSheet(int position)
    {
        MapBottomSheetFragment mapBottomSheetFragment = MapBottomSheetFragment.getInstance();
        mapBottomSheetFragment.show(getSupportFragmentManager(), MapBottomSheetFragment.TAG);

        double latitude = 0, longitude = 0;

        if (resultType == DownloadData.ADDRESS)
        {
            longitude = addressList.get(position).getX();
            latitude = addressList.get(position).getY();

            mapBottomSheetFragment.setAddress(addressList.get(position));
        } else if (resultType == DownloadData.PLACE_KEYWORD)
        {
            longitude = placeKeywordList.get(position).getX();
            latitude = placeKeywordList.get(position).getY();

            mapBottomSheetFragment.setPlaceKeyword(placeKeywordList.get(position));
        } else if (resultType == DownloadData.PLACE_CATEGORY)
        {
            longitude = Double.valueOf(placeCategoryList.get(position).getX());
            latitude = Double.valueOf(placeCategoryList.get(position).getY());

            mapBottomSheetFragment.setPlaceCategory(placeCategoryList.get(position));
        }
        setCenterPoint(latitude, longitude, position);
    }


    private void setCenterPoint(double latitude, double longitude, int position)
    {
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);

        MapPOIItem marker = new MapPOIItem();

        if (resultType == DownloadData.ADDRESS)
        {
            marker.setItemName(addressList.get(position).getAddressName());
        } else if (resultType == DownloadData.PLACE_KEYWORD)
        {
            marker.setItemName(placeKeywordList.get(position).getPlaceName());
        } else if (resultType == DownloadData.PLACE_CATEGORY)
        {
            marker.setItemName(placeCategoryList.get(position).getPlaceName());
        }
        marker.setTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker);
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