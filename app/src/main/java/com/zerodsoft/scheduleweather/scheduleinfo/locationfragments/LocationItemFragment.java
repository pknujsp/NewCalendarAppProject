package com.zerodsoft.scheduleweather.scheduleinfo.locationfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zerodsoft.scheduleweather.R;

public class LocationItemFragment extends Fragment
{
    private final double LATITUDE;
    private final double LONGITUDE;
    private final String LOCATION_NAME;

    public LocationItemFragment(String locationName, double latitude, double longitude)
    {
        this.LATITUDE = latitude;
        this.LONGITUDE = longitude;
        this.LOCATION_NAME = locationName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.around_location_viewpager_item, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        // 위치 이름 표시
        ((TextView) view.findViewById(R.id.location_name)).setText(LOCATION_NAME + " " + getString(R.string.info_around_location));
        // 표시할 정보를 가져옴
        // 정보를 표시할 프래그먼트를 각각 생성
        // 편의점, ATM 정보를 보여주기로 했다고 가정

    }
}
