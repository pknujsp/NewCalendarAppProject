package com.zerodsoft.scheduleweather.scheduleinfo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.scheduleinfo.locationfragments.LocationViewPagerAdapter;

import java.util.List;


public class InfoAroundLocationFragment extends Fragment
{
    private ViewPager2 viewPager;
    private LocationViewPagerAdapter viewPagerAdapter;

    public InfoAroundLocationFragment(List<AddressDTO> addresses, List<PlaceDTO> places)
    {
        viewPagerAdapter = new LocationViewPagerAdapter(this, addresses, places);
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_schedule_around_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        viewPager = (ViewPager2) view.findViewById(R.id.around_location_viewpager);
        viewPager.setAdapter(viewPagerAdapter);
    }
}