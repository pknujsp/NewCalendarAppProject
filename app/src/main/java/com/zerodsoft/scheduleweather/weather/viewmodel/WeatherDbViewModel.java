package com.zerodsoft.scheduleweather.weather.viewmodel;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.room.interfaces.WeatherDataQuery;
import com.zerodsoft.scheduleweather.weather.repository.WeatherDbRepository;

import java.util.List;

public class WeatherDbViewModel extends AndroidViewModel implements WeatherDataQuery
{
    private WeatherDbRepository repository;

    public WeatherDbViewModel(@NonNull Application application)
    {
        super(application);
        repository = new WeatherDbRepository(application);
    }

    @Override
    public void insert(WeatherDataDTO weatherDataDTO, CarrierMessagingService.ResultCallback<WeatherDataDTO> callback)
    {
        repository.insert(weatherDataDTO, callback);
    }

    @Override
    public void update(String latitude, String longitude, Integer dataType, String json, String downloadedDate, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        repository.update(latitude, longitude, dataType, json, downloadedDate, callback);
    }

    @Override
    public void getWeatherDataList(String latitude, String longitude, CarrierMessagingService.ResultCallback<List<WeatherDataDTO>> callback)
    {
        repository.getWeatherDataList(latitude, longitude, callback);
    }

    @Override
    public void getWeatherData(String latitude, String longitude, Integer dataType, CarrierMessagingService.ResultCallback<WeatherDataDTO> callback)
    {
        repository.getWeatherData(latitude, longitude, dataType, callback);
    }

    @Override
    public void getDownloadedDateList(String latitude, String longitude, CarrierMessagingService.ResultCallback<List<WeatherDataDTO>> callback)
    {
        repository.getDownloadedDateList(latitude, longitude, callback);
    }

    @Override
    public void delete(String latitude, String longitude, Integer dataType, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        repository.delete(latitude, longitude, dataType, callback);
    }

    @Override
    public void delete(String latitude, String longitude, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        repository.delete(latitude, longitude, callback);
    }

    @Override
    public void deleteAll(CarrierMessagingService.ResultCallback<Boolean> callback)
    {

    }

    @Override
    public void contains(String latitude, String longitude, Integer dataType, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        repository.contains(latitude, longitude, dataType, callback);
    }
}
