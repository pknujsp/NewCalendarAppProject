package com.zerodsoft.calendarplatform.event.foods.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.zerodsoft.calendarplatform.event.foods.interfaces.ISetFoodMenuPoiItems;

import org.jetbrains.annotations.NotNull;

public class RestaurantSharedViewModel extends AndroidViewModel {
	private ISetFoodMenuPoiItems ISetFoodMenuPoiItems;
	private Long eventId;

	public RestaurantSharedViewModel(@NonNull @NotNull Application application) {
		super(application);
	}

	public ISetFoodMenuPoiItems getISetFoodMenuPoiItems() {
		return ISetFoodMenuPoiItems;
	}

	public void setISetFoodMenuPoiItems(ISetFoodMenuPoiItems ISetFoodMenuPoiItems) {
		this.ISetFoodMenuPoiItems = ISetFoodMenuPoiItems;
	}


	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}
}
