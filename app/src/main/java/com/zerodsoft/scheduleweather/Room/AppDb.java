package com.zerodsoft.scheduleweather.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.zerodsoft.scheduleweather.Room.DAO.AreaDao;
import com.zerodsoft.scheduleweather.Room.DAO.CWeatherDao;
import com.zerodsoft.scheduleweather.Room.DAO.CWeatherLocationDao;
import com.zerodsoft.scheduleweather.Room.DAO.FavoriteLocDAO;
import com.zerodsoft.scheduleweather.Room.DAO.NforecastDao;
import com.zerodsoft.scheduleweather.Room.DAO.ScheduleCategoryDAO;
import com.zerodsoft.scheduleweather.Room.DAO.ScheduleDAO;
import com.zerodsoft.scheduleweather.Room.DAO.TravelDao;
import com.zerodsoft.scheduleweather.Room.DAO.UstForecastDao;
import com.zerodsoft.scheduleweather.Room.DAO.WeatherUpdateTimeDao;
import com.zerodsoft.scheduleweather.Room.DTO.Area;
import com.zerodsoft.scheduleweather.Room.DTO.CWeather;
import com.zerodsoft.scheduleweather.Room.DTO.CWeatherLocation;
import com.zerodsoft.scheduleweather.Room.DTO.FavoriteLocDTO;
import com.zerodsoft.scheduleweather.Room.DTO.Nforecast;
import com.zerodsoft.scheduleweather.Room.DTO.Schedule;
import com.zerodsoft.scheduleweather.Room.DTO.ScheduleCategoryDTO;
import com.zerodsoft.scheduleweather.Room.DTO.ScheduleDTO;
import com.zerodsoft.scheduleweather.Room.DTO.Travel;
import com.zerodsoft.scheduleweather.Room.DTO.UstForecast;
import com.zerodsoft.scheduleweather.Room.DTO.WeatherUpdateTime;

@Database(entities = {Area.class, CWeather.class, CWeatherLocation.class, Nforecast.class, Schedule.class, Travel.class, UstForecast.class, WeatherUpdateTime.class,
        FavoriteLocDTO.class, ScheduleCategoryDTO.class, ScheduleDTO.class}, version = 1, exportSchema = false)
public abstract class AppDb extends RoomDatabase
{
    private static AppDb instance = null;

    public abstract AreaDao areaDao();

    public abstract CWeatherDao cWeatherDao();

    public abstract CWeatherLocationDao cWeatherLocationDao();

    public abstract NforecastDao nforecastDao();

    public abstract TravelDao travelDao();

    public abstract UstForecastDao ustForecastDao();

    public abstract WeatherUpdateTimeDao weatherUpdateTimeDao();

    public abstract FavoriteLocDAO favoriteLocDAO();

    public abstract ScheduleDAO scheduleDAO();

    public abstract ScheduleCategoryDAO scheduleCategoryDAO();


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
