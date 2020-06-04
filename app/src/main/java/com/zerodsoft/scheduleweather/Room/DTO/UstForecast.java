package com.zerodsoft.scheduleweather.Room.DTO;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "ustforecast_table")
public class UstForecast implements Serializable
{
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "ustforecast_parent_id")
    private int parentId;

    @ColumnInfo(name = "ustforecast_date")
    private int date;

    @ColumnInfo(name = "ustforecast_time")
    private int time;

    @ColumnInfo(name = "ustforecast_temperature")
    private String temp;

    @ColumnInfo(name = "ustforecast_one_hour_precipitation")
    private String oneHourPrecipitation;

    @ColumnInfo(name = "ustforecast_sky")
    private String sky;

    @ColumnInfo(name = "ustforecast_humidity")
    private String humidity;

    @ColumnInfo(name = "ustforecast_precipitation_form")
    private String precipitationForm;

    @ColumnInfo(name = "ustforecast_thunder")
    private String thunder;

    @ColumnInfo(name = "ustforecast_wind_direction")
    private String windDirection;

    @ColumnInfo(name = "ustforecast_wind_speed")
    private String windSpeed;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getParentId()
    {
        return parentId;
    }

    public void setParentId(int parentId)
    {
        this.parentId = parentId;
    }

    public int getDate()
    {
        return date;
    }

    public void setDate(int date)
    {
        this.date = date;
    }

    public int getTime()
    {
        return time;
    }

    public void setTime(int time)
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

    public String getSky()
    {
        return sky;
    }

    public void setSky(String sky)
    {
        this.sky = sky;
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

    public String getThunder()
    {
        return thunder;
    }

    public void setThunder(String thunder)
    {
        this.thunder = thunder;
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
