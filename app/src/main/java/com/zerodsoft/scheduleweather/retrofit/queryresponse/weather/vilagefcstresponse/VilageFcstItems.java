package com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.vilagefcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.WeatherItems;

import java.util.List;

public class VilageFcstItems extends WeatherItems {
	@Expose
	@SerializedName("item")
	private List<VilageFcstItem> item;

	public void setItem(List<VilageFcstItem> item) {
		this.item = item;
	}

	public List<VilageFcstItem> getItem() {
		return item;
	}
}
