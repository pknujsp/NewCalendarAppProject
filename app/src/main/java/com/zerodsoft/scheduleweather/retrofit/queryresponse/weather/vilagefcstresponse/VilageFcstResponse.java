package com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.vilagefcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.commons.Header;

public class VilageFcstResponse {
	@Expose
	@SerializedName("header")
	private Header header;

	@Expose
	@SerializedName("body")
	private VilageFcstBody body;


	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public VilageFcstBody getBody() {
		return body;
	}

	public void setBody(VilageFcstBody body) {
		this.body = body;
	}
}
