package com.zerodsoft.scheduleweather.scheduleinfo.locationfragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LocationViewPagerAdapter extends FragmentStateAdapter
{
    List<Fragment> fragments;

    public LocationViewPagerAdapter(@NonNull Fragment fragment, List<AddressDTO> addresses, List<PlaceDTO> places)
    {
        super(fragment);
        fragments = new LinkedList<>();

        for (AddressDTO address : addresses)
        {
            fragments.add(new LocationItemFragment(address.getAddressName(), Double.parseDouble(address.getLatitude()), Double.parseDouble(address.getLongitude())));
        }

        for (PlaceDTO place : places)
        {
            fragments.add(new LocationItemFragment(place.getPlaceName(), Double.parseDouble(place.getLatitude()), Double.parseDouble(place.getLongitude())));
        }
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
