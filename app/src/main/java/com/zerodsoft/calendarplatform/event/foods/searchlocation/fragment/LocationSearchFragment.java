package com.zerodsoft.calendarplatform.event.foods.searchlocation.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.common.classes.JsonDownloader;
import com.zerodsoft.calendarplatform.databinding.FragmentLocationSearchDialogBinding;
import com.zerodsoft.calendarplatform.event.foods.searchlocation.interfaces.OnClickedLocationItem;
import com.zerodsoft.calendarplatform.event.foods.searchlocation.interfaces.OnSelectedNewLocation;
import com.zerodsoft.calendarplatform.navermap.searchresult.adapter.SearchResultListAdapter;
import com.zerodsoft.calendarplatform.navermap.searchresult.interfaces.IndicatorCreater;
import com.zerodsoft.calendarplatform.navermap.model.SearchResultChecker;
import com.zerodsoft.calendarplatform.kakaoplace.LocalParameterUtil;
import com.zerodsoft.calendarplatform.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.KakaoLocalResponse;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.addressresponse.AddressKakaoLocalResponse;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.calendarplatform.room.dto.LocationDTO;

import java.util.ArrayList;
import java.util.List;

public class LocationSearchFragment extends Fragment implements IndicatorCreater, OnClickedLocationItem, OnSelectedNewLocation {
	public static final String TAG = "LocationSearchFragment";

	private final OnSelectedNewLocation onSelectedNewLocation;

	private FragmentLocationSearchDialogBinding binding;
	private SearchResultListAdapter searchResultListAdapter;
	private OnPageCallback onPageCallback;
	private String query;

	public LocationSearchFragment(OnSelectedNewLocation onSelectedNewLocation) {
		this.onSelectedNewLocation = onSelectedNewLocation;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();
		query = bundle.getString("searchWord");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentLocationSearchDialogBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void setIndicator(int fragmentSize) {
		binding.viewpagerIndicator.createDot(0, fragmentSize);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		binding.customProgressView.setContentView(binding.contentLayout);
		binding.searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				if (!query.isEmpty()) {
					search(query);
					return true;
				} else {
					return false;
				}
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});

		searchResultListAdapter = new SearchResultListAdapter(this);
		binding.listViewpager.setAdapter(searchResultListAdapter);
		binding.searchView.setQuery(query, true);
	}


	private void search(String query) {
		// 주소, 주소 & 장소, 장소, 검색 결과없음 인 경우
		final LocalApiPlaceParameter addressParameter = LocalParameterUtil.getAddressParameter(query, "1"
				, LocalApiPlaceParameter.DEFAULT_PAGE);
		final LocalApiPlaceParameter placeParameter = LocalParameterUtil.getPlaceParameter(query, null, null,
				"1", LocalApiPlaceParameter.DEFAULT_PAGE, LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY);

		SearchResultChecker.checkExisting(addressParameter, placeParameter, new JsonDownloader<List<KakaoLocalResponse>>() {
			@Override
			public void onResponseSuccessful(List<KakaoLocalResponse> resultList) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						List<Fragment> fragments = new ArrayList<>();

						for (KakaoLocalResponse kakaoLocalResponse : resultList) {
							if (kakaoLocalResponse.isEmpty()) {
								continue;
							}

							if (kakaoLocalResponse instanceof PlaceKakaoLocalResponse) {
								fragments.add(new PlacesListFragment(LocationSearchFragment.this, LocationSearchFragment.this.query));
							} else if (kakaoLocalResponse instanceof AddressKakaoLocalResponse) {
								fragments.add(new AddressesListFragment(LocationSearchFragment.this, query));
							}
						}
						onPageCallback = new OnPageCallback();
						binding.listViewpager.registerOnPageChangeCallback(onPageCallback);
						searchResultListAdapter.setFragments(fragments);
						binding.customProgressView.onSuccessfulProcessingData();
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
	public void onClickedLocationItem(KakaoLocalDocument kakaoLocalDocument) {
		//map으로 표시
		LocationItemDetailDialogFragment locationItemDetailDialogFragment = new LocationItemDetailDialogFragment(this, kakaoLocalDocument);
		locationItemDetailDialogFragment.setCancelable(false);
		locationItemDetailDialogFragment.show(getChildFragmentManager(), getString(R.string.tag_location_item_detail_fragment));
	}

	@Override
	public void onSelectedNewLocation(LocationDTO locationDTO) {
		onSelectedNewLocation.onSelectedNewLocation(locationDTO);
	}

	class OnPageCallback extends ViewPager2.OnPageChangeCallback {
		public int lastPosition;

		@Override
		public void onPageSelected(int position) {
			super.onPageSelected(position);
			lastPosition = position;
			binding.viewpagerIndicator.selectDot(position);
		}
	}
}