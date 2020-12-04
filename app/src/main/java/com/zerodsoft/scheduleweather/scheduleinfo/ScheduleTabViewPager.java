package com.zerodsoft.scheduleweather.scheduleinfo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

import java.util.ArrayList;
import java.util.List;

public class ScheduleTabViewPager extends FragmentStateAdapter
{
    private List<Fragment> fragments;
    private static final int ITEM_COUNT = 3;

    public ScheduleTabViewPager(FragmentActivity fragmentActivity)
    {
        super(fragmentActivity);
        fragments = new ArrayList<>();
    }

    public ScheduleTabViewPager setFragments(ScheduleDTO schedule, List<AddressDTO> addresses, List<PlaceDTO> places)
    {
        fragments.clear();

        fragments.add(new ScheduleInfoFragment(schedule, addresses, places));
        fragments.add(new ScheduleWeatherFragment(addresses, places));
        fragments.add(new PlacesAroundLocationFragment(addresses, places));

        return this;
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


}
