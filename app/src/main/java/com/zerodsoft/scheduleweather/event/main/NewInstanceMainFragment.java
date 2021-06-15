package com.zerodsoft.scheduleweather.event.main;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.CalendarContract;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.util.Xml;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.placecategory.activity.PlaceCategoryActivity;
import com.zerodsoft.scheduleweather.activity.placecategory.viewmodel.PlaceCategoryViewModel;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IRefreshView;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.common.interfaces.OnHiddenFragmentListener;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.event.event.fragments.EventFragment;
import com.zerodsoft.scheduleweather.event.foods.RestaurantFragment;
import com.zerodsoft.scheduleweather.event.foods.interfaces.ISetFoodMenuPoiItems;
import com.zerodsoft.scheduleweather.event.places.interfaces.OnClickedPlacesListListener;
import com.zerodsoft.scheduleweather.event.places.interfaces.PlaceItemsGetter;
import com.zerodsoft.scheduleweather.event.places.map.PlacesOfSelectedCategoriesFragment;
import com.zerodsoft.scheduleweather.event.weather.fragment.WeatherMainFragment;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.LocationItemViewPagerAdapter;
import com.zerodsoft.scheduleweather.navermap.NaverMapFragment;
import com.zerodsoft.scheduleweather.navermap.MarkerType;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnExtraListDataListener;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import org.jetbrains.annotations.NotNull;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

public class NewInstanceMainFragment extends NaverMapFragment implements ISetFoodMenuPoiItems,
		PlacesOfSelectedCategoriesFragment.PlaceCategoryChipsViewController, DialogInterface.OnDismissListener
		, IRefreshView {
	private final int CALENDAR_ID;
	private final long EVENT_ID;
	private final long INSTANCE_ID;
	private final long ORIGINAL_BEGIN;
	private final long ORIGINAL_END;

	private Integer DEFAULT_HEIGHT_OF_BOTTOMSHEET;
	private CalendarViewModel calendarViewModel;
	private PlaceCategoryViewModel placeCategoryViewModel;

	private ContentValues instance;
	private LocationDTO selectedLocationDtoInEvent;
	private EventFunctionItemView[] functionButtons;
	private ImageView functionButton;
	private Marker selectedLocationInEventMarker;
	private InfoWindow selectedLocationInEventInfoWindow;

	private RestaurantsGetter restaurantItemGetter;
	private OnExtraListDataListener<Integer> restaurantOnExtraListDataListener;

	private PlaceItemsGetter placeItemsGetter;

	private ChipGroup placeCategoryChipGroup;
	private final ArrayMap<String, Chip> placeCategoryChipMap = new ArrayMap<>();
	private Chip placeCategoryListChip;
	private Chip placeCategorySettingsChip;
	private String selectedPlaceCategoryCode;
	private OnExtraListDataListener<String> placeCategoryOnExtraListDataListener;

	private LinearLayout chipsLayout;
	private ViewGroup functionItemsView;

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
		public void onFragmentCreated(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
			super.onFragmentCreated(fm, f, savedInstanceState);

		}

		@Override
		public void onFragmentDestroyed(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f) {
			super.onFragmentDestroyed(fm, f);

		}


	};

	public NewInstanceMainFragment(int CALENDAR_ID, long EVENT_ID, long INSTANCE_ID, long ORIGINAL_BEGIN, long ORIGINAL_END) {
		this.CALENDAR_ID = CALENDAR_ID;
		this.EVENT_ID = EVENT_ID;
		this.INSTANCE_ID = INSTANCE_ID;
		this.ORIGINAL_BEGIN = ORIGINAL_BEGIN;
		this.ORIGINAL_END = ORIGINAL_END;
	}


	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		addOnBackPressedCallback();
	}


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
		placeCategoryViewModel = new ViewModelProvider(this).get(PlaceCategoryViewModel.class);
		getChildFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		removeOnBackPressedCallback();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getChildFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		createFunctionList();

		//restaurant
		Object[] results2 = createBottomSheet(R.id.restaurant_fragment_container);
		LinearLayout restaurantsBottomSheet = (LinearLayout) results2[0];
		BottomSheetBehavior restaurantsBottomSheetBehavior = (BottomSheetBehavior) results2[1];

		bottomSheetViewMap.put(BottomSheetType.RESTAURANT, restaurantsBottomSheet);
		bottomSheetBehaviorMap.put(BottomSheetType.RESTAURANT, restaurantsBottomSheetBehavior);

		restaurantsBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
			@Override
			public void onStateChanged(@NonNull View bottomSheet, int newState) {

			}

			@Override
			public void onSlide(@NonNull View bottomSheet, float slideOffset) {

			}
		});

		binding.naverMapFragmentRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				binding.naverMapFragmentRootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

				final int HEADERBAR_HEIGHT = (int) getResources().getDimension(R.dimen.map_header_bar_height);
				final int HEADERBAR_TOP_MARGIN = (int) getResources().getDimension(R.dimen.map_header_bar_top_margin);
				final int HEADERBAR_MARGIN = (int) (HEADERBAR_TOP_MARGIN * 1.5f);
				DEFAULT_HEIGHT_OF_BOTTOMSHEET = binding.naverMapFragmentRootLayout.getHeight() - HEADERBAR_HEIGHT - HEADERBAR_MARGIN;
				functionButtons[0].callOnClick();
			}
		});
	}

	private void setHeightOfBottomSheet(int height, LinearLayout bottomSheetView, BottomSheetBehavior bottomSheetBehavior) {
		bottomSheetView.getLayoutParams().height = height;
		bottomSheetView.requestLayout();
		bottomSheetBehavior.onLayoutChild(binding.naverMapFragmentRootLayout, bottomSheetView, ViewCompat.LAYOUT_DIRECTION_LTR);
	}

	@Override
	public void onMapReady(@NonNull NaverMap naverMap) {
		super.onMapReady(naverMap);
		setInitInstanceData();
	}

	private void createFunctionList() {
		//이벤트 정보, 날씨, 음식점
		functionButton = new ImageView(getContext());
		functionButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.map_button_rect));
		functionButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.expand_less_icon));
		functionButton.setElevation(3f);
		functionButton.setClickable(true);
		functionButton.setId(R.id.function_btn_in_instance_main_fragment);

		final int btnPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics());
		functionButton.setPadding(btnPadding, btnPadding, btnPadding, btnPadding);

		ConstraintLayout.LayoutParams functionBtnLayoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		functionBtnLayoutParams.bottomToTop = binding.naverMapButtonsLayout.currentAddress.getId();
		functionBtnLayoutParams.leftToLeft = binding.naverMapButtonsLayout.getRoot().getId();

		final int marginBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, getResources().getDisplayMetrics());
		functionBtnLayoutParams.bottomMargin = marginBottom;

		binding.naverMapButtonsLayout.getRoot().addView(functionButton, functionBtnLayoutParams);
		functionButton.setOnClickListener(new View.OnClickListener() {
			boolean isExpanded = true;

			@Override
			public void onClick(View view) {
				functionButton.setImageDrawable(isExpanded ? ContextCompat.getDrawable(getContext(), R.drawable.expand_more_icon)
						: ContextCompat.getDrawable(getContext(), R.drawable.expand_less_icon));

				if (isExpanded) {
					collapseFunctions();
				} else {
					expandFunctions();
				}
				isExpanded = !isExpanded;
			}
		});

		//기능 세부 버튼
		functionItemsView = (LinearLayout) getLayoutInflater().inflate(R.layout.event_function_items_view,
				binding.naverMapButtonsLayout.getRoot(), false);

		functionButtons = new EventFunctionItemView[]{functionItemsView.findViewById(R.id.function_event_info)
				, functionItemsView.findViewById(R.id.function_weather)
				, functionItemsView.findViewById(R.id.function_restaurant)};

		ConstraintLayout.LayoutParams functionItemsViewLayoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		final int marginBottomFunctionBtnsLayout = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f,
				getResources().getDisplayMetrics());

		functionItemsViewLayoutParams.bottomMargin = marginBottomFunctionBtnsLayout;
		functionItemsViewLayoutParams.bottomToTop = functionButton.getId();
		functionItemsViewLayoutParams.leftToLeft = binding.naverMapButtonsLayout.getRoot().getId();

		binding.naverMapButtonsLayout.getRoot().addView(functionItemsView, functionItemsViewLayoutParams);

		functionButtons[0].setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//이벤트 정보
				functionButton.callOnClick();

				EventFragment eventFragment = new EventFragment(NewInstanceMainFragment.this, DEFAULT_HEIGHT_OF_BOTTOMSHEET, CALENDAR_ID,
						EVENT_ID, INSTANCE_ID,
						ORIGINAL_BEGIN, ORIGINAL_END);
				eventFragment.show(getParentFragmentManager(), EventFragment.TAG);
			}
		});

		functionButtons[1].setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//날씨
				functionButton.callOnClick();

				WeatherMainFragment weatherMainFragment = new WeatherMainFragment(DEFAULT_HEIGHT_OF_BOTTOMSHEET, CALENDAR_ID, EVENT_ID);
				Bundle bundle = new Bundle();
				LatLng latLng = naverMap.getContentBounds().getCenter();
				bundle.putString("latitude", String.valueOf(latLng.latitude));
				bundle.putString("longitude", String.valueOf(latLng.longitude));
				bundle.putBoolean("hasSimpleLocation", hasSimpleLocation());

				weatherMainFragment.setArguments(bundle);
				weatherMainFragment.show(getParentFragmentManager(), WeatherMainFragment.TAG);
			}
		});

		functionButtons[2].setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//음식점
				functionButton.callOnClick();

				FragmentManager fragmentManager = getChildFragmentManager();

				if (bottomSheetFragmentMap.containsKey(BottomSheetType.RESTAURANT)) {
					Fragment restaurantFragment = bottomSheetFragmentMap.get(BottomSheetType.RESTAURANT);

					if (restaurantFragment.isVisible()) {
						fragmentManager.popBackStackImmediate();
					} else {
						fragmentManager.beginTransaction().show(restaurantFragment).addToBackStack(getString(R.string.tag_restaurant_fragment)).commit();
					}
				} else {
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
							}, CALENDAR_ID, INSTANCE_ID, EVENT_ID);
					bottomSheetFragmentMap.put(BottomSheetType.RESTAURANT, restaurantFragment);
					fragmentManager.beginTransaction().add(binding.fragmentContainer.getId(), restaurantFragment
							, getString(R.string.tag_restaurant_fragment)).commitNow();
					fragmentManager.beginTransaction().show(restaurantFragment).addToBackStack(getString(R.string.tag_restaurant_fragment)).commit();
				}

			}
		});
	}

	private void collapseFunctions() {
		functionItemsView.setVisibility(View.GONE);
	}


	private void expandFunctions() {
		functionItemsView.setVisibility(View.VISIBLE);
	}

	private void setInitInstanceData() {
		instance = calendarViewModel.getInstance(INSTANCE_ID, ORIGINAL_BEGIN, ORIGINAL_END);
		//location
		if (hasSimpleLocation()) {
			setDetailLocationData();
		} else {

		}
	}

	public boolean hasSimpleLocation() {
		boolean result = false;

		if (instance.getAsString(CalendarContract.Instances.EVENT_LOCATION) != null) {
			result = !instance.getAsString(CalendarContract.Instances.EVENT_LOCATION).isEmpty();
		}
		return result;
	}

	private void setDetailLocationData() {
		if (hasSimpleLocation()) {
			locationViewModel.getLocation(EVENT_ID, new DbQueryCallback<LocationDTO>() {
				@Override
				public void onResultSuccessful(LocationDTO locationResultDto) {
					if (selectedLocationDtoInEvent == null) {
						selectedLocationDtoInEvent = locationResultDto;

						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								addPlaceCategoryListFragmentIntoBottomSheet();
								createPlaceCategoryListChips();
								createSelectedLocationMarker();
							}
						});
					} else {
						if (locationResultDto.equals(selectedLocationDtoInEvent)) {
						} else {
							selectedLocationDtoInEvent = locationResultDto;

							requireActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									selectedLocationInEventMarker.setMap(null);
									selectedLocationInEventMarker = null;
									createSelectedLocationMarker();
								}
							});
						}
					}
				}

				@Override
				public void onResultNoData() {

				}
			});

		}

	}

	private void createSelectedLocationMarker() {
		LatLng latLng = new LatLng(Double.parseDouble(selectedLocationDtoInEvent.getLatitude()),
				Double.parseDouble(selectedLocationDtoInEvent.getLongitude()));

		final int markerSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28f, getResources().getDisplayMetrics());

		selectedLocationInEventMarker = new Marker(latLng);
		selectedLocationInEventMarker.setMap(naverMap);
		selectedLocationInEventMarker.setWidth(markerSize);
		selectedLocationInEventMarker.setHeight(markerSize);
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
		setCurrentAddress();
	}

	private Object[] createBottomSheet(int fragmentContainerViewId) {
		XmlPullParser parser = getResources().getXml(R.xml.persistent_bottom_sheet_default_attrs);
		try {
			parser.next();
			parser.nextTag();
		} catch (Exception e) {
			e.printStackTrace();
		}

		AttributeSet attr = Xml.asAttributeSet(parser);
		LinearLayout bottomSheetView = new LinearLayout(getContext(), attr);

		CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParams.setBehavior(new BottomSheetBehavior());
		bottomSheetView.setLayoutParams(layoutParams);
		bottomSheetView.setClickable(true);
		bottomSheetView.setOrientation(LinearLayout.VERTICAL);

		binding.naverMapFragmentRootLayout.addView(bottomSheetView);

		//fragmentcontainerview 추가
		FragmentContainerView fragmentContainerView = new FragmentContainerView(getContext());
		fragmentContainerView.setId(fragmentContainerViewId);
		fragmentContainerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		bottomSheetView.addView(fragmentContainerView);

		BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
		bottomSheetBehavior.setDraggable(false);
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
		bottomSheetBehavior.setHideable(false);
		bottomSheetBehavior.setPeekHeight(0);

		return new Object[]{bottomSheetView, bottomSheetBehavior};
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

		PlacesOfSelectedCategoriesFragment placesOfSelectedCategoriesFragment = new PlacesOfSelectedCategoriesFragment(EVENT_ID, new OnClickedPlacesListListener() {
			@Override
			public void onClickedItemInList(PlaceCategoryDTO placeCategory, int index) {
				getChildFragmentManager().popBackStackImmediate();
				placeCategoryChipMap.get(placeCategory.getCode()).setChecked(true);
				onPOIItemSelectedByList(index, MarkerType.SELECTED_PLACE_CATEGORY);
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
		placeCategoryListChip.setTextColor(Color.BLACK);
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
		placeCategorySettingsChip.setTextColor(Color.BLACK);
		placeCategorySettingsChip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				placeCategoryActivityResultLauncher.launch(new Intent(getActivity(), PlaceCategoryActivity.class));
			}
		});

		placeCategoryChipGroup.addView(placeCategoryListChip, 0);
		placeCategoryChipGroup.addView(placeCategorySettingsChip, 1);
	}


	private final ActivityResultLauncher<Intent> placeCategoryActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
			new ActivityResultCallback<ActivityResult>() {
				@Override
				public void onActivityResult(ActivityResult result) {
					if (result.getResultCode() == 0) {
						if (placeCategoryChipGroup.getCheckedChipIds().size() > 0) {
							placeCategoryChipMap.get(selectedPlaceCategoryCode).setChecked(false);
						}
						((PlacesOfSelectedCategoriesFragment) bottomSheetFragmentMap.get(BottomSheetType.SELECTED_PLACE_CATEGORY)).makeCategoryListView();
					}
				}
			});

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
		final int markerSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28f, getResources().getDisplayMetrics());

		Marker criteriaLocationForRestaurantsMarker = new Marker(latLng);
		criteriaLocationForRestaurantsMarker.setMap(naverMap);
		criteriaLocationForRestaurantsMarker.setWidth(markerSize);
		criteriaLocationForRestaurantsMarker.setHeight(markerSize);
		criteriaLocationForRestaurantsMarker.setIcon(OverlayImage.fromResource(R.drawable.criteria_location_svg));
		criteriaLocationForRestaurantsMarker.setForceShowIcon(true);
		criteriaLocationForRestaurantsMarker.setCaptionColor(Color.BLACK);
		criteriaLocationForRestaurantsMarker.setCaptionTextSize(12f);
		criteriaLocationForRestaurantsMarker.setCaptionText(name);
		criteriaLocationForRestaurantsMarker.setSubCaptionText(getString(R.string.criteria_location));
		criteriaLocationForRestaurantsMarker.setSubCaptionTextSize(11f);
		criteriaLocationForRestaurantsMarker.setSubCaptionColor(Color.BLACK);

		if (!markersMap.containsKey(MarkerType.CRITERIA_LOCATION_FOR_RESTAURANTS)) {
			markersMap.put(MarkerType.CRITERIA_LOCATION_FOR_RESTAURANTS, new ArrayList<>());
		}
		markersMap.get(MarkerType.CRITERIA_LOCATION_FOR_RESTAURANTS).add(criteriaLocationForRestaurantsMarker);
	}

	@Override
	public void onChangeFoodMenu() {
		setStateOfBottomSheet(BottomSheetType.LOCATION_ITEM, BottomSheetBehavior.STATE_COLLAPSED);

		restaurantItemGetter.getRestaurants(new DbQueryCallback<List<PlaceDocuments>>() {
			@Override
			public void onResultSuccessful(List<PlaceDocuments> placeDocuments) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						createMarkers(placeDocuments, MarkerType.RESTAURANT);
						showMarkers(MarkerType.RESTAURANT);
					}
				});

			}

			@Override
			public void onResultNoData() {

			}
		});
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
		setInitInstanceData();
	}

	@Override
	public void onDismiss(DialogInterface dialogInterface) {

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