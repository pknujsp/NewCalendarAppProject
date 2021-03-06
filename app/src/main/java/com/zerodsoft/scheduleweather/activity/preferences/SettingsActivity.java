package com.zerodsoft.scheduleweather.activity.preferences;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.preferences.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.settings, new SettingsFragment())
                    .commit();
        }
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref)
    {
        if (pref.getTitle().toString().equals(getString(R.string.settings_color_of_calendar)))
        {
            getSupportActionBar().setTitle(getString(R.string.settings_color_of_calendar));
        } else if (pref.getTitle().toString().equals(getString(R.string.settings_default_timerange)))
        {
            getSupportActionBar().setTitle(getString(R.string.settings_default_timerange));
        } else if (pref.getTitle().toString().equals(getString(R.string.preference_key_searching_radius)))
        {
            getSupportActionBar().setTitle(getString(R.string.preference_key_searching_radius));
        }


        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }
}