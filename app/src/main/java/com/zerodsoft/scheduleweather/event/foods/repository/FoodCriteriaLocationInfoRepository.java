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
    public void selectByEventId(Integer calendarId, Long eventId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback)
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
    public void selectByInstanceId(Integer calendarId, Long instanceId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback)
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
    public void insertByEventId(Integer calendarId, Long eventId, Integer usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                dao.insertByEventId(calendarId, eventId, usingType, historyLocationId);
                FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO = dao.selectByEventId(calendarId, eventId);
                callback.onReceiveResult(foodCriteriaLocationInfoDTO);
            }
        });
    }

    @Override
    public void insertByInstanceId(Integer calendarId, Long instanceId, Integer usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                dao.insertByInstanceId(calendarId, instanceId, usingType, historyLocationId);
                FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO = dao.selectByInstanceId(calendarId, instanceId);
                callback.onReceiveResult(foodCriteriaLocationInfoDTO);
            }
        });
    }

    @Override
    public void updateByEventId(Integer calendarId, Long eventId, Integer usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                dao.updateByEventId(calendarId, eventId, usingType, historyLocationId);
                FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO = dao.selectByEventId(calendarId, eventId);
                callback.onReceiveResult(foodCriteriaLocationInfoDTO);
            }
        });
    }

    @Override
    public void updateByInstanceId(Integer calendarId, Long instanceId, Integer usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                dao.updateByInstanceId(calendarId, instanceId, usingType, historyLocationId);
                FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO = dao.selectByInstanceId(calendarId, instanceId);
                callback.onReceiveResult(foodCriteriaLocationInfoDTO);
            }
        });
    }

    @Override
    public void deleteByEventId(Integer calendarId, Long eventId, CarrierMessagingService.ResultCallback<Boolean> callback)
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
    public void deleteByInstanceId(Integer calendarId, Long instanceId, CarrierMessagingService.ResultCallback<Boolean> callback)
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
