package com.zerodsoft.calendarplatform.event.foods.interfaces;

import androidx.annotation.Nullable;

import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.room.dto.FoodCriteriaLocationInfoDTO;

public interface FoodCriteriaLocationInfoQuery {
	void selectByEventId(Long eventId, DbQueryCallback<FoodCriteriaLocationInfoDTO> callback);

	void insertByEventId(Long eventId, Integer usingType, Integer historyLocationId, @Nullable DbQueryCallback<FoodCriteriaLocationInfoDTO> callback);

	void updateByEventId(Long eventId, Integer usingType, Integer historyLocationId, @Nullable DbQueryCallback<FoodCriteriaLocationInfoDTO> callback);

	void deleteByEventId(Long eventId, @Nullable DbQueryCallback<Boolean> callback);

	void contains(Long eventId, DbQueryCallback<FoodCriteriaLocationInfoDTO> callback);

	void refresh(Long eventId);
}
