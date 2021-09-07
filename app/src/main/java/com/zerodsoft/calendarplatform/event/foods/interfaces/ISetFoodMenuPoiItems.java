package com.zerodsoft.calendarplatform.event.foods.interfaces;

import com.zerodsoft.calendarplatform.event.main.NewInstanceMainFragment;
import com.zerodsoft.calendarplatform.navermap.interfaces.OnExtraListDataListener;

import java.io.Serializable;

public interface ISetFoodMenuPoiItems extends Serializable {
	void createRestaurantPoiItems(NewInstanceMainFragment.RestaurantsGetter restaurantsGetter, OnExtraListDataListener<Integer> onExtraListDataListener);

	void createCriteriaLocationMarker(String name, String latitude, String longitude);

	void removeRestaurantPoiItems();

	void onChangeFoodMenu();
}