package com.zerodsoft.scheduleweather.event.places.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IstartActivity;
import com.zerodsoft.scheduleweather.event.places.adapter.PlaceItemInMapViewAdapter;
import com.zerodsoft.scheduleweather.event.places.bottomsheet.PlaceBottomSheetBehaviour;
import com.zerodsoft.scheduleweather.event.places.interfaces.BottomSheet;
import com.zerodsoft.scheduleweather.event.places.interfaces.FragmentController;
import com.zerodsoft.scheduleweather.event.places.interfaces.OnClickedPlacesListListener;
import com.zerodsoft.scheduleweather.event.places.interfaces.PlaceCategory;
import com.zerodsoft.scheduleweather.event.places.interfaces.PlaceItemsGetter;
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

public class PlacesMapFragment extends KakaoMapFragment implements OnClickedPlacesListListener
{
    public static final String TAG = "PlacesMapFragment";

    private IstartActivity istartActivity;
    private final BottomSheet bottomSheetInterface;
    private final PlaceCategory placeCategory;
    private final FragmentController fragmentController;
    private PlaceItemsGetter placeItemsGetter;

    private List<PlaceCategoryDTO> placeCategoryList;
    private LocationDTO selectedLocationDto;
    private CoordToAddress coordToAddressResult;

    private ChipGroup categoryChipGroup;
    private Map<PlaceCategoryDTO, Chip> chipMap = new HashMap<>();

    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true)
    {
        @Override
        public void handleOnBackPressed()
        {
            onBackPressedCallback.remove();
            requireActivity().finish();
        }
    };

    public PlacesMapFragment(Fragment fragment)
    {
        super();
        this.placeCategory = (PlaceCategory) fragment;
        this.bottomSheetInterface = (BottomSheet) fragment;
        this.fragmentController = (FragmentController) fragment;
    }

    public void setIstartActivity(IstartActivity istartActivity)
    {
        this.istartActivity = istartActivity;
    }

    public void setSelectedLocationDto(LocationDTO selectedLocationDto)
    {
        this.selectedLocationDto = selectedLocationDto;
    }

    public void setPlaceItemsGetter(PlaceItemsGetter placeItemsGetter)
    {
        this.placeItemsGetter = placeItemsGetter;
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

        Button typeButton = new MaterialButton(getContext());
        typeButton.setText(R.string.open_list);
        typeButton.setTextColor(Color.WHITE);
        typeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //목록 열고(리스트), 닫기(맵)
                fragmentController.replaceFragment(PlaceListFragment.TAG);
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

        setChips();
    }

    private void setChips()
    {
        placeCategoryList = placeCategory.getPlaceCategoryList();
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

            chipMap.put(placeCategory, chip);
            categoryChipGroup.addView(chip, new ChipGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
        {
            if (isChecked)
            {
                PlaceCategoryDTO placeCategory = ((ChipViewHolder) compoundButton.getTag()).placeCategory;
                List<PlaceDocuments> placeDocumentsList = placeItemsGetter.getPlaceItems(placeCategory);
                bottomSheetInterface.setPlacesItems(placeDocumentsList);
                createPlacesPoiItems(placeDocumentsList);

                mapView.fitMapViewAreaToShowAllPOIItems();
            } else
            {
                bottomSheetInterface.setBottomSheetState(BottomSheetBehavior.STATE_HIDDEN);
                removeAllPoiItems();
            }
        }
    };


    static class HorizontalMarginItemDecoration extends RecyclerView.ItemDecoration
    {
        private int horizontalMarginInPx;

        public HorizontalMarginItemDecoration(Context context)
        {
            horizontalMarginInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42f, context.getResources().getDisplayMetrics());
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
        {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.left = horizontalMarginInPx;
            outRect.right = horizontalMarginInPx;
        }
    }


    @Override
    public void onMapViewInitialized(MapView mapView)
    {
        super.onMapViewInitialized(mapView);
        MapPoint selectedLocationMapPoint = MapPoint.mapPointWithGeoCoord(selectedLocationDto.getLatitude(), selectedLocationDto.getLongitude());
        mapView.setMapCenterPointAndZoomLevel(selectedLocationMapPoint, 4, false);
    }


    @Override
    public void onClickedItem(PlaceCategoryDTO placeCategory, int index)
    {
        /*
        fragmentController.replaceFragment(PlacesMapFragment.TAG);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        //  categoryButton.setText(placeCategory.getDescription());
        createPlacesPoiItems(placeDocumentsList);
        selectPoiItem(index);

         */
    }

    @Override
    public void onClickedMore(PlaceCategoryDTO placeCategory)
    {
        fragmentController.replaceFragment(PlacesMapFragment.TAG);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        chipMap.get(placeCategory).setChecked(true);
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem)
    {
        // super.onPOIItemSelected(mapView, mapPOIItem);
        selectedPoiItemIndex = mapPOIItem.getTag();
        isSelectedPoiItem = true;

        // poiitem을 선택하였을 경우에 수행됨
        mapView.setMapCenterPoint(mapPOIItem.getMapPoint(), true);
        bottomSheetInterface.setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetInterface.onClickedItem(selectedPoiItemIndex);
    }


    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint)
    {
        if (isSelectedPoiItem)
        {
            deselectPoiItem();
            bottomSheetInterface.setBottomSheetState(PlaceBottomSheetBehaviour.STATE_COLLAPSED);
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
