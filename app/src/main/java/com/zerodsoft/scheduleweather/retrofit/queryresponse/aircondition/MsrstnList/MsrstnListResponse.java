package com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.NearbyMsrstnList.NearbyMsrstnListBody;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.commons.Header;

public class MsrstnListResponse {
	@Expose
	@SerializedName("header")
	private Header header;

	@Expose
	@SerializedName("body")
	private MsrstnListBody body;

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public MsrstnListBody getBody() {
		return body;
	}

	public void setBody(MsrstnListBody body) {
		this.body = body;
	}
}
