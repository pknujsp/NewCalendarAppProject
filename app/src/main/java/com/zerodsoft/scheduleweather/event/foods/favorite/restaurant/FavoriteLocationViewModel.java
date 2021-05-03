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
    public void insert(FavoriteLocationDTO favoriteLocationDTO, CarrierMessagingService.ResultCallback<FavoriteLocationDTO> callback)
    {
        restaurantRepository.insert(favoriteLocationDTO, callback);
    }

    @Override
    public void select(Integer type, CarrierMessagingService.ResultCallback<List<FavoriteLocationDTO>> callback)
    {
        restaurantRepository.select(type, callback);
    }

    @Override
    public void select(Integer type, Integer id, CarrierMessagingService.ResultCallback<FavoriteLocationDTO> callback)
    {
        restaurantRepository.select(type, id, callback);
    }

    @Override
    public void delete(Integer id, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        restaurantRepository.delete(id, callback);
    }

    @Override
    public void deleteAll(Integer type, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        restaurantRepository.deleteAll(type, callback);
    }

    @Override
    public void contains(Integer type, String placeId, String address, String latitude, String longitude, CarrierMessagingService.ResultCallback<FavoriteLocationDTO> callback)
    {
        restaurantRepository.contains(type, placeId, address, latitude, longitude, callback);
    }
}