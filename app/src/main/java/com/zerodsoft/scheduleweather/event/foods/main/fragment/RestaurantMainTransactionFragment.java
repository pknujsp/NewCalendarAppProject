package com.zerodsoft.scheduleweather.event.foods.main.fragment;

import android.net.ConnectivityManager;
import android.net.Network;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.common.interfaces.OnHiddenFragmentListener;
import com.zerodsoft.scheduleweather.databinding.FragmentRestaurantMainTransactionBinding;
import com.zerodsoft.scheduleweather.event.foods.favorite.FavoritesMainFragment;
import com.zerodsoft.scheduleweather.event.foods.search.search.fragment.SearchRestaurantFragment;
import com.zerodsoft.scheduleweather.event.foods.settings.FoodsSettingsFragment;
import com.zerodsoft.scheduleweather.event.main.NewInstanceMainFragment;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.navermap.interfaces.FavoriteLocationsListener;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.navermap.interfaces.INetwork;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnExtraListDataListener;
import com.zerodsoft.scheduleweather.utility.NetworkStatus;

import java.util.List;


public class RestaurantMainTransactionFragment extends Fragment implements INetwork,
		BottomNavigationView.OnNavigationItemSelectedListener, OnBackPressedCallbackController, RestaurantMainFragment.IGetEventValue {
	public static final String TAG = "RestaurantMainTransactionFragment";
	private FragmentRestaurantMainTransactionBinding binding;

	private NetworkStatus networkStatus;
	private Fragment currentShowingFragment;

	private final int CALENDAR_ID;
	private final long INSTANCE_ID;
	private final long EVENT_ID;
	private final BottomSheetController bottomSheetController;
	private final FoodMenuChipsViewController foodMenuChipsViewController;
	private final FavoriteLocationsListener favoriteLocationsListener;
	private final IMapPoint iMapPoint;

	private final OnBackPressedCallbackController mainFragmentOnBackPressedCallbackController;

	public RestaurantMainTransactionFragment(IMapPoint iMapPoint, BottomSheetController bottomSheetController,
	                                         OnBackPressedCallbackController onBackPressedCallbackController
			, FoodMenuChipsViewController foodMenuChipsViewController
			, FavoriteLocationsListener favoriteLocationsListener, int CALENDAR_ID, long INSTANCE_ID, long EVENT_ID) {
		this.iMapPoint = iMapPoint;
		this.bottomSheetController = bottomSheetController;
		this.mainFragmentOnBackPressedCallbackController = onBackPressedCallbackController;
		this.foodMenuChipsViewController = foodMenuChipsViewController;
		this.favoriteLocationsListener = favoriteLocationsListener;
		this.CALENDAR_ID = CALENDAR_ID;
		this.INSTANCE_ID = INSTANCE_ID;
		this.EVENT_ID = EVENT_ID;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (hidden) {
			removeOnBackPressedCallback();
			if (getParentFragmentManager().getBackStackEntryCount() == 0) {
				mainFragmentOnBackPressedCallbackController.addOnBackPressedCallback();
			}
			bottomSheetController.setStateOfBottomSheet(BottomSheetType.RESTAURANT, BottomSheetBehavior.STATE_COLLAPSED);
		} else {
			addOnBackPressedCallback();
			if (getParentFragmentManager().getBackStackEntryCount() == 0) {
				mainFragmentOnBackPressedCallbackController.removeOnBackPressedCallback();
			}
			bottomSheetController.setStateOfBottomSheet(BottomSheetType.RESTAURANT, BottomSheetBehavior.STATE_EXPANDED);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		networkStatus = new NetworkStatus(getContext(), new ConnectivityManager.NetworkCallback() {
			@Override
			public void onAvailable(@NonNull Network network) {
				super.onAvailable(network);
			}

			@Override
			public void onLost(@NonNull Network network) {
				super.onLost(network);
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						requireActivity().finish();
					}
				});
			}
		});
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

		binding.bottomNavigation.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
		binding.bottomNavigation.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
			@Override
			public void onNavigationItemReselected(@NonNull MenuItem item) {

			}
		});
		setInitFragments();
	}

	private void setInitFragments() {
		RestaurantMainFragment restaurantMainFragment = new RestaurantMainFragment(this, this, bottomSheetController,
				foodMenuChipsViewController, favoriteLocationsListener, iMapPoint);

		SearchRestaurantFragment searchRestaurantFragment = new SearchRestaurantFragment(bottomSheetController, favoriteLocationsListener);
		FoodsSettingsFragment foodsSettingsFragment = new FoodsSettingsFragment(bottomSheetController, favoriteLocationsListener);
		FavoritesMainFragment favoritesMainFragment = new FavoritesMainFragment(bottomSheetController, favoriteLocationsListener);

		getChildFragmentManager().beginTransaction()
				.add(binding.fragmentContainer.getId(), restaurantMainFragment, RestaurantMainFragment.TAG)
				.add(binding.fragmentContainer.getId(), searchRestaurantFragment, SearchRestaurantFragment.TAG)
				.add(binding.fragmentContainer.getId(), foodsSettingsFragment, FoodsSettingsFragment.TAG)
				.add(binding.fragmentContainer.getId(), favoritesMainFragment, FavoritesMainFragment.TAG)
				.setPrimaryNavigationFragment(restaurantMainFragment)
				.hide(foodsSettingsFragment)
				.hide(searchRestaurantFragment)
				.hide(favoritesMainFragment)
				.commitNow();

		currentShowingFragment = restaurantMainFragment;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		networkStatus.unregisterNetworkCallback();
	}

	@Override
	public boolean networkAvailable() {
		return networkStatus.networkAvailable();
	}


	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		FragmentManager fragmentManager = getChildFragmentManager();
		Fragment newFragment = null;

		switch (item.getItemId()) {
			case R.id.food_main: {
				newFragment = fragmentManager.findFragmentByTag(RestaurantMainFragment.TAG);
				break;
			}

			case R.id.food_favorite_restaurant: {
				newFragment = fragmentManager.findFragmentByTag(FavoritesMainFragment.TAG);
				break;
			}

			case R.id.food_search: {
				newFragment = fragmentManager.findFragmentByTag(SearchRestaurantFragment.TAG);
				break;
			}

			case R.id.food_settings: {
				newFragment = fragmentManager.findFragmentByTag(FoodsSettingsFragment.TAG);
				break;
			}
		}

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.hide(currentShowingFragment).show(newFragment).setPrimaryNavigationFragment(newFragment)
				.commitNow();

		currentShowingFragment = newFragment;
		return true;
	}

	@Override
	public void addOnBackPressedCallback() {
		//bottomsheet가 확장될때 호출되고, 현재 표시중인 프래그먼트의 onbackpressed를 활성화한다.
		Fragment fragment = getChildFragmentManager().getPrimaryNavigationFragment();

		if (fragment instanceof RestaurantMainFragment) {
			((RestaurantMainFragment) fragment).addOnBackPressedCallback();
		} else if (fragment instanceof FavoritesMainFragment) {
			((FavoritesMainFragment) fragment).addOnBackPressedCallback();
		} else if (fragment instanceof SearchRestaurantFragment) {
			((SearchRestaurantFragment) fragment).addOnBackPressedCallback();
		} else if (fragment instanceof FoodsSettingsFragment) {
			((FoodsSettingsFragment) fragment).addOnBackPressedCallback();
		}
	}

	@Override
	public void removeOnBackPressedCallback() {
		FragmentManager fragmentManager = getChildFragmentManager();
		Fragment fragment = fragmentManager.getPrimaryNavigationFragment();

		if (fragment instanceof RestaurantMainFragment) {
			((RestaurantMainFragment) fragment).removeOnBackPressedCallback();
		} else if (fragment instanceof FavoritesMainFragment) {
			((FavoritesMainFragment) fragment).removeOnBackPressedCallback();
		} else if (fragment instanceof SearchRestaurantFragment) {
			((SearchRestaurantFragment) fragment).removeOnBackPressedCallback();
		} else if (fragment instanceof FoodsSettingsFragment) {
			((FoodsSettingsFragment) fragment).removeOnBackPressedCallback();
		}
	}

	@Override
	public long getEventId() {
		return EVENT_ID;
	}

	@Override
	public int getCalendarId() {
		return CALENDAR_ID;
	}

	public interface FoodMenuChipsViewController {
		void createRestaurantListView(List<String> foodMenuList, NewInstanceMainFragment.RestaurantsGetter restaurantsGetter, OnExtraListDataListener<String> onExtraListDataListener, OnHiddenFragmentListener onHiddenFragmentListener);

		void removeRestaurantListView();

		void createFoodMenuChips();

		void setFoodMenuChips(List<String> foodMenuList);

		void addFoodMenuListChip();

		void setCurrentFoodMenuName(String foodMenuName);
	}
}