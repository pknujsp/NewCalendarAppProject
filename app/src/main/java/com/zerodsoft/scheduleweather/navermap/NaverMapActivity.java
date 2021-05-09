package com.zerodsoft.scheduleweather.navermap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.ActivityNaverMapBinding;
import com.zerodsoft.scheduleweather.navermap.interfaces.PlacesItemBottomSheetButtonOnClickListener;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;

public class NaverMapActivity extends AppCompatActivity implements PlacesItemBottomSheetButtonOnClickListener
{
    protected ActivityNaverMapBinding binding;
    protected NaverMapFragment naverMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_naver_map);


        naverMapFragment = (NaverMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        naverMapFragment.setPlacesItemBottomSheetButtonOnClickListener(this);
    }

    @Override
    public void onSelectedLocation(KakaoLocalDocument kakaoLocalDocument)
    {

    }

    @Override
    public void onRemovedLocation()
    {

    }
}