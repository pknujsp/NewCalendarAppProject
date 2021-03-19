package com.zerodsoft.scheduleweather.event.places.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.event.places.fragment.TestMapFragment;

public class PlacesActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        TestMapFragment testMapFragment = new TestMapFragment();
        testMapFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.places_fragment_container, testMapFragment, TestMapFragment.TAG).commit();
    }
}