package com.zerodsoft.scheduleweather.Room.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.zerodsoft.scheduleweather.Room.DTO.Travel;
import com.zerodsoft.scheduleweather.Room.DTO.TravelScheduleCountTuple;

import java.util.List;

@Dao
public interface TravelDao
{
    @Query("SELECT * FROM travel_table")
    List<Travel> getAllTravels();

    @Query("SELECT travel_id, count(travel_id) FROM travel_table")
    TravelScheduleCountTuple getTravelCount();

    @Query("SELECT * FROM travel_table WHERE travel_id = :travelId")
    Travel getTravelInfo(int travelId);

    @Insert
    long insertTravel(Travel travel);

    @Query("DELETE FROM travel_table")
    void deleteAllTravels();

    @Query("DELETE FROM travel_table WHERE travel_id = :travelId")
    int deleteTravel(int travelId);

    @Query("UPDATE travel_table SET travel_name = :travelName WHERE travel_id = :travelId")
    void updateTravelName(String travelName, int travelId);

}
