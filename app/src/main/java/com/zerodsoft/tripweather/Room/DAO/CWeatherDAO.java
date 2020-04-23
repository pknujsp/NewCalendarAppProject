package com.zerodsoft.tripweather.Room.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.zerodsoft.tripweather.Room.DTO.CWeather;

@Dao
public interface CWeatherDAO
{
    @Query("SELECT * FROM cweather_table WHERE cweather_parent_id = :parentId")
    CWeather getCurrentWeather(String parentId);

    @Insert(entity = CWeather.class)
    void insertData(CWeather cWeather);

    @Update
    void updateData(CWeather cWeather);

    @Delete
    void deleteData(CWeather cWeather);
}
