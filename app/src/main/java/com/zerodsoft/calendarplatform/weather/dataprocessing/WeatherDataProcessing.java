package com.zerodsoft.calendarplatform.weather.dataprocessing;

import android.content.Context;

import com.zerodsoft.calendarplatform.weather.common.WeatherDataCallback;
import com.zerodsoft.calendarplatform.weather.repository.WeatherDbRepository;
import com.zerodsoft.calendarplatform.weather.repository.AreaCodeRepository;

public abstract class WeatherDataProcessing<T> {
	protected AreaCodeRepository areaCodeRepository;
	protected WeatherDbRepository weatherDbRepository;
	protected Context context;
	protected final String LATITUDE;
	protected final String LONGITUDE;

	public WeatherDataProcessing(Context context, String LATITUDE, String LONGITUDE) {
		this.context = context;
		this.LATITUDE = LATITUDE;
		this.LONGITUDE = LONGITUDE;
		this.areaCodeRepository = new AreaCodeRepository(context);
		this.weatherDbRepository = new WeatherDbRepository(context);
	}

	public abstract void getWeatherData(WeatherDataCallback<T> weatherDataCallback);

	public abstract void refresh(WeatherDataCallback<T> weatherDataCallback);
}