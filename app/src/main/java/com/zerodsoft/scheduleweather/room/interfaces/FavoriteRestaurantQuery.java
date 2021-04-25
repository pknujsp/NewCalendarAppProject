package com.zerodsoft.scheduleweather.room.interfaces;

import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.room.dto.FavoriteRestaurantDTO;

import java.util.List;

public interface FavoriteRestaurantQuery
{
    void insert(String restaurantId, String restaurantName, String latitude, String longitude, CarrierMessagingService.ResultCallback<FavoriteRestaurantDTO> callback);

    void select(CarrierMessagingService.ResultCallback<List<FavoriteRestaurantDTO>> callback);

    void select(String restaurantId, CarrierMessagingService.ResultCallback<FavoriteRestaurantDTO> callback);

    void delete(String restaurantId, CarrierMessagingService.ResultCallback<Boolean> callback);

    void deleteAll(CarrierMessagingService.ResultCallback<Boolean> callback);

    void containsMenu(String restaurantId, CarrierMessagingService.ResultCallback<Boolean> callback);
}
