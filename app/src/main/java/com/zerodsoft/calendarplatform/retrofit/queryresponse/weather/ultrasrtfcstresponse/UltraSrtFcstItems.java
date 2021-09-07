package com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.ultrasrtfcstresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.WeatherItems;

import java.util.List;

public class UltraSrtFcstItems extends WeatherItems {
	@Expose
	@SerializedName("item")
	private List<UltraSrtFcstItem> item;


	public void setItem(List<UltraSrtFcstItem> item) {
		this.item = item;
	}

	public List<UltraSrtFcstItem> getItem() {
		return item;
	}
}
