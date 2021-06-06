package com.zerodsoft.scheduleweather.event.foods.favorite;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentRestaurantFavoritesHostBinding;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteRestaurantFragment;

import org.jetbrains.annotations.NotNull;


public class RestaurantFavoritesHostFragment extends Fragment {
	private FragmentRestaurantFavoritesHostBinding binding;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentRestaurantFavoritesHostBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getChildFragmentManager().beginTransaction()
				.add(binding.fragmentContainer.getId(), new FavoriteRestaurantFragment()
						, getString(R.string.tag_favorite_restaurant_fragment))
				.commitNow();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		FragmentManager fragmentManager = getChildFragmentManager();
		if (fragmentManager.findFragmentByTag(getString(R.string.tag_favorite_restaurant_fragment)) != null) {
			fragmentManager.findFragmentByTag(getString(R.string.tag_favorite_restaurant_fragment)).onHiddenChanged(hidden);
		}
	}
}