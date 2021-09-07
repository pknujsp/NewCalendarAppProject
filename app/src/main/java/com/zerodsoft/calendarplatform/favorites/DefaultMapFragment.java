package com.zerodsoft.calendarplatform.favorites;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.naver.maps.map.NaverMap;
import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.event.foods.favorite.restaurant.FavoriteLocationViewModel;
import com.zerodsoft.calendarplatform.navermap.NaverMapFragment;

import org.jetbrains.annotations.NotNull;

public class DefaultMapFragment extends NaverMapFragment {

	@Override
	public void onAttach(@NonNull @NotNull Context context) {
		super.onAttach(context);
	}

	@Override
	public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		favoriteLocationViewModel = new ViewModelProvider(requireActivity()).get(FavoriteLocationViewModel.class);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.bottomNavigation.inflateMenu(R.menu.bottomnav_menu_in_event_info_fragment);
		binding.bottomNavigation.getMenu().findItem(R.id.event_info).setVisible(false);
		binding.bottomNavigation.getMenu().findItem(R.id.weathers_info).setVisible(false);
		binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
				onClickedBottomNav();

				switch (item.getItemId()) {
					case R.id.map_around:
						onClickedAroundMap();
						break;
					case R.id.restaurants:
						openRestaurantFragment(null);
						break;
				}
				return true;
			}

		});
		removeTooltipInBottomNav();
		binding.bottomNavigation.setSelected(false);
		loadMap();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onMapReady(@NonNull @NotNull NaverMap naverMap) {
		super.onMapReady(naverMap);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}
}
