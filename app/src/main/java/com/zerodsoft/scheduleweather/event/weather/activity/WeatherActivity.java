package com.zerodsoft.scheduleweather.event.weather.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.event.weather.fragment.WeatherItemFragment;

public class WeatherActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        WeatherItemFragment weatherFragment = new WeatherItemFragment(0);
        weatherFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.weather_fragment_container, weatherFragment, WeatherItemFragment.TAG).commit();
    }
}