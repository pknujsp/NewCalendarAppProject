package com.zerodsoft.scheduleweather.favorites.restaurant;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentContainerHostBinding;
import com.zerodsoft.scheduleweather.favorites.restaurant.basefragment.FavoriteRestaurantBaseFragment;

import org.jetbrains.annotations.NotNull;


public class AllFavoriteRestaurantHostFragment extends Fragment {
	private FragmentContainerHostBinding binding;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentContainerHostBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getChildFragmentManager().beginTransaction()
				.add(binding.fragmentContainer.getId(), new AllFavoriteRestaurantFragment()
						, getString(R.string.tag_all_favorite_restaurant_fragment))
				.commit();
	}
}