package com.zerodsoft.calendarplatform.navermap.searchresult;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.naver.maps.geometry.LatLng;
import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.common.classes.AppPermission;
import com.zerodsoft.calendarplatform.common.classes.Gps;
import com.zerodsoft.calendarplatform.common.interfaces.DataProcessingCallback;
import com.zerodsoft.calendarplatform.common.interfaces.OnClickedListItem;
import com.zerodsoft.calendarplatform.databinding.FragmentLocationSearchResultBinding;
import com.zerodsoft.calendarplatform.etc.LocationType;
import com.zerodsoft.calendarplatform.navermap.MarkerType;
import com.zerodsoft.calendarplatform.navermap.interfaces.IMapData;
import com.zerodsoft.calendarplatform.navermap.interfaces.IMapPoint;
import com.zerodsoft.calendarplatform.navermap.searchresult.adapter.PlacesAdapter;
import com.zerodsoft.calendarplatform.kakaoplace.LocalParameterUtil;
import com.zerodsoft.calendarplatform.navermap.viewmodel.MapSharedViewModel;
import com.zerodsoft.calendarplatform.navermap.viewmodel.PlacesViewModel;
import com.zerodsoft.calendarplatform.navermap.interfaces.OnExtraListDataListener;
import com.zerodsoft.calendarplatform.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

public class SearchResultPlaceListFragment extends Fragment implements OnExtraListDataListener<LocationType> {
	private final String QUERY;
	private final OnClickedListItem<PlaceDocuments> placeDocumentsOnClickedListItem;

	private int currentSearchMapPointCriteria = LocalApiPlaceParameter.SEARCH_CRITERIA_MAP_POINT_MAP_CENTER;
	private int currentSearchSortTypeCriteria = LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY;

	private FragmentLocationSearchResultBinding binding;

	private PlacesViewModel viewModel;
	private PlacesAdapter adapter;

	private ArrayAdapter<CharSequence> spinnerAdapter;
	private Gps gps = new Gps();

	private Location currentLocation;

	private IMapPoint iMapPoint;
	private IMapData iMapData;
	private MapSharedViewModel mapSharedViewModel;

	public SearchResultPlaceListFragment(String query, OnClickedListItem<PlaceDocuments> placeDocumentsOnClickedListItem) {
		this.QUERY = query;
		this.placeDocumentsOnClickedListItem = placeDocumentsOnClickedListItem;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mapSharedViewModel = new ViewModelProvider(getParentFragment().getParentFragment()).get(MapSharedViewModel.class);
		iMapData = mapSharedViewModel.getiMapData();
		iMapPoint = mapSharedViewModel.getiMapPoint();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = FragmentLocationSearchResultBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.customProgressView.setContentView(binding.searchResultRecyclerview);
		binding.customProgressView.onStartedProcessingData();
		binding.searchResultType.setText(getString(R.string.result_place));

		spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
				R.array.map_search_result_sort_spinner, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		binding.searchSortSpinner.setAdapter(spinnerAdapter);
		binding.searchSortSpinner.setSelection(currentSearchSortTypeCriteria == LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY ? 1 : 0, false);
		binding.searchSortSpinner.setOnItemSelectedListener(onItemSelectedListener);

		binding.searchResultRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		binding.searchResultRecyclerview.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
		viewModel = new ViewModelProvider(this).get(PlacesViewModel.class);

		binding.searchCriteriaToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
			@Override
			public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
				if (isChecked) {
					gps.removeUpdate();

					switch (checkedId) {
						case R.id.search_around_current_location:
							currentSearchMapPointCriteria = LocalApiPlaceParameter.SEARCH_CRITERIA_MAP_POINT_CURRENT_LOCATION;
							requestPlacesByGps(QUERY);
							break;
						case R.id.search_around_map_center:
							currentSearchMapPointCriteria = LocalApiPlaceParameter.SEARCH_CRITERIA_MAP_POINT_MAP_CENTER;
							requestPlaces(QUERY);
							break;
					}
				}
			}
		});

		binding.searchCriteriaToggleGroup.check(R.id.search_around_map_center);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		requestLocationPermissionLauncher.unregister();
		requestOnGpsLauncher.unregister();
		gps.removeUpdate();
	}

	private final AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
			switch (index) {
				case 0:
					//거리 순서
					currentSearchSortTypeCriteria = LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_DISTANCE;
					break;
				case 1:
					//정확도 순서
					currentSearchSortTypeCriteria = LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY;
					break;
			}

			if (currentSearchMapPointCriteria == LocalApiPlaceParameter.SEARCH_CRITERIA_MAP_POINT_CURRENT_LOCATION) {
				requestPlacesByGps(QUERY);
			} else {
				requestPlaces(QUERY);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapterView) {

		}
	};

	private void requestPlacesByGps(String query) {
		binding.customProgressView.onStartedProcessingData();
		gps.runGps(requireActivity(), new DataProcessingCallback<Location>() {
			@Override
			public void onResultSuccessful(Location result) {
				currentLocation = result;
				requestPlaces(query);
			}

			@Override
			public void onResultNoData() {
				Toast.makeText(getContext(), R.string.failed_catch_current_location, Toast.LENGTH_SHORT).show();
				binding.searchCriteriaToggleGroup.check(R.id.search_around_map_center);
			}
		}, requestOnGpsLauncher, requestLocationPermissionLauncher);
	}

	private void requestPlaces(String query) {
		binding.customProgressView.onStartedProcessingData();
		String latitude = null;
		String longitude = null;

		if (currentSearchMapPointCriteria == LocalApiPlaceParameter.SEARCH_CRITERIA_MAP_POINT_CURRENT_LOCATION) {
			latitude = String.valueOf(currentLocation.getLatitude());
			longitude = String.valueOf(currentLocation.getLongitude());
		} else {
			LatLng latLng = iMapPoint.getMapCenterPoint();
			latitude = String.valueOf(latLng.latitude);
			longitude = String.valueOf(latLng.longitude);
		}

		LocalApiPlaceParameter parameter = LocalParameterUtil.getPlaceParameter(query, latitude, longitude, LocalApiPlaceParameter.DEFAULT_SIZE,
				LocalApiPlaceParameter.DEFAULT_PAGE, currentSearchSortTypeCriteria);

		if (adapter != null) {
			iMapData.removeMarkers(MarkerType.SEARCH_RESULT_PLACE);
		}

		adapter = new PlacesAdapter(getContext(), placeDocumentsOnClickedListItem);
		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				super.onItemRangeInserted(positionStart, itemCount);

				if (positionStart > 0) {
					iMapData.addExtraMarkers(adapter.getCurrentList().snapshot(), MarkerType.SEARCH_RESULT_PLACE);
				} else {
					if (itemCount > 0) {
						iMapData.createMarkers(adapter.getCurrentList().snapshot(), MarkerType.SEARCH_RESULT_PLACE);
						binding.customProgressView.onSuccessfulProcessingData();
					}
				}
			}
		});
		binding.searchResultRecyclerview.setAdapter(adapter);

		viewModel.init(parameter, new PagedList.BoundaryCallback<PlaceDocuments>() {
			@Override
			public void onZeroItemsLoaded() {
				super.onZeroItemsLoaded();
			}
		});
		viewModel.getPagedListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<PlaceDocuments>>() {
			@Override
			public void onChanged(PagedList<PlaceDocuments> placeDocuments) {
				adapter.submitList(placeDocuments);
			}
		});
	}

	private final ActivityResultLauncher<Intent> requestOnGpsLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
			new ActivityResultCallback<ActivityResult>() {
				@Override
				public void onActivityResult(ActivityResult result) {
					if (AppPermission.grantedPermissions(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
						binding.searchCriteriaToggleGroup.check(R.id.search_around_current_location);
					} else {
						binding.searchCriteriaToggleGroup.check(R.id.search_around_map_center);
					}
				}
			});

	private ActivityResultLauncher<String> requestLocationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission()
			, new ActivityResultCallback<Boolean>() {
				@Override
				public void onActivityResult(Boolean isGranted) {
					if (isGranted) {
						// 권한 허용됨
						binding.searchCriteriaToggleGroup.check(R.id.search_around_current_location);
					} else {
						// 권한 거부됨
						Toast.makeText(getContext(), R.string.message_needs_location_permission, Toast.LENGTH_SHORT).show();
						binding.searchCriteriaToggleGroup.check(R.id.search_around_map_center);
					}
				}
			});

	@Override
	public void loadExtraListData(LocationType e, RecyclerView.AdapterDataObserver adapterDataObserver) {

	}

	@Override
	public void loadExtraListData(RecyclerView.AdapterDataObserver adapterDataObserver) {
		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				super.onItemRangeInserted(positionStart, itemCount);
				adapterDataObserver.onItemRangeInserted(positionStart, itemCount);
				adapter.unregisterAdapterDataObserver(this);
			}
		});
		binding.searchResultRecyclerview.scrollBy(0, 10000);
	}

}
