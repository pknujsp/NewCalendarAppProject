package com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.vilagefcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VilageFcstBody {
	@Expose
	@SerializedName("items")
	private VilageFcstItems items;


	public void setItems(VilageFcstItems items) {
		this.items = items;
	}

	public VilageFcstItems getItems() {
		return items;
	}
}
