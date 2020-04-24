package com.zerodsoft.tripweather.Room.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.zerodsoft.tripweather.Room.DTO.Nforecast;

import java.util.List;

@Dao
public interface NforecastDao
{
    @Query("SELECT * FROM nforecast_table WHERE nforecast_parent_id = :parentId")
    List<Nforecast> getNforecastData(int parentId);

    @Insert
    void insertNforecastData(Nforecast nforecast);

    @Delete
    void deleteNforecastData(Nforecast nforecast);

    @Update
    void updateNforecastData(Nforecast nforecast);

    @Query("DELETE FROM nforecast_table WHERE nforecast_parent_id = :parentId")
    void deleteNforecastData(int parentId);

    @Query("DELETE FROM nforecast_table WHERE nforecast_parent_id = :parentId AND (nforecast_date < :date OR nforecast_time < :time)")
    void deleteOldNforecastData(int parentId, int date, int time);
}
