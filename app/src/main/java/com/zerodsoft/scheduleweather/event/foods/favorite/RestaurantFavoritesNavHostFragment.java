package com.zerodsoft.scheduleweather.event.foods.favorite;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentRestaurantFavoritesNavHostBinding;


public class RestaurantFavoritesNavHostFragment extends Fragment {
	private FragmentRestaurantFavoritesNavHostBinding binding;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentRestaurantFavoritesNavHostBinding.inflate(inflater);
		return binding.getRoot();
	}
}