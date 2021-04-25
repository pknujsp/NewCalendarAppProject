package com.zerodsoft.scheduleweather.event.foods.favorite.restaurant;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.zerodsoft.scheduleweather.room.dto.FavoriteRestaurantDTO;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteRestaurantQuery;

import java.util.List;

public class FavoriteRestaurantViewModel extends AndroidViewModel implements FavoriteRestaurantQuery
{
    private FavoriteRestaurantRepository restaurantRepository;

    public FavoriteRestaurantViewModel(@NonNull Application application)
    {
        super(application);
        restaurantRepository = new FavoriteRestaurantRepository(application);
    }


    @Override
    public void insert(String restaurantId, String restaurantName, String latitude, String longitude, CarrierMessagingService.ResultCallback<FavoriteRestaurantDTO> callback)
    {
        restaurantRepository.insert(restaurantId, restaurantName, latitude, longitude, callback);
    }

    @Override
    public void select(CarrierMessagingService.ResultCallback<List<FavoriteRestaurantDTO>> callback)
    {
        restaurantRepository.select(callback);
    }

    @Override
    public void select(String restaurantId, CarrierMessagingService.ResultCallback<FavoriteRestaurantDTO> callback)
    {
        restaurantRepository.select(restaurantId, callback);
    }

    @Override
    public void delete(String restaurantId, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        restaurantRepository.delete(restaurantId, callback);
    }

    @Override
    public void deleteAll(CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        restaurantRepository.deleteAll(callback);
    }

    @Override
    public void containsMenu(String restaurantId, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        restaurantRepository.containsMenu(restaurantId, callback);
    }
}
