package com.zerodsoft.scheduleweather.event.places.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.placecategory.viewmodel.PlaceCategoryViewModel;
import com.zerodsoft.scheduleweather.event.places.adapter.PlaceItemInMapViewAdapter;
import com.zerodsoft.scheduleweather.event.places.interfaces.IClickedPlaceItem;
import com.zerodsoft.scheduleweather.event.places.interfaces.IFragment;
import com.zerodsoft.scheduleweather.kakaomap.fragment.KakaoMapFragment;
import com.zerodsoft.scheduleweather.kakaomap.model.CustomPoiItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultMapFragment extends KakaoMapFragment implements IClickedPlaceItem
{
    public static final String TAG = "DefaultMapFragment";

    private static DefaultMapFragment instance;
    private final LocationDTO selectedLocation;

    private List<PlacesFragment.PlaceViewModelData> placeViewModelDataList;
    private List<PlaceCategoryDTO> placeCategoryList;
    private Map<PlaceCategoryDTO, PlaceItemInMapViewAdapter> adapterMap = new HashMap<>();
    private Map<PlaceCategoryDTO, Chip> chipMap = new HashMap<>();
    private PlaceCategoryDTO selectedPlaceCategory;

    private ChipGroup categoryChipGroup;

    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true)
    {
        @Override
        public void handleOnBackPressed()
        {
            getParentFragmentManager().popBackStack();
            onBackPressedCallback.remove();
        }
    };

    public static DefaultMapFragment newInstance(Fragment fragment, List<PlaceCategoryDTO> placeCategoryList, LocationDTO locationDTO)
    {
        instance = new DefaultMapFragment(fragment, placeCategoryList, locationDTO);
        return instance;
    }

    public static DefaultMapFragment getInstance()
    {
        return instance;
    }

    public static void close()
    {
        instance = null;
    }

    public DefaultMapFragment(Fragment fragment, List<PlaceCategoryDTO> placeCategoryList, LocationDTO locationDTO)
    {
        super();
        this.placeCategoryList = placeCategoryList;
        this.selectedLocation = locationDTO;
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
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

        HorizontalScrollView chipScrollView = new HorizontalScrollView(getContext());
        chipScrollView.setHorizontalScrollBarEnabled(false);
        CoordinatorLayout.LayoutParams chipLayoutParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        chipLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        chipScrollView.setLayoutParams(chipLayoutParams);
        binding.mapRootLayout.addView(chipScrollView);

        categoryChipGroup = new ChipGroup(getContext(), null, R.style.Widget_MaterialComponents_ChipGroup);
        categoryChipGroup.setSingleSelection(true);
        categoryChipGroup.setSingleLine(true);
        categoryChipGroup.setId(R.id.chip_group);
        categoryChipGroup.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        chipScrollView.addView(categoryChipGroup);

        //카테고리를 chip으로 표시
        int index = 0;
        for (PlaceCategoryDTO placeCategory : placeCategoryList)
        {
            PlaceItemInMapViewAdapter adapter = new PlaceItemInMapViewAdapter(placeCategory);
            adapter.submitList(placeViewModelDataList.get(index).getAdapter().getCurrentList());


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
                    ChipViewHolder chipViewHolder = (ChipViewHolder) buttonView.getTag();
                    PlaceCategoryDTO placeCategory = chipViewHolder.placeCategory;

                    //선택된 카테고리의 poiitem들을 표시
                    List<PlaceDocuments> placeDocuments = adapterMap.get(placeCategory).getCurrentList().snapshot();
                    removeAllPoiItems();
                    createPlacesPoiItems(placeDocuments);
                    mapView.fitMapViewAreaToShowAllPOIItems();
                }
            });

            final ChipViewHolder chipViewHolder = new ChipViewHolder(placeCategory, adapter, index++);
            chip.setTag(chipViewHolder);

            adapterMap.put(placeCategory, adapter);
            chipMap.put(placeCategory, chip);

            categoryChipGroup.addView(chip, new ChipGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        Chip chip = chipMap.get(selectedPlaceCategory);
        chip.setChecked(true);

    }

    public void setSelectedPlaceCategory(PlaceCategoryDTO selectedPlaceCategory)
    {
        this.selectedPlaceCategory = selectedPlaceCategory;
    }

    @Override
    public void onMapViewInitialized(MapView mapView)
    {
        super.onMapViewInitialized(mapView);

        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(selectedLocation.getLatitude(), selectedLocation.getLongitude());
        MapPOIItem poiItem = new MapPOIItem();
        poiItem.setItemName(selectedLocation.getPlaceName() != null ? selectedLocation.getPlaceName() : selectedLocation.getAddressName());
        poiItem.setMapPoint(mapPoint);
        poiItem.setTag(0);
        poiItem.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(poiItem);
        mapView.setMapCenterPointAndZoomLevel(mapPoint, 4, false);
    }

    public void setViewModel(List<PlacesFragment.PlaceViewModelData> placeViewModelDataList)
    {
        this.placeViewModelDataList = placeViewModelDataList;
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
        //   categoryButton.setText(placeCategory.getDescription());
        createPlacesPoiItems(placeDocumentsList);
        mapView.fitMapViewAreaToShowAllPOIItems();
    }

    public void updatePlaceItems(PlaceCategoryDTO placeCategory, PagedList<PlaceDocuments> pagedList)
    {
        adapterMap.get(placeCategory).submitList(pagedList);
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

}
