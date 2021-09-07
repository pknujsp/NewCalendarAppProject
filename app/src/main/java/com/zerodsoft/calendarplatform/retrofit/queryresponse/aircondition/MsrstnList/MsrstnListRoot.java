package com.zerodsoft.calendarplatform.retrofit.queryresponse.aircondition.MsrstnList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.aircondition.FindStationRoot;

public class MsrstnListRoot extends FindStationRoot {
	@Expose
	@SerializedName("response")
	private MsrstnListResponse response;

	public MsrstnListResponse getResponse() {
		return response;
	}

	public void setResponse(MsrstnListResponse response) {
		this.response = response;
	}
}
