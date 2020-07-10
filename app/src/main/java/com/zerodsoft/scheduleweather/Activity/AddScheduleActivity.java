package com.zerodsoft.scheduleweather.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.zerodsoft.scheduleweather.Fragment.DatePickerFragment;
import com.zerodsoft.scheduleweather.R;

import java.util.ArrayList;
import java.util.List;

public class AddScheduleActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    private Spinner accountSpinner;
    private EditText subjectEditText;
    private Switch allDaySwitch;
    private TextView startDateRightTextView;
    private TextView endDateTextView;
    private EditText contentEditText;
    private Button addLocationButton;
    private TextView locationTextView;
    private TextView alarmTextView;
    private TextView startDateLeftTextView;
    private LinearLayout endDateLayout;

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
        startDateRightTextView = (TextView) findViewById(R.id.start_date_right_textview);
        endDateTextView = (TextView) findViewById(R.id.end_date_right_textview);
        contentEditText = (EditText) findViewById(R.id.content_multiline);
        addLocationButton = (Button) findViewById(R.id.add_location_button);
        locationTextView = (TextView) findViewById(R.id.location_right_textview);
        alarmTextView = (TextView) findViewById(R.id.alarm_right_textview);
        startDateLeftTextView = (TextView) findViewById(R.id.start_date_left_textview);
        endDateLayout = (LinearLayout) findViewById(R.id.enddate_layout);

        setAccountSpinner();
        setAllDaySwitch();
        setDateEditText();
        setAddLocationButton();
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

    private void setAccountSpinner()
    {
        List<String> accountList = new ArrayList<>();
        accountList.add("GOOGLE");
        accountList.add("LOCAL");

        SpinnerAdapter adapter = new ArrayAdapter<>(AddScheduleActivity.this, android.R.layout.simple_spinner_dropdown_item, accountList);
        accountSpinner.setAdapter(adapter);
    }

    private void setAllDaySwitch()
    {
        allDaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if (isChecked)
                {
                    // 하루 종일
                    startDateLeftTextView.setText(getResources().getString(R.string.start_date_text_view_allday));
                    endDateLayout.setVisibility(View.GONE);
                } else
                {
                    startDateLeftTextView.setText(getResources().getString(R.string.start_date_text_view_not_allday));
                    endDateLayout.setVisibility(View.VISIBLE);
                }
                startDateRightTextView.setText("");
                endDateTextView.setText("");
            }
        });
    }

    private void setDateEditText()
    {
        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //날짜 설정 다이얼로그 표시
                //하루종일인 경우 : 연월일, 아닌 경우 : 연원일시분
                if (view.getId() == R.id.start_date_right_textview)
                {
                    DatePickerFragment datePickerFragment = DatePickerFragment.getInstance();
                    datePickerFragment.show(getSupportFragmentManager(), DatePickerFragment.TAG);
                }
            }
        };

        startDateRightTextView.setOnClickListener(onClickListener);
        endDateTextView.setOnClickListener(onClickListener);
    }

    private void setAlarmEditText()
    {
        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //알람 시각을 설정하는 다이얼로그 표시
                //하루종일 인 경우와 아닌 경우 내용이 다르다
            }
        };

        alarmTextView.setOnClickListener(onClickListener);
    }

    private void setAddLocationButton()
    {
        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //위치를 설정하는 액티비티 표시
            }
        };

        addLocationButton.setOnClickListener(onClickListener);
    }
}