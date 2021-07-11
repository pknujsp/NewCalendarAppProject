package com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtfcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.WeatherItems;

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
