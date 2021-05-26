package com.zerodsoft.scheduleweather.event.foods.main.fragment;

import android.content.Context;
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
import com.zerodsoft.scheduleweather.databinding.FragmentRestaurantMainBinding;
import com.zerodsoft.scheduleweather.event.foods.categorylist.RestaurantListTabFragment;
import com.zerodsoft.scheduleweather.event.foods.categorylist.FoodsMenuListFragment;
import com.zerodsoft.scheduleweather.event.foods.criterialocation.RestaurantCriteriaLocationSettingsFragment;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.navermap.interfaces.FavoriteLocationsListener;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.navermap.interfaces.INetwork;

public class RestaurantMainFragment extends Fragment implements OnBackPressedCallbackController {
	public static final String TAG = "FoodsHomeFragment";
	private final BottomSheetController bottomSheetController;
	private final RestaurantMainTransactionFragment.FoodMenuChipsViewController foodMenuChipsViewController;
	private FragmentRestaurantMainBinding binding;
	private final INetwork iNetwork;
	private final FavoriteLocationsListener favoriteLocationsListener;
	private final IGetEventValue iGetEventValue;
	private final IMapPoint iMapPoint;

	private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
		@Override
		public void handleOnBackPressed() {
			FragmentManager fragmentManager = getChildFragmentManager();

			if (fragmentManager.findFragmentByTag(FoodsMenuListFragment.TAG) != null) {
				if (fragmentManager.findFragmentByTag(FoodsMenuListFragment.TAG).isVisible()) {
					getParentFragment().getParentFragmentManager().popBackStack();
					bottomSheetController.setStateOfBottomSheet(BottomSheetType.RESTAURANT, BottomSheetBehavior.STATE_COLLAPSED);
				} else if (fragmentManager.findFragmentByTag(RestaurantListTabFragment.TAG) != null) {
					fragmentManager.popBackStackImmediate();
				} else if (fragmentManager.findFragmentByTag(RestaurantCriteriaLocationSettingsFragment.TAG) != null) {
					if (((RestaurantCriteriaLocationSettingsFragment) fragmentManager.findFragmentByTag(RestaurantCriteriaLocationSettingsFragment.TAG)).checkChangeBeforeClose()) {
						fragmentManager.popBackStackImmediate();
					}
				}
			}
		}
	};

	public RestaurantMainFragment(INetwork iNetwork, IGetEventValue iGetEventValue, BottomSheetController bottomSheetController,
	                              RestaurantMainTransactionFragment.FoodMenuChipsViewController foodMenuChipsViewController, FavoriteLocationsListener favoriteLocationsListener, IMapPoint iMapPoint) {
		this.iNetwork = iNetwork;
		this.iGetEventValue = iGetEventValue;
		this.bottomSheetController = bottomSheetController;
		this.foodMenuChipsViewController = foodMenuChipsViewController;
		this.favoriteLocationsListener = favoriteLocationsListener;
		this.iMapPoint = iMapPoint;
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		addOnBackPressedCallback();
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentRestaurantMainBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		FoodsMenuListFragment foodsMenuListFragment = new FoodsMenuListFragment(iGetEventValue, iNetwork, foodMenuChipsViewController,
				bottomSheetController, favoriteLocationsListener, iMapPoint);
		foodsMenuListFragment.setArguments(getArguments());

		getChildFragmentManager().beginTransaction().add(binding.foodsMainFragmentContainer.getId(), foodsMenuListFragment, FoodsMenuListFragment.TAG)
				.commit();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		Fragment foodCategoryTabFragment = getChildFragmentManager().findFragmentByTag(RestaurantListTabFragment.TAG);

		if (hidden) {
			removeOnBackPressedCallback();
			if (foodCategoryTabFragment != null) {
				getChildFragmentManager().beginTransaction().hide(foodCategoryTabFragment)
						.commitNow();
			}
		} else {
			addOnBackPressedCallback();
			if (foodCategoryTabFragment != null) {
				getChildFragmentManager().beginTransaction().show(foodCategoryTabFragment)
						.commitNow();
			}
		}
	}

	@Override
	public void addOnBackPressedCallback() {
		requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
	}

	@Override
	public void removeOnBackPressedCallback() {
		onBackPressedCallback.remove();
	}


	public interface IGetEventValue {
		long getEventId();

		int getCalendarId();
	}
}