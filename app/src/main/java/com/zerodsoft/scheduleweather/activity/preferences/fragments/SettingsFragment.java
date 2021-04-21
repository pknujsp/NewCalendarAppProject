package com.zerodsoft.scheduleweather.activity.preferences.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.activity.placecategory.activity.PlaceCategoryActivity;
import com.zerodsoft.scheduleweather.activity.preferences.SettingsActivity;
import com.zerodsoft.scheduleweather.activity.preferences.custom.RadiusPreference;
import com.zerodsoft.scheduleweather.activity.preferences.custom.SearchBuildingRangeRadiusPreference;
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

    private RadiusPreference searchMapCategoryRangeRadiusPreference;
    private SearchBuildingRangeRadiusPreference searchBuildingRangeRadiusPreference;
    private Preference placesCategoryPreference;
    private Preference customFoodPreference;

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
        customFoodPreference = findPreference(getString(R.string.preference_key_custom_food_menu));

        initPreference();
        initValue();

        useDefaultTimeZoneSwitchPreference.setOnPreferenceChangeListener(preferenceChangeListener);
        weekOfYearSwitchPreference.setOnPreferenceChangeListener(preferenceChangeListener);
        showCanceledInstanceSwitchPreference.setOnPreferenceChangeListener(preferenceChangeListener);
        hourSystemSwitchPreference.setOnPreferenceChangeListener(preferenceChangeListener);

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
            // 범위

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

        //카테고리 검색범위 반지름
        searchMapCategoryRangeRadiusPreference = new RadiusPreference(getContext());
        searchMapCategoryRangeRadiusPreference.setKey(getString(R.string.preference_key_radius_range));
        searchMapCategoryRangeRadiusPreference.setSummary(R.string.preference_summary_radius_range);
        searchMapCategoryRangeRadiusPreference.setTitle(R.string.preference_title_radius_range);
        searchMapCategoryRangeRadiusPreference.setWidgetLayoutResource(R.layout.custom_preference_layout);
        searchMapCategoryRangeRadiusPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                Slider slider = new Slider(getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER_VERTICAL;
                layoutParams.weight = 1;

                int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, getResources().getDisplayMetrics());

                layoutParams.topMargin = margin;
                layoutParams.bottomMargin = margin;

                slider.setLayoutParams(layoutParams);
                slider.setValue(Float.parseFloat(App.getPreference_key_radius_range()) / 1000f);
                slider.setValueFrom(0.1f);
                slider.setValueTo(20.0f);
                slider.setStepSize(0.1f);

                linearLayout.addView(slider);

                new MaterialAlertDialogBuilder(getActivity()).setView(linearLayout)
                        .setPositiveButton(R.string.check, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                String value = String.valueOf(slider.getValue() * 1000);
                                searchMapCategoryRangeRadiusPreference.callChangeListener(value);
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
                return true;
            }
        });

        searchMapCategoryRangeRadiusPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                App.setPreference_key_radius_range((String) newValue);
                searchMapCategoryRangeRadiusPreference.setValue();
                return true;
            }
        });

        ((PreferenceCategory) getPreferenceManager().findPreference(getString(R.string.preference_place_category_title)))
                .addPreference(searchMapCategoryRangeRadiusPreference);

        //빌딩 검색범위 반지름
        searchBuildingRangeRadiusPreference = new SearchBuildingRangeRadiusPreference(getContext());
        searchBuildingRangeRadiusPreference.setKey(getString(R.string.preference_key_range_meter_for_search_buildings));
        searchBuildingRangeRadiusPreference.setSummary(R.string.preference_summary_range_meter_for_search_buildings);
        searchBuildingRangeRadiusPreference.setTitle(R.string.preference_title_range_meter_for_search_buildings);
        searchBuildingRangeRadiusPreference.setWidgetLayoutResource(R.layout.custom_preference_layout);
        searchBuildingRangeRadiusPreference.setDialogTitle(R.string.preference_message_range_meter_for_search_buildings);

        ((PreferenceCategory) getPreferenceManager().findPreference(getString(R.string.preference_place_category_title)))
                .addPreference(searchBuildingRangeRadiusPreference);

        searchBuildingRangeRadiusPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                int radius = 0;
                try
                {
                    radius = Integer.parseInt((String) newValue);
                } catch (NumberFormatException e)
                {
                    Toast.makeText(getActivity(), "50~500 사이의 숫자를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (radius >= 50 && radius <= 500)
                {
                    App.setPreference_key_range_meter_for_search_buildings((String) newValue);
                    searchBuildingRangeRadiusPreference.setValue();
                    return true;
                } else
                {
                    Toast.makeText(getActivity(), "50~500 사이로 입력하세요", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });
    }

    private final Preference.OnPreferenceChangeListener preferenceChangeListener = new Preference.OnPreferenceChangeListener()
    {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue)
        {
            //주차
            if (preference.getKey().equals(weekOfYearSwitchPreference.getKey()))
            {
                boolean value = (Boolean) newValue;
                if (value != weekOfYearSwitchPreference.isChecked())
                {
                    App.setPreference_key_show_week_of_year(value);
                    return true;
                } else
                {
                    return false;
                }
            }

            // 기기 기본 시간 사용
            else if (preference.getKey().equals(useDefaultTimeZoneSwitchPreference.getKey()))
            {
                boolean value = (Boolean) newValue;

                if (value != useDefaultTimeZoneSwitchPreference.isChecked())
                {
                    App.setPreference_key_using_timezone_of_device(value);
                    return true;
                } else
                {
                    return false;
                }
            }

            // 거절한 일정 표시
            else if (preference.getKey().equals(showCanceledInstanceSwitchPreference.getKey()))
            {
                boolean value = (Boolean) newValue;

                if (value != showCanceledInstanceSwitchPreference.isChecked())
                {
                    App.setPreference_key_show_canceled_instances(value);
                    return true;
                } else
                {
                    return false;
                }
            }

            // 시간제
            else if (preference.getKey().equals(hourSystemSwitchPreference.getKey()))
            {
                boolean value = (Boolean) newValue;

                if (value != hourSystemSwitchPreference.isChecked())
                {
                    App.setPreference_key_settings_hour_system(value);
                    return true;
                } else
                {
                    return false;
                }
            }
            return false;
        }
    };

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

        //지도 카테고리 검색 반지름 범위
        String searchMapCategoryRadius = preferences.getString(getString(R.string.preference_key_radius_range), "");
        float convertedRadius = Float.parseFloat(searchMapCategoryRadius) / 1000f;
        searchMapCategoryRangeRadiusPreference.setDefaultValue(String.valueOf(convertedRadius));

        //지도 빌딩 검색 반지름 범위
        String searchBuildingRangeRadius = preferences.getString(getString(R.string.preference_key_range_meter_for_search_buildings), "");
        searchBuildingRangeRadiusPreference.setDefaultValue(searchBuildingRangeRadius);
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
