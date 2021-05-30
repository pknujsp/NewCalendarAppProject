package com.zerodsoft.scheduleweather.event.foods.favorite;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.databinding.FragmentFavoritesMainBinding;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteRestaurantFragment;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.navermap.interfaces.FavoriteLocationsListener;


public class FavoritesMainFragment extends Fragment {
	public static final String TAG = "FavoritesMainFragment";
	private FragmentFavoritesMainBinding binding;
	private final BottomSheetController bottomSheetController;
	private final FavoriteLocationsListener favoriteLocationsListener;

	public FavoritesMainFragment(BottomSheetController bottomSheetController, FavoriteLocationsListener favoriteLocationsListener) {
		this.bottomSheetController = bottomSheetController;
		this.favoriteLocationsListener = favoriteLocationsListener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentFavoritesMainBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		FavoriteRestaurantFragment favoriteRestaurantFragment = new FavoriteRestaurantFragment(favoriteLocationsListener);
		getChildFragmentManager().beginTransaction().add(binding.favoritesFragmentContainer.getId(),
				favoriteRestaurantFragment, FavoriteRestaurantFragment.TAG).commit();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (hidden) {

		} else {
			//데이터 리스트 갱신
			((FavoriteRestaurantFragment) getChildFragmentManager().findFragmentByTag(FavoriteRestaurantFragment.TAG)).refreshList();
		}
	}
}