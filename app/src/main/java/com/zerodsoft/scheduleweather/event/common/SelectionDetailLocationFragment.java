package com.zerodsoft.scheduleweather.event.common;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.naver.maps.map.NaverMap;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.enums.LocationIntentCode;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.MarkerType;
import com.zerodsoft.scheduleweather.navermap.NaverMapFragment;
import com.zerodsoft.scheduleweather.navermap.searchheader.MapHeaderMainFragment;
import com.zerodsoft.scheduleweather.navermap.searchheader.MapHeaderSearchFragment;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class SelectionDetailLocationFragment extends NaverMapFragment {
	private LocationDTO selectedLocationDTOInEvent;
	private LocationIntentCode requestCode;
	private OnDetailLocationSelectionResultListener onDetailLocationSelectionResultListener;

	private LocationDTO selectedLocationDTOInMap;
	private String locationNameInEvent;

	boolean mapReady = false;
	boolean mapHeaderSearchFragmentStarted = false;

	public SelectionDetailLocationFragment(OnDetailLocationSelectionResultListener onDetailLocationSelectionResultListener) {
		this.onDetailLocationSelectionResultListener = onDetailLocationSelectionResultListener;
	}

	private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
		@Override
		public void handleOnBackPressed() {
			FragmentManager fragmentManager = getChildFragmentManager();
			if (!fragmentManager.popBackStackImmediate()) {
				getParentFragmentManager().popBackStackImmediate();
			}
		}
	};

	private FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
		@Override
		public void onFragmentStarted(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f) {
			super.onFragmentStarted(fm, f);
			if (f instanceof MapHeaderSearchFragment) {
				if (requestCode == LocationIntentCode.REQUEST_CODE_SELECT_LOCATION_BY_QUERY) {
					mapHeaderSearchFragmentStarted = true;
					attemptSearchQuery();
					fm.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
				}
			} else if (f instanceof MapHeaderMainFragment) {
				if (requestCode == LocationIntentCode.REQUEST_CODE_SELECT_LOCATION_BY_QUERY) {
					binding.headerFragmentContainer.callOnClick();
				}
			}
		}
	};

	@Override
	public void onAttach(@NonNull @NotNull Context context) {
		super.onAttach(context);
		requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
	}

	@Override
	public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle arguments = getArguments();

		requestCode = LocationIntentCode.enumOf(arguments.getInt("requestCode"));

		getChildFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		binding.naverMapButtonsLayout.buildingButton.setVisibility(View.GONE);
		binding.naverMapButtonsLayout.favoriteLocationsButton.setVisibility(View.GONE);

		setPlaceBottomSheetSelectBtnVisibility(View.VISIBLE);
		setPlaceBottomSheetUnSelectBtnVisibility(View.GONE);

		switch (requestCode) {
			case REQUEST_CODE_CHANGE_LOCATION:
				//지정한 위치를 표시할 바텀시트 프래그먼트 생성
				break;
		}

		loadMap();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		onBackPressedCallback.remove();
	}

	private void showSelectedLocation() {
		// 위치가 이미 선택되어 있는 경우 해당 위치 정보를 표시함 (삭제 버튼 추가)
		if (selectedLocationDTOInEvent.getLocationType() == LocationType.ADDRESS) {
			// 주소 검색 순서 : 좌표로 주소 변환

			CoordToAddressDocuments coordToAddressDocuments = new CoordToAddressDocuments();
			//주소, 좌표값

			createMarkers(Collections.singletonList(coordToAddressDocuments), MarkerType.SELECTED_ADDRESS_IN_EVENT);
			onPOIItemSelectedByList(coordToAddressDocuments, MarkerType.SELECTED_ADDRESS_IN_EVENT);


		} else if (selectedLocationDTOInEvent.getLocationType() == LocationType.PLACE) {
			PlaceDocuments document = new PlaceDocuments();


			createMarkers(Collections.singletonList(document), MarkerType.SELECTED_PLACE_IN_EVENT);
			onPOIItemSelectedByList(document, MarkerType.SELECTED_PLACE_IN_EVENT);
		}

	}

	@Override
	public void onSelectedLocation(KakaoLocalDocument kakaoLocalDocument) {
		if (selectedLocationDTOInMap != null) {
			new MaterialAlertDialogBuilder(requireActivity())
					.setTitle(R.string.request_select_location_title)
					.setMessage(R.string.message_existing_selected_location)
					.setCancelable(false)
					.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							dialogInterface.dismiss();
						}
					})
					.setPositiveButton(R.string.check, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							saveLocation();
						}
					}).create().show();
		} else {
			saveLocation();
		}
	}

	private void saveLocation() {
		LocationDTO location = getSelectedLocationDto();
		selectedLocationDTOInMap = location;
		Bundle bundle = new Bundle();
		bundle.putParcelable(DetailLocationSelectorKey.SELECTED_LOCATION_DTO_IN_MAP.value(), location);
		requireActivity().getIntent().putExtras(bundle);

		bottomSheetBehaviorMap.get(BottomSheetType.LOCATION_ITEM).setState(BottomSheetBehavior.STATE_COLLAPSED);

		String locationName = location.getLocationType() == LocationType.PLACE ? location.getPlaceName() : location.getAddressName();
		Toast.makeText(getContext(), locationName + " - " + getString(R.string.selected_location), Toast.LENGTH_SHORT).show();

		if (requestCode == LocationIntentCode.REQUEST_CODE_CHANGE_LOCATION) {
			onDetailLocationSelectionResultListener.onResultChangedLocation(location);
		} else {
			onDetailLocationSelectionResultListener.onResultSelectedLocation(location);
		}
		getParentFragmentManager().popBackStackImmediate();
	}

	@Override
	public void onRemovedLocation() {
		deselectMarker();
		removeAllMarkers();

		bottomSheetBehaviorMap.get(BottomSheetType.LOCATION_ITEM).setState(BottomSheetBehavior.STATE_COLLAPSED);
		Toast.makeText(getContext(), R.string.canceled_location, Toast.LENGTH_SHORT).show();

		onDetailLocationSelectionResultListener.onResultUnselectedLocation();
		getParentFragmentManager().popBackStackImmediate();
	}

	@Override
	public void onMapReady(@NonNull @NotNull NaverMap naverMap) {
		super.onMapReady(naverMap);
		mapReady = true;

		Bundle arguments = getArguments();

		switch (requestCode) {
			case REQUEST_CODE_SELECT_LOCATION_EMPTY_QUERY:
				//지도 기본 화면 표시
				break;
			case REQUEST_CODE_SELECT_LOCATION_BY_QUERY:
				locationNameInEvent = arguments.getString(DetailLocationSelectorKey.LOCATION_NAME_IN_EVENT.value());
				attemptSearchQuery();
				break;
			case REQUEST_CODE_CHANGE_LOCATION:
				selectedLocationDTOInEvent = arguments.getParcelable(DetailLocationSelectorKey.SELECTED_LOCATION_DTO_IN_EVENT.value());
				showSelectedLocation();
				break;
		}

	}

	private void attemptSearchQuery() {
		if (mapReady && mapHeaderSearchFragmentStarted) {
			((MapHeaderSearchFragment) getChildFragmentManager().findFragmentByTag(getString(R.string.tag_map_header_search_fragment))).setQuery(locationNameInEvent, true);
		}
	}


	public interface OnDetailLocationSelectionResultListener {
		public void onResultChangedLocation(LocationDTO newLocation);

		public void onResultSelectedLocation(LocationDTO newLocation);

		public void onResultUnselectedLocation();
	}
}
