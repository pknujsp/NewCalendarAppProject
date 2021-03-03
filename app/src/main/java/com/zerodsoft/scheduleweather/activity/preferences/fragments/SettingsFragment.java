package com.zerodsoft.scheduleweather.activity.preferences.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.activity.TimeZoneActivity;
import com.zerodsoft.scheduleweather.activity.placecategory.activity.PlaceCategoryActivity;
import com.zerodsoft.scheduleweather.activity.preferences.CustomListPreference;
import com.zerodsoft.scheduleweather.activity.preferences.interfaces.PreferenceListener;
import com.zerodsoft.scheduleweather.event.util.EventUtil;

import java.util.Locale;
import java.util.TimeZone;

public class SettingsFragment extends PreferenceFragmentCompat implements PreferenceListener
{
    private SharedPreferences sharedPreferences;

    private SwitchPreference useDefaultTimeZoneSwitchPreference;
    private Preference customTimezonePreference;
    private SwitchPreference weekOfYearSwitchPreference;
    private SwitchPreference showCanceledInstanceSwitchPreference;
    private Preference timeRangePreference;
    private Preference calendarColorListPreference;
    private CustomListPreference holidayColorListPreference;
    private SwitchPreference hourSystemSwitchPreference;

    private ListPreference searchingRadiusListPreference;
    private Preference placesCategoryPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.app_settings_main_preference, rootKey);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);

        useDefaultTimeZoneSwitchPreference = findPreference(getString(R.string.preference_key_use_default_timezone));
        customTimezonePreference = findPreference(getString(R.string.preference_key_custom_timezone));
        weekOfYearSwitchPreference = findPreference(getString(R.string.preference_key_show_week_of_year));
        showCanceledInstanceSwitchPreference = findPreference(getString(R.string.preference_key_show_canceled_instances));
        timeRangePreference = findPreference(getString(R.string.preference_key_timerange));
        calendarColorListPreference = findPreference(getString(R.string.preference_key_calendar_color));
        holidayColorListPreference = findPreference(getString(R.string.preference_key_holiday_color));
        hourSystemSwitchPreference = findPreference(getString(R.string.preference_key_use_24_hour_system));

        searchingRadiusListPreference = findPreference(getString(R.string.preference_key_searching_radius));
        placesCategoryPreference = findPreference(getString(R.string.preference_key_places_category));

        holidayColorListPreference.setPreferenceListener(this::onCreatedPreferenceView);

        useDefaultTimeZoneSwitchPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                if (useDefaultTimeZoneSwitchPreference.isChecked())
                {
                    customTimezonePreference.setEnabled(false);
                } else
                {
                    customTimezonePreference.setEnabled(true);
                }
                return true;
            }
        });

        customTimezonePreference.setIntent(new Intent(getActivity(), TimeZoneActivity.class));
        placesCategoryPreference.setIntent(new Intent(getActivity(), PlaceCategoryActivity.class));

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        return super.onCreateRecyclerView(inflater, parent, savedInstanceState);
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener()
    {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s)
        {

        }
    };

    private void initValue()
    {
        //기기 기본 시간대 사용 여부
        boolean usingDeviceTimeZone = sharedPreferences.getBoolean(getString(R.string.preference_key_use_default_timezone), false);
        if (usingDeviceTimeZone)
        {
            useDefaultTimeZoneSwitchPreference.setChecked(true);
        } else
        {
            useDefaultTimeZoneSwitchPreference.setChecked(false);
        }
        // 사용하지 않더라도 커스텀 시간대는 설정해놓는다.
        String customTimeZoneId = sharedPreferences.getString(getString(R.string.preference_key_custom_timezone), "");
        TimeZone timeZone = TimeZone.getTimeZone(customTimeZoneId);
        customTimezonePreference.setSummary(timeZone.getDisplayName(Locale.KOREAN));

        //주차 표시
        boolean showingWeekOfYear = sharedPreferences.getBoolean(getString(R.string.preference_key_show_week_of_year), false);
        if (showingWeekOfYear)
        {
            weekOfYearSwitchPreference.setChecked(true);
        } else
        {
            weekOfYearSwitchPreference.setChecked(false);
        }

        //거절한 일정 표시
        boolean showingCanceledInstance = sharedPreferences.getBoolean(getString(R.string.preference_key_show_canceled_instances), false);
        if (showingCanceledInstance)
        {
            showCanceledInstanceSwitchPreference.setChecked(true);
        } else
        {
            showCanceledInstanceSwitchPreference.setChecked(false);
        }

        //캘린더 색상, 기본 시간 범위

        //공휴일 색상
        int holidayColor = sharedPreferences.getInt(getString(R.string.preference_key_holiday_color), 0);
        holidayColor = EventUtil.getColor(holidayColor);
        //색상을 오른쪽에 표시

        //24시간제 사용
        //거절한 일정 표시
        boolean using24HourSystem = sharedPreferences.getBoolean(getString(R.string.preference_key_use_24_hour_system), false);
        if (using24HourSystem)
        {
            hourSystemSwitchPreference.setChecked(true);
            hourSystemSwitchPreference.setSummary(getString(R.string.hour_24_system));
        } else
        {
            hourSystemSwitchPreference.setChecked(false);
            hourSystemSwitchPreference.setSummary(getString(R.string.hour_12_system));
        }

        //검색 반지름 범위
        int searchingRadius = sharedPreferences.getInt(getString(R.string.preference_key_searching_radius), 0);
        //오른쪽에 반지름 표시
    }

    @Override
    public void onCreatedPreferenceView()
    {
        holidayColorListPreference.getCustomView().setBackgroundColor(Color.BLUE);
    }
}
