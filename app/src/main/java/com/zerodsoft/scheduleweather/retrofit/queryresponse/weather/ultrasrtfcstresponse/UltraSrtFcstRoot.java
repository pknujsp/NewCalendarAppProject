package com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtfcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

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
