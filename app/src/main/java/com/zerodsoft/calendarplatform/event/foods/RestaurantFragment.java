package com.zerodsoft.calendarplatform.event.foods;

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
import android.widget.RelativeLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.common.interfaces.OnHiddenFragmentListener;
import com.zerodsoft.calendarplatform.databinding.FragmentRestaurantMainTransactionBinding;
import com.zerodsoft.calendarplatform.event.foods.favorite.RestaurantFavoritesHostFragment;
import com.zerodsoft.calendarplatform.event.foods.interfaces.ISetFoodMenuPoiItems;
import com.zerodsoft.calendarplatform.event.foods.interfaces.IOnSetView;
import com.zerodsoft.calendarplatform.event.foods.main.RestaurantMainHostFragment;
import com.zerodsoft.calendarplatform.event.foods.search.RestaurantSearchHostFragment;
import com.zerodsoft.calendarplatform.event.foods.share.CriteriaLocationCloud;
import com.zerodsoft.calendarplatform.event.foods.viewmodel.FoodCriteriaLocationHistoryViewModel;
import com.zerodsoft.calendarplatform.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;
import com.zerodsoft.calendarplatform.event.foods.viewmodel.RestaurantSharedViewModel;

import org.jetbrains.annotations.NotNull;

public class RestaurantFragment extends Fragment implements IOnSetView {
	private final Long EVENT_ID;
	private final ISetFoodMenuPoiItems ISetFoodMenuPoiItems;
	private OnHiddenFragmentListener onHiddenFragmentListener;

	private FragmentRestaurantMainTransactionBinding binding;
	private RestaurantSharedViewModel restaurantSharedViewModel;
	private FoodCriteriaLocationInfoViewModel foodCriteriaLocationInfoViewModel;
	private FoodCriteriaLocationHistoryViewModel foodCriteriaLocationHistoryViewModel;


	public RestaurantFragment(ISetFoodMenuPoiItems ISetFoodMenuPoiItems
			, OnHiddenFragmentListener onHiddenFragmentListener, @Nullable Long EVENT_ID) {
		this.ISetFoodMenuPoiItems = ISetFoodMenuPoiItems;
		this.onHiddenFragmentListener = onHiddenFragmentListener;
		this.EVENT_ID = EVENT_ID;
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
				new ViewModelProvider(this).get(RestaurantSharedViewModel.class);
		restaurantSharedViewModel.setISetFoodMenuPoiItems(ISetFoodMenuPoiItems);
		restaurantSharedViewModel.setEventId(EVENT_ID);

		foodCriteriaLocationInfoViewModel = new ViewModelProvider(requireActivity()).get(FoodCriteriaLocationInfoViewModel.KEY,
				FoodCriteriaLocationInfoViewModel.class);
		foodCriteriaLocationHistoryViewModel =
				new ViewModelProvider(requireActivity()).get(FoodCriteriaLocationHistoryViewModel.KEY,
						FoodCriteriaLocationHistoryViewModel.class);
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		CriteriaLocationCloud.clear();
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
	public void setFragmentContainerVisibility(ViewType viewType, int visibility) {
		switch (viewType) {
			case CONTENT:
				binding.bottomNavigation.setVisibility(visibility);
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.fragmentContainer.getLayoutParams();

				if (visibility == View.VISIBLE) {
					layoutParams.height = 0;
					layoutParams.addRule(RelativeLayout.ABOVE, binding.bottomNavigation.getId());
				} else {
					layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
					layoutParams.removeRule(RelativeLayout.ABOVE);
				}
				break;
		}
	}

	@Override
	public void setFragmentContainerHeight(int height) {

	}

	private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
		@Override
		public void handleOnBackPressed() {
			Fragment primaryNavFragment = getChildFragmentManager().getPrimaryNavigationFragment();
			FragmentManager fragmentManager = primaryNavFragment.getChildFragmentManager();
			if (!fragmentManager.popBackStackImmediate()) {
				getParentFragmentManager().popBackStack();
			}
		}
	};

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		onHiddenFragmentListener.onHiddenChangedFragment(hidden);

		if (hidden) {
			onBackPressedCallback.remove();
		} else {
			requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
		}

	}
}