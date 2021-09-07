package com.zerodsoft.calendarplatform.event.main;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.os.Parcel;
import android.provider.CalendarContract;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.activity.editevent.activity.ModifyInstanceFragment;
import com.zerodsoft.calendarplatform.activity.editevent.interfaces.OnEditEventResultListener;
import com.zerodsoft.calendarplatform.calendar.CalendarViewModel;
import com.zerodsoft.calendarplatform.calendar.EditEventPopupMenu;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IRefreshView;
import com.zerodsoft.calendarplatform.common.classes.CloseWindow;
import com.zerodsoft.calendarplatform.common.enums.LocationIntentCode;
import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.etc.LocationType;
import com.zerodsoft.calendarplatform.event.common.DetailLocationSelectorKey;
import com.zerodsoft.calendarplatform.event.common.SelectionDetailLocationFragment;
import com.zerodsoft.calendarplatform.event.event.fragments.EventFragment;
import com.zerodsoft.calendarplatform.event.weather.fragment.WeatherMainFragment;
import com.zerodsoft.calendarplatform.navermap.NaverMapFragment;
import com.zerodsoft.calendarplatform.navermap.MarkerType;
import com.zerodsoft.calendarplatform.navermap.searchheader.MapHeaderSearchFragment;
import com.zerodsoft.calendarplatform.room.dto.LocationDTO;
import com.zerodsoft.calendarplatform.utility.NetworkStatus;

import org.jetbrains.annotations.NotNull;

public class NewInstanceMainFragment extends NaverMapFragment implements IRefreshView {
	private int calendarId;
	private long eventId;
	private long instanceId;
	private long originalBegin;
	private long originalEnd;

	private CalendarViewModel calendarViewModel;
	private ContentValues eventValues;
	private LocationDTO selectedLocationDtoInEvent;
	private Marker selectedLocationInEventMarker;
	private InfoWindow selectedLocationInEventInfoWindow;


	private NetworkStatus networkStatus;
	private CloseWindow closeWindow = new CloseWindow(new CloseWindow.OnBackKeyDoubleClickedListener() {
		@Override
		public void onDoubleClicked() {
			getParentFragmentManager().popBackStackImmediate();
		}
	});

	private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
		@Override
		public void handleOnBackPressed() {
			FragmentManager fragmentManager = getChildFragmentManager();
			if (!fragmentManager.popBackStackImmediate()) {
				closeWindow.clicked(requireActivity());
			}
		}
	};


	private FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
		@Override
		public void onFragmentAttached(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f, @NonNull @NotNull Context context) {
			super.onFragmentAttached(fm, f, context);
			if (f instanceof MapHeaderSearchFragment) {
				if (selectedLocationDtoInEvent != null) {
					binding.bottomNavigation.setVisibility(View.GONE);
					chipsLayout.setVisibility(View.GONE);
				}
			}
		}


		@Override
		public void onFragmentDestroyed(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f) {
			super.onFragmentDestroyed(fm, f);
			if (f instanceof MapHeaderSearchFragment) {
				if (selectedLocationDtoInEvent != null) {
					binding.bottomNavigation.setVisibility(View.VISIBLE);
				}
			} else if (f instanceof EventFragment) {
				if (((EventFragment) f).editing) {
					return;
				}
				if (!networkStatus.networkAvailable()) {
					getParentFragmentManager().popBackStackImmediate();
					return;
				}

				if (selectedLocationDtoInEvent == null) {
					getParentFragmentManager().popBackStackImmediate();
				}
			} else if (f instanceof ModifyInstanceFragment) {
				if (!((ModifyInstanceFragment) f).edited) {
					onClickedOpenEventFragmentBtn();
				}
			} else if (f instanceof SelectionDetailLocationFragment) {
				if (!((SelectionDetailLocationFragment) f).edited) {
					onClickedOpenEventFragmentBtn();
				}
			}
		}

	};

	public NewInstanceMainFragment() {
	}


	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
	}


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getChildFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, false);

		Bundle arguments = getArguments();
		eventId = arguments.getLong(CalendarContract.Instances.EVENT_ID);
		instanceId = arguments.getLong(CalendarContract.Instances._ID);
		calendarId = arguments.getInt(CalendarContract.Instances.CALENDAR_ID);
		originalBegin = arguments.getLong(CalendarContract.Instances.BEGIN);
		originalEnd = arguments.getLong(CalendarContract.Instances.END);

		calendarViewModel = new ViewModelProvider(requireActivity()).get(CalendarViewModel.class);

		eventValues = calendarViewModel.getInstance(instanceId, originalBegin, originalEnd);

		networkStatus = new NetworkStatus(getContext(), new ConnectivityManager.NetworkCallback() {
			@Override
			public void onAvailable(@NonNull Network network) {
				super.onAvailable(network);
			}

			@Override
			public void onLost(@NonNull Network network) {
				super.onLost(network);
				if (getActivity() != null) {
					FragmentManager fragmentManager = getChildFragmentManager();

					if (fragmentManager.findFragmentByTag(getString(R.string.tag_modify_instance_fragment)) != null) {
						return;
					}
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							networkStatus.showToastDisconnected();
							//모든 프래그먼트 종료
							if (fragmentManager.getBackStackEntryCount() > 0) {
								while (fragmentManager.popBackStackImmediate()) {
								}
							}

							EventFragment eventFragment = null;
							if (fragmentManager.findFragmentByTag(getString(R.string.tag_event_fragment)) != null) {
								eventFragment = (EventFragment) fragmentManager.findFragmentByTag(getString(R.string.tag_event_fragment));
							}

							if (eventFragment == null) {
								onClickedOpenEventFragmentBtn();
							} else {

							}
						}

					});
				}
			}
		});
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		binding.bottomNavigation.inflateMenu(R.menu.bottomnav_menu_in_event_info_fragment);
		binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
				//이벤트 정보, 날씨, 음식점
				onClickedBottomNav();

				switch (item.getItemId()) {
					case R.id.event_info:
						onClickedOpenEventFragmentBtn();
						break;
					case R.id.weathers_info:
						WeatherMainFragment weatherMainFragment = new WeatherMainFragment(new DialogInterface() {
							@Override
							public void cancel() {

							}

							@Override
							public void dismiss() {
							}
						}, DEFAULT_HEIGHT_OF_BOTTOMSHEET, calendarId,
								eventId);
						weatherMainFragment.show(getChildFragmentManager(), WeatherMainFragment.TAG);
						break;
					case R.id.map_around:
						onClickedAroundMap();
						break;
					case R.id.restaurants:
						openRestaurantFragment(eventId);
						break;
				}
				return true;
			}

		});
		removeTooltipInBottomNav();
		binding.bottomNavigation.setSelected(false);

		binding.headerLayout.setVisibility(View.GONE);
		binding.naverMapButtonsLayout.getRoot().setVisibility(View.GONE);
		gpsButton.setLongClickable(true);
		gpsButton.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (selectedLocationInEventMarker != null) {
					CameraUpdate cameraUpdate = CameraUpdate.scrollTo(selectedLocationInEventMarker.getPosition());
					cameraUpdate.animate(CameraAnimation.Easing, 200);
					naverMap.moveCamera(cameraUpdate);
				}
				return true;
			}
		});

		binding.naverMapFragmentRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				binding.naverMapFragmentRootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

				if (networkStatus.networkAvailable()) {
					if (eventValues.getAsString(CalendarContract.Events.EVENT_LOCATION) != null) {
						locationViewModel.getLocation(eventId, new DbQueryCallback<LocationDTO>() {
							@Override
							public void onResultSuccessful(LocationDTO savedLocationDto) {
								requireActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										//지도 로드
										binding.headerLayout.setVisibility(View.VISIBLE);
										binding.naverMapButtonsLayout.getRoot().setVisibility(View.VISIBLE);
										selectedLocationDtoInEvent = savedLocationDto;

										loadMap();
									}
								});
							}

							@Override
							public void onResultNoData() {
								requireActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										//인스턴스 정보 프래그먼트 표시
										onClickedOpenEventFragmentBtn();
									}
								});
							}
						});
					} else {
						//인스턴스 정보 프래그먼트 표시
						locationViewModel.removeLocation(eventId, null);
						onClickedOpenEventFragmentBtn();
					}
				} else {
					onClickedOpenEventFragmentBtn();
				}

			}
		});

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		onBackPressedCallback.remove();
		networkStatus.unregisterNetworkCallback();
		getChildFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
	}

	@Override
	public void onMapReady(@NonNull NaverMap naverMap) {
		super.onMapReady(naverMap);
		createSelectedLocationMarker();
	}

	private final EventFragment.OnEventEditCallback onEventEditCallback = new EventFragment.OnEventEditCallback() {
		@Override
		public void onResult() {
			locationViewModel.getLocation(eventId, new DbQueryCallback<LocationDTO>() {
				@Override
				public void onResultSuccessful(LocationDTO savedLocationDto) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							refreshView();
							selectedLocationDtoInEvent = savedLocationDto;

							binding.headerLayout.setVisibility(View.VISIBLE);
							binding.naverMapButtonsLayout.getRoot().setVisibility(View.VISIBLE);

							loadMap();
						}
					});

				}

				@Override
				public void onResultNoData() {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							refreshView();
							getParentFragmentManager().popBackStackImmediate();
						}
					});
				}
			});

		}

		@Override
		public void onProcess(ProcessType processType) {
			EventFragment eventFragment = (EventFragment) getChildFragmentManager().findFragmentByTag(getString(R.string.tag_event_fragment));

			if (processType == ProcessType.DETAIL_LOCATION) {
				locationViewModel.hasDetailLocation(eventId, new DbQueryCallback<Boolean>() {
					@Override
					public void onResultSuccessful(Boolean hasDetailLocation) {
						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (hasDetailLocation) {
									eventFragment.dismissForEdit();
									locationViewModel.getLocation(eventId, new DbQueryCallback<LocationDTO>() {
										@Override
										public void onResultSuccessful(LocationDTO locationResultDto) {
											if (!locationResultDto.isEmpty()) {
												SelectionDetailLocationFragment selectionDetailLocationFragment = new SelectionDetailLocationFragment(new SelectionDetailLocationFragment.OnDetailLocationSelectionResultListener() {
													@Override
													public void onResultChangedLocation(LocationDTO newLocation) {
														changedDetailLocation(newLocation);
													}

													@Override
													public void onResultSelectedLocation(LocationDTO newLocation) {

													}

													@Override
													public void onResultUnselectedLocation() {
														deletedDetailLocation();
													}
												});

												Bundle bundle = new Bundle();
												bundle.putParcelable(DetailLocationSelectorKey.SELECTED_LOCATION_DTO_IN_EVENT.value(), locationResultDto);
												bundle.putInt("requestCode", LocationIntentCode.REQUEST_CODE_CHANGE_LOCATION.value());

												selectionDetailLocationFragment.setArguments(bundle);
												getChildFragmentManager().beginTransaction().add(binding.fragmentContainer.getId(),
														selectionDetailLocationFragment,
														getString(R.string.tag_detail_location_selection_fragment))
														.addToBackStack(getString(R.string.tag_detail_location_selection_fragment)).commit();
											}
										}

										@Override
										public void onResultNoData() {

										}
									});
								} else {
									showSetLocationDialog(eventFragment);
								}
							}
						});
					}

					@Override
					public void onResultNoData() {

					}
				});

			} else if (processType == ProcessType.REMOVE_EVENT) {
				EditEventPopupMenu eventPopupMenu = new EditEventPopupMenu() {
					@Override
					public void onClickedEditEvent(Fragment modificationFragment) {
					}
				};

				eventPopupMenu.showRemoveDialog(requireActivity(), eventValues
						, new EditEventPopupMenu.OnEditedEventCallback() {
							@Override
							public void onRemoved() {
								eventFragment.editing = true;
								getParentFragmentManager().popBackStackImmediate();
							}
						}, calendarViewModel);
			} else if (processType == ProcessType.MODIFY_EVENT) {
				ModifyInstanceFragment modifyInstanceFragment = new ModifyInstanceFragment(new OnEditEventResultListener() {
					@Override
					public void onSavedNewEvent(long dtStart) {
						getParentFragmentManager().popBackStackImmediate();
					}

					@Override
					public void onUpdatedOnlyThisEvent(long dtStart) {
						getParentFragmentManager().popBackStackImmediate();
					}

					@Override
					public void onUpdatedFollowingEvents(long dtStart) {
						getParentFragmentManager().popBackStackImmediate();
					}

					@Override
					public void onUpdatedAllEvents(long dtStart) {
						getParentFragmentManager().popBackStackImmediate();
					}

					@Override
					public void onRemovedAllEvents() {

					}

					@Override
					public void onRemovedFollowingEvents() {

					}

					@Override
					public void onRemovedOnlyThisEvents() {

					}

					@Override
					public int describeContents() {
						return 0;
					}

					@Override
					public void writeToParcel(Parcel dest, int flags) {

					}
				});
				Bundle bundle = new Bundle();

				bundle.putLong(CalendarContract.Instances.EVENT_ID, eventId);
				bundle.putLong(CalendarContract.Instances._ID, instanceId);
				bundle.putLong(CalendarContract.Instances.BEGIN, originalBegin);
				bundle.putLong(CalendarContract.Instances.END, originalEnd);

				modifyInstanceFragment.setArguments(bundle);
				getChildFragmentManager().beginTransaction().add(binding.fragmentContainer.getId(), modifyInstanceFragment,
						getString(R.string.tag_modify_instance_fragment)).addToBackStack(getString(R.string.tag_modify_instance_fragment)).commit();
			}
		}
	};

	private void onClickedOpenEventFragmentBtn() {
		EventFragment eventFragment = new EventFragment(onEventEditCallback, DEFAULT_HEIGHT_OF_BOTTOMSHEET);

		Bundle bundle = new Bundle();
		bundle.putLong(CalendarContract.Instances._ID, instanceId);
		bundle.putLong(CalendarContract.Instances.EVENT_ID, eventId);
		bundle.putLong(CalendarContract.Instances.BEGIN, originalBegin);
		bundle.putLong(CalendarContract.Instances.END, originalEnd);

		eventFragment.setArguments(bundle);
		eventFragment.show(getChildFragmentManager(), getString(R.string.tag_event_fragment));
	}


	public void showSetLocationDialog(EventFragment eventFragment) {
		MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity())
				.setTitle(getString(R.string.request_select_location_title))
				.setMessage(getString(R.string.request_select_location_description))
				.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.cancel();
					}
				})
				.setPositiveButton(getString(R.string.check), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						Bundle bundle = new Bundle();
						bundle.putString(DetailLocationSelectorKey.LOCATION_NAME_IN_EVENT.value(),
								eventValues.getAsString(CalendarContract.Instances.EVENT_LOCATION));
						bundle.putInt("requestCode", LocationIntentCode.REQUEST_CODE_SELECT_LOCATION_BY_QUERY.value());

						SelectionDetailLocationFragment selectionDetailLocationFragment = new SelectionDetailLocationFragment(new SelectionDetailLocationFragment.OnDetailLocationSelectionResultListener() {
							@Override
							public void onResultChangedLocation(LocationDTO newLocation) {

							}

							@Override
							public void onResultSelectedLocation(LocationDTO newLocation) {
								saveDetailLocation(newLocation);
							}

							@Override
							public void onResultUnselectedLocation() {

							}
						});

						selectionDetailLocationFragment.setArguments(bundle);
						getChildFragmentManager().beginTransaction().add(binding.fragmentContainer.getId(), selectionDetailLocationFragment,
								getString(R.string.tag_detail_location_selection_fragment)).addToBackStack(getString(R.string.tag_detail_location_selection_fragment)).commit();
						dialogInterface.dismiss();
						eventFragment.dismissForEdit();
					}
				});

		AlertDialog dialog = builder.create();
		dialog.show();
	}


	private void saveDetailLocation(LocationDTO locationDTO) {
		// 지정이 완료된 경우 - DB에 등록하고 이벤트 액티비티로 넘어가서 날씨 또는 주변 정보 프래그먼트를 실행한다.
		locationDTO.setEventId(eventId);

		//선택된 위치를 DB에 등록
		locationViewModel.addLocation(locationDTO, new DbQueryCallback<LocationDTO>() {
			@Override
			public void onResultSuccessful(LocationDTO result) {
				onEventEditCallback.onResult();
			}

			@Override
			public void onResultNoData() {

			}
		});
	}

	private void changedDetailLocation(LocationDTO locationDTO) {
		// 지정이 완료된 경우 - DB에 등록하고 이벤트 액티비티로 넘어가서 날씨 또는 주변 정보 프래그먼트를 실행한다.
		// 선택된 위치를 DB에 등록
		locationDTO.setEventId(eventId);
		locationViewModel.removeLocation(eventId, new DbQueryCallback<Boolean>() {
			@Override
			public void onResultSuccessful(Boolean result) {
				locationViewModel.addLocation(locationDTO, new DbQueryCallback<LocationDTO>() {
					@Override
					public void onResultSuccessful(LocationDTO result) {
						onEventEditCallback.onResult();
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

	private void deletedDetailLocation() {
		locationViewModel.removeLocation(eventId, new DbQueryCallback<Boolean>() {
			@Override
			public void onResultSuccessful(Boolean result) {
				onEventEditCallback.onResult();
			}

			@Override
			public void onResultNoData() {

			}
		});
	}

	private void createSelectedLocationMarker() {
		LatLng latLng = new LatLng(Double.parseDouble(selectedLocationDtoInEvent.getLatitude()),
				Double.parseDouble(selectedLocationDtoInEvent.getLongitude()));

		final int markerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42f, getResources().getDisplayMetrics());
		final int markerHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42f, getResources().getDisplayMetrics());

		if (selectedLocationInEventMarker != null) {
			selectedLocationInEventMarker.setMap(null);
		}

		selectedLocationInEventMarker = new Marker(latLng);
		selectedLocationInEventMarker.setMap(naverMap);
		selectedLocationInEventMarker.setWidth(markerWidth);
		selectedLocationInEventMarker.setHeight(markerHeight);
		selectedLocationInEventMarker.setIcon(OverlayImage.fromResource(R.drawable.current_location_icon));
		selectedLocationInEventMarker.setForceShowIcon(true);
		selectedLocationInEventMarker.setCaptionColor(Color.BLUE);
		selectedLocationInEventMarker.setCaptionHaloColor(Color.rgb(200, 255, 200));
		selectedLocationInEventMarker.setCaptionTextSize(12f);
		selectedLocationInEventMarker.setOnClickListener(new Overlay.OnClickListener() {
			@Override
			public boolean onClick(@NonNull Overlay overlay) {
				if (selectedLocationInEventInfoWindow.getMarker() == null) {
					selectedLocationInEventInfoWindow.open(selectedLocationInEventMarker);
					selectedLocationInEventMarker.setCaptionText(getString(R.string.message_click_marker_to_delete));
				} else {
					selectedLocationInEventInfoWindow.close();
					selectedLocationInEventMarker.setCaptionText("");
				}
				return true;
			}
		});

		selectedLocationInEventInfoWindow = new InfoWindow();
		selectedLocationInEventInfoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getContext()) {
			@NonNull
			@Override
			public CharSequence getText(@NonNull InfoWindow infoWindow) {
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(getString(R.string.selected_location_in_event));
				stringBuilder.append("\n");
				if (selectedLocationDtoInEvent.getLocationType() == LocationType.PLACE) {
					stringBuilder.append(getString(R.string.place));
					stringBuilder.append(" : ");
					stringBuilder.append(selectedLocationDtoInEvent.getPlaceName());
					stringBuilder.append("\n");
					stringBuilder.append(getString(R.string.address));
					stringBuilder.append(" : ");
					stringBuilder.append(selectedLocationDtoInEvent.getAddressName());
				} else {
					stringBuilder.append(getString(R.string.address));
					stringBuilder.append(" : ");
					stringBuilder.append(selectedLocationDtoInEvent.getAddressName());
				}
				return stringBuilder.toString();
			}
		});

		selectedLocationInEventMarker.performClick();
		CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(latLng, 13);
		naverMap.moveCamera(cameraUpdate);
	}

	@Override
	public void onPageSelectedLocationItemBottomSheetViewPager(int position, MarkerType markerType) {
		super.onPageSelectedLocationItemBottomSheetViewPager(position, markerType);

		switch (markerType) {

		}
	}


	@Override
	public void refreshView() {
		removeAllMarkers();
		selectedLocationDtoInEvent = null;

		if (selectedLocationInEventMarker != null) {
			selectedLocationInEventMarker.setMap(null);
			selectedLocationInEventMarker = null;
		}
	}

}