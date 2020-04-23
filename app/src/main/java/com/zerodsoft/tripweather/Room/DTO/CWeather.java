package com.zerodsoft.tripweather.Room.DTO;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.io.Serializable;

@Entity(tableName = "cweather_table")
public class CWeather implements Serializable
{
    @ColumnInfo(name = "cweather_parent_id")
    private int parentId;

    @ColumnInfo(name = "cweather_date")
    private String date;

    @ColumnInfo(name = "cweather_time")
    private String time;

    @ColumnInfo(name = "cweather_temperature")
    private String temp;

    @ColumnInfo(name = "cweather_one_hour_precipitation")
    private String oneHourPrecipitation;

    @ColumnInfo(name = "cweather_humidity")
    private String humidity;

    @ColumnInfo(name = "cweather_precipitation_form")
    private String precipitationForm;

    @ColumnInfo(name = "cweather_wind_direction")
    private String windDirection;

    @ColumnInfo(name = "cweather_wind_speed")
    private String windSpeed;

    public int getParentId()
    {
        return parentId;
    }

    public void setParentId(int parentId)
    {
        this.parentId = parentId;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getTemp()
    {
        return temp;
    }

    public void setTemp(String temp)
    {
        this.temp = temp;
    }

    public String getOneHourPrecipitation()
    {
        return oneHourPrecipitation;
    }

    public void setOneHourPrecipitation(String oneHourPrecipitation)
    {
        this.oneHourPrecipitation = oneHourPrecipitation;
    }

    public String getHumidity()
    {
        return humidity;
    }

    public void setHumidity(String humidity)
    {
        this.humidity = humidity;
    }

    public String getPrecipitationForm()
    {
        return precipitationForm;
    }

    public void setPrecipitationForm(String precipitationForm)
    {
        this.precipitationForm = precipitationForm;
    }

    public String getWindDirection()
    {
        return windDirection;
    }

    public void setWindDirection(String windDirection)
    {
        this.windDirection = windDirection;
    }

    public String getWindSpeed()
    {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed)
    {
        this.windSpeed = windSpeed;
    }
}
