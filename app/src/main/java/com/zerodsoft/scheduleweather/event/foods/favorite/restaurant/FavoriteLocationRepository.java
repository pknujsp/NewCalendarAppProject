package com.zerodsoft.scheduleweather.event.foods.favorite.restaurant;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.FavoriteLocationDAO;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteLocationQuery;

import java.util.List;

import lombok.SneakyThrows;

class FavoriteLocationRepository implements FavoriteLocationQuery
{
    private final FavoriteLocationDAO dao;

    public FavoriteLocationRepository(Application application)
    {
        dao = AppDb.getInstance(application.getApplicationContext()).favoriteRestaurantDAO();
    }


    @Override
    public void insert(FavoriteLocationDTO favoriteLocationDTO, CarrierMessagingService.ResultCallback<FavoriteLocationDTO> callback)
    {
        new Thread(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                long id = dao.insert(favoriteLocationDTO);
                int type = favoriteLocationDTO.getType();
                FavoriteLocationDTO favoriteLocationDTO = dao.select(type, (int) id);
                callback.onReceiveResult(favoriteLocationDTO);
            }
        }).start();
    }

    @Override
    public void select(Integer type, CarrierMessagingService.ResultCallback<List<FavoriteLocationDTO>> callback)
    {
        new Thread(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                List<FavoriteLocationDTO> list = dao.select(type);
                callback.onReceiveResult(list);
            }
        }).start();
    }

    @Override
    public void select(Integer type, Integer id, CarrierMessagingService.ResultCallback<FavoriteLocationDTO> callback)
    {
        new Thread(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                FavoriteLocationDTO favoriteLocationDTO = dao.select(type, id);
                callback.onReceiveResult(favoriteLocationDTO);
            }
        }).start();
    }

    @Override
    public void delete(Integer id, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        new Thread(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                dao.delete(id);
                callback.onReceiveResult(true);
            }
        }).start();
    }

    @Override
    public void deleteAll(Integer type, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        new Thread(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                dao.deleteAll(type);
                callback.onReceiveResult(true);
            }
        }).start();
    }

    @Override
    public void contains(Integer type, String placeId, String address, String latitude, String longitude, CarrierMessagingService.ResultCallback<FavoriteLocationDTO> callback)
    {
        new Thread(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                FavoriteLocationDTO favoriteLocationDTO = dao.contains(type, placeId, address, latitude, longitude);
                callback.onReceiveResult(favoriteLocationDTO);
            }
        }).start();
    }
}
