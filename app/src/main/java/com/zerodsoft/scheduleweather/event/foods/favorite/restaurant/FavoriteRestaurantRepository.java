package com.zerodsoft.scheduleweather.event.foods.favorite.restaurant;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.event.foods.share.FavoriteRestaurantCloud;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.FavoriteRestaurantDAO;
import com.zerodsoft.scheduleweather.room.dto.FavoriteRestaurantDTO;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteRestaurantQuery;

import java.util.List;

import lombok.SneakyThrows;

class FavoriteRestaurantRepository implements FavoriteRestaurantQuery
{
    private final FavoriteRestaurantDAO favoriteRestaurantDAO;

    public FavoriteRestaurantRepository(Application application)
    {
        favoriteRestaurantDAO = AppDb.getInstance(application.getApplicationContext()).favoriteRestaurantDAO();
    }


    @Override
    public void insert(String restaurantId, CarrierMessagingService.ResultCallback<FavoriteRestaurantDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                favoriteRestaurantDAO.insert(restaurantId);
                FavoriteRestaurantDTO favoriteRestaurantDTO = favoriteRestaurantDAO.select(restaurantId);
                FavoriteRestaurantCloud.getInstance().add(restaurantId);
                callback.onReceiveResult(favoriteRestaurantDTO);
            }
        });
    }

    @Override
    public void select(CarrierMessagingService.ResultCallback<List<FavoriteRestaurantDTO>> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                List<FavoriteRestaurantDTO> list = favoriteRestaurantDAO.select();
                callback.onReceiveResult(list);
            }
        });
    }

    @Override
    public void select(String restaurantId, CarrierMessagingService.ResultCallback<FavoriteRestaurantDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                callback.onReceiveResult(favoriteRestaurantDAO.select(restaurantId));
            }
        });
    }

    @Override
    public void delete(String restaurantId, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                favoriteRestaurantDAO.delete(restaurantId);
                FavoriteRestaurantCloud.getInstance().delete(restaurantId);
                callback.onReceiveResult(true);
            }
        });
    }

    @Override
    public void deleteAll(CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                favoriteRestaurantDAO.deleteAll();
                FavoriteRestaurantCloud.getInstance().deleteAll();
                callback.onReceiveResult(true);
            }
        });
    }


    @Override
    public void containsMenu(String restaurantId, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                callback.onReceiveResult(favoriteRestaurantDAO.containsMenu(restaurantId) == 1);
            }
        });
    }
}

