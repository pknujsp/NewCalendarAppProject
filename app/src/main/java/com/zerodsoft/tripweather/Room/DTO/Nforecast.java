package com.zerodsoft.tripweather.Room.DTO;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.io.Serializable;

@Entity(tableName = "nforecast_table")
public class Nforecast implements Serializable
{
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

    @ColumnInfo(name = "nforecast_thunder")
    private String thunder;
}
