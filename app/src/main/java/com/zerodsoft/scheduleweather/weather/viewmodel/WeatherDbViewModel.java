package com.zerodsoft.scheduleweather.weather.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.room.interfaces.WeatherDataQuery;
import com.zerodsoft.scheduleweather.weather.repository.WeatherDbRepository;

import java.util.List;

public class WeatherDbViewModel extends AndroidViewModel implements WeatherDataQuery {
	private WeatherDbRepository repository;

	public WeatherDbViewModel(@NonNull Application application) {
		super(application);
		repository = new WeatherDbRepository(application.getApplicationContext());
	}

	@Override
	public void insert(WeatherDataDTO weatherDataDTO, DbQueryCallback<WeatherDataDTO> callback) {
		repository.insert(weatherDataDTO, callback);
	}

	@Override
	public void update(String latitude, String longitude, Integer dataType, String json, String downloadedDate, DbQueryCallback<Boolean> callback) {
		repository.update(latitude, longitude, dataType, json, downloadedDate, callback);
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
	public void delete(String latitude, String longitude, Integer dataType, DbQueryCallback<Boolean> callback) {
		repository.delete(latitude, longitude, dataType, callback);
	}

	@Override
	public void delete(String latitude, String longitude, DbQueryCallback<Boolean> callback) {
		repository.delete(latitude, longitude, callback);
	}

	@Override
	public void deleteAll(DbQueryCallback<Boolean> callback) {
		repository.deleteAll(callback);
	}

	@Override
	public void contains(String latitude, String longitude, Integer dataType, DbQueryCallback<Boolean> callback) {
		repository.contains(latitude, longitude, dataType, callback);
	}
}
