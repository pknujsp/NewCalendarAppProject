package com.zerodsoft.tripweather.Room.DTO;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

public class ScheduleNForecast
{
    @PrimaryKey(autoGenerate = true)
    private int nForecastId;

    @ColumnInfo(name = "nforecast_parent_id")
    private int scheduleId;

    @ColumnInfo(name = "schedule_parent_id")
    private int travelId;

    @ColumnInfo(name = "schedule_area_name")
    private String areaName;

    @ColumnInfo(name = "schedule_area_id")
    private int areaId;

    @ColumnInfo(name = "schedule_area_x")
    private String areaX;

    @ColumnInfo(name = "schedule_area_y")
    private String areaY;

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

    public int getScheduleId()
    {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId)
    {
        this.scheduleId = scheduleId;
    }

    public int getTravelId()
    {
        return travelId;
    }

    public void setTravelId(int travelId)
    {
        this.travelId = travelId;
    }

    public String getAreaName()
    {
        return areaName;
    }

    public void setAreaName(String areaName)
    {
        this.areaName = areaName;
    }

    public int getAreaId()
    {
        return areaId;
    }

    public void setAreaId(int areaId)
    {
        this.areaId = areaId;
    }

    public String getAreaX()
    {
        return areaX;
    }

    public void setAreaX(String areaX)
    {
        this.areaX = areaX;
    }

    public String getAreaY()
    {
        return areaY;
    }

    public void setAreaY(String areaY)
    {
        this.areaY = areaY;
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
