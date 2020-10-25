package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.retrofit.paremeters.MidFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.VilageFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midlandfcstresponse.MidLandFcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midtaresponse.MidTaItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtfcstresponse.UltraSrtFcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtncstresponse.UltraSrtNcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.vilagefcstresponse.VilageFcstItem;

import java.util.List;

public class WeatherViewModel extends AndroidViewModel
{
    private WeatherRepository weatherRepository;

    private MutableLiveData<List<UltraSrtNcstItem>> ultraSrtNcstLiveData = new MutableLiveData<>();
    private MutableLiveData<List<UltraSrtFcstItem>> ultraSrtFcstLiveData = new MutableLiveData<>();
    private MutableLiveData<List<VilageFcstItem>> vilageFcstLiveData = new MutableLiveData<>();
    private MutableLiveData<List<MidLandFcstItem>> midLandFcstLiveData = new MutableLiveData<>();
    private MutableLiveData<List<MidTaItem>> midTaLiveData = new MutableLiveData<>();

    public WeatherViewModel(@NonNull Application application)
    {
        super(application);
        weatherRepository = new WeatherRepository(application);
    }

    public WeatherViewModel getUltraSrtNcstData(VilageFcstParameter parameter)
    {
        weatherRepository.getUltraSrtNcstData(parameter);
        return this;
    }

    public WeatherViewModel getUltraSrtFcstData(VilageFcstParameter parameter)
    {
        weatherRepository.getUltraSrtFcstData(parameter);
        return this;
    }

    public WeatherViewModel getVilageFcstData(VilageFcstParameter parameter)
    {
        weatherRepository.getVilageFcstData(parameter);
        return this;
    }

    public WeatherViewModel getMidLandFcstData(MidFcstParameter parameter)
    {
        weatherRepository.getMidLandFcstData(parameter);
        return this;
    }

    public WeatherViewModel getMidTaData(MidFcstParameter parameter)
    {
        weatherRepository.getMidTaData(parameter);
        return this;
    }

    public MutableLiveData<List<UltraSrtNcstItem>> getUltraSrtNcstLiveData()
    {
        ultraSrtNcstLiveData = weatherRepository.getUltraSrtNcstLiveData();
        return ultraSrtNcstLiveData;
    }

    public MutableLiveData<List<UltraSrtFcstItem>> getUltraSrtFcstLiveData()
    {
        ultraSrtFcstLiveData = weatherRepository.getUltraSrtFcstLiveData();
        return ultraSrtFcstLiveData;
    }

    public MutableLiveData<List<VilageFcstItem>> getVilageFcstLiveData()
    {
        vilageFcstLiveData = weatherRepository.getVilageFcstLiveData();
        return vilageFcstLiveData;
    }

    public MutableLiveData<List<MidLandFcstItem>> getMidLandFcstLiveData()
    {
        midLandFcstLiveData = weatherRepository.getMidLandFcstLiveData();
        return midLandFcstLiveData;
    }

    public MutableLiveData<List<MidTaItem>> getMidTaLiveData()
    {
        midTaLiveData = weatherRepository.getMidTaLiveData();
        return midTaLiveData;
    }
}
