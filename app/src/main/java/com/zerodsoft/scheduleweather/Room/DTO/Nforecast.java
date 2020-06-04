package com.zerodsoft.scheduleweather.Room.DTO;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "nforecast_table")
public class Nforecast implements Serializable
{
    @PrimaryKey(autoGenerate = true)
    private int nForecastId;

    @ColumnInfo(name = "nforecast_parent_id")
    private int parentId;

    @ColumnInfo(name = "nforecast_date")
    private String date;

    @ColumnInfo(name = "nforecast_time")
    private String time;

    @ColumnInfo(name = "nforecast_chance_of_shower")
    private String chanceOfShower;

    @ColumnInfo(name = "nforecast_precipitation_form")
    private String precipitationForm;

    @ColumnInfo(name = "nforecast_six_hour_precipitation")
    private String sixHourPrecipitation;

    @ColumnInfo(name = "nforecast_humidity")
    private String humidity;

    @ColumnInfo(name = "nforecast_six_hour_fresh_snow_cover")
    private String sixHourFreshSnowCover;

    @ColumnInfo(name = "nforecast_sky")
    private String sky;

    @ColumnInfo(name = "nforecast_three_hour_temperature")
    private String threeHourTemp;

    @ColumnInfo(name = "nforecast_morning_min_temp")
    private String morningMinTemp;

    @ColumnInfo(name = "nforecast_day_max_temp")
    private String dayMaxTemp;

    @ColumnInfo(name = "nforecast_wind_direction")
    private String windDirection;

    @ColumnInfo(name = "nforecast_wind_speed")
    private String windSpeed;


    public int getNForecastId()
    {
        return nForecastId;
    }

    public void setNForecastId(int nForecastId)
    {
        this.nForecastId = nForecastId;
    }

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

    public String getChanceOfShower()
    {
        return chanceOfShower;
    }

    public void setChanceOfShower(String chanceOfShower)
    {
        this.chanceOfShower = chanceOfShower;
    }

    public String getPrecipitationForm()
    {
        return precipitationForm;
    }

    public void setPrecipitationForm(String precipitationForm)
    {
        this.precipitationForm = precipitationForm;
    }

    public String getSixHourPrecipitation()
    {
        return sixHourPrecipitation;
    }

    public void setSixHourPrecipitation(String sixHourPrecipitation)
    {
        this.sixHourPrecipitation = sixHourPrecipitation;
    }

    public String getHumidity()
    {
        return humidity;
    }

    public void setHumidity(String humidity)
    {
        this.humidity = humidity;
    }

    public String getSixHourFreshSnowCover()
    {
        return sixHourFreshSnowCover;
    }

    public void setSixHourFreshSnowCover(String sixHourFreshSnowCover)
    {
        this.sixHourFreshSnowCover = sixHourFreshSnowCover;
    }

    public String getSky()
    {
        return sky;
    }

    public void setSky(String sky)
    {
        this.sky = sky;
    }

    public String getThreeHourTemp()
    {
        return threeHourTemp;
    }

    public void setThreeHourTemp(String threeHourTemp)
    {
        this.threeHourTemp = threeHourTemp;
    }

    public String getMorningMinTemp()
    {
        return morningMinTemp;
    }

    public void setMorningMinTemp(String morningMinTemp)
    {
        this.morningMinTemp = morningMinTemp;
    }

    public String getDayMaxTemp()
    {
        return dayMaxTemp;
    }

    public void setDayMaxTemp(String dayMaxTemp)
    {
        this.dayMaxTemp = dayMaxTemp;
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
