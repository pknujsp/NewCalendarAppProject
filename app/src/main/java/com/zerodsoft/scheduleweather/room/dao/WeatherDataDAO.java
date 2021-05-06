package com.zerodsoft.scheduleweather.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;

import java.util.List;

@Dao
public interface WeatherDataDAO
{
    @Insert(entity = WeatherDataDTO.class, onConflict = OnConflictStrategy.IGNORE)
    long insert(WeatherDataDTO weatherDataDTO);

    @Query("SELECT * FROM weather_data_table ")
    List<WeatherDataDTO> select(String latitude,String longitude);
}
