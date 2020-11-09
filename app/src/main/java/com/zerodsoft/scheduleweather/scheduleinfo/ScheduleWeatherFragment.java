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
    private WeatherViewModel viewModel;
    private PlaceDTO place;
    private AddressDTO address;
    private ImageButton refreshButton;

    private WeatherViewPagerAdapter adapter;

    private ViewPager2 viewPager;

    public ScheduleWeatherFragment(PlaceDTO place, AddressDTO address)
    {
        this.place = place;
        this.address = address;
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule_weather, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        VilageFcstParameter vilageFcstParameter = new VilageFcstParameter();
        MidFcstParameter midLandFcstParameter = new MidFcstParameter();
        MidFcstParameter midTaParameter = new MidFcstParameter();

        double longitude = 0;
        double latitude = 0;

        if (place != null)
        {
            longitude = Double.valueOf(place.getLongitude());
            latitude = Double.valueOf(place.getLatitude());
        } else if (address != null)
        {
            longitude = Double.valueOf(address.getLongitude());
            latitude = Double.valueOf(address.getLatitude());
        }

        LonLat lonLat = LonLatConverter.lonLatToGridXY(longitude, latitude);

        viewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        viewModel.init(getContext(), lonLat.getX(), lonLat.getY());
        viewModel.getAreaCodeLiveData().observe(getViewLifecycleOwner(), new Observer<List<WeatherAreaCodeDTO>>()
        {
            @Override
            public void onChanged(List<WeatherAreaCodeDTO> weatherAreaCodes)
            {
                if (weatherAreaCodes != null)
                {
                    // regId설정하는 코드 작성
                    WeatherAreaCodeDTO weatherAreaCode = weatherAreaCodes.get(0);

                    vilageFcstParameter.setNx(weatherAreaCode.getX()).setNy(weatherAreaCode.getY()).setNumOfRows("10").setPageNo("1");
                    midLandFcstParameter.setNumOfRows("10").setPageNo("1").setRegId(weatherAreaCode.getMidLandFcstCode());
                    midTaParameter.setNumOfRows("10").setPageNo("1").setRegId(weatherAreaCode.getMidTaCode());

                    adapter = new WeatherViewPagerAdapter(ScheduleWeatherFragment.this, 1);
                    viewPager.setAdapter(adapter);
                    rotateRefreshButton(true);

                    viewModel.getAllWeathersData(vilageFcstParameter, midLandFcstParameter, midTaParameter, weatherAreaCode);
                }
            }
        });

        viewModel.getWeatherDataLiveData().observe(getViewLifecycleOwner(), new Observer<List<WeatherData>>()
        {
            @Override
            public void onChanged(List<WeatherData> weatherDataList)
            {
                if (weatherDataList != null)
                {
                    adapter.setData(weatherDataList);
                    adapter.notifyDataSetChanged();
                    rotateRefreshButton(false);
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        viewPager = view.findViewById(R.id.location_items_pager);
        refreshButton = (ImageButton) view.findViewById(R.id.refresh_weather_data_button);

        refreshButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // rotateRefreshButton(true);
            }
        });

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