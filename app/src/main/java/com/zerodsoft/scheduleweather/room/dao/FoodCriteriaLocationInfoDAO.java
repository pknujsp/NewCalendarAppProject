package com.zerodsoft.scheduleweather.room.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;

@Dao
public interface FoodCriteriaLocationInfoDAO {
	@Query("SELECT * FROM food_criteria_location_info_table WHERE event_id = :eventId")
	FoodCriteriaLocationInfoDTO selectByEventId(Long eventId);

	@Query("SELECT * FROM food_criteria_location_info_table WHERE instance_id = :instanceId")
	FoodCriteriaLocationInfoDTO selectByInstanceId(Long instanceId);

	@Query("INSERT INTO food_criteria_location_info_table (event_id, using_type, history_location_id) " +
			"VALUES (:eventId, :usingType, :historyLocationId)")
	void insertByEventId(Long eventId, Integer usingType, Integer historyLocationId);

	@Query("INSERT INTO food_criteria_location_info_table (instance_id, using_type, history_location_id) " +
			"VALUES ( :instanceId, :usingType, :historyLocationId)")
	void insertByInstanceId(Long instanceId, Integer usingType, Integer historyLocationId);


	@Query("UPDATE food_criteria_location_info_table SET using_type = :usingType, history_location_id = :historyLocationId WHERE event_id" +
			" = :eventId")
	void updateByEventId(Long eventId, Integer usingType, Integer historyLocationId);

	@Query("UPDATE food_criteria_location_info_table SET using_type = :usingType, history_location_id = :historyLocationId WHERE instance_id = :instanceId")
	void updateByInstanceId(Long instanceId, Integer usingType, Integer historyLocationId);


	@Query("DELETE FROM food_criteria_location_info_table WHERE event_id = :eventId")
	void deleteByEventId(Long eventId);

	@Query("DELETE FROM food_criteria_location_info_table WHERE instance_id = :instanceId")
	void deleteByInstanceId(Long instanceId);

	@Query("SELECT * FROM food_criteria_location_info_table WHERE event_id = :eventId")
	FoodCriteriaLocationInfoDTO contains(Long eventId);
}
