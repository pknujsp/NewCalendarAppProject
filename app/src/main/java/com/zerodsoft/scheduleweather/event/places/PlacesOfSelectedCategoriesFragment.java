package com.zerodsoft.scheduleweather.event.places;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.activity.placecategory.viewmodel.PlaceCategoryViewModel;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.common.interfaces.OnHiddenFragmentListener;
import com.zerodsoft.scheduleweather.common.view.CustomProgressView;
import com.zerodsoft.scheduleweather.databinding.PlacelistFragmentBinding;
import com.zerodsoft.scheduleweather.etc.CustomRecyclerViewItemDecoration;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.places.adapter.PlaceItemsAdapters;
import com.zerodsoft.scheduleweather.event.places.interfaces.OnClickedPlacesListListener;
import com.zerodsoft.scheduleweather.event.places.interfaces.PlaceItemsGetter;
import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.navermap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.navermap.viewmodel.MapSharedViewModel;
import com.zerodsoft.scheduleweather.navermap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnExtraListDataListener;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlacesOfSelectedCategoriesFragment extends Fragment implements PlaceItemsGetter, OnExtraListDataListener<String> {
	private final long EVENT_ID;
	private final OnClickedPlacesListListener onClickedPlacesListListener;

	private DecimalFormat decimalFormat = new DecimalFormat("#.#");
	private BottomSheetController bottomSheetController;
	private OnHiddenFragmentListener onHiddenFragmentListener;

	private PlacelistFragmentBinding binding;
	private Set<PlaceCategoryDTO> placeCategorySet = new HashSet<>();
	private LocationDTO selectedLocationDto;

	private PlaceCategoryViewModel placeCategoryViewModel;
	private LocationViewModel locationViewModel;
	private MapSharedViewModel mapSharedViewModel;

	private ArrayMap<String, PlaceItemsAdapters> adaptersMap = new ArrayMap<>();
	private ArrayMap<String, RecyclerView> listMap = new ArrayMap<>();


	public PlacesOfSelectedCategoriesFragment(long EVENT_ID, OnClickedPlacesListListener onClickedPlacesListListener,
	                                          OnHiddenFragmentListener onHiddenFragmentListener) {
		this.EVENT_ID = EVENT_ID;
		this.onClickedPlacesListListener = onClickedPlacesListListener;
		this.onHiddenFragmentListener = onHiddenFragmentListener;
	}


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mapSharedViewModel = new ViewModelProvider(getParentFragment()).get(MapSharedViewModel.class);
		bottomSheetController = mapSharedViewModel.getBottomSheetController();

		locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
		placeCategoryViewModel = new ViewModelProvider(getParentFragment()).get(PlaceCategoryViewModel.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = PlacelistFragmentBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		onHiddenFragmentListener.onHiddenChangedFragment(hidden);
		if (hidden) {

		} else {

		}
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.customProgressView.setContentView(binding.categoryViewlist);
		binding.customProgressView.onStartedProcessingData();

		binding.radiusSeekbarLayout.setVisibility(View.GONE);
		DecimalFormat decimalFormat = new DecimalFormat("#.#");
		float value = Math.round((Float.parseFloat(App.getPreference_key_radius_range()) / 1000f) * 10) / 10f;
		binding.radiusSeekbar.setValue(Float.parseFloat(decimalFormat.format(value)));

		binding.searchRadius.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				binding.radiusSeekbarLayout.setVisibility(binding.radiusSeekbarLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
			}
		});

		binding.applyRadius.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//변경한 값 적용
				binding.radiusSeekbarLayout.setVisibility(View.GONE);

				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
				SharedPreferences.Editor editor = preferences.edit();

				final String newValueStrMeter = String.valueOf((int) (binding.radiusSeekbar.getValue() * 1000));
				editor.putString(getString(R.string.preference_key_radius_range), newValueStrMeter);
				editor.apply();

				App.setPreference_key_radius_range(newValueStrMeter);
				setSearchRadius();

				makeCategoryListView();
			}
		});

		setSearchRadius();
		locationViewModel.getLocation(EVENT_ID, new DbQueryCallback<LocationDTO>() {
			@Override
			public void onResultSuccessful(LocationDTO locationResultDto) {
				selectedLocationDto = locationResultDto;
				makeCategoryListView();
			}

			@Override
			public void onResultNoData() {

			}
		});
	}


	private void setSearchRadius() {
		float value = Math.round((Float.parseFloat(App.getPreference_key_radius_range()) / 1000f) * 10) / 10f;
		binding.searchRadius.setText(getString(R.string.search_radius) + " " + decimalFormat.format(value) + "km");
	}

	public void makeCategoryListView() {
		placeCategoryViewModel.selectConvertedSelected(new DbQueryCallback<List<PlaceCategoryDTO>>() {
			@Override
			public void onResultSuccessful(List<PlaceCategoryDTO> newPlaceCategoriesList) {
				placeCategorySet.addAll(newPlaceCategoriesList);
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						binding.addressName.setText(selectedLocationDto.getAddressName());

						if (newPlaceCategoriesList.isEmpty()) {
							binding.customProgressView.onFailedProcessingData(getString(R.string.not_selected_place_category));
							return;
						} else {
							binding.customProgressView.onSuccessfulProcessingData();
						}

						binding.categoryViewlist.removeAllViews();
						adaptersMap.clear();
						listMap.clear();

						final String rangeRadius = App.getPreference_key_radius_range();

						for (PlaceCategoryDTO placeCategory : newPlaceCategoriesList) {
							addCategoryView(placeCategory, rangeRadius);
						}
					}
				});
			}

			@Override
			public void onResultNoData() {

			}
		});
	}

	public void refreshList() {
		placeCategoryViewModel.selectConvertedSelected(new DbQueryCallback<List<PlaceCategoryDTO>>() {
			@Override
			public void onResultSuccessful(List<PlaceCategoryDTO> newPlaceCategoryList) {
				Set<PlaceCategoryDTO> newSet = new HashSet<>();
				newSet.addAll(newPlaceCategoryList);

				Set<PlaceCategoryDTO> removedSet = new HashSet<>(placeCategorySet);
				Set<PlaceCategoryDTO> addedSet = new HashSet<>(newSet);

				removedSet.removeAll(newSet);
				addedSet.removeAll(placeCategorySet);

				if (!removedSet.isEmpty() || !addedSet.isEmpty()) {
					placeCategorySet = newSet;

					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							//삭제
							if (!removedSet.isEmpty()) {
								Set<String> removeCodeSet = new HashSet<>();
								for (PlaceCategoryDTO placeCategoryDTO : removedSet) {
									removeCodeSet.add(placeCategoryDTO.getCode());
								}

								int childCount = binding.categoryViewlist.getChildCount();

								for (int index = childCount - 1; index >= 0; index--) {
									String removeCode = (String) binding.categoryViewlist.getChildAt(index).getTag();
									if (removeCodeSet.contains(removeCode)) {
										binding.categoryViewlist.removeViewAt(index);
										listMap.remove(removeCode);
										adaptersMap.remove(removeCode);
									}
								}

							}

							if (!addedSet.isEmpty()) {
								//추가
								final String rangeRadius = App.getPreference_key_radius_range();

								for (PlaceCategoryDTO placeCategory : addedSet) {
									addCategoryView(placeCategory, rangeRadius);
								}
							}

							if (newPlaceCategoryList.isEmpty()) {
								binding.customProgressView.onFailedProcessingData(getString(R.string.not_selected_place_category));
								binding.categoryViewlist.removeAllViews();
								adaptersMap.clear();
								listMap.clear();
								return;
							} else {
								binding.customProgressView.onSuccessfulProcessingData();
							}
						}
					});
				}

			}

			@Override
			public void onResultNoData() {
			}
		});
	}

	private void addCategoryView(PlaceCategoryDTO placeCategory, String rangeRadius) {
		LinearLayout categoryView = (LinearLayout) getLayoutInflater().inflate(R.layout.place_category_view, null);
		PlaceItemsAdapters adapter = new PlaceItemsAdapters(onClickedPlacesListListener, placeCategory);
		RecyclerView itemRecyclerView = (RecyclerView) categoryView.findViewById(R.id.map_category_itemsview);

		adaptersMap.put(placeCategory.getCode(), adapter);
		listMap.put(placeCategory.getCode(), itemRecyclerView);
		categoryView.setTag(placeCategory.getCode());
		binding.categoryViewlist.addView(categoryView);

		((TextView) categoryView.findViewById(R.id.map_category_name)).setText(placeCategory.getDescription());

		CustomProgressView customProgressView = (CustomProgressView) categoryView.findViewById(R.id.custom_progress_view);
		customProgressView.setContentView(itemRecyclerView);
		customProgressView.onStartedProcessingData();

		itemRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
		itemRecyclerView.addItemDecoration(new CustomRecyclerViewItemDecoration((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics())));

		itemRecyclerView.setAdapter(adapter);
		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				super.onItemRangeInserted(positionStart, itemCount);
				if (positionStart == 0) {
					customProgressView.onSuccessfulProcessingData();
				}
			}
		});

		PlacesViewModel viewModel =
				new ViewModelProvider(PlacesOfSelectedCategoriesFragment.this).get(PlacesViewModel.class);

		LocalApiPlaceParameter placeParameter = LocalParameterUtil.getPlaceParameter(placeCategory.getCode(), String.valueOf(selectedLocationDto.getLatitude()),
				String.valueOf(selectedLocationDto.getLongitude()), LocalApiPlaceParameter.DEFAULT_SIZE, LocalApiPlaceParameter.DEFAULT_PAGE,
				LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY);
		placeParameter.setRadius(rangeRadius);

		viewModel.init(placeParameter, new PagedList.BoundaryCallback<PlaceDocuments>() {
			@Override
			public void onZeroItemsLoaded() {
				super.onZeroItemsLoaded();
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						customProgressView.onFailedProcessingData(getString(R.string.not_founded_search_result));
					}
				});
			}
		});
		viewModel.getPagedListMutableLiveData().observe(PlacesOfSelectedCategoriesFragment.this.getViewLifecycleOwner(),
				new Observer<PagedList<PlaceDocuments>>() {
					@Override
					public void onChanged(PagedList<PlaceDocuments> placeDocuments) {
						adapter.submitList(placeDocuments);
					}
				});

		((Button) categoryView.findViewById(R.id.map_category_more)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onClickedPlacesListListener.onClickedMoreInList(placeCategory);
			}
		});

	}

	@Override
	public void getPlaces(DbQueryCallback<List<PlaceDocuments>> callback, String categoryCode) {
		requireActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				callback.processResult(adaptersMap.get(categoryCode).getCurrentList().snapshot());
			}
		});
	}

	@Override
	public void loadExtraListData(String placeCategoryCode, RecyclerView.AdapterDataObserver adapterDataObserver) {
		PlaceItemsAdapters adapter = adaptersMap.get(placeCategoryCode);
		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				adapterDataObserver.onItemRangeInserted(positionStart, itemCount);
				adapter.unregisterAdapterDataObserver(this);
			}

		});

		RecyclerView recyclerView = listMap.get(placeCategoryCode);
		recyclerView.scrollBy(100000, 0);
	}

	@Override
	public void loadExtraListData(RecyclerView.AdapterDataObserver adapterDataObserver) {

	}

	public interface PlaceCategoryChipsViewController {
		void createPlaceCategoryListChips();

		void addPlaceCategoryListFragmentIntoBottomSheet();

		void setPlaceCategoryChips(List<PlaceCategoryDTO> placeCategoryList);

		void addDefaultChips();
	}
}
