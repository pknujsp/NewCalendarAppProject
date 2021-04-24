package com.zerodsoft.scheduleweather.event.places.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.event.places.map.PlacesMapFragmentNaver;

public class PlacesActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        PlacesMapFragmentNaver placesMapFragmentNaver = new PlacesMapFragmentNaver();
        placesMapFragmentNaver.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.places_fragment_container, placesMapFragmentNaver, PlacesMapFragmentNaver.TAG).commit();
    }
}