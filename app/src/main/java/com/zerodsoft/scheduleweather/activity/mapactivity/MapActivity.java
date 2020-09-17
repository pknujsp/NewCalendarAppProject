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
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.SearchResultController;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.SearchResultFragment;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.SearchResultHeaderFragment;
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
    // 프래그먼트가 변경되면서 localapi 객체가 변형됨
    public static int requestCode = 0;
    public static boolean isSelectedLocation = false;
    private final int fragmentViewId;
    private MapController mapController;

    public static final LocalApiPlaceParameter parameters = new LocalApiPlaceParameter();
    public static LocationSearchResult searchResult = new LocationSearchResult();

    public MapActivity()
    {
        fragmentViewId = R.id.map_activity_fragment_layout;
        mapController = new MapController(this);
    }

    @Override
    public void onDownloadedData(int dataType, String fragmentTag)
    {
        // 다운로드된 데이터를 전달
        if (fragmentTag.equals(SearchResultFragment.TAG))
        {
            // 초기 검색 결과
            SearchResultController searchResultController = SearchResultController.getInstance(this);
            searchResultController.setDownloadedData();

            MapFragment mapFragment = MapFragment.getInstance(this);
            mapFragment.setPoiItems();

        } else if (fragmentTag.equals(SearchResultViewPagerAdapter.TAG))
        {
            if (dataType != MapController.TYPE_NOT)
            {
                // 스크롤하면서 추가 데이터가 필요한 경우
                SearchResultController searchResultController = SearchResultController.getInstance(this);
                searchResultController.setDownloadedExtraData(dataType);
            } else
            {
                // 스피너 사용, 내 위치/지도 중심으로 변경한 경우

            }
            MapFragment mapFragment = MapFragment.getInstance(this);
            mapFragment.addExtraData();
            mapFragment.setPoiItems();

        } else if (fragmentTag.equals(MapFragment.TAG))
        {
            // 지정된 주소 검색 완료
            MapFragment mapFragment = MapFragment.getInstance(this);
        }
    }

    @Override
    public void requestData(int dataType, String fragmentTag)
    {
        mapController.selectLocation(dataType, fragmentTag);
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
        SearchResultController searchResultController = SearchResultController.getInstance(this);
        searchResultController.setChangeButtonDrawable();

        MapFragment mapFragment = MapFragment.getInstance(this);
        mapFragment.setDataType(dataType).onItemSelected(position);
        MapFragment.isClickedListItem = true;
        MapFragment.isMain = false;
        SearchResultController.isShowList = false;

        searchResultController.setVisibility(SearchResultFragment.TAG, View.GONE);
        getSupportFragmentManager().beginTransaction().hide(SearchFragment.getInstance(this)).commit();
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
            fragmentTransaction.add(fragmentViewId, mapFragment, MapFragment.TAG);
        } else if (fragmentTag.equals(SearchFragment.TAG))
        {
            // Map프래그먼트에서 상단 바를 터치한 경우
            SearchFragment searchFragment = SearchFragment.getInstance(this);
            fragmentTransaction.add(fragmentViewId, searchFragment, SearchFragment.TAG).addToBackStack(SearchFragment.TAG);
        } else if (fragmentTag.equals(SearchResultFragment.TAG))
        {
            SearchResultController searchResultController = SearchResultController.getInstance(this);
            searchResultController.setInitialData(bundle);
            fragmentTransaction.add(fragmentViewId, searchResultController, SearchResultController.TAG).addToBackStack(SearchResultController.TAG);
        }
        fragmentTransaction.commit();
    }

    public void changeMapOrList(int dataType)
    {
        SearchResultController searchResultController = SearchResultController.getInstance(this);
        searchResultController.setChangeButtonDrawable();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        MapFragment mapFragment = MapFragment.getInstance(this);

        if (SearchResultController.isShowList)
        {
            // to map
            // result header가 원래 자리에 있어야함
            mapFragment.setDataType(dataType).clickedMapButton();
            MapFragment.isClickedChangeButton = true;
            MapFragment.isMain = false;

            searchResultController.setVisibility(SearchResultFragment.TAG, View.GONE);
            fragmentTransaction.hide(SearchFragment.getInstance(this)).commit();
            SearchResultController.isShowList = false;
        } else
        {
            // to list
            searchResultController.setVisibility(SearchResultFragment.TAG, View.VISIBLE);
            fragmentTransaction.show(SearchFragment.getInstance(this)).commit();
            SearchResultController.isShowList = true;
        }
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
        /*
        - mapfragment의 경우

        1. main인 경우
        액티비티 종료

        2. main이 아닌 경우
        2-1. 이전에 선택된 위치의 정보를 표시하는 경우
        back시에 액티비티 종료

        2-2. poiitem을 표시중인 경우
        result list를 재표시

        - resultfragment의 경우
        searchfragment를 재표시

        - searchfragment의 경우
        mapfragment를 재표시

        searchresult header와 list는 인터페이스를 구현하지 않음
         */
        List<Fragment> fragments = getSupportFragmentManager().getFragments();

        for (int i = fragments.size() - 1; i >= 0; i--)
        {
            if (fragments.get(i) instanceof OnBackPressedListener)
            {
                ((OnBackPressedListener) fragments.get(i)).onBackPressed();
                return;
            }
        }

        // MapFragment가 Main인 경우에 수행됨
        setResult(RESULT_CANCELED);
        finish();
    }
}