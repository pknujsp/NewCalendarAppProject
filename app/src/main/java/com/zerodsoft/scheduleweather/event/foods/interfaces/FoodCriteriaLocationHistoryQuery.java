package com.zerodsoft.scheduleweather.event.foods.interfaces;

import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationSearchHistoryDTO;

import java.util.List;

public interface FoodCriteriaLocationHistoryQuery
{
    void selectByEventId(int calendarId, long eventId, CarrierMessagingService.ResultCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback);

    void selectByInstanceId(int calendarId, long instanceId, CarrierMessagingService.ResultCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback);

    void select(int id, CarrierMessagingService.ResultCallback<FoodCriteriaLocationSearchHistoryDTO> callback);

    void selectAll(CarrierMessagingService.ResultCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback);

    void insertByEventId(int calendarId, long eventId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, CarrierMessagingService.ResultCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback);

    void insertByInstanceId(int calendarId, long instanceId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, CarrierMessagingService.ResultCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback);

    void updateByEventId(int calendarId, long eventId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, CarrierMessagingService.ResultCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback);

    void updateByInstanceId(int calendarId, long instanceId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, CarrierMessagingService.ResultCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback);

    void update(int id, String placeName, String addressName, String roadAddressName, String latitude, String longitude, CarrierMessagingService.ResultCallback<FoodCriteriaLocationSearchHistoryDTO> callback);

    void deleteByEventId(int calendarId, long eventId, CarrierMessagingService.ResultCallback<Boolean> callback);

    void deleteByInstanceId(int calendarId, long instanceId, CarrierMessagingService.ResultCallback<Boolean> callback);

    void delete(int id, CarrierMessagingService.ResultCallback<Boolean> callback);

    void deleteAll(CarrierMessagingService.ResultCallback<Boolean> callback);

    void containsData(int id, CarrierMessagingService.ResultCallback<Boolean> callback);
}
