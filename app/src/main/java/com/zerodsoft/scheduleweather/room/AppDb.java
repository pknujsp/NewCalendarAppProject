package com.zerodsoft.scheduleweather.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.zerodsoft.scheduleweather.retrofit.PlaceCategory;
import com.zerodsoft.scheduleweather.room.dao.FavoriteLocDAO;
import com.zerodsoft.scheduleweather.room.dao.LocationDAO;
import com.zerodsoft.scheduleweather.room.dao.PlaceCategoryDAO;
import com.zerodsoft.scheduleweather.room.dao.WeatherAreaCodeDAO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;

@Database(entities = {LocationDTO.class, WeatherAreaCodeDTO.class, PlaceCategory.class}, version = 1, exportSchema = false)
public abstract class AppDb extends RoomDatabase
{
    private static volatile AppDb instance = null;

    public abstract FavoriteLocDAO favoriteLocDAO();

    public abstract PlaceCategoryDAO placeCategoryDAO();

    public abstract LocationDAO locationDAO();

    public abstract WeatherAreaCodeDAO weatherAreaCodeDAO();

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
