package com.zerodsoft.scheduleweather.activity.map;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.zerodsoft.scheduleweather.activity.editevent.activity.EventActivity;
import com.zerodsoft.scheduleweather.kakaomap.KakaoMapActivity;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.ICatchedLocation;
import com.zerodsoft.scheduleweather.activity.map.fragment.map.MapFragment;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;

public class MapActivity extends KakaoMapActivity
{
    private OnBackPressedCallback onBackPressedCallback;

    public MapActivity()
    {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

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

        kakaoMapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.kakao_map_fragment);
        kakaoMapFragment.setiBottomSheet(this);
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        onBackPressedCallback.remove();
    }


}