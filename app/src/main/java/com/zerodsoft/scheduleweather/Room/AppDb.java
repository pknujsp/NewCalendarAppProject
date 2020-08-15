package com.zerodsoft.scheduleweather.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.zerodsoft.scheduleweather.Room.DAO.FavoriteLocDAO;
import com.zerodsoft.scheduleweather.Room.DAO.LocationDAO;
import com.zerodsoft.scheduleweather.Room.DAO.ScheduleDAO;
import com.zerodsoft.scheduleweather.Room.DTO.AddressDTO;
import com.zerodsoft.scheduleweather.Room.DTO.FavoriteLocDTO;
import com.zerodsoft.scheduleweather.Room.DTO.PlaceDTO;
import com.zerodsoft.scheduleweather.Room.DTO.ScheduleDTO;
import com.zerodsoft.scheduleweather.Room.DTO.TypeConverter;

@Database(entities = {FavoriteLocDTO.class, PlaceDTO.class, AddressDTO.class, ScheduleDTO.class}, version = 1, exportSchema = false)
@TypeConverters({TypeConverter.class})
public abstract class AppDb extends RoomDatabase
{
    private static AppDb instance = null;

    public abstract FavoriteLocDAO favoriteLocDAO();

    public abstract ScheduleDAO scheduleDAO();

    public abstract LocationDAO locationDAO();


    public static AppDb getInstance(Context context)
    {
        if (instance == null)
        {
            instance = Room.databaseBuilder(context, AppDb.class, "appdbfile")
                    .createFromAsset("database/appdbfile.db").build();
        }
        return instance;
    }

    public static void closeInstance()
    {
        instance = null;
    }
}
