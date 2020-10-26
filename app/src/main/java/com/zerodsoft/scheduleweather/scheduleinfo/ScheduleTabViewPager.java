package com.zerodsoft.scheduleweather.scheduleinfo;

import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

import java.util.ArrayList;
import java.util.Arrays;
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

    public ScheduleTabViewPager setFragments(ScheduleDTO schedule, AddressDTO address, PlaceDTO place)
    {
        fragments.clear();
        fragments.add(new ScheduleInfoFragment(schedule, place, address));
        fragments.add(new ScheduleWeatherFragment(place, address));
        fragments.add(new ScheduleMapFragment(place, address));

        return this;
    }

    public void addFragment(Fragment fragment, int index)
    {
        fragments.add(index, fragment);
        notifyDataSetChanged();
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
