package com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtfcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UltraSrtFcstBody {
	@Expose
	@SerializedName("items")
	private UltraSrtFcstItems items;


	public void setItems(UltraSrtFcstItems items) {
		this.items = items;
	}

	public UltraSrtFcstItems getItems() {
		return items;
	}
}
