package com.zerodsoft.tripweather.Room.DAO;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import com.zerodsoft.tripweather.Room.DTO.CWeatherLocation;

import java.io.Serializable;
import java.util.List;

@Dao
public interface CWeatherLocationDao
{
    @Query("SELECT * FROM cweather_location_table")
    List<CWeatherLocation> getLocations();

    @Query("SELECT * FROM cweather_location_table WHERE location_id = :locationId")
    CWeatherLocation getLocation(int locationId);

    @Insert(entity = CWeatherLocation.class)
    void insertLocation(CWeatherLocation cWeatherLocation);

    @Delete
    void deleteLocation(CWeatherLocation cWeatherLocation);
}
