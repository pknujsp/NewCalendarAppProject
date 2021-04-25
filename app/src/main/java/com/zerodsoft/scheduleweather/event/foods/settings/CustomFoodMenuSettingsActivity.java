package com.zerodsoft.scheduleweather.event.foods.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.preferences.customfoodmenu.fragment.CustomFoodMenuSettingsFragment;

public class CustomFoodMenuSettingsActivity extends AppCompatActivity
{
    private CustomFoodMenuSettingsFragment customFoodMenuSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_food_menu_settings);

        customFoodMenuSettingsFragment = (CustomFoodMenuSettingsFragment) getSupportFragmentManager().findFragmentById(R.id.custom_food_settings_fragment);
    }

    @Override
    public void onBackPressed()
    {
        setResult(customFoodMenuSettingsFragment.isEdited() ? RESULT_OK : RESULT_CANCELED);
        super.onBackPressed();
    }
}