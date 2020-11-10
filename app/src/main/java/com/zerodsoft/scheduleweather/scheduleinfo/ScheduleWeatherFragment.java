package com.zerodsoft.scheduleweather.scheduleinfo;

import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.retrofit.paremeters.MidFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.VilageFcstParameter;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.WeatherViewPagerAdapter;
import com.zerodsoft.scheduleweather.utility.WeatherDataConverter;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.WeatherViewModel;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.WeatherData;
import com.zerodsoft.scheduleweather.utility.LonLat;
import com.zerodsoft.scheduleweather.utility.LonLatConverter;

import java.util.ArrayList;
import java.util.List;


public class ScheduleWeatherFragment extends Fragment
{
    private ImageButton refreshButton;
    private WeatherViewPagerAdapter adapter;
    private ViewPager2 viewPager;

    private List<PlaceDTO> places;
    private List<AddressDTO> addresses;

    public ScheduleWeatherFragment(List<AddressDTO> addresses, List<PlaceDTO> places)
    {
        this.places = places;
        this.addresses = addresses;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        WeatherDataConverter.context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_schedule_weather, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        viewPager = view.findViewById(R.id.location_items_pager);
        adapter = new WeatherViewPagerAdapter(getActivity(), places, addresses);
        viewPager.setAdapter(adapter);
    }

    public void rotateRefreshButton(boolean value)
    {
        if (value)
        {
            RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
            refreshButton.startAnimation(rotateAnimation);
        } else
        {
            refreshButton.getAnimation().cancel();
        }
    }
}