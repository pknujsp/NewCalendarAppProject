package com.zerodsoft.scheduleweather.event.places.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.placecategory.viewmodel.PlaceCategoryViewModel;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IstartActivity;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocation;
import com.zerodsoft.scheduleweather.event.places.adapter.PlaceItemInMapViewAdapter;
import com.zerodsoft.scheduleweather.event.places.interfaces.IClickedPlaceItem;
import com.zerodsoft.scheduleweather.kakaomap.fragment.KakaoMapFragment;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlacesMapFragment extends KakaoMapFragment implements IClickedPlaceItem
{
    public static final String TAG = "PlacesMapFragment";

    private final ILocation iLocation;
    private final IstartActivity istartActivity;

    private PlaceCategoryViewModel placeCategoryViewModel;

    private List<PlaceCategoryDTO> placeCategoryList;
    private LocationDTO selectedLocationDto;

    private ChipGroup categoryChipGroup;
    private Map<PlaceCategoryDTO, Chip> chipMap = new HashMap<>();
    private PlaceListBottomSheetView placeListBottomSheetView;
    private BottomSheetBehavior newBottomSheetBehavior;
    private View customBottomSheet;

    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true)
    {
        @Override
        public void handleOnBackPressed()
        {
            onBackPressedCallback.remove();
            requireActivity().finish();
        }
    };

    public PlacesMapFragment(ILocation iLocation, IstartActivity istartActivity)
    {
        super();
        this.iLocation = iLocation;
        this.istartActivity = istartActivity;
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        requireActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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

        placeCategoryViewModel = new ViewModelProvider(this).get(PlaceCategoryViewModel.class);
        initLocation();

        Button typeButton = new MaterialButton(getContext());
        typeButton.setText(R.string.open_list);
        typeButton.setTextColor(Color.WHITE);
        typeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //목록 열고(리스트), 닫기(맵)
            }
        });

        CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(layoutParams);
        binding.mapRootLayout.addView(linearLayout);

        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics());

        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        buttonLayoutParams.leftMargin = margin;
        buttonLayoutParams.rightMargin = margin;
        typeButton.setLayoutParams(buttonLayoutParams);
        linearLayout.addView(typeButton);

        HorizontalScrollView chipScrollView = new HorizontalScrollView(getContext());
        chipScrollView.setHorizontalScrollBarEnabled(false);
        LinearLayout.LayoutParams chipLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        chipLayoutParams.weight = 1;
        chipLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        chipScrollView.setLayoutParams(chipLayoutParams);
        linearLayout.addView(chipScrollView);

        categoryChipGroup = new ChipGroup(getContext(), null, R.style.Widget_MaterialComponents_ChipGroup);
        categoryChipGroup.setSingleSelection(true);
        categoryChipGroup.setSingleLine(true);
        categoryChipGroup.setId(R.id.chip_group);
        categoryChipGroup.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        chipScrollView.addView(categoryChipGroup);

        //bottomsheet추가
        /*
        FrameLayout customBottomSheet = (FrameLayout) getLayoutInflater().inflate(R.layout.fragment_place_list_bottom_sheet, null);
        binding.mapRootLayout.addView(customBottomSheet);

        placeListBottomSheetView = new PlaceListBottomSheetView(customBottomSheet);
        newBottomSheetBehavior = BottomSheetBehavior.from(customBottomSheet.getRootView());
        newBottomSheetBehavior.setDraggable(false);
        newBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

         */
    }

    private void initLocation()
    {
        iLocation.getLocation(new CarrierMessagingService.ResultCallback<LocationDTO>()
        {
            @Override
            public void onReceiveResult(@NonNull LocationDTO location) throws RemoteException
            {
                if (location.getId() >= 0)
                {
                    selectedLocationDto = location;

                    placeCategoryViewModel.selectConvertedSelected(new CarrierMessagingService.ResultCallback<List<PlaceCategoryDTO>>()
                    {
                        @Override
                        public void onReceiveResult(@NonNull List<PlaceCategoryDTO> result) throws RemoteException
                        {
                            placeCategoryList = result;

                            getActivity().runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    //카테고리를 chip으로 표시
                                    int index = 0;
                                    for (PlaceCategoryDTO placeCategory : placeCategoryList)
                                    {
                                        PlaceItemInMapViewAdapter adapter = new PlaceItemInMapViewAdapter(placeCategory);

                                        Chip chip = new Chip(getContext(), null, R.style.Widget_MaterialComponents_Chip_Choice);
                                        chip.setChecked(false);
                                        chip.setText(placeCategory.getDescription());
                                        chip.setClickable(true);
                                        chip.setVisibility(View.VISIBLE);
                                        chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                                        {
                                            @Override
                                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                                            {
                    /*
                    ChipViewHolder chipViewHolder = (ChipViewHolder) buttonView.getTag();
                    PlaceCategoryDTO placeCategory = chipViewHolder.placeCategory;

                    //선택된 카테고리의 poiitem들을 표시
                    List<PlaceDocuments> placeDocuments = adapter.getCurrentList().snapshot();
                    removeAllPoiItems();
                    createPlacesPoiItems(placeDocuments);
                    mapView.fitMapViewAreaToShowAllPOIItems();


                     */
                                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                            }
                                        });

                                        final ChipViewHolder chipViewHolder = new ChipViewHolder(placeCategory, adapter, index++);
                                        chip.setTag(chipViewHolder);

                                        chipMap.put(placeCategory, chip);
                                        categoryChipGroup.addView(chip, new ChipGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }


    @Override
    public void onMapViewInitialized(MapView mapView)
    {
        super.onMapViewInitialized(mapView);

        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(selectedLocationDto.getLatitude(), selectedLocationDto.getLongitude());
        MapPOIItem poiItem = new MapPOIItem();
        poiItem.setItemName(selectedLocationDto.getPlaceName() != null ? selectedLocationDto.getPlaceName() : selectedLocationDto.getAddressName());
        poiItem.setMapPoint(mapPoint);
        poiItem.setTag(0);
        poiItem.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(poiItem);
        mapView.setMapCenterPointAndZoomLevel(mapPoint, 4, false);
    }


    @Override
    public void onClickedItem(int index, PlaceCategoryDTO placeCategory, List<PlaceDocuments> placeDocumentsList)
    {
        // iFragment.replaceFragment(MorePlacesFragment.TAG);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        //  categoryButton.setText(placeCategory.getDescription());
        createPlacesPoiItems(placeDocumentsList);
        selectPoiItem(index);
    }

    @Override
    public void onClickedMore(PlaceCategoryDTO placeCategory, List<PlaceDocuments> placeDocumentsList)
    {
        //  iFragment.replaceFragment(MorePlacesFragment.TAG);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        //  categoryButton.setText(placeCategory.getDescription());
        createPlacesPoiItems(placeDocumentsList);
        mapView.fitMapViewAreaToShowAllPOIItems();
    }

    static final class ChipViewHolder
    {
        PlaceCategoryDTO placeCategory;
        PlaceItemInMapViewAdapter adapter;
        int index;

        public ChipViewHolder(PlaceCategoryDTO placeCategory, PlaceItemInMapViewAdapter adapter, int index)
        {
            this.placeCategory = placeCategory;
            this.adapter = adapter;
            this.index = index;
        }
    }


    static final class CustomBottomSheet extends FrameLayout
    {
        public CustomBottomSheet(@NonNull Context context)
        {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev)
        {
            getParent().requestDisallowInterceptTouchEvent(true);
            return super.dispatchTouchEvent(ev);
        }
    }

}
