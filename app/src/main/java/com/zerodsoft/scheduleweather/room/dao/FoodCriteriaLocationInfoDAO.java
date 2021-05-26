package com.zerodsoft.scheduleweather.room.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;

@Dao
public interface FoodCriteriaLocationInfoDAO {
	@Query("SELECT * FROM food_criteria_location_info_table WHERE calendar_id = :calendarId AND event_id = :eventId")
	FoodCriteriaLocationInfoDTO selectByEventId(Integer calendarId, Long eventId);

	@Query("SELECT * FROM food_criteria_location_info_table WHERE calendar_id = :calendarId AND instance_id = :instanceId")
	FoodCriteriaLocationInfoDTO selectByInstanceId(Integer calendarId, Long instanceId);


	@Query("INSERT INTO food_criteria_location_info_table (calendar_id, event_id, using_type, history_location_id) " +
			"VALUES (:calendarId, :eventId, :usingType, :historyLocationId)")
	void insertByEventId(Integer calendarId, Long eventId, Integer usingType, Integer historyLocationId);

	@Query("INSERT INTO food_criteria_location_info_table (calendar_id, instance_id, using_type, history_location_id) " +
			"VALUES (:calendarId, :instanceId, :usingType, :historyLocationId)")
	void insertByInstanceId(Integer calendarId, Long instanceId, Integer usingType, Integer historyLocationId);


	@Query("UPDATE food_criteria_location_info_table SET using_type = :usingType, history_location_id = :historyLocationId WHERE calendar_id = :calendarId AND event_id = :eventId")
	void updateByEventId(Integer calendarId, Long eventId, Integer usingType, Integer historyLocationId);

	@Query("UPDATE food_criteria_location_info_table SET using_type = :usingType, history_location_id = :historyLocationId WHERE calendar_id = :calendarId AND instance_id = :instanceId")
	void updateByInstanceId(Integer calendarId, Long instanceId, Integer usingType, Integer historyLocationId);


	@Query("DELETE FROM food_criteria_location_info_table WHERE calendar_id = :calendarId AND event_id = :eventId")
	void deleteByEventId(Integer calendarId, Long eventId);

	@Query("DELETE FROM food_criteria_location_info_table WHERE calendar_id = :calendarId AND instance_id = :instanceId")
	void deleteByInstanceId(Integer calendarId, Long instanceId);

	@Query("SELECT * FROM food_criteria_location_info_table WHERE event_id = :eventId")
	FoodCriteriaLocationInfoDTO contains(Long eventId);
}
