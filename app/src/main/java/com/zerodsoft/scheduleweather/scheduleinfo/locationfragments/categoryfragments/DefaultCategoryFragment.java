package com.zerodsoft.scheduleweather.scheduleinfo.locationfragments.categoryfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zerodsoft.scheduleweather.R;

public class DefaultCategoryFragment extends Fragment
{
    private final double LATITUDE;
    private final double LONGITUDE;
    private final String LOCATION_NAME;

    public DefaultCategoryFragment(String locationName, double latitude, double longitude)
    {
        this.LATITUDE = latitude;
        this.LONGITUDE = longitude;
        this.LOCATION_NAME = locationName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_map_category_common, container, false);
    }
}
