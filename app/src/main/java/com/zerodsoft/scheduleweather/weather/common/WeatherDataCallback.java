package com.zerodsoft.scheduleweather.weather.common;

import java.util.Date;

public abstract class WeatherDataCallback<T> {
	public abstract void isSuccessful(T e);
	
	public abstract void isFailure(T e, Date date);
}
