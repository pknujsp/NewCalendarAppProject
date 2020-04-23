package com.zerodsoft.tripweather.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.zerodsoft.tripweather.Room.DAO.AreaDao;
import com.zerodsoft.tripweather.Room.DTO.Area;

@Database(entities = {Area.class}, version = 1, exportSchema = false)
public abstract class AreaDb extends RoomDatabase
{
    private static AreaDb instance = null;

    public abstract AreaDao areaDao();

    public static AreaDb getInstance(Context context)
    {
        if (instance == null)
        {
            instance = Room.databaseBuilder(context, AreaDb.class, "area_db")
                    .createFromAsset("database/area_db.db").build();
        }
        return instance;
    }

    public static void closeInstance()
    {
        instance = null;
    }
}
