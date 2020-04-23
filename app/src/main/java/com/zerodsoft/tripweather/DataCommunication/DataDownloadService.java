package com.zerodsoft.tripweather.DataCommunication;

import com.zerodsoft.tripweather.RequestResponse.CurrentWeatherResponse;
import com.zerodsoft.tripweather.RequestResponse.CurrentWeatherResponseItem;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface DataDownloadService {
    @GET("getUltraSrtNcst")
    Call<CurrentWeatherResponse> downloadCurrentWeatherData(@QueryMap(encoded = true) Map<String, String> queryMap);
}
