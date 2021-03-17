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
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IBottomSheet;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapToolbar;
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
    private Button listButton;

    private int lastBottomSheetState;
    private boolean isFirstItemSelected = false;


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

    public void setFirstItemSelected(boolean firstItemSelected)
    {
        isFirstItemSelected = firstItemSelected;
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
        //----------list button
        listButton = new MaterialButton(getContext());
        listButton.setText(R.string.open_list);
        listButton.setTextColor(Color.WHITE);
        listButton.setBackgroundColor(Color.GREEN);

        CoordinatorLayout.LayoutParams buttonLayoutParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        listButton.setLayoutParams(buttonLayoutParams);
        binding.mapRootLayout.addView(listButton);

        listButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //목록 열고(리스트), 닫기(맵)
                fragmentController.replaceFragment(PlaceListFragment.TAG);
                lastBottomSheetState = bottomSheetInterface.getBottomSheetState();

                if (lastBottomSheetState == BottomSheetBehavior.STATE_EXPANDED)
                {
                    bottomSheetInterface.setBottomSheetState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });
        //---------------------

        //-----------chip group
        HorizontalScrollView chipScrollView = new HorizontalScrollView(getContext());
        chipScrollView.setHorizontalScrollBarEnabled(false);
        CoordinatorLayout.LayoutParams chipLayoutParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        chipLayoutParams.gravity = Gravity.TOP;
        chipScrollView.setLayoutParams(chipLayoutParams);
        binding.mapRootLayout.addView(chipScrollView);

        categoryChipGroup = new ChipGroup(getContext(), null, R.style.Widget_MaterialComponents_ChipGroup);
        categoryChipGroup.setSingleSelection(true);
        categoryChipGroup.setSingleLine(true);
        categoryChipGroup.setId(R.id.chip_group);
        categoryChipGroup.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        chipScrollView.addView(categoryChipGroup);

        setChips();
    }

    public int getLastBottomSheetState()
    {
        return lastBottomSheetState;
    }

    @Override
    public void onClickedSearchView()
    {
        super.onClickedSearchView();
        //리스트 버튼과 chips, bottomsheet를 숨긴다
        listButton.setVisibility(View.GONE);
        categoryChipGroup.setVisibility(View.GONE);
        lastBottomSheetState = bottomSheetInterface.getBottomSheetState();
        bottomSheetInterface.setBottomSheetState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public void closeSearchView(int viewType)
    {
        super.closeSearchView(viewType);
        switch (viewType)
        {
            case IBottomSheet.SEARCH_VIEW:
                listButton.setVisibility(View.VISIBLE);
                categoryChipGroup.setVisibility(View.VISIBLE);
                bottomSheetInterface.setBottomSheetState(lastBottomSheetState);
                break;
        }
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
                if (isSelectedPoiItem)
                {
                    deselectPoiItem();
                    isFirstItemSelected = false;
                }

                PlaceCategoryDTO placeCategory = ((ChipViewHolder) compoundButton.getTag()).placeCategory;
                List<PlaceDocuments> placeDocumentsList = placeItemsGetter.getPlaceItems(placeCategory);
                bottomSheetInterface.setPlacesItems(placeDocumentsList);
                createPlacesPoiItems(placeDocumentsList);

                mapView.fitMapViewAreaToShowAllPOIItems();
            } else if (categoryChipGroup.getCheckedChipIds().isEmpty() && mapView.getPOIItems().length > 0)
            {
                removeAllPoiItems();
            }
            bottomSheetInterface.setBottomSheetState(BottomSheetBehavior.STATE_HIDDEN);

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

    public void onBottomSheetPageSelected(int index)
    {
        if (isFirstItemSelected)
        {
            selectedPoiItemIndex = index;
            isSelectedPoiItem = true;

            mapView.selectPOIItem(mapView.getPOIItems()[index], true);
            mapView.setMapCenterPoint(mapView.getPOIItems()[index].getMapPoint(), true);
        }
    }


    @Override
    public void onClickedItem(PlaceCategoryDTO placeCategory, int index)
    {
        fragmentController.replaceFragment(PlacesMapFragment.TAG);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        chipMap.get(placeCategory).setChecked(true);
        bottomSheetInterface.onClickedItem(index);
        bottomSheetInterface.setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED);
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
            isFirstItemSelected = false;
            bottomSheetInterface.setBottomSheetState(PlaceBottomSheetBehaviour.STATE_HIDDEN);
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
