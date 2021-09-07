package com.zerodsoft.calendarplatform.weather.common;

public abstract class WeatherDataCallback<T> {
	public abstract void isSuccessful(T e);
	
	public abstract void isFailure(Exception e);
}
