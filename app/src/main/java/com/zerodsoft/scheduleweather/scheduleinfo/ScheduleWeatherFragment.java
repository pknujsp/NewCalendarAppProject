package com.zerodsoft.scheduleweather.scheduleinfo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.scheduleinfo.placefragments.LocationInfo;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.WeatherItemFragment;
import com.zerodsoft.scheduleweather.utility.WeatherDataConverter;

import java.util.ArrayList;
import java.util.List;


public class ScheduleWeatherFragment extends Fragment
{
    private ImageButton refreshButton;
    private int fragmentContainerId;

    private FragmentManager fragmentManager;
    private List<Fragment> fragments;
    private List<PlaceDTO> places;
    private List<AddressDTO> addresses;

    public ScheduleWeatherFragment(List<AddressDTO> addresses, List<PlaceDTO> places)
    {
        this.places = places;
        this.addresses = addresses;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        WeatherDataConverter.context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_schedule_weather, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        fragmentContainerId = R.id.weather_fragment_container_view;

        if (!places.isEmpty() || !addresses.isEmpty())
        {
            fragments = new ArrayList<>();

            for (int i = 0; i < places.size(); i++)
            {
                fragments.add(new WeatherItemFragment(new LocationInfo(Double.parseDouble(places.get(i).getLatitude()), Double.parseDouble(places.get(i).getLongitude())
                        , places.get(i).getAddressName())));
            }
            for (int i = 0; i < addresses.size(); i++)
            {
                fragments.add(new WeatherItemFragment(new LocationInfo(Double.parseDouble(addresses.get(i).getLatitude()), Double.parseDouble(addresses.get(i).getLongitude())
                        , addresses.get(i).getAddressName())));
            }
        }
        fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().add(fragmentContainerId, fragments.get(0)).commitAllowingStateLoss();
    }

    public void rotateRefreshButton(boolean value)
    {
        if (value)
        {
            RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
            refreshButton.startAnimation(rotateAnimation);
        } else
        {
            refreshButton.getAnimation().cancel();
        }
    }
}