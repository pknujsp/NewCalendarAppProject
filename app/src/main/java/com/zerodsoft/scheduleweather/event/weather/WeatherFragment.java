package com.zerodsoft.scheduleweather.event.weather;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.event.location.placefragments.LocationInfo;
import com.zerodsoft.scheduleweather.event.weather.weatherfragments.fragment.WeatherItemFragment;
import com.zerodsoft.scheduleweather.utility.WeatherDataConverter;

import java.util.ArrayList;


public class WeatherFragment extends Fragment
{
    // 이벤트의 위치 값으로 날씨를 표시할 정확한 위치를 지정하기 위해 위치 지정 액티비티 생성(기상청 지역 리스트 기반)
    private ImageButton refreshButton;
    private FragmentManager fragmentManager;


    public WeatherFragment()
    {

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