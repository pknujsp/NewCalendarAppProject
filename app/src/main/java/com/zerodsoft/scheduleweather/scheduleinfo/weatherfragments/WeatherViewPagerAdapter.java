package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.WeatherData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeatherViewPagerAdapter extends RecyclerView.Adapter<WeatherViewPagerAdapter.WeatherViewHolder>
{
    private int size;
    private SparseArray<WeatherViewHolder> weatherViewHolderSparseArray;

    public WeatherViewPagerAdapter(@NonNull Fragment fragment, int size)
    {
        this.size = size;
        this.weatherViewHolderSparseArray = new SparseArray<>(size);
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new WeatherViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_recycler_view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position)
    {
        holder.onBind();
        weatherViewHolderSparseArray.put(position, holder);
    }

    @Override
    public int getItemCount()
    {
        return size;
    }

    class WeatherViewHolder extends RecyclerView.ViewHolder
    {
        private WeatherItemView weatherItemView;

        public WeatherViewHolder(@NonNull View itemView)
        {
            super(itemView);
        }

        public void onBind()
        {
            FrameLayout viewGroup = (FrameLayout) itemView.findViewById(R.id.weather_container);
            weatherItemView = new WeatherItemView(itemView.getContext());
            viewGroup.addView(weatherItemView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        public void setData(WeatherData weatherData)
        {
            weatherItemView.setWeatherData(weatherData);
        }
    }

    public void setData(List<WeatherData> weatherDataList)
    {
        for (int i = 0; i < weatherDataList.size(); i++)
        {
            weatherViewHolderSparseArray.get(i).setData(weatherDataList.get(i));
        }
    }
}
