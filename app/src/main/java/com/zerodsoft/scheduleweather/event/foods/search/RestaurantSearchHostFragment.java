package com.zerodsoft.scheduleweather.event.foods.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentRestaurantSearchHostBinding;
import com.zerodsoft.scheduleweather.event.foods.search.search.fragment.SearchRestaurantFragment;

import org.jetbrains.annotations.NotNull;

public class RestaurantSearchHostFragment extends Fragment {
	private FragmentRestaurantSearchHostBinding binding;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentRestaurantSearchHostBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getChildFragmentManager().beginTransaction()
				.add(binding.fragmentContainer.getId(), new SearchRestaurantFragment(), getString(R.string.tag_search_restaurant_fragment))
				.commit();
	}
}