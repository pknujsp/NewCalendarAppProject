package com.zerodsoft.scheduleweather.event.location.placefragments.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.kakaomap.fragment.KakaoMapFragment;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.event.location.placefragments.interfaces.IPlaceItem;

import java.util.List;

public class PlacesMapFragment extends KakaoMapFragment
{
    public static final String TAG = "PlacesMapFragment";
    private static PlacesMapFragment instance;

    private IPlaceItem iPlaceItem;

    private OnBackPressedCallback onBackPressedCallback;

    private ChipGroup chipGroup;
    private String selectedCategoryName;


    public static PlacesMapFragment getInstance()
    {
        return instance;
    }

    public static PlacesMapFragment newInstance(IPlaceItem iPlaceItem, String selectedCategoryName)
    {
        instance = new PlacesMapFragment(iPlaceItem, selectedCategoryName);
        return instance;
    }

    public static void close()
    {
        instance = null;
    }

    public PlacesMapFragment(IPlaceItem iPlaceItem, String selectedCategoryName)
    {
        this.iPlaceItem = iPlaceItem;
        this.selectedCategoryName = selectedCategoryName;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        onBackPressedCallback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                backToPreviousView();
                onBackPressedCallback.remove();
                getParentFragmentManager().popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
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

        gpsButton.setVisibility(View.GONE);

        chipGroup = new ChipGroup(getContext());
        chipGroup.setSingleSelection(true);
        chipGroup.setSingleLine(true);
        chipGroup.setSelectionRequired(true);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.TOP;
        mapViewContainer.addView(chipGroup, layoutParams);

        LayoutInflater layoutInflater = getLayoutInflater();
        List<String> categories = iPlaceItem.getCategoryNames();

        for (String categoryName : categories)
        {
            View child = layoutInflater.inflate(R.layout.choicechip, chipGroup, false);
            ((Chip) child).setText(categoryName);
            child.setOnClickListener(chipOnClickListener);
            chipGroup.addView(child);
        }
    }

    private final View.OnClickListener chipOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            final String chipCategory = ((Chip) view).getText().toString();

            if (selectedCategoryName.equals(chipCategory))
            {
                if (mapView.getPOIItems().length != iPlaceItem.getPlaceItemsSize(selectedCategoryName))
                {
                    refreshPlaceItems();
                } else
                {
                    showAllPoiItems();
                }
            } else
            {
                selectedCategoryName = chipCategory;
                refreshPlaceItems();
            }
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        selectChip();
    }


    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        onBackPressedCallback.remove();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

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

    public void selectChip(String selectedCategoryName)
    {
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
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
        List<PlaceDocuments> documents = iPlaceItem.getPlaceItems(selectedCategoryName);
        createPlacesPoiItems(documents);
        showAllPoiItems();
    }
}