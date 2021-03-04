package com.zerodsoft.scheduleweather.activity.preferences.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.preferences.ColorListAdapter;
import com.zerodsoft.scheduleweather.activity.preferences.ColorValue;
import com.zerodsoft.scheduleweather.activity.preferences.custom.ColorPreference;
import com.zerodsoft.scheduleweather.activity.preferences.interfaces.PreferenceListener;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CalendarColorFragment extends PreferenceFragmentCompat implements PreferenceListener
{
    private CalendarViewModel calendarViewModel;
    private final ColorPreferenceInterface colorPreferenceInterface = new ColorPreferenceInterface()
    {
        @Override
        public void onCreatedPreferenceView(ContentValues calendar, ColorPreference colorPreference)
        {
            super.onCreatedPreferenceView(calendar, colorPreference);
        }
    };

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.app_settings_calendar_color_preference, rootKey);
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
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        calendarViewModel.init(getContext());

        calendarViewModel.getCalendarListLiveData().observe(getViewLifecycleOwner(), new Observer<DataWrapper<List<ContentValues>>>()
        {
            @Override
            public void onChanged(DataWrapper<List<ContentValues>> result)
            {
                if (result.getData() != null)
                {
                    List<ContentValues> calendarList = result.getData();
                    // 계정 별로 나눈다.
                    Map<String, List<ContentValues>> accountMap = new HashMap<>();

                    String accountName = null;
                    for (ContentValues calendar : calendarList)
                    {
                        accountName = calendar.getAsString(CalendarContract.Calendars.ACCOUNT_NAME);

                        if (!accountMap.containsKey(accountName))
                        {
                            accountMap.put(accountName, new ArrayList<>());
                        }
                        accountMap.get(accountName).add(calendar);
                    }

                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            //카테고리를 생성(accountName별로 분류)
                            Set<String> accountSet = accountMap.keySet();
                            PreferenceScreen preferenceScreen = getPreferenceScreen();

                            for (String accountName : accountSet)
                            {
                                PreferenceCategory preferenceCategory = new PreferenceCategory(getContext());
                                preferenceCategory.setTitle(accountName);
                                preferenceScreen.addPreference(preferenceCategory);

                                List<ContentValues> calendars = accountMap.get(accountName);

                                for (ContentValues calendar : calendars)
                                {
                                    ColorPreference preference = new ColorPreference(getContext(), colorPreferenceInterface, calendar);

                                    preference.setTitle(calendar.getAsString(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME));
                                    preference.setWidgetLayoutResource(R.layout.custom_preference_layout);

                                    preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
                                    {
                                        @Override
                                        public boolean onPreferenceClick(Preference preference)
                                        {
                                            List<ContentValues> colors = calendarViewModel.getCalendarColors(calendar.getAsString(CalendarContract.Calendars.ACCOUNT_NAME),
                                                    calendar.getAsString(CalendarContract.Calendars.ACCOUNT_TYPE));

                                            GridView gridView = (GridView) LayoutInflater.from(getContext()).inflate(R.layout.color_dialog_view, null);
                                            gridView.setAdapter(new ColorListAdapter(calendar.getAsInteger(CalendarContract.Calendars.CALENDAR_COLOR), colors, getContext()));

                                            new MaterialAlertDialogBuilder(getContext())
                                                    .setTitle(R.string.selection_color)
                                                    .setView(gridView)
                                                    .setAdapter(gridView.getAdapter(), new DialogInterface.OnClickListener()
                                                    {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int index)
                                                        {
                                                            Toast.makeText(getActivity(), String.valueOf(colors.get(index).getAsInteger(CalendarContract.Colors.COLOR)), Toast.LENGTH_SHORT)
                                                                    .show();
                                                        }
                                                    }).create().show();

                                            return true;
                                        }
                                    });

                                    preferenceCategory.addPreference(preference);
                                }
                            }

                        }
                    });
                }
            }
        });
        calendarViewModel.getCalendars();
    }

    @Override
    public void onCreatedPreferenceView()
    {
        //캘린더 색상 설정
    }

    public abstract class ColorPreferenceInterface
    {
        public void onCreatedPreferenceView(ContentValues calendar, ColorPreference colorPreference)
        {

        }
    }
}