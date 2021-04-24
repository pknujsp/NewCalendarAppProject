package com.zerodsoft.scheduleweather.event.places.map;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.placecategory.activity.PlaceCategoryActivity;
import com.zerodsoft.scheduleweather.activity.placecategory.viewmodel.PlaceCategoryViewModel;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.main.InstanceMainActivity;
import com.zerodsoft.scheduleweather.event.places.interfaces.OnClickedPlacesListListener;
import com.zerodsoft.scheduleweather.event.places.interfaces.PlaceCategory;
import com.zerodsoft.scheduleweather.event.places.interfaces.PlaceItemsGetter;
import com.zerodsoft.scheduleweather.kakaomap.bottomsheet.adapter.PlaceItemInMapViewAdapter;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchheader.MapHeaderSearchFragment;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchresult.LocationSearchResultFragment;
import com.zerodsoft.scheduleweather.kakaomap.model.CoordToAddressUtil;
import com.zerodsoft.scheduleweather.navermap.NaverMapFragment;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import org.xmlpull.v1.XmlPullParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlacesMapFragmentNaver extends NaverMapFragment implements OnClickedPlacesListListener, OnBackPressedCallbackController, PlaceCategory
{
    public static final String TAG = "PlacesMapFragmentNaver";

    private CoordToAddress coordToAddressResult;
    private LocationDTO selectedLocationDto;
    private List<PlaceCategoryDTO> placeCategoryList;
    private PlaceCategoryViewModel placeCategoryViewModel;

    private LocationViewModel locationViewModel;
    private PlaceItemsGetter placeItemsGetter;

    private ChipGroup categoryChipGroup;
    private Map<String, Chip> chipMap = new HashMap<>();
    private Chip listChip;
    private Chip settingsChip;

    private LinearLayout placeCategoryBottomSheet;
    private BottomSheetBehavior placeCategoryBottomSheetBehavior;
    private PlacesOfSelectedCategoriesFragment placesOfSelectedCategoriesFragment;
    private PlaceCategoryDTO selectedPlaceCategory;

    private final ContentValues INSTANCE_VALUES = new ContentValues();
    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true)
    {
        @Override
        public void handleOnBackPressed()
        {
            if (placeCategoryBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED)
            {
                placeCategoryBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else
            {
                requireActivity().finish();
            }
        }
    };

    public PlacesMapFragmentNaver()
    {
        super();
    }


    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        addOnBackPressedCallback();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        removeOnBackPressedCallback();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        placeBottomSheetSelectBtnVisibility = View.GONE;
        placeBottomSheetUnSelectBtnVisibility = View.GONE;

        Bundle bundle = getArguments();

        INSTANCE_VALUES.put(CalendarContract.Instances.CALENDAR_ID, bundle.getInt(CalendarContract.Instances.CALENDAR_ID));
        INSTANCE_VALUES.put(CalendarContract.Instances.EVENT_ID, bundle.getLong(CalendarContract.Instances.EVENT_ID));
        INSTANCE_VALUES.put(CalendarContract.Instances._ID, bundle.getLong(CalendarContract.Instances._ID));
        INSTANCE_VALUES.put(CalendarContract.Instances.BEGIN, bundle.getLong(CalendarContract.Instances.BEGIN));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        placeCategoryViewModel = new ViewModelProvider(this).get(PlaceCategoryViewModel.class);

        //-----------chip group
        HorizontalScrollView chipScrollView = new HorizontalScrollView(getContext());
        chipScrollView.setHorizontalScrollBarEnabled(false);
        RelativeLayout.LayoutParams chipLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        chipLayoutParams.addRule(RelativeLayout.BELOW, binding.naverMapHeaderBar.getRoot().getId());
        chipLayoutParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics());
        chipScrollView.setLayoutParams(chipLayoutParams);
        binding.naverMapViewLayout.addView(chipScrollView);

        categoryChipGroup = new ChipGroup(getContext(), null, R.style.Widget_MaterialComponents_ChipGroup);
        categoryChipGroup.setSingleSelection(true);
        categoryChipGroup.setSingleLine(true);
        categoryChipGroup.setId(R.id.chip_group);
        categoryChipGroup.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        chipScrollView.addView(categoryChipGroup);

        setPlaceCategoryBottomSheet();
        addListChip();
        addSettingsChip();
        initLocation();

        binding.naverMapFragmentRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                //search bottom sheet 크기 조정
                int headerBarHeight = (int) getResources().getDimension(R.dimen.map_header_bar_height);
                int headerBarTopMargin = (int) getResources().getDimension(R.dimen.map_header_bar_top_margin);
                int headerBarMargin = (int) (headerBarTopMargin * 1.5f);

                int placeCategoryBottomSheetHeight = binding.naverMapFragmentRootLayout.getHeight() - headerBarHeight - headerBarMargin;
                //placecategory list bottom sheet 크기 조정
                int extraHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 68f, getContext().getResources().getDisplayMetrics());

                placeCategoryBottomSheet.getLayoutParams().height = placeCategoryBottomSheetHeight - extraHeight;
                placeCategoryBottomSheet.requestLayout();
                placeCategoryBottomSheetBehavior.onLayoutChild(binding.naverMapFragmentRootLayout, placeCategoryBottomSheet, ViewCompat.LAYOUT_DIRECTION_LTR);

                binding.naverMapFragmentRootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    private void initLocation()
    {
        locationViewModel.getLocation(INSTANCE_VALUES.getAsInteger(CalendarContract.Instances.CALENDAR_ID)
                , INSTANCE_VALUES.getAsLong(CalendarContract.Instances.EVENT_ID), new CarrierMessagingService.ResultCallback<LocationDTO>()
                {
                    @Override
                    public void onReceiveResult(@NonNull LocationDTO location) throws RemoteException
                    {
                        if (!location.isEmpty())
                        {
                            selectedLocationDto = location;
                            setPlaceCategoryList();
                        }
                    }
                });
    }

    private void setPlaceCategoryList()
    {
        placeCategoryViewModel.selectConvertedSelected(new CarrierMessagingService.ResultCallback<List<PlaceCategoryDTO>>()
        {
            @Override
            public void onReceiveResult(@NonNull List<PlaceCategoryDTO> result) throws RemoteException
            {
                placeCategoryList = result;
                //  coordToAddress();
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        setCategoryChips();
                    }
                });
            }
        });
    }

    private void coordToAddress()
    {
        LocalApiPlaceParameter localApiPlaceParameter = new LocalApiPlaceParameter();
        localApiPlaceParameter.setX(String.valueOf(selectedLocationDto.getLongitude()));
        localApiPlaceParameter.setY(String.valueOf(selectedLocationDto.getLatitude()));

        CoordToAddressUtil.coordToAddress(localApiPlaceParameter, new CarrierMessagingService.ResultCallback<DataWrapper<CoordToAddress>>()
        {
            @Override
            public void onReceiveResult(@NonNull DataWrapper<CoordToAddress> coordToAddressDataWrapper) throws RemoteException
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (coordToAddressDataWrapper.getException() == null)
                        {
                            coordToAddressResult = coordToAddressDataWrapper.getData();
                        } else
                        {
                            Toast.makeText(getActivity(), coordToAddressDataWrapper.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    @Override
    public void onPageSelectedPlaceBottomSheetViewPager(int position)
    {
        if (adapter.getItemCount() - 1 == position)
        {
            FragmentManager fragmentManager = getChildFragmentManager();

            PlacesOfSelectedCategoriesFragment placesOfSelectedCategoriesFragment
                    = (PlacesOfSelectedCategoriesFragment) fragmentManager.findFragmentByTag(PlacesOfSelectedCategoriesFragment.TAG);

            if (placesOfSelectedCategoriesFragment != null)
            {
                if (placesOfSelectedCategoriesFragment.isVisible() && categoryChipGroup.getCheckedChipIds().size() >= 1)
                {
                    placesOfSelectedCategoriesFragment.loadExtraListData(selectedPlaceCategory, new RecyclerView.AdapterDataObserver()
                    {
                        @Override
                        public void onItemRangeInserted(int positionStart, int itemCount)
                        {
                            super.onItemRangeInserted(positionStart, itemCount);
                            addPoiItems(placeItemsGetter.getPlaceItems(selectedPlaceCategory));
                        }
                    });
                    return;
                }
            }

        }

        super.onPageSelectedPlaceBottomSheetViewPager(position);
    }

    private void setPlaceCategoryBottomSheet()
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
        placeCategoryBottomSheet = new LinearLayout(getContext(), attr);

        CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setBehavior(new BottomSheetBehavior());
        placeCategoryBottomSheet.setLayoutParams(layoutParams);

        placeCategoryBottomSheet.setClickable(true);
        placeCategoryBottomSheet.setOrientation(LinearLayout.VERTICAL);

        binding.naverMapFragmentRootLayout.addView(placeCategoryBottomSheet);

        //fragmentcontainerview 추가
        FragmentContainerView fragmentContainerView = new FragmentContainerView(getContext());
        fragmentContainerView.setId(R.id.fragment_container);
        fragmentContainerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        fragmentContainerView.setPadding(0, getResources().getDimensionPixelSize(R.dimen.corner_radius), 0, 0);
        placeCategoryBottomSheet.addView(fragmentContainerView);

        placeCategoryBottomSheetBehavior = BottomSheetBehavior.from(placeCategoryBottomSheet);
        placeCategoryBottomSheetBehavior.setDraggable(false);
        placeCategoryBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        placeCategoryBottomSheetBehavior.setHideable(false);
        placeCategoryBottomSheetBehavior.setPeekHeight(0);
        placeCategoryBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
                if (newState == BottomSheetBehavior.STATE_EXPANDED)
                {
                    listChip.setText(R.string.close_list);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                {
                    listChip.setText(R.string.open_list);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {

            }
        });

        addPlaceListFragment();
    }

    private void addPlaceListFragment()
    {
        placesOfSelectedCategoriesFragment = new PlacesOfSelectedCategoriesFragment(INSTANCE_VALUES, this);
        // placeListFragment.setOnClickedPlacesListListener(placesMapFragmentKakao);

        // placesMapFragmentKakao.setPlaceItemsGetter(placeListFragment);
        placeItemsGetter = placesOfSelectedCategoriesFragment;

        getChildFragmentManager().beginTransaction()
                .add(placeCategoryBottomSheet.getChildAt(0).getId(), placesOfSelectedCategoriesFragment, PlacesOfSelectedCategoriesFragment.TAG).commitNow();
    }

    private void setCategoryChips()
    {
        if (categoryChipGroup.getChildCount() >= 3)
        {
            categoryChipGroup.removeViews(2, categoryChipGroup.getChildCount() - 2);
        }
        chipMap.clear();

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
            chip.setOnCheckedChangeListener(onCheckedChangeListener);

            final ChipViewHolder chipViewHolder = new ChipViewHolder(placeCategory, index++);
            chip.setTag(chipViewHolder);

            chipMap.put(placeCategory.getCode(), chip);
            categoryChipGroup.addView(chip, new ChipGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }


    }

    private void addListChip()
    {
        listChip = new Chip(getContext());
        listChip.setChecked(false);
        listChip.setText(R.string.open_list);
        listChip.setClickable(true);
        listChip.setCheckable(false);
        listChip.setTextColor(Color.BLACK);
        listChip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                placeCategoryBottomSheetBehavior.setState(placeCategoryBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED
                        ? BottomSheetBehavior.STATE_EXPANDED : BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        categoryChipGroup.addView(listChip, 0);
    }

    private void addSettingsChip()
    {
        settingsChip = new Chip(getContext());
        settingsChip.setChecked(false);
        settingsChip.setText(R.string.app_settings);
        settingsChip.setClickable(true);
        settingsChip.setCheckable(false);
        settingsChip.setTextColor(Color.BLACK);
        settingsChip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                placeCategoryActivityResultLauncher.launch(new Intent(getActivity(), PlaceCategoryActivity.class));
            }
        });

        categoryChipGroup.addView(settingsChip, 1);
    }

    private final ActivityResultLauncher<Intent> placeCategoryActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() == InstanceMainActivity.RESULT_EDITED_PLACE_CATEGORY)
                    {
                        if (categoryChipGroup.getCheckedChipIds().size() > 0)
                        {
                            chipMap.get(selectedPlaceCategory.getCode()).setChecked(false);
                        }
                        setPlaceCategoryList();
                        ((PlacesOfSelectedCategoriesFragment) getChildFragmentManager().findFragmentByTag(PlacesOfSelectedCategoriesFragment.TAG)).makeCategoryListView();
                    }
                }
            });

    private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener()
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
                        ((MapHeaderSearchFragment) getChildFragmentManager().findFragmentByTag(MapHeaderSearchFragment.TAG)).getBinding().closeButton.callOnClick();
                    }
                }

                if (isSelectedPoiItem)
                {
                    deselectPoiItem();
                }
                setPlacesListAdapter(new PlaceItemInMapViewAdapter());

                PlaceCategoryDTO placeCategory = ((ChipViewHolder) compoundButton.getTag()).placeCategory;
                List<PlaceDocuments> placeDocumentsList = placeItemsGetter.getPlaceItems(placeCategory);
                selectedPlaceCategory = placeCategory;

                createPoiItems(placeDocumentsList);
                showAllPoiItems();
            } else if (categoryChipGroup.getCheckedChipIds().isEmpty() && !markerList.isEmpty())
            {
                removeAllPoiItems();
                isSelectedPoiItem = false;
                selectedPlaceCategory = null;
            }
            setPlacesListBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    };


    @Override
    public void onMapReady(@NonNull NaverMap naverMap)
    {
        super.onMapReady(naverMap);
        LatLng latLng = new LatLng(selectedLocationDto.getLatitude(), selectedLocationDto.getLongitude());
        Marker marker = new Marker(latLng);
        marker.setMap(naverMap);
        marker.setIcon(OverlayImage.fromResource(R.drawable.current_location_icon));
        marker.setForceShowIcon(true);

        CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(latLng, 12);
        naverMap.moveCamera(cameraUpdate);
    }

    @Override
    public void onClickedItemInList(PlaceCategoryDTO placeCategory, int index)
    {
        listChip.callOnClick();
        //create poi items
        chipMap.get(placeCategory.getCode()).setChecked(true);
        //select poi item
        onPOIItemSelectedByList(index);
    }

    @Override
    public void onClickedMoreInList(PlaceCategoryDTO placeCategory)
    {
        listChip.callOnClick();
        chipMap.get(placeCategory.getCode()).setChecked(true);
    }

    @Override
    public void addOnBackPressedCallback()
    {
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public void removeOnBackPressedCallback()
    {
        onBackPressedCallback.remove();
    }

    @Override
    public List<PlaceCategoryDTO> getPlaceCategoryList()
    {
        return placeCategoryList;
    }

    @Override
    public void search(String query)
    {
        super.search(query);
        if (categoryChipGroup.getCheckedChipIds().size() > 0)
        {
            chipMap.get(selectedPlaceCategory.getCode()).setChecked(false);
        }
    }

    static final class ChipViewHolder
    {
        PlaceCategoryDTO placeCategory;
        int index;

        public ChipViewHolder(PlaceCategoryDTO placeCategory, int index)
        {
            this.placeCategory = placeCategory;
            this.index = index;
        }
    }


}
