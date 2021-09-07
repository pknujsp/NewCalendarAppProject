package com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.vilagefcstresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.commons.Header;

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
