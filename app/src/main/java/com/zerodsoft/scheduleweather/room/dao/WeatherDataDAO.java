package com.zerodsoft.scheduleweather.room.dao;

import android.service.carrier.CarrierMessagingService;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;

import java.util.List;

@Dao
public interface WeatherDataDAO
{
    @Insert(entity = WeatherDataDTO.class, onConflict = OnConflictStrategy.REPLACE)
    long insert(WeatherDataDTO weatherDataDTO);

    @Query("UPDATE weather_data_table SET json = :json WHERE latitude = :latitude AND longitude = :longitude AND data_type = :dataType")
    void update(String latitude, String longitude, Integer dataType, String json);

    @Query("SELECT * FROM weather_data_table WHERE latitude = :latitude AND longitude = :longitude")
    List<WeatherDataDTO> getWeatherDataList(String latitude, String longitude);

    @Query("SELECT * FROM weather_data_table WHERE latitude = :latitude AND longitude = :longitude AND data_type = :dataType")
    WeatherDataDTO getWeatherData(String latitude, String longitude, Integer dataType);

    @Query("SELECT downloaded_date, data_type FROM weather_data_table WHERE latitude = :latitude AND longitude = :longitude")
    List<WeatherDataDTO> getDownloadedDataList(String latitude, String longitude);

    @Query("DELETE FROM weather_data_table WHERE latitude = :latitude AND longitude = :longitude AND data_type = :dataType")
    void delete(String latitude, String longitude, Integer dataType);

    @Query("DELETE FROM weather_data_table WHERE latitude = :latitude AND longitude = :longitude")
    void delete(String latitude, String longitude);
}
