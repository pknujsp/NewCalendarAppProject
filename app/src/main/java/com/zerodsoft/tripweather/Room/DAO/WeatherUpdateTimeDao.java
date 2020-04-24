package com.zerodsoft.tripweather.Room.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.zerodsoft.tripweather.Room.DTO.WeatherUpdateTime;

@Dao
public interface WeatherUpdateTimeDao
{
    @Query("SELECT * FROM weather_update_time_table WHERE update_time_travel_parent_id = :parentId")
    WeatherUpdateTime getTravelUpdateData(int parentId);

    @Query("SELECT * FROM weather_update_time_table WHERE update_time_cweather_parent_id = :parentId")
    WeatherUpdateTime getCWeatherUpdateData(int parentId);

    @Insert
    void insertUpdateData(WeatherUpdateTime weatherUpdateTime);

    @Query("DELETE FROM weather_update_time_table WHERE update_time_travel_parent_id =:parentId")
    void deleteTravelUpdateData(int parentId);

    @Query("DELETE FROM weather_update_time_table WHERE update_time_cweather_parent_id =:parentId")
    void deleteCWeatherUpdateData(int parentId);

    @Update
    void updateUpdateData(WeatherUpdateTime weatherUpdateTime);
}
