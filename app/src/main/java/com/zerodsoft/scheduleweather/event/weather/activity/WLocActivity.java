package com.zerodsoft.scheduleweather.event.weather.activity;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.map.fragment.map.MapFragment;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.ICatchedLocation;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public class WLocActivity extends AppCompatActivity implements ICatchedLocation
{
    private OnBackPressedCallback onBackPressedCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w_loc);

        FragmentManager fragmentManager = getSupportFragmentManager();


        // Map프래그먼트 추가/실행
        fragmentManager.beginTransaction().add(R.id.locfinder_fragment_container, MapFragment.newInstance(this), MapFragment.TAG).commit();

        onBackPressedCallback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }


    @Override
    public LocationDTO getLocation()
    {
        return null;
    }

    @Override
    public void choiceLocation(String location)
    {

    }

    @Override
    public void choiceLocation(LocationDTO locationDTO)
    {

    }
}