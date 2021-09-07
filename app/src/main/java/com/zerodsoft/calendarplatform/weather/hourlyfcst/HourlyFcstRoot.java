package com.zerodsoft.calendarplatform.weather.hourlyfcst;

import com.google.gson.JsonObject;

public class HourlyFcstRoot {
	int count = 0;
	private volatile JsonObject ultraSrtFcst;
	private volatile JsonObject vilageFcst;
	private volatile Exception exception;

	public JsonObject getUltraSrtFcst() {
		return ultraSrtFcst;
	}

	public void setUltraSrtFcst(JsonObject ultraSrtFcst) {
		++this.count;
		this.ultraSrtFcst = ultraSrtFcst;
	}

	public JsonObject getVilageFcst() {
		return vilageFcst;
	}

	public void setVilageFcst(JsonObject vilageFcst) {
		++this.count;
		this.vilageFcst = vilageFcst;
	}

	public void setException(Exception exception) {
		this.exception = exception;
		++this.count;

	}

	public Exception getException() {
		return exception;
	}

	public int getCount() {
		return count;
	}
}