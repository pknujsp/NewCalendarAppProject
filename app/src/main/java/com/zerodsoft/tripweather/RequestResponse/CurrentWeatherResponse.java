package com.zerodsoft.tripweather.RequestResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class CurrentWeatherResponse {
    @SerializedName("response")
    @Expose
    private CurrentWeatherResponseResponse currentWeatherResponseResponse;

    public CurrentWeatherResponseResponse getCurrentWeatherResponseResponse() {
        return currentWeatherResponseResponse;
    }

    public void setCurrentWeatherResponseResponse(CurrentWeatherResponseResponse currentWeatherResponseResponse) {
        this.currentWeatherResponseResponse = currentWeatherResponseResponse;
    }
}
