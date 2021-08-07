package com.zerodsoft.scheduleweather.event.foods.interfaces;

import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationSearchHistoryDTO;

import java.util.List;

public interface FoodCriteriaLocationHistoryQuery {
	void selectByEventId(long eventId, DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback);

	void select(int id, DbQueryCallback<FoodCriteriaLocationSearchHistoryDTO> callback);

	void selectAll(DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback);

	void insertByEventId(long eventId, String placeName, String addressName, String latitude, String longitude, Integer locationType, DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback);

	void updateByEventId(long eventId, String placeName, String addressName, String latitude, String longitude, Integer locationType, DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback);

	void update(int id, String placeName, String addressName, String latitude, String longitude, Integer locationType, DbQueryCallback<FoodCriteriaLocationSearchHistoryDTO> callback);

	void deleteByEventId(long eventId, DbQueryCallback<Boolean> callback);

	void delete(int id, DbQueryCallback<Boolean> callback);

	void deleteAll(DbQueryCallback<Boolean> callback);

	void containsData(int id, DbQueryCallback<Boolean> callback);
}
