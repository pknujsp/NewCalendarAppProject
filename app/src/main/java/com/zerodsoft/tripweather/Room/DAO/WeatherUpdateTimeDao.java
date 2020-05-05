package com.zerodsoft.tripweather.Room.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.zerodsoft.tripweather.Room.DTO.WeatherUpdateTime;

@Dao
public interface WeatherUpdateTimeDao
{
    @Query("SELECT * FROM weather_update_time_table WHERE update_time_travel_parent_id = :parentId")
    WeatherUpdateTime getTravelUpdateInfo(int parentId);

    @Query("SELECT * FROM weather_update_time_table WHERE update_time_cweather_parent_id = :parentId")
    WeatherUpdateTime getCWeatherUpdateInfo(int parentId);

    @Insert
    long insertUpdateData(WeatherUpdateTime weatherUpdateTime);

    @Query("DELETE FROM weather_update_time_table WHERE update_time_travel_parent_id =:parentId")
    void deleteTravelUpdateData(int parentId);

    @Query("DELETE FROM weather_update_time_table WHERE update_time_cweather_parent_id =:parentId")
    void deleteCWeatherUpdateData(int parentId);

    @Query("UPDATE weather_update_time_table SET update_time_nforecast_date = :baseDate, update_time_nforecast_time = :baseTime, "
            + "updated_date = :updatedDate, updated_time = :updatedTime WHERE update_time_travel_parent_id = :travelId")
    void updateUpdateInfo(String baseDate, String baseTime, String updatedDate, String updatedTime, int travelId);
}
