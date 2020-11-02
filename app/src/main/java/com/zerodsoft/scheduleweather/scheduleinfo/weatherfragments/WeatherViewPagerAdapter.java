package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments;

import android.content.Context;
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
import java.util.List;

public class WeatherViewPagerAdapter extends RecyclerView.Adapter<WeatherViewPagerAdapter.WeatherViewHolder>
{
    private List<WeatherItemView> viewList;

    public WeatherViewPagerAdapter(@NonNull Fragment fragment, List<WeatherData> weatherDataList)
    {
        viewList = new ArrayList<>(weatherDataList.size());
        for (int i = 0; i < weatherDataList.size(); i++)
        {
            viewList.add(new WeatherItemView(fragment.getContext(), weatherDataList.get(i)));
        }
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

        public void onBind()
        {
            FrameLayout viewGroup = (FrameLayout) itemView.findViewById(R.id.weather_container);
            viewGroup.addView(viewList.get(getAdapterPosition()));
        }
    }

}
