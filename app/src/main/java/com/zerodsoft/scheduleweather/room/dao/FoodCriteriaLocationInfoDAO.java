package com.zerodsoft.scheduleweather.room.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;

@Dao
public interface FoodCriteriaLocationInfoDAO
{
    @Query("SELECT * FROM food_criteria_location_info_table WHERE calendar_id = :calendarId AND event_id = :eventId")
    FoodCriteriaLocationInfoDTO selectByEventId(int calendarId, long eventId);

    @Query("SELECT * FROM food_criteria_location_info_table WHERE calendar_id = :calendarId AND instance_id = :instanceId")
    FoodCriteriaLocationInfoDTO selectByInstanceId(int calendarId, long instanceId);


    @Query("INSERT INTO food_criteria_location_info_table (calendar_id, event_id, using_type, history_location_id) " +
            "VALUES (:calendarId, :eventId, :usingType, :historyLocationId)")
    void insertByEventId(int calendarId, long eventId, int usingType, Integer historyLocationId);

    @Query("INSERT INTO food_criteria_location_info_table (calendar_id, instance_id, using_type, history_location_id) " +
            "VALUES (:calendarId, :instanceId, :usingType, :historyLocationId)")
    void insertByInstanceId(int calendarId, long instanceId, int usingType, Integer historyLocationId);


    @Query("UPDATE food_criteria_location_info_table SET using_type = :usingType AND history_location_id = :historyLocationId WHERE calendar_id = :calendarId AND event_id = :eventId")
    void updateByEventId(int calendarId, long eventId, int usingType, Integer historyLocationId);

    @Query("UPDATE food_criteria_location_info_table SET using_type = :usingType AND history_location_id = :historyLocationId WHERE calendar_id = :calendarId AND instance_id = :instanceId")
    void updateByInstanceId(int calendarId, long instanceId, int usingType, Integer historyLocationId);


    @Query("DELETE FROM food_criteria_location_info_table WHERE calendar_id = :calendarId AND event_id = :eventId")
    void deleteByEventId(int calendarId, long eventId);

    @Query("DELETE FROM food_criteria_location_info_table WHERE calendar_id = :calendarId AND instance_id = :instanceId")
    void deleteByInstanceId(int calendarId, long instanceId);
}
