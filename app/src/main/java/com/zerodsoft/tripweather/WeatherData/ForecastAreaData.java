package com.zerodsoft.tripweather.WeatherData;

import java.util.ArrayList;

public class ForecastAreaData
{
    private String areaX;
    private String areaY;
    private ArrayList<WeatherData> forecastData;

    public String getAreaX()
    {
        return areaX;
    }

    public ForecastAreaData setAreaX(String areaX)
    {
        this.areaX = areaX;
        return this;
    }

    public String getAreaY()
    {
        return areaY;
    }

    public ForecastAreaData setAreaY(String areaY)
    {
        this.areaY = areaY;
        return this;
    }

    public ArrayList<WeatherData> getForecastData()
    {
        return forecastData;
    }

    public ForecastAreaData setForecastData(ArrayList<WeatherData> forecastData)
    {
        this.forecastData = forecastData;
        return this;
    }
}