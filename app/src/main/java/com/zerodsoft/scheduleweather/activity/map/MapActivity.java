package com.zerodsoft.scheduleweather.activity.map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.zerodsoft.scheduleweather.activity.editschedule.ScheduleEditActivity;
import com.zerodsoft.scheduleweather.activity.map.fragment.interfaces.FragmentReplace;
import com.zerodsoft.scheduleweather.activity.map.fragment.map.MapFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.search.SearchFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.SearchResultFragmentController;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.SearchResultFragment;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.LocationSearchResult;

import java.util.List;

public class MapActivity extends AppCompatActivity implements FragmentReplace
{
    /*
    <오류>
    결과의 dataType이 2개 이상인 경우, 스피너 재 선택/ 위치 기준 재지정 시 다운로드한 데이터 값 갱신에 오류
     */
    public static int requestCode = 0;
    public static boolean isSelectedLocation = false;
    public static boolean isDeletedLocation = false;
    private final int FRAGMENT_CONTAINER_ID;

    public static final LocalApiPlaceParameter parameters = new LocalApiPlaceParameter();
    public static LocationSearchResult searchResult = new LocationSearchResult();

    private MapFragment mapFragment;
    private SearchFragment searchFragment;
    private SearchResultFragment searchResultFragment;

    public MapActivity()
    {
        FRAGMENT_CONTAINER_ID = R.id.map_activity_fragment_container;
    }

    @Override
    public void replaceFragment(String fragmentTag, Bundle bundle)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (fragmentTag.equals(MapFragment.TAG))
        {
            mapFragment = MapFragment.getInstance(this);
            mapFragment.setInitialData(bundle);
            fragmentTransaction.add(FRAGMENT_CONTAINER_ID, mapFragment, MapFragment.TAG);
        } else if (fragmentTag.equals(SearchFragment.TAG))
        {
            // Map프래그먼트에서 상단 바를 터치한 경우
            searchFragment = new SearchFragment();
            fragmentTransaction.hide(mapFragment).add(FRAGMENT_CONTAINER_ID, searchFragment, SearchFragment.TAG)
                    .addToBackStack(SearchFragment.TAG);
        } else if (fragmentTag.equals(SearchResultFragment.TAG))
        {
            SearchResultFragmentController searchResultFragmentController = new SearchResultFragmentController(bundle);
            fragmentTransaction.hide(searchFragment).add(FRAGMENT_CONTAINER_ID, searchResultFragmentController, SearchResultFragmentController.TAG)
                    .addToBackStack(SearchResultFragmentController.TAG);
        }
        fragmentTransaction.commitAllowingStateLoss();
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
            case ScheduleEditActivity.ADD_LOCATION:
                isSelectedLocation = false;
                break;

            case ScheduleEditActivity.EDIT_LOCATION:
                isSelectedLocation = true;
                bundle.putParcelable("selectedPlace", intent.getParcelableExtra("place"));
                bundle.putParcelable("selectedAddress", intent.getParcelableExtra("address"));
                break;
        }
        // Map프래그먼트 실행
        replaceFragment(MapFragment.TAG, bundle);
    }


    @Override
    public void onChoicedLocation(Bundle bundle)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        List<Fragment> fragments = fragmentManager.getFragments();

        for (Fragment fragment : fragments)
        {
            fragmentTransaction.remove(fragment);
            break;
        }
        fragmentTransaction.commit();

        MapActivity.parameters.clear();
        MapFragment.getInstance(this).setMain();

        getIntent().putExtras(bundle);
        if (isDeletedLocation)
        {
            setResult(ScheduleEditActivity.RESULT_RESELECTED, getIntent());
        } else
        {
            setResult(ScheduleEditActivity.RESULT_SELECTED, getIntent());
        }
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
        if (isDeletedLocation)
        {
            setResult(ScheduleEditActivity.RESULT_DELETED);
        } else
        {
            setResult(RESULT_CANCELED);
        }
        isSelectedLocation = false;
        isDeletedLocation = false;
        finish();
    }
}