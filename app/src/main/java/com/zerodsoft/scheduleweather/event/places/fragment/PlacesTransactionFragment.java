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
import com.zerodsoft.scheduleweather.event.places.interfaces.IFragment;

public class PlacesTransactionFragment extends Fragment implements IFragment
{
    private final ILocation iLocation;
    private final IstartActivity istartActivity;

    private FragmentPlacesTransactionBinding binding;
    private PlacesFragment placesFragment;
    private MorePlacesFragment morePlacesFragment;


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

        placesFragment = new PlacesFragment(iLocation, istartActivity, this);
        morePlacesFragment = new MorePlacesFragment(this);
        placesFragment.setiClickedPlaceItem(morePlacesFragment);

        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.add(binding.fragmentContainerView.getId(), placesFragment, PlacesFragment.TAG)
                .add(binding.fragmentContainerView.getId(), morePlacesFragment, MorePlacesFragment.TAG).show(placesFragment)
                .hide(morePlacesFragment)
                .commit();
    }

    public void refresh()
    {
        //placefragment 카테고리 재 검색
        placesFragment.refresh();
    }

    @Override
    public void replaceFragment(String fragmentTag)
    {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();

        switch (fragmentTag)
        {
            case PlacesFragment.TAG:
                fragmentTransaction.show(placesFragment).hide(morePlacesFragment).commit();
                break;
            case MorePlacesFragment.TAG:
                fragmentTransaction.hide(placesFragment).show(morePlacesFragment).addToBackStack(null).commit();
                break;
        }
    }
}