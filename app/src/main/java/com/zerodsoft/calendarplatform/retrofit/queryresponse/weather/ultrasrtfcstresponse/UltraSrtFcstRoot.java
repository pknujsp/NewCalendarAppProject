package com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.ultrasrtfcstresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UltraSrtFcstRoot {
	@Expose
	@SerializedName("response")
	private UltraSrtFcstResponse response;


	public void setResponse(UltraSrtFcstResponse response) {
		this.response = response;
	}

	public UltraSrtFcstResponse getResponse() {
		return response;
	}
}
