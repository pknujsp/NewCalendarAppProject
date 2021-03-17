package com.zerodsoft.scheduleweather.kakaomap.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.ActivityKakaoMapBinding;
import com.zerodsoft.scheduleweather.kakaomap.fragment.KakaoMapFragment;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.BottomSheetButtonOnClickListener;

public class KakaoMapActivity extends AppCompatActivity implements BottomSheetButtonOnClickListener
{
    protected ActivityKakaoMapBinding binding;
    protected KakaoMapFragment kakaoMapFragment;

    public KakaoMapActivity()
    {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_kakao_map);

        kakaoMapFragment = (KakaoMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        kakaoMapFragment.setBottomSheetButtonOnClickListener(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onSelectedLocation()
    {

    }

    @Override
    public void onRemovedLocation()
    {

    }
}