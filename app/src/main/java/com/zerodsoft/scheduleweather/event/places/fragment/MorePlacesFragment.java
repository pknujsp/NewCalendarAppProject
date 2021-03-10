package com.zerodsoft.scheduleweather.event.places.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.button.MaterialButton;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.event.places.interfaces.IClickedPlaceItem;
import com.zerodsoft.scheduleweather.event.places.interfaces.IFragment;
import com.zerodsoft.scheduleweather.kakaomap.fragment.KakaoMapFragment;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import java.util.List;

public class MorePlacesFragment extends KakaoMapFragment implements IClickedPlaceItem
{
    public static final String TAG = "MorePlacesFragment";
    private IFragment iFragment;
    private Button categoryButton;
    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true)
    {
        @Override
        public void handleOnBackPressed()
        {
            getParentFragmentManager().popBackStack();
            onBackPressedCallback.remove();
        }
    };

    public MorePlacesFragment(IFragment iFragment)
    {
        super();
        this.iFragment = iFragment;
    }

    public MorePlacesFragment()
    {
        super();
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
        binding.bottomSheet.mapBottomSheetToolbar.removeLocationButton.setVisibility(View.GONE);
        binding.bottomSheet.mapBottomSheetToolbar.selectLocationButton.setVisibility(View.GONE);
        binding.appbarLayout.setVisibility(View.GONE);

        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getContext(), R.style.Widget_MaterialComponents_Button_OutlinedButton);

        categoryButton = new MaterialButton(contextThemeWrapper);

        CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36f, getContext().getResources().getDisplayMetrics());
        layoutParams.leftMargin = margin;
        layoutParams.topMargin = margin;

        binding.mapRootLayout.addView(categoryButton, layoutParams);
        categoryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mapView.fitMapViewAreaToShowAllPOIItems();
            }
        });
    }


    @Override
    public void onClickedItem(int index, PlaceCategoryDTO placeCategory, List<PlaceDocuments> placeDocumentsList)
    {
        iFragment.replaceFragment(MorePlacesFragment.TAG);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        categoryButton.setText(placeCategory.getDescription());
        createPlacesPoiItems(placeDocumentsList);
        selectPoiItem(index);
    }

    @Override
    public void onClickedMore(PlaceCategoryDTO placeCategory, List<PlaceDocuments> placeDocumentsList)
    {
        iFragment.replaceFragment(MorePlacesFragment.TAG);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        categoryButton.setText(placeCategory.getDescription());
        createPlacesPoiItems(placeDocumentsList);
        mapView.fitMapViewAreaToShowAllPOIItems();
    }

}