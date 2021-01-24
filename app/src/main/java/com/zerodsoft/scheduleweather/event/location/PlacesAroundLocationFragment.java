package com.zerodsoft.scheduleweather.event.location;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.event.location.placefragments.LocationInfo;
import com.zerodsoft.scheduleweather.event.location.placefragments.fragment.PlacesFragment;


public class PlacesAroundLocationFragment extends Fragment
{
    // 이벤트의 위치 값으로 정확한 위치를 지정하기 위해 위치 지정 액티비티 생성(카카오맵 검색 값 기반)
    private LocationDTO location;

    public PlacesAroundLocationFragment()
    {
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        location = getArguments().getParcelable("location");
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
        Fragment fragment = null;

        if (location.getPlaceId() != null)
        {
            fragment = new PlacesFragment(new LocationInfo(location.getLatitude(), location.getLongitude(), location.getPlaceName()));
        } else
        {
            fragment = new PlacesFragment(new LocationInfo(location.getLatitude(), location.getLongitude(), location.getAddressName()));
        }
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().add(R.id.places_around_location_fragment_container, fragment).commit();
    }
}