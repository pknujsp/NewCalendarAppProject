package com.zerodsoft.scheduleweather.Room.DTO;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "weather_update_time_table", indices = {@Index(value = {"update_time_travel_parent_id", "update_time_cweather_parent_id"}, unique = true)})
public class WeatherUpdateTime implements Serializable
{
    @PrimaryKey(autoGenerate = true)
    private int updateId;

    @ColumnInfo(name = "update_time_travel_parent_id")
    private int travelParentId;

    @ColumnInfo(name = "update_time_cweather_parent_id")
    private int cWeatherParentId;

    @ColumnInfo(name = "update_time_nforecast_date")
    private String forecastDate;

    @ColumnInfo(name = "update_time_nforecast_time")
    private String forecastTime;

    @ColumnInfo(name = "update_time_ust_date")
    private String ustDate;

    @ColumnInfo(name = "update_time_ust_time")
    private String ustTime;

    @ColumnInfo(name = "updated_date")
    private String updatedDate;

    @ColumnInfo(name = "updated_time")
    private String updatedTime;

    public int getUpdateId()
    {
        return updateId;
    }

    public void setUpdateId(int updateId)
    {
        this.updateId = updateId;
    }

    public int getTravelParentId()
    {
        return travelParentId;
    }

    public void setTravelParentId(int travelParentId)
    {
        this.travelParentId = travelParentId;
    }

    public int getCWeatherParentId()
    {
        return cWeatherParentId;
    }

    public void setCWeatherParentId(int cWeatherParentId)
    {
        this.cWeatherParentId = cWeatherParentId;
    }


    public String getUstDate()
    {
        return ustDate;
    }

    public void setUstDate(String ustDate)
    {
        this.ustDate = ustDate;
    }

    public String getUstTime()
    {
        return ustTime;
    }

    public void setUstTime(String ustTime)
    {
        this.ustTime = ustTime;
    }

    public String getUpdatedDate()
    {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate)
    {
        this.updatedDate = updatedDate;
    }

    public String getUpdatedTime()
    {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime)
    {
        this.updatedTime = updatedTime;
    }

    public String getForecastDate()
    {
        return forecastDate;
    }

    public void setForecastDate(String forecastDate)
    {
        this.forecastDate = forecastDate;
    }

    public String getForecastTime()
    {
        return forecastTime;
    }

    public void setForecastTime(String forecastTime)
    {
        this.forecastTime = forecastTime;
    }
}
