package com.zerodsoft.scheduleweather.favorites;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
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
import com.zerodsoft.scheduleweather.common.classes.CloseWindow;
import com.zerodsoft.scheduleweather.databinding.FragmentAllFavoritesHostBinding;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteLocationViewModel;
import com.zerodsoft.scheduleweather.favorites.restaurant.AllFavoriteRestaurantHostFragment;
import com.zerodsoft.scheduleweather.utility.NetworkStatus;

import org.jetbrains.annotations.NotNull;

public class AllFavoritesHostFragment extends Fragment {
	private FragmentAllFavoritesHostBinding binding;
	private NetworkStatus networkStatus;
	private CloseWindow closeWindow = new CloseWindow(new CloseWindow.OnBackKeyDoubleClickedListener() {
		@Override
		public void onDoubleClicked() {
			getParentFragmentManager().popBackStackImmediate();
		}
	});

	private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
		@Override
		public void handleOnBackPressed() {
			Fragment primaryNavFragment = getChildFragmentManager().getPrimaryNavigationFragment();
			FragmentManager fragmentManager = primaryNavFragment.getChildFragmentManager();
			if (!fragmentManager.popBackStackImmediate()) {
				closeWindow.clicked(requireActivity());
			}
		}
	};

	@Override
	public void onAttach(@NonNull @NotNull Context context) {
		super.onAttach(context);
		requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		networkStatus = new NetworkStatus(getContext(), new ConnectivityManager.NetworkCallback() {
			@Override
			public void onLost(Network network) {
				super.onLost(network);
				if (getActivity() != null) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							networkStatus.showToastDisconnected();
							getParentFragmentManager().popBackStackImmediate();
						}
					});
				}
			}
		});
		new ViewModelProvider(requireActivity()).get(FavoriteLocationViewModel.class);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentAllFavoritesHostBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.bottomNavigation.setVisibility(View.GONE);

		binding.bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
		binding.bottomNavigation.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
			@Override
			public void onNavigationItemReselected(@NonNull @NotNull MenuItem item) {

			}
		});

		onNavigationItemSelectedListener.onNavigationItemSelected(binding.bottomNavigation.getMenu().getItem(0));
	}

	private final BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
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
					case R.id.map:
						destinationFragment = new DefaultMapFragment();
						break;
					case R.id.restaurants:
						destinationFragment = new AllFavoriteRestaurantHostFragment();
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
	public void onDestroy() {
		onBackPressedCallback.remove();
		super.onDestroy();
	}
}