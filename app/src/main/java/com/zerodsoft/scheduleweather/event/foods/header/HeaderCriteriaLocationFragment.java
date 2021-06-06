package com.zerodsoft.scheduleweather.event.foods.header;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.common.interfaces.DataProcessingCallback;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.databinding.FragmentHeaderCriteriaLocationBinding;
import com.zerodsoft.scheduleweather.etc.AppPermission;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.criterialocation.RestaurantCriteriaLocationSettingsFragment;
import com.zerodsoft.scheduleweather.event.foods.enums.CriteriaLocationType;
import com.zerodsoft.scheduleweather.event.foods.main.RestaurantMainHostFragment;
import com.zerodsoft.scheduleweather.event.foods.share.CriteriaLocationCloud;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.CustomFoodMenuViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationHistoryViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.RestaurantSharedViewModel;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.navermap.model.CoordToAddressUtil;
import com.zerodsoft.scheduleweather.navermap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationSearchHistoryDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public class HeaderCriteriaLocationFragment extends Fragment {
	private FragmentHeaderCriteriaLocationBinding binding;

	private LocationViewModel locationViewModel;
	private FoodCriteriaLocationInfoViewModel foodCriteriaLocationInfoViewModel;
	private FoodCriteriaLocationHistoryViewModel foodCriteriaLocationSearchHistoryViewModel;
	private RestaurantSharedViewModel sharedViewModel;

	private LocationManager locationManager;
	private LocationDTO selectedLocationDTO;
	private FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO;

	private LocationDTO criteriaLocationDTO;
	private boolean clickedGps = false;
	private Long eventId;
	private IMapPoint iMapPoint;

	private DataProcessingCallback<LocationDTO> foodCriteriaLocationCallback;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
		sharedViewModel = new ViewModelProvider(requireActivity()).get(RestaurantSharedViewModel.class);
		locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

		foodCriteriaLocationInfoViewModel = new ViewModelProvider(requireActivity()).get(FoodCriteriaLocationInfoViewModel.class);
		foodCriteriaLocationSearchHistoryViewModel = new ViewModelProvider(requireActivity()).get(FoodCriteriaLocationHistoryViewModel.class);

		eventId = sharedViewModel.getEventId();
		iMapPoint = sharedViewModel.getiMapPoint();

		foodCriteriaLocationInfoViewModel.getOnChangedCriteriaLocationLiveData().observe(this,
				new Observer<FoodCriteriaLocationInfoDTO>() {
					@Override
					public void onChanged(FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO) {
						if (getActivity() != null) {
							requireActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									loadSelectedDetailLocation();
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

		binding.customProgressView.onStartedProcessingData();
		binding.criteriaLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Fragment parentFragment = getParentFragment();
				Fragment hostFragment = parentFragment.getParentFragment();

				hostFragment.getChildFragmentManager().beginTransaction().hide(parentFragment)
						.add(R.id.content_fragment_container, new RestaurantCriteriaLocationSettingsFragment(),
								RestaurantCriteriaLocationSettingsFragment.TAG)
						.addToBackStack(RestaurantCriteriaLocationSettingsFragment.TAG).commit();
			}

		});


		loadSelectedDetailLocation();
	}

	public void setFoodCriteriaLocationCallback(DataProcessingCallback<LocationDTO> foodCriteriaLocationCallback) {
		this.foodCriteriaLocationCallback = foodCriteriaLocationCallback;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		gpsOnResultLauncher.unregister();
		permissionsResultLauncher.unregister();
	}

	public void loadSelectedDetailLocation() {
		//지정한 위치정보를 가져온다
		locationViewModel.getLocation(
				eventId, new DbQueryCallback<LocationDTO>() {
					@Override
					public void onResultSuccessful(LocationDTO locationResultDto) {
						selectedLocationDTO = locationResultDto;
						loadCriteriaLocation();
					}

					@Override
					public void onResultNoData() {
						setCriteriaIfHavntLocation();
					}
				});
	}

	private void loadCriteriaLocation() {
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
						setCriteriaIfHavntCriteria();
					}
				});
	}

	private void setCriteriaIfHavntLocation() {
		//기본/상세 위치가 지정되어 있지 않으면, 맵의 중심 좌표를 기준으로 하도록 설정해준다
		foodCriteriaLocationInfoViewModel.contains(eventId
				, new DbQueryCallback<FoodCriteriaLocationInfoDTO>() {
					@Override
					public void onResultSuccessful(FoodCriteriaLocationInfoDTO result) {
						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								foodCriteriaLocationInfoDTO = result;
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
					}
				});

	}

	private void setCriteriaIfHavntCriteria() {
		//기준 정보가 지정되어 있지 않으면, 지정한 장소/주소를 기준으로 하도록 설정해준다
		foodCriteriaLocationInfoViewModel.contains(eventId, new DbQueryCallback<FoodCriteriaLocationInfoDTO>() {
			@Override
			public void onResultSuccessful(FoodCriteriaLocationInfoDTO result) {
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
						, CriteriaLocationType.TYPE_SELECTED_LOCATION.value(), null,
						new DbQueryCallback<FoodCriteriaLocationInfoDTO>() {
							@Override
							public void onResultSuccessful(FoodCriteriaLocationInfoDTO result) {
								requireActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										setCriteria(CriteriaLocationType.TYPE_SELECTED_LOCATION);
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
				criteriaLocationDTO = selectedLocationDTO;
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
		binding.criteriaLocation.setText(getString(R.string.finding_current_location));
		clickedGps = true;

		//권한 확인
		boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
				checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
			if (isGpsEnabled) {
				locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
					@Override
					public void onLocationChanged(Location location) {
						locationManager.removeUpdates(this);

						if (clickedGps) {
							clickedGps = false;
							onCatchedGps(location);
						}
					}

					@Override
					public void onStatusChanged(String s, int i, Bundle bundle) {

					}

					@Override
					public void onProviderEnabled(String s) {

					}

					@Override
					public void onProviderDisabled(String s) {

					}
				}, null);

				locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
					@Override
					public void onLocationChanged(Location location) {
						locationManager.removeUpdates(this);

						if (clickedGps) {
							clickedGps = false;
							onCatchedGps(location);
						}
					}

					@Override
					public void onStatusChanged(String s, int i, Bundle bundle) {

					}

					@Override
					public void onProviderEnabled(String s) {

					}

					@Override
					public void onProviderDisabled(String s) {

					}
				}, null);
			} else {
				showRequestGpsDialog();
			}
		}

	}

	private void showRequestGpsDialog() {
		new AlertDialog.Builder(getActivity())
				.setMessage(getString(R.string.request_to_make_gps_on))
				.setPositiveButton(getString(R.string.check), new
						DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface paramDialogInterface, int paramInt) {
								gpsOnResultLauncher.launch(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
				.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						binding.criteriaLocation.callOnClick();
					}
				})
				.setCancelable(false)
				.show();
	}

	private final ActivityResultLauncher<Intent> gpsOnResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
			new ActivityResultCallback<ActivityResult>() {
				@Override
				public void onActivityResult(ActivityResult result) {
					if (AppPermission.grantedPermissions(getContext(), Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
						gps();
					} else {
						binding.criteriaLocation.callOnClick();
					}
				}
			});

	private final ActivityResultLauncher<String[]> permissionsResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
			new ActivityResultCallback<Map<String, Boolean>>() {
				@Override
				public void onActivityResult(Map<String, Boolean> result) {
					if (result.get(Manifest.permission.ACCESS_COARSE_LOCATION) &&
							result.get(Manifest.permission.ACCESS_FINE_LOCATION)) {
						// 권한 허용됨
						gps();
					} else {
						// 권한 거부됨
						Toast.makeText(getContext(), R.string.message_needs_location_permission, Toast.LENGTH_SHORT).show();
						foodCriteriaLocationInfoViewModel.updateByEventId(eventId
								, CriteriaLocationType.TYPE_SELECTED_LOCATION.value(), null, new DbQueryCallback<FoodCriteriaLocationInfoDTO>() {
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
}