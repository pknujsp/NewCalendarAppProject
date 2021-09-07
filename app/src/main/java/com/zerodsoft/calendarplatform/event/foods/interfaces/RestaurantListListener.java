package com.zerodsoft.calendarplatform.event.foods.interfaces;

import androidx.annotation.Nullable;

import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

import java.util.List;

public interface RestaurantListListener {
	void onLoadedInitialRestaurantList(String query, @Nullable List<PlaceDocuments> restaurantList);

	void onLoadedExtraRestaurantList(String query, List<PlaceDocuments> restaurantList);
}
