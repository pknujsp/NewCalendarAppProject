package com.zerodsoft.scheduleweather.room.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationSearchHistoryDTO;

import java.util.List;

@Dao
public interface FoodCriteriaLocationSearchHistoryDAO
{
    @Query("SELECT * FROM food_criteria_location_search_history_table WHERE calendar_id = :calendarId AND event_id = :eventId")
    List<FoodCriteriaLocationSearchHistoryDTO> selectByEventId(Integer calendarId, Long eventId);

    @Query("SELECT * FROM food_criteria_location_search_history_table WHERE calendar_id = :calendarId AND instance_id = :instanceId")
    List<FoodCriteriaLocationSearchHistoryDTO> selectByInstanceId(Integer calendarId, Long instanceId);

    @Query("SELECT * FROM food_criteria_location_search_history_table WHERE id = :id")
    FoodCriteriaLocationSearchHistoryDTO select(Integer id);

    @Query("SELECT * FROM food_criteria_location_search_history_table")
    List<FoodCriteriaLocationSearchHistoryDTO> selectAll();

    @Query("INSERT INTO food_criteria_location_search_history_table (calendar_id, event_id, place_name, address_name, road_address_name, latitude, longitude, location_type) " +
            "VALUES (:calendarId, :eventId, :placeName, :addressName, :roadAddressName, :latitude, :longitude, :locationType)")
    void insertByEventId(Integer calendarId, Long eventId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, Integer locationType);

    @Query("INSERT INTO food_criteria_location_search_history_table (calendar_id, instance_id, place_name, address_name, road_address_name, latitude, longitude, location_type) " +
            "VALUES (:calendarId, :instanceId, :placeName, :addressName, :roadAddressName, :latitude, :longitude, :locationType)")
    void insertByInstanceId(Integer calendarId, Long instanceId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, Integer locationType);

    @Query("UPDATE food_criteria_location_search_history_table " +
            "SET place_name = :placeName , address_name = :addressName , road_address_name = :roadAddressName , latitude = :latitude , longitude = :longitude, location_type = :locationType " +
            "WHERE calendar_id = :calendarId AND event_id = :eventId")
    void updateByEventId(Integer calendarId, Long eventId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, Integer locationType);

    @Query("UPDATE food_criteria_location_search_history_table " +
            "SET place_name = :placeName , address_name = :addressName , road_address_name = :roadAddressName , latitude = :latitude , longitude = :longitude, location_type = :locationType " +
            "WHERE calendar_id = :calendarId AND instance_id = :instanceId")
    void updateByInstanceId(Integer calendarId, Long instanceId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, Integer locationType);

    @Query("UPDATE food_criteria_location_search_history_table " +
            "SET place_name = :placeName , address_name = :addressName , road_address_name = :roadAddressName , latitude = :latitude , longitude = :longitude, location_type = :locationType " +
            "WHERE id = :id")
    void update(int id, String placeName, String addressName, String roadAddressName, String latitude, String longitude, Integer locationType);

    @Query("DELETE FROM food_criteria_location_search_history_table WHERE calendar_id = :calendarId AND event_id = :eventId")
    void deleteByEventId(Integer calendarId, Long eventId);

    @Query("DELETE FROM food_criteria_location_search_history_table WHERE calendar_id = :calendarId AND instance_id = :instanceId")
    void deleteByInstanceId(Integer calendarId, Long instanceId);

    @Query("DELETE FROM food_criteria_location_search_history_table WHERE id = :id")
    void delete(int id);

    @Query("DELETE FROM food_criteria_location_search_history_table")
    void deleteAll();

    @Query("SELECT EXISTS (SELECT * FROM food_criteria_location_search_history_table WHERE id = :id) AS SUCCESS")
    int containsData(int id);
}
