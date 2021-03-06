package com.zerodsoft.scheduleweather.activity.preferences.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.activity.TimeZoneActivity;
import com.zerodsoft.scheduleweather.activity.placecategory.activity.PlaceCategoryActivity;
import com.zerodsoft.scheduleweather.activity.preferences.custom.RadiusPreference;
import com.zerodsoft.scheduleweather.activity.preferences.interfaces.PreferenceListener;

import java.util.Locale;
import java.util.TimeZone;

public class SettingsFragment extends PreferenceFragmentCompat implements PreferenceListener
{
    private SharedPreferences preferences;
    private OnBackPressedCallback onBackPressedCallback;

    private SwitchPreference useDefaultTimeZoneSwitchPreference;
    private Preference customTimezonePreference;
    private SwitchPreference weekOfYearSwitchPreference;
    private SwitchPreference showCanceledInstanceSwitchPreference;
    private Preference timeRangePreference;
    private Preference calendarColorListPreference;
    private SwitchPreference hourSystemSwitchPreference;

    private RadiusPreference searchingRadiusPreference;
    private Preference placesCategoryPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.app_settings_main_preference, rootKey);
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
                getActivity().finish();
                onBackPressedCallback.remove();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        preferences.registerOnSharedPreferenceChangeListener(listener);

        useDefaultTimeZoneSwitchPreference = findPreference(getString(R.string.preference_key_use_default_timezone));
        customTimezonePreference = findPreference(getString(R.string.preference_key_custom_timezone));
        weekOfYearSwitchPreference = findPreference(getString(R.string.preference_key_show_week_of_year));
        showCanceledInstanceSwitchPreference = findPreference(getString(R.string.preference_key_show_canceled_instances));
        timeRangePreference = findPreference(getString(R.string.preference_key_timerange));
        calendarColorListPreference = findPreference(getString(R.string.preference_key_calendar_color));
        hourSystemSwitchPreference = findPreference(getString(R.string.preference_key_use_24_hour_system));

        placesCategoryPreference = findPreference(getString(R.string.preference_key_places_category));

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

        hourSystemSwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                if ((Boolean) newValue)
                {
                    hourSystemSwitchPreference.setSummary(getString(R.string.hour_24_system));
                } else
                {
                    hourSystemSwitchPreference.setSummary(getString(R.string.hour_12_system));
                }
                return true;
            }
        });

        customTimezonePreference.setIntent(new Intent(getActivity(), TimeZoneActivity.class));
        placesCategoryPreference.setIntent(new Intent(getActivity(), PlaceCategoryActivity.class));

        initValue();

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
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            //값 변경시 호출됨
            //시간대
            if (key.equals(useDefaultTimeZoneSwitchPreference.getKey()))
            {

            }

            //주차
            if (key.equals(weekOfYearSwitchPreference.getKey()))
            {

            }

            //거절한 일정
            if (key.equals(showCanceledInstanceSwitchPreference.getKey()))
            {
            }

            //24시간제
            if (key.equals(hourSystemSwitchPreference.getKey()))
            {
            }

            //검색 반지름 범위
            if (key.equals(searchingRadiusPreference.getKey()))
            {
                searchingRadiusPreference.setValue(sharedPreferences.getString(key, ""));
            }
        }
    };

    private void initValue()
    {
        //기기 기본 시간대 사용 여부
        boolean usingDeviceTimeZone = preferences.getBoolean(getString(R.string.preference_key_use_default_timezone), false);
        if (usingDeviceTimeZone)
        {
            useDefaultTimeZoneSwitchPreference.setChecked(true);
            customTimezonePreference.setEnabled(false);
        } else
        {
            useDefaultTimeZoneSwitchPreference.setChecked(false);
            customTimezonePreference.setEnabled(true);
        }

        // 사용하지 않더라도 커스텀 시간대는 설정해놓는다.
        String customTimeZoneId = preferences.getString(getString(R.string.preference_key_custom_timezone), "");
        TimeZone timeZone = TimeZone.getTimeZone(customTimeZoneId);
        customTimezonePreference.setSummary(timeZone.getDisplayName(Locale.KOREAN));

        //주차 표시
        boolean showingWeekOfYear = preferences.getBoolean(getString(R.string.preference_key_show_week_of_year), false);
        if (showingWeekOfYear)
        {
            weekOfYearSwitchPreference.setChecked(true);
        } else
        {
            weekOfYearSwitchPreference.setChecked(false);
        }

        //거절한 일정 표시
        boolean showingCanceledInstance = preferences.getBoolean(getString(R.string.preference_key_show_canceled_instances), false);
        if (showingCanceledInstance)
        {
            showCanceledInstanceSwitchPreference.setChecked(true);
        } else
        {
            showCanceledInstanceSwitchPreference.setChecked(false);
        }

        //캘린더 색상, 기본 시간 범위

        //24시간제 사용
        //거절한 일정 표시
        boolean using24HourSystem = preferences.getBoolean(getString(R.string.preference_key_use_24_hour_system), false);
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
        String searchingRadius = preferences.getString(getString(R.string.preference_key_searching_radius), "100");
        //오른쪽에 반지름 표시
        searchingRadiusPreference = new RadiusPreference(getContext(), searchingRadius);
        searchingRadiusPreference.setKey(getString(R.string.preference_key_searching_radius));
        searchingRadiusPreference.setSummary(R.string.summary_setting_search_places_distance);
        searchingRadiusPreference.setTitle(R.string.settings_searching_radius_custom);
        searchingRadiusPreference.setWidgetLayoutResource(R.layout.custom_preference_layout);
        searchingRadiusPreference.setDefaultValue(searchingRadius);
        searchingRadiusPreference.setDialogMessage(R.string.radius_dialog_message);

        ((PreferenceCategory) getPreferenceManager().findPreference(getString(R.string.preference_key_places_settings_category)))
                .addPreference(searchingRadiusPreference);

        searchingRadiusPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                int radius = Integer.parseInt((String) newValue);

                if (radius >= 0 && radius <= 20000)
                {
                    return true;
                } else
                {
                    Toast.makeText(getActivity(), "0~20000사이로 입력하세요", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });
    }

    @Override
    public void onCreatedPreferenceView()
    {
    }
}
