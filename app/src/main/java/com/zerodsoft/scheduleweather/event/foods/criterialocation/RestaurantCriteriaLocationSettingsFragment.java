package com.zerodsoft.scheduleweather.event.foods.criterialocation;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.databinding.FragmentRestaurantCriteriaLocationSettingsBinding;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.adapter.FoodCriteriaLocationHistoryAdapter;
import com.zerodsoft.scheduleweather.event.foods.enums.CriteriaLocationType;
import com.zerodsoft.scheduleweather.event.foods.interfaces.LocationHistoryController;
import com.zerodsoft.scheduleweather.event.foods.main.fragment.RestaurantMainFragment;
import com.zerodsoft.scheduleweather.event.foods.searchlocation.fragment.LocationSearchDialogFragment;
import com.zerodsoft.scheduleweather.event.foods.searchlocation.interfaces.OnSelectedNewLocation;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationHistoryViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationSearchHistoryDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RestaurantCriteriaLocationSettingsFragment extends Fragment implements LocationHistoryController, OnSelectedNewLocation, OnBackPressedCallbackController {
	public static final String TAG = "RestaurantCriteriaLocationSettingsFragment";
	private final RestaurantMainFragment.IGetEventValue iGetEventValue;

	private FragmentRestaurantCriteriaLocationSettingsBinding binding;

	private LocationViewModel locationViewModel;
	private CalendarViewModel calendarViewModel;
	private FoodCriteriaLocationInfoViewModel foodCriteriaLocationInfoViewModel;
	private FoodCriteriaLocationHistoryViewModel foodCriteriaLocationSearchHistoryViewModel;

	private LocationDTO locationDTO;
	private List<FoodCriteriaLocationSearchHistoryDTO> foodCriteriaLocationHistoryList;
	private FoodCriteriaLocationSearchHistoryDTO selectedFoodCriteriaLocationSearchHistoryDTO;
	private FoodCriteriaLocationHistoryAdapter foodCriteriaLocationHistoryAdapter;

	private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
		@Override
		public void handleOnBackPressed() {
			onBackPressed();
		}
	};

	public RestaurantCriteriaLocationSettingsFragment(RestaurantMainFragment.IGetEventValue iGetEventValue) {
		this.iGetEventValue = iGetEventValue;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

		locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
		calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
		foodCriteriaLocationInfoViewModel = new ViewModelProvider(this).get(FoodCriteriaLocationInfoViewModel.class);
		foodCriteriaLocationSearchHistoryViewModel = new ViewModelProvider(this).get(FoodCriteriaLocationHistoryViewModel.class);

		binding.addressHistoryRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		binding.addressHistoryRecyclerview.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

		binding.radioGroup.setOnCheckedChangeListener(radioOnCheckedChangeListener);

		setSearchView();

		locationViewModel.getLocation(iGetEventValue.getCalendarId(),
				iGetEventValue.getEventId(), new DbQueryCallback<LocationDTO>() {
					@Override
					public void onResultSuccessful(LocationDTO locationResultDto) {
						//address, place 구분
						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								locationDTO = locationResultDto;

								if (locationDTO.getLocationType() == LocationType.PLACE) {
									binding.radioUseSelectedLocation.setText(locationDTO.getPlaceName());
								} else {
									binding.radioUseSelectedLocation.setText(locationDTO.getAddressName());
								}

								//지정한 위치 정보 데이터를 가져왔으면 기준 위치 선택정보를 가져온다.
								foodCriteriaLocationInfoViewModel.selectByEventId(iGetEventValue.getCalendarId(), iGetEventValue.getEventId(), new DbQueryCallback<FoodCriteriaLocationInfoDTO>() {
									@Override
									public void onResultSuccessful(FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoResultDto) {
										requireActivity().runOnUiThread(new Runnable() {
											@Override
											public void run() {
												switch (CriteriaLocationType.enumOf(foodCriteriaLocationInfoResultDto.getUsingType())) {
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
														foodCriteriaLocationSearchHistoryViewModel.select(foodCriteriaLocationInfoResultDto.getId(),
																new DbQueryCallback<FoodCriteriaLocationSearchHistoryDTO>() {
																	@Override
																	public void onResultSuccessful(FoodCriteriaLocationSearchHistoryDTO foodCriteriaLocationSearchHistoryResultDto) {
																		selectedFoodCriteriaLocationSearchHistoryDTO =
																				foodCriteriaLocationSearchHistoryResultDto;
																	}

																	@Override
																	public void onResultNoData() {

																	}
																});
														break;
													default:
														assert (false) : "Unknown";
												}
											}
										});
									}

									@Override
									public void onResultNoData() {

									}
								});

							}
						});
					}

					@Override
					public void onResultNoData() {

					}
				});

		foodCriteriaLocationSearchHistoryViewModel.selectByEventId(iGetEventValue.getCalendarId(), iGetEventValue.getEventId(), new DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>>() {
			@Override
			public void onResultSuccessful(List<FoodCriteriaLocationSearchHistoryDTO> foodCriteriaLocationSearchHistoryResultDtos) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						foodCriteriaLocationHistoryList = foodCriteriaLocationSearchHistoryResultDtos;

						foodCriteriaLocationHistoryAdapter =
								new FoodCriteriaLocationHistoryAdapter(RestaurantCriteriaLocationSettingsFragment.this);
						foodCriteriaLocationHistoryAdapter.setFoodCriteriaLocationHistoryList(foodCriteriaLocationHistoryList);
						binding.addressHistoryRecyclerview.setAdapter(foodCriteriaLocationHistoryAdapter);
					}
				});
			}

			@Override
			public void onResultNoData() {

			}
		});
	}

	private RadioGroup.OnCheckedChangeListener radioOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
			if (checkedId == binding.radioUseSelectedLocation.getId()) {
				//지정한 위치사용
				binding.addressHistoryRecyclerview.setVisibility(View.GONE);
				binding.searchView.setVisibility(View.GONE);
			} else if (checkedId == binding.radioCurrentLocation.getId()) {
				binding.addressHistoryRecyclerview.setVisibility(View.GONE);
				binding.searchView.setVisibility(View.GONE);
			} else if (checkedId == binding.radioCustomSelection.getId()) {
				binding.addressHistoryRecyclerview.setVisibility(View.VISIBLE);
				binding.searchView.setVisibility(View.VISIBLE);
			}
		}
	};

	private void setSearchView() {
		binding.searchView.setVisibility(View.VISIBLE);

		binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				//검색 결과 목록 표시
				if (!query.isEmpty()) {
					LocationSearchDialogFragment searchDialogFragment = new LocationSearchDialogFragment(RestaurantCriteriaLocationSettingsFragment.this);
					Bundle bundle = new Bundle();
					bundle.putString("searchWord", query);

					searchDialogFragment.setArguments(bundle);
					searchDialogFragment.show(getParentFragmentManager(), LocationSearchDialogFragment.TAG);
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

	}

	public void onBackPressed() {
        /*
        지정한 위치인 경우 : 해당 이벤트 인스턴스가 지정된 위치를 기준으로 검색한다고 DB에 입력
        현재 위치인 경우 : 해당 이벤트 인스턴스가 현재 위치를 기준으로 검색한다고 DB에 입력
        직접 검색 후 지정한 위치인 경우 : 해당 이벤트 인스턴스가 커스텀 위치를 기준으로 검색한다고 DB에 입력하고,
        커스텀 위치 정보를 DB에 입력
         */
		if (binding.radioGroup.getCheckedRadioButtonId() == binding.radioUseSelectedLocation.getId()) {
			//변경 타입 업데이트
			foodCriteriaLocationInfoViewModel.updateByEventId(iGetEventValue.getCalendarId(), iGetEventValue.getEventId(),
					CriteriaLocationType.TYPE_SELECTED_LOCATION.value(), null, finishCallback);

		} else if (binding.radioGroup.getCheckedRadioButtonId() == binding.radioCurrentLocation.getId()) {
			//변경 타입 업데이트
			foodCriteriaLocationInfoViewModel.updateByEventId(iGetEventValue.getCalendarId(), iGetEventValue.getEventId(),
					CriteriaLocationType.TYPE_CURRENT_LOCATION_GPS.value(), null, finishCallback);

		} else if (binding.radioGroup.getCheckedRadioButtonId() == binding.radioCustomSelection.getId()) {
			//선택된 정보가 없는 경우 : 이벤트에서 지정한 위치를 기준으로 강제설정
			if (selectedFoodCriteriaLocationSearchHistoryDTO == null) {
				foodCriteriaLocationInfoViewModel.updateByEventId(iGetEventValue.getCalendarId(), iGetEventValue.getEventId(),
						CriteriaLocationType.TYPE_SELECTED_LOCATION.value(), null, finishCallback);
			} else {
				foodCriteriaLocationSearchHistoryViewModel.containsData(selectedFoodCriteriaLocationSearchHistoryDTO.getId(), new DbQueryCallback<Boolean>() {
					@Override
					public void onResultSuccessful(Boolean result) {
						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (result) {
									setResult(RESULT_OK);
									finish();
								} else {
									foodCriteriaLocationInfoViewModel.updateByEventId(iGetEventValue.getCalendarId(),
											iGetEventValue.getEventId(), CriteriaLocationType.TYPE_SELECTED_LOCATION.value(), null, finishCallback);
								}

							}
						});
					}

					@Override
					public void onResultNoData() {

					}
				});

			}
		}

	}

	private final CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> finishCallback = new CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO>() {
		@Override
		public void onReceiveResult(@NonNull FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO) throws RemoteException {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					setResult(RESULT_OK);
					finish();
				}
			});
		}
	};

	@Override
	public void onClickedLocationHistoryItem(FoodCriteriaLocationSearchHistoryDTO foodCriteriaLocationSearchHistoryDTO) {
		foodCriteriaLocationInfoViewModel.updateByEventId(iGetEventValue.getCalendarId(), iGetEventValue.getEventId(), CriteriaLocationType.TYPE_CUSTOM_SELECTED_LOCATION.value()
				, foodCriteriaLocationSearchHistoryDTO.getId(), new DbQueryCallback<FoodCriteriaLocationInfoDTO>() {
					@Override
					public void onResultSuccessful(FoodCriteriaLocationInfoDTO result) {
						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								setResult(RESULT_OK);
								finish();
							}
						});
					}

					@Override
					public void onResultNoData() {

					}
				});

	}

	@Override
	public void delete(int id) {
		foodCriteriaLocationSearchHistoryViewModel.delete(id, new DbQueryCallback<Boolean>() {
			@Override
			public void onResultSuccessful(Boolean isDeleted) {
				if (isDeleted) {
					foodCriteriaLocationSearchHistoryViewModel.selectByEventId(iGetEventValue.getCalendarId(), iGetEventValue.getEventId(), new DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>>() {
						@Override
						public void onResultSuccessful(List<FoodCriteriaLocationSearchHistoryDTO> foodCriteriaLocationSearchHistoryResultDtos) {
							requireActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									foodCriteriaLocationHistoryList = foodCriteriaLocationSearchHistoryResultDtos;

									foodCriteriaLocationHistoryAdapter.setFoodCriteriaLocationHistoryList(foodCriteriaLocationHistoryList);
									foodCriteriaLocationHistoryAdapter.notifyDataSetChanged();
								}
							});

						}

						@Override
						public void onResultNoData() {

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
	public void onSelectedNewLocation(LocationDTO locationDTO) {
		foodCriteriaLocationSearchHistoryViewModel.insertByEventId(iGetEventValue.getCalendarId(), iGetEventValue.getEventId(), locationDTO.getPlaceName(), locationDTO.getAddressName(),
				locationDTO.getRoadAddressName()
				, String.valueOf(locationDTO.getLatitude()), String.valueOf(locationDTO.getLongitude()), locationDTO.getLocationType(),
				new DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>>() {
					@Override
					public void onResultSuccessful(List<FoodCriteriaLocationSearchHistoryDTO> foodCriteriaLocationSearchHistoryResultDtos) {
						//변경 타입 업데이트
						int id = foodCriteriaLocationSearchHistoryResultDtos.get(foodCriteriaLocationSearchHistoryResultDtos.size() - 1).getId();

						foodCriteriaLocationInfoViewModel.updateByEventId(iGetEventValue.getCalendarId(), iGetEventValue.getEventId(),
								CriteriaLocationType.TYPE_CUSTOM_SELECTED_LOCATION.value(),
								id, new DbQueryCallback<FoodCriteriaLocationInfoDTO>() {
									@Override
									public void onResultSuccessful(FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoResultDto) {
										requireActivity().runOnUiThread(new Runnable() {
											@Override
											public void run() {
												setResult(RESULT_OK);
												finish();
											}
										});
									}


									@Override
									public void onResultNoData() {

									}
								});
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
}