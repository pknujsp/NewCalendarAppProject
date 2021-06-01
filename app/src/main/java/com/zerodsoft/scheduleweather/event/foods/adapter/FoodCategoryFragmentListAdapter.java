package com.zerodsoft.scheduleweather.event.foods.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zerodsoft.scheduleweather.event.foods.main.RestaurantListTabFragment;
import com.zerodsoft.scheduleweather.event.foods.main.RestaurantListFragment;
import com.zerodsoft.scheduleweather.navermap.interfaces.FavoriteLocationsListener;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteLocationQuery;

import java.util.ArrayList;
import java.util.List;

public class FoodCategoryFragmentListAdapter extends FragmentStateAdapter implements RestaurantListTabFragment.RefreshFavoriteState {
	private List<RestaurantListFragment> fragments;
	private List<String> categoryList;
	private FavoriteLocationQuery favoriteLocationQuery;
	private FavoriteLocationsListener favoriteLocationsListener;

	public FoodCategoryFragmentListAdapter(@NonNull Fragment fragment) {
		super(fragment);
	}

	public List<RestaurantListFragment> getFragments() {
		return fragments;
	}

	public void init(FavoriteLocationQuery favoriteLocationQuery, FavoriteLocationsListener favoriteLocationsListener, List<String> categoryList) {
		this.favoriteLocationQuery = favoriteLocationQuery;
		this.categoryList = categoryList;
		this.fragments = new ArrayList<>();

		for (String categoryName : categoryList) {
			RestaurantListFragment fragment = new RestaurantListFragment();
			Bundle bundle = new Bundle();
			bundle.putString("query", categoryName);
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

	@Override
	public void refreshFavorites() {
		for (RestaurantListFragment fragment : fragments) {
			fragment.refreshFavorites();
		}
	}

}
