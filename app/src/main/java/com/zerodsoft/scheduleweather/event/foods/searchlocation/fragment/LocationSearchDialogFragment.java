package com.zerodsoft.scheduleweather.event.foods.searchlocation.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentLocationSearchDialogBinding;
import com.zerodsoft.scheduleweather.event.foods.searchlocation.interfaces.OnClickedLocationItem;
import com.zerodsoft.scheduleweather.event.foods.searchlocation.interfaces.OnSelectedNewLocation;
import com.zerodsoft.scheduleweather.navermap.fragment.searchresult.adapter.SearchResultListAdapter;
import com.zerodsoft.scheduleweather.navermap.fragment.searchresult.interfaces.IndicatorCreater;
import com.zerodsoft.scheduleweather.navermap.model.SearchResultChecker;
import com.zerodsoft.scheduleweather.navermap.model.callback.CheckerCallback;
import com.zerodsoft.scheduleweather.navermap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressKakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import java.util.ArrayList;
import java.util.List;

public class LocationSearchDialogFragment extends DialogFragment implements IndicatorCreater, OnClickedLocationItem, OnSelectedNewLocation {
	public static final String TAG = "LocationSearchDialogFragment";

	private final OnSelectedNewLocation onSelectedNewLocation;

	private FragmentLocationSearchDialogBinding binding;
	private SearchResultListAdapter searchResultListAdapter;
	private OnPageCallback onPageCallback;
	private String searchWord;

	public LocationSearchDialogFragment(OnSelectedNewLocation onSelectedNewLocation) {
		this.onSelectedNewLocation = onSelectedNewLocation;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, R.style.AppTheme_FullScreenDialog);
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

		Bundle bundle = getArguments();
		searchWord = bundle.getString("searchWord");

		setSearchView();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	private void setSearchView() {
		binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
			public boolean onQueryTextChange(String query) {
				return false;
			}
		});

		binding.searchView.setQuery(searchWord, true);
	}

	private void search(String searchWord) {
		final LocalApiPlaceParameter addressParameter = LocalParameterUtil.getAddressParameter(searchWord, LocalApiPlaceParameter.DEFAULT_SIZE
				, LocalApiPlaceParameter.DEFAULT_PAGE);
		final LocalApiPlaceParameter placeParameter = LocalParameterUtil.getPlaceParameter(searchWord, null, null,
				LocalApiPlaceParameter.DEFAULT_SIZE, LocalApiPlaceParameter.DEFAULT_PAGE, LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY);

		// 주소, 주소 & 장소, 장소, 검색 결과없음 인 경우
		SearchResultChecker.checkExisting(addressParameter, placeParameter, new CheckerCallback<DataWrapper<KakaoLocalResponse>>() {
			@Override
			public void onResult() {
				// 오류 여부 확인
				for (DataWrapper<KakaoLocalResponse> response : list) {
					if (response.getException() != null) {
						// error, exception
						binding.errorText.setText(getContext().getString(R.string.error) + ", (" + response.getException().getMessage() + ")");
						binding.errorText.setVisibility(View.VISIBLE);
						return;
					}
				}

				// 오류 없으면 진행
				int totalResultCount = 0;

				for (DataWrapper<KakaoLocalResponse> response : list) {
					if (response.getData() instanceof AddressKakaoLocalResponse) {
						totalResultCount += response.getData().size();
					} else if (response.getData() instanceof PlaceKakaoLocalResponse) {
						totalResultCount += response.getData().size();
					}
				}

				if (totalResultCount == 0) {
					// 검색 결과 없음
					binding.errorText.setText(getContext().getString(R.string.not_founded_search_result));
					binding.errorText.setVisibility(View.VISIBLE);
					// searchview클릭 후 재검색 시 search fragment로 이동
				} else {
					List<Fragment> fragments = new ArrayList<>();

					for (DataWrapper<KakaoLocalResponse> response : list) {
						if (response.getData() instanceof PlaceKakaoLocalResponse) {
							PlaceKakaoLocalResponse placeKakaoLocalResponse = (PlaceKakaoLocalResponse) response.getData();

							if (!placeKakaoLocalResponse.getPlaceDocuments().isEmpty()) {
								fragments.add(new PlacesListFragment(LocationSearchDialogFragment.this, searchWord));
							}
						} else if (response.getData() instanceof AddressKakaoLocalResponse) {
							AddressKakaoLocalResponse addressKakaoLocalResponse = (AddressKakaoLocalResponse) response.getData();

							if (!addressKakaoLocalResponse.getAddressResponseDocumentsList().isEmpty()) {
								fragments.add(new AddressesListFragment(LocationSearchDialogFragment.this, searchWord));
							}
						}
					}

					searchResultListAdapter = new SearchResultListAdapter(LocationSearchDialogFragment.this);
					searchResultListAdapter.setFragments(fragments);
					binding.listViewpager.setAdapter(searchResultListAdapter);

					onPageCallback = new OnPageCallback();
					binding.listViewpager.registerOnPageChangeCallback(onPageCallback);
					binding.viewpagerIndicator.createDot(0, fragments.size());
				}
			}
		});
	}

	@Override
	public void onClickedLocationItem(KakaoLocalDocument kakaoLocalDocument) {
		//map으로 표시
		LocationItemDetailDialogFragment locationItemDetailDialogFragment = new LocationItemDetailDialogFragment(this, kakaoLocalDocument);
		locationItemDetailDialogFragment.show(getParentFragmentManager(), "");
	}

	@Override
	public void onSelectedNewLocation(LocationDTO locationDTO) {
		dismiss();
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