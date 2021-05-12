package com.zerodsoft.scheduleweather.weather.repository;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.WeatherDataDAO;
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.room.interfaces.WeatherDataQuery;

import java.util.List;

import lombok.SneakyThrows;

public class WeatherDbRepository implements WeatherDataQuery
{
    private WeatherDataDAO dao;

    public WeatherDbRepository(Application application)
    {
        dao = AppDb.getInstance(application.getApplicationContext()).weatherDataDAO();
    }

    @Override
    public void insert(WeatherDataDTO weatherDataDTO, CarrierMessagingService.ResultCallback<WeatherDataDTO> callback)
    {
        new Thread(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                long id = dao.insert(weatherDataDTO);
                WeatherDataDTO result = dao.getWeatherData(weatherDataDTO.getLatitude(), weatherDataDTO.getLongitude(), weatherDataDTO.getDataType());
                callback.onReceiveResult(result);
            }
        }).start();
    }

    @Override
    public void update(String latitude, String longitude, Integer dataType, String json, String downloadedDate, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        new Thread(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                dao.update(latitude, longitude, dataType, json, downloadedDate);
                callback.onReceiveResult(true);
            }
        }).start();
    }

    @Override
    public void getWeatherDataList(String latitude, String longitude, CarrierMessagingService.ResultCallback<List<WeatherDataDTO>> callback)
    {
        new Thread(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                List<WeatherDataDTO> list = dao.getWeatherDataList(latitude, longitude);
                callback.onReceiveResult(list);
            }
        }).start();
    }

    @Override
    public void getWeatherData(String latitude, String longitude, Integer dataType, CarrierMessagingService.ResultCallback<WeatherDataDTO> callback)
    {
        new Thread(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                WeatherDataDTO result = dao.getWeatherData(latitude, longitude, dataType);
                callback.onReceiveResult(result);
            }
        }).start();
    }

    @Override
    public void getDownloadedDateList(String latitude, String longitude, CarrierMessagingService.ResultCallback<List<WeatherDataDTO>> callback)
    {
        new Thread(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                List<WeatherDataDTO> list = dao.getDownloadedDateList(latitude, longitude);
                callback.onReceiveResult(list);
            }
        }).start();
    }

    @Override
    public void delete(String latitude, String longitude, Integer dataType, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        new Thread(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                dao.delete(latitude, longitude, dataType);
                callback.onReceiveResult(true);
            }
        }).start();
    }

    @Override
    public void delete(String latitude, String longitude, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        new Thread(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                dao.delete(latitude, longitude);
                callback.onReceiveResult(true);
            }
        }).start();
    }

    @Override
    public void deleteAll(CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        new Thread(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                dao.deleteAll();
                callback.onReceiveResult(true);
            }
        }).start();
    }

    @Override
    public void contains(String latitude, String longitude, Integer dataType, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        new Thread(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                boolean result = dao.contains(latitude, longitude, dataType) == 1;
                callback.onReceiveResult(result);
            }
        }).start();
    }
}
