package com.zerodsoft.scheduleweather.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.zerodsoft.scheduleweather.R;

public class AddScheduleActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    private Spinner accountSpinner;
    private EditText subjectEditText;
    private Switch allDaySwitch;
    private EditText startDateEditText;
    private EditText endDateEditText;
    private EditText contentEditText;
    private Button addLocationButton;
    private EditText locationEditText;
    private EditText alarmEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        toolbar = (Toolbar) findViewById(R.id.add_schedule_toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        accountSpinner = (Spinner) findViewById(R.id.account_spinner);
        subjectEditText = (EditText) findViewById(R.id.subject_edittext);
        allDaySwitch = (Switch) findViewById(R.id.schedule_allday_switch);
        startDateEditText = (EditText) findViewById(R.id.start_date_edittext);
        endDateEditText = (EditText) findViewById(R.id.end_date_edittext);
        contentEditText = (EditText) findViewById(R.id.content_multiline);
        addLocationButton = (Button) findViewById(R.id.add_location_button);
        locationEditText = (EditText) findViewById(R.id.location_edittext);
        alarmEditText = (EditText) findViewById(R.id.alarm_edittext);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.add_schedule_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.save:
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}