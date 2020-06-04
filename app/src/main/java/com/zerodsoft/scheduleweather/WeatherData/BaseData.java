package com.zerodsoft.scheduleweather.WeatherData;

public class BaseData
{
    private String fcstDate;
    private String fcstTime;

    public String getFcstDate()
    {
        return fcstDate;
    }

    public BaseData setFcstDate(String fcstDate)
    {
        this.fcstDate = fcstDate;
        return this;
    }

    public String getFcstTime()
    {
        return fcstTime;
    }

    public BaseData setFcstTime(String fcstTime)
    {
        this.fcstTime = fcstTime;
        return this;
    }
}
