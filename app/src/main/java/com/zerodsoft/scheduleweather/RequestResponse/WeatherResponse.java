package com.zerodsoft.scheduleweather.RequestResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class WeatherResponse
{
    @SerializedName("response")
    @Expose
    private WeatherResponseResponse weatherResponseResponse;

    public WeatherResponseResponse getWeatherResponseResponse() {
        return weatherResponseResponse;
    }

    public void setWeatherResponseResponse(WeatherResponseResponse weatherResponseResponse) {
        this.weatherResponseResponse = weatherResponseResponse;
    }
}
