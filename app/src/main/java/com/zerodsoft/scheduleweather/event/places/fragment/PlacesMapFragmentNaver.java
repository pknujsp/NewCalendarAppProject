package com.zerodsoft.scheduleweather.event.places.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentOnAttachListener;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.NaverMap;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IstartActivity;
import com.zerodsoft.scheduleweather.event.places.interfaces.FragmentController;
import com.zerodsoft.scheduleweather.event.places.interfaces.OnClickedPlacesListListener;
import com.zerodsoft.scheduleweather.event.places.interfaces.PlaceCategory;
import com.zerodsoft.scheduleweather.event.places.interfaces.PlaceItemsGetter;
import com.zerodsoft.scheduleweather.kakaomap.bottomsheet.adapter.PlaceItemInMapViewAdapter;
import com.zerodsoft.scheduleweather.kakaomap.fragment.main.KakaoMapFragment;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchheader.MapHeaderSearchFragment;
import com.zerodsoft.scheduleweather.navermap.NaverMapFragment;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlacesMapFragmentNaver extends NaverMapFragment implements OnClickedPlacesListListener
{
    public static final String TAG = "PlacesMapFragmentNaver";

    private IstartActivity istartActivity;
    private final PlaceCategory placeCategory;
    private final FragmentController fragmentController;
    private PlaceItemsGetter placeItemsGetter;

    private List<PlaceCategoryDTO> placeCategoryList;
    private LocationDTO selectedLocationDto;
    private CoordToAddress coordToAddressResult;

    private ChipGroup categoryChipGroup;
    private Map<PlaceCategoryDTO, Chip> chipMap = new HashMap<>();
    private Chip listChip;

    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true)
    {
        @Override
        public void handleOnBackPressed()
        {
            binding.naverMapViewLayout.removeAllViews();
            requireActivity().finish();
            onBackPressedCallback.remove();
        }
    };

    public PlacesMapFragmentNaver(Fragment fragment)
    {
        super();
        this.placeCategory = (PlaceCategory) fragment;
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
        placeBottomSheetSelectBtnVisibility = View.GONE;
        placeBottomSheetUnSelectBtnVisibility = View.GONE;
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

        listChip = new Chip(getContext());
        listChip.setChecked(false);
        listChip.setText(R.string.open_list);
        listChip.setClickable(true);
        listChip.setCheckable(false);
        listChip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //리스트 열고, placeslistbottomsheet닫고, poiitem이 선택된 경우 선택해제
                fragmentController.replaceFragment(PlaceListFragment.TAG);
            }
        });

        categoryChipGroup.addView(listChip, 0);

        getChildFragmentManager().addFragmentOnAttachListener(new FragmentOnAttachListener()
        {
            @Override
            public void onAttachFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment)
            {
                if (fragment instanceof MapHeaderSearchFragment)
                {
                    if (categoryChipGroup.getCheckedChipId() != View.NO_ID)
                    {
                        categoryChipGroup.findViewById(categoryChipGroup.getCheckedChipId()).performClick();
                    }
                }
            }
        });
    }

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
                if (isSelectedPoiItem)
                {
                    deselectPoiItem();
                }
                setPlacesListAdapter(new PlaceItemInMapViewAdapter());

                PlaceCategoryDTO placeCategory = ((ChipViewHolder) compoundButton.getTag()).placeCategory;
                List<PlaceDocuments> placeDocumentsList = placeItemsGetter.getPlaceItems(placeCategory);

                createPlacesPoiItems(placeDocumentsList);
                showAllPoiItems();
            } else if (categoryChipGroup.getCheckedChipIds().isEmpty() && !markerList.isEmpty())
            {
                removeAllPoiItems();
                isSelectedPoiItem = false;
            }
            setPlacesListBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    };

    @Override
    public void onMapReady(@NonNull NaverMap naverMap)
    {
        super.onMapReady(naverMap);
        LatLng latLng = new LatLng(selectedLocationDto.getLatitude(), selectedLocationDto.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(latLng, 10);
        naverMap.moveCamera(cameraUpdate);
    }

    @Override
    public void onClickedItemInList(PlaceCategoryDTO placeCategory, int index)
    {
        fragmentController.replaceFragment(PlacesMapFragmentNaver.TAG);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        //create poi items
        chipMap.get(placeCategory).setChecked(true);
        //select poi item
        onPOIItemSelectedByList(index);
    }

    @Override
    public void onClickedMoreInList(PlaceCategoryDTO placeCategory)
    {
        fragmentController.replaceFragment(PlacesMapFragmentNaver.TAG);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        chipMap.get(placeCategory).setChecked(true);
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
