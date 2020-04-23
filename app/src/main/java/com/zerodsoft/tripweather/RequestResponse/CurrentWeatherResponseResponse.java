package com.zerodsoft.tripweather.RequestResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrentWeatherResponseResponse {
    @SerializedName("body")
    @Expose
    private CurrentWeatherResponseBody currentWeatherResponseBody;

    public CurrentWeatherResponseBody getCurrentWeatherResponseBody() {
        return currentWeatherResponseBody;
    }

    public void setCurrentWeatherResponseBody(CurrentWeatherResponseBody currentWeatherResponseBody) {
        this.currentWeatherResponseBody = currentWeatherResponseBody;
    }
}
