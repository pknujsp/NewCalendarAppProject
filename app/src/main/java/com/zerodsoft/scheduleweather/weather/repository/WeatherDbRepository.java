package com.zerodsoft.scheduleweather.weather.repository;

import android.content.Context;

import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.WeatherDataDAO;
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.room.interfaces.WeatherDataQuery;

import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;


public class WeatherDbRepository implements WeatherDataQuery {
	private WeatherDataDAO dao;

	public WeatherDbRepository(Context context) {
		dao = AppDb.getInstance(context).weatherDataDAO();
	}

	@Override
	public void insert(WeatherDataDTO weatherDataDTO, DbQueryCallback<WeatherDataDTO> callback) {
		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				long id = dao.insert(weatherDataDTO);
				WeatherDataDTO result = dao.getWeatherData(weatherDataDTO.getLatitude(), weatherDataDTO.getLongitude(), weatherDataDTO.getDataType());
				callback.onResultSuccessful(result);
			}
		}).start();
	}

	@Override
	public void update(String latitude, String longitude, Integer dataType, String json, String downloadedDate, DbQueryCallback<Boolean> callback) {
		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.update(latitude, longitude, dataType, json, downloadedDate);
				callback.onResultSuccessful(true);
			}
		}).start();
	}

	@Override
	public void getWeatherDataList(String latitude, String longitude, DbQueryCallback<List<WeatherDataDTO>> callback) {
		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				List<WeatherDataDTO> list = dao.getWeatherDataList(latitude, longitude);
				if (list == null) {
					callback.onResultNoData();
				} else {
					callback.onResultSuccessful(list);
				}

			}
		}).start();
	}

	@Override
	public void getWeatherData(String latitude, String longitude, Integer dataType, DbQueryCallback<WeatherDataDTO> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				WeatherDataDTO result = dao.getWeatherData(latitude, longitude, dataType);
				if (result == null) {
					callback.onResultNoData();
				} else {
					callback.onResultSuccessful(result);
				}
			}
		}).start();
	}

	@Override
	public void getWeatherMultipleData(String latitude, String longitude, DbQueryCallback<List<WeatherDataDTO>> callback, Integer... dataTypes) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<WeatherDataDTO> list = new ArrayList<>();
				for (Integer dataType : dataTypes) {
					WeatherDataDTO result = dao.getWeatherData(latitude, longitude, dataType);
					list.add(result);
				}
				if (list.isEmpty()) {
					callback.onResultNoData();
				} else {
					callback.onResultSuccessful(list);
				}
			}
		}).start();
	}

	@Override
	public void getDownloadedDateList(String latitude, String longitude, DbQueryCallback<List<WeatherDataDTO>> callback) {
		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				List<WeatherDataDTO> list = dao.getDownloadedDateList(latitude, longitude);
				if (list == null) {
					callback.onResultNoData();
				} else {
					callback.onResultSuccessful(list);
				}
			}
		}).start();
	}

	@Override
	public void delete(String latitude, String longitude, Integer dataType, DbQueryCallback<Boolean> callback) {
		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.delete(latitude, longitude, dataType);
				callback.onResultSuccessful(true);
			}
		}).start();
	}

	@Override
	public void delete(String latitude, String longitude, DbQueryCallback<Boolean> callback) {
		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.delete(latitude, longitude);
				callback.onResultSuccessful(true);
			}
		}).start();
	}

	@Override
	public void deleteAll(DbQueryCallback<Boolean> callback) {
		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.deleteAll();
				callback.onResultSuccessful(true);
			}
		}).start();
	}

	@Override
	public void contains(String latitude, String longitude, Integer dataType, DbQueryCallback<Boolean> callback) {
		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				boolean result = dao.contains(latitude, longitude, dataType) == 1;
				callback.onResultSuccessful(result);
			}
		}).start();
	}
}
