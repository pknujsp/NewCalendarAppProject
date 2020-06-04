package com.zerodsoft.scheduleweather.RequestResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherResponseItem
{
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

    @SerializedName("fcstDate")
    @Expose
    private String fcstDate;

    @SerializedName("fcstTime")
    @Expose
    private String fcstTime;

    @SerializedName("fcstValue")
    @Expose
    private String fcstValue;

    public String getFcstDate()
    {
        return fcstDate;
    }

    public void setFcstDate(String fcstDate)
    {
        this.fcstDate = fcstDate;
    }

    public String getFcstTime()
    {
        return fcstTime;
    }

    public void setFcstTime(String fcstTime)
    {
        this.fcstTime = fcstTime;
    }

    public String getFcstValue()
    {
        return fcstValue;
    }

    public void setFcstValue(String fcstValue)
    {
        this.fcstValue = fcstValue;
    }

    public String getBaseDate()
    {
        return baseDate;
    }

    public WeatherResponseItem setBaseDate(String baseDate)
    {
        this.baseDate = baseDate;
        return this;
    }

    public String getBaseTime()
    {
        return baseTime;
    }

    public WeatherResponseItem setBaseTime(String baseTime)
    {
        this.baseTime = baseTime;
        return this;
    }

    public String getCategory()
    {
        return category;
    }

    public WeatherResponseItem setCategory(String category)
    {
        this.category = category;
        return this;
    }

    public String getNx()
    {
        return nx;
    }

    public WeatherResponseItem setNx(String nx)
    {
        this.nx = nx;
        return this;
    }

    public String getNy()
    {
        return ny;
    }

    public WeatherResponseItem setNy(String ny)
    {
        this.ny = ny;
        return this;
    }

    public String getObsrValue()
    {
        return obsrValue;
    }

    public WeatherResponseItem setObsrValue(String obsrValue)
    {
        this.obsrValue = obsrValue;
        return this;
    }
}
