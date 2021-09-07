package com.zerodsoft.calendarplatform.event.foods.header;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.common.classes.Gps;
import com.zerodsoft.calendarplatform.common.interfaces.DataProcessingCallback;
import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.databinding.FragmentHeaderCriteriaLocationBinding;
import com.zerodsoft.calendarplatform.common.classes.AppPermission;
import com.zerodsoft.calendarplatform.etc.LocationType;
import com.zerodsoft.calendarplatform.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.calendarplatform.event.foods.criterialocation.RestaurantCriteriaLocationSettingsFragment;
import com.zerodsoft.calendarplatform.event.foods.enums.CriteriaLocationType;
import com.zerodsoft.calendarplatform.event.foods.interfaces.CriteriaLocationListener;
import com.zerodsoft.calendarplatform.event.foods.interfaces.IOnSetView;
import com.zerodsoft.calendarplatform.event.foods.share.CriteriaLocationCloud;
import com.zerodsoft.calendarplatform.event.foods.viewmodel.FoodCriteriaLocationHistoryViewModel;
import com.zerodsoft.calendarplatform.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;
import com.zerodsoft.calendarplatform.event.foods.viewmodel.RestaurantSharedViewModel;
import com.zerodsoft.calendarplatform.kakaoplace.retrofit.KakaoLocalDownloader;
import com.zerodsoft.calendarplatform.navermap.interfaces.IMapPoint;
import com.zerodsoft.calendarplatform.navermap.interfaces.OnKakaoLocalApiCallback;
import com.zerodsoft.calendarplatform.kakaoplace.LocalParameterUtil;
import com.zerodsoft.calendarplatform.navermap.viewmodel.MapSharedViewModel;
import com.zerodsoft.calendarplatform.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.KakaoLocalResponse;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;
import com.zerodsoft.calendarplatform.room.dto.FoodCriteriaLocationInfoDTO;
import com.zerodsoft.calendarplatform.room.dto.FoodCriteriaLocationSearchHistoryDTO;
import com.zerodsoft.calendarplatform.room.dto.LocationDTO;

import org.jetbrains.annotations.NotNull;

public class HeaderCriteriaLocationFragment extends Fragment {
	private FragmentHeaderCriteriaLocationBinding binding;

	private LocationViewModel locationViewModel;
	private FoodCriteriaLocationInfoViewModel foodCriteriaLocationInfoViewModel;
	private FoodCriteriaLocationHistoryViewModel foodCriteriaLocationSearchHistoryViewModel;
	private RestaurantSharedViewModel restaurantSharedViewModel;
	private MapSharedViewModel mapSharedViewModel;

	private FoodCriteriaLocationInfoDTO savedFoodCriteriaLocationInfoDTO;

	private LocationDTO criteriaLocationDTO;
	private Long eventId;
	private IMapPoint iMapPoint;

	private CriteriaLocationListener criteriaLocationListener;
	private IOnSetView iOnSetView;

	private final Gps gps = new Gps();

	private boolean initializing = true;

	private FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
		@Override
		public void onFragmentCreated(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
			super.onFragmentCreated(fm, f, savedInstanceState);
			if (f instanceof RestaurantCriteriaLocationSettingsFragment) {
				iOnSetView.setFragmentContainerVisibility(IOnSetView.ViewType.HEADER, View.GONE);
			}
		}


		@Override
		public void onFragmentDestroyed(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f) {
			super.onFragmentDestroyed(fm, f);
			if (f instanceof RestaurantCriteriaLocationSettingsFragment) {
				iOnSetView.setFragmentContainerVisibility(IOnSetView.ViewType.HEADER, View.VISIBLE);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		iOnSetView = (IOnSetView) getParentFragment();

		getParentFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);

		restaurantSharedViewModel = new ViewModelProvider(getParentFragment().getParentFragment()).get(RestaurantSharedViewModel.class);
		locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
		mapSharedViewModel =
				new ViewModelProvider(getParentFragment().getParentFragment().getParentFragment()).get(MapSharedViewModel.class);
		iMapPoint = mapSharedViewModel.getiMapPoint();

		foodCriteriaLocationInfoViewModel =
				new ViewModelProvider(requireActivity()).get(FoodCriteriaLocationInfoViewModel.class);
		foodCriteriaLocationSearchHistoryViewModel =
				new ViewModelProvider(requireActivity()).get(FoodCriteriaLocationHistoryViewModel.class);

		eventId = restaurantSharedViewModel.getEventId();

		foodCriteriaLocationInfoViewModel.getOnRefreshCriteriaLocationLiveData().observe(this,
				new Observer<FoodCriteriaLocationInfoDTO>() {
					@Override
					public void onChanged(FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO) {
						if (!initializing) {
							requireActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									CriteriaLocationCloud.clear();
									loadCriteriaLocation();
								}
							});
						}
					}
				});

		foodCriteriaLocationInfoViewModel.getOnChangedCriteriaLocationLiveData().observe(this,
				new Observer<FoodCriteriaLocationInfoDTO>() {
					@Override
					public void onChanged(FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO) {
						if (!initializing) {
							requireActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									CriteriaLocationCloud.clear();
									loadCriteriaLocation();
								}
							});
						}
					}
				});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentHeaderCriteriaLocationBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		binding.customProgressView.setContentView(binding.criteriaLocationLayout);
		binding.criteriaLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				FragmentManager parentFragmentManager = getParentFragmentManager();
				Fragment contentFragment = parentFragmentManager.findFragmentById(R.id.content_fragment_container);

				String tag = getString(R.string.tag_restaurant_criteria_location_settings_fragment);
				parentFragmentManager.beginTransaction()
						.hide(contentFragment)
						.add(R.id.content_fragment_container, new RestaurantCriteriaLocationSettingsFragment(), tag)
						.addToBackStack(tag).commit();
			}
		});

		if (eventId == null) {
			binding.criteriaLocation.setClickable(false);
		}

		loadCriteriaLocation();
	}

	public void setCriteriaLocationListener(CriteriaLocationListener criteriaLocationListener) {
		this.criteriaLocationListener = criteriaLocationListener;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getParentFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
		requestOnGpsLauncher.unregister();
		requestLocationPermissionLauncher.unregister();
		gps.removeUpdate();
	}

	private void loadCriteriaLocation() {
		binding.customProgressView.onStartedProcessingData(getString(R.string.loading_criteria_location));
		if (criteriaLocationListener != null) {
			criteriaLocationListener.onStartedGettingCriteriaLocation();
		}

		if (!CriteriaLocationCloud.isEmpty()) {
			setCriteria(CriteriaLocationCloud.getCriteriaLocationType());
			return;
		}

		if (eventId == null) {
			setCriteria(CriteriaLocationType.TYPE_MAP_CENTER_POINT);
		} else {
			foodCriteriaLocationInfoViewModel.selectByEventId(eventId
					, new DbQueryCallback<FoodCriteriaLocationInfoDTO>() {
						@Override
						public void onResultSuccessful(FoodCriteriaLocationInfoDTO result) {
							savedFoodCriteriaLocationInfoDTO = result;

							if (CriteriaLocationType.enumOf(savedFoodCriteriaLocationInfoDTO.getUsingType()) == CriteriaLocationType.TYPE_SELECTED_LOCATION) {
								locationViewModel.getLocation(eventId, new DbQueryCallback<LocationDTO>() {
									@Override
									public void onResultSuccessful(LocationDTO savedLocationDto) {
										requireActivity().runOnUiThread(new Runnable() {
											@Override
											public void run() {
												criteriaLocationDTO = savedLocationDto;
												setCriteria(CriteriaLocationType.enumOf(savedFoodCriteriaLocationInfoDTO.getUsingType()));
											}
										});
									}

									@Override
									public void onResultNoData() {
										foodCriteriaLocationInfoViewModel.updateByEventId(eventId, CriteriaLocationType.TYPE_MAP_CENTER_POINT.value(), null, new DbQueryCallback<FoodCriteriaLocationInfoDTO>() {
											@Override
											public void onResultSuccessful(FoodCriteriaLocationInfoDTO newFoodCriteriaLocationInfoDTO) {
												requireActivity().runOnUiThread(new Runnable() {
													@Override
													public void run() {
														savedFoodCriteriaLocationInfoDTO = newFoodCriteriaLocationInfoDTO;
														setCriteria(CriteriaLocationType.enumOf(newFoodCriteriaLocationInfoDTO.getUsingType()));
													}
												});
											}

											@Override
											public void onResultNoData() {

											}
										});
									}
								});
							} else {
								requireActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										setCriteria(CriteriaLocationType.enumOf(savedFoodCriteriaLocationInfoDTO.getUsingType()));
									}
								});
							}
						}

						@Override
						public void onResultNoData() {
							locationViewModel.getLocation(eventId, new DbQueryCallback<LocationDTO>() {
								@Override
								public void onResultSuccessful(LocationDTO savedLocationDto) {
									criteriaLocationDTO = savedLocationDto;
									foodCriteriaLocationInfoViewModel.insertByEventId(eventId
											, CriteriaLocationType.TYPE_SELECTED_LOCATION.value(), null,
											new DbQueryCallback<FoodCriteriaLocationInfoDTO>() {
												@Override
												public void onResultSuccessful(FoodCriteriaLocationInfoDTO newFoodCriteriaLocationInfoDTO) {
													savedFoodCriteriaLocationInfoDTO = newFoodCriteriaLocationInfoDTO;

													requireActivity().runOnUiThread(new Runnable() {
														@Override
														public void run() {
															setCriteria(CriteriaLocationType.enumOf(newFoodCriteriaLocationInfoDTO.getUsingType()));
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
									foodCriteriaLocationInfoViewModel.insertByEventId(eventId
											, CriteriaLocationType.TYPE_MAP_CENTER_POINT.value(), null,
											new DbQueryCallback<FoodCriteriaLocationInfoDTO>() {
												@Override
												public void onResultSuccessful(FoodCriteriaLocationInfoDTO newFoodCriteriaLocationInfoDTO) {
													savedFoodCriteriaLocationInfoDTO = newFoodCriteriaLocationInfoDTO;

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
							});

						}
					});
		}
	}


	private void setCriteria(CriteriaLocationType criteriaLocationType) {
		initializing = false;

		switch (criteriaLocationType) {
			case TYPE_SELECTED_LOCATION: {
				CriteriaLocationCloud.setCoordinate(criteriaLocationType,
						criteriaLocationDTO.getLatitude(), criteriaLocationDTO.getLongitude(), criteriaLocationDTO.getLocTitleName());

				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (criteriaLocationDTO.getLocationType() == LocationType.PLACE) {
							binding.criteriaLocation.setText(criteriaLocationDTO.getPlaceName());
						} else {
							binding.criteriaLocation.setText(criteriaLocationDTO.getAddressName());
						}

						binding.customProgressView.onSuccessfulProcessingData();
						if (criteriaLocationListener != null) {
							criteriaLocationListener.onFinishedGettingCriteriaLocation(criteriaLocationDTO);
						}
					}
				});
				break;
			}

			case TYPE_MAP_CENTER_POINT: {
				//현재 위치 파악
				LatLng mapCenterPoint = iMapPoint.getMapCenterPoint();
				LocalApiPlaceParameter localApiPlaceParameter = LocalParameterUtil.getCoordToAddressParameter(mapCenterPoint.latitude,
						mapCenterPoint.longitude);

				KakaoLocalDownloader.coordToAddress(localApiPlaceParameter, new OnKakaoLocalApiCallback() {
					@Override
					public void onResultSuccessful(int type, KakaoLocalResponse result) {
						LocationDTO locationDTO = new LocationDTO();
						CoordToAddressDocuments coordToAddressDocument = ((CoordToAddress) result).getCoordToAddressDocuments().get(0);

						locationDTO.setAddress(coordToAddressDocument.getCoordToAddressAddress().getAddressName(), null,
								String.valueOf(mapCenterPoint.latitude),
								String.valueOf(mapCenterPoint.longitude));

						criteriaLocationDTO = locationDTO;
						CriteriaLocationCloud.setCoordinate(criteriaLocationType, criteriaLocationDTO.getLatitude(), criteriaLocationDTO.getLongitude(),
								criteriaLocationDTO.getLocTitleName());

						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								binding.criteriaLocation.setText(criteriaLocationDTO.getAddressName());
								binding.customProgressView.onSuccessfulProcessingData();

								if (criteriaLocationListener != null) {
									criteriaLocationListener.onFinishedGettingCriteriaLocation(criteriaLocationDTO);
								}
							}
						});
					}

					@Override
					public void onResultNoData() {

					}
				});
				break;
			}

			case TYPE_CURRENT_LOCATION_GPS: {
				gps();
				break;
			}

			case TYPE_CUSTOM_SELECTED_LOCATION: {
				//지정 위치 파악
				foodCriteriaLocationSearchHistoryViewModel.containsData(savedFoodCriteriaLocationInfoDTO.getHistoryLocationId(), new DbQueryCallback<Boolean>() {
					@Override
					public void onResultSuccessful(Boolean isContains) {
						if (isContains) {
							foodCriteriaLocationSearchHistoryViewModel.select(savedFoodCriteriaLocationInfoDTO.getHistoryLocationId(), new DbQueryCallback<FoodCriteriaLocationSearchHistoryDTO>() {
								@Override
								public void onResultSuccessful(FoodCriteriaLocationSearchHistoryDTO foodCriteriaLocationSearchHistoryResultDto) {
									LocationDTO locationDTO = new LocationDTO();

									locationDTO.setAddress(foodCriteriaLocationSearchHistoryResultDto.getAddressName(), null,
											foodCriteriaLocationSearchHistoryResultDto.getLatitude(), foodCriteriaLocationSearchHistoryResultDto.getLongitude());

									criteriaLocationDTO = locationDTO;
									CriteriaLocationCloud.setCoordinate(criteriaLocationType, criteriaLocationDTO.getLatitude(),
											criteriaLocationDTO.getLongitude(), criteriaLocationDTO.getLocTitleName());

									requireActivity().runOnUiThread(new Runnable() {
										@Override
										public void run() {
											if (criteriaLocationDTO.getLocationType() == LocationType.PLACE) {
												binding.criteriaLocation.setText(criteriaLocationDTO.getPlaceName());
											} else {
												binding.criteriaLocation.setText(criteriaLocationDTO.getAddressName());
											}
											binding.customProgressView.onSuccessfulProcessingData();

											if (criteriaLocationListener != null) {
												criteriaLocationListener.onFinishedGettingCriteriaLocation(criteriaLocationDTO);
											}
										}
									});
								}

								@Override
								public void onResultNoData() {

								}
							});
						} else {
							foodCriteriaLocationInfoViewModel.updateByEventId(eventId
									, CriteriaLocationType.TYPE_MAP_CENTER_POINT.value(), null,
									new DbQueryCallback<FoodCriteriaLocationInfoDTO>() {
										@Override
										public void onResultSuccessful(FoodCriteriaLocationInfoDTO result) {
											requireActivity().runOnUiThread(new Runnable() {
												@Override
												public void run() {
													setCriteria(CriteriaLocationType.TYPE_MAP_CENTER_POINT);
													Toast.makeText(getContext(),
															R.string.selected_map_center_point_because_not_selected_custom_criteria_location,
															Toast.LENGTH_SHORT).show();
													if (criteriaLocationListener != null) {
														criteriaLocationListener.onFinishedGettingCriteriaLocation(criteriaLocationDTO);
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

					@Override
					public void onResultNoData() {

					}
				});


			}
			break;

			default:
				assert (false) : "Unknown";
		}
	}

	private void gps() {
		gps.runGps(requireActivity(), new DataProcessingCallback<Location>() {
			@Override
			public void onResultSuccessful(Location result) {
				onCatchedGps(result);
			}

			@Override
			public void onResultNoData() {
				foodCriteriaLocationInfoViewModel.updateByEventId(eventId, CriteriaLocationType.TYPE_MAP_CENTER_POINT.value(), null, new DbQueryCallback<FoodCriteriaLocationInfoDTO>() {
					@Override
					public void onResultSuccessful(FoodCriteriaLocationInfoDTO newFoodCriteriaLocationInfoDTO) {
						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								savedFoodCriteriaLocationInfoDTO = newFoodCriteriaLocationInfoDTO;
								setCriteria(CriteriaLocationType.enumOf(newFoodCriteriaLocationInfoDTO.getUsingType()));
							}
						});
					}

					@Override
					public void onResultNoData() {

					}
				});
			}
		}, requestOnGpsLauncher, requestLocationPermissionLauncher);

	}

	private final ActivityResultLauncher<Intent> requestOnGpsLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
			new ActivityResultCallback<ActivityResult>() {
				@Override
				public void onActivityResult(ActivityResult result) {
					if (AppPermission.grantedPermissions(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
						gps();
					} else {
						binding.criteriaLocation.callOnClick();
					}
				}
			});


	private void onCatchedGps(Location location) {
		LocationDTO locationDtoByGps = new LocationDTO();
		locationDtoByGps.setAddress(null, null, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
		setCurrentLocationData(locationDtoByGps);
	}

	private void setCurrentLocationData(LocationDTO locationDtoByGps) {
		//주소 reverse geocoding
		LocalApiPlaceParameter localApiPlaceParameter =
				LocalParameterUtil.getCoordToAddressParameter(Double.parseDouble(locationDtoByGps.getLatitude()),
						Double.parseDouble(locationDtoByGps.getLongitude()));

		KakaoLocalDownloader.coordToAddress(localApiPlaceParameter, new OnKakaoLocalApiCallback() {
			@Override
			public void onResultSuccessful(int type, KakaoLocalResponse result) {
				LocationDTO locationDTO = new LocationDTO();
				CoordToAddressDocuments coordToAddressDocument = ((CoordToAddress) result).getCoordToAddressDocuments().get(0);

				locationDTO.setAddress(coordToAddressDocument.getCoordToAddressAddress().getAddressName(), null,
						locationDtoByGps.getLatitude(),
						locationDtoByGps.getLongitude());

				criteriaLocationDTO = locationDTO;
				CriteriaLocationCloud.setCoordinate(CriteriaLocationType.TYPE_CURRENT_LOCATION_GPS, criteriaLocationDTO.getLatitude(),
						criteriaLocationDTO.getLongitude(),
						criteriaLocationDTO.getLocTitleName());

				if (getActivity() != null) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							binding.criteriaLocation.setText(criteriaLocationDTO.getAddressName());
							binding.customProgressView.onSuccessfulProcessingData();

							if (criteriaLocationListener != null) {
								criteriaLocationListener.onFinishedGettingCriteriaLocation(criteriaLocationDTO);
							}
						}
					});
				}
			}

			@Override
			public void onResultNoData() {
				binding.customProgressView.onFailedProcessingData(getString(R.string.error_downloading_address));
			}
		});

	}

	private ActivityResultLauncher<String> requestLocationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission()
			, new ActivityResultCallback<Boolean>() {
				@Override
				public void onActivityResult(Boolean isGranted) {
					if (isGranted) {
						// 권한 허용됨
						gps();
					} else {
						// 권한 거부됨
						Toast.makeText(getContext(), R.string.message_needs_location_permission, Toast.LENGTH_SHORT).show();
						foodCriteriaLocationInfoViewModel.updateByEventId(eventId
								, CriteriaLocationType.TYPE_MAP_CENTER_POINT.value(), null, new DbQueryCallback<FoodCriteriaLocationInfoDTO>() {
									@Override
									public void onResultSuccessful(FoodCriteriaLocationInfoDTO result) {
										requireActivity().runOnUiThread(new Runnable() {
											@Override
											public void run() {
												loadCriteriaLocation();
											}
										});
									}

									@Override
									public void onResultNoData() {

									}
								});
					}
				}
			});
}