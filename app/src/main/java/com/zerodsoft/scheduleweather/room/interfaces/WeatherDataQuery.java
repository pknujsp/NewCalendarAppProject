package com.zerodsoft.scheduleweather.room.interfaces;

import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.room.dao.WeatherDataDAO;
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.thread.Result;

import java.util.List;

public interface WeatherDataQuery
{
    void insert(WeatherDataDTO weatherDataDTO, CarrierMessagingService.ResultCallback<WeatherDataDTO> callback);

    void update(String latitude, String longitude, Integer dataType, String json, CarrierMessagingService.ResultCallback<Boolean> callback);

    void getWeatherDataList(String latitude, String longitude, CarrierMessagingService.ResultCallback<List<WeatherDataDTO>> callback);

    void getWeatherData(String latitude, String longitude, Integer dataType, CarrierMessagingService.ResultCallback<WeatherDataDTO> callback);

    void getDownloadedDataList(String latitude, String longitude, CarrierMessagingService.ResultCallback<List<WeatherDataDTO>> callback);

    void delete(String latitude, String longitude, Integer dataType, CarrierMessagingService.ResultCallback<Boolean> callback);

    void delete(String latitude, String longitude, CarrierMessagingService.ResultCallback<Boolean> callback);

    void contains(String latitude, String longitude, Integer dataType, CarrierMessagingService.ResultCallback<Boolean> callback);
}
