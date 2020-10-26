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

import java.util.List;

public class WeatherViewModel extends ViewModel
{
    private WeatherRepository weatherRepository;

    private MutableLiveData<List<UltraSrtNcstItem>> ultraSrtNcstLiveData = new MutableLiveData<>();
    private MutableLiveData<List<UltraSrtFcstItem>> ultraSrtFcstLiveData = new MutableLiveData<>();
    private MutableLiveData<List<VilageFcstItem>> vilageFcstLiveData = new MutableLiveData<>();
    private MutableLiveData<List<MidLandFcstItem>> midLandFcstLiveData = new MutableLiveData<>();
    private MutableLiveData<List<MidTaItem>> midTaLiveData = new MutableLiveData<>();

    private LiveData<List<WeatherAreaCodeDTO>> areaCodeLiveData;

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
