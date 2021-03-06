package com.zerodsoft.scheduleweather.activity.preferences.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.preferences.SettingsActivity;

public class SearchingRadiusFragment extends PreferenceFragmentCompat
{
    private OnBackPressedCallback onBackPressedCallback;
    private SharedPreferences preferences;
    private SwitchPreference switchPreference;
    private ListPreference listPreference;
    private EditTextPreference editTextPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.app_settings_searching_radius, rootKey);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        preferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);

        switchPreference = findPreference(getString(R.string.preference_key_selected_custom_radius));
        listPreference = findPreference(getString(R.string.preference_key_searching_radius));
        editTextPreference = findPreference(getString(R.string.preference_key_radius_custom));

        switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                if ((Boolean) newValue)
                {
                    editTextPreference.setEnabled(true);
                    listPreference.setEnabled(false);

                    editTextPreference.setVisible(true);
                    listPreference.setVisible(false);
                } else
                {
                    editTextPreference.setEnabled(false);
                    listPreference.setEnabled(true);

                    editTextPreference.setVisible(false);
                    listPreference.setVisible(true);
                }
                return true;
            }
        });

        initValue();
    }

    private void initValue()
    {
        String radius = null;
        //반지름(기본)
        if (preferences.getBoolean(getString(R.string.preference_key_selected_custom_radius), false))
        {
            radius = preferences.getString(getString(R.string.preference_key_radius_custom), "");

            editTextPreference.setText(radius);
            switchPreference.setChecked(true);
        } else
        {
            radius = preferences.getString(getString(R.string.preference_key_searching_radius), "");

            CharSequence[] entryValues = listPreference.getEntryValues();
            for (int i = 0; i < entryValues.length; i++)
            {
                if (entryValues[i].toString().equals(radius))
                {
                    listPreference.setValueIndex(i);
                    break;
                }
            }
            switchPreference.setChecked(false);
        }
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
                getParentFragmentManager().popBackStack();
                ((SettingsActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_settings);
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

        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }
}
