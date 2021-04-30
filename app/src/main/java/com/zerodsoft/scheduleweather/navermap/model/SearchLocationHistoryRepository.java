package com.zerodsoft.scheduleweather.navermap.model;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

import androidx.annotation.NonNull;

import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.navermap.interfaces.SearchHistoryQuery;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.SearchHistoryDAO;
import com.zerodsoft.scheduleweather.room.dto.SearchHistoryDTO;

import java.util.List;

import lombok.SneakyThrows;

public class SearchLocationHistoryRepository implements SearchHistoryQuery
{
    private SearchHistoryDAO dao;

    public SearchLocationHistoryRepository(@NonNull Application application)
    {
        dao = AppDb.getInstance(application.getApplicationContext()).searchHistoryDAO();
    }


    @Override
    public void insert(Integer type, String value, CarrierMessagingService.ResultCallback<SearchHistoryDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                dao.insert(type, value);
                SearchHistoryDTO searchHistoryDTO = dao.select(type, value);
                callback.onReceiveResult(searchHistoryDTO);
            }
        });
    }

    @Override
    public void select(Integer type, CarrierMessagingService.ResultCallback<List<SearchHistoryDTO>> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                List<SearchHistoryDTO> list = dao.select(type);
                callback.onReceiveResult(list);
            }
        });
    }

    @Override
    public void select(Integer type, String value, CarrierMessagingService.ResultCallback<SearchHistoryDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                SearchHistoryDTO searchHistoryDTO = dao.select(type, value);
                callback.onReceiveResult(searchHistoryDTO);
            }
        });
    }

    @Override
    public void delete(int id, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                dao.delete(id);
                callback.onReceiveResult(true);
            }
        });
    }

    @Override
    public void delete(Integer type, String value, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                dao.delete(type, value);
                callback.onReceiveResult(true);
            }
        });
    }

    @Override
    public void deleteAll(Integer type, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                dao.delete(type);
                callback.onReceiveResult(true);
            }
        });
    }

    @Override
    public void contains(Integer type, String value, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                int result = dao.contains(type, value);
                callback.onReceiveResult(result == 1);
            }
        });
    }
}
