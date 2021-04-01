package com.zerodsoft.scheduleweather.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.zerodsoft.scheduleweather.room.dao.CustomFoodCategoryDAO;
import com.zerodsoft.scheduleweather.room.dao.CustomPlaceCategoryDAO;
import com.zerodsoft.scheduleweather.room.dao.FoodCriteriaLocationInfoDAO;
import com.zerodsoft.scheduleweather.room.dao.FoodCriteriaLocationSearchHistoryDAO;
import com.zerodsoft.scheduleweather.room.dto.CustomFoodCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.CustomPlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dao.FavoriteLocDAO;
import com.zerodsoft.scheduleweather.room.dao.LocationDAO;
import com.zerodsoft.scheduleweather.room.dao.SelectedPlaceCategoryDAO;
import com.zerodsoft.scheduleweather.room.dao.WeatherAreaCodeDAO;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationSearchHistoryDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.SelectedPlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;

@Database(entities = {LocationDTO.class, WeatherAreaCodeDTO.class, SelectedPlaceCategoryDTO.class, CustomPlaceCategoryDTO.class, CustomFoodCategoryDTO.class,
        FoodCriteriaLocationInfoDTO.class, FoodCriteriaLocationSearchHistoryDTO.class}, version = 1, exportSchema = false)
public abstract class AppDb extends RoomDatabase
{
    private static volatile AppDb instance = null;

    public abstract FavoriteLocDAO favoriteLocDAO();

    public abstract SelectedPlaceCategoryDAO selectedPlaceCategoryDAO();

    public abstract CustomPlaceCategoryDAO customPlaceCategoryDAO();

    public abstract LocationDAO locationDAO();

    public abstract WeatherAreaCodeDAO weatherAreaCodeDAO();

    public abstract CustomFoodCategoryDAO customFoodCategoryDAO();

    public abstract FoodCriteriaLocationInfoDAO foodCriteriaLocationInfoDAO();

    public abstract FoodCriteriaLocationSearchHistoryDAO foodCriteriaLocationSearchHistoryDAO();


    public static synchronized AppDb getInstance(Context context)
    {
        if (instance == null)
        {
            instance = Room.databaseBuilder(context, AppDb.class, "appdb")
                    .createFromAsset("database/appdb.db").build();
        }
        return instance;
    }

    public static void closeInstance()
    {
        instance = null;
    }
}
