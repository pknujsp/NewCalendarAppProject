package com.zerodsoft.tripweather.RequestResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrentWeatherResponseBody {
    @SerializedName("items")
    @Expose
    private CurrentWeatherResponseItems currentWeatherResponseItems;

    public CurrentWeatherResponseItems getCurrentWeatherResponseItems() {
        return currentWeatherResponseItems;
    }

    public void setCurrentWeatherResponseItems(CurrentWeatherResponseItems currentWeatherResponseItems) {
        this.currentWeatherResponseItems = currentWeatherResponseItems;
    }
}
