package com.zerodsoft.scheduleweather.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.zerodsoft.scheduleweather.room.dao.FavoriteLocDAO;
import com.zerodsoft.scheduleweather.room.dao.LocationDAO;
import com.zerodsoft.scheduleweather.room.dao.ScheduleDAO;
import com.zerodsoft.scheduleweather.room.dao.WeatherAreaCodeDAO;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;

@Database(entities = {FavoriteLocDTO.class, PlaceDTO.class, AddressDTO.class, ScheduleDTO.class, WeatherAreaCodeDTO.class}, version = 1, exportSchema = false)
public abstract class AppDb extends RoomDatabase
{
    private static volatile AppDb instance = null;

    public abstract FavoriteLocDAO favoriteLocDAO();

    public abstract ScheduleDAO scheduleDAO();

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
