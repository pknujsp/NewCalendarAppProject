package com.zerodsoft.scheduleweather.kakaomap.fragment.searchresult.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

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