package com.zerodsoft.tripweather.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.zerodsoft.tripweather.Room.DAO.AreaDao;
import com.zerodsoft.tripweather.Room.DAO.CWeatherDao;
import com.zerodsoft.tripweather.Room.DAO.CWeatherLocationDao;
import com.zerodsoft.tripweather.Room.DAO.NforecastDao;
import com.zerodsoft.tripweather.Room.DAO.ScheduleDao;
import com.zerodsoft.tripweather.Room.DAO.TravelDao;
import com.zerodsoft.tripweather.Room.DAO.UstForecastDao;
import com.zerodsoft.tripweather.Room.DAO.WeatherUpdateTimeDao;
import com.zerodsoft.tripweather.Room.DTO.Area;
import com.zerodsoft.tripweather.Room.DTO.CWeather;
import com.zerodsoft.tripweather.Room.DTO.CWeatherLocation;
import com.zerodsoft.tripweather.Room.DTO.Nforecast;
import com.zerodsoft.tripweather.Room.DTO.Schedule;
import com.zerodsoft.tripweather.Room.DTO.Travel;
import com.zerodsoft.tripweather.Room.DTO.UstForecast;
import com.zerodsoft.tripweather.Room.DTO.WeatherUpdateTime;

@Database(entities = {Area.class, CWeather.class, CWeatherLocation.class, Nforecast.class, Schedule.class, Travel.class, UstForecast.class, WeatherUpdateTime.class}, version = 1, exportSchema = false)
public abstract class AppDb extends RoomDatabase
{
    private static AppDb instance = null;

    public abstract AreaDao areaDao();

    public abstract CWeatherDao cWeatherDao();

    public abstract CWeatherLocationDao cWeatherLocationDao();

    public abstract NforecastDao nforecastDao();

    public abstract ScheduleDao scheduleDao();

    public abstract TravelDao travelDao();

    public abstract UstForecastDao ustForecastDao();

    public abstract WeatherUpdateTimeDao weatherUpdateTimeDao();


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
