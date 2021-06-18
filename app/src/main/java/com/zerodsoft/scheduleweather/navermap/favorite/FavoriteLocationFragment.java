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

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.naver.maps.geometry.LatLng;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.databinding.FragmentFavoriteLocationBinding;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteLocationViewModel;
import com.zerodsoft.scheduleweather.event.places.interfaces.MarkerOnClickListener;
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

public class FavoriteLocationFragment extends Fragment implements OnClickedFavoriteItem
		, SharedPreferences.OnSharedPreferenceChangeListener {
	private FragmentFavoriteLocationBinding binding;
	private MarkerOnClickListener markerOnClickListener;
	private BottomSheetController bottomSheetController;
	private IMapData iMapData;
	private IMapPoint iMapPoint;
	private MapSharedViewModel mapSharedViewModel;

	private LatLng latLngOnCurrentLocation;

	private FavoriteLocationViewModel favoriteLocationViewModel;
	private FavoriteLocationAdapter favoriteLocationAdapter;

	private SharedPreferences sharedPreferences;
	private ArrayAdapter<CharSequence> spinnerAdapter;

	private Set<FavoriteLocationDTO> checkedFavoriteLocationSet = new HashSet<>();

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

				favoriteLocationAdapter.setList(list);
				favoriteLocationAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);

		mapSharedViewModel = new ViewModelProvider(getParentFragment()).get(MapSharedViewModel.class);
		iMapData = mapSharedViewModel.getiMapData();
		bottomSheetController = mapSharedViewModel.getBottomSheetController();
		markerOnClickListener = mapSharedViewModel.getPoiItemOnClickListener();
		iMapPoint = mapSharedViewModel.getiMapPoint();

		favoriteLocationViewModel = new ViewModelProvider(getParentFragment()).get(FavoriteLocationViewModel.class);
		latLngOnCurrentLocation = iMapPoint.getMapCenterPoint();

		favoriteLocationViewModel.getAddedFavoriteLocationMutableLiveData().observe(this, new Observer<FavoriteLocationDTO>() {
			@Override
			public void onChanged(FavoriteLocationDTO addedFavoriteLocationDTO) {
				if (addedFavoriteLocationDTO.getType() != FavoriteLocationDTO.RESTAURANT) {
					List<FavoriteLocationDTO> list = favoriteLocationAdapter.getList();
					list.add(addedFavoriteLocationDTO);

					calcDistance(list);
					sort(list);

					favoriteLocationAdapter.notifyDataSetChanged();
				}
			}
		});

		favoriteLocationViewModel.getRemovedFavoriteLocationMutableLiveData().observe(this, new Observer<FavoriteLocationDTO>() {
			@Override
			public void onChanged(FavoriteLocationDTO favoriteLocationDTO) {
				List<FavoriteLocationDTO> list = favoriteLocationAdapter.getList();

				int indexOfList = 0;
				int listSize = list.size();
				for (; indexOfList < listSize; indexOfList++) {
					if (list.get(indexOfList).getId().equals(favoriteLocationDTO.getId())) {
						list.remove(indexOfList);
						break;
					}
				}

				favoriteLocationAdapter.notifyItemRemoved(indexOfList);
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentFavoriteLocationBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.deleteFavoriteLocations.setVisibility(View.GONE);
		binding.customProgressView.setContentView(binding.favoriteLocationRecyclerView);

		spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
				R.array.favorite_locations_sort_spinner, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		binding.sortSpinner.setAdapter(spinnerAdapter);
		binding.sortSpinner.setSelection(1);
		binding.sortSpinner.setOnItemSelectedListener(spinnerItemSelectedListener);

		boolean showFavoriteLocationsMarkersOnMap = App.isPreference_key_show_favorite_locations_markers_on_map();
		binding.switchShowFavoriteLocationsMarkerOnMap.setChecked(showFavoriteLocationsMarkersOnMap);
		binding.switchShowFavoriteLocationsMarkerOnMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putBoolean(getString(R.string.preference_key_show_favorite_locations_markers_on_map), isChecked);
				App.setPreference_key_show_favorite_locations_markers_on_map(isChecked);
				editor.commit();

				iMapData.showMarkers(MarkerType.FAVORITE, isChecked);
			}
		});

		binding.favoriteLocationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
		binding.favoriteLocationRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

		favoriteLocationAdapter = new FavoriteLocationAdapter(this, checkBoxOnCheckedChangeListener, View.VISIBLE);
		binding.favoriteLocationRecyclerView.setAdapter(favoriteLocationAdapter);

		favoriteLocationAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				super.onItemRangeInserted(positionStart, itemCount);
				if (positionStart == 0 && itemCount > 0) {
					binding.customProgressView.onSuccessfulProcessingData();
				}
			}

			@Override
			public void onItemRangeRemoved(int positionStart, int itemCount) {
				super.onItemRangeRemoved(positionStart, itemCount);
				if (favoriteLocationAdapter.getItemCount() == 0) {
					binding.customProgressView.onFailedProcessingData(getString(R.string.empty_favorite_locations_list));
				}
			}

			@Override
			public void onChanged() {
				super.onChanged();
				if (favoriteLocationAdapter.getItemCount() == 0) {
					binding.customProgressView.onFailedProcessingData(getString(R.string.empty_favorite_locations_list));
				} else {
					binding.customProgressView.onSuccessfulProcessingData();
				}
			}
		});

		binding.editButton.setOnClickListener(new View.OnClickListener() {
			boolean isChecked = false;

			@Override
			public void onClick(View view) {
				isChecked = !isChecked;

				if (isChecked) {
					binding.editButton.setTextColor(Color.BLUE);
					binding.deleteFavoriteLocations.setVisibility(View.VISIBLE);
					favoriteLocationAdapter.setCheckBoxVisibility(View.VISIBLE);
				} else {
					binding.editButton.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_600));
					binding.deleteFavoriteLocations.setVisibility(View.GONE);
					favoriteLocationAdapter.setCheckBoxVisibility(View.GONE);
				}
				checkedFavoriteLocationSet.clear();
				favoriteLocationAdapter.notifyDataSetChanged();
			}
		});

		binding.deleteFavoriteLocations.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (checkedFavoriteLocationSet.isEmpty()) {
					Toast.makeText(getContext(), R.string.not_checked_favorite_locations, Toast.LENGTH_SHORT).show();
				} else {
					for (FavoriteLocationDTO favoriteLocationDTO : checkedFavoriteLocationSet) {
						favoriteLocationViewModel.delete(favoriteLocationDTO, null);
					}

					binding.editButton.callOnClick();
				}
			}
		});

		setFavoriteLocationList();
	}

	private void setFavoriteLocationList() {
		binding.customProgressView.onStartedProcessingData();

		favoriteLocationViewModel.getFavoriteLocations(FavoriteLocationDTO.ONLY_FOR_MAP, new DbQueryCallback<List<FavoriteLocationDTO>>() {
			@Override
			public void onResultSuccessful(List<FavoriteLocationDTO> list) {
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

			@Override
			public void onResultNoData() {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						binding.customProgressView.onFailedProcessingData(getString(R.string.empty_favorite_locations_list));
					}
				});
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
		if (!binding.switchShowFavoriteLocationsMarkerOnMap.isChecked()) {
			binding.switchShowFavoriteLocationsMarkerOnMap.setChecked(true);
		}
		getParentFragmentManager().popBackStackImmediate();
		markerOnClickListener.onFavoritePOIItemSelectedByList(e);
	}

	@Override
	public void deleteListItem(FavoriteLocationDTO e, int position) {

	}

	@Override
	public void onClickedEditButton(FavoriteLocationDTO e, View anchorView, int index) {
		PopupMenu popupMenu = new PopupMenu(getContext(), anchorView, Gravity.LEFT);

		popupMenu.getMenuInflater().inflate(R.menu.favorite_locations_menu, popupMenu.getMenu());
		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@SuppressLint("NonConstantResourceId")
			@Override
			public boolean onMenuItemClick(MenuItem menuItem) {
				switch (menuItem.getItemId()) {
					case R.id.delete_favorite_location:
						favoriteLocationViewModel.delete(e, null);
						break;
				}
				return true;
			}
		});

		popupMenu.show();
	}

	@Override
	public void onClickedShareButton(FavoriteLocationDTO e) {

	}

	private void sort(List<FavoriteLocationDTO> list) {
		if (binding.sortSpinner.getSelectedItemPosition() == 0) {
			//distance
			list.sort(distanceComparator);
		} else if (binding.sortSpinner.getSelectedItemPosition() == 1) {
			//datetime
			list.sort(addedDateTimeComparator);
		} else {
			//카테고리 유형 순서 - 음식점, 주소, 장소
			//먼저 카테고리 분류를 시행
			Set<Integer> categoryTypeSet = new HashSet<>();
			Map<Integer, List<FavoriteLocationDTO>> sortedListMap = new HashMap<>();

			for (FavoriteLocationDTO favoriteLocationDTO : list) {
				categoryTypeSet.add(favoriteLocationDTO.getType());
			}

			for (Integer type : categoryTypeSet) {
				sortedListMap.put(type, new ArrayList<>());
			}

			for (FavoriteLocationDTO favoriteLocationDTO : list) {
				sortedListMap.get(favoriteLocationDTO.getType()).add(favoriteLocationDTO);
			}

			list.clear();
			for (Integer type : categoryTypeSet) {
				list.addAll(sortedListMap.get(type));
			}
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

	}

	private final CompoundButton.OnCheckedChangeListener checkBoxOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
			FavoriteLocationDTO favoriteLocationDTO = (FavoriteLocationDTO) compoundButton.getTag();

			if (b) {
				checkedFavoriteLocationSet.add(favoriteLocationDTO);
			} else {
				checkedFavoriteLocationSet.remove(favoriteLocationDTO);
			}
		}
	};

	private final AdapterView.OnItemSelectedListener spinnerItemSelectedListener = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
			sort(favoriteLocationAdapter.getList());
			favoriteLocationAdapter.notifyDataSetChanged();
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapterView) {

		}
	};

	private final Comparator<FavoriteLocationDTO> addedDateTimeComparator = new Comparator<FavoriteLocationDTO>() {
		@Override
		public int compare(FavoriteLocationDTO t1, FavoriteLocationDTO t2) {
			return Long.compare(Long.parseLong(t1.getAddedDateTime()), Long.parseLong(t2.getAddedDateTime()));
		}
	};

	private final Comparator<FavoriteLocationDTO> distanceComparator = new Comparator<FavoriteLocationDTO>() {
		@Override
		public int compare(FavoriteLocationDTO t1, FavoriteLocationDTO t2) {
			return Integer.compare(t1.getDistance(), t2.getDistance());
		}
	};
}