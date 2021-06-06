package com.zerodsoft.scheduleweather.event.foods.search.searchresult.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.databinding.FragmentSearchRestaurantBinding;
import com.zerodsoft.scheduleweather.databinding.FragmentSearchResultRestaurantBinding;
import com.zerodsoft.scheduleweather.event.foods.main.RestaurantListFragment;
import com.zerodsoft.scheduleweather.event.foods.search.search.fragment.SearchRestaurantFragment;
import com.zerodsoft.scheduleweather.room.dto.SearchHistoryDTO;

import org.jetbrains.annotations.NotNull;


public class SearchResultRestaurantFragment extends Fragment {
	private String query;
	private FragmentSearchResultRestaurantBinding binding;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		query = bundle.getString("query");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentSearchResultRestaurantBinding.inflate(inflater);
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

		binding.searchView.setQuery(query, false);
		binding.searchView.setOnBackClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getParentFragmentManager().popBackStack();
			}
		});
		binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				if (!query.isEmpty()) {
					restaurantListFragment.requestRestaurantList(query);
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});


	}
}