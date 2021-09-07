package com.zerodsoft.calendarplatform.weather.mid;

import com.google.gson.JsonObject;

public class MidFcstRoot {
	int count = 0;
	private volatile JsonObject midTa;
	private volatile JsonObject midLandFcst;
	private volatile Exception exception;

	public JsonObject getMidTa() {
		return midTa;
	}

	public void setMidTa(JsonObject midTa) {
		++this.count;
		this.midTa = midTa;
	}

	public JsonObject getMidLandFcst() {
		return midLandFcst;
	}

	public void setMidLandFcst(JsonObject midLandFcst) {
		++this.count;
		this.midLandFcst = midLandFcst;
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
