package com.zerodsoft.scheduleweather.scheduleinfo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.scheduleinfo.placefragments.LocationInfo;
import com.zerodsoft.scheduleweather.scheduleinfo.placefragments.fragment.PlacesFragment;

import java.util.LinkedList;
import java.util.List;


public class PlacesAroundLocationFragment extends Fragment
{
    private int fragmentContainerId;
    private FragmentManager fragmentManager;
    private List<AddressDTO> addresses;
    private List<PlaceDTO> places;
    private List<Fragment> fragments;

    public PlacesAroundLocationFragment(List<AddressDTO> addresses, List<PlaceDTO> places)
    {
        this.addresses = addresses;
        this.places = places;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_schedule_around_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        fragmentContainerId = R.id.places_around_location_fragment_container;

        fragments = new LinkedList<>();

        for (AddressDTO address : addresses)
        {
            fragments.add(new PlacesFragment(new LocationInfo(Double.parseDouble(address.getLatitude()), Double.parseDouble(address.getLongitude()), address.getAddressName())));
        }

        for (PlaceDTO place : places)
        {
            fragments.add(new PlacesFragment(new LocationInfo(Double.parseDouble(place.getLatitude()), Double.parseDouble(place.getLongitude()), place.getPlaceName())));
        }

        fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().add(fragmentContainerId, fragments.get(0)).commit();
    }
}