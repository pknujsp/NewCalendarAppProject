package com.zerodsoft.calendarplatform.navermap.searchheader;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.common.enums.KakaoLocalApiResultType;
import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.common.interfaces.OnClickedListItem;
import com.zerodsoft.calendarplatform.databinding.FragmentLocationSearchBarBinding;
import com.zerodsoft.calendarplatform.navermap.BottomSheetType;
import com.zerodsoft.calendarplatform.navermap.MarkerType;
import com.zerodsoft.calendarplatform.navermap.search.adapter.PlaceCategoriesAdapter;
import com.zerodsoft.calendarplatform.navermap.searchresult.LocationSearchResultFragment;
import com.zerodsoft.calendarplatform.navermap.searchresult.SearchResultAddressListFragment;
import com.zerodsoft.calendarplatform.navermap.searchresult.SearchResultPlaceListFragment;
import com.zerodsoft.calendarplatform.navermap.interfaces.BottomSheetController;
import com.zerodsoft.calendarplatform.navermap.interfaces.IMapData;
import com.zerodsoft.calendarplatform.navermap.viewmodel.MapSharedViewModel;
import com.zerodsoft.calendarplatform.navermap.viewmodel.SearchHistoryViewModel;
import com.zerodsoft.calendarplatform.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.calendarplatform.room.dto.PlaceCategoryDTO;
import com.zerodsoft.calendarplatform.room.dto.SearchHistoryDTO;

import org.jetbrains.annotations.NotNull;

public class MapHeaderSearchFragment extends Fragment {
	private FragmentLocationSearchBarBinding binding;

	private SearchHistoryViewModel searchHistoryViewModel;
	private MapSharedViewModel mapSharedViewModel;
	private PlaceCategoriesAdapter categoriesAdapter;
	private BottomSheetController bottomSheetController;
	private IMapData iMapData;

	private Drawable mapDrawable;
	private Drawable listDrawable;

	private final OnClickedListItem<PlaceCategoryDTO> onClickedListItemOnPlaceCategory = new OnClickedListItem<PlaceCategoryDTO>() {
		@Override
		public void onClickedListItem(PlaceCategoryDTO e, int position) {
			binding.searchView.setQuery(e.getDescription(), false);
			search(e.getCode());
		}

		@Override
		public void deleteListItem(PlaceCategoryDTO e, int position) {

		}
	};

	private final FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
		@Override
		public void onFragmentCreated(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
			super.onFragmentCreated(fm, f, savedInstanceState);
			if (f instanceof SearchResultPlaceListFragment || f instanceof SearchResultAddressListFragment) {
				binding.viewTypeButton.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onFragmentDestroyed(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f) {
			super.onFragmentDestroyed(fm, f);
			if (f instanceof LocationSearchResultFragment) {
				binding.viewTypeButton.setVisibility(View.GONE);
			}
		}
	};

	private BottomSheetBehavior.BottomSheetCallback searchLocationBottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
		@Override
		public void onStateChanged(@NonNull @NotNull View bottomSheet, int newState) {
			switch (newState) {
				case BottomSheetBehavior.STATE_EXPANDED:
					binding.viewTypeButton.setImageDrawable(mapDrawable);
					break;
				case BottomSheetBehavior.STATE_COLLAPSED:
					binding.viewTypeButton.setImageDrawable(listDrawable);
					break;
			}
		}

		@Override
		public void onSlide(@NonNull @NotNull View bottomSheet, float slideOffset) {

		}
	};

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getParentFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);

		Fragment parentFragment = getParentFragment();
		searchHistoryViewModel = new ViewModelProvider(parentFragment).get(SearchHistoryViewModel.class);
		mapSharedViewModel = new ViewModelProvider(parentFragment).get(MapSharedViewModel.class);

		iMapData = mapSharedViewModel.getiMapData();
		bottomSheetController = mapSharedViewModel.getBottomSheetController();
		bottomSheetController.getBottomSheetBehavior(BottomSheetType.SEARCH_LOCATION).addBottomSheetCallback(searchLocationBottomSheetCallback);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = FragmentLocationSearchBarBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		binding.categoriesRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));

		categoriesAdapter = new PlaceCategoriesAdapter(onClickedListItemOnPlaceCategory);
		binding.categoriesRecyclerview.setAdapter(categoriesAdapter);

		binding.searchView.setOnBackClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				requireActivity().getOnBackPressedDispatcher().onBackPressed();
			}
		});

		binding.viewTypeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				FragmentManager parentFragmentManager = getParentFragmentManager();
				LocationSearchResultFragment locationSearchResultFragment =
						((LocationSearchResultFragment) parentFragmentManager.findFragmentByTag(getString(R.string.tag_location_search_result_fragment)));

				if (locationSearchResultFragment.isVisible()) {
					//장소/주소중 현재 상태로 선택
					KakaoLocalApiResultType currentResultType = locationSearchResultFragment.getCurrentListType();

					if (currentResultType == KakaoLocalApiResultType.ADDRESS) {
						iMapData.showMarkers(MarkerType.SEARCH_RESULT_ADDRESS);
					} else {
						iMapData.showMarkers(MarkerType.SEARCH_RESULT_PLACE);
					}

					parentFragmentManager.beginTransaction().hide(locationSearchResultFragment)
							.addToBackStack(getString(R.string.tag_hide_location_search_result_fragment)).commit();
				} else {
					parentFragmentManager.popBackStackImmediate();
				}
			}
		});


		binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				if (!query.isEmpty()) {
					if (!KakaoLocalApiCategoryUtil.isCategory(query)) {
						searchHistoryViewModel.contains(SearchHistoryDTO.LOCATION_SEARCH, query, new DbQueryCallback<Boolean>() {
							@Override
							public void onResultSuccessful(Boolean isDuplicate) {
								if (!isDuplicate) {
									searchHistoryViewModel.insert(SearchHistoryDTO.LOCATION_SEARCH, query);
								}
							}

							@Override
							public void onResultNoData() {

							}
						});
					}
					search(query);
					return true;
				} else {
					Toast.makeText(getContext(), R.string.message_request_input_address_or_place, Toast.LENGTH_SHORT).show();
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
		binding.viewTypeButton.setVisibility(View.GONE);

		mapDrawable = ContextCompat.getDrawable(getContext(), R.drawable.map_icon);
		listDrawable = ContextCompat.getDrawable(getContext(), R.drawable.list_icon);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		bottomSheetController.getBottomSheetBehavior(BottomSheetType.LOCATION_ITEM).removeBottomSheetCallback(searchLocationBottomSheetCallback);
		getParentFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
	}


	public void search(String query) {
		// location search fragment is added?
		FragmentManager parentFragmentManager = getParentFragmentManager();
		LocationSearchResultFragment locationSearchResultFragment =
				(LocationSearchResultFragment) parentFragmentManager.findFragmentByTag(getString(R.string.tag_location_search_result_fragment));

		if (locationSearchResultFragment == null) {
			locationSearchResultFragment = new LocationSearchResultFragment();
			Bundle bundle = new Bundle();
			bundle.putString("query", query);

			locationSearchResultFragment.setArguments(bundle);

			parentFragmentManager.beginTransaction()
					.hide(parentFragmentManager.findFragmentByTag(getString(R.string.tag_location_search_fragment)))
					.add(R.id.search_fragment_container, locationSearchResultFragment, getString(R.string.tag_location_search_result_fragment))
					.addToBackStack(getString(R.string.tag_location_search_result_fragment)).commit();
		} else {
			// added
			iMapData.removeMarkers(MarkerType.SEARCH_RESULT_ADDRESS, MarkerType.SEARCH_RESULT_PLACE);
			if (locationSearchResultFragment.isHidden()) {
				parentFragmentManager.popBackStackImmediate();
			}
			locationSearchResultFragment.searchLocation(query);
		}
	}

	public void setQuery(String query, boolean submit) {
		binding.searchView.setQuery(query, submit);
	}
}
