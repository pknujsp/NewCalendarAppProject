package com.zerodsoft.scheduleweather.event.foods.interfaces;

import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.room.dto.CustomFoodMenuDTO;

import java.util.List;

public interface CustomFoodMenuQuery
{
    void insert(String menuName, CarrierMessagingService.ResultCallback<CustomFoodMenuDTO> callback);

    void select(CarrierMessagingService.ResultCallback<List<CustomFoodMenuDTO>> callback);

    void update(String previousMenuName, String newMenuName, CarrierMessagingService.ResultCallback<CustomFoodMenuDTO> callback);

    void delete(String menuName, CarrierMessagingService.ResultCallback<Boolean> callback);

    void deleteAll(CarrierMessagingService.ResultCallback<Boolean> callback);

    void containsMenu(String menuName, CarrierMessagingService.ResultCallback<Boolean> callback);
}