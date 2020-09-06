package com.zerodsoft.scheduleweather.activity.mapactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zerodsoft.scheduleweather.activity.ScheduleInfoActivity;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.MapController;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.MapFragment;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.SearchFragment;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.SearchResultFragment;
import com.zerodsoft.scheduleweather.fragment.SearchResultController;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.recyclerviewadapter.SearchResultViewPagerAdapter;
import com.zerodsoft.scheduleweather.retrofit.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.LocationSearchResult;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;

import net.daum.mf.map.api.MapPoint;

import java.util.List;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements MapController.OnDownloadListener
{
    public static int requestCode = 0;
    public static boolean isSelectedLocation = false;
    private final int fragmentViewId;

    private AddressDTO selectedAddress;
    private PlaceDTO selectedPlace;

    private MapController mapController;

    public MapActivity()
    {
        fragmentViewId = R.id.map_activity_fragment_layout;
        mapController = new MapController(this);
    }

    @Override
    public void onDownloadedData(LocalApiPlaceParameter parameter, String tag, LocationSearchResult locationSearchResult)
    {
        // 다운로드된 데이터를 전달
        if (tag.equals(SearchResultFragment.TAG))
        {
            // 초기 검색 결과
            SearchResultFragment searchResultFragment = SearchResultFragment.getInstance(this);
            searchResultFragment.setDownloadedData(parameter, locationSearchResult);
        } else if (tag.equals(SearchResultViewPagerAdapter.TAG))
        {
            // 스피너 사용, 내 위치/지도 중심으로 변경한 경우
        }
    }

    @Override
    public void onDownloadedExtraData(LocalApiPlaceParameter parameter, int type, LocationSearchResult locationSearchResult)
    {
        SearchResultFragment searchResultFragment = SearchResultFragment.getInstance(this);
        searchResultFragment.setDownloadedExtraData(parameter, type, locationSearchResult);
    }

    @Override
    public void requestData(LocalApiPlaceParameter parameter, String tag)
    {
        mapController.selectLocation(parameter, tag);
    }

    @Override
    public void requestExtraData(LocalApiPlaceParameter parameter, int type)
    {
        mapController.selectLocation(parameter, type);
    }

    public MapPoint.GeoCoordinate getMapCenterPoint()
    {
        MapFragment mapFragment = MapFragment.getInstance();
        return mapFragment.getMapCenterPoint();
    }


    public interface OnBackPressedListener
    {
        void onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        requestCode = intent.getIntExtra("requestCode", 0);
        Bundle bundle = new Bundle();

        switch (requestCode)
        {
            ScheduleInfoActivity.ADD_LOCATION:
            isSelectedLocation = false;
            break;

            ScheduleInfoActivity.EDIT_LOCATION:
            isSelectedLocation = true;
            bundle.putParcelable("selectedPlace", intent.getParcelableExtra("place"));
            bundle.putParcelable("selectedAddress", intent.getParcelableExtra("address"));

            break;
        }
        onFragmentChanged(MapFragment.TAG, bundle);
    }

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
    protected void onStop()
    {
        super.onStop();
    }

    public void onFragmentChanged(String fragmentTag, Bundle bundle)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (fragmentTag.equals(MapFragment.TAG))
        {
            MapFragment mapFragment = MapFragment.getInstance();
            mapFragment.setInitialData(bundle);
            fragmentTransaction.replace(fragmentViewId, mapFragment);
        } else if (fragmentTag.equals(SearchFragment.TAG))
        {
            SearchFragment searchFragment = SearchFragment.getInstance(this);
            searchFragment.setInitialData(bundle);
            fragmentTransaction.replace(fragmentViewId, searchFragment);
        } else if (fragmentTag.equals(SearchResultFragment.TAG))
        {
            SearchResultFragment searchResultFragment = SearchResultFragment.getInstance(this);
            searchResultFragment.setInitialData(bundle);
            fragmentTransaction.replace(fragmentViewId, searchResultFragment);
        }
        fragmentTransaction.addToBackStack(null).commit();
    }

}