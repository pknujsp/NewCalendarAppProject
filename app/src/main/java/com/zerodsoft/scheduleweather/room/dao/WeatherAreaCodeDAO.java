package com.zerodsoft.scheduleweather.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;

import java.util.List;

@Dao
public interface WeatherAreaCodeDAO
{
    @Query("SELECT * FROM weather_area_code_table WHERE latitude_hours = :lat_degree AND latitude_minutes = :lat_minutes AND longitude_hours = :lon_degree AND longitude_minutes = :lon_minutes")
    LiveData<List<WeatherAreaCodeDTO>> selectAreaCode(String lat_degree, String lat_minutes, String lon_degree, String lon_minutes);
}
