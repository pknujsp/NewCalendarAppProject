package com.zerodsoft.scheduleweather.event.foods.interfaces;

import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;

public interface FoodCriteriaLocationInfoQuery
{
    void selectByEventId(Integer calendarId, Long eventId, DbQueryCallback<FoodCriteriaLocationInfoDTO> callback);

    void selectByInstanceId(Integer calendarId, Long instanceId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback);

    void insertByEventId(Integer calendarId, Long eventId, Integer usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback);

    void insertByInstanceId(Integer calendarId, Long instanceId, Integer usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback);

    void updateByEventId(Integer calendarId, Long eventId, Integer usingType, Integer historyLocationId, DbQueryCallback<FoodCriteriaLocationInfoDTO> callback);

    void updateByInstanceId(Integer calendarId, Long instanceId, Integer usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback);

    void deleteByEventId(Integer calendarId, Long eventId, CarrierMessagingService.ResultCallback<Boolean> callback);

    void deleteByInstanceId(Integer calendarId, Long instanceId, CarrierMessagingService.ResultCallback<Boolean> callback);
}
