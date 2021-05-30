package com.zerodsoft.scheduleweather.event.foods.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.databinding.FragmentRestaurantMainBinding;
import com.zerodsoft.scheduleweather.databinding.FragmentRestaurantMainNavHostBinding;
import com.zerodsoft.scheduleweather.event.foods.RestaurantDialogFragment;
import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.navermap.interfaces.FavoriteLocationsListener;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapPoint;

public class RestaurantMainNavHostFragment extends NavHostFragment {
	public static final String TAG = "FoodsHomeFragment";
	private final BottomSheetController bottomSheetController;
	private final RestaurantDialogFragment.FoodMenuChipsViewController foodMenuChipsViewController;
	private FragmentRestaurantMainNavHostBinding binding;
	private final FavoriteLocationsListener favoriteLocationsListener;
	private final IGetEventValue iGetEventValue;
	private final IMapPoint iMapPoint;

	public RestaurantMainNavHostFragment(IGetEventValue iGetEventValue, BottomSheetController bottomSheetController,
	                                     RestaurantDialogFragment.FoodMenuChipsViewController foodMenuChipsViewController, FavoriteLocationsListener favoriteLocationsListener, IMapPoint iMapPoint) {
		this.iGetEventValue = iGetEventValue;
		this.bottomSheetController = bottomSheetController;
		this.foodMenuChipsViewController = foodMenuChipsViewController;
		this.favoriteLocationsListener = favoriteLocationsListener;
		this.iMapPoint = iMapPoint;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentRestaurantMainNavHostBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			FragmentManager fragmentManager = getChildFragmentManager();
			Fragment primaryNavigationFragment = fragmentManager.getPrimaryNavigationFragment();

			if (primaryNavigationFragment instanceof RestaurantListTabFragment) {
				((RestaurantListTabFragment) primaryNavigationFragment).onHiddenChanged(false);
			}
		}
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		FoodsMenuListFragment foodsMenuListFragment = new FoodsMenuListFragment(iGetEventValue, foodMenuChipsViewController,
				bottomSheetController, favoriteLocationsListener, iMapPoint);
		foodsMenuListFragment.setArguments(getArguments());

		getChildFragmentManager().beginTransaction().add(binding.foodsMainFragmentContainer.getId(), foodsMenuListFragment, FoodsMenuListFragment.TAG)
				.setPrimaryNavigationFragment(foodsMenuListFragment)
				.commitNow();
	}


	public interface IGetEventValue {
		long getEventId();

		int getCalendarId();
	}
}