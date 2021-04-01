package com.zerodsoft.scheduleweather.event.foods.viewmodel;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.zerodsoft.scheduleweather.event.foods.interfaces.FoodSearchCriteriaLocationQuery;
import com.zerodsoft.scheduleweather.event.foods.repository.FoodSearchCriteriaLocationRepository;

public class FoodSearchCriteriaLocationViewModel extends AndroidViewModel implements FoodSearchCriteriaLocationQuery
{
    private FoodSearchCriteriaLocationRepository repository;

    public FoodSearchCriteriaLocationViewModel(@NonNull Application application)
    {
        super(application);
        repository = new FoodSearchCriteriaLocationRepository(application);
    }

    @Override
    public void selectByEventId(int calendarId, long eventId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationDTO> callback)
    {
        repository.selectByEventId(calendarId, eventId, callback);
    }

    @Override
    public void selectByInstanceId(int calendarId, long instanceId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationDTO> callback)
    {
        repository.selectByInstanceId(calendarId, instanceId, callback);
    }

    @Override
    public void insertByEventId(int calendarId, long eventId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, CarrierMessagingService.ResultCallback<FoodCriteriaLocationDTO> callback)
    {
        repository.insertByEventId(calendarId, eventId, placeName, addressName, roadAddressName, latitude, longitude, callback);
    }

    @Override
    public void insertByInstanceId(int calendarId, long instanceId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, CarrierMessagingService.ResultCallback<FoodCriteriaLocationDTO> callback)
    {
        repository.insertByInstanceId(calendarId, instanceId, placeName, addressName, roadAddressName, latitude, longitude, callback);
    }

    @Override
    public void updateByEventId(int calendarId, long eventId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, CarrierMessagingService.ResultCallback<FoodCriteriaLocationDTO> callback)
    {
        repository.updateByEventId(calendarId, eventId, placeName, addressName, roadAddressName, latitude, longitude, callback);
    }

    @Override
    public void updateByInstanceId(int calendarId, long instanceId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, CarrierMessagingService.ResultCallback<FoodCriteriaLocationDTO> callback)
    {
        repository.updateByInstanceId(calendarId, instanceId, placeName, addressName, roadAddressName, latitude, longitude, callback);
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
