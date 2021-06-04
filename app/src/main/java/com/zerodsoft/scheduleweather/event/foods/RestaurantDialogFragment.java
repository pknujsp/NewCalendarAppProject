package com.zerodsoft.scheduleweather.event.foods;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentRestaurantMainTransactionBinding;
import com.zerodsoft.scheduleweather.event.foods.favorite.RestaurantFavoritesHostFragment;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteLocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.interfaces.FoodMenuChipsViewController;
import com.zerodsoft.scheduleweather.event.foods.interfaces.IGetEventValue;
import com.zerodsoft.scheduleweather.event.foods.main.RestaurantMainHostFragment;
import com.zerodsoft.scheduleweather.event.foods.search.RestaurantSearchHostFragment;
import com.zerodsoft.scheduleweather.event.foods.settings.RestaurantSettingsHostFragment;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.RestaurantSharedViewModel;
import com.zerodsoft.scheduleweather.navermap.interfaces.FavoriteLocationsListener;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapPoint;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class RestaurantDialogFragment extends BottomSheetDialogFragment {
	public static final String TAG = "RestaurantDialogFragment";
	private FragmentRestaurantMainTransactionBinding binding;

	private final int CALENDAR_ID;
	private final long INSTANCE_ID;
	private final long EVENT_ID;
	private final int VIEW_HEIGHT;

	private final FoodMenuChipsViewController foodMenuChipsViewController;
	private final FavoriteLocationsListener favoriteLocationsListener;
	private final IMapPoint iMapPoint;

	private BottomSheetBehavior bottomSheetBehavior;

	public RestaurantDialogFragment(IMapPoint iMapPoint
			, FoodMenuChipsViewController foodMenuChipsViewController
			, FavoriteLocationsListener favoriteLocationsListener, int CALENDAR_ID, long INSTANCE_ID, long EVENT_ID, int VIEW_HEIGHT) {
		this.iMapPoint = iMapPoint;
		this.foodMenuChipsViewController = foodMenuChipsViewController;
		this.favoriteLocationsListener = favoriteLocationsListener;
		this.CALENDAR_ID = CALENDAR_ID;
		this.INSTANCE_ID = INSTANCE_ID;
		this.EVENT_ID = EVENT_ID;
		this.VIEW_HEIGHT = VIEW_HEIGHT;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
					FragmentManager fragmentManager = getChildFragmentManager().getPrimaryNavigationFragment().getChildFragmentManager();
					if (!fragmentManager.popBackStackImmediate()) {
						dismiss();
					}
				}
				return true;
			}
		});

		bottomSheetBehavior = ((BottomSheetDialog) dialog).getBehavior();
		bottomSheetBehavior.setDraggable(false);
		bottomSheetBehavior.setPeekHeight(0);
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		RestaurantSharedViewModel restaurantSharedViewModel =
				new ViewModelProvider(requireActivity()).get(RestaurantSharedViewModel.class);
		restaurantSharedViewModel.setFavoriteLocationsListener(favoriteLocationsListener);
		restaurantSharedViewModel.setFoodMenuChipsViewController(foodMenuChipsViewController);
		restaurantSharedViewModel.setiMapPoint(iMapPoint);
		restaurantSharedViewModel.setEventId(EVENT_ID);
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

		View bottomSheet = getDialog().findViewById(R.id.design_bottom_sheet);
		bottomSheet.getLayoutParams().height = VIEW_HEIGHT;
		
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

			fragmentTransaction.setPrimaryNavigationFragment(destinationFragment).commit();
			return true;

		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}