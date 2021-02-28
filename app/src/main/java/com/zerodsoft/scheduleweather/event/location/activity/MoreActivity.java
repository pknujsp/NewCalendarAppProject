package com.zerodsoft.scheduleweather.event.location.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.kakaomap.activity.KakaoMapActivity;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MoreActivity extends KakaoMapActivity
{
    private Map<PlaceCategoryDTO, List<PlaceDocuments>> map;
    private ChipGroup chipGroup;
    private String selectedCategoryDescription;

    public MoreActivity()
    {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        selectedCategoryDescription = bundle.getString("selectedCategoryDescription");
        map = (HashMap<PlaceCategoryDTO, List<PlaceDocuments>>) bundle.getSerializable("map");

        binding.appbarLayout.setVisibility(View.GONE);
        kakaoMapFragment.getGpsButton().setVisibility(View.VISIBLE);

        chipGroup = new ChipGroup(getApplicationContext());
        chipGroup.setSingleSelection(true);
        chipGroup.setSingleLine(true);
        chipGroup.setSelectionRequired(true);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.TOP;
        binding.mapCoordinatorLayout.addView(chipGroup, layoutParams);

        LayoutInflater layoutInflater = getLayoutInflater();

        Set<PlaceCategoryDTO> placeCategorySet = map.keySet();
        for (PlaceCategoryDTO category : placeCategorySet)
        {
            View child = layoutInflater.inflate(R.layout.choicechip, chipGroup, false);
            ((Chip) child).setText(category.getDescription());

            final CategoryViewHolder viewHolder = new CategoryViewHolder();
            viewHolder.placeCategoryDTO = category;
            viewHolder.placeDocuments = map.get(category);
            child.setTag(viewHolder);
            child.setOnClickListener(chipOnClickListener);
            chipGroup.addView(child);
        }

        selectChip();
    }

    private final View.OnClickListener chipOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            /*
            이미 선택된 카테고리 인 경우 : 모든 아이템이 화면에 들어오도록 조정
            다른 카테고리 인 경우 : 해당 카테고리으로 변경
             */
            CategoryViewHolder viewHolder = (CategoryViewHolder) view.getTag();

            if (viewHolder.placeCategoryDTO.getDescription().equals(((Chip) view).getText().toString()))
            {
                kakaoMapFragment.showAllPoiItems();
            } else
            {
                selectedCategoryDescription = viewHolder.placeCategoryDTO.getDescription();
                refreshPlaceItems(viewHolder.placeCategoryDTO);
            }
        }
    };

    public void selectChip()
    {
        int chipsCount = chipGroup.getChildCount();
        for (int i = 0; i < chipsCount; i++)
        {
            if (((Chip) chipGroup.getChildAt(i)).getText().equals(selectedCategoryDescription))
            {
                ((Chip) chipGroup.getChildAt(i)).performClick();
                break;
            }
        }
    }


    public void refreshPlaceItems(PlaceCategoryDTO placeCategoryDTO)
    {
        kakaoMapFragment.createPlacesPoiItems(map.get(placeCategoryDTO));
        kakaoMapFragment.showAllPoiItems();
    }

    static final class CategoryViewHolder
    {
        PlaceCategoryDTO placeCategoryDTO;
        List<PlaceDocuments> placeDocuments;
    }
}
