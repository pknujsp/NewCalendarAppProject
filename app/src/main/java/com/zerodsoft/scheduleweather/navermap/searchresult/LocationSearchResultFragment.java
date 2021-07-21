package com.zerodsoft.scheduleweather.navermap.searchresult;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.common.enums.KakaoLocalApiResultType;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.databinding.FragmentSearchResultListBinding;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.event.places.interfaces.MarkerOnClickListener;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.MarkerType;
import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.navermap.searchresult.adapter.SearchResultListAdapter;
import com.zerodsoft.scheduleweather.navermap.searchresult.interfaces.IndicatorCreater;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.navermap.model.SearchResultChecker;
import com.zerodsoft.scheduleweather.kakaoplace.LocalParameterUtil;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnExtraListDataListener;
import com.zerodsoft.scheduleweather.navermap.viewmodel.MapSharedViewModel;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressKakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;

import java.util.ArrayList;
import java.util.List;

public class LocationSearchResultFragment extends Fragment implements IndicatorCreater, OnExtraListDataListener<LocationType> {
	private FragmentSearchResultListBinding binding;

	private SearchResultListAdapter searchResultListAdapter;
	private String query;

	private OnPageCallback onPageCallback;
	private OnExtraListDataListener<LocationType> placesOnExtraListDataListener;
	private OnExtraListDataListener<LocationType> addressesOnExtraListDataListener;

	private MapSharedViewModel mapSharedViewModel;
	private IMapData iMapData;
	private BottomSheetController bottomSheetController;
	private MarkerOnClickListener markerOnClickListener;


	@Override
	public void setIndicator(int fragmentSize) {
		binding.viewpagerIndicator.createDot(0, fragmentSize);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		query = getArguments().getString("query");

		mapSharedViewModel = new ViewModelProvider(getParentFragment()).get(MapSharedViewModel.class);
		iMapData = mapSharedViewModel.getiMapData();
		bottomSheetController = mapSharedViewModel.getBottomSheetController();
		markerOnClickListener = mapSharedViewModel.getPoiItemOnClickListener();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentSearchResultListBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.customProgressView.setContentView(binding.contentLayout);

		searchResultListAdapter = new SearchResultListAdapter(LocationSearchResultFragment.this);
		onPageCallback = new OnPageCallback();
		binding.listViewpager.registerOnPageChangeCallback(onPageCallback);
		binding.listViewpager.setAdapter(searchResultListAdapter);

		searchLocation(query);
	}

	public void searchLocation(String query) {
		this.query = query;

		final LocalApiPlaceParameter addressParameter = LocalParameterUtil.getAddressParameter(query, "1"
				, LocalApiPlaceParameter.DEFAULT_PAGE);
		final LocalApiPlaceParameter placeParameter = LocalParameterUtil.getPlaceParameter(query, null, null,
				"1", LocalApiPlaceParameter.DEFAULT_PAGE, LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY);

		SearchResultChecker.checkExisting(addressParameter, placeParameter, new JsonDownloader<List<KakaoLocalResponse>>() {
			@Override
			public void onResponseSuccessful(List<KakaoLocalResponse> resultList) {
				List<Fragment> fragments = new ArrayList<>();

				for (KakaoLocalResponse kakaoLocalResponse : resultList) {
					if (kakaoLocalResponse.isEmpty()) {
						continue;
					}

					if (kakaoLocalResponse instanceof PlaceKakaoLocalResponse) {
						SearchResultPlaceListFragment searchResultPlaceListFragment = new SearchResultPlaceListFragment(query, placeDocumentsOnClickedListItem);
						placesOnExtraListDataListener = searchResultPlaceListFragment;
						fragments.add(searchResultPlaceListFragment);
					} else if (kakaoLocalResponse instanceof AddressKakaoLocalResponse) {
						SearchResultAddressListFragment addressesListFragment = new SearchResultAddressListFragment(query, addressResponseDocumentsOnClickedListItem);
						addressesOnExtraListDataListener = addressesListFragment;
						fragments.add(addressesListFragment);
					}
				}

				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						binding.customProgressView.onSuccessfulProcessingData();

						onPageCallback.lastPosition = 0;
						searchResultListAdapter.setFragments(fragments);
						searchResultListAdapter.notifyDataSetChanged();

						binding.viewpagerIndicator.createDot(0, fragments.size());
					}
				});
			}

			@Override
			public void onResponseFailed(Exception e) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						binding.customProgressView.onFailedProcessingData(getString(R.string.not_founded_search_result));
						binding.listViewpager.unregisterOnPageChangeCallback(onPageCallback);
						searchResultListAdapter.setFragments(new ArrayList<>());
						searchResultListAdapter.notifyDataSetChanged();
					}
				});
			}
		});

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		iMapData.removeMarkers(MarkerType.SEARCH_RESULT_ADDRESS, MarkerType.SEARCH_RESULT_PLACE);
	}

	@Override
	public void loadExtraListData(LocationType e, RecyclerView.AdapterDataObserver adapterDataObserver) {


	}

	@Override
	public void loadExtraListData(RecyclerView.AdapterDataObserver adapterDataObserver) {
		KakaoLocalApiResultType currentResultType = getCurrentListType();

		if (currentResultType == KakaoLocalApiResultType.ADDRESS) {
			addressesOnExtraListDataListener.loadExtraListData(adapterDataObserver);
		} else {
			placesOnExtraListDataListener.loadExtraListData(adapterDataObserver);
		}

	}

	public KakaoLocalApiResultType getCurrentListType() {
		int currentItem = binding.listViewpager.getCurrentItem();
		Fragment currentFragment = searchResultListAdapter.getFragment(currentItem);

		if (currentFragment instanceof SearchResultAddressListFragment) {
			return KakaoLocalApiResultType.ADDRESS;
		} else {
			return KakaoLocalApiResultType.PLACE;
		}
	}

	class OnPageCallback extends ViewPager2.OnPageChangeCallback {
		public int lastPosition = 0;

		@Override
		public void onPageSelected(int position) {
			super.onPageSelected(position);
			if (lastPosition != position) {
				lastPosition = position;
				binding.viewpagerIndicator.selectDot(position);
			}
		}
	}

	private final OnClickedListItem<PlaceDocuments> placeDocumentsOnClickedListItem = new OnClickedListItem<PlaceDocuments>() {
		@Override
		public void onClickedListItem(PlaceDocuments e, int position) {
			iMapData.showMarkers(MarkerType.SEARCH_RESULT_PLACE);
			markerOnClickListener.onPOIItemSelectedByList(e, MarkerType.SEARCH_RESULT_PLACE);
			bottomSheetController.setStateOfBottomSheet(BottomSheetType.SEARCH_LOCATION, BottomSheetBehavior.STATE_COLLAPSED);
			showMap();
		}

		@Override
		public void deleteListItem(PlaceDocuments e, int position) {

		}
	};

	private final OnClickedListItem<AddressResponseDocuments> addressResponseDocumentsOnClickedListItem = new OnClickedListItem<AddressResponseDocuments>() {
		@Override
		public void onClickedListItem(AddressResponseDocuments e, int position) {
			iMapData.showMarkers(MarkerType.SEARCH_RESULT_ADDRESS);
			markerOnClickListener.onPOIItemSelectedByList(e, MarkerType.SEARCH_RESULT_ADDRESS);
			bottomSheetController.setStateOfBottomSheet(BottomSheetType.SEARCH_LOCATION, BottomSheetBehavior.STATE_COLLAPSED);
			showMap();
		}

		@Override
		public void deleteListItem(AddressResponseDocuments e, int position) {

		}
	};

	private void showMap() {
		bottomSheetController.setStateOfBottomSheet(BottomSheetType.SEARCH_LOCATION, BottomSheetBehavior.STATE_COLLAPSED);
		getParentFragmentManager().beginTransaction().hide(this)
				.addToBackStack(getString(R.string.tag_hide_location_search_result_fragment)).commit();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (hidden) {
			bottomSheetController.setStateOfBottomSheet(BottomSheetType.SEARCH_LOCATION, BottomSheetBehavior.STATE_COLLAPSED);
		} else {
			bottomSheetController.setStateOfBottomSheet(BottomSheetType.LOCATION_ITEM, BottomSheetBehavior.STATE_COLLAPSED);
			bottomSheetController.setStateOfBottomSheet(BottomSheetType.SEARCH_LOCATION, BottomSheetBehavior.STATE_EXPANDED);
		}
	}
}