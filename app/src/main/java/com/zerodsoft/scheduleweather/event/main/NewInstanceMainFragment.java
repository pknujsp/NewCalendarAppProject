package com.zerodsoft.scheduleweather.event.main;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcel;
import android.provider.CalendarContract;
import android.util.ArrayMap;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.activity.ModifyInstanceFragment;
import com.zerodsoft.scheduleweather.activity.editevent.interfaces.OnEditEventResultListener;
import com.zerodsoft.scheduleweather.activity.placecategory.PlaceCategorySettingsFragment;
import com.zerodsoft.scheduleweather.activity.placecategory.viewmodel.PlaceCategoryViewModel;
import com.zerodsoft.scheduleweather.calendar.AsyncQueryService;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendar.EventHelper;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IRefreshView;
import com.zerodsoft.scheduleweather.common.classes.CloseWindow;
import com.zerodsoft.scheduleweather.common.enums.LocationIntentCode;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.common.interfaces.OnHiddenFragmentListener;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.event.common.DetailLocationSelectorKey;
import com.zerodsoft.scheduleweather.event.common.SelectionDetailLocationFragment;
import com.zerodsoft.scheduleweather.event.event.fragments.EventFragment;
import com.zerodsoft.scheduleweather.event.foods.RestaurantFragment;
import com.zerodsoft.scheduleweather.event.foods.interfaces.ISetFoodMenuPoiItems;
import com.zerodsoft.scheduleweather.event.foods.interfaces.RestaurantListListener;
import com.zerodsoft.scheduleweather.event.places.interfaces.OnClickedPlacesListListener;
import com.zerodsoft.scheduleweather.event.places.interfaces.PlaceItemsGetter;
import com.zerodsoft.scheduleweather.event.places.PlacesOfSelectedCategoriesFragment;
import com.zerodsoft.scheduleweather.event.weather.fragment.WeatherMainFragment;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.NaverMapFragment;
import com.zerodsoft.scheduleweather.navermap.MarkerType;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnExtraListDataListener;
import com.zerodsoft.scheduleweather.navermap.searchheader.MapHeaderSearchFragment;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NewInstanceMainFragment extends NaverMapFragment implements ISetFoodMenuPoiItems,
		PlacesOfSelectedCategoriesFragment.PlaceCategoryChipsViewController
		, IRefreshView, RestaurantListListener {
	private int calendarId;
	private long eventId;
	private long instanceId;
	private long originalBegin;
	private long originalEnd;

	private CalendarViewModel calendarViewModel;
	private PlaceCategoryViewModel placeCategoryViewModel;

	private ContentValues eventValues;
	private LocationDTO selectedLocationDtoInEvent;
	private Button[] functionButtons;
	private TextView functionButton;
	private Marker selectedLocationInEventMarker;
	private InfoWindow selectedLocationInEventInfoWindow;

	private RestaurantsGetter restaurantItemGetter;
	private OnExtraListDataListener<Integer> restaurantOnExtraListDataListener;
	private PlaceItemsGetter placeItemsGetter;

	private ChipGroup placeCategoryChipGroup;
	private ArrayMap<String, Chip> placeCategoryChipMap = new ArrayMap<>();
	private Chip placeCategoryListChip;
	private Chip placeCategorySettingsChip;
	private String selectedPlaceCategoryCode;
	private OnExtraListDataListener<String> placeCategoryOnExtraListDataListener;
	private Set<PlaceCategoryDTO> savedPlaceCategorySet = new HashSet<>();

	private AlertDialog functionDialog;

	private LinearLayout chipsLayout;
	private ViewGroup functionItemsView;
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
			if (f instanceof RestaurantFragment) {
				binding.headerLayout.setVisibility(View.GONE);
				binding.naverMapButtonsLayout.buildingButton.setVisibility(View.GONE);
				binding.naverMapButtonsLayout.favoriteLocationsButton.setVisibility(View.GONE);
				functionButton.setVisibility(View.GONE);
			} else if (f instanceof MapHeaderSearchFragment) {
				if (selectedLocationDtoInEvent != null) {
					functionButton.setVisibility(View.GONE);
					chipsLayout.setVisibility(View.GONE);
				}
			}

			if (f instanceof RestaurantFragment || f instanceof MapHeaderSearchFragment) {
				if (placeCategoryChipGroup != null) {
					if (placeCategoryChipGroup.getCheckedChipIds().size() > 0) {
						placeCategoryChipGroup.clearCheck();
					}
				}
			}

		}


		@Override
		public void onFragmentDestroyed(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f) {
			super.onFragmentDestroyed(fm, f);
			if (f instanceof PlaceCategorySettingsFragment) {
				//place category가 변경된 경우 갱신
				placeCategoryViewModel.selectConvertedSelected(new DbQueryCallback<List<PlaceCategoryDTO>>() {
					@Override
					public void onResultSuccessful(List<PlaceCategoryDTO> newPlaceCategoryList) {
						Set<PlaceCategoryDTO> newSet = new HashSet<>();
						newSet.addAll(newPlaceCategoryList);

						Set<PlaceCategoryDTO> removedSet = new HashSet<>(savedPlaceCategorySet);
						Set<PlaceCategoryDTO> addedSet = new HashSet<>(newSet);

						removedSet.removeAll(newSet);
						addedSet.removeAll(savedPlaceCategorySet);

						if (!removedSet.isEmpty() || !addedSet.isEmpty()) {
							savedPlaceCategorySet = newSet;

							requireActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									//chips 재 생성
									placeCategoryChipMap.clear();
									placeCategoryChipGroup.removeViews(2, placeCategoryChipGroup.getChildCount() - 2);
									setPlaceCategoryChips(newPlaceCategoryList);
								}
							});
						}

					}

					@Override
					public void onResultNoData() {
					}
				});
			} else if (f instanceof RestaurantFragment) {
				binding.headerLayout.setVisibility(View.VISIBLE);
				binding.naverMapButtonsLayout.buildingButton.setVisibility(View.VISIBLE);
				binding.naverMapButtonsLayout.favoriteLocationsButton.setVisibility(View.VISIBLE);
				functionButton.setVisibility(View.VISIBLE);
			} else if (f instanceof MapHeaderSearchFragment) {
				if (selectedLocationDtoInEvent != null) {
					functionButton.setVisibility(View.VISIBLE);
					chipsLayout.setVisibility(View.VISIBLE);
				}
			} else if (f instanceof EventFragment) {
				if (selectedLocationDtoInEvent == null) {
					if (!((EventFragment) f).editing) {
						getParentFragmentManager().popBackStackImmediate();
					}
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

		Bundle arguments = getArguments();
		eventId = arguments.getLong(CalendarContract.Instances.EVENT_ID);
		instanceId = arguments.getLong(CalendarContract.Instances._ID);
		calendarId = arguments.getInt(CalendarContract.Instances.CALENDAR_ID);
		originalBegin = arguments.getLong(CalendarContract.Instances.BEGIN);
		originalEnd = arguments.getLong(CalendarContract.Instances.END);

		calendarViewModel = new ViewModelProvider(requireActivity()).get(CalendarViewModel.class);
		placeCategoryViewModel = new ViewModelProvider(this).get(PlaceCategoryViewModel.class);
		getChildFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, false);

		eventValues = calendarViewModel.getEvent(eventId);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

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

				final int headerbarHeight = (int) getResources().getDimension(R.dimen.map_header_bar_height);
				final int headerbarTopMargin = (int) getResources().getDimension(R.dimen.map_header_bar_top_margin);
				final int headerbarMargin = (int) (headerbarTopMargin * 1.5f);

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

									createFunctionList();
									loadMap();
									addPlaceCategoryListFragmentIntoBottomSheet();
									createPlaceCategoryListChips();
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
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		onBackPressedCallback.remove();
		getChildFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
	}

	private void setHeightOfBottomSheet(int height, LinearLayout bottomSheetView, BottomSheetBehavior bottomSheetBehavior) {
		bottomSheetView.getLayoutParams().height = height;
		bottomSheetView.requestLayout();
		bottomSheetBehavior.onLayoutChild(binding.naverMapFragmentRootLayout, bottomSheetView, ViewCompat.LAYOUT_DIRECTION_LTR);
	}

	@Override
	public void onMapReady(@NonNull NaverMap naverMap) {
		super.onMapReady(naverMap);
		createSelectedLocationMarker();
	}

	private void createFunctionList() {
		//이벤트 정보, 날씨, 음식점
		functionButton = new TextView(getContext());
		functionButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.map_button_rect));
		functionButton.setElevation(4f);
		functionButton.setClickable(true);
		functionButton.setText(R.string.show_functions);
		functionButton.setTextColor(Color.BLACK);
		functionButton.setId(R.id.function_btn_in_instance_main_fragment);

		final int btnPaddingLR = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, getResources().getDisplayMetrics());
		final int btnPaddingTB = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics());
		functionButton.setPadding(btnPaddingLR, btnPaddingTB, btnPaddingLR, btnPaddingTB);

		ConstraintLayout.LayoutParams functionBtnLayoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		functionBtnLayoutParams.bottomToTop = binding.naverMapButtonsLayout.currentAddress.getId();
		functionBtnLayoutParams.leftToLeft = binding.naverMapButtonsLayout.getRoot().getId();

		final int marginBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, getResources().getDisplayMetrics());
		functionBtnLayoutParams.bottomMargin = marginBottom;

		binding.naverMapButtonsLayout.getRoot().addView(functionButton, functionBtnLayoutParams);
		functionButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if (getStateOfBottomSheet(BottomSheetType.LOCATION_ITEM) == BottomSheetBehavior.STATE_EXPANDED) {
					setStateOfBottomSheet(BottomSheetType.LOCATION_ITEM, BottomSheetBehavior.STATE_COLLAPSED);
				}
				functionDialog.show();
			}
		});

		//기능 세부 버튼
		functionItemsView = (ViewGroup) getLayoutInflater().inflate(R.layout.event_function_items_view,
				null);

		functionButtons = new Button[]{functionItemsView.findViewById(R.id.function_event_info)
				, functionItemsView.findViewById(R.id.function_weather)
				, functionItemsView.findViewById(R.id.function_restaurant)};

		functionDialog = new MaterialAlertDialogBuilder(requireActivity())
				.setView(functionItemsView)
				.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.transparent_background))
				.create();

		functionButtons[0].setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				functionDialog.dismiss();
				onClickedOpenEventFragmentBtn();
			}
		});

		functionButtons[1].setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//날씨
				functionDialog.dismiss();

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
			}
		});

		functionButtons[2].setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//음식점
				functionDialog.dismiss();
				FragmentManager fragmentManager = getChildFragmentManager();
				//restaurant
				Object[] results2 = createBottomSheet(R.id.restaurant_fragment_container);
				LinearLayout restaurantsBottomSheet = (LinearLayout) results2[0];
				BottomSheetBehavior restaurantsBottomSheetBehavior = (BottomSheetBehavior) results2[1];

				bottomSheetViewMap.put(BottomSheetType.RESTAURANT, restaurantsBottomSheet);
				bottomSheetBehaviorMap.put(BottomSheetType.RESTAURANT, restaurantsBottomSheetBehavior);

				RestaurantFragment restaurantFragment =
						new RestaurantFragment(NewInstanceMainFragment.this
								, new OnHiddenFragmentListener() {
							@Override
							public void onHiddenChangedFragment(boolean hidden) {

							}
						}, eventId);

				bottomSheetFragmentMap.put(BottomSheetType.RESTAURANT, restaurantFragment);
				fragmentManager.beginTransaction().add(binding.fragmentContainer.getId(), restaurantFragment
						, getString(R.string.tag_restaurant_fragment)).addToBackStack(getString(R.string.tag_restaurant_fragment)).commit();
			}
		});
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

							if (functionButton != null) {
								binding.naverMapButtonsLayout.getRoot().removeView(functionButton);
								functionButtons = null;
							}
							if (bottomSheetFragmentMap.containsKey(BottomSheetType.SELECTED_PLACE_CATEGORY)) {
								binding.naverMapFragmentRootLayout.removeView(bottomSheetViewMap.get(BottomSheetType.SELECTED_PLACE_CATEGORY));
								getChildFragmentManager().beginTransaction()
										.remove(getChildFragmentManager().findFragmentByTag(getString(R.string.tag_places_of_selected_categories_fragment)))
										.commit();
								bottomSheetFragmentMap.remove(BottomSheetType.SELECTED_PLACE_CATEGORY);
								bottomSheetBehaviorMap.remove(BottomSheetType.SELECTED_PLACE_CATEGORY);
								bottomSheetViewMap.remove(BottomSheetType.SELECTED_PLACE_CATEGORY);
							}
							if (chipsLayout != null) {
								chipsLayout.removeAllViews();
								binding.headerLayout.removeView(chipsLayout);
								chipsLayout = null;
							}
							createFunctionList();
							loadMap();
							addPlaceCategoryListFragmentIntoBottomSheet();
							createPlaceCategoryListChips();
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
				String[] items = null;
				//이번 일정만 삭제, 향후 모든 일정 삭제, 모든 일정 삭제
                /*
                반복없는 이벤트 인 경우 : 일정 삭제
                반복있는 이벤트 인 경우 : 이번 일정만 삭제, 향후 모든 일정 삭제, 모든 일정 삭제
                 */
				if (eventValues.getAsString(CalendarContract.Instances.RRULE) != null) {
					items = new String[]{getString(R.string.remove_this_instance), getString(R.string.remove_all_future_instance_including_current_instance)
							, getString(R.string.remove_event)};
				} else {
					items = new String[]{getString(R.string.remove_event)};
				}
				new MaterialAlertDialogBuilder(getActivity()).setTitle(getString(R.string.remove_event))
						.setItems(items, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int index) {
								if (eventValues.getAsString(CalendarContract.Instances.RRULE) != null) {
									switch (index) {
										case 0:
											// 이번 일정만 삭제
											// 완성
											showRemoveOnlyThisEventDialog(eventFragment);
											break;
										case 1:
											// 향후 모든 일정만 삭제
											removeFollowingEvents(eventFragment);
											break;
										case 2:
											// 모든 일정 삭제
											showRemoveAllEventsDialog(eventFragment);
											break;
									}
								} else {
									switch (index) {
										case 0:
											// 모든 일정 삭제
											showRemoveAllEventsDialog(eventFragment);
											break;
									}
								}
							}
						}).create().show();
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

	private void showRemoveAllEventsDialog(EventFragment eventFragment) {
		new MaterialAlertDialogBuilder(requireActivity())
				.setTitle(R.string.remove_event)
				.setPositiveButton(R.string.check, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EventHelper eventHelper = new EventHelper(new AsyncQueryService(getContext(), calendarViewModel));
						eventHelper.removeEvent(EventHelper.EventEditType.REMOVE_ALL_EVENTS, eventValues);
						dialog.dismiss();
						getParentFragmentManager().popBackStackImmediate();
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
	}


	private void removeFollowingEvents(EventFragment eventFragment) {
		EventHelper eventHelper = new EventHelper(new AsyncQueryService(getContext(), calendarViewModel));
		eventHelper.removeEvent(EventHelper.EventEditType.REMOVE_FOLLOWING_EVENTS, eventValues);
		getParentFragmentManager().popBackStackImmediate();
	}

	private void showRemoveOnlyThisEventDialog(EventFragment eventFragment) {
		new MaterialAlertDialogBuilder(requireActivity())
				.setTitle(R.string.remove_this_instance)
				.setPositiveButton(R.string.check, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EventHelper eventHelper = new EventHelper(new AsyncQueryService(getContext(), calendarViewModel));
						eventHelper.removeEvent(EventHelper.EventEditType.REMOVE_ONLY_THIS_EVENT, eventValues);
						dialog.dismiss();
						getParentFragmentManager().popBackStackImmediate();
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
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


	public void createPlaceCategoryListChips() {
		chipsLayout = new LinearLayout(getContext());
		chipsLayout.setOrientation(LinearLayout.HORIZONTAL);

		LinearLayout.LayoutParams chipLayoutsParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		binding.headerLayout.addView(chipsLayout, chipLayoutsParams);

		//scrollview
		HorizontalScrollView chipScrollView = new HorizontalScrollView(getContext());
		chipScrollView.setHorizontalScrollBarEnabled(false);
		chipScrollView.setId(R.id.place_category_chips_scroll_layout);
		LinearLayout.LayoutParams chipLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
		chipLayoutParams.gravity = Gravity.CENTER_VERTICAL;
		chipLayoutParams.weight = 1;

		chipsLayout.addView(chipScrollView, chipLayoutParams);

		placeCategoryChipGroup = new ChipGroup(getContext(), null, R.style.Widget_MaterialComponents_ChipGroup);
		placeCategoryChipGroup.setSingleSelection(true);
		placeCategoryChipGroup.setSingleLine(true);
		placeCategoryChipGroup.setId(R.id.chip_group);
		placeCategoryChipGroup.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		chipScrollView.addView(placeCategoryChipGroup);
		addDefaultChips();

		placeCategoryViewModel.selectConvertedSelected(new DbQueryCallback<List<PlaceCategoryDTO>>() {
			@Override
			public void onResultSuccessful(List<PlaceCategoryDTO> savedPlaceCategoryList) {
				savedPlaceCategorySet.addAll(savedPlaceCategoryList);

				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setPlaceCategoryChips(savedPlaceCategoryList);
					}
				});
			}

			@Override
			public void onResultNoData() {
			}
		});
	}

	@Override
	public void addPlaceCategoryListFragmentIntoBottomSheet() {
		//place category
		Object[] placeCategoryResult = createBottomSheet(R.id.place_category_fragment_container);
		LinearLayout placeCategoryBottomSheet = (LinearLayout) placeCategoryResult[0];
		BottomSheetBehavior placeCategoryBottomSheetBehavior = (BottomSheetBehavior) placeCategoryResult[1];

		bottomSheetViewMap.put(BottomSheetType.SELECTED_PLACE_CATEGORY, placeCategoryBottomSheet);
		bottomSheetBehaviorMap.put(BottomSheetType.SELECTED_PLACE_CATEGORY, placeCategoryBottomSheetBehavior);

		setHeightOfBottomSheet(DEFAULT_HEIGHT_OF_BOTTOMSHEET, placeCategoryBottomSheet, placeCategoryBottomSheetBehavior);

		placeCategoryBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
			@Override
			public void onStateChanged(@NonNull View bottomSheet, int newState) {
				if (newState == BottomSheetBehavior.STATE_EXPANDED) {
					placeCategoryListChip.setText(R.string.close_place_category_list);
				} else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
					placeCategoryListChip.setText(R.string.open_place_category_list);
				}
			}

			@Override
			public void onSlide(@NonNull View bottomSheet, float slideOffset) {

			}
		});

		PlacesOfSelectedCategoriesFragment placesOfSelectedCategoriesFragment = new PlacesOfSelectedCategoriesFragment(eventId, new OnClickedPlacesListListener() {
			@Override
			public void onClickedItemInList(PlaceCategoryDTO placeCategory, PlaceDocuments placeDocument, int index) {
				getChildFragmentManager().popBackStackImmediate();
				placeCategoryChipMap.get(placeCategory.getCode()).setChecked(true);
				onPOIItemSelectedByList(placeDocument, MarkerType.SELECTED_PLACE_CATEGORY);
			}

			@Override
			public void onClickedMoreInList(PlaceCategoryDTO placeCategory) {
				getChildFragmentManager().popBackStackImmediate();
				placeCategoryChipMap.get(placeCategory.getCode()).setChecked(true);
			}

		}, new OnHiddenFragmentListener() {
			@Override
			public void onHiddenChangedFragment(boolean hidden) {
				if (hidden) {
					setStateOfBottomSheet(BottomSheetType.SELECTED_PLACE_CATEGORY, BottomSheetBehavior.STATE_COLLAPSED);
				} else {

				}
			}
		});
		bottomSheetFragmentMap.put(BottomSheetType.SELECTED_PLACE_CATEGORY, placesOfSelectedCategoriesFragment);
		placeCategoryOnExtraListDataListener = placesOfSelectedCategoriesFragment;
		placeItemsGetter = placesOfSelectedCategoriesFragment;

		getChildFragmentManager().beginTransaction()
				.add(placeCategoryBottomSheet.getChildAt(0).getId()
						, placesOfSelectedCategoriesFragment, getString(R.string.tag_places_of_selected_categories_fragment)).commit();
	}

	@Override
	public void setPlaceCategoryChips(List<PlaceCategoryDTO> placeCategoryList) {
		//카테고리를 chip으로 표시
		int index = 0;

		for (PlaceCategoryDTO placeCategory : placeCategoryList) {
			Chip chip = new Chip(getContext(), null, R.style.Widget_MaterialComponents_Chip_Filter);
			chip.setChecked(false);
			chip.setText(placeCategory.getDescription());
			chip.setClickable(true);
			chip.setCheckable(true);
			chip.setVisibility(View.VISIBLE);
			chip.setOnCheckedChangeListener(placeCategoryChipOnCheckedChangeListener);
			chip.setGravity(Gravity.CENTER);

			final PlaceCategoryChipViewHolder chipViewHolder = new PlaceCategoryChipViewHolder(placeCategory, index++);
			chip.setTag(chipViewHolder);

			placeCategoryChipMap.put(placeCategory.getCode(), chip);
			placeCategoryChipGroup.addView(chip, new ChipGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		}
	}

	@Override
	public void addDefaultChips() {
		//바텀시트를 확장/축소 하는 chip
		placeCategoryListChip = new Chip(getContext());
		placeCategoryListChip.setChecked(false);
		placeCategoryListChip.setText(R.string.open_place_category_list);
		placeCategoryListChip.setClickable(true);
		placeCategoryListChip.setCheckable(false);
		placeCategoryListChip.setTextColor(Color.BLUE);
		placeCategoryListChip.setOnClickListener(new View.OnClickListener() {
			boolean initializing = true;

			@Override
			public void onClick(View view) {
				FragmentManager fragmentManager = getChildFragmentManager();
				Fragment placesOfSelectedCategoriesFragment =
						bottomSheetFragmentMap.get(BottomSheetType.SELECTED_PLACE_CATEGORY);

				if (placesOfSelectedCategoriesFragment.isVisible()) {
					if (initializing) {
						initializing = false;
						fragmentManager.beginTransaction()
								.show(placesOfSelectedCategoriesFragment)
								.addToBackStack(getString(R.string.tag_places_of_selected_categories_fragment))
								.commit();

						setStateOfBottomSheet(BottomSheetType.SELECTED_PLACE_CATEGORY, BottomSheetBehavior.STATE_EXPANDED);
					} else {
						fragmentManager.popBackStackImmediate();
					}
				} else {
					fragmentManager.beginTransaction()
							.show(placesOfSelectedCategoriesFragment)
							.addToBackStack(getString(R.string.tag_places_of_selected_categories_fragment))
							.commit();

					setStateOfBottomSheet(BottomSheetType.SELECTED_PLACE_CATEGORY, BottomSheetBehavior.STATE_EXPANDED);
				}
			}
		});

		//place category를 설정하는 프래그먼트를 여는 chip
		placeCategorySettingsChip = new Chip(getContext());
		placeCategorySettingsChip.setChecked(false);
		placeCategorySettingsChip.setText(R.string.app_settings);
		placeCategorySettingsChip.setClickable(true);
		placeCategorySettingsChip.setCheckable(false);
		placeCategorySettingsChip.setTextColor(Color.BLUE);
		placeCategorySettingsChip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				PlaceCategorySettingsFragment placeCategorySettingsFragment = new PlaceCategorySettingsFragment();
				getChildFragmentManager().beginTransaction().add(binding.fragmentContainer.getId(), placeCategorySettingsFragment,
						getString(R.string.tag_place_category_settings_fragment)).addToBackStack(getString(R.string.tag_place_category_settings_fragment)).commit();
			}
		});

		placeCategoryChipGroup.addView(placeCategoryListChip, 0);
		placeCategoryChipGroup.addView(placeCategorySettingsChip, 1);
	}


	private final CompoundButton.OnCheckedChangeListener placeCategoryChipOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            /*
           - chip이 이미 선택되어 있는 경우
           같은 chip인 경우 : 선택해제, poiitem모두 삭제하고 bottomsheet를 숨긴다
           다른 chip인 경우 : 새로운 chip이 선택되고 난 뒤에 기존 chip이 선택해제 된다
           poiitem이 선택된 경우 해제하고, poiitem을 새로 생성한 뒤 poiitem전체가 보이도록 설정
             */
			if (isChecked) {
				if (bottomSheetFragmentMap.get(BottomSheetType.SELECTED_PLACE_CATEGORY).isVisible()) {
					getChildFragmentManager().popBackStackImmediate();
				}
				selectedPlaceCategoryCode = ((PlaceCategoryChipViewHolder) compoundButton.getTag()).placeCategory.getCode();

				placeItemsGetter.getPlaces(new DbQueryCallback<List<PlaceDocuments>>() {
					@Override
					public void onResultSuccessful(List<PlaceDocuments> result) {
						createMarkers(result, MarkerType.SELECTED_PLACE_CATEGORY);
						showMarkers(MarkerType.SELECTED_PLACE_CATEGORY);
					}

					@Override
					public void onResultNoData() {
						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(getActivity(), getString(R.string.not_founded_search_result), Toast.LENGTH_SHORT).show();
							}
						});
					}
				}, selectedPlaceCategoryCode);
			} else if (placeCategoryChipGroup.getCheckedChipIds().isEmpty()
					&& !markersMap.get(MarkerType.SELECTED_PLACE_CATEGORY)
					.isEmpty()) {
				removeMarkers(MarkerType.SELECTED_PLACE_CATEGORY);
			}
			setStateOfBottomSheet(BottomSheetType.LOCATION_ITEM, BottomSheetBehavior.STATE_COLLAPSED);
		}
	};


	@Override
	public void createRestaurantPoiItems(RestaurantsGetter restaurantsGetter,
	                                     OnExtraListDataListener<Integer> onExtraListDataListener) {
		this.restaurantItemGetter = restaurantsGetter;
		this.restaurantOnExtraListDataListener = onExtraListDataListener;
	}

	@Override
	public void removeRestaurantPoiItems() {
		setStateOfBottomSheet(BottomSheetType.LOCATION_ITEM, BottomSheetBehavior.STATE_COLLAPSED);

		restaurantItemGetter = null;
		restaurantOnExtraListDataListener = null;

		viewPagerAdapterMap.remove(MarkerType.RESTAURANT);
		removeMarkers(MarkerType.RESTAURANT);
		removeMarkers(MarkerType.CRITERIA_LOCATION_FOR_RESTAURANTS);

		markersMap.get(MarkerType.CRITERIA_LOCATION_FOR_RESTAURANTS).clear();
	}

	@Override
	public void createCriteriaLocationMarker(String name, String latitude, String longitude) {
		LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
		final int markerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42f, getResources().getDisplayMetrics());
		final int markerHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42f, getResources().getDisplayMetrics());

		Marker criteriaLocationForRestaurantsMarker = new Marker(latLng);
		criteriaLocationForRestaurantsMarker.setMap(naverMap);
		criteriaLocationForRestaurantsMarker.setWidth(markerWidth);
		criteriaLocationForRestaurantsMarker.setHeight(markerHeight);
		criteriaLocationForRestaurantsMarker.setIcon(OverlayImage.fromResource(R.drawable.criteria_location_svg));
		criteriaLocationForRestaurantsMarker.setForceShowIcon(true);
		criteriaLocationForRestaurantsMarker.setCaptionColor(Color.BLACK);
		criteriaLocationForRestaurantsMarker.setCaptionTextSize(13f);
		criteriaLocationForRestaurantsMarker.setCaptionText(name);
		criteriaLocationForRestaurantsMarker.setSubCaptionText(getString(R.string.criteria_location));
		criteriaLocationForRestaurantsMarker.setSubCaptionTextSize(11f);
		criteriaLocationForRestaurantsMarker.setSubCaptionColor(Color.BLUE);

		if (!markersMap.containsKey(MarkerType.CRITERIA_LOCATION_FOR_RESTAURANTS)) {
			markersMap.put(MarkerType.CRITERIA_LOCATION_FOR_RESTAURANTS, new ArrayList<>());
		}
		markersMap.get(MarkerType.CRITERIA_LOCATION_FOR_RESTAURANTS).add(criteriaLocationForRestaurantsMarker);
	}

	@Override
	public void onChangeFoodMenu() {
		setStateOfBottomSheet(BottomSheetType.LOCATION_ITEM, BottomSheetBehavior.STATE_COLLAPSED);
	}


	@Override
	public void onPageSelectedLocationItemBottomSheetViewPager(int position, MarkerType markerType) {
		super.onPageSelectedLocationItemBottomSheetViewPager(position, markerType);

		switch (markerType) {
			case SELECTED_PLACE_CATEGORY: {
				placeCategoryOnExtraListDataListener.loadExtraListData(selectedPlaceCategoryCode, new RecyclerView.AdapterDataObserver() {
					@Override
					public void onItemRangeInserted(int positionStart, int itemCount) {
						placeItemsGetter.getPlaces(new DbQueryCallback<List<PlaceDocuments>>() {
							@Override
							public void onResultSuccessful(List<PlaceDocuments> placeDocuments) {
								addExtraMarkers(placeDocuments, markerType);
							}

							@Override
							public void onResultNoData() {

							}
						}, selectedPlaceCategoryCode);
					}
				});
				break;
			}

			case RESTAURANT: {
				restaurantOnExtraListDataListener.loadExtraListData(null, new RecyclerView.AdapterDataObserver() {
					@Override
					public void onItemRangeInserted(int positionStart, int itemCount) {
						restaurantItemGetter.getRestaurants(new DbQueryCallback<List<PlaceDocuments>>() {
							@Override
							public void onResultSuccessful(List<PlaceDocuments> placeDocuments) {
								addExtraMarkers(placeDocuments, markerType);
							}

							@Override
							public void onResultNoData() {

							}
						});
					}
				});
				break;
			}
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

	@Override
	public void onLoadedInitialRestaurantList(String query, List<PlaceDocuments> restaurantList) {
		if (restaurantList.size() == 0) {
			Toast.makeText(getActivity(), getString(R.string.not_founded_search_result), Toast.LENGTH_SHORT).show();
			removeMarkers(MarkerType.RESTAURANT);
		} else {
			createMarkers(restaurantList, MarkerType.RESTAURANT);
			showMarkers(MarkerType.RESTAURANT);
		}
	}

	@Override
	public void onLoadedExtraRestaurantList(String query, List<PlaceDocuments> restaurantList) {

	}


	static final class PlaceCategoryChipViewHolder {
		PlaceCategoryDTO placeCategory;
		int index;

		public PlaceCategoryChipViewHolder(PlaceCategoryDTO placeCategory, int index) {
			this.placeCategory = placeCategory;
			this.index = index;
		}
	}

	public interface RestaurantsGetter {
		void getRestaurants(DbQueryCallback<List<PlaceDocuments>> callback);
	}
}