package com.zerodsoft.scheduleweather.weather.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.weather.repository.WeatherRepository;
import com.zerodsoft.scheduleweather.utility.LonLat;

import java.util.List;

public class WeatherViewModel extends ViewModel
{
    private WeatherRepository weatherRepository;

    private LiveData<List<WeatherAreaCodeDTO>> areaCodeLiveData;

    public void init(Context context, LonLat lonLat)
    {
        weatherRepository = new WeatherRepository(context);
        selectAreaCode(lonLat);
    }

    public LiveData<List<WeatherAreaCodeDTO>> getAreaCodeLiveData()
    {
        areaCodeLiveData = weatherRepository.getAreaCodeLiveData();
        return areaCodeLiveData;
    }

    public WeatherViewModel selectAreaCode(LonLat lonLat)
    {
        weatherRepository.selectAreaCode(lonLat);
        return this;
    }

}
