package com.zerodsoft.scheduleweather.event.foods.header;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
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
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.classes.Gps;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.common.interfaces.DataProcessingCallback;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.databinding.FragmentHeaderCriteriaLocationBinding;
import com.zerodsoft.scheduleweather.common.classes.AppPermission;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.criterialocation.RestaurantCriteriaLocationSettingsFragment;
import com.zerodsoft.scheduleweather.event.foods.enums.CriteriaLocationType;
import com.zerodsoft.scheduleweather.event.foods.share.CriteriaLocationCloud;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationHistoryViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.RestaurantSharedViewModel;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.navermap.model.CoordToAddressUtil;
import com.zerodsoft.scheduleweather.navermap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationSearchHistoryDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import org.jetbrains.annotations.NotNull;

public class HeaderCriteriaLocationFragment extends Fragment {
	private FragmentHeaderCriteriaLocationBinding binding;

	private LocationViewModel locationViewModel;
	private FoodCriteriaLocationInfoViewModel foodCriteriaLocationInfoViewModel;
	private FoodCriteriaLocationHistoryViewModel foodCriteriaLocationSearchHistoryViewModel;
	private RestaurantSharedViewModel sharedViewModel;

	private FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO;

	private LocationDTO criteriaLocationDTO;
	private Long eventId;
	private IMapPoint iMapPoint;

	private DataProcessingCallback<LocationDTO> foodCriteriaLocationCallback;

	private Gps gps = new Gps();

	private FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
		@Override
		public void onFragmentCreated(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
			super.onFragmentCreated(fm, f, savedInstanceState);
		}


		@Override
		public void onFragmentDestroyed(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f) {
			super.onFragmentDestroyed(fm, f);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getParentFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);

		sharedViewModel = new ViewModelProvider(requireActivity()).get(RestaurantSharedViewModel.class);
		locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

		foodCriteriaLocationInfoViewModel = new ViewModelProvider(requireActivity()).get(FoodCriteriaLocationInfoViewModel.class);
		foodCriteriaLocationSearchHistoryViewModel = new ViewModelProvider(requireActivity()).get(FoodCriteriaLocationHistoryViewModel.class);

		eventId = sharedViewModel.getEventId();
		iMapPoint = sharedViewModel.getiMapPoint();

		foodCriteriaLocationInfoViewModel.getOnRefreshCriteriaLocationLiveData().observe(this,
				new Observer<FoodCriteriaLocationInfoDTO>() {
					@Override
					public void onChanged(FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO) {
						if (getActivity() != null) {
							requireActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
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

		loadCriteriaLocation();
	}

	public void setFoodCriteriaLocationCallback(DataProcessingCallback<LocationDTO> foodCriteriaLocationCallback) {
		this.foodCriteriaLocationCallback = foodCriteriaLocationCallback;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getParentFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
		requestOnGpsLauncher.unregister();
		requestLocationPermissionLauncher.unregister();
	}

	private void loadCriteriaLocation() {
		binding.customProgressView.onStartedProcessingData(getString(R.string.loading_criteria_location));

		foodCriteriaLocationInfoViewModel.selectByEventId(eventId
				, new DbQueryCallback<FoodCriteriaLocationInfoDTO>() {
					@Override
					public void onResultSuccessful(FoodCriteriaLocationInfoDTO result) {
						foodCriteriaLocationInfoDTO = result;

						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								setCriteria(CriteriaLocationType.enumOf(result.getUsingType()));
							}
						});
					}

					@Override
					public void onResultNoData() {
						foodCriteriaLocationInfoViewModel.insertByEventId(eventId
								, CriteriaLocationType.TYPE_MAP_CENTER_POINT.value(), null,
								new DbQueryCallback<FoodCriteriaLocationInfoDTO>() {
									@Override
									public void onResultSuccessful(FoodCriteriaLocationInfoDTO result) {
										foodCriteriaLocationInfoDTO = result;

										requireActivity().runOnUiThread(new Runnable() {
											@Override
											public void run() {
												setCriteria(CriteriaLocationType.enumOf(result.getUsingType()));
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


	private void setCriteria(CriteriaLocationType criteriaLocationType) {
		switch (criteriaLocationType) {
			case TYPE_SELECTED_LOCATION: {
				locationViewModel.getLocation(eventId, new DbQueryCallback<LocationDTO>() {
					@Override
					public void onResultSuccessful(LocationDTO result) {
						criteriaLocationDTO = result;
						CriteriaLocationCloud.setCoordinate(criteriaLocationDTO.getLatitude(), criteriaLocationDTO.getLongitude());

						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (criteriaLocationDTO.getLocationType() == LocationType.PLACE) {
									binding.criteriaLocation.setText(criteriaLocationDTO.getPlaceName());
								} else {
									binding.criteriaLocation.setText(criteriaLocationDTO.getAddressName());
								}
								binding.customProgressView.onSuccessfulProcessingData();
								if (foodCriteriaLocationCallback != null) {
									foodCriteriaLocationCallback.processResult(criteriaLocationDTO);
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

			case TYPE_MAP_CENTER_POINT: {
				//현재 위치 파악
				LatLng mapCenterPoint = iMapPoint.getMapCenterPoint();
				LocalApiPlaceParameter localApiPlaceParameter = LocalParameterUtil.getCoordToAddressParameter(mapCenterPoint.latitude,
						mapCenterPoint.longitude);

				CoordToAddressUtil.coordToAddress(localApiPlaceParameter, new JsonDownloader<CoordToAddress>() {
					@Override
					public void onResponseSuccessful(CoordToAddress result) {
						LocationDTO locationDTO = new LocationDTO();
						CoordToAddressDocuments coordToAddressDocument = result.getCoordToAddressDocuments().get(0);

						locationDTO.setAddress(coordToAddressDocument.getCoordToAddressAddress().getAddressName(), null,
								String.valueOf(mapCenterPoint.latitude),
								String.valueOf(mapCenterPoint.longitude));

						criteriaLocationDTO = locationDTO;
						CriteriaLocationCloud.setCoordinate(criteriaLocationDTO.getLatitude(), criteriaLocationDTO.getLongitude());

						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								binding.criteriaLocation.setText(criteriaLocationDTO.getAddressName());

								binding.customProgressView.onSuccessfulProcessingData();
								if (foodCriteriaLocationCallback != null) {
									foodCriteriaLocationCallback.processResult(criteriaLocationDTO);
								}
							}
						});
					}

					@Override
					public void onResponseFailed(Exception e) {

					}
				});
				break;
			}

			case TYPE_CURRENT_LOCATION_GPS: {
				gps();
			}
			break;

			case TYPE_CUSTOM_SELECTED_LOCATION: {
				//지정 위치 파악
				if (foodCriteriaLocationInfoDTO.getHistoryLocationId() == null) {
					foodCriteriaLocationInfoViewModel.updateByEventId(eventId
							, CriteriaLocationType.TYPE_MAP_CENTER_POINT.value(), null,
							new DbQueryCallback<FoodCriteriaLocationInfoDTO>() {
								@Override
								public void onResultSuccessful(FoodCriteriaLocationInfoDTO result) {
									requireActivity().runOnUiThread(new Runnable() {
										@Override
										public void run() {
											setCriteria(CriteriaLocationType.TYPE_MAP_CENTER_POINT);
										}
									});
								}

								@Override
								public void onResultNoData() {

								}
							});
					return;
				}

				foodCriteriaLocationSearchHistoryViewModel.select(foodCriteriaLocationInfoDTO.getHistoryLocationId(), new DbQueryCallback<FoodCriteriaLocationSearchHistoryDTO>() {
					@Override
					public void onResultSuccessful(FoodCriteriaLocationSearchHistoryDTO foodCriteriaLocationSearchHistoryResultDto) {
						LocationDTO locationDTO = new LocationDTO();

						locationDTO.setAddress(foodCriteriaLocationSearchHistoryResultDto.getAddressName(), null,
								foodCriteriaLocationSearchHistoryResultDto.getLatitude(), foodCriteriaLocationSearchHistoryResultDto.getLongitude());

						criteriaLocationDTO = locationDTO;
						CriteriaLocationCloud.setCoordinate(criteriaLocationDTO.getLatitude(), criteriaLocationDTO.getLongitude());

						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (criteriaLocationDTO.getLocationType() == LocationType.PLACE) {
									binding.criteriaLocation.setText(criteriaLocationDTO.getPlaceName());
								} else {
									binding.criteriaLocation.setText(criteriaLocationDTO.getAddressName());
								}
								binding.customProgressView.onSuccessfulProcessingData();
								if (foodCriteriaLocationCallback != null) {
									foodCriteriaLocationCallback.processResult(criteriaLocationDTO);
								}
							}
						});
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
				binding.criteriaLocation.callOnClick();
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

		CoordToAddressUtil.coordToAddress(localApiPlaceParameter, new JsonDownloader<CoordToAddress>() {
			@Override
			public void onResponseSuccessful(CoordToAddress result) {
				LocationDTO locationDTO = new LocationDTO();
				CoordToAddressDocuments coordToAddressDocument = result.getCoordToAddressDocuments().get(0);

				locationDTO.setAddress(coordToAddressDocument.getCoordToAddressAddress().getAddressName(), null,
						locationDtoByGps.getLatitude(),
						locationDtoByGps.getLongitude());

				criteriaLocationDTO = locationDTO;
				CriteriaLocationCloud.setCoordinate(criteriaLocationDTO.getLatitude(), criteriaLocationDTO.getLongitude());

				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						binding.criteriaLocation.setText(criteriaLocationDTO.getAddressName());
						binding.customProgressView.onSuccessfulProcessingData();

						if (foodCriteriaLocationCallback != null) {
							foodCriteriaLocationCallback.processResult(criteriaLocationDTO);
						}
					}
				});
			}

			@Override
			public void onResponseFailed(Exception e) {
				binding.customProgressView.onFailedProcessingData(e.getMessage().toString());
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