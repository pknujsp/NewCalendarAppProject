package com.zerodsoft.scheduleweather.event.foods;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
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
import com.zerodsoft.scheduleweather.databinding.FragmentRestaurantMainTransactionBinding;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteLocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteRestaurantFragmentArgs;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteRestaurantFragmentDirections;
import com.zerodsoft.scheduleweather.event.foods.interfaces.FoodMenuChipsViewController;
import com.zerodsoft.scheduleweather.event.foods.interfaces.IGetEventValue;
import com.zerodsoft.scheduleweather.event.foods.main.FoodsMenuListFragmentArgs;
import com.zerodsoft.scheduleweather.event.foods.search.search.fragment.SearchRestaurantFragmentArgs;
import com.zerodsoft.scheduleweather.navermap.interfaces.FavoriteLocationsListener;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapPoint;

import org.jetbrains.annotations.NotNull;


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

	private NavHostFragment navHostFragment;

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
					NavController navController = navHostFragment.getNavController();
					if (!navController.popBackStack()) {
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

		navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(binding.navHostFragment.getId());
		NavController navController = navHostFragment.getNavController();

		FoodsMenuListFragmentArgs navArgs = new FoodsMenuListFragmentArgs.Builder(iMapPoint,
				favoriteLocationsListener, foodMenuChipsViewController, EVENT_ID).build();
		Bundle bundle = navArgs.toBundle();

		navController.setGraph(R.navigation.restaurant_root_nav_graph, bundle);
		NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
		binding.bottomNavigation.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
			@Override
			public void onNavigationItemReselected(@NonNull @NotNull MenuItem item) {

			}
		});
		binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
				final int id = item.getItemId();
				bundle.clear();

				switch (id) {
					case R.id.restaurant_main_nav_graph:
						break;
					case R.id.restaurant_favorites_nav_graph:
						bundle.putAll(new FavoriteRestaurantFragmentArgs.Builder(favoriteLocationsListener).build().toBundle());
						break;
					case R.id.restaurant_search_nav_graph:
						bundle.putAll(new SearchRestaurantFragmentArgs.Builder(iMapPoint, foodMenuChipsViewController,
								favoriteLocationsListener).build().toBundle());
						break;
					case R.id.restaurant_settings_nav_graph:
						break;
				}
				navController.navigate(id, bundle);
				return true;
			}
		});

		/*
		navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
			@Override
			public void onDestinationChanged(@NonNull @NotNull NavController controller, @NonNull @NotNull NavDestination destination, @Nullable @org.jetbrains.annotations.Nullable Bundle arguments) {
				//bottom navigation btn을 누르면 호출되고 이후 프래그먼트 뷰가 초기화 된다
				//id값으로는 startDestination id가 온다
				final int id = destination.getId();
			}
		});

		 */

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