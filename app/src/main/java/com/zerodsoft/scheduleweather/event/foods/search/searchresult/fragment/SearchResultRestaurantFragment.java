package com.zerodsoft.scheduleweather.event.foods.search.searchresult.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.databinding.FragmentSearchResultRestaurantBinding;
import com.zerodsoft.scheduleweather.event.foods.interfaces.IOnSetView;
import com.zerodsoft.scheduleweather.event.foods.main.RestaurantListFragment;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.RestaurantSharedViewModel;
import com.zerodsoft.scheduleweather.navermap.place.PlaceInfoWebFragment;
import com.zerodsoft.scheduleweather.navermap.viewmodel.SearchHistoryViewModel;
import com.zerodsoft.scheduleweather.room.dto.SearchHistoryDTO;

import org.jetbrains.annotations.NotNull;


public class SearchResultRestaurantFragment extends Fragment {
	private String query;
	private FragmentSearchResultRestaurantBinding binding;
	private RestaurantSharedViewModel restaurantSharedViewModel;
	private SearchHistoryViewModel searchHistoryViewModel;
	private IOnSetView iOnSetView;

	private final FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks =
			new FragmentManager.FragmentLifecycleCallbacks() {
				@Override
				public void onFragmentCreated(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
					super.onFragmentCreated(fm, f, savedInstanceState);
					if (f instanceof PlaceInfoWebFragment) {
						iOnSetView.setFragmentContainerVisibility(IOnSetView.ViewType.HEADER, View.GONE);
					}
				}

				@Override
				public void onFragmentDestroyed(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f) {
					super.onFragmentDestroyed(fm, f);
					if (f instanceof PlaceInfoWebFragment) {
						iOnSetView.setFragmentContainerVisibility(IOnSetView.ViewType.HEADER, View.GONE);
					}
				}
			};


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getParentFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);
		restaurantSharedViewModel = new ViewModelProvider(requireActivity()).get(RestaurantSharedViewModel.class);
		searchHistoryViewModel = new ViewModelProvider(getParentFragment()).get(SearchHistoryViewModel.class);
		iOnSetView = (IOnSetView) getParentFragment();
		iOnSetView.setFragmentContainerVisibility(IOnSetView.ViewType.HEADER, View.GONE);

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
		getChildFragmentManager().beginTransaction().add(binding.fragmentContainer.getId(), restaurantListFragment,
				getString(R.string.tag_place_info_web_fragment)).commit();

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
					searchHistoryViewModel.contains(SearchHistoryDTO.FOOD_RESTAURANT_SEARCH, query, new DbQueryCallback<Boolean>() {
						@Override
						public void onResultSuccessful(Boolean isDuplicated) {
							if (!isDuplicated) {
								searchHistoryViewModel.insert(SearchHistoryDTO.FOOD_RESTAURANT_SEARCH, query);
							}
						}

						@Override
						public void onResultNoData() {

						}
					});
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		getParentFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
	}
}