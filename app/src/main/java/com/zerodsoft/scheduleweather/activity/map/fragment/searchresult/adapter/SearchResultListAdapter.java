package com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zerodsoft.scheduleweather.activity.map.fragment.dto.SearchData;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.AddressListFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.PlaceListFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.FragmentRemover;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.IndicatorCreater;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SearchResultListAdapter extends FragmentStateAdapter implements FragmentRemover
{
    private List<Fragment> fragments;
    private IndicatorCreater indicatorCreater;

    public SearchResultListAdapter(@NonNull Fragment fragment, SearchData searchData)
    {
        super(fragment);
        this.indicatorCreater = (IndicatorCreater) fragment;
        fragments = new ArrayList<>();
        fragments.add(new AddressListFragment(this, searchData));
        fragments.add(new PlaceListFragment(this, searchData));
    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        return fragments.get(position);
    }

    @Override
    public int getItemCount()
    {
        return fragments.size();
    }

    @Override
    public void removeFragment(Fragment fragment)
    {
        fragments.remove(fragment);
        notifyDataSetChanged();
    }
}