package com.zerodsoft.tripweather.Room.DTO;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.io.Serializable;

@Entity(tableName = "weather_update_time_table")
public class WeatherUpdateTime implements Serializable
{
    @ColumnInfo(name = "update_time_travel_parent_id")
    private String travelParentId;

    @ColumnInfo(name = "update_time_cweather_parent_id")
    private String cWeatherParentId;

    public String getTravelParentId()
    {
        return travelParentId;
    }

    public void setTravelParentId(String travelParentId)
    {
        this.travelParentId = travelParentId;
    }

    public String getcWeatherParentId()
    {
        return cWeatherParentId;
    }

    public void setcWeatherParentId(String cWeatherParentId)
    {
        this.cWeatherParentId = cWeatherParentId;
    }
}
