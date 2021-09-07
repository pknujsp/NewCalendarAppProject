package com.zerodsoft.calendarplatform.event.foods.search.search.fragment;

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

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.common.interfaces.OnClickedListItem;
import com.zerodsoft.calendarplatform.common.interfaces.OnHiddenFragmentListener;
import com.zerodsoft.calendarplatform.databinding.FragmentSearchRestaurantBinding;
import com.zerodsoft.calendarplatform.event.foods.interfaces.IOnSetView;
import com.zerodsoft.calendarplatform.event.foods.interfaces.OnClickedRestaurantItem;
import com.zerodsoft.calendarplatform.event.foods.search.searchresult.fragment.SearchResultRestaurantFragment;
import com.zerodsoft.calendarplatform.event.foods.viewmodel.RestaurantSharedViewModel;
import com.zerodsoft.calendarplatform.navermap.viewmodel.SearchHistoryViewModel;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.calendarplatform.room.dto.SearchHistoryDTO;

import org.jetbrains.annotations.NotNull;

public class SearchRestaurantFragment extends Fragment implements OnClickedListItem<SearchHistoryDTO>,
		OnClickedRestaurantItem, OnHiddenFragmentListener {
	private FragmentSearchRestaurantBinding binding;
	private RestaurantSharedViewModel restaurantSharedViewModel;
	private FoodRestaurantSearchHistoryFragment foodRestaurantSearchHistoryFragment;
	private SearchHistoryViewModel searchHistoryViewModel;
	private IOnSetView iOnSetView;

	private final FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks =
			new FragmentManager.FragmentLifecycleCallbacks() {
				@Override
				public void onFragmentCreated(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
					super.onFragmentCreated(fm, f, savedInstanceState);
				}

				@Override
				public void onFragmentDestroyed(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f) {
					super.onFragmentDestroyed(fm, f);
				}
			};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getParentFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);
		restaurantSharedViewModel = new ViewModelProvider(getParentFragment().getParentFragment()).get(RestaurantSharedViewModel.class);
		searchHistoryViewModel = new ViewModelProvider(getParentFragment()).get(SearchHistoryViewModel.class);

		iOnSetView = (IOnSetView) getParentFragment();
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

		//검색 기록 프래그먼트 표시

		foodRestaurantSearchHistoryFragment = new FoodRestaurantSearchHistoryFragment(this);
		getChildFragmentManager().beginTransaction().add(binding.fragmentContainer.getId(),
				foodRestaurantSearchHistoryFragment, getString(R.string.tag_restaurant_search_history_fragment)).commit();

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
											if (!isDuplicate) {
												searchHistoryViewModel.insert(SearchHistoryDTO.FOOD_RESTAURANT_SEARCH, query);
											}
											search(query);
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		getParentFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
	}

	private void search(String query) {
		SearchResultRestaurantFragment searchResultRestaurantFragment = new SearchResultRestaurantFragment();
		Bundle bundle = new Bundle();
		bundle.putString("query", query);
		searchResultRestaurantFragment.setArguments(bundle);

		getParentFragmentManager().beginTransaction().hide(this)
				.add(R.id.content_fragment_container, searchResultRestaurantFragment, getString(R.string.tag_search_result_restaurant_fragment))
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

	@Override
	public void onHiddenChangedFragment(boolean hidden) {

	}
}