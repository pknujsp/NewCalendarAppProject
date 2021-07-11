package com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtfcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.commons.Header;

public class UltraSrtFcstResponse {
	@Expose
	@SerializedName("header")
	private Header header;

	@Expose
	@SerializedName("body")
	private UltraSrtFcstBody body;


	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public UltraSrtFcstBody getBody() {
		return body;
	}

	public void setBody(UltraSrtFcstBody body) {
		this.body = body;
	}
}
