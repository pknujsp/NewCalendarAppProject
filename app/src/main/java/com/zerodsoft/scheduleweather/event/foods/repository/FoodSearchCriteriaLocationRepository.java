package com.zerodsoft.scheduleweather.event.foods.repository;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.event.foods.interfaces.FoodSearchCriteriaLocationQuery;
import com.zerodsoft.scheduleweather.room.AppDb;

import java.util.List;

import lombok.SneakyThrows;

public class FoodSearchCriteriaLocationRepository implements FoodSearchCriteriaLocationQuery
{
    private FoodCriteriaLocationDAO dao;

    public FoodSearchCriteriaLocationRepository(Application application)
    {
        dao = AppDb.getInstance(application.getApplicationContext()).foodSearchCriteriaLocationDAO();
    }

    @Override
    public void selectByEventId(int calendarId, long eventId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                List<FoodCriteriaLocationDTO> list = dao.selectByEventId(calendarId, eventId);
                callback.onReceiveResult(list);
            }
        });
    }

    @Override
    public void selectByInstanceId(int calendarId, long instanceId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                List<FoodCriteriaLocationDTO> list = dao.selectByInstanceId(calendarId, instanceId);
                callback.onReceiveResult(list);
            }
        });
    }

    @Override
    public void insertByEventId(int calendarId, long eventId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, CarrierMessagingService.ResultCallback<FoodCriteriaLocationDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                dao.insertByEventId(calendarId, eventId, placeName, addressName, roadAddressName, latitude, longitude);
                List<FoodCriteriaLocationDTO> list = dao.selectByEventId(calendarId, eventId);
                callback.onReceiveResult(list);
            }
        });
    }

    @Override
    public void insertByInstanceId(int calendarId, long instanceId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, CarrierMessagingService.ResultCallback<FoodCriteriaLocationDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                dao.insertByInstanceId(calendarId, instanceId, placeName, addressName, roadAddressName, latitude, longitude);
                List<FoodCriteriaLocationDTO> list = dao.selectByInstanceId(calendarId, instanceId);
                callback.onReceiveResult(list);
            }
        });
    }

    @Override
    public void updateByEventId(int calendarId, long eventId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, CarrierMessagingService.ResultCallback<FoodCriteriaLocationDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                dao.updateByEventId(calendarId, eventId, placeName, addressName, roadAddressName, latitude, longitude);
                List<FoodCriteriaLocationDTO> list = dao.selectByEventId(calendarId, eventId);
                callback.onReceiveResult(list);
            }
        });
    }

    @Override
    public void updateByInstanceId(int calendarId, long instanceId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, CarrierMessagingService.ResultCallback<FoodCriteriaLocationDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                dao.updateByInstanceId(calendarId, instanceId, placeName, addressName, roadAddressName, latitude, longitude);
                List<FoodCriteriaLocationDTO> list = dao.selectByInstanceId(calendarId, instanceId);
                callback.onReceiveResult(list);
            }
        });
    }

    @Override
    public void deleteByEventId(int calendarId, long eventId, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                dao.deleteByEventId(calendarId, eventId);
                callback.onReceiveResult(true);
            }
        });
    }

    @Override
    public void deleteByInstanceId(int calendarId, long instanceId, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                dao.deleteByInstanceId(calendarId, instanceId);
                callback.onReceiveResult(true);
            }
        });
    }
}
