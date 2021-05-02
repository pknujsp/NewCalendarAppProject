package com.zerodsoft.scheduleweather.event.foods.favorite.restaurant;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.FavoriteLocationDAO;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteLocationQuery;

import java.util.List;

import lombok.SneakyThrows;

class FavoriteLocationRepository implements FavoriteLocationQuery
{
    private final FavoriteLocationDAO favoriteLocationDAO;

    public FavoriteLocationRepository(Application application)
    {
        favoriteLocationDAO = AppDb.getInstance(application.getApplicationContext()).favoriteRestaurantDAO();
    }


    @Override
    public void insert(String locationId, String locationName, String latitude, String longitude, Integer type, CarrierMessagingService.ResultCallback<FavoriteLocationDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                favoriteLocationDAO.insert(locationId, locationName, latitude, longitude, type);
                FavoriteLocationDTO favoriteLocationDTO = favoriteLocationDAO.select(locationName, type);
                callback.onReceiveResult(favoriteLocationDTO);
            }
        });
    }

    @Override
    public void select(Integer type, CarrierMessagingService.ResultCallback<List<FavoriteLocationDTO>> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                List<FavoriteLocationDTO> list = favoriteLocationDAO.select(type);
                callback.onReceiveResult(list);
            }
        });
    }

    @Override
    public void select(String locationName, Integer type, CarrierMessagingService.ResultCallback<FavoriteLocationDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                callback.onReceiveResult(favoriteLocationDAO.select(locationName, type));
            }
        });
    }

    @Override
    public void delete(String locationName, Integer type, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                favoriteLocationDAO.delete(locationName, type);
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
                favoriteLocationDAO.deleteAll(type);
                callback.onReceiveResult(true);
            }
        });
    }


    @Override
    public void contains(Integer type, String locationName, String locationId, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                callback.onReceiveResult(favoriteLocationDAO.contains(type, locationName, locationId) == 1);
            }
        });
    }
}

