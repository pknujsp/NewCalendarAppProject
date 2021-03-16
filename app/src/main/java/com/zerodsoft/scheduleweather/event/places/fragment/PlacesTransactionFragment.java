package com.zerodsoft.scheduleweather.event.places.fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.calendarview.interfaces.IstartActivity;
import com.zerodsoft.scheduleweather.databinding.FragmentPlacesTransactionBinding;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocation;

public class PlacesTransactionFragment extends Fragment
{
    private final ILocation iLocation;
    private final IstartActivity istartActivity;

    private FragmentPlacesTransactionBinding binding;
    private TestMapFragment testMapFragment;

    public PlacesTransactionFragment(Activity activity)
    {
        this.iLocation = (ILocation) activity;
        this.istartActivity = (IstartActivity) activity;
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
        binding = FragmentPlacesTransactionBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        testMapFragment = new TestMapFragment(iLocation, istartActivity);

        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.add(binding.fragmentContainerView.getId(), testMapFragment, PlacesMapFragment.TAG)
                .commit();
    }

    public void refresh()
    {

    }
}