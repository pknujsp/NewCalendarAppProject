package com.zerodsoft.tripweather.Room.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.zerodsoft.tripweather.Room.DTO.UstForecast;

import java.util.List;

@Dao
public interface UstForecastDao
{
    @Query("SELECT * FROM ustforecast_table WHERE ustforecast_parent_id = :parentId")
    List<UstForecast> getUstForecastData(int parentId);

    @Insert
    void insertUstForecastData(UstForecast ustForecast);

    @Delete
    void deleteUstForecastData(UstForecast ustForecast);

    @Update
    void updateUstForecastData(UstForecast ustForecast);

    @Query("DELETE FROM ustforecast_table WHERE ustforecast_parent_id = :parentId")
    void deleteUstForecastData(int parentId);

    @Query("DELETE FROM ustforecast_table WHERE ustforecast_parent_id = :parentId AND (ustforecast_date < :date OR ustforecast_time < :time)")
    void deleteUstForecastData(int parentId, int date, int time);
}
