package com.zerodsoft.scheduleweather.room.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationSearchHistoryDTO;

import java.util.List;

@Dao
public interface FoodCriteriaLocationSearchHistoryDAO {
	@Query("SELECT * FROM food_criteria_location_search_history_table WHERE event_id = :eventId")
	List<FoodCriteriaLocationSearchHistoryDTO> selectByEventId(Long eventId);

	@Query("SELECT * FROM food_criteria_location_search_history_table WHERE id = :id")
	FoodCriteriaLocationSearchHistoryDTO select(Integer id);

	@Query("SELECT * FROM food_criteria_location_search_history_table")
	List<FoodCriteriaLocationSearchHistoryDTO> selectAll();

	@Query("INSERT INTO food_criteria_location_search_history_table (event_id, place_name, address_name, latitude, longitude, location_type) " +
			"VALUES (:eventId, :placeName, :addressName, :latitude, :longitude, :locationType)")
	void insertByEventId(Long eventId, String placeName, String addressName, String latitude, String longitude, Integer locationType);

	@Query("UPDATE food_criteria_location_search_history_table " +
			"SET place_name = :placeName , address_name = :addressName , latitude = :latitude , longitude = :longitude, location_type = :locationType " +
			"WHERE event_id = :eventId")
	void updateByEventId(Long eventId, String placeName, String addressName, String latitude, String longitude, Integer locationType);

	@Query("UPDATE food_criteria_location_search_history_table " +
			"SET place_name = :placeName , address_name = :addressName , latitude = :latitude , longitude = :longitude, location_type = :locationType " +
			"WHERE id = :id")
	void update(int id, String placeName, String addressName, String latitude, String longitude, Integer locationType);

	@Query("DELETE FROM food_criteria_location_search_history_table WHERE event_id = :eventId")
	void deleteByEventId(Long eventId);


	@Query("DELETE FROM food_criteria_location_search_history_table WHERE id = :id")
	void delete(int id);

	@Query("DELETE FROM food_criteria_location_search_history_table")
	void deleteAll();

	@Query("SELECT EXISTS (SELECT * FROM food_criteria_location_search_history_table WHERE id = :id) AS SUCCESS")
	int containsData(int id);
}
