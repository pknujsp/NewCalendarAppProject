package com.zerodsoft.scheduleweather.activity.preferences.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.fragments.TimeZoneFragment;
import com.zerodsoft.scheduleweather.activity.editevent.interfaces.ITimeZone;
import com.zerodsoft.scheduleweather.activity.preferences.SettingsActivity;
import com.zerodsoft.scheduleweather.activity.preferences.interfaces.IPreferenceFragment;

import java.util.TimeZone;

public class SettingsTimeZoneFragment extends TimeZoneFragment implements ITimeZone
{
    private OnBackPressedCallback onBackPressedCallback;
    private final IPreferenceFragment iPreferenceFragment;

    public SettingsTimeZoneFragment(IPreferenceFragment iPreferenceFragment)
    {
        this.iPreferenceFragment = iPreferenceFragment;
        setiTimeZone(this);
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
                //설정값 변경
                getParentFragmentManager().popBackStack();
                ((SettingsActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_settings);
                onBackPressedCallback.remove();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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
    }

    @Override
    public void onSelectedTimeZone(TimeZone timeZone)
    {
        // settingsfragment로 선택 값 전달
        iPreferenceFragment.onFinished(timeZone);
        onBackPressedCallback.handleOnBackPressed();
    }

}
