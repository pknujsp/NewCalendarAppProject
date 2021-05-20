package com.zerodsoft.scheduleweather.weather.common;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.commons.Header;

public abstract class WeatherDataHeaderChecker {
	public void processResult(Header header) {
		if (header.getResultCode().equals("00")) {
			isSuccessful();
		} else {
			isFailure(new Exception(header.getResultMsg()));
		}
	}

	public abstract void isSuccessful();

	public abstract void isFailure(Exception e);
}
