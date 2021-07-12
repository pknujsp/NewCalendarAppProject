package com.zerodsoft.scheduleweather.room.interfaces;

import androidx.annotation.Nullable;

import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;

import java.util.List;

public interface WeatherDataQuery {
	void insert(WeatherDataDTO weatherDataDTO, @Nullable DbQueryCallback<WeatherDataDTO> callback);

	void update(String latitude, String longitude, Integer dataType, String json, String downloadedDate, @Nullable DbQueryCallback<Boolean> callback);

	void getWeatherDataList(String latitude, String longitude, DbQueryCallback<List<WeatherDataDTO>> callback);

	void getWeatherData(String latitude, String longitude, Integer dataType, DbQueryCallback<WeatherDataDTO> callback);

	void getWeatherMultipleData(String latitude, String longitude, DbQueryCallback<List<WeatherDataDTO>> callback, Integer... dataTypes);

	void getDownloadedDateList(String latitude, String longitude, DbQueryCallback<List<WeatherDataDTO>> callback);

	void delete(String latitude, String longitude, Integer dataType, @Nullable DbQueryCallback<Boolean> callback);

	void delete(String latitude, String longitude, @Nullable DbQueryCallback<Boolean> callback);

	void deleteAll(@Nullable DbQueryCallback<Boolean> callback);

	void contains(String latitude, String longitude, Integer dataType, DbQueryCallback<Boolean> callback);
}
