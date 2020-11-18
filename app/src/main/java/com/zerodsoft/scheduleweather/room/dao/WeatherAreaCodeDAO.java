package com.zerodsoft.scheduleweather.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.utility.LonLat;

import java.util.List;

@Dao
public interface WeatherAreaCodeDAO
{
    @Query("SELECT * FROM weather_area_code_table WHERE latitude_seconds_divide_100 >= :latitude-0.2 AND latitude_seconds_divide_100 <= :latitude+0.2" +
            " AND longitude_seconds_divide_100 >= :longitude-0.2 AND longitude_seconds_divide_100 <= :longitude+0.2")
    LiveData<List<WeatherAreaCodeDTO>> selectAreaCode(double latitude, double longitude);
}
