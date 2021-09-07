package com.zerodsoft.calendarplatform.event.foods.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zerodsoft.calendarplatform.event.foods.dto.FoodCategoryItem;
import com.zerodsoft.calendarplatform.event.foods.main.RestaurantListFragment;
import com.zerodsoft.calendarplatform.event.foods.share.CriteriaLocationCloud;

import java.util.ArrayList;
import java.util.List;

public class FoodCategoryFragmentListAdapter extends FragmentStateAdapter {
	private List<RestaurantListFragment> fragments = new ArrayList<>();

	public FoodCategoryFragmentListAdapter(@NonNull Fragment fragment) {
		super(fragment);
	}

	public List<RestaurantListFragment> getFragments() {
		return fragments;
	}

	public void init(List<FoodCategoryItem> foodCategoryItemList) {
		for (FoodCategoryItem foodCategoryItem : foodCategoryItemList) {
			RestaurantListFragment fragment = new RestaurantListFragment();
			Bundle bundle = new Bundle();
			bundle.putString("query", foodCategoryItem.getCategoryName());
			bundle.putString("criteriaLatitude", CriteriaLocationCloud.getLatitude());
			bundle.putString("criteriaLongitude", CriteriaLocationCloud.getLongitude());

			fragment.setArguments(bundle);
			fragments.add(fragment);
		}

	}

	@NonNull
	@Override
	public Fragment createFragment(int position) {
		return fragments.get(position);
	}

	@Override
	public int getItemCount() {
		return fragments.size();
	}

}
