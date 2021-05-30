package com.zerodsoft.scheduleweather.event.foods.main;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.RemoteException;
import android.provider.Settings;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IRefreshView;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.common.interfaces.OnPopBackStackFragmentCallback;
import com.zerodsoft.scheduleweather.databinding.FragmentFoodsCategoryListBinding;
import com.zerodsoft.scheduleweather.etc.AppPermission;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.adapter.FoodCategoryAdapter;
import com.zerodsoft.scheduleweather.event.foods.criterialocation.RestaurantCriteriaLocationSettingsFragment;
import com.zerodsoft.scheduleweather.event.foods.dto.FoodCategoryItem;
import com.zerodsoft.scheduleweather.event.foods.enums.CriteriaLocationType;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedCategoryItem;
import com.zerodsoft.scheduleweather.event.foods.RestaurantDialogFragment;
import com.zerodsoft.scheduleweather.event.foods.settings.CustomFoodMenuSettingsActivity;
import com.zerodsoft.scheduleweather.event.foods.share.CriteriaLocationCloud;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.CustomFoodMenuViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationHistoryViewModel;
import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.navermap.interfaces.FavoriteLocationsListener;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.navermap.model.CoordToAddressUtil;
import com.zerodsoft.scheduleweather.navermap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;
import com.zerodsoft.scheduleweather.room.dto.CustomFoodMenuDTO;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationSearchHistoryDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.checkSelfPermission;

public class FoodsMenuListFragment extends Fragment implements OnClickedCategoryItem, OnClickedListItem<FoodCategoryItem>,
		IRefreshView {
	public static final String TAG = "FoodsMenuListFragment";

	private final RestaurantDialogFragment.FoodMenuChipsViewController foodMenuChipsViewController;
	private final BottomSheetController bottomSheetController;
	private final FavoriteLocationsListener favoriteLocationsListener;
	private final int COLUMN_COUNT = 4;
	private final RestaurantMainNavHostFragment.IGetEventValue iGetEventValue;
	private final IMapPoint iMapPoint;

	private FragmentFoodsCategoryListBinding binding;

	private CustomFoodMenuViewModel customFoodCategoryViewModel;
	private LocationViewModel locationViewModel;
	private CalendarViewModel calendarViewModel;
	private FoodCriteriaLocationInfoViewModel foodCriteriaLocationInfoViewModel;
	private FoodCriteriaLocationHistoryViewModel foodCriteriaLocationSearchHistoryViewModel;

	private LocationManager locationManager;
	private LocationDTO selectedLocationDTO;
	private FoodCriteriaLocationSearchHistoryDTO foodCriteriaLocationSearchHistoryDTO;
	private FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO;

	private LocationDTO criteriaLocationDTO;
	private boolean clickedGps = false;

	public FoodsMenuListFragment(RestaurantMainNavHostFragment.IGetEventValue iGetEventValue,
	                             RestaurantDialogFragment.FoodMenuChipsViewController foodMenuChipsViewController,
	                             BottomSheetController bottomSheetController, FavoriteLocationsListener favoriteLocationsListener, IMapPoint iMapPoint) {
		this.iGetEventValue = iGetEventValue;
		this.foodMenuChipsViewController = foodMenuChipsViewController;
		this.bottomSheetController = bottomSheetController;
		this.favoriteLocationsListener = favoriteLocationsListener;
		this.iMapPoint = iMapPoint;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentFoodsCategoryListBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
		locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
		foodCriteriaLocationInfoViewModel = new ViewModelProvider(this).get(FoodCriteriaLocationInfoViewModel.class);
		foodCriteriaLocationSearchHistoryViewModel = new ViewModelProvider(this).get(FoodCriteriaLocationHistoryViewModel.class);
		customFoodCategoryViewModel = new ViewModelProvider(this).get(CustomFoodMenuViewModel.class);

		getParentFragmentManager().addOnBackStackChangedListener(onBackStackChangedListener);

		GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), COLUMN_COUNT);
		binding.categoryGridview.setLayoutManager(gridLayoutManager);

		//기준 주소 표시
		loadSelectedDetailLocation();
		setCategories();

		binding.criteriaLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				RestaurantCriteriaLocationSettingsFragment restaurantCriteriaLocationSettingsFragment =
						new RestaurantCriteriaLocationSettingsFragment(iGetEventValue, FoodsMenuListFragment.this);

				getParentFragmentManager().beginTransaction().hide(FoodsMenuListFragment.this).add(R.id.foods_main_fragment_container,
						restaurantCriteriaLocationSettingsFragment,
						RestaurantCriteriaLocationSettingsFragment.TAG).addToBackStack(RestaurantCriteriaLocationSettingsFragment.TAG).commit();
			}
		});
	}

	@Override
	public void onDestroy() {
		customFoodSettingsActivityResultLauncher.unregister();
		gpsOnResultLauncher.unregister();
		locationSettingsActivityResultLauncher.unregister();
		permissionsResultLauncher.unregister();
		getParentFragmentManager().removeOnBackStackChangedListener(onBackStackChangedListener);
		super.onDestroy();
	}

	private void setCriteriaIfHavntLocation() {
		//기본/상세 위치가 지정되어 있지 않으면, 맵의 중심 좌표를 기준으로 하도록 설정해준다
		foodCriteriaLocationInfoViewModel.contains(iGetEventValue.getEventId()
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
						foodCriteriaLocationInfoViewModel.insertByEventId(iGetEventValue.getCalendarId(), iGetEventValue.getEventId()
								, CriteriaLocationType.TYPE_MAP_CENTER_POINT.value(), null,
								new CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO>() {
									@Override
									public void onReceiveResult(@NonNull FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO) throws RemoteException {

										requireActivity().runOnUiThread(new Runnable() {
											@Override
											public void run() {
												setCriteria(CriteriaLocationType.TYPE_MAP_CENTER_POINT);
											}
										});
									}
								});
					}
				});

	}

	private void setCriteriaIfHavntCriteria() {
		//기준 정보가 지정되어 있지 않으면, 지정한 장소/주소를 기준으로 하도록 설정해준다
		foodCriteriaLocationInfoViewModel.contains(iGetEventValue.getEventId(), new DbQueryCallback<FoodCriteriaLocationInfoDTO>() {
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
				foodCriteriaLocationInfoViewModel.insertByEventId(iGetEventValue.getCalendarId(), iGetEventValue.getEventId()
						, CriteriaLocationType.TYPE_SELECTED_LOCATION.value(), null,
						new CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO>() {
							@Override
							public void onReceiveResult(@NonNull FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO) throws RemoteException {

								requireActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										setCriteria(CriteriaLocationType.TYPE_SELECTED_LOCATION);
									}
								});
							}
						});
			}
		});

	}

	private void loadSelectedDetailLocation() {
		//지정한 위치정보를 가져온다
		locationViewModel.getLocation(
				iGetEventValue.getEventId(), new DbQueryCallback<LocationDTO>() {
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
		foodCriteriaLocationInfoViewModel.selectByEventId(iGetEventValue.getCalendarId(), iGetEventValue.getEventId()
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
						binding.progressBar.setVisibility(View.GONE);
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
								binding.progressBar.setVisibility(View.GONE);
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
						foodCriteriaLocationSearchHistoryDTO = foodCriteriaLocationSearchHistoryResultDto;
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
								binding.progressBar.setVisibility(View.GONE);
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
		binding.progressBar.setVisibility(View.VISIBLE);
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
				binding.progressBar.setVisibility(View.GONE);
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
						foodCriteriaLocationInfoViewModel.updateByEventId(iGetEventValue.getCalendarId(), iGetEventValue.getEventId()
								, CriteriaLocationType.TYPE_SELECTED_LOCATION.value(), null, new DbQueryCallback<FoodCriteriaLocationInfoDTO>() {
									@Override
									public void onResultSuccessful(FoodCriteriaLocationInfoDTO result) {
										requireActivity().runOnUiThread(new Runnable() {
											@Override
											public void run() {
												binding.progressBar.setVisibility(View.GONE);
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
						binding.progressBar.setVisibility(View.GONE);
						binding.categoryGridview.setVisibility(View.VISIBLE);
					}
				});
			}

			@Override
			public void onResponseFailed(Exception e) {

			}
		});
	}


	private void setCategories() {
		customFoodCategoryViewModel.select(new DbQueryCallback<List<CustomFoodMenuDTO>>() {
			@Override
			public void onResultSuccessful(List<CustomFoodMenuDTO> resultList) {
				FoodCategoryAdapter foodCategoryAdapter = new FoodCategoryAdapter(FoodsMenuListFragment.this, COLUMN_COUNT);

				final String[] DEFAULT_FOOD_MENU_NAME_ARR = getResources().getStringArray(R.array.food_menu_list);
				List<FoodCategoryItem> itemsList = new ArrayList<>();

				TypedArray imgs = getResources().obtainTypedArray(R.array.food_menu_image_list);

				for (int index = 0; index < DEFAULT_FOOD_MENU_NAME_ARR.length; index++) {
					itemsList.add(new FoodCategoryItem(DEFAULT_FOOD_MENU_NAME_ARR[index]
							, imgs.getDrawable(index), true));
				}

				if (!resultList.isEmpty()) {
					for (CustomFoodMenuDTO customFoodCategory : resultList) {
						itemsList.add(new FoodCategoryItem(customFoodCategory.getMenuName(), null, false));
					}
				}
				itemsList.add(new FoodCategoryItem(getString(R.string.add_custom_food_menu), null, false));
				foodCategoryAdapter.setItems(itemsList);

				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						binding.categoryGridview.setAdapter(foodCategoryAdapter);
					}
				});
			}

			@Override
			public void onResultNoData() {

			}
		});


	}

	@Override
	public void onClickedFoodCategory(FoodCategoryItem foodCategoryItem) {

	}

	private final ActivityResultLauncher<Intent> locationSettingsActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
			new ActivityResultCallback<ActivityResult>() {
				@Override
				public void onActivityResult(ActivityResult result) {
					if (result.getResultCode() == RESULT_OK) {
						loadCriteriaLocation();
					}
				}
			});


	@Override
	public void onClickedListItem(FoodCategoryItem e, int position) {
		if (!e.isDefault() && e.getCategoryName().equals(getString(R.string.add_custom_food_menu))) {
			customFoodSettingsActivityResultLauncher.launch(new Intent(getActivity(), CustomFoodMenuSettingsActivity.class));
		} else {

			RestaurantListTabFragment restaurantListTabFragment = new RestaurantListTabFragment(e.getCategoryName(),
					foodMenuChipsViewController, bottomSheetController, favoriteLocationsListener, new OnPopBackStackFragmentCallback() {
				@Override
				public void onPopped() {
				}
			});
			FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
			fragmentTransaction.hide(this).add(R.id.foods_main_fragment_container, restaurantListTabFragment,
					RestaurantListTabFragment.TAG).addToBackStack(RestaurantListTabFragment.TAG).setPrimaryNavigationFragment(restaurantListTabFragment).commit();
		}
	}

	@Override
	public void deleteListItem(FoodCategoryItem e, int position) {

	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (hidden) {

		} else {

		}
	}

	private final ActivityResultLauncher<Intent> customFoodSettingsActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
			new ActivityResultCallback<ActivityResult>() {
				@Override
				public void onActivityResult(ActivityResult result) {
					if (result.getResultCode() == RESULT_OK) {
						setCategories();
					}
				}
			});

	private final FragmentManager.OnBackStackChangedListener onBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
		@Override
		public void onBackStackChanged() {
		}
	};

	@Override
	public void refreshView() {
		loadSelectedDetailLocation();
	}
}