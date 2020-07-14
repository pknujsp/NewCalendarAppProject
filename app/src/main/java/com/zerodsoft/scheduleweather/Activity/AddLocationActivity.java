package com.zerodsoft.scheduleweather.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Retrofit.DownloadData;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressSearchResult;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceCategoryResponse.PlaceCategoryDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceKeywordResponse.PlaceKeywordDocuments;
import com.zerodsoft.scheduleweather.Room.DTO.LocationDTO;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class AddLocationActivity extends AppCompatActivity
{
    private TextView addressTextView;
    private ImageButton checkButton;
    private ImageButton zoomInButton;
    private ImageButton zoomOutButton;
    private ImageButton gpsButton;

    private List<PlaceKeywordDocuments> addressList = null;
    private List<PlaceKeywordDocuments> placeKeywordList = null;
    private List<PlaceCategoryDocuments> placeCategoryList = null;
    private int type;
    private int position;

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

        if (getIntent().getExtras() != null)
        {
            Bundle bundle = getIntent().getExtras();
            type = bundle.getInt("type");
            position = bundle.getInt("position");
            double longitude = 0, latitude = 0;

            if (type == DownloadData.ADDRESS)
            {
                addressList = bundle.getParcelableArrayList("placeInfo");
                longitude = addressList.get(position).getX();
                latitude = addressList.get(position).getY();
            } else if (type == DownloadData.PLACE_KEYWORD)
            {
                placeKeywordList = bundle.getParcelableArrayList("placeInfo");
                longitude = placeKeywordList.get(position).getX();
                latitude = placeKeywordList.get(position).getY();
            } else if (type == DownloadData.PLACE_CATEGORY)
            {
                placeCategoryList = bundle.getParcelableArrayList("placeInfo");
                longitude = Double.valueOf(placeCategoryList.get(position).getX());
                latitude = Double.valueOf(placeCategoryList.get(position).getY());
            }
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
        } else
        {
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true);
        }
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
                Bundle bundle = new Bundle();
                bundle.putSerializable("location", locationDTO);
                getIntent().putExtras(bundle);
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