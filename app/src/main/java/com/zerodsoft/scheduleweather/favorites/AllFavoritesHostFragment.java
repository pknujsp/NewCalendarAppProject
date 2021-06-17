package com.zerodsoft.scheduleweather.favorites;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentAllFavoritesHostBinding;
import com.zerodsoft.scheduleweather.event.foods.favorite.RestaurantFavoritesHostFragment;
import com.zerodsoft.scheduleweather.event.foods.main.RestaurantMainHostFragment;
import com.zerodsoft.scheduleweather.event.foods.search.RestaurantSearchHostFragment;
import com.zerodsoft.scheduleweather.event.foods.settings.RestaurantSettingsHostFragment;

import org.jetbrains.annotations.NotNull;

public class AllFavoritesHostFragment extends Fragment {
	private FragmentAllFavoritesHostBinding binding;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentAllFavoritesHostBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		binding.bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
		binding.bottomNavigation.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
			@Override
			public void onNavigationItemReselected(@NonNull @NotNull MenuItem item) {

			}
		});

		onNavigationItemSelectedListener.onNavigationItemSelected(binding.bottomNavigation.getMenu().getItem(0));
	}

	private final BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
		@Override
		public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
			FragmentManager fragmentManager = getChildFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

			Fragment foregroundFragment = fragmentManager.getPrimaryNavigationFragment();
			if (foregroundFragment != null) {
				fragmentTransaction.hide(foregroundFragment);
			}

			final String tag = item.getTitle().toString();
			Fragment destinationFragment = fragmentManager.findFragmentByTag(tag);

			if (destinationFragment == null) {
				switch (item.getItemId()) {
					case R.id.addresses_places:
						destinationFragment = new AllFavoriteAddressPlaceFragment();
						break;
					case R.id.restaurants:
						destinationFragment = new AllFavoriteRestaurantFragment();
						break;
				}

				fragmentTransaction.add(binding.fragmentContainer.getId(), destinationFragment, tag);
			} else {
				fragmentTransaction.show(destinationFragment);
			}

			fragmentTransaction.setPrimaryNavigationFragment(destinationFragment).commitNow();
			return true;
		}
	};
}