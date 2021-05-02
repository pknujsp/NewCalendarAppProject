package com.zerodsoft.scheduleweather.event.foods.favorite.restaurant;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteLocationQuery;

import java.util.List;

public class FavoriteLocationViewModel extends AndroidViewModel implements FavoriteLocationQuery
{
    private FavoriteLocationRepository restaurantRepository;

    public FavoriteLocationViewModel(@NonNull Application application)
    {
        super(application);
        restaurantRepository = new FavoriteLocationRepository(application);
    }

    @Override
    public void insert(String locationId, String locationName, String latitude, String longitude, Integer type, CarrierMessagingService.ResultCallback<FavoriteLocationDTO> callback)
    {
        restaurantRepository.insert(locationId, locationName, latitude, longitude, type, callback);
    }

    @Override
    public void select(Integer type, CarrierMessagingService.ResultCallback<List<FavoriteLocationDTO>> callback)
    {
        restaurantRepository.select(type, callback);
    }

    @Override
    public void select(String locationName, Integer type, CarrierMessagingService.ResultCallback<FavoriteLocationDTO> callback)
    {
        restaurantRepository.select(locationName, type, callback);
    }

    @Override
    public void delete(String locationName, Integer type, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        restaurantRepository.delete(locationName, type, callback);
    }

    @Override
    public void deleteAll(Integer type, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        restaurantRepository.deleteAll(type, callback);
    }

    @Override
    public void contains(Integer type, String locationName, String locationId, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        restaurantRepository.contains(type, locationName, locationId, callback);
    }
}
