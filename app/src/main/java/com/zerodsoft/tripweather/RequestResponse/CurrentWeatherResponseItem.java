package com.zerodsoft.tripweather.RequestResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrentWeatherResponseItem {
    @SerializedName("baseDate")
    @Expose
    private String baseDate;

    @SerializedName("baseTime")
    @Expose
    private String baseTime;

    @SerializedName("category")
    @Expose
    private String category;

    @SerializedName("nx")
    @Expose
    private String nx;

    @SerializedName("ny")
    @Expose
    private String ny;

    @SerializedName("obsrValue")
    @Expose
    private String obsrValue;

    public String getBaseDate() {
        return baseDate;
    }

    public CurrentWeatherResponseItem setBaseDate(String baseDate) {
        this.baseDate = baseDate;
        return this;
    }

    public String getBaseTime() {
        return baseTime;
    }

    public CurrentWeatherResponseItem setBaseTime(String baseTime) {
        this.baseTime = baseTime;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public CurrentWeatherResponseItem setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getNx() {
        return nx;
    }

    public CurrentWeatherResponseItem setNx(String nx) {
        this.nx = nx;
        return this;
    }

    public String getNy() {
        return ny;
    }

    public CurrentWeatherResponseItem setNy(String ny) {
        this.ny = ny;
        return this;
    }

    public String getObsrValue() {
        return obsrValue;
    }

    public CurrentWeatherResponseItem setObsrValue(String obsrValue) {
        this.obsrValue = obsrValue;
        return this;
    }
}
