package com.zerodsoft.scheduleweather.navermap.favorite;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.naver.maps.geometry.LatLng;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteLocationViewModel;
import com.zerodsoft.scheduleweather.event.places.interfaces.MarkerOnClickListener;
import com.zerodsoft.scheduleweather.favorites.addressplace.basefragment.FavoriteLocationsBaseFragment;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.MarkerType;
import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.navermap.viewmodel.MapSharedViewModel;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FavoriteLocationFragment extends FavoriteLocationsBaseFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
	private MarkerOnClickListener markerOnClickListener;
	private BottomSheetController bottomSheetController;
	private IMapData iMapData;
	private IMapPoint iMapPoint;
	private MapSharedViewModel mapSharedViewModel;
	private LatLng latLngOnCurrentLocation;
	private SharedPreferences sharedPreferences;
	private SwitchMaterial markerVisibilitySwitch;

	@Override
	public void onAddedFavoriteLocation(FavoriteLocationDTO addedFavoriteLocation) {
		List<FavoriteLocationDTO> list = favoriteLocationAdapter.getList();
		list.add(addedFavoriteLocation);
		calcDistance(list);
		sort(list);
		favoriteLocationAdapter.notifyDataSetChanged();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (hidden) {
			bottomSheetController.setStateOfBottomSheet(BottomSheetType.FAVORITE_LOCATIONS, BottomSheetBehavior.STATE_COLLAPSED);
		} else {
			LatLng newLatLngOnCurrentLocation = iMapPoint.getMapCenterPoint();
			if (!latLngOnCurrentLocation.equals(newLatLngOnCurrentLocation)) {
				latLngOnCurrentLocation = newLatLngOnCurrentLocation;
				List<FavoriteLocationDTO> list = favoriteLocationAdapter.getList();

				calcDistance(list);
				sort(list);

				favoriteLocationAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);

		mapSharedViewModel = new ViewModelProvider(getParentFragment()).get(MapSharedViewModel.class);
		iMapData = mapSharedViewModel.getiMapData();
		bottomSheetController = mapSharedViewModel.getBottomSheetController();
		markerOnClickListener = mapSharedViewModel.getPoiItemOnClickListener();
		iMapPoint = mapSharedViewModel.getiMapPoint();

		favoriteLocationViewModel = new ViewModelProvider(getParentFragment()).get(FavoriteLocationViewModel.class);
		latLngOnCurrentLocation = iMapPoint.getMapCenterPoint();

		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		binding.showAllFavorites.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (favoriteLocationAdapter.getItemCount() > 0) {
					if (!markerVisibilitySwitch.isChecked()) {
						markerVisibilitySwitch.setChecked(true);
					}
					iMapData.showMarkers(MarkerType.FAVORITE);
					getParentFragmentManager().popBackStackImmediate();
				} else {
					Toast.makeText(getContext(), R.string.empty_favorite_locations_list, Toast.LENGTH_SHORT).show();
				}
			}
		});

		LinearLayout settingsLayout = new LinearLayout(getContext());
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics());
		settingsLayout.setLayoutParams(layoutParams);
		settingsLayout.setOrientation(LinearLayout.VERTICAL);

		binding.rootLinearLayout.addView(settingsLayout, 1);

		markerVisibilitySwitch = new SwitchMaterial(getContext());
		markerVisibilitySwitch.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		markerVisibilitySwitch.setText(R.string.show_favorite_locations_marker_on_map);

		settingsLayout.addView(markerVisibilitySwitch);

		spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
				R.array.favorite_locations_sort_spinner, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		binding.sortSpinnerForAddressPlace.setAdapter(spinnerAdapter);
		binding.sortSpinnerForAddressPlace.setSelection(1);
		binding.sortSpinnerForAddressPlace.setOnItemSelectedListener(spinnerItemSelectedListener);

		boolean showFavoriteLocationsMarkersOnMap = App.isPreference_key_show_favorite_locations_markers_on_map();
		markerVisibilitySwitch.setChecked(showFavoriteLocationsMarkersOnMap);
		markerVisibilitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putBoolean(getString(R.string.preference_key_show_favorite_locations_markers_on_map), isChecked);
				App.setPreference_key_show_favorite_locations_markers_on_map(isChecked);
				editor.commit();

				iMapData.showMarkers(MarkerType.FAVORITE, isChecked);
			}
		});

		favoriteLocationAdapter.setDistanceVisibility(View.VISIBLE);
		setFavoriteLocationList();
	}

	@Override
	protected void onLoadedFavoriteLocationsList(List<FavoriteLocationDTO> list) {
		calcDistance(list);
		sort(list);
		favoriteLocationAdapter.setList(list);

		requireActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				favoriteLocationAdapter.notifyDataSetChanged();
			}
		});
	}

	private void calcDistance(List<FavoriteLocationDTO> list) {
		LatLng latLng = null;

		for (FavoriteLocationDTO data : list) {
			latLng = new LatLng(Double.parseDouble(data.getLatitude()), Double.parseDouble(data.getLongitude()));

			double distance = latLngOnCurrentLocation.distanceTo(latLng);
			data.setDistance((int) distance);
		}
	}


	@Override
	public void onClickedListItem(FavoriteLocationDTO e, int position) {
		//맵에서 마커 클릭 후, 아이템 정보 바텀시트 보여주고 프래그먼트 바텀 시트 닫음
		if (!markerVisibilitySwitch.isChecked()) {
			markerVisibilitySwitch.setChecked(true);
		}
		getParentFragmentManager().popBackStackImmediate();
		markerOnClickListener.onFavoritePOIItemSelectedByList(e);
	}


	@Override
	public void onClickedShareButton(FavoriteLocationDTO e) {

	}

	@Override
	protected void sort(List<FavoriteLocationDTO> list) {
		switch (binding.sortSpinnerForAddressPlace.getSelectedItemPosition()) {
			case 0:
				//거리순
				list.sort(distanceComparator);
				break;
			case 1:
				//등록순
				sortByAddedDateTime(list);
				break;
			case 2:
				//카테고리 별
				sortByAddedCategory(list);
				break;
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

	}


	private final Comparator<FavoriteLocationDTO> distanceComparator = new Comparator<FavoriteLocationDTO>() {
		@Override
		public int compare(FavoriteLocationDTO t1, FavoriteLocationDTO t2) {
			return Integer.compare(t1.getDistance(), t2.getDistance());
		}
	};
}