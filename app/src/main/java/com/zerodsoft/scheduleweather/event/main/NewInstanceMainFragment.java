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
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.placecategory.PlaceCategorySettingsFragment;
import com.zerodsoft.scheduleweather.activity.placecategory.viewmodel.PlaceCategoryViewModel;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IRefreshView;
import com.zerodsoft.scheduleweather.common.classes.CloseWindow;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.common.interfaces.OnHiddenFragmentListener;
import com.zerodsoft.scheduleweather.etc.LocationType;
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
									//place category list fragment 갱신
									PlacesOfSelectedCategoriesFragment fragment =
											(PlacesOfSelectedCategoriesFragment) getChildFragmentManager().findFragmentByTag(getString(R.string.tag_places_of_selected_categories_fragment));
									fragment.refreshList();

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
		getChildFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);

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
							selectedLocationDtoInEvent = savedLocationDto;

							requireActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									//지도 로드
									binding.headerLayout.setVisibility(View.VISIBLE);
									binding.naverMapButtonsLayout.getRoot().setVisibility(View.VISIBLE);
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
					onClickedOpenEventFragmentBtn();
					locationViewModel.removeLocation(eventId, null);
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
		functionButton.setId(R.id.function_btn_in_instance_main_fragment);

		final int btnPaddingLR = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics());
		final int btnPaddingTB = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, getResources().getDisplayMetrics());
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
				Bundle bundle = new Bundle();
				LatLng latLng = naverMap.getContentBounds().getCenter();
				bundle.putString("latitude", String.valueOf(latLng.latitude));
				bundle.putString("longitude", String.valueOf(latLng.longitude));
				bundle.putBoolean("hasSimpleLocation", false);

				weatherMainFragment.setArguments(bundle);
				weatherMainFragment.show(getChildFragmentManager(), WeatherMainFragment.TAG);
			}
		});

		functionButtons[2].setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//음식점
				functionDialog.dismiss();

				FragmentManager fragmentManager = getChildFragmentManager();

				if (bottomSheetFragmentMap.containsKey(BottomSheetType.RESTAURANT)) {
					Fragment restaurantFragment = bottomSheetFragmentMap.get(BottomSheetType.RESTAURANT);

					if (restaurantFragment.isVisible()) {
						fragmentManager.popBackStackImmediate();
					} else {
						fragmentManager.beginTransaction().show(restaurantFragment).addToBackStack(getString(R.string.tag_restaurant_fragment)).commit();
					}
				} else {
					//restaurant
					Object[] results2 = createBottomSheet(R.id.restaurant_fragment_container);
					LinearLayout restaurantsBottomSheet = (LinearLayout) results2[0];
					BottomSheetBehavior restaurantsBottomSheetBehavior = (BottomSheetBehavior) results2[1];

					bottomSheetViewMap.put(BottomSheetType.RESTAURANT, restaurantsBottomSheet);
					bottomSheetBehaviorMap.put(BottomSheetType.RESTAURANT, restaurantsBottomSheetBehavior);

					RestaurantFragment restaurantFragment =
							new RestaurantFragment(NewInstanceMainFragment.this, NewInstanceMainFragment.this
									, new OnHiddenFragmentListener() {
								@Override
								public void onHiddenChangedFragment(boolean hidden) {
									if (hidden) {
										binding.headerLayout.setVisibility(View.VISIBLE);
										binding.naverMapButtonsLayout.buildingButton.setVisibility(View.VISIBLE);
										binding.naverMapButtonsLayout.favoriteLocationsButton.setVisibility(View.VISIBLE);
										functionButton.setVisibility(View.VISIBLE);
									} else {
										binding.headerLayout.setVisibility(View.GONE);
										binding.naverMapButtonsLayout.buildingButton.setVisibility(View.GONE);
										binding.naverMapButtonsLayout.favoriteLocationsButton.setVisibility(View.GONE);
										functionButton.setVisibility(View.GONE);
									}
								}
							}, calendarId, instanceId, eventId);
					bottomSheetFragmentMap.put(BottomSheetType.RESTAURANT, restaurantFragment);
					fragmentManager.beginTransaction().add(binding.fragmentContainer.getId(), restaurantFragment
							, getString(R.string.tag_restaurant_fragment)).commitNow();
					fragmentManager.beginTransaction().show(restaurantFragment).addToBackStack(getString(R.string.tag_restaurant_fragment)).commit();
				}

			}
		});
	}

	private void onClickedOpenEventFragmentBtn() {
		EventFragment eventFragment = new EventFragment(new EventFragment.OnEventFragmentDismissListener() {
			@Override
			public void onResult(long newEventId) {
				eventId = newEventId;
				eventValues = calendarViewModel.getEvent(eventId);

				if (eventValues.getAsString(CalendarContract.Events.EVENT_LOCATION) != null) {
					locationViewModel.getLocation(eventId, new DbQueryCallback<LocationDTO>() {
						@Override
						public void onResultSuccessful(LocationDTO savedLocationDto) {
							if (selectedLocationDtoInEvent != null) {
								if (selectedLocationDtoInEvent.getId() != savedLocationDto.getId()) {
									selectedLocationDtoInEvent = savedLocationDto;

									requireActivity().runOnUiThread(new Runnable() {
										@Override
										public void run() {
											createSelectedLocationMarker();
										}
									});
								}
							} else {
								selectedLocationDtoInEvent = savedLocationDto;
								requireActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										binding.headerLayout.setVisibility(View.VISIBLE);
										binding.naverMapButtonsLayout.getRoot().setVisibility(View.VISIBLE);

										createFunctionList();
										addPlaceCategoryListFragmentIntoBottomSheet();
										createPlaceCategoryListChips();
										loadMap();
									}
								});
							}

						}

						@Override
						public void onResultNoData() {
							requireActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									getParentFragmentManager().popBackStackImmediate();
								}
							});
						}
					});
				} else {
					getParentFragmentManager().popBackStackImmediate();
				}
			}
		}, NewInstanceMainFragment.this, DEFAULT_HEIGHT_OF_BOTTOMSHEET
		);

		Bundle bundle = new Bundle();
		bundle.putLong(CalendarContract.Instances._ID, instanceId);
		bundle.putLong(CalendarContract.Instances.EVENT_ID, eventId);
		bundle.putLong(CalendarContract.Instances.BEGIN, originalBegin);
		bundle.putLong(CalendarContract.Instances.END, originalEnd);

		eventFragment.setArguments(bundle);
		eventFragment.show(getChildFragmentManager(), getString(R.string.tag_event_fragment));
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
		chipLayoutsParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics());
		binding.headerLayout.addView(chipsLayout, chipLayoutsParams);

		//chip title
		TextView titleTextView = new TextView(getContext());
		titleTextView.setText(R.string.place);
		titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
		titleTextView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparent));

		LinearLayout.LayoutParams titleTextViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		titleTextViewLayoutParams.gravity = Gravity.CENTER_VERTICAL;
		int titleMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics());
		titleTextViewLayoutParams.leftMargin = titleMargin;
		titleTextViewLayoutParams.rightMargin = titleMargin;

		chipsLayout.addView(titleTextView, titleTextViewLayoutParams);

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
					placeCategoryListChip.setText(R.string.close_list);
				} else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
					placeCategoryListChip.setText(R.string.open_list);
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
		placeCategoryListChip.setText(R.string.open_list);
		placeCategoryListChip.setClickable(true);
		placeCategoryListChip.setCheckable(false);
		placeCategoryListChip.setTextColor(Color.BLUE);
		placeCategoryListChip.setOnClickListener(new View.OnClickListener() {
			boolean initializing = true;

			@Override
			public void onClick(View view) {
				FragmentManager fragmentManager = getChildFragmentManager();

				if (bottomSheetFragmentMap.get(BottomSheetType.SELECTED_PLACE_CATEGORY).isVisible()) {
					if (initializing) {
						initializing = false;
						fragmentManager.beginTransaction()
								.show(bottomSheetFragmentMap.get(BottomSheetType.SELECTED_PLACE_CATEGORY))
								.addToBackStack(getString(R.string.tag_places_of_selected_categories_fragment))
								.commit();

						setStateOfBottomSheet(BottomSheetType.SELECTED_PLACE_CATEGORY, BottomSheetBehavior.STATE_EXPANDED);
					} else {
						fragmentManager.popBackStackImmediate();
					}
				} else {
					Fragment placesOfSelectedCategoriesFragment =
							bottomSheetFragmentMap.get(BottomSheetType.SELECTED_PLACE_CATEGORY);

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

		/*
		restaurantItemGetter.getRestaurants(new DbQueryCallback<List<PlaceDocuments>>() {
			@Override
			public void onResultSuccessful(List<PlaceDocuments> placeDocuments) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (placeDocuments.size() == 0) {
							Toast.makeText(getActivity(), getString(R.string.not_founded_search_result), Toast.LENGTH_SHORT).show();
							removeMarkers(MarkerType.RESTAURANT);
						} else {
							createMarkers(placeDocuments, MarkerType.RESTAURANT);
							showMarkers(MarkerType.RESTAURANT);
						}
					}
				});

			}

			@Override
			public void onResultNoData() {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getActivity(), getString(R.string.not_founded_search_result), Toast.LENGTH_SHORT).show();
						removeMarkers(MarkerType.RESTAURANT);
					}
				});
			}
		});

		 */
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
								requireActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										addExtraMarkers(placeDocuments, markerType);
									}
								});
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