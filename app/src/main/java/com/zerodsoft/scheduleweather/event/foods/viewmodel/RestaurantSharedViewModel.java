package com.zerodsoft.scheduleweather.event.foods.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.zerodsoft.scheduleweather.event.foods.interfaces.ISetFoodMenuPoiItems;
import com.zerodsoft.scheduleweather.navermap.interfaces.FavoriteLocationsListener;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapPoint;

import org.jetbrains.annotations.NotNull;

public class RestaurantSharedViewModel extends AndroidViewModel {
	private FavoriteLocationsListener favoriteLocationsListener;
	private ISetFoodMenuPoiItems ISetFoodMenuPoiItems;
	private IMapPoint iMapPoint;
	private Long eventId;

	public RestaurantSharedViewModel(@NonNull @NotNull Application application) {
		super(application);
	}

	public FavoriteLocationsListener getFavoriteLocationsListener() {
		return favoriteLocationsListener;
	}

	public void setFavoriteLocationsListener(FavoriteLocationsListener favoriteLocationsListener) {
		this.favoriteLocationsListener = favoriteLocationsListener;
	}


	public ISetFoodMenuPoiItems getISetFoodMenuPoiItems() {
		return ISetFoodMenuPoiItems;
	}

	public void setISetFoodMenuPoiItems(ISetFoodMenuPoiItems ISetFoodMenuPoiItems) {
		this.ISetFoodMenuPoiItems = ISetFoodMenuPoiItems;
	}

	public IMapPoint getiMapPoint() {
		return iMapPoint;
	}

	public void setiMapPoint(IMapPoint iMapPoint) {
		this.iMapPoint = iMapPoint;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}
}
