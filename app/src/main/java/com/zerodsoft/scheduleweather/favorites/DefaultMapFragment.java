package com.zerodsoft.scheduleweather.favorites;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.naver.maps.map.NaverMap;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.OnHiddenFragmentListener;
import com.zerodsoft.scheduleweather.event.foods.RestaurantFragment;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteLocationViewModel;
import com.zerodsoft.scheduleweather.event.main.NewInstanceMainFragment;
import com.zerodsoft.scheduleweather.event.weather.fragment.WeatherMainFragment;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.NaverMapFragment;

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
