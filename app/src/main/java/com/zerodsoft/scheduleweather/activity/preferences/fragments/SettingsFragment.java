package com.zerodsoft.scheduleweather.activity.preferences.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.activity.placecategory.activity.PlaceCategoryActivity;
import com.zerodsoft.scheduleweather.activity.preferences.SettingsActivity;
import com.zerodsoft.scheduleweather.activity.preferences.custom.RadiusPreference;
import com.zerodsoft.scheduleweather.activity.preferences.custom.TimeZonePreference;
import com.zerodsoft.scheduleweather.activity.preferences.interfaces.IPreferenceFragment;
import com.zerodsoft.scheduleweather.activity.preferences.interfaces.PreferenceListener;

import java.util.TimeZone;

public class SettingsFragment extends PreferenceFragmentCompat implements PreferenceListener, IPreferenceFragment
{
    private SharedPreferences preferences;
    private OnBackPressedCallback onBackPressedCallback;

    private SwitchPreference useDefaultTimeZoneSwitchPreference;
    private TimeZonePreference customTimeZonePreference;
    private SwitchPreference weekOfYearSwitchPreference;
    private SwitchPreference showCanceledInstanceSwitchPreference;
    private Preference calendarColorListPreference;
    private SwitchPreference hourSystemSwitchPreference;

    private RadiusPreference searchingRadiusPreference;
    private Preference placesCategoryPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.app_settings_main_preference, rootKey);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        preferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);

        useDefaultTimeZoneSwitchPreference = findPreference(getString(R.string.preference_key_using_timezone_of_device));
        weekOfYearSwitchPreference = findPreference(getString(R.string.preference_key_show_week_of_year));
        showCanceledInstanceSwitchPreference = findPreference(getString(R.string.preference_key_show_canceled_instances));
        hourSystemSwitchPreference = findPreference(getString(R.string.preference_key_using_24_hour_system));
        calendarColorListPreference = findPreference(getString(R.string.preference_key_calendar_color));
        placesCategoryPreference = findPreference(getString(R.string.preference_key_places_category));

        initPreference();
        initValue();

        useDefaultTimeZoneSwitchPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                if (useDefaultTimeZoneSwitchPreference.isChecked())
                {
                    customTimeZonePreference.setEnabled(false);
                } else
                {
                    customTimeZonePreference.setEnabled(true);
                }
                return true;
            }
        });

        placesCategoryPreference.setIntent(new Intent(getActivity(), PlaceCategoryActivity.class));
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

    private final SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener()
    {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            //값 변경완료시 호출됨

            // 기기 기본 시간 사용
            if (key.equals(useDefaultTimeZoneSwitchPreference.getKey()))
            {
                App.setPreference_key_using_timezone_of_device(useDefaultTimeZoneSwitchPreference.isChecked());
            }

            // 커스텀 시간대
            else if (key.equals(customTimeZonePreference.getKey()))
            {
                App.setPreference_key_custom_timezone(customTimeZonePreference.getTimeZone());
            }

            // 주차 표시
            else if (key.equals(weekOfYearSwitchPreference.getKey()))
            {
                App.setPreference_key_show_week_of_year(weekOfYearSwitchPreference.isChecked());
            }

            // 거절한 일정 표시
            else if (key.equals(showCanceledInstanceSwitchPreference.getKey()))
            {
                App.setPreference_key_show_canceled_instances(showCanceledInstanceSwitchPreference.isChecked());
            }

            // 시간제
            else if (key.equals(hourSystemSwitchPreference.getKey()))
            {
                App.setPreference_key_settings_hour_system(hourSystemSwitchPreference.isChecked());
            }

            //검색 반지름 범위
            if (key.equals(searchingRadiusPreference.getKey()))
            {
                App.setPreference_key_radius_range(searchingRadiusPreference.getText());
                searchingRadiusPreference.setValue();
            }
        }
    };

    private void initPreference()
    {
        //커스텀 시간대
        customTimeZonePreference = new TimeZonePreference(getContext());
        customTimeZonePreference.setKey(getString(R.string.preference_key_custom_timezone));
        customTimeZonePreference.setSummary(R.string.preference_summary_custom_timezone);
        customTimeZonePreference.setTitle(R.string.preference_title_custom_timezone);
        customTimeZonePreference.setWidgetLayoutResource(R.layout.custom_preference_layout);

        customTimeZonePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                getParentFragmentManager().beginTransaction().replace(R.id.settings_fragment_container, new SettingsTimeZoneFragment(SettingsFragment.this))
                        .addToBackStack(null).commit();
                ((SettingsActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.preference_title_custom_timezone));
                return true;
            }
        });

        ((PreferenceCategory) getPreferenceManager().findPreference(getString(R.string.preference_calendar_category_title)))
                .addPreference(customTimeZonePreference);

        //검색 반지름
        searchingRadiusPreference = new RadiusPreference(getContext());
        searchingRadiusPreference.setKey(getString(R.string.preference_key_radius_range));
        searchingRadiusPreference.setSummary(R.string.preference_summary_radius_range);
        searchingRadiusPreference.setTitle(R.string.preference_title_radius_range);
        searchingRadiusPreference.setWidgetLayoutResource(R.layout.custom_preference_layout);
        searchingRadiusPreference.setDialogMessage(R.string.preference_dialog_message_radius_range);

        ((PreferenceCategory) getPreferenceManager().findPreference(getString(R.string.preference_place_category_title)))
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

    private void initValue()
    {
        // 사용하지 않더라도 커스텀 시간대는 설정해놓는다.
        String customTimeZoneId = preferences.getString(getString(R.string.preference_key_custom_timezone), "");
        TimeZone timeZone = TimeZone.getTimeZone(customTimeZoneId);
        customTimeZonePreference.setTimeZone(timeZone);

        //기기 기본 시간대 사용 여부
        boolean usingDeviceTimeZone = preferences.getBoolean(getString(R.string.preference_key_using_timezone_of_device), false);
        if (usingDeviceTimeZone)
        {
            useDefaultTimeZoneSwitchPreference.setChecked(true);
            customTimeZonePreference.setEnabled(false);
        } else
        {
            useDefaultTimeZoneSwitchPreference.setChecked(false);
            customTimeZonePreference.setEnabled(true);
        }

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

        //24시간제 사용
        boolean using24HourSystem = preferences.getBoolean(getString(R.string.preference_key_using_24_hour_system), false);
        hourSystemSwitchPreference.setChecked(using24HourSystem);

        //검색 반지름 범위
        String searchingRadius = preferences.getString(getString(R.string.preference_key_radius_range), "");
        searchingRadiusPreference.setDefaultValue(searchingRadius);
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
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onCreatedPreferenceView()
    {
    }


    @Override
    public void onFinished(Object result)
    {
        if (result instanceof TimeZone)
        {
            //수동 시간대 설정이 완료된 경우
            TimeZone currentTimeZone = customTimeZonePreference.getTimeZone();
            TimeZone newTimeZone = (TimeZone) result;

            if (currentTimeZone.getID().equals(newTimeZone.getID()))
            {
                Toast.makeText(getActivity(), "이미 선택된 시간대 입니다", Toast.LENGTH_SHORT).show();
            } else
            {
                customTimeZonePreference.setTimeZone(newTimeZone);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(getString(R.string.preference_key_custom_timezone), newTimeZone.getID()).apply();
                App.setPreference_key_custom_timezone(newTimeZone);
            }
        }
    }
}
