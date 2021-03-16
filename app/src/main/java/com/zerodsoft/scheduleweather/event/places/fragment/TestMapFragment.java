package com.zerodsoft.scheduleweather.event.places.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IstartActivity;
import com.zerodsoft.scheduleweather.databinding.FragmentTestMapBinding;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocation;
import com.zerodsoft.scheduleweather.event.places.bottomsheet.LockableBottomSheetBehavior;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IBottomSheet;


public class TestMapFragment extends Fragment implements PlacesMapFragment.PlaceListBottomSheetInterface
{
    private FragmentTestMapBinding binding;
    private PlacesMapFragment placesMapFragment;
    private BottomSheetBehavior placeListBottomSheetBehavior;

    private final ILocation iLocation;
    private final IstartActivity istartActivity;

    public TestMapFragment(ILocation iLocation, IstartActivity istartActivity)
    {
        this.iLocation = iLocation;
        this.istartActivity = istartActivity;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentTestMapBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        //add map fragment
        placesMapFragment = new PlacesMapFragment(this, iLocation, istartActivity);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().add(binding.placesMapFragmentContainer.getId(), placesMapFragment, PlacesMapFragment.TAG)
                .commit();

        //set bottomsheet
        FrameLayout customBottomSheet = (FrameLayout) view.findViewById(R.id.place_list_bottom_sheet_view);

        placeListBottomSheetBehavior = BottomSheetBehavior.from(customBottomSheet);
        placeListBottomSheetBehavior.setPeekHeight(300);
        placeListBottomSheetBehavior.setBottomSheetCallback(new MyBottomSheetCallback());
    }


    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void setBottomSheetState(int state)
    {
        placeListBottomSheetBehavior.setState(state);
    }

    class MyBottomSheetCallback extends BottomSheetBehavior.BottomSheetCallback
    {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState)
        {
            if (newState == BottomSheetBehavior.STATE_EXPANDED)
            {
                if (placeListBottomSheetBehavior instanceof LockableBottomSheetBehavior)
                {
                    ((LockableBottomSheetBehavior) placeListBottomSheetBehavior).setLocked(true);
                }
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset)
        {
        }
    }

}