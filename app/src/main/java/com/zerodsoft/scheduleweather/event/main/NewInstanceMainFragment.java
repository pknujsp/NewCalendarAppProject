package com.zerodsoft.scheduleweather.event.main;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.os.RemoteException;
import android.provider.CalendarContract;
import android.service.carrier.CarrierMessagingService;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.event.fragments.EventFragment;
import com.zerodsoft.scheduleweather.event.foods.main.fragment.NewFoodsMainFragment;
import com.zerodsoft.scheduleweather.event.places.interfaces.PlaceItemsGetter;
import com.zerodsoft.scheduleweather.event.places.map.PlacesOfSelectedCategoriesFragment;
import com.zerodsoft.scheduleweather.event.weather.fragment.WeatherItemFragment;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.LocationItemViewPagerAdapter;
import com.zerodsoft.scheduleweather.navermap.NaverMapFragment;
import com.zerodsoft.scheduleweather.navermap.MarkerType;
import com.zerodsoft.scheduleweather.navermap.fragment.search.LocationSearchFragment;
import com.zerodsoft.scheduleweather.navermap.fragment.searchheader.MapHeaderMainFragment;
import com.zerodsoft.scheduleweather.navermap.fragment.searchheader.MapHeaderSearchFragment;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnExtraListDataListener;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import org.xmlpull.v1.XmlPullParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewInstanceMainFragment extends NaverMapFragment implements NewFoodsMainFragment.FoodMenuChipsViewController, PlacesOfSelectedCategoriesFragment.PlaceCategoryChipsViewController
{
    public static final String TAG = "NewInstanceMainFragment";

    private final int CALENDAR_ID;
    private final long EVENT_ID;
    private final long INSTANCE_ID;
    private final long ORIGINAL_BEGIN;
    private final long ORIGINAL_END;

    //request
    public static final int REQUEST_SELECT_LOCATION = 1000;
    public static final int REQUEST_RESELECT_LOCATION = 1100;
    public static final int REQUEST_DELETE_EVENT = 1200;
    public static final int REQUEST_EXCEPT_THIS_INSTANCE = 1300;
    public static final int REQUEST_SUBSEQUENT_INCLUDING_THIS = 1400;

    //result
    public static final int RESULT_SELECTED_LOCATION = 2000;
    public static final int RESULT_RESELECTED_LOCATION = 2100;
    public static final int RESULT_REMOVED_LOCATION = 2200;

    public static final int RESULT_REMOVED_EVENT = 3000;
    public static final int RESULT_EXCEPTED_INSTANCE = 3100;
    public static final int RESULT_UPDATED_INSTANCE = 3200;

    public static final int RESULT_EDITED_PLACE_CATEGORY = 4000;
    public static final int RESULT_UPDATED_VALUE = 5000;

    private CalendarViewModel calendarViewModel;
    private LocationViewModel locationViewModel;

    private ContentValues instance;
    private LocationDTO selectedLocationDtoInEvent;
    private TextView[] functionButtons;
    private ImageView functionButton;
    private Marker selectedLocationInEventMarker;
    private InfoWindow selectedLocationInEventInfoWindow;

    private ChipGroup foodMenuChipGroup;
    private Map<String, Chip> foodMenuChipMap = new HashMap<>();
    private Chip foodMenuListChip;
    private String selectedFoodMenu;
    private RestaurantsGetter restaurantItemGetter;
    private OnExtraListDataListener<String> restaurantOnExtraListDataListener;

    private PlaceItemsGetter placeItemsGetter;

    private ChipGroup placeCategoryChipGroup;
    private final Map<String, Chip> placeCategoryChipMap = new HashMap<>();
    private Chip placeCategoryListChip;
    private Chip placeCategorySettingsChip;
    private PlaceCategoryDTO selectedPlaceCategory;

    private LinearLayout chipsLayout;

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        addOnBackPressedCallback();
    }

    public NewInstanceMainFragment(int CALENDAR_ID, long EVENT_ID, long INSTANCE_ID, long ORIGINAL_BEGIN, long ORIGINAL_END)
    {
        this.CALENDAR_ID = CALENDAR_ID;
        this.EVENT_ID = EVENT_ID;
        this.INSTANCE_ID = INSTANCE_ID;
        this.ORIGINAL_BEGIN = ORIGINAL_BEGIN;
        this.ORIGINAL_END = ORIGINAL_END;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        removeOnBackPressedCallback();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        createFunctionList();

        functionButton.setVisibility(View.GONE);

        chipsLayout = new LinearLayout(getContext());
        chipsLayout.setOrientation(LinearLayout.VERTICAL);

        RelativeLayout.LayoutParams chipLayoutsParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        chipLayoutsParams.addRule(RelativeLayout.BELOW, binding.naverMapHeaderBar.getRoot().getId());
        chipLayoutsParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics());

        binding.naverMapViewLayout.addView(chipsLayout, chipLayoutsParams);

        //place category
        Object[] placeCategoryResult = createBottomSheet(R.id.place_category_fragment_container);
        LinearLayout placeCategoryBottomSheet = (LinearLayout) placeCategoryResult[0];
        BottomSheetBehavior placeCategoryBottomSheetBehavior = (BottomSheetBehavior) placeCategoryResult[1];

        bottomSheetViewMap.put(BottomSheetType.SELECTED_PLACE_CATEGORY, placeCategoryBottomSheet);
        bottomSheetBehaviorMap.put(BottomSheetType.SELECTED_PLACE_CATEGORY, placeCategoryBottomSheetBehavior);

        addPlaceCategoryListFragmentIntoBottomSheet();

        placeCategoryBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
                if (newState == BottomSheetBehavior.STATE_EXPANDED)
                {
                    placeCategoryListChip.setText(R.string.close_list);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                {
                    placeCategoryListChip.setText(R.string.open_list);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {

            }
        });

        //restaurant
        Object[] results2 = createBottomSheet(R.id.restaurant_fragment_container);
        LinearLayout restaurantsBottomSheet = (LinearLayout) results2[0];
        BottomSheetBehavior restaurantsBottomSheetBehavior = (BottomSheetBehavior) results2[1];

        bottomSheetViewMap.put(BottomSheetType.RESTAURANT, restaurantsBottomSheet);
        bottomSheetBehaviorMap.put(BottomSheetType.RESTAURANT, restaurantsBottomSheetBehavior);

        restaurantsBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
                switch (newState)
                {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        NewFoodsMainFragment newFoodsMainFragment = (NewFoodsMainFragment) getChildFragmentManager().findFragmentByTag(NewFoodsMainFragment.TAG);
                        getChildFragmentManager().beginTransaction().show(newFoodsMainFragment).addToBackStack(NewFoodsMainFragment.TAG).commit();
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {

            }
        });

        binding.naverMapFragmentRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                binding.naverMapFragmentRootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                final int HEADERBAR_HEIGHT = (int) getResources().getDimension(R.dimen.map_header_bar_height);
                final int HEADERBAR_TOP_MARGIN = (int) getResources().getDimension(R.dimen.map_header_bar_top_margin);
                final int HEADERBAR_MARGIN = (int) (HEADERBAR_TOP_MARGIN * 1.5f);
                final int DEFAULT_HEIGHT_OF_BOTTOMSHEET = binding.naverMapFragmentRootLayout.getHeight() - HEADERBAR_HEIGHT - HEADERBAR_MARGIN;

                addEventFragmentIntoBottomSheet(DEFAULT_HEIGHT_OF_BOTTOMSHEET);
                addWeatherFragmentIntoBottomSheet(DEFAULT_HEIGHT_OF_BOTTOMSHEET);

                setHeightOfBottomSheet(DEFAULT_HEIGHT_OF_BOTTOMSHEET, restaurantsBottomSheet, restaurantsBottomSheetBehavior);
                setHeightOfBottomSheet(DEFAULT_HEIGHT_OF_BOTTOMSHEET, placeCategoryBottomSheet, placeCategoryBottomSheetBehavior);

                functionButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setHeightOfBottomSheet(int height, LinearLayout bottomSheetView, BottomSheetBehavior bottomSheetBehavior)
    {
        bottomSheetView.getLayoutParams().height = height;
        bottomSheetView.requestLayout();
        bottomSheetBehavior.onLayoutChild(binding.naverMapFragmentRootLayout, bottomSheetView, ViewCompat.LAYOUT_DIRECTION_LTR);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap)
    {
        super.onMapReady(naverMap);
        setInitInstanceData();
    }

    private void createFunctionList()
    {
        //이벤트 정보, 날씨, 음식점
        functionButton = new ImageView(getContext());
        functionButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.map_button_rect));
        functionButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.expand_less_icon));
        functionButton.setElevation(3f);
        functionButton.setClickable(true);

        final int btnPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics());
        final int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics());
        functionButton.setPadding(btnPadding, btnPadding, btnPadding, btnPadding);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ABOVE, binding.naverMapButtonsLayout.currentAddress.getId());
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        final int marginBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, getResources().getDisplayMetrics());
        final int marginLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics());
        layoutParams.bottomMargin = marginBottom;
        layoutParams.leftMargin = marginLeft;

        binding.naverMapButtonsLayout.getRoot().addView(functionButton, layoutParams);
        functionButton.setOnClickListener(new View.OnClickListener()
        {
            boolean isExpanded = false;

            @Override
            public void onClick(View view)
            {
                functionButton.setImageDrawable(isExpanded ? ContextCompat.getDrawable(getContext(), R.drawable.expand_more_icon)
                        : ContextCompat.getDrawable(getContext(), R.drawable.expand_less_icon));

                if (isExpanded)
                {
                    collapseFunctions();
                } else
                {
                    expandFunctions();
                }
                isExpanded = !isExpanded;
            }
        });

        //기능 세부 버튼
        functionButtons = new TextView[3];
        String[] functionNameList = {getString(R.string.instance_info), getString(R.string.weather), getString(R.string.restaurant)};

        for (int i = 0; i < functionButtons.length; i++)
        {
            functionButtons[i] = new TextView(getContext());
            functionButtons[i].setBackground(ContextCompat.getDrawable(getContext(), R.drawable.map_button_rect));
            functionButtons[i].setText(functionNameList[i]);
            functionButtons[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
            functionButtons[i].setTextColor(Color.BLACK);
            functionButtons[i].setElevation(3f);
            functionButtons[i].setClickable(true);
            functionButtons[i].setPadding(padding, padding, padding, padding);
            functionButtons[i].setVisibility(View.GONE);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ABOVE, binding.naverMapButtonsLayout.currentAddress.getId());
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.bottomMargin = marginBottom;
            params.leftMargin = marginLeft;

            functionButtons[i].setLayoutParams(params);
            binding.naverMapButtonsLayout.getRoot().addView(functionButtons[i]);
        }

        functionButtons[0].setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //이벤트 정보
                functionButton.callOnClick();

                EventFragment eventFragment = (EventFragment) bottomSheetFragmentMap.get(BottomSheetType.INSTANCE_INFO);
                eventFragment.show(getParentFragmentManager(), EventFragment.TAG);
            }
        });

        functionButtons[1].setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //날씨
                functionButton.callOnClick();

                WeatherItemFragment weatherItemFragment = (WeatherItemFragment) bottomSheetFragmentMap.get(BottomSheetType.WEATHER);
                weatherItemFragment.show(getParentFragmentManager(), WeatherItemFragment.TAG);
            }
        });

        functionButtons[2].setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //음식점
                functionButton.callOnClick();
                if (getChildFragmentManager().findFragmentByTag(NewFoodsMainFragment.TAG) == null)
                {
                    addRestaurantFragmentIntoBottomSheet();
                }
                onCalledBottomSheet(BottomSheetBehavior.STATE_EXPANDED, bottomSheetBehaviorMap.get(BottomSheetType.RESTAURANT));
                setStateOfBottomSheet(BottomSheetType.RESTAURANT, BottomSheetBehavior.STATE_EXPANDED);
            }
        });

    }

    private void collapseFunctions()
    {
        functionButtons[0].animate().translationY(0);
        functionButtons[1].animate().translationY(0);
        functionButtons[2].animate().translationY(0);

        functionButtons[0].setVisibility(View.GONE);
        functionButtons[1].setVisibility(View.GONE);
        functionButtons[2].setVisibility(View.GONE);
    }


    private void expandFunctions()
    {
        final float y = functionButton.getTranslationY();
        final float margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, getResources().getDisplayMetrics());
        final float fabHeight = functionButton.getHeight();

        functionButtons[2].setVisibility(View.VISIBLE);
        functionButtons[1].setVisibility(View.VISIBLE);
        functionButtons[0].setVisibility(View.VISIBLE);

        functionButtons[2].animate().translationY(y - (fabHeight + margin));
        functionButtons[1].animate().translationY(y - (fabHeight + margin) * 2);
        functionButtons[0].animate().translationY(y - (fabHeight + margin) * 3);
    }

    private void setInitInstanceData()
    {
        instance = calendarViewModel.getInstance(CALENDAR_ID, INSTANCE_ID, ORIGINAL_BEGIN, ORIGINAL_END);
        //location
        if (hasSimpleLocation())
        {
            setDetailLocationData();
        } else
        {

        }
    }

    public boolean hasSimpleLocation()
    {
        boolean result = false;

        if (instance.getAsString(CalendarContract.Instances.EVENT_LOCATION) != null)
        {
            result = !instance.getAsString(CalendarContract.Instances.EVENT_LOCATION).isEmpty();
        }
        return result;
    }

    private void setDetailLocationData()
    {
        if (hasSimpleLocation())
        {
            locationViewModel.hasDetailLocation(CALENDAR_ID, EVENT_ID, new CarrierMessagingService.ResultCallback<Boolean>()
            {
                @Override
                public void onReceiveResult(@NonNull Boolean hasDetailLocation) throws RemoteException
                {
                    locationViewModel.getLocation(CALENDAR_ID, EVENT_ID, new CarrierMessagingService.ResultCallback<LocationDTO>()
                    {
                        @Override
                        public void onReceiveResult(@NonNull LocationDTO locationDTO) throws RemoteException
                        {
                            if (selectedLocationDtoInEvent == null)
                            {
                                selectedLocationDtoInEvent = locationDTO;

                                requireActivity().runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        createSelectedLocationMarker();
                                    }
                                });
                            } else
                            {
                                if (locationDTO.equals(selectedLocationDtoInEvent))
                                {
                                } else
                                {
                                    selectedLocationDtoInEvent = locationDTO;
                                    selectedLocationInEventMarker.setMap(null);
                                    selectedLocationInEventMarker = null;

                                    requireActivity().runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            createSelectedLocationMarker();
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            });
        }

    }

    private void createSelectedLocationMarker()
    {
        LatLng latLng = new LatLng(selectedLocationDtoInEvent.getLatitude(), selectedLocationDtoInEvent.getLongitude());

        final int markerSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, getResources().getDisplayMetrics());

        selectedLocationInEventMarker = new Marker(latLng);
        selectedLocationInEventMarker.setMap(naverMap);
        selectedLocationInEventMarker.setWidth(markerSize);
        selectedLocationInEventMarker.setHeight(markerSize);
        selectedLocationInEventMarker.setIcon(OverlayImage.fromResource(R.drawable.current_location_icon));
        selectedLocationInEventMarker.setForceShowIcon(true);
        selectedLocationInEventMarker.setCaptionColor(Color.BLUE);
        selectedLocationInEventMarker.setCaptionHaloColor(Color.rgb(200, 255, 200));
        selectedLocationInEventMarker.setCaptionTextSize(12f);
        selectedLocationInEventMarker.setOnClickListener(new Overlay.OnClickListener()
        {
            @Override
            public boolean onClick(@NonNull Overlay overlay)
            {
                if (selectedLocationInEventInfoWindow.getMarker() == null)
                {
                    selectedLocationInEventInfoWindow.open(selectedLocationInEventMarker);
                    selectedLocationInEventMarker.setCaptionText(getString(R.string.message_click_marker_to_delete));
                } else
                {
                    selectedLocationInEventInfoWindow.close();
                    selectedLocationInEventMarker.setCaptionText("");
                }
                return true;
            }
        });

        selectedLocationInEventInfoWindow = new InfoWindow();
        selectedLocationInEventInfoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getContext())
        {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow)
            {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(getString(R.string.selected_location_in_event));
                stringBuilder.append("\n");
                if (selectedLocationDtoInEvent.getLocationType() == LocationType.PLACE)
                {
                    stringBuilder.append(getString(R.string.place));
                    stringBuilder.append(" : ");
                    stringBuilder.append(selectedLocationDtoInEvent.getPlaceName());
                    stringBuilder.append("\n");
                    stringBuilder.append(getString(R.string.address));
                    stringBuilder.append(" : ");
                    stringBuilder.append(selectedLocationDtoInEvent.getAddressName());
                } else
                {
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

    private Object[] createBottomSheet(int fragmentContainerViewId)
    {
        XmlPullParser parser = getResources().getXml(R.xml.persistent_bottom_sheet_default_attrs);
        try
        {
            parser.next();
            parser.nextTag();
        } catch (Exception e)
        {
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

    private void addEventFragmentIntoBottomSheet(int viewHeight)
    {
        EventFragment eventFragment = EventFragment.newInstance(viewHeight, CALENDAR_ID, EVENT_ID, INSTANCE_ID, ORIGINAL_BEGIN, ORIGINAL_END);
        bottomSheetFragmentMap.put(BottomSheetType.INSTANCE_INFO, eventFragment);
    }

    private void addWeatherFragmentIntoBottomSheet(int viewHeight)
    {
        WeatherItemFragment weatherFragment = WeatherItemFragment.newInstance(viewHeight);
        Bundle bundle = new Bundle();
        bundle.putInt(CalendarContract.Instances.CALENDAR_ID, CALENDAR_ID);
        bundle.putLong(CalendarContract.Instances.EVENT_ID, EVENT_ID);
        bundle.putLong(CalendarContract.Instances._ID, INSTANCE_ID);
        bundle.putLong(CalendarContract.Instances.BEGIN, ORIGINAL_BEGIN);

        weatherFragment.setArguments(bundle);
        bottomSheetFragmentMap.put(BottomSheetType.WEATHER, weatherFragment);
    }

    private void addRestaurantFragmentIntoBottomSheet()
    {
        NewFoodsMainFragment newFoodsMainFragment = new NewFoodsMainFragment(this, this
                , this, CALENDAR_ID, INSTANCE_ID, EVENT_ID);
        getChildFragmentManager().beginTransaction()
                .add(bottomSheetViewMap.get(BottomSheetType.RESTAURANT).getChildAt(0).getId(), newFoodsMainFragment, NewFoodsMainFragment.TAG).hide(newFoodsMainFragment).commitNow();
        bottomSheetFragmentMap.put(BottomSheetType.RESTAURANT, newFoodsMainFragment);
    }

    public void createPlaceCategoryListChips()
    {
        //-----------chip group
        HorizontalScrollView chipScrollView = new HorizontalScrollView(getContext());
        chipScrollView.setHorizontalScrollBarEnabled(false);
        chipScrollView.setId(R.id.place_category_chips_scroll_layout);

        LinearLayout.LayoutParams chipLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        chipsLayout.addView(chipScrollView, chipLayoutParams);

        placeCategoryChipGroup = new ChipGroup(getContext(), null, R.style.Widget_MaterialComponents_ChipGroup);
        placeCategoryChipGroup.setSingleSelection(true);
        placeCategoryChipGroup.setSingleLine(true);
        placeCategoryChipGroup.setId(R.id.chip_group);
        placeCategoryChipGroup.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        chipScrollView.addView(placeCategoryChipGroup);

        addListChip();
        addSettingsChip();
    }

    @Override
    public void addPlaceCategoryListFragmentIntoBottomSheet()
    {
        PlacesOfSelectedCategoriesFragment placesOfSelectedCategoriesFragment = new PlacesOfSelectedCategoriesFragment(CALENDAR_ID, EVENT_ID, this, this, this, this);
        placeItemsGetter = placesOfSelectedCategoriesFragment;

        getChildFragmentManager().beginTransaction()
                .add(bottomSheetViewMap.get(BottomSheetType.SELECTED_PLACE_CATEGORY).getChildAt(0).getId()
                        , placesOfSelectedCategoriesFragment, PlacesOfSelectedCategoriesFragment.TAG).commitNow();

        bottomSheetFragmentMap.put(BottomSheetType.SELECTED_PLACE_CATEGORY, placesOfSelectedCategoriesFragment);
    }

    @Override
    public void setPlaceCategoryChips(List<PlaceCategoryDTO> placeCategoryList)
    {
        if (placeCategoryChipGroup.getChildCount() >= 3)
        {
            placeCategoryChipGroup.removeViews(2, placeCategoryChipGroup.getChildCount() - 2);
        }
        placeCategoryChipMap.clear();

        //카테고리를 chip으로 표시
        int index = 0;

        for (PlaceCategoryDTO placeCategory : placeCategoryList)
        {
            Chip chip = new Chip(getContext(), null, R.style.Widget_MaterialComponents_Chip_Filter);
            chip.setChecked(false);
            chip.setText(placeCategory.getDescription());
            chip.setClickable(true);
            chip.setCheckable(true);
            chip.setVisibility(View.VISIBLE);
            chip.setOnCheckedChangeListener(placeCategoryChipOnCheckedChangeListener);

            final PlaceCategoryChipViewHolder chipViewHolder = new PlaceCategoryChipViewHolder(placeCategory, index++);
            chip.setTag(chipViewHolder);

            placeCategoryChipMap.put(placeCategory.getCode(), chip);
            placeCategoryChipGroup.addView(chip, new ChipGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }


    }

    @Override
    public void addListChip()
    {
        placeCategoryListChip = new Chip(getContext());
        placeCategoryListChip.setChecked(false);
        placeCategoryListChip.setText(R.string.open_list);
        placeCategoryListChip.setClickable(true);
        placeCategoryListChip.setCheckable(false);
        placeCategoryListChip.setTextColor(Color.BLACK);
        placeCategoryListChip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (bottomSheetBehaviorMap.get(BottomSheetType.SELECTED_PLACE_CATEGORY).getState() != BottomSheetBehavior.STATE_EXPANDED)
                {
                    onCalledBottomSheet(BottomSheetBehavior.STATE_EXPANDED, bottomSheetBehaviorMap.get(BottomSheetType.SELECTED_PLACE_CATEGORY));
                    PlacesOfSelectedCategoriesFragment placesOfSelectedCategoriesFragment = (PlacesOfSelectedCategoriesFragment) bottomSheetFragmentMap.get(BottomSheetType.SELECTED_PLACE_CATEGORY);

                    getChildFragmentManager().beginTransaction()
                            .hide(placesOfSelectedCategoriesFragment).commitNow();

                    getChildFragmentManager().beginTransaction()
                            .show(placesOfSelectedCategoriesFragment)
                            .addToBackStack(PlacesOfSelectedCategoriesFragment.TAG)
                            .commit();
                } else
                {
                    getChildFragmentManager().popBackStackImmediate();
                }
            }
        });

        placeCategoryChipGroup.addView(placeCategoryListChip, 0);
    }

    @Override
    public void addSettingsChip()
    {
        placeCategorySettingsChip = new Chip(getContext());
        placeCategorySettingsChip.setChecked(false);
        placeCategorySettingsChip.setText(R.string.app_settings);
        placeCategorySettingsChip.setClickable(true);
        placeCategorySettingsChip.setCheckable(false);
        placeCategorySettingsChip.setTextColor(Color.BLACK);
        placeCategorySettingsChip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                placeCategoryActivityResultLauncher.launch(new Intent(getActivity(), PlaceCategoryActivity.class));
            }
        });

        placeCategoryChipGroup.addView(placeCategorySettingsChip, 1);
    }

    private final ActivityResultLauncher<Intent> placeCategoryActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() == 0)
                    {
                        if (placeCategoryChipGroup.getCheckedChipIds().size() > 0)
                        {
                            placeCategoryChipMap.get(selectedPlaceCategory.getCode()).setChecked(false);
                        }
                        ((PlacesOfSelectedCategoriesFragment) bottomSheetFragmentMap.get(BottomSheetType.SELECTED_PLACE_CATEGORY)).makeCategoryListView();
                    }
                }
            });

    private final CompoundButton.OnCheckedChangeListener placeCategoryChipOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
        {
            /*
           - chip이 이미 선택되어 있는 경우
           같은 chip인 경우 : 선택해제, poiitem모두 삭제하고 bottomsheet를 숨긴다
           다른 chip인 경우 : 새로운 chip이 선택되고 난 뒤에 기존 chip이 선택해제 된다
           poiitem이 선택된 경우 해제하고, poiitem을 새로 생성한 뒤 poiitem전체가 보이도록 설정
             */
            if (isChecked)
            {
                if (getChildFragmentManager().findFragmentByTag(MapHeaderSearchFragment.TAG) != null)
                {
                    if (getChildFragmentManager().findFragmentByTag(MapHeaderSearchFragment.TAG).isVisible())
                    {
                        closeSearchFragments();
                    }
                }

                setLocationItemViewPagerAdapter(new LocationItemViewPagerAdapter(getContext()), MarkerType.SELECTED_PLACE_CATEGORY);

                PlaceCategoryDTO placeCategory = ((PlaceCategoryChipViewHolder) compoundButton.getTag()).placeCategory;
                List<PlaceDocuments> placeDocumentsList = placeItemsGetter.getPlaceItems(placeCategory);
                selectedPlaceCategory = placeCategory;

                createPoiItems(placeDocumentsList, MarkerType.SELECTED_PLACE_CATEGORY);
                showPoiItems(MarkerType.SELECTED_PLACE_CATEGORY);
            } else if (placeCategoryChipGroup.getCheckedChipIds().isEmpty()
                    && !markerMap.get((MarkerType) locationItemBottomSheetViewPager.getTag())
                    .isEmpty())
            {
                removePoiItems(MarkerType.SELECTED_PLACE_CATEGORY);
                selectedPlaceCategory = null;
            }
            setStateOfBottomSheet(BottomSheetType.LOCATION_ITEM, BottomSheetBehavior.STATE_COLLAPSED);
        }
    };

    @Override
    public void onClickedItemInList(PlaceCategoryDTO placeCategory, int index)
    {
        placeCategoryListChip.callOnClick();
        //create poi items
        placeCategoryChipMap.get(placeCategory.getCode()).setChecked(true);
        //select poi item
        onPOIItemSelectedByList(index, MarkerType.SELECTED_PLACE_CATEGORY);
    }

    @Override
    public void onClickedMoreInList(PlaceCategoryDTO placeCategory)
    {
        placeCategoryListChip.callOnClick();
        placeCategoryChipMap.get(placeCategory.getCode()).setChecked(true);
    }

    @Override
    public void createRestaurantListView(List<String> foodMenuList, RestaurantsGetter restaurantsGetter, OnExtraListDataListener<String> onExtraListDataListener)
    {
        this.restaurantItemGetter = restaurantsGetter;
        this.restaurantOnExtraListDataListener = onExtraListDataListener;

        createFoodMenuChips();
        addFoodMenuListChip();
        setFoodMenuChips(foodMenuList);
    }

    @Override
    public void removeRestaurantListView()
    {
        selectedFoodMenu = null;
        foodMenuChipMap.clear();

        HorizontalScrollView scrollView = chipsLayout.findViewById(R.id.food_menu_chips_scroll_layout);
        foodMenuChipGroup.removeAllViews();
        scrollView.removeAllViews();
        chipsLayout.removeView(scrollView);

        foodMenuChipGroup = null;
        foodMenuListChip = null;
        removePoiItems(MarkerType.RESTAURANT);
    }

    @Override
    public void createFoodMenuChips()
    {
        //-----------chip group
        HorizontalScrollView chipScrollView = new HorizontalScrollView(getContext());
        chipScrollView.setId(R.id.food_menu_chips_scroll_layout);
        chipScrollView.setHorizontalScrollBarEnabled(false);

        LinearLayout.LayoutParams chipLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        chipsLayout.addView(chipScrollView, chipLayoutParams);

        foodMenuChipGroup = new ChipGroup(getContext(), null, R.style.Widget_MaterialComponents_ChipGroup);
        foodMenuChipGroup.setSingleSelection(true);
        foodMenuChipGroup.setSingleLine(true);
        foodMenuChipGroup.setId(R.id.chip_group);
        foodMenuChipGroup.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        chipScrollView.addView(foodMenuChipGroup);
    }

    @Override
    public void setFoodMenuChips(List<String> foodMenuList)
    {
        if (foodMenuChipGroup.getChildCount() >= 2)
        {
            foodMenuChipGroup.removeViews(1, foodMenuChipGroup.getChildCount() - 1);
        }
        foodMenuChipMap.clear();

        //카테고리를 chip으로 표시
        int index = 0;

        for (String menu : foodMenuList)
        {
            Chip chip = new Chip(getContext(), null, R.style.Widget_MaterialComponents_Chip_Filter);
            chip.setChecked(false);
            chip.setText(menu);
            chip.setClickable(true);
            chip.setCheckable(true);
            chip.setVisibility(View.VISIBLE);
            chip.setOnCheckedChangeListener(foodMenuOnCheckedChangeListener);

            final FoodChipViewHolder foodChipViewHolder = new FoodChipViewHolder(menu, index++);
            chip.setTag(foodChipViewHolder);

            foodMenuChipMap.put(menu, chip);
            foodMenuChipGroup.addView(chip, new ChipGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }


    private final CompoundButton.OnCheckedChangeListener foodMenuOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
        {
            /*
           - chip이 이미 선택되어 있는 경우
           같은 chip인 경우 : 선택해제, poiitem모두 삭제하고 bottomsheet를 숨긴다
           다른 chip인 경우 : 새로운 chip이 선택되고 난 뒤에 기존 chip이 선택해제 된다
           poiitem이 선택된 경우 해제하고, poiitem을 새로 생성한 뒤 poiitem전체가 보이도록 설정
             */
            if (isChecked)
            {
                LocationItemViewPagerAdapter locationItemViewPagerAdapter = new LocationItemViewPagerAdapter(getContext());
                locationItemViewPagerAdapter.setIsVisibleFavoriteBtn(View.GONE);
                setLocationItemViewPagerAdapter(locationItemViewPagerAdapter, MarkerType.RESTAURANT);

                selectedFoodMenu = ((FoodChipViewHolder) compoundButton.getTag()).foodMenuName;
                restaurantItemGetter.getRestaurants(selectedFoodMenu, new CarrierMessagingService.ResultCallback<List<PlaceDocuments>>()
                {
                    @Override
                    public void onReceiveResult(@NonNull List<PlaceDocuments> placeDocuments) throws RemoteException
                    {
                        requireActivity().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                createPoiItems(placeDocuments, MarkerType.RESTAURANT);
                                showPoiItems(MarkerType.RESTAURANT);
                            }
                        });

                    }
                });
            } else if (foodMenuChipGroup.getCheckedChipIds().isEmpty())
            {
                removePoiItems(MarkerType.RESTAURANT);
                selectedFoodMenu = null;
            }
            setStateOfBottomSheet(BottomSheetType.LOCATION_ITEM, BottomSheetBehavior.STATE_COLLAPSED);
        }
    };

    @Override
    public void addFoodMenuListChip()
    {
        foodMenuListChip = new Chip(getContext());
        foodMenuListChip.setChecked(false);
        foodMenuListChip.setText(R.string.open_list);
        foodMenuListChip.setClickable(true);
        foodMenuListChip.setCheckable(false);
        foodMenuListChip.setTextColor(Color.BLACK);
        foodMenuListChip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                setStateOfBottomSheet(BottomSheetType.RESTAURANT, BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        foodMenuChipGroup.addView(foodMenuListChip, 0);
    }

    @Override
    public void setCurrentFoodMenuName(String foodMenuName)
    {
        this.selectedFoodMenu = foodMenuName;
        foodMenuChipMap.get(selectedFoodMenu).setChecked(true);
    }

    @Override
    public void onPageSelectedLocationItemBottomSheetViewPager(int position, MarkerType markerType)
    {
        super.onPageSelectedLocationItemBottomSheetViewPager(position, markerType);

        switch (markerType)
        {
            case SELECTED_PLACE_CATEGORY:
            {
                PlacesOfSelectedCategoriesFragment placesOfSelectedCategoriesFragment
                        = (PlacesOfSelectedCategoriesFragment) bottomSheetFragmentMap.get(BottomSheetType.SELECTED_PLACE_CATEGORY);

                if (placeCategoryChipGroup.getCheckedChipIds().size() >= 1)
                {
                    placesOfSelectedCategoriesFragment.loadExtraListData(selectedPlaceCategory, new RecyclerView.AdapterDataObserver()
                    {
                        @Override
                        public void onItemRangeInserted(int positionStart, int itemCount)
                        {
                            super.onItemRangeInserted(positionStart, itemCount);
                            addPoiItems(placeItemsGetter.getPlaceItems(selectedPlaceCategory), markerType);
                        }
                    });
                    return;
                }
                break;
            }

            case RESTAURANT:
            {
                if (foodMenuChipGroup.getCheckedChipIds().size() >= 1)
                {
                    restaurantOnExtraListDataListener.loadExtraListData(selectedFoodMenu, new RecyclerView.AdapterDataObserver()
                    {
                        @Override
                        public void onItemRangeInserted(int positionStart, int itemCount)
                        {
                            restaurantItemGetter.getRestaurants(selectedFoodMenu, new CarrierMessagingService.ResultCallback<List<PlaceDocuments>>()
                            {
                                @Override
                                public void onReceiveResult(@NonNull List<PlaceDocuments> placeDocuments) throws RemoteException
                                {
                                    requireActivity().runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            addPoiItems(placeDocuments, markerType);
                                        }
                                    });
                                }
                            });
                        }
                    });
                    return;
                }
                break;
            }
        }
    }


    static final class FoodChipViewHolder
    {
        String foodMenuName;
        int index;

        public FoodChipViewHolder(String foodMenuName, int index)
        {
            this.foodMenuName = foodMenuName;
            this.index = index;
        }
    }

    static final class PlaceCategoryChipViewHolder
    {
        PlaceCategoryDTO placeCategory;
        int index;

        public PlaceCategoryChipViewHolder(PlaceCategoryDTO placeCategory, int index)
        {
            this.placeCategory = placeCategory;
            this.index = index;
        }
    }

    public interface RestaurantsGetter
    {
        void getRestaurants(String foodName, CarrierMessagingService.ResultCallback<List<PlaceDocuments>> callback);
    }
}