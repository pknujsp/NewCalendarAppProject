package com.zerodsoft.scheduleweather.RequestResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherResponseBody
{
    @SerializedName("items")
    @Expose
    private WeatherResponseItems weatherResponseItems;

    public WeatherResponseItems getWeatherResponseItems() {
        return weatherResponseItems;
    }

    public void setWeatherResponseItems(WeatherResponseItems weatherResponseItems) {
        this.weatherResponseItems = weatherResponseItems;
    }
}
