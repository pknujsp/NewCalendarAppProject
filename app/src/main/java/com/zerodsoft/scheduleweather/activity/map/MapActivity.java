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
    private final int FRAGMENT_CONTAINER_ID;

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

        // Map프래그먼트 실행
        replaceFragment(MapFragment.TAG, bundle);
    }


    @Override
    public void onChoicedLocation(Bundle bundle)
    {

    }

    @Override
    public void onBackPressed()
    {

    }
}