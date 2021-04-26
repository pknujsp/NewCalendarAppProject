package com.zerodsoft.scheduleweather.event.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naver.maps.map.NaverMap;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.navermap.NaverMapFragment;

public class NewInstanceMainFragment extends NaverMapFragment
{
    public static final String TAG = "NewInstanceMainFragment";
    private final int CALENDAR_ID;
    private final long EVENT_ID;
    private final long INSTANCE_ID;
    private final long ORIGINAL_BEGIN;
    private final long ORIGINAL_END;

    public NewInstanceMainFragment(int CALENDAR_ID, long EVENT_ID, long INSTANCE_ID, long ORIGINAL_BEGIN, long ORIGINAL_END)
    {
        this.CALENDAR_ID = CALENDAR_ID;
        this.EVENT_ID = EVENT_ID;
        this.INSTANCE_ID = INSTANCE_ID;
        this.ORIGINAL_BEGIN = ORIGINAL_BEGIN;
        this.ORIGINAL_END = ORIGINAL_END;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap)
    {
        super.onMapReady(naverMap);
    }
}