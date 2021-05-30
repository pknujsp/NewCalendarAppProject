package com.zerodsoft.scheduleweather.event.foods.settings;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentRestaurantSettingsNavHostBinding;

public class RestaurantSettingsNavHostFragment extends NavHostFragment {
	private FragmentRestaurantSettingsNavHostBinding binding;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentRestaurantSettingsNavHostBinding.inflate(inflater);
		return binding.getRoot();
	}
}