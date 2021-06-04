package com.zerodsoft.scheduleweather.navermap.favorite;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
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
import com.naver.maps.map.overlay.Marker;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.databinding.FragmentFavoriteLocationBinding;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteLocationViewModel;
import com.zerodsoft.scheduleweather.event.places.interfaces.PoiItemOnClickListener;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.MarkerType;
import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.navermap.interfaces.FavoriteLocationsListener;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteLocationQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.SneakyThrows;

public class FavoriteLocationFragment extends Fragment implements OnBackPressedCallbackController, OnClickedFavoriteItem, FavoriteLocationQuery
		, SharedPreferences.OnSharedPreferenceChangeListener {
	private FragmentFavoriteLocationBinding binding;
	public static final String TAG = "FavoriteLocationFragment";

	private final FavoriteLocationsListener favoriteLocationsListener;
	private final PoiItemOnClickListener<Marker> poiItemOnClickListener;
	private final OnBackPressedCallbackController mainFragmentOnBackPressedCallbackController;
	private final BottomSheetController bottomSheetController;
	private final IMapData iMapData;

	private LatLng latLngOnCurrentLocation;

	private FavoriteLocationViewModel favoriteLocationViewModel;
	private FavoriteLocationAdapter favoriteLocationAdapter;

	private SharedPreferences sharedPreferences;
	private ArrayAdapter<CharSequence> spinnerAdapter;

	private Set<FavoriteLocationDTO> checkedFavoriteLocationSet = new HashSet<>();

	public FavoriteLocationFragment(FavoriteLocationsListener favoriteLocationsListener, OnBackPressedCallbackController onBackPressedCallbackController
			, BottomSheetController bottomSheetController
			, PoiItemOnClickListener<Marker> poiItemOnClickListener, IMapData iMapData) {
		this.mainFragmentOnBackPressedCallbackController = onBackPressedCallbackController;
		this.bottomSheetController = bottomSheetController;
		this.favoriteLocationsListener = favoriteLocationsListener;
		this.poiItemOnClickListener = poiItemOnClickListener;
		this.iMapData = iMapData;
	}

	public void setLatLngOnCurrentLocation(LatLng latLngOnCurrentLocation) {
		this.latLngOnCurrentLocation = latLngOnCurrentLocation;
	}

	public OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
		@Override
		public void handleOnBackPressed() {
			getParentFragmentManager().popBackStack();
		}
	};

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);

		if (hidden) {
			removeOnBackPressedCallback();
			if (getParentFragmentManager().getBackStackEntryCount() == 0) {
				mainFragmentOnBackPressedCallbackController.addOnBackPressedCallback();
			}
			bottomSheetController.setStateOfBottomSheet(BottomSheetType.FAVORITE_LOCATIONS, BottomSheetBehavior.STATE_COLLAPSED);
		} else {
			refresh();
			addOnBackPressedCallback();
			if (getParentFragmentManager().getBackStackEntryCount() == 0) {
				mainFragmentOnBackPressedCallbackController.removeOnBackPressedCallback();
			}
			bottomSheetController.setStateOfBottomSheet(BottomSheetType.FAVORITE_LOCATIONS, BottomSheetBehavior.STATE_EXPANDED);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
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

		binding.errorText.setVisibility(View.GONE);
		binding.deleteFavoriteLocations.setVisibility(View.GONE);

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

				iMapData.showPoiItems(MarkerType.FAVORITE, isChecked);
			}
		});

		favoriteLocationViewModel = new ViewModelProvider(this).get(FavoriteLocationViewModel.class);
		binding.favoriteLocationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
		binding.favoriteLocationRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

		favoriteLocationAdapter = new FavoriteLocationAdapter(this, checkBoxOnCheckedChangeListener);
		binding.favoriteLocationRecyclerView.setAdapter(favoriteLocationAdapter);

		favoriteLocationAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				super.onItemRangeInserted(positionStart, itemCount);
				if (binding.errorText.getVisibility() == View.VISIBLE) {
					binding.errorText.setVisibility(View.GONE);
				}
			}

			@Override
			public void onItemRangeRemoved(int positionStart, int itemCount) {
				super.onItemRangeRemoved(positionStart, itemCount);
				if (favoriteLocationAdapter.getItemCount() == 0) {
					binding.errorText.setVisibility(View.VISIBLE);
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
					binding.editButton.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_500));
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
					List<Integer> indexInListList = new ArrayList<>();
					List<FavoriteLocationDTO> currentList = favoriteLocationAdapter.getList();

					for (FavoriteLocationDTO favoriteLocationDTO : checkedFavoriteLocationSet) {
						indexInListList.add(currentList.indexOf(favoriteLocationDTO));

						delete(favoriteLocationDTO.getId(), new CarrierMessagingService.ResultCallback<Boolean>() {
							@Override
							public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException {

							}
						});
					}

					Collections.sort(indexInListList, Comparator.reverseOrder());
					for (int index : indexInListList) {
						currentList.remove(index);
					}

					favoriteLocationAdapter.setList(currentList);
					favoriteLocationAdapter.notifyDataSetChanged();
					binding.editButton.callOnClick();
				}
			}
		});

		setFavoriteLocationList();
	}

	private void setFavoriteLocationList() {
		favoriteLocationViewModel.select(FavoriteLocationDTO.ONLY_FOR_MAP, new DbQueryCallback<List<FavoriteLocationDTO>>() {
			@Override
			public void onResultSuccessful(List<FavoriteLocationDTO> list) {
				calcDistance(list);
				sort(list);
				favoriteLocationAdapter.setList(list);

				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (list.isEmpty()) {
							binding.errorText.setVisibility(View.VISIBLE);
						} else {
							binding.errorText.setVisibility(View.GONE);
						}
						favoriteLocationsListener.createFavoriteLocationsPoiItems(list);
						iMapData.showPoiItems(MarkerType.FAVORITE, App.isPreference_key_show_favorite_locations_markers_on_map());
						favoriteLocationAdapter.notifyDataSetChanged();
					}
				});
			}

			@Override
			public void onResultNoData() {

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

	public void refresh() {
		//추가,삭제 된 경우만 동작시킨다
		favoriteLocationViewModel.select(FavoriteLocationDTO.ONLY_FOR_MAP, new DbQueryCallback<List<FavoriteLocationDTO>>() {
			@Override
			public void onResultSuccessful(List<FavoriteLocationDTO> newList) {
				Set<FavoriteLocationDTO> currentSet = new HashSet<>(favoriteLocationAdapter.getList());
				Set<FavoriteLocationDTO> newSet = new HashSet<>(newList);

				Set<FavoriteLocationDTO> addedSet = new HashSet<>(newSet);
				Set<FavoriteLocationDTO> removedSet = new HashSet<>(currentSet);

				addedSet.removeAll(currentSet);
				removedSet.removeAll(newSet);

				if (!addedSet.isEmpty() || !removedSet.isEmpty()) {
					if (!addedSet.isEmpty()) {
						favoriteLocationAdapter.getList().addAll(addedSet);
					}

					if (!removedSet.isEmpty()) {
						favoriteLocationAdapter.getList().removeAll(removedSet);
					}

					calcDistance(favoriteLocationAdapter.getList());
					sort(favoriteLocationAdapter.getList());

					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (favoriteLocationAdapter.getItemCount() == 0) {
								binding.errorText.setVisibility(View.VISIBLE);
							} else {
								binding.errorText.setVisibility(View.GONE);
							}
							favoriteLocationAdapter.notifyDataSetChanged();
						}
					});
				} else {
					calcDistance(favoriteLocationAdapter.getList());
					sort(favoriteLocationAdapter.getList());
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							favoriteLocationAdapter.notifyDataSetChanged();
						}
					});
				}
			}

			@Override
			public void onResultNoData() {

			}
		});
	}

	@Override
	public void addOnBackPressedCallback() {
		requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
	}

	@Override
	public void removeOnBackPressedCallback() {
		onBackPressedCallback.remove();
	}

	@Override
	public void onClickedListItem(FavoriteLocationDTO e, int position) {
		//맵에서 마커 클릭 후, 아이템 정보 바텀시트 보여주고 프래그먼트 바텀 시트 닫음
		poiItemOnClickListener.onPOIItemSelectedByList(position, MarkerType.FAVORITE);
		onBackPressedCallback.handleOnBackPressed();
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
						delete(e.getId(), new CarrierMessagingService.ResultCallback<Boolean>() {
							@Override
							public void onReceiveResult(@NonNull Boolean isDeleted) throws RemoteException {
								requireActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										favoriteLocationAdapter.getList().remove(index);
										favoriteLocationAdapter.notifyItemRemoved(index);
									}
								});
							}
						});
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
	public void insert(FavoriteLocationDTO favoriteLocationDTO, CarrierMessagingService.ResultCallback<FavoriteLocationDTO> callback) {
		favoriteLocationViewModel.insert(favoriteLocationDTO, new CarrierMessagingService.ResultCallback<FavoriteLocationDTO>() {
			@Override
			public void onReceiveResult(@NonNull FavoriteLocationDTO insertedFavoriteLocationDTO) throws RemoteException {
				requireActivity().runOnUiThread(new Runnable() {
					@SneakyThrows
					@Override
					public void run() {
						callback.onReceiveResult(insertedFavoriteLocationDTO);
						favoriteLocationsListener.addFavoriteLocationsPoiItem(insertedFavoriteLocationDTO);
					}
				});
			}
		});
	}

	@Override
	public void addFavoriteLocation(FavoriteLocationDTO favoriteLocationDTO) {

	}

	@Override
	public void select(Integer type, DbQueryCallback<List<FavoriteLocationDTO>> callback) {
		favoriteLocationViewModel.select(type, new DbQueryCallback<List<FavoriteLocationDTO>>() {
			@Override
			public void onResultSuccessful(List<FavoriteLocationDTO> result) {
				requireActivity().runOnUiThread(new Runnable() {
					@SneakyThrows
					@Override
					public void run() {
						callback.processResult(result);
					}
				});
			}

			@Override
			public void onResultNoData() {

			}
		});
	}

	@Override
	public void select(Integer type, Integer id, DbQueryCallback<FavoriteLocationDTO> callback) {
		favoriteLocationViewModel.select(type, id, new DbQueryCallback<FavoriteLocationDTO>() {
			@Override
			public void onResultSuccessful(FavoriteLocationDTO result) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						callback.processResult(result);
					}
				});
			}

			@Override
			public void onResultNoData() {

			}
		});
	}

	@Override
	public void delete(Integer id, CarrierMessagingService.ResultCallback<Boolean> callback) {
		favoriteLocationViewModel.select(null, id, new DbQueryCallback<FavoriteLocationDTO>() {
			@Override
			public void onResultSuccessful(FavoriteLocationDTO result) {
				favoriteLocationViewModel.delete(id, new CarrierMessagingService.ResultCallback<Boolean>() {
					@Override
					public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException {
						requireActivity().runOnUiThread(new Runnable() {
							@SneakyThrows
							@Override
							public void run() {
								callback.onReceiveResult(aBoolean);
								favoriteLocationsListener.removeFavoriteLocationsPoiItem(result);
							}
						});
					}
				});
			}

			@Override
			public void onResultNoData() {

			}
		});


	}

	@Override
	public void deleteAll(Integer type, CarrierMessagingService.ResultCallback<Boolean> callback) {
		favoriteLocationViewModel.deleteAll(type, new CarrierMessagingService.ResultCallback<Boolean>() {
			@Override
			public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException {
				requireActivity().runOnUiThread(new Runnable() {
					@SneakyThrows
					@Override
					public void run() {
						callback.onReceiveResult(aBoolean);
					}
				});
			}
		});
	}

	@Override
	public void deleteAll(CarrierMessagingService.ResultCallback<Boolean> callback) {

	}

	@Override
	public void contains(String placeId, String address, String latitude, String longitude, CarrierMessagingService.ResultCallback<FavoriteLocationDTO> callback) {
		favoriteLocationViewModel.contains(placeId, address, latitude, longitude, new CarrierMessagingService.ResultCallback<FavoriteLocationDTO>() {
			@Override
			public void onReceiveResult(@NonNull FavoriteLocationDTO favoriteLocationDTO) throws RemoteException {
				requireActivity().runOnUiThread(new Runnable() {
					@SneakyThrows
					@Override
					public void run() {
						callback.onReceiveResult(favoriteLocationDTO);
					}
				});
			}
		});
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