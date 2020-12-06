package com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.AddressListFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.PlaceListFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.SearchResultFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.FragmentRemover;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.IndicatorCreater;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;

import java.util.LinkedList;
import java.util.List;

public class SearchResultAdapter extends FragmentStateAdapter implements FragmentRemover
{
    private List<Fragment> fragments;
    private IndicatorCreater indicatorCreater;

    public SearchResultAdapter(@NonNull FragmentActivity fragmentActivity, Fragment fragment, String searchWord, LocalApiPlaceParameter parameter)
    {
        super(fragmentActivity);
        indicatorCreater = (IndicatorCreater) fragment;
        fragments = new LinkedList<>();
        fragments.add(new AddressListFragment(searchWord, parameter.copy()));
        fragments.add(new PlaceListFragment(searchWord, parameter.copy()));
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
