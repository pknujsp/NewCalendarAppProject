package com.zerodsoft.scheduleweather.event.foods.repository;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.event.foods.interfaces.CustomFoodMenuQuery;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.CustomFoodMenuDAO;
import com.zerodsoft.scheduleweather.room.dto.CustomFoodMenuDTO;

import java.util.List;

import lombok.SneakyThrows;

public class CustomFoodMenuRepository implements CustomFoodMenuQuery
{
    private CustomFoodMenuDAO categoryDAO;

    public CustomFoodMenuRepository(Application application)
    {
        categoryDAO = AppDb.getInstance(application.getApplicationContext()).customFoodCategoryDAO();
    }

    @Override
    public void insert(String menuName, CarrierMessagingService.ResultCallback<CustomFoodMenuDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                categoryDAO.insert(menuName);
                CustomFoodMenuDTO categoryDTO = categoryDAO.select(menuName);
                callback.onReceiveResult(categoryDTO);
            }
        });
    }

    @Override
    public void select(CarrierMessagingService.ResultCallback<List<CustomFoodMenuDTO>> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                List<CustomFoodMenuDTO> list = categoryDAO.select();
                callback.onReceiveResult(list);
            }
        });
    }

    @Override
    public void update(String previousMenuName, String newMenuName, CarrierMessagingService.ResultCallback<CustomFoodMenuDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                categoryDAO.update(previousMenuName, newMenuName);
                CustomFoodMenuDTO categoryDTO = categoryDAO.select(newMenuName);
                callback.onReceiveResult(categoryDTO);
            }
        });
    }

    @Override
    public void delete(String menuName, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                categoryDAO.delete(menuName);
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
    public void containsMenu(String menuName, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                int result = categoryDAO.containsMenu(menuName);
                callback.onReceiveResult(result == 1);
            }
        });
    }
}
