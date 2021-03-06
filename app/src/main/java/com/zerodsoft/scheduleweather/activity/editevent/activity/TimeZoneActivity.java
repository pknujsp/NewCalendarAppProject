package com.zerodsoft.scheduleweather.activity.editevent.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.MenuItem;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.fragments.TimeZoneFragment;
import com.zerodsoft.scheduleweather.activity.editevent.interfaces.ITimeZone;

import java.util.TimeZone;

public class TimeZoneActivity extends AppCompatActivity implements ITimeZone
{
    private TimeZoneFragment timeZoneFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_zone);

        setSupportActionBar((Toolbar) findViewById(R.id.timezone_toolbar));

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        timeZoneFragment = new TimeZoneFragment();
        timeZoneFragment.setiTimeZone(this);
        Bundle bundle = new Bundle();
        bundle.putLong("startTime", getIntent().getLongExtra("startTime", 0L));
        timeZoneFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.timezone_fragment_container, timeZoneFragment).commit();
    }

    @Override
    public void onSelectedTimeZone(TimeZone timeZone)
    {
        getIntent().putExtra(CalendarContract.Events.EVENT_TIMEZONE, timeZone);
        setResult(RESULT_OK, getIntent());
        finish();
    }

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}