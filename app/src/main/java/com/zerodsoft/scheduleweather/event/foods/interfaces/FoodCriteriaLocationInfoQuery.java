package com.zerodsoft.scheduleweather.event.foods.interfaces;

import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;

public interface FoodCriteriaLocationInfoQuery {
	void selectByEventId(Long eventId, DbQueryCallback<FoodCriteriaLocationInfoDTO> callback);

	void selectByInstanceId(Long instanceId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback);

	void insertByEventId(Long eventId, Integer usingType, Integer historyLocationId, DbQueryCallback<FoodCriteriaLocationInfoDTO> callback);

	void insertByInstanceId(Long instanceId, Integer usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback);

	void updateByEventId(Long eventId, Integer usingType, Integer historyLocationId, DbQueryCallback<FoodCriteriaLocationInfoDTO> callback);

	void updateByInstanceId(Long instanceId, Integer usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback);

	void deleteByEventId(Long eventId, CarrierMessagingService.ResultCallback<Boolean> callback);

	void deleteByInstanceId(Long instanceId, CarrierMessagingService.ResultCallback<Boolean> callback);

	void contains(Long eventId, DbQueryCallback<FoodCriteriaLocationInfoDTO> callback);
}
