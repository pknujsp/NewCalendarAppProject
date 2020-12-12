package com.zerodsoft.scheduleweather.activity.map.fragment.searchresult;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.zerodsoft.scheduleweather.activity.map.fragment.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.adapter.SearchResultListAdapter;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.IMapSearch;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.IndicatorCreater;
import com.zerodsoft.scheduleweather.etc.ViewPagerIndicator;
import com.zerodsoft.scheduleweather.R;

public class SearchResultListFragment extends Fragment implements IndicatorCreater
{
    public static final String TAG = "SearchResultFragment";
    private static SearchResultListFragment instance;

    private ViewPager2 fragmentsViewPager;
    private SearchResultListAdapter searchResultListAdapter;

    private ViewPagerIndicator viewPagerIndicator;
    private final String SEARCH_WORD;

    private OnPageCallback onPageCallback;
    private IMapPoint iMapPoint;

    @Override
    public void setIndicator(int fragmentSize)
    {
        viewPagerIndicator.createDot(0, fragmentSize);
    }

    public SearchResultListFragment(String searchWord, IMapPoint iMapPoint)
    {
        this.SEARCH_WORD = searchWord;
        this.iMapPoint = iMapPoint;
    }

    public static SearchResultListFragment getInstance()
    {
        return instance;
    }

    public static SearchResultListFragment newInstance(String searchWord, IMapPoint iMapPoint)
    {
        instance = new SearchResultListFragment(searchWord, iMapPoint);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_search_result_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        fragmentsViewPager = (ViewPager2) view.findViewById(R.id.map_search_result_viewpager);
        viewPagerIndicator = (ViewPagerIndicator) view.findViewById(R.id.map_result_view_pager_indicator);

        searchResultListAdapter = new SearchResultListAdapter(this, iMapPoint, SEARCH_WORD);
        onPageCallback = new OnPageCallback();

        fragmentsViewPager.setAdapter(searchResultListAdapter);
        fragmentsViewPager.registerOnPageChangeCallback(onPageCallback);
        viewPagerIndicator.createDot(0, 2);
    }


    class OnPageCallback extends ViewPager2.OnPageChangeCallback
    {
        public int lastPosition;

        @Override
        public void onPageSelected(int position)
        {
            super.onPageSelected(position);
            lastPosition = position;
            viewPagerIndicator.selectDot(position);
        }
    }

}