package com.zerodsoft.calendarplatform.weather.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.room.dto.WeatherDataDTO;
import com.zerodsoft.calendarplatform.room.interfaces.WeatherDataQuery;
import com.zerodsoft.calendarplatform.weather.repository.WeatherDbRepository;

import java.util.List;

public class WeatherDbViewModel extends AndroidViewModel implements WeatherDataQuery {
	private WeatherDbRepository repository;

	public WeatherDbViewModel(@NonNull Application application) {
		super(application);
		repository = new WeatherDbRepository(application.getApplicationContext());
	}

	@Override
	public void insert(WeatherDataDTO weatherDataDTO, @Nullable DbQueryCallback<WeatherDataDTO> callback) {
		repository.insert(weatherDataDTO, callback);
	}

	@Override
	public void update(String latitude, String longitude, Integer dataType, String json, String downloadedDate,
	                   String baseDateTime, @Nullable DbQueryCallback<Boolean> callback) {
		repository.update(latitude, longitude, dataType, json, downloadedDate, baseDateTime, callback);
	}

	@Override
	public void getWeatherDataList(String latitude, String longitude, DbQueryCallback<List<WeatherDataDTO>> callback) {
		repository.getWeatherDataList(latitude, longitude, callback);
	}

	@Override
	public void getWeatherData(String latitude, String longitude, Integer dataType, DbQueryCallback<WeatherDataDTO> callback) {
		repository.getWeatherData(latitude, longitude, dataType, callback);
	}

	@Override
	public void getWeatherMultipleData(String latitude, String longitude, DbQueryCallback<List<WeatherDataDTO>> callback, Integer... dataTypes) {

	}

	@Override
	public void getDownloadedDateList(String latitude, String longitude, DbQueryCallback<List<WeatherDataDTO>> callback) {
		repository.getDownloadedDateList(latitude, longitude, callback);
	}

	@Override
	public void delete(String latitude, String longitude, Integer dataType, @Nullable DbQueryCallback<Boolean> callback) {
		repository.delete(latitude, longitude, dataType, callback);
	}

	@Override
	public void delete(String latitude, String longitude, @Nullable DbQueryCallback<Boolean> callback) {
		repository.delete(latitude, longitude, callback);
	}

	@Override
	public void deleteAll(@Nullable DbQueryCallback<Boolean> callback) {
		repository.deleteAll(callback);
	}

	@Override
	public void contains(String latitude, String longitude, Integer dataType, DbQueryCallback<Boolean> callback) {
		repository.contains(latitude, longitude, dataType, callback);
	}
}
