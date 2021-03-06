package com.zerodsoft.scheduleweather.activity.editevent.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.adapter.TimeZoneRecyclerViewAdapter;
import com.zerodsoft.scheduleweather.activity.editevent.fragments.TimeZoneFragment;
import com.zerodsoft.scheduleweather.activity.editevent.interfaces.ITimeZone;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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

        timeZoneFragment = (TimeZoneFragment) getSupportFragmentManager().findFragmentById(R.id.timezone_fragment);
        timeZoneFragment.setiTimeZone(this);
        Bundle bundle = new Bundle();
        bundle.putLong("startTime", getIntent().getLongExtra("startTime", 0L));
        timeZoneFragment.setArguments(bundle);
    }

    @Override
    public void onSelectedTimeZone(TimeZone timeZone)
    {
        getIntent().putExtra("timeZone", timeZone);
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