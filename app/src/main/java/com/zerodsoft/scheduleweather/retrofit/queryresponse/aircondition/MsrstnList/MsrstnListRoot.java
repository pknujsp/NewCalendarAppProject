package com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.FindStationRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.NearbyMsrstnList.NearbyMsrstnListResponse;

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
