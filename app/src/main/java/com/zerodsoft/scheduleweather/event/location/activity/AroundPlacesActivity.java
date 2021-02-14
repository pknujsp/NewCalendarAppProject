package com.zerodsoft.scheduleweather.event.location.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.kakaomap.activity.KakaoMapActivity;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AroundPlacesActivity extends KakaoMapActivity
{
    private Map<String, List<PlaceDocuments>> map;

    private ChipGroup chipGroup;
    private String selectedCategoryName;

    public AroundPlacesActivity()
    {
        super();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        selectedCategoryName = getIntent().getStringExtra("selectedCategory");
        map = (HashMap<String, List<PlaceDocuments>>) getIntent().getSerializableExtra("map");

        binding.appbarLayout.setVisibility(View.GONE);
        kakaoMapFragment.getGpsButton().setVisibility(View.GONE);

        chipGroup = new ChipGroup(getApplicationContext());
        chipGroup.setSingleSelection(true);
        chipGroup.setSingleLine(true);
        chipGroup.setSelectionRequired(true);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.TOP;
        binding.mapCoordinatorLayout.addView(chipGroup, layoutParams);

        LayoutInflater layoutInflater = getLayoutInflater();

        Set<String> categoryNameSet = map.keySet();
        for (String categoryName : categoryNameSet)
        {
            View child = layoutInflater.inflate(R.layout.choicechip, chipGroup, false);
            ((Chip) child).setText(categoryName);
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
            final String chipCategory = ((Chip) view).getText().toString();

            if (selectedCategoryName.equals(chipCategory))
            {
                if (kakaoMapFragment.mapView.getPOIItems().length != map.get(selectedCategoryName).size())
                {
                    refreshPlaceItems();
                } else
                {
                    kakaoMapFragment.showAllPoiItems();
                }
            } else
            {
                selectedCategoryName = chipCategory;
                refreshPlaceItems();
            }
        }
    };

    public void selectChip()
    {
        int chipsCount = chipGroup.getChildCount();
        for (int i = 0; i < chipsCount; i++)
        {
            if (((Chip) chipGroup.getChildAt(i)).getText().equals(selectedCategoryName))
            {
                ((Chip) chipGroup.getChildAt(i)).performClick();
                break;
            }
        }
    }


    public void refreshPlaceItems()
    {
        kakaoMapFragment.createPlacesPoiItems(map.get(selectedCategoryName));
        kakaoMapFragment.showAllPoiItems();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onSelectLocation()
    {
        super.onSelectLocation();
    }

    @Override
    public void onRemoveLocation()
    {
        super.onRemoveLocation();
    }

    @Override
    public void setBottomSheetState(int state)
    {
        super.setBottomSheetState(state);
    }

    @Override
    public int getBottomSheetState()
    {
        return super.getBottomSheetState();
    }

    @Override
    public void setVisibility(int viewType, int state)
    {
        super.setVisibility(viewType, state);
    }

    @Override
    public void setAddress(AddressResponseDocuments documents)
    {
        super.setAddress(documents);
    }

    @Override
    public void setPlace(PlaceDocuments documents)
    {
        super.setPlace(documents);
    }

    @Override
    public void setItemVisibility(int state)
    {
        super.setItemVisibility(state);
    }

    @Override
    public void setFragmentVisibility(int state)
    {
        super.setFragmentVisibility(state);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void changeOpenCloseMenuVisibility(boolean isSearching)
    {
        super.changeOpenCloseMenuVisibility(isSearching);
    }

    @Override
    public void setMenuVisibility(int type, boolean state)
    {
        super.setMenuVisibility(type, state);
    }

    @Override
    public void setText(String text)
    {
        super.setText(text);
    }
}
