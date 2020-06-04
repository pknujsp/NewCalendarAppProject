package com.zerodsoft.scheduleweather.DataCommunication;

import com.zerodsoft.scheduleweather.RequestResponse.WeatherResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface DataDownloadService
{
    @GET("getUltraSrtNcst")
    Call<WeatherResponse> downloadCurrentWeatherData(@QueryMap(encoded = true) Map<String, String> queryMap);

    @GET("getVilageFcst")
    Call<WeatherResponse> downloadNForecastData(@QueryMap(encoded = true) Map<String, String> queryMap);
}
