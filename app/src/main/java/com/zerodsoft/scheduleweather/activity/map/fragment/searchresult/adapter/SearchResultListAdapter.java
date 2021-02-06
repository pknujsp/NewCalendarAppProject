package com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zerodsoft.scheduleweather.kakaomap.interfaces.IBottomSheet;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.AddressListFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.PlaceListFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.FragmentRemover;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.IndicatorCreater;

import java.util.ArrayList;
import java.util.List;

public class SearchResultListAdapter extends FragmentStateAdapter
{
    private List<Fragment> fragments;

    public SearchResultListAdapter(@NonNull Fragment fragment)
    {
        super(fragment);
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

    public void setFragments(List<Fragment> fragments)
    {
        this.fragments = fragments;
    }

    public Fragment getFragment(int position)
    {
        return fragments.get(position);
    }
}