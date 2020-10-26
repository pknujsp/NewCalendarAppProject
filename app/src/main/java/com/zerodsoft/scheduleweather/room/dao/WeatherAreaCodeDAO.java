package com.zerodsoft.scheduleweather.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;

import java.util.List;

@Dao
public interface WeatherAreaCodeDAO
{
    @Query("SELECT * FROM weather_area_code_table WHERE x = :x AND y = :y")
    LiveData<List<WeatherAreaCodeDTO>> selectAreaCode(String x, String y);
}
