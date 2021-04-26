package com.zerodsoft.scheduleweather.event.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.provider.CalendarContract;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.ActivityNewInstanceMainBinding;

public class NewInstanceMainActivity extends AppCompatActivity
{
    private ActivityNewInstanceMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_instance_main);

        Bundle bundle = getIntent().getExtras();

        final int calendarId = bundle.getInt(CalendarContract.Instances.CALENDAR_ID);
        final long instanceId = bundle.getLong(CalendarContract.Instances._ID);
        final long eventId = bundle.getLong(CalendarContract.Instances.EVENT_ID);
        final long begin = bundle.getLong(CalendarContract.Instances.BEGIN);
        final long end = bundle.getLong(CalendarContract.Instances.END);

        NewInstanceMainFragment newInstanceMainFragment = new NewInstanceMainFragment(calendarId, eventId, instanceId, begin, end);
        getSupportFragmentManager().beginTransaction().add(binding.fragmentContainer.getId(), newInstanceMainFragment, NewInstanceMainFragment.TAG).commitNow();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}