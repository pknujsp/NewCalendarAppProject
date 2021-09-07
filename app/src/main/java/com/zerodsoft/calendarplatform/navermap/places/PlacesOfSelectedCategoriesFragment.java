package com.zerodsoft.calendarplatform.navermap.places;

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
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.naver.maps.geometry.LatLng;
import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.activity.App;
import com.zerodsoft.calendarplatform.activity.placecategory.PlaceCategorySettingsFragment;
import com.zerodsoft.calendarplatform.activity.placecategory.viewmodel.PlaceCategoryViewModel;
import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.common.interfaces.OnHiddenFragmentListener;
import com.zerodsoft.calendarplatform.common.view.CustomProgressView;
import com.zerodsoft.calendarplatform.databinding.PlacelistFragmentBinding;
import com.zerodsoft.calendarplatform.etc.CustomRecyclerViewItemDecoration;
import com.zerodsoft.calendarplatform.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.calendarplatform.navermap.places.adapter.PlaceItemsAdapters;
import com.zerodsoft.calendarplatform.navermap.places.interfaces.OnClickedPlacesListListener;
import com.zerodsoft.calendarplatform.navermap.places.interfaces.PlaceItemsGetter;
import com.zerodsoft.calendarplatform.kakaoplace.retrofit.KakaoLocalDownloader;
import com.zerodsoft.calendarplatform.navermap.BottomSheetType;
import com.zerodsoft.calendarplatform.navermap.interfaces.BottomSheetController;
import com.zerodsoft.calendarplatform.kakaoplace.LocalParameterUtil;
import com.zerodsoft.calendarplatform.navermap.interfaces.OnKakaoLocalApiCallback;
import com.zerodsoft.calendarplatform.navermap.viewmodel.MapSharedViewModel;
import com.zerodsoft.calendarplatform.navermap.viewmodel.PlacesViewModel;
import com.zerodsoft.calendarplatform.navermap.interfaces.OnExtraListDataListener;
import com.zerodsoft.calendarplatform.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.KakaoLocalResponse;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.calendarplatform.room.dto.PlaceCategoryDTO;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlacesOfSelectedCategoriesFragment extends Fragment implements PlaceItemsGetter, OnExtraListDataListener<String> {
	private final OnClickedPlacesListListener onClickedPlacesListListener;

	private DecimalFormat decimalFormat = new DecimalFormat("#.#");
	private OnHiddenFragmentListener onHiddenFragmentListener;
	private OnRefreshCriteriaLocationListener onRefreshCriteriaLocationListener;

	private PlacelistFragmentBinding binding;
	private Set<PlaceCategoryDTO> placeCategorySet = new HashSet<>();

	private PlaceCategoryViewModel placeCategoryViewModel;
	private LocationViewModel locationViewModel;
	private MapSharedViewModel mapSharedViewModel;
	private BottomSheetController bottomSheetController;
	private LatLng newLatLng;
	private LatLng lastLatLng = new LatLng(0L, 0L);

	private ArrayMap<String, PlaceItemsAdapters> adaptersMap = new ArrayMap<>();
	private ArrayMap<String, RecyclerView> listMap = new ArrayMap<>();

	private boolean initializing = true;

	private FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
		@Override
		public void onFragmentDestroyed(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f) {
			super.onFragmentDestroyed(fm, f);
			if (f instanceof PlaceCategorySettingsFragment) {
				bottomSheetController.setStateOfBottomSheet(BottomSheetType.SELECTED_PLACE_CATEGORY, BottomSheetBehavior.STATE_EXPANDED);
			}
		}
	};

	public PlacesOfSelectedCategoriesFragment(OnClickedPlacesListListener onClickedPlacesListListener,
	                                          OnHiddenFragmentListener onHiddenFragmentListener,
	                                          OnRefreshCriteriaLocationListener onRefreshCriteriaLocationListener) {
		this.onClickedPlacesListListener = onClickedPlacesListListener;
		this.onHiddenFragmentListener = onHiddenFragmentListener;
		this.onRefreshCriteriaLocationListener = onRefreshCriteriaLocationListener;
	}

	public void setNewLatLng(LatLng newLatLng) {
		this.newLatLng = newLatLng;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getParentFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, false);

		mapSharedViewModel = new ViewModelProvider(getParentFragment()).get(MapSharedViewModel.class);
		locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
		placeCategoryViewModel = new ViewModelProvider(requireActivity()).get(PlaceCategoryViewModel.class);

		bottomSheetController = mapSharedViewModel.getBottomSheetController();
		placeCategoryViewModel.getOnSelectedCategoryLiveData().observe(this, new Observer<PlaceCategoryDTO>() {
			@Override
			public void onChanged(PlaceCategoryDTO placeCategoryDTO) {
				if (!initializing) {
					refreshList();
				}
			}
		});

		placeCategoryViewModel.getOnUnSelectedCategoryLiveData().observe(this, new Observer<String>() {
			@Override
			public void onChanged(String s) {
				if (!initializing) {
					refreshList();
				}
			}
		});

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
			refreshPlacesList(false);
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

		binding.settings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bottomSheetController.setStateOfBottomSheet(BottomSheetType.SELECTED_PLACE_CATEGORY, BottomSheetBehavior.STATE_COLLAPSED);

				PlaceCategorySettingsFragment placeCategorySettingsFragment = new PlaceCategorySettingsFragment();
				getParentFragmentManager().beginTransaction().add(R.id.fragment_container, placeCategorySettingsFragment,
						getString(R.string.tag_place_category_settings_fragment)).addToBackStack(getString(R.string.tag_place_category_settings_fragment)).commit();
			}
		});

		binding.searchRadius.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				binding.radiusSeekbarLayout.setVisibility(binding.radiusSeekbarLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
				int drawableId = 0;
				if (binding.radiusSeekbarLayout.getVisibility() == View.VISIBLE) {
					drawableId = R.drawable.expand_less_icon;
				} else {
					drawableId = R.drawable.expand_more_icon;
				}
				binding.searchRadius.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableId, 0);
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

				refreshPlacesList(true);
			}
		});

		/*
		locationViewModel.getLocation(EVENT_ID, new DbQueryCallback<LocationDTO>() {
			@Override
			public void onResultSuccessful(LocationDTO locationResultDto) {
				selectedLocationDto = locationResultDto;
			}

			@Override
			public void onResultNoData() {
				initializing = false;
			}
		});

		 */

		setSearchRadius();
		makeCategoryListView();
	}


	private void setSearchRadius() {
		float value = Math.round((Float.parseFloat(App.getPreference_key_radius_range()) / 1000f) * 10) / 10f;
		binding.searchRadius.setText(getString(R.string.search_radius) + " " + decimalFormat.format(value) + "km");
	}

	public void refreshPlacesList(boolean forceRefresh) {
		if (lastLatLng.equals(newLatLng)) {
			if (!forceRefresh) {
				return;
			}
		}
		LocalApiPlaceParameter coordToAddressParameter = LocalParameterUtil.getCoordToAddressParameter(newLatLng.latitude,
				newLatLng.longitude);
		KakaoLocalDownloader.coordToAddress(coordToAddressParameter, new OnKakaoLocalApiCallback() {
			@Override
			public void onResultSuccessful(int type, KakaoLocalResponse result) {
				lastLatLng = newLatLng;
				CoordToAddress coordToAddress = (CoordToAddress) result;

				if (getActivity() != null) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							binding.addressName.setText(coordToAddress.getCoordToAddressDocuments().get(0).getCoordToAddressAddress().getAddressName());
						}
					});
				}
			}

			@Override
			public void onResultNoData() {

			}
		});

		for (PlaceCategoryDTO placeCategoryDTO : placeCategorySet) {
			loadList(placeCategoryDTO);
		}

		onRefreshCriteriaLocationListener.onRefreshedCriteriaLocation();
	}

	public void makeCategoryListView() {
		LocalApiPlaceParameter coordToAddressParameter = LocalParameterUtil.getCoordToAddressParameter(newLatLng.latitude,
				newLatLng.longitude);
		KakaoLocalDownloader.coordToAddress(coordToAddressParameter, new OnKakaoLocalApiCallback() {
			@Override
			public void onResultSuccessful(int type, KakaoLocalResponse result) {
				lastLatLng = newLatLng;

				placeCategoryViewModel.selectConvertedSelected(new DbQueryCallback<List<PlaceCategoryDTO>>() {
					@Override
					public void onResultSuccessful(List<PlaceCategoryDTO> newPlaceCategoriesList) {

						placeCategorySet.addAll(newPlaceCategoriesList);
						if (getActivity() != null) {
							requireActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									CoordToAddress coordToAddress = (CoordToAddress) result;
									binding.addressName.setText(coordToAddress.getCoordToAddressDocuments().get(0).getCoordToAddressAddress().getAddressName());

									if (newPlaceCategoriesList.isEmpty()) {
										initializing = false;
										binding.customProgressView.onFailedProcessingData(getString(R.string.not_selected_place_category));
									} else {
										binding.categoryViewlist.removeAllViews();
										adaptersMap.clear();
										listMap.clear();

										for (PlaceCategoryDTO placeCategory : newPlaceCategoriesList) {
											addCategoryView(placeCategory);
										}

										binding.customProgressView.onSuccessfulProcessingData();
										initializing = false;
									}


								}

							});
						}
					}

					@Override
					public void onResultNoData() {
						initializing = false;
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
				Set<PlaceCategoryDTO> newSet = new HashSet<>(newPlaceCategoryList);

				Set<PlaceCategoryDTO> removedSet = new HashSet<>(placeCategorySet);
				Set<PlaceCategoryDTO> addedSet = new HashSet<>(newSet);

				removedSet.removeAll(newSet);
				addedSet.removeAll(placeCategorySet);

				if (!removedSet.isEmpty() || !addedSet.isEmpty()) {
					placeCategorySet = newSet;

					if (getActivity() != null) {
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

									for (PlaceCategoryDTO placeCategory : addedSet) {
										addCategoryView(placeCategory);
									}
								}

								if (newPlaceCategoryList.isEmpty()) {
									binding.categoryViewlist.removeAllViews();
									adaptersMap.clear();
									listMap.clear();
									binding.customProgressView.onFailedProcessingData(getString(R.string.not_selected_place_category));
								} else {
									binding.customProgressView.onSuccessfulProcessingData();
								}
							}
						});
					}
				}

			}

			@Override
			public void onResultNoData() {
			}
		});
	}

	private void addCategoryView(PlaceCategoryDTO placeCategory) {
		LinearLayout categoryView = (LinearLayout) getLayoutInflater().inflate(R.layout.place_category_view, null);
		RecyclerView itemRecyclerView = (RecyclerView) categoryView.findViewById(R.id.map_category_itemsview);

		listMap.put(placeCategory.getCode(), itemRecyclerView);
		categoryView.setTag(placeCategory.getCode());
		binding.categoryViewlist.addView(categoryView);

		((TextView) categoryView.findViewById(R.id.map_category_name)).setText(placeCategory.getDescription());

		CustomProgressView customProgressView = (CustomProgressView) categoryView.findViewById(R.id.custom_progress_view);
		customProgressView.setContentView(itemRecyclerView);

		itemRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
		itemRecyclerView.addItemDecoration(new CustomRecyclerViewItemDecoration((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics())));

		((Button) categoryView.findViewById(R.id.map_category_more)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onClickedPlacesListListener.onClickedMoreInList(placeCategory);
			}
		});

		loadList(placeCategory);
	}

	private void loadList(PlaceCategoryDTO placeCategory) {
		CustomProgressView customProgressView = (CustomProgressView) binding.categoryViewlist.findViewWithTag(placeCategory.getCode())
				.findViewById(R.id.custom_progress_view);
		customProgressView.onStartedProcessingData();
		PlaceItemsAdapters adapter = new PlaceItemsAdapters(onClickedPlacesListListener, placeCategory);

		adaptersMap.put(placeCategory.getCode(), adapter);
		listMap.get(placeCategory.getCode()).setAdapter(adapter);

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

		LocalApiPlaceParameter placeParameter = LocalParameterUtil.getPlaceParameter(placeCategory.getCode(), String.valueOf(newLatLng.latitude),
				String.valueOf(newLatLng.longitude), LocalApiPlaceParameter.DEFAULT_SIZE, LocalApiPlaceParameter.DEFAULT_PAGE,
				LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY);
		placeParameter.setRadius(App.getPreference_key_radius_range());

		viewModel.init(placeParameter, new PagedList.BoundaryCallback<PlaceDocuments>() {
			@Override
			public void onZeroItemsLoaded() {
				super.onZeroItemsLoaded();

				if (getActivity() != null) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							customProgressView.onFailedProcessingData(getString(R.string.not_founded_search_result));
						}
					});
				}
			}
		});
		viewModel.getPagedListMutableLiveData().observe(PlacesOfSelectedCategoriesFragment.this.getViewLifecycleOwner(),
				new Observer<PagedList<PlaceDocuments>>() {
					@Override
					public void onChanged(PagedList<PlaceDocuments> placeDocuments) {
						adapter.submitList(placeDocuments);
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

	}

	public interface OnRefreshCriteriaLocationListener {
		void onRefreshedCriteriaLocation();
	}
}
