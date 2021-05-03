package com.zerodsoft.scheduleweather.event.main;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

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
import com.zerodsoft.scheduleweather.navermap.PoiItemType;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnExtraListDataListener;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import org.xmlpull.v1.XmlPullParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewInstanceMainFragment extends NaverMapFragment implements NewFoodsMainFragment.FoodMenuChipsViewController
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
    private Map<String, Chip> placeCategoryChipMap = new HashMap<>();
    private Chip placeCategoryListChip;
    private Chip placeCategorySettingsChip;
    private PlaceCategoryDTO selectedPlaceCategory;

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

        Object[] results0 = createBottomSheet(R.id.instance_info_fragment_container);
        LinearLayout instanceInfoBottomSheet = (LinearLayout) results0[0];
        BottomSheetBehavior instanceInfoBottomSheetBehavior = (BottomSheetBehavior) results0[1];

        Object[] results1 = createBottomSheet(R.id.weather_fragment_container);
        LinearLayout weatherBottomSheet = (LinearLayout) results1[0];
        BottomSheetBehavior weatherBottomSheetBehavior = (BottomSheetBehavior) results1[1];

        Object[] results2 = createBottomSheet(R.id.restaurant_fragment_container);
        LinearLayout restaurantsBottomSheet = (LinearLayout) results2[0];
        BottomSheetBehavior restaurantsBottomSheetBehavior = (BottomSheetBehavior) results2[1];

        bottomSheetViewMap.put(BottomSheetType.INSTANCE_INFO, instanceInfoBottomSheet);
        bottomSheetViewMap.put(BottomSheetType.WEATHER, weatherBottomSheet);
        bottomSheetViewMap.put(BottomSheetType.RESTAURANT, restaurantsBottomSheet);
        bottomSheetBehaviorMap.put(BottomSheetType.INSTANCE_INFO, instanceInfoBottomSheetBehavior);
        bottomSheetBehaviorMap.put(BottomSheetType.WEATHER, weatherBottomSheetBehavior);
        bottomSheetBehaviorMap.put(BottomSheetType.RESTAURANT, restaurantsBottomSheetBehavior);

        instanceInfoBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
                switch (newState)
                {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        EventFragment eventFragment = (EventFragment) getChildFragmentManager().findFragmentByTag(EventFragment.TAG);
                        getChildFragmentManager().beginTransaction().show(eventFragment).addToBackStack(EventFragment.TAG).commit();
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

        weatherBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
                switch (newState)
                {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        WeatherItemFragment weatherItemFragment = (WeatherItemFragment) getChildFragmentManager().findFragmentByTag(WeatherItemFragment.TAG);
                        getChildFragmentManager().beginTransaction().show(weatherItemFragment).addToBackStack(WeatherItemFragment.TAG).commit();
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
                functionButton.callOnClick();

                final int HEADERBAR_HEIGHT = (int) getResources().getDimension(R.dimen.map_header_bar_height);
                final int HEADERBAR_TOP_MARGIN = (int) getResources().getDimension(R.dimen.map_header_bar_top_margin);
                final int HEADERBAR_MARGIN = (int) (HEADERBAR_TOP_MARGIN * 1.5f);
                final int DEFAULT_HEIGHT_OF_BOTTOMSHEET = binding.naverMapFragmentRootLayout.getHeight() - HEADERBAR_HEIGHT - HEADERBAR_MARGIN;

                setHeightOfBottomSheet(DEFAULT_HEIGHT_OF_BOTTOMSHEET, instanceInfoBottomSheet, instanceInfoBottomSheetBehavior);
                setHeightOfBottomSheet(DEFAULT_HEIGHT_OF_BOTTOMSHEET, weatherBottomSheet, weatherBottomSheetBehavior);
                setHeightOfBottomSheet(DEFAULT_HEIGHT_OF_BOTTOMSHEET, restaurantsBottomSheet, restaurantsBottomSheetBehavior);

                binding.naverMapFragmentRootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
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
                if (getChildFragmentManager().findFragmentByTag(EventFragment.TAG) == null)
                {
                    addEventFragmentIntoBottomSheet();
                }
                onCalledBottomSheet(BottomSheetBehavior.STATE_EXPANDED, bottomSheetBehaviorMap.get(BottomSheetType.INSTANCE_INFO));
                bottomSheetBehaviorMap.get(BottomSheetType.INSTANCE_INFO).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        functionButtons[1].setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //날씨
                functionButton.callOnClick();
                if (getChildFragmentManager().findFragmentByTag(WeatherItemFragment.TAG) == null)
                {
                    addWeatherFragmentIntoBottomSheet();
                }
                onCalledBottomSheet(BottomSheetBehavior.STATE_EXPANDED, bottomSheetBehaviorMap.get(BottomSheetType.WEATHER));
                bottomSheetBehaviorMap.get(BottomSheetType.WEATHER).setState(BottomSheetBehavior.STATE_EXPANDED);
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
                bottomSheetBehaviorMap.get(BottomSheetType.RESTAURANT).setState(BottomSheetBehavior.STATE_EXPANDED);
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

                                getActivity().runOnUiThread(new Runnable()
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

                                    getActivity().runOnUiThread(new Runnable()
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

        selectedLocationInEventMarker = new Marker(latLng);
        selectedLocationInEventMarker.setMap(naverMap);
        selectedLocationInEventMarker.setIcon(OverlayImage.fromResource(R.drawable.current_location_icon));
        selectedLocationInEventMarker.setForceShowIcon(true);
        selectedLocationInEventMarker.setCaptionColor(Color.BLUE);
        selectedLocationInEventMarker.setCaptionHaloColor(Color.rgb(200, 255, 200));
        selectedLocationInEventMarker.setCaptionTextSize(13f);
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

    private void addEventFragmentIntoBottomSheet()
    {
        EventFragment eventFragment = new EventFragment(this, this, CALENDAR_ID, EVENT_ID, INSTANCE_ID, ORIGINAL_BEGIN, ORIGINAL_END);
        getChildFragmentManager().beginTransaction()
                .add(bottomSheetViewMap.get(BottomSheetType.INSTANCE_INFO).getChildAt(0).getId(), eventFragment, EventFragment.TAG).hide(eventFragment).commitNow();
        bottomSheetFragmentMap.put(BottomSheetType.INSTANCE_INFO, eventFragment);
    }

    private void addWeatherFragmentIntoBottomSheet()
    {
        WeatherItemFragment weatherFragment = new WeatherItemFragment(this, this);
        Bundle bundle = new Bundle();
        bundle.putInt(CalendarContract.Instances.CALENDAR_ID, CALENDAR_ID);
        bundle.putLong(CalendarContract.Instances.EVENT_ID, EVENT_ID);
        bundle.putLong(CalendarContract.Instances._ID, INSTANCE_ID);
        bundle.putLong(CalendarContract.Instances.BEGIN, ORIGINAL_BEGIN);

        weatherFragment.setArguments(bundle);
        getChildFragmentManager().beginTransaction()
                .add(bottomSheetViewMap.get(BottomSheetType.WEATHER).getChildAt(0).getId(), weatherFragment, WeatherItemFragment.TAG).hide(weatherFragment).commitNow();
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

        HorizontalScrollView scrollView = binding.naverMapViewLayout.findViewById(R.id.chip_scroll_view);
        foodMenuChipGroup.removeAllViews();
        scrollView.removeAllViews();
        binding.naverMapViewLayout.removeView(scrollView);

        foodMenuChipGroup = null;
        foodMenuListChip = null;
        removePoiItems(PoiItemType.RESTAURANT);
    }

    @Override
    public void createFoodMenuChips()
    {
        //-----------chip group
        HorizontalScrollView chipScrollView = new HorizontalScrollView(getContext());
        chipScrollView.setId(R.id.chip_scroll_view);
        chipScrollView.setHorizontalScrollBarEnabled(false);

        RelativeLayout.LayoutParams chipLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        chipLayoutParams.addRule(RelativeLayout.BELOW, binding.naverMapHeaderBar.getRoot().getId());
        chipLayoutParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics());

        binding.naverMapViewLayout.addView(chipScrollView, chipLayoutParams);

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

            final ChipViewHolder chipViewHolder = new ChipViewHolder(menu, index++);
            chip.setTag(chipViewHolder);

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
                setLocationItemViewPagerAdapter(new LocationItemViewPagerAdapter(), PoiItemType.RESTAURANT);

                selectedFoodMenu = ((ChipViewHolder) compoundButton.getTag()).foodMenuName;
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
                                createPoiItems(placeDocuments, PoiItemType.RESTAURANT);
                                showPoiItems(PoiItemType.RESTAURANT);
                            }
                        });

                    }
                });
            } else if (foodMenuChipGroup.getCheckedChipIds().isEmpty())
            {
                removePoiItems(PoiItemType.RESTAURANT);
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
    public void onPageSelectedLocationItemBottomSheetViewPager(int position, PoiItemType poiItemType)
    {
        super.onPageSelectedLocationItemBottomSheetViewPager(position, poiItemType);

        switch (poiItemType)
        {
            case SELECTED_PLACE_CATEGORY:
            {
                PlacesOfSelectedCategoriesFragment placesOfSelectedCategoriesFragment
                        = (PlacesOfSelectedCategoriesFragment) bottomSheetFragmentMap.get(BottomSheetType.SELECTED_PLACE_CATEGORY);

                if (placesOfSelectedCategoriesFragment.isVisible() && placeCategoryChipGroup.getCheckedChipIds().size() >= 1)
                {
                    placesOfSelectedCategoriesFragment.loadExtraListData(selectedPlaceCategory, new RecyclerView.AdapterDataObserver()
                    {
                        @Override
                        public void onItemRangeInserted(int positionStart, int itemCount)
                        {
                            super.onItemRangeInserted(positionStart, itemCount);
                            addPoiItems(placeItemsGetter.getPlaceItems(selectedPlaceCategory), poiItemType);
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
                                            addPoiItems(placeDocuments, poiItemType);
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


    static final class ChipViewHolder
    {
        String foodMenuName;
        int index;

        public ChipViewHolder(String foodMenuName, int index)
        {
            this.foodMenuName = foodMenuName;
            this.index = index;
        }
    }

    public interface RestaurantsGetter
    {
        void getRestaurants(String foodName, CarrierMessagingService.ResultCallback<List<PlaceDocuments>> callback);
    }
}