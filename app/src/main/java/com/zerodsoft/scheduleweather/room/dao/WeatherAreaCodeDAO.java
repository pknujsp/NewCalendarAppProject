package com.zerodsoft.scheduleweather.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.utility.LonLat;

import java.util.List;

@Dao
public interface WeatherAreaCodeDAO {
	@Query("SELECT * FROM weather_area_code_table WHERE latitude_seconds_divide_100 >= :latitude-0.19 AND latitude_seconds_divide_100 <= " +
			":latitude+0.19" +
			" AND longitude_seconds_divide_100 >= :longitude-0.19 AND longitude_seconds_divide_100 <= :longitude+0.19")
	List<WeatherAreaCodeDTO> getAreaCodes(double latitude, double longitude);
}
