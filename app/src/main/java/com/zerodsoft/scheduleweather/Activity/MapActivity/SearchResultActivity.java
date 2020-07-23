package com.zerodsoft.scheduleweather.Activity.MapActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.RecyclerVIewAdapter.SearchResultViewPagerAdapter;
import com.zerodsoft.scheduleweather.Retrofit.DownloadData;
import com.zerodsoft.scheduleweather.Retrofit.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressSearchResult;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;

public class SearchResultActivity extends AppCompatActivity
{

    private ImageButton closeButton;
    private ImageButton goToMapButton;
    private ViewPager2 viewPager2;
    private SearchResultViewPagerAdapter searchResultViewPagerAdapter;
    private AddressSearchResult result;
    private TextView rescanMapCenter;
    private TextView rescanMyLocCenter;
    private LocalApiPlaceParameter parameters;
    private LocationManager locationManager;

    private LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            parameters.setX(Double.toString(location.getLongitude()));
            parameters.setY(Double.toString(location.getLatitude()));
            parameters.setRadius(LocalApiPlaceParameter.DEFAULT_RADIUS);
            parameters.setRect(null);
            // 자원해제
            locationManager.removeUpdates(locationListener);

            if (isCategory)
            {
                DownloadData.searchPlaceCategory(handler, parameters);
            } else
            {
                DownloadData.searchAddress(handler, parameters);
                DownloadData.searchPlaceKeyWord(handler, parameters);
            }
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
    private boolean isCategory;

    private Handler handler = new Handler()
    {
        private AddressSearchResult addressSearchResult = null;

        @Override
        public void handleMessage(Message msg)
        {
            Bundle bundle = msg.getData();

            if (addressSearchResult == null)
            {
                addressSearchResult = new AddressSearchResult();
            }

            switch (msg.what)
            {
                case DownloadData.ADDRESS:
                    addressSearchResult.setAddressResponseDocuments(bundle.getParcelableArrayList("documents"));
                    break;
                case DownloadData.PLACE_KEYWORD:
                    addressSearchResult.setPlaceKeywordDocuments(bundle.getParcelableArrayList("documents"));
                    break;
                case DownloadData.PLACE_CATEGORY:
                    addressSearchResult.setPlaceCategoryDocuments(bundle.getParcelableArrayList("documents"));
                    break;
            }

            if (addressSearchResult.getResultNum() == 1 && !addressSearchResult.getPlaceCategoryDocuments().isEmpty())
            {
                result = addressSearchResult.clone();
                addressSearchResult.clearAll();
                searchResultViewPagerAdapter.setAddressSearchResult(result);
                searchResultViewPagerAdapter.notifyDataSetChanged();
            } else if (addressSearchResult.getResultNum() == 2)
            {
                result = addressSearchResult.clone();
                addressSearchResult.clearAll();
                searchResultViewPagerAdapter.setAddressSearchResult(result);
                searchResultViewPagerAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        closeButton = (ImageButton) findViewById(R.id.search_result_close_button);
        goToMapButton = (ImageButton) findViewById(R.id.search_result_map_button);
        viewPager2 = (ViewPager2) findViewById(R.id.search_address_viewpager);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Bundle bundle = getIntent().getExtras();
        result = bundle.getParcelable("result");
        parameters = bundle.getParcelable("parameters");
        isCategory = bundle.getBoolean("isCategory");

        searchResultViewPagerAdapter = new SearchResultViewPagerAdapter(SearchResultActivity.this);
        searchResultViewPagerAdapter.setAddressSearchResult(result);
        viewPager2.setAdapter(searchResultViewPagerAdapter);

        rescanMapCenter = (TextView) findViewById(R.id.search_result_map_center_rescan);
        rescanMyLocCenter = (TextView) findViewById(R.id.search_result_myloc_center_rescan);

        rescanMapCenter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                MapPointBounds mapPointBounds = MapActivity.mapView.getMapPointBounds();
                String rect = mapPointBounds.bottomLeft.getMapPointGeoCoord().longitude + "," +
                        mapPointBounds.bottomLeft.getMapPointGeoCoord().latitude + "," +
                        mapPointBounds.topRight.getMapPointGeoCoord().longitude + "," +
                        mapPointBounds.topRight.getMapPointGeoCoord().latitude;

                parameters.setRect(rect);

                if (isCategory)
                {
                    DownloadData.searchPlaceCategory(handler, parameters);
                } else
                {
                    DownloadData.searchAddress(handler, parameters);
                    DownloadData.searchPlaceKeyWord(handler, parameters);
                }
            }
        });

        rescanMyLocCenter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                int fineLocationPermission = ContextCompat.checkSelfPermission(SearchResultActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                int coarseLocationPermission = ContextCompat.checkSelfPermission(SearchResultActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);

                if (isGpsEnabled && isNetworkEnabled)
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                }
            }
        });


        closeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onBackPressed();
            }
        });

        goToMapButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }
}