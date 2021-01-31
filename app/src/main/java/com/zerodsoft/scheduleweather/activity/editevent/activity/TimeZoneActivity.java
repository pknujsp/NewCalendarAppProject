package com.zerodsoft.scheduleweather.activity.editevent.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.adapter.TimeZoneRecyclerViewAdapter;
import com.zerodsoft.scheduleweather.activity.editevent.interfaces.ITimeZone;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TimeZoneActivity extends AppCompatActivity implements ITimeZone
{
    private TimeZoneRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private EditText searchEditText;

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

        recyclerView = (RecyclerView) findViewById(R.id.timezone_list);
        searchEditText = (EditText) findViewById(R.id.search_timezone);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        final String[] timeZones = TimeZone.getAvailableIDs();
        final List<TimeZone> timeZoneList = new ArrayList<>();

        for (String v : timeZones)
        {
            timeZoneList.add(TimeZone.getTimeZone(v));
        }

        Date startDate = (Date) getIntent().getSerializableExtra("startDate");

        adapter = new TimeZoneRecyclerViewAdapter(this, timeZoneList, startDate);
        recyclerView.setAdapter(adapter);

        searchEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                // 실시간 검색
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });
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