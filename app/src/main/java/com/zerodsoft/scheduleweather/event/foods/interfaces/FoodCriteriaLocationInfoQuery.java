package com.zerodsoft.scheduleweather.event.foods.interfaces;

import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;

public interface FoodCriteriaLocationInfoQuery
{
    void selectByEventId(int calendarId, long eventId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback);

    void selectByInstanceId(int calendarId, long instanceId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback);

    void insertByEventId(int calendarId, long eventId, int usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback);

    void insertByInstanceId(int calendarId, long instanceId, int usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback);

    void updateByEventId(int calendarId, long eventId, int usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback);

    void updateByInstanceId(int calendarId, long instanceId, int usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback);

    void deleteByEventId(int calendarId, long eventId, CarrierMessagingService.ResultCallback<Boolean> callback);

    void deleteByInstanceId(int calendarId, long instanceId, CarrierMessagingService.ResultCallback<Boolean> callback);
}
