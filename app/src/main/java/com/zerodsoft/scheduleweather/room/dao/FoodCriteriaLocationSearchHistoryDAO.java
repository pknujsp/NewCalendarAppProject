package com.zerodsoft.scheduleweather.room.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationSearchHistoryDTO;

import java.util.List;

@Dao
public interface FoodCriteriaLocationSearchHistoryDAO
{
    @Query("SELECT * FROM food_criteria_location_search_history_table WHERE calendar_id = :calendarId AND event_id = :eventId")
    List<FoodCriteriaLocationSearchHistoryDTO> selectByEventId(int calendarId, long eventId);

    @Query("SELECT * FROM food_criteria_location_search_history_table WHERE calendar_id = :calendarId AND instance_id = :instanceId")
    List<FoodCriteriaLocationSearchHistoryDTO> selectByInstanceId(int calendarId, long instanceId);

    @Query("SELECT * FROM food_criteria_location_search_history_table WHERE id = :id")
    FoodCriteriaLocationSearchHistoryDTO select(int id);

    @Query("SELECT * FROM food_criteria_location_search_history_table")
    List<FoodCriteriaLocationSearchHistoryDTO> selectAll();

    @Query("INSERT INTO food_criteria_location_search_history_table (calendar_id, event_id, place_name, address_name, road_address_name, latitude, longitude) " +
            "VALUES (:calendarId, :eventId, :placeName, :addressName, :roadAddressName, :latitude, :longitude)")
    void insertByEventId(int calendarId, long eventId, String placeName, String addressName, String roadAddressName, String latitude, String longitude);

    @Query("INSERT INTO food_criteria_location_search_history_table (calendar_id, instance_id, place_name, address_name, road_address_name, latitude, longitude) " +
            "VALUES (:calendarId, :instanceId, :placeName, :addressName, :roadAddressName, :latitude, :longitude)")
    void insertByInstanceId(int calendarId, long instanceId, String placeName, String addressName, String roadAddressName, String latitude, String longitude);

    @Query("UPDATE food_criteria_location_search_history_table " +
            "SET place_name = :placeName AND address_name = :addressName AND road_address_name = :roadAddressName AND latitude = :latitude AND longitude = :longitude " +
            "WHERE calendar_id = :calendarId AND event_id = :eventId")
    void updateByEventId(int calendarId, long eventId, String placeName, String addressName, String roadAddressName, String latitude, String longitude);

    @Query("UPDATE food_criteria_location_search_history_table " +
            "SET place_name = :placeName AND address_name = :addressName AND road_address_name = :roadAddressName AND latitude = :latitude AND longitude = :longitude " +
            "WHERE calendar_id = :calendarId AND instance_id = :instanceId")
    void updateByInstanceId(int calendarId, long instanceId, String placeName, String addressName, String roadAddressName, String latitude, String longitude);

    @Query("UPDATE food_criteria_location_search_history_table " +
            "SET place_name = :placeName AND address_name = :addressName AND road_address_name = :roadAddressName AND latitude = :latitude AND longitude = :longitude " +
            "WHERE id = :id")
    void update(int id, String placeName, String addressName, String roadAddressName, String latitude, String longitude);

    @Query("DELETE FROM food_criteria_location_search_history_table WHERE calendar_id = :calendarId AND event_id = :eventId")
    void deleteByEventId(int calendarId, long eventId);

    @Query("DELETE FROM food_criteria_location_search_history_table WHERE calendar_id = :calendarId AND instance_id = :instanceId")
    void deleteByInstanceId(int calendarId, long instanceId);

    @Query("DELETE FROM food_criteria_location_search_history_table WHERE id = :id")
    void delete(int id);

    @Query("DELETE FROM food_criteria_location_search_history_table")
    void deleteAll();

    @Query("SELECT EXISTS (SELECT * FROM food_criteria_location_search_history_table WHERE id = :id) AS SUCCESS")
    int containsData(int id);
}
