package com.zerodsoft.scheduleweather.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.zerodsoft.scheduleweather.Activity.MapActivity.MapActivity;
import com.zerodsoft.scheduleweather.Etc.SelectedNotificationTime;
import com.zerodsoft.scheduleweather.Fragment.DatePickerFragment;
import com.zerodsoft.scheduleweather.Fragment.NotificationFragment;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Retrofit.DownloadData;
import com.zerodsoft.scheduleweather.Room.DTO.AddressDTO;
import com.zerodsoft.scheduleweather.Room.DTO.PlaceDTO;
import com.zerodsoft.scheduleweather.Utility.Clock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddScheduleActivity extends AppCompatActivity implements DatePickerFragment.OnOkButtonClickListener, NotificationFragment.OnNotificationTimeListener
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
    private TextView notiValueTextView;
    private TextView startDateLeftTextView;
    private LinearLayout endDateLayout;

    private PlaceDTO placeDTO;
    private AddressDTO addressDTO;
    private int locType;

    public static Calendar startDate = null;
    public static Calendar endDate = null;

    private SelectedNotificationTime selectedNotificationTime;

    private boolean isAllDay = false;

    public static final int ADD_LOCATION_ACTIVITY = 0;

    @Override
    public void clickedOkButton(long timeMilliSec, DATE_PICKER_CATEGORY datePickerCategory)
    {
        if (datePickerCategory == DATE_PICKER_CATEGORY.START)
        {
            if (startDate == null)
            {
                startDate = Calendar.getInstance();
            }
            startDate.setTimeInMillis(timeMilliSec);
            startDateRightTextView.setText(Clock.dateFormat2.format(startDate.getTime()));
        } else if (datePickerCategory == DATE_PICKER_CATEGORY.END)
        {
            if (endDate == null)
            {
                endDate = Calendar.getInstance();
            }
            endDate.setTimeInMillis(timeMilliSec);
            endDateTextView.setText(Clock.dateFormat2.format(endDate.getTime()));
        } else
        {
            // allday
            if (startDate == null)
            {
                startDate = Calendar.getInstance();
            }
            startDate.setTimeInMillis(timeMilliSec);
            startDateRightTextView.setText(Clock.dateFormat3.format(startDate.getTime()));
        }
    }

    @Override
    public void onNotiTimeSelected(SelectedNotificationTime selectedNotificationTime)
    {
        this.selectedNotificationTime = selectedNotificationTime;
        notiValueTextView.setText(selectedNotificationTime.getResultStr());
    }

    public enum DATE_PICKER_CATEGORY
    {
        START, END, ALL_DAY
    }

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
        notiValueTextView = (TextView) findViewById(R.id.alarm_value_textview);
        startDateLeftTextView = (TextView) findViewById(R.id.start_date_left_textview);
        endDateLayout = (LinearLayout) findViewById(R.id.enddate_layout);

        setAccountSpinner();
        setAllDaySwitch();
        setDateEditText();
        setAddLocationButton();
        setNotiValue();

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(subjectEditText.getWindowToken(), 0);
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
                isAllDay = isChecked;

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

                startDate.clear();
                endDate.clear();
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
                DatePickerFragment datePickerFragment = DatePickerFragment.getInstance();
                datePickerFragment.setOnOkButtonClickListener(AddScheduleActivity.this);

                if (view.getId() == R.id.start_date_right_textview)
                {
                    if (isAllDay)
                    {
                        datePickerFragment.setDatePickerCategory(DATE_PICKER_CATEGORY.ALL_DAY);
                    } else
                    {
                        datePickerFragment.setDatePickerCategory(DATE_PICKER_CATEGORY.START);
                    }
                } else if (view.getId() == R.id.end_date_right_textview)
                {
                    datePickerFragment.setDatePickerCategory(DATE_PICKER_CATEGORY.END);
                }
                datePickerFragment.show(getSupportFragmentManager(), DatePickerFragment.TAG);
            }
        };

        startDateRightTextView.setOnClickListener(onClickListener);
        endDateTextView.setOnClickListener(onClickListener);
    }

    private void setNotiValue()
    {
        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //알람 시각을 설정하는 다이얼로그 표시
                //하루종일 인 경우와 아닌 경우 내용이 다르다
                NotificationFragment notificationFragment = new NotificationFragment();
                notificationFragment.show(getSupportFragmentManager(), NotificationFragment.TAG);
            }
        };

        notiValueTextView.setOnClickListener(onClickListener);
    }

    private void setAddLocationButton()
    {
        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //위치를 설정하는 액티비티 표시
                Intent intent = new Intent(AddScheduleActivity.this, MapActivity.class);
                startActivityForResult(intent, ADD_LOCATION_ACTIVITY);
            }
        };

        addLocationButton.setOnClickListener(onClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_LOCATION_ACTIVITY)
        {
            if (resultCode == RESULT_OK)
            {
                Bundle bundle = data.getExtras();
                locType = bundle.getInt("type");
                String locName = null;
                double latitude = 0f;
                double longitude = 0f;

                switch (locType)
                {
                    case DownloadData.ADDRESS:
                        addressDTO = bundle.getParcelable("addressDTO");
                        locName = addressDTO.getAddressName();
                        latitude = Double.valueOf(addressDTO.getLatitude());
                        longitude = Double.valueOf(addressDTO.getLongitude());
                        break;

                    case DownloadData.PLACE_KEYWORD:
                    case DownloadData.PLACE_CATEGORY:
                        placeDTO = bundle.getParcelable("placeDTO");
                        locName = placeDTO.getPlaceName();
                        latitude = Double.valueOf(placeDTO.getLatitude());
                        longitude = Double.valueOf(placeDTO.getLongitude());
                        break;
                }
                locationTextView.setText(locName);
                locationTextView.setVisibility(View.VISIBLE);

            } else if (resultCode == RESULT_CANCELED)
            {

            }
        }
    }
}