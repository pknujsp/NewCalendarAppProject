package com.zerodsoft.scheduleweather.activity.mapactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.zerodsoft.scheduleweather.activity.ScheduleInfoActivity;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.MapController;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.MapFragment;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.SearchFragment;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.SearchResultController;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.SearchResultFragment;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.recyclerviewadapter.SearchResultViewAdapter;
import com.zerodsoft.scheduleweather.recyclerviewadapter.SearchResultViewPagerAdapter;
import com.zerodsoft.scheduleweather.retrofit.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.LocationSearchResult;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;

import net.daum.mf.map.api.MapPoint;

import java.util.List;

public class MapActivity extends AppCompatActivity implements MapController.OnDownloadListener, SearchResultViewAdapter.OnItemSelectedListener
{
    public static int requestCode = 0;
    public static boolean isSelectedLocation = false;
    private final int fragmentViewId;

    private MapController mapController;

    public MapActivity()
    {
        fragmentViewId = R.id.map_activity_fragment_layout;
        mapController = new MapController(this);
    }

    @Override
    public void onDownloadedData(LocalApiPlaceParameter parameter, int dataType, String fragmentTag, LocationSearchResult locationSearchResult)
    {
        // 다운로드된 데이터를 전달
        if (fragmentTag.equals(SearchResultFragment.TAG))
        {
            // 초기 검색 결과
            SearchResultController searchResultController = SearchResultController.getInstance(this);
            searchResultController.setDownloadedData(parameter, locationSearchResult);

            MapFragment mapFragment = MapFragment.getInstance(this);
            mapFragment.setLocationSearchResult(locationSearchResult);
            mapFragment.setPoiItems();

        } else if (fragmentTag.equals(SearchResultViewPagerAdapter.TAG))
        {
            if (dataType != MapController.TYPE_NOT)
            {
                // 스크롤하면서 추가 데이터가 필요한 경우
                SearchResultController searchResultController = SearchResultController.getInstance(this);
                searchResultController.setDownloadedExtraData(parameter, dataType, locationSearchResult);
            } else
            {
                // 스피너 사용, 내 위치/지도 중심으로 변경한 경우

            }
            MapFragment mapFragment = MapFragment.getInstance(this);
            mapFragment.setLocationSearchResult(locationSearchResult);
            mapFragment.setPoiItems();

        } else if (fragmentTag.equals(MapFragment.TAG))
        {
            // 지정된 주소 검색 완료
            MapFragment mapFragment = MapFragment.getInstance(this);
            mapFragment.setSelectedLocationData(parameter, dataType, locationSearchResult);
        }
    }

    @Override
    public void requestData(LocalApiPlaceParameter parameter, int dataType, String fragmentTag)
    {
        mapController.selectLocation(parameter, dataType, fragmentTag);
    }

    public MapPoint.GeoCoordinate getMapCenterPoint()
    {
        MapFragment mapFragment = MapFragment.getInstance(this);
        return mapFragment.getMapCenterPoint();
    }

    @Override
    public void onItemSelected(int position, int dataType)
    {
        // 위치 검색 결과 리스트에서 하나를 선택한 경우 수행된다
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
            case ScheduleInfoActivity.ADD_LOCATION:
                isSelectedLocation = false;
                break;

            case ScheduleInfoActivity.EDIT_LOCATION:
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
            MapFragment mapFragment = MapFragment.getInstance(this);
            mapFragment.setInitialData(bundle);
            fragmentTransaction.replace(fragmentViewId, mapFragment);
        } else if (fragmentTag.equals(SearchFragment.TAG))
        {
            // Map프래그먼트에서 상단 바를 터치한 경우
            SearchFragment searchFragment = SearchFragment.getInstance(this);
            searchFragment.setInitialData(bundle);
            fragmentTransaction.replace(fragmentViewId, searchFragment);
        } else if (fragmentTag.equals(SearchResultFragment.TAG))
        {
            SearchResultController searchResultController = SearchResultController.getInstance(this);
            searchResultController.setInitialData(bundle);
            fragmentTransaction.replace(fragmentViewId, searchResultController);
        }
        fragmentTransaction.commit();
    }

    public void onChoicedLocation(Bundle bundle)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        List<Fragment> fragments = fragmentManager.getFragments();

        for (Fragment fragment : fragments)
        {
            if (fragment instanceof SearchResultFragment || fragment instanceof MapFragment)
            {
                fragmentTransaction.remove(fragment).commit();
                break;
            }
        }

        getIntent().putExtras(bundle);
        setResult(RESULT_OK, getIntent());
        finish();
    }

    @Override
    public void onBackPressed()
    {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();

        for (Fragment fragment : fragments)
        {
            if (fragment instanceof OnBackPressedListener)
            {
                ((OnBackPressedListener) fragment).onBackPressed();
                return;
            }
        }

        setResult(RESULT_CANCELED);
        finish();
    }
}