package com.zerodsoft.scheduleweather.event.foods.search.search.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.databinding.FragmentSearchRestaurantBinding;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedRestaurantItem;
import com.zerodsoft.scheduleweather.event.foods.search.searchresult.fragment.SearchResultRestaurantFragment;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.RestaurantSharedViewModel;
import com.zerodsoft.scheduleweather.navermap.interfaces.FavoriteLocationsListener;
import com.zerodsoft.scheduleweather.navermap.viewmodel.SearchHistoryViewModel;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.SearchHistoryDTO;

public class SearchRestaurantFragment extends Fragment implements OnClickedListItem<SearchHistoryDTO>,
		OnClickedRestaurantItem {
	public static final String TAG = "SearchRestaurantFragment";
	private FragmentSearchRestaurantBinding binding;
	private RestaurantSharedViewModel restaurantSharedViewModel;
	private FoodRestaurantSearchHistoryFragment foodRestaurantSearchHistoryFragment;
	private SearchHistoryViewModel searchHistoryViewModel;
	private FavoriteLocationsListener favoriteLocationsListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		restaurantSharedViewModel = new ViewModelProvider(requireActivity()).get(RestaurantSharedViewModel.class);
		favoriteLocationsListener = restaurantSharedViewModel.getFavoriteLocationsListener();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentSearchRestaurantBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		searchHistoryViewModel = new ViewModelProvider(this).get(SearchHistoryViewModel.class);

		//검색 기록 프래그먼트 표시
		foodRestaurantSearchHistoryFragment = new FoodRestaurantSearchHistoryFragment(this);
		getChildFragmentManager().beginTransaction().add(binding.fragmentContainer.getId(),
				foodRestaurantSearchHistoryFragment, FoodRestaurantSearchHistoryFragment.TAG).commitNow();

		binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				if (!query.isEmpty()) {
					searchHistoryViewModel.contains(SearchHistoryDTO.FOOD_RESTAURANT_SEARCH, query,
							new DbQueryCallback<Boolean>() {
								@Override
								public void onResultSuccessful(Boolean isDuplicate) {
									requireActivity().runOnUiThread(new Runnable() {
										@Override
										public void run() {
											if (isDuplicate) {
												Toast.makeText(getContext(), R.string.duplicate_value, Toast.LENGTH_SHORT).show();
											} else {
												search(query);
												foodRestaurantSearchHistoryFragment.insertHistory(query);
											}
										}
									});
								}

								@Override
								public void onResultNoData() {

								}
							});
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});

	}


	private void search(String query) {
		SearchResultRestaurantFragment searchResultRestaurantFragment = new SearchResultRestaurantFragment();
		Bundle bundle = new Bundle();
		bundle.putString("query", query);
		searchResultRestaurantFragment.setArguments(bundle);

		getParentFragmentManager().beginTransaction().hide(this)
				.add(R.id.fragment_container, searchResultRestaurantFragment, getString(R.string.tag_search_result_restaurant_fragment))
				.addToBackStack(getString(R.string.tag_search_result_restaurant_fragment))
				.commit();
	}

	@Override
	public void onClickedListItem(SearchHistoryDTO e, int position) {
		binding.searchView.setQuery(e.getValue(), false);
		search(e.getValue());
	}

	@Override
	public void deleteListItem(SearchHistoryDTO e, int position) {

	}


	@Override
	public void onClickedRestaurantItem(PlaceDocuments placeDocuments) {

	}
}