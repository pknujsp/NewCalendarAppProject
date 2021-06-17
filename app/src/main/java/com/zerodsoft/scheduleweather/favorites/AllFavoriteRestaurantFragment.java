package com.zerodsoft.scheduleweather.favorites;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentAllFavoriteRestaurantBinding;

public class AllFavoriteRestaurantFragment extends Fragment {
	private FragmentAllFavoriteRestaurantBinding binding;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentAllFavoriteRestaurantBinding.inflate(inflater);
		return binding.getRoot();
	}
}