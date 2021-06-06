package com.zerodsoft.scheduleweather.event.foods.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.zerodsoft.scheduleweather.event.foods.interfaces.FoodMenuChipsViewController;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnSetViewVisibility;
import com.zerodsoft.scheduleweather.navermap.interfaces.FavoriteLocationsListener;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapPoint;

import org.jetbrains.annotations.NotNull;

public class RestaurantSharedViewModel extends AndroidViewModel {
	private FavoriteLocationsListener favoriteLocationsListener;
	private FoodMenuChipsViewController foodMenuChipsViewController;
	private OnSetViewVisibility onSetViewVisibility;
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

	public void setOnSetViewVisibility(OnSetViewVisibility onSetViewVisibility) {
		this.onSetViewVisibility = onSetViewVisibility;
	}

	public OnSetViewVisibility getOnSetViewVisibility() {
		return onSetViewVisibility;
	}

	public FoodMenuChipsViewController getFoodMenuChipsViewController() {
		return foodMenuChipsViewController;
	}

	public void setFoodMenuChipsViewController(FoodMenuChipsViewController foodMenuChipsViewController) {
		this.foodMenuChipsViewController = foodMenuChipsViewController;
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
