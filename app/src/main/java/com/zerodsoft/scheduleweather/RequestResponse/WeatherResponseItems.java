package com.zerodsoft.scheduleweather.RequestResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherResponseItems
{
    @SerializedName("item")
    @Expose
    private List<WeatherResponseItem> weatherResponseItemList;

    public List<WeatherResponseItem> getWeatherResponseItemList() {
        return weatherResponseItemList;
    }

    public void setWeatherResponseItemList(List<WeatherResponseItem> weatherResponseItemList) {
        this.weatherResponseItemList = weatherResponseItemList;
    }
}
