package com.zerodsoft.scheduleweather.event.foods.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.service.carrier.CarrierMessagingService;
import android.view.View;
import android.widget.RadioGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.ActivityLocationSettingsBinding;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.adapter.FoodCriteriaLocationHistoryAdapter;
import com.zerodsoft.scheduleweather.event.foods.enums.CriteriaLocationType;
import com.zerodsoft.scheduleweather.event.foods.interfaces.LocationHistoryController;
import com.zerodsoft.scheduleweather.event.foods.main.fragment.RestaurantMainFragment;
import com.zerodsoft.scheduleweather.event.foods.searchlocation.fragment.LocationSearchDialogFragment;
import com.zerodsoft.scheduleweather.event.foods.searchlocation.interfaces.OnSelectedNewLocation;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationHistoryViewModel;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationSearchHistoryDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import java.util.List;

public class LocationSettingsActivity extends AppCompatActivity implements LocationHistoryController, OnSelectedNewLocation {
	private ActivityLocationSettingsBinding binding;
	private LocationViewModel locationViewModel;
	private FoodCriteriaLocationInfoViewModel foodCriteriaLocationInfoViewModel;
	private FoodCriteriaLocationHistoryViewModel foodCriteriaLocationSearchHistoryViewModel;
	private final RestaurantMainFragment.IGetEventValue iGetEventValue;

	private LocationDTO locationDTO;
	private List<FoodCriteriaLocationSearchHistoryDTO> foodCriteriaLocationHistoryList;
	private FoodCriteriaLocationSearchHistoryDTO selectedFoodCriteriaLocationSearchHistoryDTO;
	private FoodCriteriaLocationHistoryAdapter foodCriteriaLocationHistoryAdapter;

	public LocationSettingsActivity(RestaurantMainFragment.IGetEventValue iGetEventValue) {
		this.iGetEventValue = iGetEventValue;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_location_settings);

		Bundle bundle = getIntent().getExtras();

		binding.addressHistoryRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		binding.addressHistoryRecyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

		binding.radioGroup.setOnCheckedChangeListener(radioOnCheckedChangeListener);

		setSearchView();

		locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
		foodCriteriaLocationInfoViewModel = new ViewModelProvider(this).get(FoodCriteriaLocationInfoViewModel.class);
		foodCriteriaLocationSearchHistoryViewModel = new ViewModelProvider(this).get(FoodCriteriaLocationHistoryViewModel.class);

		locationViewModel.getLocation(iGetEventValue.getCalendarId(), iGetEventValue.getEventId(),
				new CarrierMessagingService.ResultCallback<LocationDTO>() {
					@Override
					public void onReceiveResult(@NonNull LocationDTO locationDTO) throws RemoteException {
						//address, place 구분
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								LocationSettingsActivity.this.locationDTO = locationDTO;

								if (locationDTO.getLocationType() == LocationType.PLACE) {
									binding.radioUseSelectedLocation.setText(locationDTO.getPlaceName());
								} else {
									binding.radioUseSelectedLocation.setText(locationDTO.getAddressName());
								}

								//지정한 위치 정보 데이터를 가져왔으면 기준 위치 선택정보를 가져온다.
								foodCriteriaLocationInfoViewModel.selectByEventId(iGetEventValue.getCalendarId(), iGetEventValue.getEventId(), new CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO>() {
									@Override
									public void onReceiveResult(@NonNull FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO) throws RemoteException {
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												switch (CriteriaLocationType.enumOf(foodCriteriaLocationInfoDTO.getUsingType())) {
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
														foodCriteriaLocationSearchHistoryViewModel.select(foodCriteriaLocationInfoDTO.getId(),
																new CarrierMessagingService.ResultCallback<FoodCriteriaLocationSearchHistoryDTO>() {
																	@Override
																	public void onReceiveResult(@NonNull FoodCriteriaLocationSearchHistoryDTO result) throws RemoteException {
																		LocationSettingsActivity.this.selectedFoodCriteriaLocationSearchHistoryDTO =
																				result;
																	}
																});
														break;
												}
											}
										});

									}
								});

							}
						});

					}
				});

		foodCriteriaLocationSearchHistoryViewModel.selectByEventId(iGetEventValue.getCalendarId(), iGetEventValue.getEventId(), new CarrierMessagingService.ResultCallback<List<FoodCriteriaLocationSearchHistoryDTO>>() {
			@Override
			public void onReceiveResult(@NonNull List<FoodCriteriaLocationSearchHistoryDTO> result) throws RemoteException {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						LocationSettingsActivity.this.foodCriteriaLocationHistoryList = result;

						foodCriteriaLocationHistoryAdapter = new FoodCriteriaLocationHistoryAdapter(LocationSettingsActivity.this);
						foodCriteriaLocationHistoryAdapter.setFoodCriteriaLocationHistoryList(foodCriteriaLocationHistoryList);

						binding.addressHistoryRecyclerview.setAdapter(foodCriteriaLocationHistoryAdapter);
					}
				});
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
					LocationSearchDialogFragment searchDialogFragment = new LocationSearchDialogFragment(LocationSettingsActivity.this);
					Bundle bundle = new Bundle();
					bundle.putString("searchWord", query);

					searchDialogFragment.setArguments(bundle);
					searchDialogFragment.show(getSupportFragmentManager(), LocationSearchDialogFragment.TAG);
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

	@Override
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
				foodCriteriaLocationSearchHistoryViewModel.containsData(selectedFoodCriteriaLocationSearchHistoryDTO.getId(), new CarrierMessagingService.ResultCallback<Boolean>() {
					@Override
					public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (aBoolean) {
									setResult(RESULT_OK);
									finish();
								} else {
									foodCriteriaLocationInfoViewModel.updateByEventId(iGetEventValue.getCalendarId(),
											iGetEventValue.getEventId(), CriteriaLocationType.TYPE_SELECTED_LOCATION.value(), null, finishCallback);
								}

							}
						});
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
				, foodCriteriaLocationSearchHistoryDTO.getId(), new CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO>() {
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
				});

	}

	@Override
	public void delete(int id) {
		foodCriteriaLocationSearchHistoryViewModel.delete(id, new CarrierMessagingService.ResultCallback<Boolean>() {
			@Override
			public void onReceiveResult(@NonNull Boolean result) throws RemoteException {
				if (result) {
					foodCriteriaLocationSearchHistoryViewModel.selectByEventId(iGetEventValue.getCalendarId(), iGetEventValue.getEventId(), new CarrierMessagingService.ResultCallback<List<FoodCriteriaLocationSearchHistoryDTO>>() {
						@Override
						public void onReceiveResult(@NonNull List<FoodCriteriaLocationSearchHistoryDTO> result) throws RemoteException {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									LocationSettingsActivity.this.foodCriteriaLocationHistoryList = result;

									foodCriteriaLocationHistoryAdapter.setFoodCriteriaLocationHistoryList(foodCriteriaLocationHistoryList);
									foodCriteriaLocationHistoryAdapter.notifyDataSetChanged();
								}
							});

						}
					});
				}

			}
		});

	}

	@Override
	public void onSelectedNewLocation(LocationDTO locationDTO) {
		foodCriteriaLocationSearchHistoryViewModel.insertByEventId(iGetEventValue.getCalendarId(), iGetEventValue.getEventId(), locationDTO.getPlaceName(), locationDTO.getAddressName(),
				locationDTO.getRoadAddressName()
				, String.valueOf(locationDTO.getLatitude()), String.valueOf(locationDTO.getLongitude()), locationDTO.getLocationType(),
				new CarrierMessagingService.ResultCallback<List<FoodCriteriaLocationSearchHistoryDTO>>() {
					@Override
					public void onReceiveResult(@NonNull List<FoodCriteriaLocationSearchHistoryDTO> result) throws RemoteException {
						//변경 타입 업데이트
						int id = result.get(result.size() - 1).getId();
						foodCriteriaLocationInfoViewModel.updateByEventId(iGetEventValue.getCalendarId(), iGetEventValue.getEventId(),
								CriteriaLocationType.TYPE_CUSTOM_SELECTED_LOCATION.value(),
								id, new CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO>() {
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
								});
					}
				});

	}
}