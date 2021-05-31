package com.zerodsoft.scheduleweather.event.foods.interfaces;

import com.zerodsoft.scheduleweather.common.interfaces.OnHiddenFragmentListener;
import com.zerodsoft.scheduleweather.event.main.NewInstanceMainFragment;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnExtraListDataListener;

import java.io.Serializable;
import java.util.List;

public interface FoodMenuChipsViewController extends Serializable {
	void createRestaurantListView(List<String> foodMenuList, NewInstanceMainFragment.RestaurantsGetter restaurantsGetter, OnExtraListDataListener<String> onExtraListDataListener, OnHiddenFragmentListener onHiddenFragmentListener);

	void removeRestaurantListView();

	void createFoodMenuChips();

	void setFoodMenuChips(List<String> foodMenuList);

	void addFoodMenuListChip();

	void setCurrentFoodMenuName(String foodMenuName);
}