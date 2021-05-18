package com.zerodsoft.scheduleweather.weather.dataprocessing;

import com.zerodsoft.scheduleweather.weather.common.WeatherDataCallback;

public abstract class WeatherDataProcessing<T> {
	
	public abstract void getWeatherData(String latitude, String longitude, WeatherDataCallback<T> weatherDataCallback);
	
	public abstract void refresh();
}
