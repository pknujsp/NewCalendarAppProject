package com.zerodsoft.scheduleweather.event.foods;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavArgs;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

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
import com.zerodsoft.scheduleweather.common.interfaces.OnHiddenFragmentListener;
import com.zerodsoft.scheduleweather.databinding.FragmentRestaurantMainTransactionBinding;
import com.zerodsoft.scheduleweather.event.foods.favorite.FavoritesMainFragment;
import com.zerodsoft.scheduleweather.event.foods.favorite.FavoritesMainFragmentArgs;
import com.zerodsoft.scheduleweather.event.foods.interfaces.FoodMenuChipsViewController;
import com.zerodsoft.scheduleweather.event.foods.interfaces.IGetEventValue;
import com.zerodsoft.scheduleweather.event.foods.main.FoodsMenuListFragmentArgs;
import com.zerodsoft.scheduleweather.event.foods.main.FoodsMenuListFragmentDirections;
import com.zerodsoft.scheduleweather.event.foods.main.RestaurantMainNavHostFragment;
import com.zerodsoft.scheduleweather.event.foods.search.search.fragment.SearchRestaurantFragment;
import com.zerodsoft.scheduleweather.event.foods.search.search.fragment.SearchRestaurantFragmentArgs;
import com.zerodsoft.scheduleweather.event.foods.settings.FoodsSettingsFragment;
import com.zerodsoft.scheduleweather.event.foods.settings.FoodsSettingsFragmentArgs;
import com.zerodsoft.scheduleweather.event.main.NewInstanceMainFragment;
import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.navermap.interfaces.FavoriteLocationsListener;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnExtraListDataListener;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

import lombok.val;


public class RestaurantDialogFragment extends BottomSheetDialogFragment implements IGetEventValue {
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
		/*
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
					Fragment primaryNavigationFragment = getChildFragmentManager().getPrimaryNavigationFragment();
					FragmentManager childFragmentManagerInPrimaryNavigationFragment = primaryNavigationFragment.getChildFragmentManager();

					if (childFragmentManagerInPrimaryNavigationFragment.getBackStackEntryCount() == 0) {
						dismiss();
					} else {
						childFragmentManagerInPrimaryNavigationFragment.popBackStack();
					}
				}
				return true;
			}
		});
		 */
		bottomSheetBehavior = ((BottomSheetDialog) dialog).getBehavior();
		bottomSheetBehavior.setDraggable(false);
		bottomSheetBehavior.setPeekHeight(0);
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

		NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(binding.navHostFragment.getId());
		NavController navController = navHostFragment.getNavController();
		NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
		navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
			@Override
			public void onDestinationChanged(@NonNull @NotNull NavController controller, @NonNull @NotNull NavDestination destination
					, @Nullable @org.jetbrains.annotations.Nullable Bundle arguments) {
				NavArgs navArgs = null;
				switch (destination.getId()) {
					case R.id.restaurant_main_nav_graph:
						navArgs = new FoodsMenuListFragmentArgs.Builder(iMapPoint,
								favoriteLocationsListener, foodMenuChipsViewController).build();
						arguments = ((FoodsMenuListFragmentArgs) navArgs).toBundle();
						break;

					case R.id.restaurant_favorites_nav_graph:
						navArgs = new FavoritesMainFragmentArgs.Builder(iMapPoint,
								foodMenuChipsViewController, favoriteLocationsListener).build();
						arguments = ((FavoritesMainFragmentArgs) navArgs).toBundle();
						break;

					case R.id.restaurant_search_nav_graph:
						navArgs = new SearchRestaurantFragmentArgs.Builder(iMapPoint,
								foodMenuChipsViewController, favoriteLocationsListener).build();
						arguments = ((SearchRestaurantFragmentArgs) navArgs).toBundle();
						break;

					case R.id.restaurant_settings_nav_graph:
						navArgs = new FoodsSettingsFragmentArgs.Builder(iMapPoint,
								foodMenuChipsViewController, favoriteLocationsListener).build();
						arguments = ((FoodsSettingsFragmentArgs) navArgs).toBundle();
						break;
					default:
						assert (false) : "Unknown";
				}
			}
		});

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}


	@Override
	public long getEventId() {
		return EVENT_ID;
	}

	@Override
	public int getCalendarId() {
		return CALENDAR_ID;
	}


}