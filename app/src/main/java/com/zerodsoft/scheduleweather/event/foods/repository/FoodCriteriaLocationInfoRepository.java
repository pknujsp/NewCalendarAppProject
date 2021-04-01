package com.zerodsoft.scheduleweather.event.foods.repository;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.event.foods.interfaces.FoodCriteriaLocationInfoQuery;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.FoodCriteriaLocationInfoDAO;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;

import lombok.SneakyThrows;

public class FoodCriteriaLocationInfoRepository implements FoodCriteriaLocationInfoQuery
{
    private FoodCriteriaLocationInfoDAO dao;

    public FoodCriteriaLocationInfoRepository(Application application)
    {
        dao = AppDb.getInstance(application.getApplicationContext()).foodCriteriaLocationInfoDAO();
    }

    @Override
    public void selectByEventId(int calendarId, long eventId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO = dao.selectByEventId(calendarId, eventId);
                callback.onReceiveResult(foodCriteriaLocationInfoDTO);
            }
        });
    }

    @Override
    public void selectByInstanceId(int calendarId, long instanceId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO = dao.selectByInstanceId(calendarId, instanceId);
                callback.onReceiveResult(foodCriteriaLocationInfoDTO);
            }
        });
    }

    @Override
    public void insertByEventId(int calendarId, long eventId, int usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                dao.insertByEventId(calendarId, eventId, usingType);
                FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO = dao.selectByEventId(calendarId, eventId);
                callback.onReceiveResult(foodCriteriaLocationInfoDTO);
            }
        });
    }

    @Override
    public void insertByInstanceId(int calendarId, long instanceId, int usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                dao.insertByInstanceId(calendarId, instanceId, usingType);
                FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO = dao.selectByInstanceId(calendarId, instanceId);
                callback.onReceiveResult(foodCriteriaLocationInfoDTO);
            }
        });
    }

    @Override
    public void updateByEventId(int calendarId, long eventId, int usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                dao.updateByEventId(calendarId, eventId, usingType);
                FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO = dao.selectByEventId(calendarId, eventId);
                callback.onReceiveResult(foodCriteriaLocationInfoDTO);
            }
        });
    }

    @Override
    public void updateByInstanceId(int calendarId, long instanceId, int usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                dao.updateByInstanceId(calendarId, instanceId, usingType);
                FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO = dao.selectByInstanceId(calendarId, instanceId);
                callback.onReceiveResult(foodCriteriaLocationInfoDTO);
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
