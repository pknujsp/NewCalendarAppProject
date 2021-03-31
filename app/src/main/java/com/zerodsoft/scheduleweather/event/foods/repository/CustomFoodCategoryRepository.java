package com.zerodsoft.scheduleweather.event.foods.repository;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.event.foods.interfaces.CustomFoodCategoryQuery;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.CustomFoodCategoryDAO;
import com.zerodsoft.scheduleweather.room.dto.CustomFoodCategoryDTO;

import java.util.List;

import lombok.SneakyThrows;

public class CustomFoodCategoryRepository implements CustomFoodCategoryQuery
{
    private CustomFoodCategoryDAO categoryDAO;

    public CustomFoodCategoryRepository(Application application)
    {
        categoryDAO = AppDb.getInstance(application.getApplicationContext()).customFoodCategoryDAO();
    }

    @Override
    public void insert(String categoryName, CarrierMessagingService.ResultCallback<CustomFoodCategoryDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                categoryDAO.insert(categoryName);
                CustomFoodCategoryDTO categoryDTO = categoryDAO.select(categoryName);
                callback.onReceiveResult(categoryDTO);
            }
        });
    }

    @Override
    public void select(CarrierMessagingService.ResultCallback<List<CustomFoodCategoryDTO>> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                List<CustomFoodCategoryDTO> list = categoryDAO.select();
                callback.onReceiveResult(list);
            }
        });
    }

    @Override
    public void update(String previousCategoryName, String newCategoryName, CarrierMessagingService.ResultCallback<CustomFoodCategoryDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                categoryDAO.update(previousCategoryName, newCategoryName);
                CustomFoodCategoryDTO categoryDTO = categoryDAO.select(newCategoryName);
                callback.onReceiveResult(categoryDTO);
            }
        });
    }

    @Override
    public void delete(String categoryName, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                categoryDAO.delete(categoryName);
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
                categoryDAO.deleteAll();
                callback.onReceiveResult(true);
            }
        });
    }

    @Override
    public void containsCategory(String categoryName, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                int result = categoryDAO.containsCode(categoryName);
                callback.onReceiveResult(result == 1);
            }
        });
    }
}
