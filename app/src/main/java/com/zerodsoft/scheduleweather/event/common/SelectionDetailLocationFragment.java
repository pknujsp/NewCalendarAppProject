package com.zerodsoft.scheduleweather.event.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.Projection;
import com.naver.maps.map.overlay.Marker;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.enums.LocationIntentCode;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.NaverMapFragment;
import com.zerodsoft.scheduleweather.navermap.searchheader.MapHeaderMainFragment;
import com.zerodsoft.scheduleweather.navermap.searchheader.MapHeaderSearchFragment;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.utility.NetworkStatus;

import org.jetbrains.annotations.NotNull;

public class SelectionDetailLocationFragment extends NaverMapFragment {
	private LocationDTO selectedLocationDTOInEvent;
	private LocationIntentCode requestCode;
	private OnDetailLocationSelectionResultListener onDetailLocationSelectionResultListener;

	private String locationNameInEvent;
	private Marker selectedLocationMarker;

	boolean mapReady = false;
	boolean mapHeaderSearchFragmentStarted = false;
	public boolean edited = false;

	private NetworkStatus networkStatus;

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
		networkStatus = new NetworkStatus(getContext(), new ConnectivityManager.NetworkCallback() {
			@Override
			public void onLost(Network network) {
				super.onLost(network);
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
					}
				});
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		binding.bottomNavigation.setVisibility(View.GONE);
		binding.naverMapButtonsLayout.favoriteLocationsButton.setVisibility(View.GONE);

		setPlaceBottomSheetSelectBtnVisibility(View.VISIBLE);
		setPlaceBottomSheetUnSelectBtnVisibility(View.GONE);

		if (requestCode == LocationIntentCode.REQUEST_CODE_CHANGE_LOCATION) {
			binding.naverMapButtonsLayout.favoriteLocationsButton.setVisibility(View.GONE);
			binding.naverMapButtonsLayout.currentAddress.setVisibility(View.GONE);
			binding.naverMapButtonsLayout.gpsButton.setVisibility(View.GONE);
			binding.headerLayout.setVisibility(View.GONE);

			Object[] selectedLocationInfoBottomSheetResult =
					createBottomSheet(R.id.selected_location_info_bottomsheet_fragment_container);
			LinearLayout selectedLocationInfoBottomSheet = (LinearLayout) selectedLocationInfoBottomSheetResult[0];
			BottomSheetBehavior selectedLocationInfoBottomSheetBehavior = (BottomSheetBehavior) selectedLocationInfoBottomSheetResult[1];

			bottomSheetBehaviorMap.put(BottomSheetType.SELECTED_LOCATION_INFO, selectedLocationInfoBottomSheetBehavior);
			bottomSheetViewMap.put(BottomSheetType.SELECTED_LOCATION_INFO, selectedLocationInfoBottomSheet);

			selectedLocationInfoBottomSheet.findViewById(R.id.selected_location_info_bottomsheet_fragment_container)
					.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

			selectedLocationInfoBottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

			selectedLocationInfoBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
				boolean initializing = true;
				boolean firstInitializing = true;
				float mapTranslationYByInfoBottomSheet;

				@Override
				public void onStateChanged(@NonNull View bottomSheet, int newState) {
					//바텀 시트의 상태에 따라서 카메라를 이동시킬 Y값
					if (firstInitializing) {
						firstInitializing = false;

						final int bottomSheetTopY = binding.naverMapViewLayout.getHeight() - selectedLocationInfoBottomSheet.getHeight();
						final int SIZE_BETWEEN_HEADER_BAR_BOTTOM_AND_BOTTOM_SHEET_TOP = bottomSheetTopY;

						Projection projection = naverMap.getProjection();
						PointF point = projection.toScreenLocation(naverMap.getContentBounds().getCenter());

						mapTranslationYByInfoBottomSheet = (float) (point.y - (SIZE_BETWEEN_HEADER_BAR_BOTTOM_AND_BOTTOM_SHEET_TOP / 2.0));
					}

					switch (newState) {
						case BottomSheetBehavior.STATE_EXPANDED: {
							if (initializing) {
								PointF movePoint = new PointF(0f, -mapTranslationYByInfoBottomSheet);
								CameraUpdate cameraUpdate = CameraUpdate.scrollBy(movePoint);
								//naverMap.moveCamera(cameraUpdate);
								initializing = false;
							}
							break;
						}
						case BottomSheetBehavior.STATE_COLLAPSED: {
							PointF movePoint = new PointF(0f, mapTranslationYByInfoBottomSheet);
							CameraUpdate cameraUpdate = CameraUpdate.scrollBy(movePoint);
							//naverMap.moveCamera(cameraUpdate);
							initializing = true;
							break;
						}
					}
				}

				@Override
				public void onSlide(@NonNull View bottomSheet, float slideOffset) {
					//expanded일때 offset == 1.0, collapsed일때 offset == 0.0
					//offset에 따라서 버튼들이 이동하고, 지도의 좌표가 변경되어야 한다.
					float translationValue = -selectedLocationInfoBottomSheet.getHeight() * slideOffset;
					binding.naverMapButtonsLayout.getRoot().animate().translationY(translationValue);
				}
			});
		}

		loadMap();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		onBackPressedCallback.remove();
		networkStatus.unregisterNetworkCallback();
	}

	private void showMarkerOfSelectedLocation() {
		selectedLocationMarker = new Marker();
		String caption = null;

		if (selectedLocationDTOInEvent.getLocationType() == LocationType.ADDRESS) {
			caption = selectedLocationDTOInEvent.getAddressName();
		} else if (selectedLocationDTOInEvent.getLocationType() == LocationType.PLACE) {
			caption = selectedLocationDTOInEvent.getPlaceName();
		}

		selectedLocationMarker.setCaptionText(caption);
		selectedLocationMarker.setCaptionColor(Color.BLACK);
		selectedLocationMarker.setPosition(new LatLng(Double.parseDouble(selectedLocationDTOInEvent.getLatitude())
				, Double.parseDouble(selectedLocationDTOInEvent.getLongitude())));

		int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 46f, getResources().getDisplayMetrics());
		int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42f, getResources().getDisplayMetrics());
		selectedLocationMarker.setWidth(width);
		selectedLocationMarker.setWidth(height);

		selectedLocationMarker.setMap(naverMap);
		CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(selectedLocationMarker.getPosition(), 13);
		naverMap.moveCamera(cameraUpdate);
	}

	@Override
	public void onSelectedLocation(KakaoLocalDocument kakaoLocalDocument) {
		saveLocation();
	}

	private void saveLocation() {
		LocationDTO newLocationDto = getSelectedLocationDto();

		String locationName = newLocationDto.getLocationType() == LocationType.PLACE ? newLocationDto.getPlaceName() : newLocationDto.getAddressName();
		Toast.makeText(getContext(), locationName + " - " + getString(R.string.selected_location), Toast.LENGTH_SHORT).show();

		if (requestCode == LocationIntentCode.REQUEST_CODE_CHANGE_LOCATION) {
			onDetailLocationSelectionResultListener.onResultChangedLocation(newLocationDto);
		} else {
			onDetailLocationSelectionResultListener.onResultSelectedLocation(newLocationDto);
		}
		edited = true;
		getParentFragmentManager().popBackStackImmediate();
	}

	@Override
	public void onRemovedLocation() {
	}

	@Override
	public void onMapReady(@NonNull @NotNull NaverMap naverMap) {
		super.onMapReady(naverMap);
		mapReady = true;

		Bundle arguments = getArguments();

		switch (requestCode) {
			case REQUEST_CODE_SELECT_LOCATION_EMPTY_QUERY:
				setCurrentAddress();
				//지도 기본 화면 표시
				break;
			case REQUEST_CODE_SELECT_LOCATION_BY_QUERY:
				locationNameInEvent = arguments.getString(DetailLocationSelectorKey.LOCATION_NAME_IN_EVENT.value());
				attemptSearchQuery();
				setCurrentAddress();
				break;
			case REQUEST_CODE_CHANGE_LOCATION:
				selectedLocationDTOInEvent = arguments.getParcelable(DetailLocationSelectorKey.SELECTED_LOCATION_DTO_IN_EVENT.value());
				showMarkerOfSelectedLocation();

				mapFragment.getMapView().setOnTouchListener(new View.OnTouchListener() {
					@Override
					public boolean onTouch(View view, MotionEvent motionEvent) {
						return true;
					}
				});

				SelectedLocationInfoFragment selectedLocationInfoFragment = new SelectedLocationInfoFragment(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Toast.makeText(getContext(), R.string.canceled_location, Toast.LENGTH_SHORT).show();

						onDetailLocationSelectionResultListener.onResultUnselectedLocation();
						edited = true;
						getParentFragmentManager().popBackStackImmediate();
					}
				}, new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						setStateOfBottomSheet(BottomSheetType.SELECTED_LOCATION_INFO, BottomSheetBehavior.STATE_COLLAPSED);
						selectedLocationMarker.setMap(null);
						Toast.makeText(getContext(), R.string.canceled_location, Toast.LENGTH_SHORT).show();

						mapFragment.getMapView().setOnTouchListener(null);
						binding.naverMapButtonsLayout.favoriteLocationsButton.setVisibility(View.VISIBLE);
						binding.naverMapButtonsLayout.currentAddress.setVisibility(View.VISIBLE);
						binding.naverMapButtonsLayout.gpsButton.setVisibility(View.VISIBLE);
						binding.headerLayout.setVisibility(View.VISIBLE);
					}
				});

				Bundle bundle = new Bundle();
				bundle.putParcelable("selectedLocationDto", selectedLocationDTOInEvent);
				selectedLocationInfoFragment.setArguments(bundle);

				getChildFragmentManager().beginTransaction()
						.add(bottomSheetViewMap.get(BottomSheetType.SELECTED_LOCATION_INFO).getChildAt(0).getId()
								, selectedLocationInfoFragment, getString(R.string.tag_selected_location_info_fragment)).commitNow();

				setStateOfBottomSheet(BottomSheetType.SELECTED_LOCATION_INFO, BottomSheetBehavior.STATE_EXPANDED);
				break;
		}

	}

	private void attemptSearchQuery() {
		if (mapReady && mapHeaderSearchFragmentStarted) {
			((MapHeaderSearchFragment) getChildFragmentManager().findFragmentByTag(getString(R.string.tag_map_header_search_fragment))).setQuery(locationNameInEvent, true);
		}
	}

	@Override
	protected void onResultLocationPermission(boolean isGranted) {
		super.onResultLocationPermission(isGranted);
	}

	public interface OnDetailLocationSelectionResultListener {
		public void onResultChangedLocation(LocationDTO newLocation);

		public void onResultSelectedLocation(LocationDTO newLocation);

		public void onResultUnselectedLocation();
	}

}
