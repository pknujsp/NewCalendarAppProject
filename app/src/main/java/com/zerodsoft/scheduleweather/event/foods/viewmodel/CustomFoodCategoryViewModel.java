package com.zerodsoft.scheduleweather.event.foods.viewmodel;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.zerodsoft.scheduleweather.event.foods.interfaces.CustomFoodCategoryQuery;
import com.zerodsoft.scheduleweather.event.foods.repository.CustomFoodCategoryRepository;
import com.zerodsoft.scheduleweather.room.dto.CustomFoodCategoryDTO;

import java.util.List;

public class CustomFoodCategoryViewModel extends AndroidViewModel implements CustomFoodCategoryQuery
{
    private CustomFoodCategoryRepository repository;

    public CustomFoodCategoryViewModel(@NonNull Application application)
    {
        super(application);
        repository = new CustomFoodCategoryRepository(application);
    }

    @Override
    public void insert(String categoryName, CarrierMessagingService.ResultCallback<CustomFoodCategoryDTO> callback)
    {
        repository.insert(categoryName, callback);
    }

    @Override
    public void select(CarrierMessagingService.ResultCallback<List<CustomFoodCategoryDTO>> callback)
    {
        repository.select(callback);
    }

    @Override
    public void update(String previousCategoryName, String newCategoryName, CarrierMessagingService.ResultCallback<CustomFoodCategoryDTO> callback)
    {
        repository.update(previousCategoryName, newCategoryName, callback);
    }

    @Override
    public void delete(String categoryName, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        repository.delete(categoryName, callback);
    }

    @Override
    public void deleteAll(CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        repository.deleteAll(callback);
    }

    @Override
    public void containsCategory(String categoryName, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        repository.containsCategory(categoryName, callback);
    }
}
