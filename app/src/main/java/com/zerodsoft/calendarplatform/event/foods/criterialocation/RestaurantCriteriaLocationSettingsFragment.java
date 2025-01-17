package com.zerodsoft.calendarplatform.event.foods.criterialocation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.databinding.FragmentRestaurantCriteriaLocationSettingsBinding;
import com.zerodsoft.calendarplatform.etc.LocationType;
import com.zerodsoft.calendarplatform.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.calendarplatform.event.foods.adapter.FoodCriteriaLocationHistoryAdapter;
import com.zerodsoft.calendarplatform.event.foods.enums.CriteriaLocationType;
import com.zerodsoft.calendarplatform.event.foods.interfaces.IOnSetView;
import com.zerodsoft.calendarplatform.event.foods.interfaces.LocationHistoryController;
import com.zerodsoft.calendarplatform.event.foods.searchlocation.fragment.LocationSearchFragment;
import com.zerodsoft.calendarplatform.event.foods.searchlocation.interfaces.OnSelectedNewLocation;
import com.zerodsoft.calendarplatform.event.foods.viewmodel.FoodCriteriaLocationHistoryViewModel;
import com.zerodsoft.calendarplatform.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;
import com.zerodsoft.calendarplatform.event.foods.viewmodel.RestaurantSharedViewModel;
import com.zerodsoft.calendarplatform.room.dto.FoodCriteriaLocationInfoDTO;
import com.zerodsoft.calendarplatform.room.dto.FoodCriteriaLocationSearchHistoryDTO;
import com.zerodsoft.calendarplatform.room.dto.LocationDTO;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RestaurantCriteriaLocationSettingsFragment extends Fragment implements LocationHistoryController, OnSelectedNewLocation {
	private FragmentRestaurantCriteriaLocationSettingsBinding binding;

	private LocationViewModel locationViewModel;
	private RestaurantSharedViewModel sharedViewModel;
	private IOnSetView iOnSetView;
	private FoodCriteriaLocationInfoViewModel foodCriteriaLocationInfoViewModel;
	private FoodCriteriaLocationHistoryViewModel foodCriteriaLocationSearchHistoryViewModel;

	private LocationDTO selectedLocationDto;
	private FoodCriteriaLocationInfoDTO savedFoodCriteriaLocationInfoDTO;
	private FoodCriteriaLocationInfoDTO newFoodCriteriaLocationInfoDTO;
	private Long eventId;
	private Integer selectedSearchHistoryId;

	private FoodCriteriaLocationHistoryAdapter foodCriteriaLocationHistoryAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		iOnSetView = (IOnSetView) getParentFragment();

		sharedViewModel = new ViewModelProvider(getParentFragment().getParentFragment()).get(RestaurantSharedViewModel.class);
		eventId = sharedViewModel.getEventId();

		locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
		foodCriteriaLocationInfoViewModel = new ViewModelProvider(requireActivity()).get(FoodCriteriaLocationInfoViewModel.class);
		foodCriteriaLocationSearchHistoryViewModel = new ViewModelProvider(requireActivity()).get(FoodCriteriaLocationHistoryViewModel.class);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentRestaurantCriteriaLocationSettingsBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.searchLayout.setVisibility(View.GONE);

		binding.customProgressView.setContentView(binding.addressHistoryRecyclerview);
		binding.customProgressView.onSuccessfulProcessingData();

		binding.addressHistoryRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		binding.addressHistoryRecyclerview.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

		foodCriteriaLocationHistoryAdapter =
				new FoodCriteriaLocationHistoryAdapter(RestaurantCriteriaLocationSettingsFragment.this);
		foodCriteriaLocationHistoryAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				if (foodCriteriaLocationHistoryAdapter.getItemCount() == 0) {
					binding.customProgressView.onFailedProcessingData(getString(R.string.not_search_history));
				} else {
					binding.customProgressView.onSuccessfulProcessingData();
				}
			}

			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				super.onItemRangeInserted(positionStart, itemCount);
				if (positionStart == 0) {
					binding.customProgressView.onSuccessfulProcessingData();
				}
			}

			@Override
			public void onItemRangeRemoved(int positionStart, int itemCount) {
				super.onItemRangeRemoved(positionStart, itemCount);
				if (foodCriteriaLocationHistoryAdapter.getItemCount() == 0) {
					binding.customProgressView.onFailedProcessingData(getString(R.string.not_search_history));
				}
			}
		});
		binding.addressHistoryRecyclerview.setAdapter(foodCriteriaLocationHistoryAdapter);

		binding.radioGroup.setOnCheckedChangeListener(radioOnCheckedChangeListener);
		binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				//검색 결과 목록 표시
				if (!query.isEmpty()) {
					LocationSearchFragment locationSearchFragment = new LocationSearchFragment(RestaurantCriteriaLocationSettingsFragment.this);
					Bundle bundle = new Bundle();
					bundle.putString("searchWord", query);

					locationSearchFragment.setArguments(bundle);
					getParentFragmentManager().beginTransaction()
							.hide(RestaurantCriteriaLocationSettingsFragment.this)
							.add(R.id.content_fragment_container, locationSearchFragment, LocationSearchFragment.TAG)
							.addToBackStack(LocationSearchFragment.TAG).commit();
					return true;
				} else {
					return false;
				}
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});

		foodCriteriaLocationSearchHistoryViewModel.selectAll(new DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>>() {
			@Override
			public void onResultSuccessful(List<FoodCriteriaLocationSearchHistoryDTO> result) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						foodCriteriaLocationHistoryAdapter.setFoodCriteriaLocationHistoryList(result);
						foodCriteriaLocationHistoryAdapter.notifyDataSetChanged();
					}
				});
			}

			@Override
			public void onResultNoData() {
			}
		});

		locationViewModel.getLocation(eventId, new DbQueryCallback<LocationDTO>() {
			@Override
			public void onResultSuccessful(LocationDTO locationResultDto) {
				//address, place 구분
				selectedLocationDto = locationResultDto;

				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						binding.radioUseSelectedLocation.setVisibility(View.VISIBLE);

						if (selectedLocationDto.getLocationType() == LocationType.PLACE) {
							binding.radioUseSelectedLocation.setText(selectedLocationDto.getPlaceName());
						} else {
							binding.radioUseSelectedLocation.setText(selectedLocationDto.getAddressName());
						}

						loadCriteria();
					}
				});
			}

			@Override
			public void onResultNoData() {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						binding.radioUseSelectedLocation.setVisibility(View.GONE);
						loadCriteria();
					}
				});
			}
		});
	}

	@Override
	public void onDestroy() {
		checkChangedData();
		super.onDestroy();
	}


	private void loadCriteria() {
		foodCriteriaLocationInfoViewModel.selectByEventId(eventId
				, new DbQueryCallback<FoodCriteriaLocationInfoDTO>() {
					@Override
					public void onResultSuccessful(FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoResultDto) {
						savedFoodCriteriaLocationInfoDTO = foodCriteriaLocationInfoResultDto;

						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								setCriteria(CriteriaLocationType.enumOf(savedFoodCriteriaLocationInfoDTO.getUsingType()));
							}
						});
					}

					@Override
					public void onResultNoData() {

					}
				});
	}

	private void setCriteria(CriteriaLocationType criteriaLocationType) {
		switch (criteriaLocationType) {
			case TYPE_SELECTED_LOCATION:
				binding.radioUseSelectedLocation.setChecked(true);
				break;
			case TYPE_MAP_CENTER_POINT:
				binding.radioCurrentMapCenterPoint.setChecked(true);
				break;
			case TYPE_CURRENT_LOCATION_GPS:
				binding.radioCurrentLocation.setChecked(true);
				break;
			case TYPE_CUSTOM_SELECTED_LOCATION:
				binding.radioCustomSelection.setChecked(true);
				break;
			default:
				assert (false) : "Unknown";
		}
	}

	private final RadioGroup.OnCheckedChangeListener radioOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
		boolean initializing = true;

		@Override
		public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
			if (checkedId == binding.radioUseSelectedLocation.getId()) {
				binding.searchLayout.setVisibility(View.GONE);

				if (!initializing) {
					foodCriteriaLocationInfoViewModel.updateByEventId(eventId,
							CriteriaLocationType.TYPE_SELECTED_LOCATION.value(), null, finishCallback);
				}
			} else if (checkedId == binding.radioCurrentMapCenterPoint.getId()) {
				binding.searchLayout.setVisibility(View.GONE);

				if (!initializing) {
					foodCriteriaLocationInfoViewModel.updateByEventId(eventId,
							CriteriaLocationType.TYPE_MAP_CENTER_POINT.value(), null, finishCallback);
				}
			} else if (checkedId == binding.radioCurrentLocation.getId()) {
				binding.searchLayout.setVisibility(View.GONE);

				if (!initializing) {
					foodCriteriaLocationInfoViewModel.updateByEventId(
							eventId,
							CriteriaLocationType.TYPE_CURRENT_LOCATION_GPS.value(), null, finishCallback);
				}
			} else if (checkedId == binding.radioCustomSelection.getId()) {
				binding.searchLayout.setVisibility(View.VISIBLE);

				if (!initializing) {

				} else {
					selectedSearchHistoryId = savedFoodCriteriaLocationInfoDTO.getHistoryLocationId();
				}
			}

			initializing = false;
		}
	};


	private final DbQueryCallback<FoodCriteriaLocationInfoDTO> finishCallback = new DbQueryCallback<FoodCriteriaLocationInfoDTO>() {
		@Override
		public void onResultSuccessful(FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoResultDto) {
			newFoodCriteriaLocationInfoDTO = foodCriteriaLocationInfoResultDto;
			requireActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					requireActivity().getOnBackPressedDispatcher().onBackPressed();
				}
			});
		}

		@Override
		public void onResultNoData() {

		}
	};

	@Override
	public void onClickedLocationHistoryItem(FoodCriteriaLocationSearchHistoryDTO clickedCriteriaLocationSearchHistoryDTO) {
		foodCriteriaLocationInfoViewModel.updateByEventId(eventId, CriteriaLocationType.TYPE_CUSTOM_SELECTED_LOCATION.value()
				, clickedCriteriaLocationSearchHistoryDTO.getId(), finishCallback);
	}

	@Override
	public void delete(int id) {
		List<FoodCriteriaLocationSearchHistoryDTO> list = foodCriteriaLocationHistoryAdapter.getFoodCriteriaLocationHistoryList();
		for (int i = list.size() - 1; i >= 0; i--) {
			if (list.get(i).getId() == id) {
				list.remove(i);
				foodCriteriaLocationHistoryAdapter.notifyItemRemoved(i);
				break;
			}
		}

		foodCriteriaLocationSearchHistoryViewModel.delete(id, new DbQueryCallback<Boolean>() {
			@Override
			public void onResultSuccessful(Boolean isDeleted) {
			}

			@Override
			public void onResultNoData() {

			}
		});


		if (selectedSearchHistoryId != null) {
			if (selectedSearchHistoryId == id) {
				selectedSearchHistoryId = null;
			}
		}
	}

	@Override
	public void onSelectedNewLocation(LocationDTO newLocationDto) {
		foodCriteriaLocationSearchHistoryViewModel.insertByEventId(eventId, newLocationDto.getPlaceName(), newLocationDto.getAddressName(),
				String.valueOf(newLocationDto.getLatitude()), String.valueOf(newLocationDto.getLongitude()), newLocationDto.getLocationType(),
				new DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>>() {
					@Override
					public void onResultSuccessful(List<FoodCriteriaLocationSearchHistoryDTO> newHistoryList) {
						//변경 타입 업데이트
						if (getActivity() != null) {
							requireActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									foodCriteriaLocationHistoryAdapter.setFoodCriteriaLocationHistoryList(newHistoryList);
									foodCriteriaLocationHistoryAdapter.notifyDataSetChanged();

									int id = newHistoryList.get(newHistoryList.size() - 1).getId();

									requireActivity().getOnBackPressedDispatcher().onBackPressed();
									foodCriteriaLocationInfoViewModel.updateByEventId(eventId,
											CriteriaLocationType.TYPE_CUSTOM_SELECTED_LOCATION.value(),
											id, finishCallback);
								}
							});
						}

					}

					@Override
					public void onResultNoData() {

					}
				});
	}

	public void checkChangedData() {
		if (newFoodCriteriaLocationInfoDTO == null) {
			if (binding.radioCurrentLocation.isChecked()) {
				foodCriteriaLocationInfoViewModel.refresh(eventId);

			} else if (binding.radioCustomSelection.isChecked()) {
				if (selectedSearchHistoryId == null) {
					foodCriteriaLocationInfoViewModel.updateByEventId(eventId, CriteriaLocationType.TYPE_MAP_CENTER_POINT.value(), null
							, new DbQueryCallback<FoodCriteriaLocationInfoDTO>() {
								@Override
								public void onResultSuccessful(FoodCriteriaLocationInfoDTO result) {

								}

								@Override
								public void onResultNoData() {

								}
							});
					Toast.makeText(getContext(), R.string.selected_map_center_point_because_not_selected_custom_criteria_location, Toast.LENGTH_SHORT).show();
				}
			}

		}
	}
}