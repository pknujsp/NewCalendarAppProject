package com.zerodsoft.tripweather.RequestResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CurrentWeatherResponseItems {
    @SerializedName("item")
    @Expose
    private List<CurrentWeatherResponseItem> currentWeatherResponseItemList;

    public List<CurrentWeatherResponseItem> getCurrentWeatherResponseItemList() {
        return currentWeatherResponseItemList;
    }

    public void setCurrentWeatherResponseItemList(List<CurrentWeatherResponseItem> currentWeatherResponseItemList) {
        this.currentWeatherResponseItemList = currentWeatherResponseItemList;
    }
}
