package com.zerodsoft.calendarplatform.retrofit.queryresponse.aircondition.MsrstnList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.aircondition.FindStationRoot;

import java.util.List;

public class MsrstnListBody extends FindStationRoot {
	@Expose
	@SerializedName("items")
	private List<MsrstnListItem> items;

	public List<MsrstnListItem> getItems() {
		return items;
	}

	public void setItems(List<MsrstnListItem> items) {
		this.items = items;
	}
}
