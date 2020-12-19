package com.zerodsoft.scheduleweather.scheduleinfo.placefragments.fragment;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.map.fragment.map.MapFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.search.SearchFragment;
import com.zerodsoft.scheduleweather.kakaomap.fragment.KakaoMapFragment;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.ICatchedLocation;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.scheduleinfo.placefragments.interfaces.IPlaceItem;
import com.zerodsoft.scheduleweather.scheduleinfo.placefragments.interfaces.ISelectCategory;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.List;
import java.util.Set;

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

    public static PlacesMapFragment newInstance(IPlaceItem iPlaceItem)
    {
        instance = new PlacesMapFragment(iPlaceItem);
        return instance;
    }

    public static void close()
    {
        instance = null;
    }

    public PlacesMapFragment(IPlaceItem iPlaceItem)
    {
        this.iPlaceItem = iPlaceItem;
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
                FragmentManager fragmentManager = getParentFragmentManager();
                PlacesFragment placesFragment = null;
                List<Fragment> fragments = fragmentManager.getFragments();
                for (Fragment fragment : fragments)
                {
                    if (fragment instanceof PlacesFragment)
                    {
                        placesFragment = (PlacesFragment) fragment;
                        break;
                    }
                }
                fragmentManager.beginTransaction().hide(PlacesMapFragment.this).show(placesFragment).commit();
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
        headerBar.setVisibility(View.GONE);
        gpsButton.setVisibility(View.GONE);
        bottomSheet.findViewById(R.id.choice_location_button).setVisibility(View.GONE);
        bottomSheet.findViewById(R.id.cancel_location_button).setVisibility(View.GONE);

        chipGroup = new ChipGroup(getContext());
        chipGroup.setSingleSelection(true);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.TOP;
        mapViewContainer.addView(chipGroup, layoutParams);

        LayoutInflater layoutInflater = getLayoutInflater();
        List<String> categories = iPlaceItem.getCategoryNames();

        for (String categoryName : categories)
        {
            View child = layoutInflater.inflate(R.layout.choicechip, chipGroup, false);
            ((Chip) child).setText(categoryName);
            chipGroup.addView(child);
        }

        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId)
            {
                Chip chip = (Chip) chipGroup.findViewById(checkedId);
                selectedCategoryName = chip.getText().toString();
                // 선택한 카테고리로 데이터 리스트 변경
                List<PlaceDocuments> documents = iPlaceItem.getPlaceItems(selectedCategoryName);
                createPlacesPoiItems(documents);
                showAllPoiItems();
            }
        });

        mapView.setPOIItemEventListener(this);
        mapView.setMapViewEventListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onStart()
    {
        super.onStart();
        init();
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

    public void setSelectedCategoryName(String selectedCategoryName)
    {
        this.selectedCategoryName = selectedCategoryName;
    }

    public void init()
    {
        int chipsCount = chipGroup.getChildCount();
        for (int i = 0; i < chipsCount; i++)
        {
            if (((Chip) chipGroup.getChildAt(i)).getText().equals(selectedCategoryName))
            {
                chipGroup.check(chipGroup.getChildAt(i).getId());
                break;
            }
        }
    }
}