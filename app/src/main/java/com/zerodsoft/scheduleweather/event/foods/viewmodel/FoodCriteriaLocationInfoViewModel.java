package com.zerodsoft.scheduleweather.event.foods.viewmodel;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.zerodsoft.scheduleweather.event.foods.interfaces.FoodCriteriaLocationInfoQuery;
import com.zerodsoft.scheduleweather.event.foods.repository.FoodCriteriaLocationInfoRepository;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;

public class FoodCriteriaLocationInfoViewModel extends AndroidViewModel implements FoodCriteriaLocationInfoQuery
{
    private FoodCriteriaLocationInfoRepository repository;

    public FoodCriteriaLocationInfoViewModel(@NonNull Application application)
    {
        super(application);
        repository = new FoodCriteriaLocationInfoRepository(application);
    }

    @Override
    public void selectByEventId(int calendarId, long eventId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback)
    {
        repository.selectByEventId(calendarId, eventId, callback);
    }

    @Override
    public void selectByInstanceId(int calendarId, long instanceId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback)
    {
        repository.selectByInstanceId(calendarId, instanceId, callback);
    }

    @Override
    public void insertByEventId(int calendarId, long eventId, int usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback)
    {
        repository.insertByEventId(calendarId, eventId, usingType, historyLocationId, callback);
    }

    @Override
    public void insertByInstanceId(int calendarId, long instanceId, int usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback)
    {
        repository.insertByInstanceId(calendarId, instanceId, usingType, historyLocationId, callback);
    }

    @Override
    public void updateByEventId(int calendarId, long eventId, int usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback)
    {
        repository.updateByEventId(calendarId, eventId, usingType, historyLocationId, callback);
    }

    @Override
    public void updateByInstanceId(int calendarId, long instanceId, int usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback)
    {
        repository.updateByInstanceId(calendarId, instanceId, usingType, historyLocationId, callback);
    }

    @Override
    public void deleteByEventId(int calendarId, long eventId, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        repository.deleteByEventId(calendarId, eventId, callback);
    }

    @Override
    public void deleteByInstanceId(int calendarId, long instanceId, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        repository.deleteByInstanceId(calendarId, instanceId, callback);
    }
}
