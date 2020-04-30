package com.zerodsoft.tripweather.RequestResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherResponseResponse
{
    @SerializedName("body")
    @Expose
    private WeatherResponseBody weatherResponseBody;

    public WeatherResponseBody getWeatherResponseBody() {
        return weatherResponseBody;
    }

    public void setWeatherResponseBody(WeatherResponseBody weatherResponseBody) {
        this.weatherResponseBody = weatherResponseBody;
    }
}
