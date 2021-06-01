package com.zerodsoft.scheduleweather.event.foods.search.searchresult.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentSearchRestaurantBinding;
import com.zerodsoft.scheduleweather.event.foods.main.RestaurantListFragment;
import com.zerodsoft.scheduleweather.event.foods.search.search.fragment.SearchRestaurantFragment;
import com.zerodsoft.scheduleweather.event.foods.search.search.fragment.SearchRestaurantFragmentArgs;

import org.jetbrains.annotations.NotNull;


public class SearchResultRestaurantFragment extends Fragment {
	private String query;
	private FragmentSearchRestaurantBinding binding;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SearchResultRestaurantFragmentArgs args = SearchResultRestaurantFragmentArgs.fromBundle(getArguments());
		query = args.getQuery();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentSearchRestaurantBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		RestaurantListFragment restaurantListFragment = new RestaurantListFragment();
		Bundle bundle = new Bundle();
		bundle.putString("query", query);
		restaurantListFragment.setArguments(bundle);
		getChildFragmentManager().beginTransaction().add(binding.fragmentContainer.getId(), restaurantListFragment, "").commitNow();
	}
}