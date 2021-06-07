package com.zerodsoft.scheduleweather.event.foods.interfaces;

import com.zerodsoft.scheduleweather.event.main.NewInstanceMainFragment;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnExtraListDataListener;

import java.io.Serializable;

public interface ISetFoodMenuPoiItems extends Serializable {
	void createRestaurantPoiItems(NewInstanceMainFragment.RestaurantsGetter restaurantsGetter, OnExtraListDataListener<Integer> onExtraListDataListener);

	void removeRestaurantPoiItems();

	void onChangeFoodMenu();
}