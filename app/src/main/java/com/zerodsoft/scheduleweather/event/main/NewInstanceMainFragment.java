package com.zerodsoft.scheduleweather.event.main;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.databinding.InstanceMainActivityBinding;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.event.fragments.EventFragment;
import com.zerodsoft.scheduleweather.event.places.map.PlacesOfSelectedCategoriesFragment;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.kakaomap.model.CoordToAddressUtil;
import com.zerodsoft.scheduleweather.navermap.NaverMapFragment;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import org.xmlpull.v1.XmlPullParser;

public class NewInstanceMainFragment extends NaverMapFragment
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
    private LocationDTO selectedLocationDto;
    private TextView[] functionButtons;
    private ImageView functionButton;
    private Marker selectedLocationMarker;

    private BottomSheetBehavior instanceInfoBottomSheetBehavior;
    private BottomSheetBehavior weatherBottomSheetBehavior;
    private BottomSheetBehavior restaurantsBottomSheetBehavior;

    private LinearLayout instanceInfoBottomSheet;
    private LinearLayout weatherBottomSheet;
    private LinearLayout restaurantsBottomSheet;

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

        Object[] results = createBottomSheet();
        instanceInfoBottomSheet = (LinearLayout) results[0];
        instanceInfoBottomSheetBehavior = (BottomSheetBehavior) results[1];

        results = createBottomSheet();
        weatherBottomSheet = (LinearLayout) results[0];
        weatherBottomSheetBehavior = (BottomSheetBehavior) results[1];

        results = createBottomSheet();
        restaurantsBottomSheet = (LinearLayout) results[0];
        restaurantsBottomSheetBehavior = (BottomSheetBehavior) results[1];

        addEventFragmentIntoBottomSheet();
        //   addWeatherFragmentIntoBottomSheet();
        //   addRestaurantFragmentIntoBottomSheet();

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
                instanceInfoBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        functionButtons[1].setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //날씨
                functionButton.callOnClick();
            }
        });

        functionButtons[2].setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //음식점
                functionButton.callOnClick();
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
                            if (selectedLocationDto == null)
                            {
                                selectedLocationDto = locationDTO;

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
                                if (locationDTO.equals(selectedLocationDto))
                                {
                                } else
                                {
                                    selectedLocationDto = locationDTO;
                                    selectedLocationMarker.setMap(null);
                                    selectedLocationMarker = null;

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
        LatLng latLng = new LatLng(selectedLocationDto.getLatitude(), selectedLocationDto.getLongitude());
        selectedLocationMarker = new Marker(latLng);
        selectedLocationMarker.setMap(naverMap);
        selectedLocationMarker.setIcon(OverlayImage.fromResource(R.drawable.current_location_icon));
        selectedLocationMarker.setForceShowIcon(true);

        CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(latLng, 12);
        naverMap.moveCamera(cameraUpdate);
    }

    private Object[] createBottomSheet()
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
        fragmentContainerView.setId(R.id.fragment_container);
        fragmentContainerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        bottomSheetView.addView(fragmentContainerView);

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
        bottomSheetBehavior.setDraggable(false);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {

            }
        });

        return new Object[]{bottomSheetView, bottomSheetBehavior};
    }

    private void addEventFragmentIntoBottomSheet()
    {
        EventFragment eventFragment = new EventFragment(CALENDAR_ID, EVENT_ID, INSTANCE_ID, ORIGINAL_BEGIN, ORIGINAL_END);
        getChildFragmentManager().beginTransaction()
                .add(instanceInfoBottomSheet.getChildAt(0).getId(), eventFragment, EventFragment.TAG).commit();
    }

    private void addWeatherFragmentIntoBottomSheet()
    {
        EventFragment eventFragment = new EventFragment(CALENDAR_ID, EVENT_ID, INSTANCE_ID, ORIGINAL_BEGIN, ORIGINAL_END);
        getChildFragmentManager().beginTransaction()
                .add(weatherBottomSheet.getChildAt(0).getId(), eventFragment, EventFragment.TAG).commit();
    }

    private void addRestaurantFragmentIntoBottomSheet()
    {
        EventFragment eventFragment = new EventFragment(CALENDAR_ID, EVENT_ID, INSTANCE_ID, ORIGINAL_BEGIN, ORIGINAL_END);
        getChildFragmentManager().beginTransaction()
                .add(restaurantsBottomSheet.getChildAt(0).getId(), eventFragment, EventFragment.TAG).commit();
    }

}