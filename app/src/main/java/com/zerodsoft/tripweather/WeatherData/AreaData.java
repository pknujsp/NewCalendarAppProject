package com.zerodsoft.tripweather.WeatherData;

import com.zerodsoft.tripweather.Room.DTO.Area;

import java.util.ArrayList;

public class AreaData
{
    private Area area;
    private ArrayList<WeatherData> weatherDataList = new ArrayList<>();

    public Area getArea()
    {
        return area;
    }

    public AreaData setArea(Area area)
    {
        this.area = area;
        return this;
    }

    public ArrayList<WeatherData> getWeatherDataList()
    {
        return weatherDataList;
    }

    public AreaData setWeatherDataList(ArrayList<WeatherData> weatherDataList)
    {
        this.weatherDataList = weatherDataList;
        return this;
    }

    public void addItem(WeatherData weatherData)
    {
        weatherDataList.add(weatherData);
    }
}