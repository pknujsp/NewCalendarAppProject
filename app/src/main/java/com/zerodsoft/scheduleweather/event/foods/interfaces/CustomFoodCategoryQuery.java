package com.zerodsoft.scheduleweather.event.foods.interfaces;

import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.room.dto.CustomFoodCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.CustomPlaceCategoryDTO;

import java.util.List;

public interface CustomFoodCategoryQuery
{
    void insert(String categoryName, CarrierMessagingService.ResultCallback<CustomFoodCategoryDTO> callback);

    void select(CarrierMessagingService.ResultCallback<List<CustomFoodCategoryDTO>> callback);

    void update(String previousCategoryName, String newCategoryName, CarrierMessagingService.ResultCallback<CustomFoodCategoryDTO> callback);

    void delete(String categoryName, CarrierMessagingService.ResultCallback<Boolean> callback);

    void deleteAll(CarrierMessagingService.ResultCallback<Boolean> callback);

    void containsCategory(String categoryName, CarrierMessagingService.ResultCallback<Boolean> callback);
}
