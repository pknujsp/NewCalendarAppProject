package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.WeatherData;

import java.util.ArrayList;
import java.util.List;

public class WeatherViewPagerAdapter extends FragmentStateAdapter
{
    private final int SIZE;
    private List<Fragment> fragments;

    public WeatherViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<PlaceDTO> places, List<AddressDTO> addresses)
    {
        super(fragmentActivity);
        this.SIZE = places.size() + addresses.size();
        this.fragments = new ArrayList<>();

        for (int i = 0; i < places.size(); i++)
        {
            fragments.add(new WeatherItemFragment(Double.parseDouble(places.get(i).getLatitude()), Double.parseDouble(places.get(i).getLongitude())
                    , places.get(i).getAddressName()));
        }
        for (int i = 0; i < addresses.size(); i++)
        {
            fragments.add(new WeatherItemFragment(Double.parseDouble(addresses.get(i).getLatitude()), Double.parseDouble(addresses.get(i).getLongitude())
                    , addresses.get(i).getAddressName()));
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
        return SIZE;
    }
}
