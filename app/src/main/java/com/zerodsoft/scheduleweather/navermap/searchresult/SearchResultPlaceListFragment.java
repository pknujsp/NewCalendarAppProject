package com.zerodsoft.scheduleweather.navermap.searchresult;

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

import com.naver.maps.geometry.LatLng;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.classes.AppPermission;
import com.zerodsoft.scheduleweather.common.classes.Gps;
import com.zerodsoft.scheduleweather.common.interfaces.DataProcessingCallback;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.databinding.FragmentLocationSearchResultBinding;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.navermap.MarkerType;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.navermap.searchresult.adapter.PlacesAdapter;
import com.zerodsoft.scheduleweather.navermap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.navermap.viewmodel.MapSharedViewModel;
import com.zerodsoft.scheduleweather.navermap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnExtraListDataListener;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

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

		replaceButtonStyle();

		binding.searchAroundMapCenter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				currentSearchMapPointCriteria = LocalApiPlaceParameter.SEARCH_CRITERIA_MAP_POINT_MAP_CENTER;
				replaceButtonStyle();
				requestPlaces(QUERY);
			}
		});

		binding.searchAroundCurrentLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				currentSearchMapPointCriteria = LocalApiPlaceParameter.SEARCH_CRITERIA_MAP_POINT_CURRENT_LOCATION;
				replaceButtonStyle();
				requestPlacesByGps(QUERY);
			}
		});

		requestPlaces(QUERY);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		requestLocationPermissionLauncher.unregister();
		requestOnGpsLauncher.unregister();
	}

	private void replaceButtonStyle() {
		switch (currentSearchMapPointCriteria) {
			case LocalApiPlaceParameter.SEARCH_CRITERIA_MAP_POINT_CURRENT_LOCATION:
				binding.searchAroundCurrentLocation.setTextColor(getResources().getColor(R.color.black, null));
				binding.searchAroundMapCenter.setTextColor(getResources().getColor(R.color.gray_600, null));
				break;

			case LocalApiPlaceParameter.SEARCH_CRITERIA_MAP_POINT_MAP_CENTER:
				binding.searchAroundCurrentLocation.setTextColor(getResources().getColor(R.color.gray_600, null));
				binding.searchAroundMapCenter.setTextColor(getResources().getColor(R.color.black, null));
				break;
		}
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
		gps.runGps(requireActivity(), new DataProcessingCallback<Location>() {
			@Override
			public void onResultSuccessful(Location result) {
				currentLocation = result;
				requestPlaces(query);
			}

			@Override
			public void onResultNoData() {
				Toast.makeText(getContext(), R.string.failed_catch_current_location, Toast.LENGTH_SHORT).show();
				binding.searchAroundMapCenter.callOnClick();
			}
		}, requestOnGpsLauncher, requestLocationPermissionLauncher);
	}

	private void requestPlaces(String query) {
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
						binding.searchAroundCurrentLocation.callOnClick();
					} else {
						binding.searchAroundMapCenter.callOnClick();
					}
				}
			});

	private ActivityResultLauncher<String> requestLocationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission()
			, new ActivityResultCallback<Boolean>() {
				@Override
				public void onActivityResult(Boolean isGranted) {
					if (isGranted) {
						// 권한 허용됨
						binding.searchAroundCurrentLocation.callOnClick();
					} else {
						// 권한 거부됨
						Toast.makeText(getContext(), R.string.message_needs_location_permission, Toast.LENGTH_SHORT).show();
						binding.searchAroundMapCenter.callOnClick();
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
