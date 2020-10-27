package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.WeatherData;

import java.util.ArrayList;
import java.util.List;

public class WeatherViewPagerAdapter extends FragmentStateAdapter
{
    private List<WeatherItemFragment> fragmentList;

    public WeatherViewPagerAdapter(@NonNull Fragment fragment, List<WeatherData> weatherDataList)
    {
        super(fragment);
        fragmentList = new ArrayList<>(weatherDataList.size());
        for (WeatherData weatherData : weatherDataList)
        {
            fragmentList.add(new WeatherItemFragment(fragment.getContext(), weatherData));
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount()
    {
        return fragmentList.size();
    }
}
