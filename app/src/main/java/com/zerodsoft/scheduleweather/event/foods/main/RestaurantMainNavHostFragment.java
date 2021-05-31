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

import com.zerodsoft.scheduleweather.databinding.FragmentRestaurantMainNavHostBinding;
import com.zerodsoft.scheduleweather.event.foods.RestaurantDialogFragment;
import com.zerodsoft.scheduleweather.event.foods.interfaces.FoodMenuChipsViewController;
import com.zerodsoft.scheduleweather.event.foods.interfaces.IGetEventValue;
import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.navermap.interfaces.FavoriteLocationsListener;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapPoint;

public class RestaurantMainNavHostFragment extends Fragment {
	public static final String TAG = "RestaurantMainNavHostFragment";
	private FoodMenuChipsViewController foodMenuChipsViewController;
	private FavoriteLocationsListener favoriteLocationsListener;
	private IGetEventValue iGetEventValue;
	private IMapPoint iMapPoint;

	private FragmentRestaurantMainNavHostBinding binding;

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
	
}