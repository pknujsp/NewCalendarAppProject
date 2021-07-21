package com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoregioncoderesponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoordToRegionCodeMeta {
	@Expose
	@SerializedName("total_count")
	private int totalCount;

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
