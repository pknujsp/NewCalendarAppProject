package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.WeatherData;

import java.util.ArrayList;
import java.util.List;

public class WeatherViewPagerAdapter extends FragmentStateAdapter
{
    private List<WeatherItemView> viewList;

    public WeatherViewPagerAdapter(@NonNull Fragment fragment, List<WeatherData> weatherDataList)
    {
        super(fragment);
        viewList = new ArrayList<>(weatherDataList.size());
        for (WeatherData weatherData : weatherDataList)
        {
            viewList.add(new WeatherItemView(fragment.getContext(), weatherData));
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        return viewList.get(position);
    }

    @Override
    public int getItemCount()
    {
        return viewList.size();
    }

    class WeatherViewHolder extends RecyclerView.ViewHolder
    {

        public WeatherViewHolder(@NonNull View itemView)
        {
            super(itemView);
        }
    }

}
