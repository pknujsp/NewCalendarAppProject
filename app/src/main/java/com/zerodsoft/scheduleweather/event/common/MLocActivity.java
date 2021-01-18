package com.zerodsoft.scheduleweather.event.common;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.kakaomap.KakaoMapActivity;

import java.util.Map;

public class MLocActivity extends KakaoMapActivity
{
    private String savedLocation;
    private String accountName;
    private int calendarId;
    private int eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        savedLocation = intent.getStringExtra("location");
        accountName = intent.getStringExtra("accountName");
        calendarId = intent.getIntExtra("calendarId", 0);
        eventId = intent.getIntExtra("eventId", 0);

        // 검색 결과가 바로 나타난다.
    }

    @Override
    public void onSelectLocation()
    {
        super.onSelectLocation();
    }

    @Override
    public void onRemoveLocation()
    {
        super.onRemoveLocation();
    }
}