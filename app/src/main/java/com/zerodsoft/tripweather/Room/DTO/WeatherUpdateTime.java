package com.zerodsoft.tripweather.Room.DTO;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "weather_update_time_table")
public class WeatherUpdateTime implements Serializable
{
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "update_time_travel_parent_id")
    private String travelParentId;

    @ColumnInfo(name = "update_time_cweather_parent_id")
    private String cWeatherParentId;

    @ColumnInfo(name = "update_time_forecast_date")
    private int forecastDate;

    @ColumnInfo(name = "update_time_forecast_time")
    private int forecastTime;

    @ColumnInfo(name = "update_time_ust_date")
    private int ustDate;

    @ColumnInfo(name = "update_time_ust_time")
    private int ustTime;

    @ColumnInfo(name = "update_time_current_date")
    private int currentDate;

    @ColumnInfo(name = "update_time_current_time")
    private int currentTime;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getTravelParentId()
    {
        return travelParentId;
    }

    public void setTravelParentId(String travelParentId)
    {
        this.travelParentId = travelParentId;
    }

    public String getCWeatherParentId()
    {
        return cWeatherParentId;
    }

    public void setCWeatherParentId(String cWeatherParentId)
    {
        this.cWeatherParentId = cWeatherParentId;
    }

    public int getForecastDate()
    {
        return forecastDate;
    }

    public void setForecastDate(int forecastDate)
    {
        this.forecastDate = forecastDate;
    }

    public int getForecastTime()
    {
        return forecastTime;
    }

    public void setForecastTime(int forecastTime)
    {
        this.forecastTime = forecastTime;
    }

    public int getUstDate()
    {
        return ustDate;
    }

    public void setUstDate(int ustDate)
    {
        this.ustDate = ustDate;
    }

    public int getUstTime()
    {
        return ustTime;
    }

    public void setUstTime(int ustTime)
    {
        this.ustTime = ustTime;
    }

    public int getCurrentDate()
    {
        return currentDate;
    }

    public void setCurrentDate(int currentDate)
    {
        this.currentDate = currentDate;
    }

    public int getCurrentTime()
    {
        return currentTime;
    }

    public void setCurrentTime(int currentTime)
    {
        this.currentTime = currentTime;
    }
}
