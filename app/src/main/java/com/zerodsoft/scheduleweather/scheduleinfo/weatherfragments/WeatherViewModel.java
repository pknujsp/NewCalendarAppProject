package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.zerodsoft.scheduleweather.retrofit.paremeters.MidFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.VilageFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midlandfcstresponse.MidLandFcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midtaresponse.MidTaItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtfcstresponse.UltraSrtFcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtncstresponse.UltraSrtNcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.vilagefcstresponse.VilageFcstItem;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.WeatherData;

import java.util.List;

public class WeatherViewModel extends ViewModel
{
    private WeatherRepository weatherRepository;

    private LiveData<List<WeatherAreaCodeDTO>> areaCodeLiveData;
    private MutableLiveData<List<WeatherData>> weatherDataLiveData;

    public void init(Context context, int x, int y)
    {
        weatherRepository = new WeatherRepository(context);
        selectAreaCode(x, y);
    }

    public LiveData<List<WeatherAreaCodeDTO>> getAreaCodeLiveData()
    {
        areaCodeLiveData = weatherRepository.getAreaCodeLiveData();
        return areaCodeLiveData;
    }

    public WeatherViewModel selectAreaCode(int x, int y)
    {
        weatherRepository.selectAreaCode(x, y);
        return this;
    }

    public void getAllWeathersData(VilageFcstParameter vilageFcstParameter, MidFcstParameter midLandFcstParameter, MidFcstParameter midTaFcstParameter, WeatherAreaCodeDTO weatherAreaCode)
    {
        weatherRepository.getAllWeathersData(vilageFcstParameter, midLandFcstParameter, midTaFcstParameter, weatherAreaCode);
    }

    public MutableLiveData<List<WeatherData>> getWeatherDataLiveData()
    {
        weatherDataLiveData = weatherRepository.getWeatherDataLiveData();
        return weatherDataLiveData;
    }
}
