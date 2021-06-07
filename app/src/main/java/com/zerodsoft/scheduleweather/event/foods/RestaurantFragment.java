package com.zerodsoft.scheduleweather.event.foods;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentRestaurantMainTransactionBinding;
import com.zerodsoft.scheduleweather.event.foods.favorite.RestaurantFavoritesHostFragment;
import com.zerodsoft.scheduleweather.event.foods.interfaces.FoodMenuChipsViewController;
import com.zerodsoft.scheduleweather.event.foods.main.RestaurantMainHostFragment;
import com.zerodsoft.scheduleweather.event.foods.search.RestaurantSearchHostFragment;
import com.zerodsoft.scheduleweather.event.foods.settings.RestaurantSettingsHostFragment;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.CustomFoodMenuViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationHistoryViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.RestaurantSharedViewModel;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapPoint;

import org.jetbrains.annotations.NotNull;


public class RestaurantFragment extends Fragment {
	public static final String TAG = "RestaurantFragment";
	private FragmentRestaurantMainTransactionBinding binding;

	private final int CALENDAR_ID;
	private final long INSTANCE_ID;
	private final long EVENT_ID;

	private final FoodMenuChipsViewController foodMenuChipsViewController;
	private final IMapPoint iMapPoint;

	private RestaurantSharedViewModel restaurantSharedViewModel;

	private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
		@Override
		public void handleOnBackPressed() {
			FragmentManager fragmentManager = getChildFragmentManager().getPrimaryNavigationFragment().getChildFragmentManager();
			if (!fragmentManager.popBackStackImmediate()) {
				getParentFragmentManager().popBackStack();
			}
		}
	};


	public RestaurantFragment(IMapPoint iMapPoint
			, FoodMenuChipsViewController foodMenuChipsViewController
			, int CALENDAR_ID, long INSTANCE_ID, long EVENT_ID) {
		this.iMapPoint = iMapPoint;
		this.foodMenuChipsViewController = foodMenuChipsViewController;
		this.CALENDAR_ID = CALENDAR_ID;
		this.INSTANCE_ID = INSTANCE_ID;
		this.EVENT_ID = EVENT_ID;
	}

	public FragmentRestaurantMainTransactionBinding getBinding() {
		return binding;
	}

	@Override
	public void onAttach(@NonNull @NotNull Context context) {
		super.onAttach(context);
		requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		restaurantSharedViewModel =
				new ViewModelProvider(requireActivity()).get(RestaurantSharedViewModel.class);
		restaurantSharedViewModel.setFoodMenuChipsViewController(foodMenuChipsViewController);
		restaurantSharedViewModel.setiMapPoint(iMapPoint);
		restaurantSharedViewModel.setEventId(EVENT_ID);

		new ViewModelProvider(requireActivity()).get(FoodCriteriaLocationInfoViewModel.class);
		new ViewModelProvider(requireActivity()).get(FoodCriteriaLocationHistoryViewModel.class);
		new ViewModelProvider(requireActivity()).get(CustomFoodMenuViewModel.class);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentRestaurantMainTransactionBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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
		@SuppressLint("NonConstantResourceId")
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
					case R.id.main:
						destinationFragment = new RestaurantMainHostFragment();
						break;
					case R.id.favorites:
						destinationFragment = new RestaurantFavoritesHostFragment();
						break;
					case R.id.search:
						destinationFragment = new RestaurantSearchHostFragment();
						break;
					case R.id.settings:
						destinationFragment = new RestaurantSettingsHostFragment();
						fragmentTransaction = null;
						return false;
				}

				fragmentTransaction.add(binding.fragmentContainer.getId(), destinationFragment, tag);
			} else {
				fragmentTransaction.show(destinationFragment);
			}

			fragmentTransaction.setPrimaryNavigationFragment(destinationFragment).commitNow();
			return true;
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		onBackPressedCallback.remove();
	}
}