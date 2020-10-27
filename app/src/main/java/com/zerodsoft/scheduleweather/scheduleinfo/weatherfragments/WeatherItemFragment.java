package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.WeatherData;

public class WeatherItemFragment extends Fragment
{
    private WeatherData weatherData;
    private Context context;

    public WeatherItemFragment(Context context, WeatherData weatherData)
    {
        this.context = context;
        this.weatherData = weatherData;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.weather_recycler_view_item, container, false);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Toast.makeText(getActivity(), weatherData.getUltraSrtNcstData().getTemperature(), Toast.LENGTH_SHORT).show();
    }
}
