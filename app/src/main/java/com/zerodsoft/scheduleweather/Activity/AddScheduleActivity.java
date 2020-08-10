package com.zerodsoft.scheduleweather.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.zerodsoft.scheduleweather.Room.AppDb;
import com.zerodsoft.scheduleweather.Room.DAO.LocationDAO;
import com.zerodsoft.scheduleweather.Room.DAO.ScheduleDAO;
import com.zerodsoft.scheduleweather.Room.DTO.AddressDTO;
import com.zerodsoft.scheduleweather.Room.DTO.PlaceDTO;
import com.zerodsoft.scheduleweather.Room.DTO.ScheduleDTO;
import com.zerodsoft.scheduleweather.Utility.Clock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddScheduleActivity extends AppCompatActivity implements NotificationFragment.OnNotificationTimeListener
{
    private Toolbar toolbar;
    private Spinner accountSpinner;
    private EditText subjectEditText;
    private Switch allDaySwitch;

    private TextView allDayValueTextView;
    private TextView startDateValueTextView;
    private TextView endDateValueTextView;
    private EditText contentEditText;
    private Button addLocationButton;

    private TextView locationTextView;
    private TextView notiValueTextView;
    private LinearLayout allDayLayout;
    private LinearLayout startDateLayout;
    private LinearLayout endDateLayout;

    private PlaceDTO placeDTO;
    private AddressDTO addressDTO;
    private int locType;

    private DatePickerFragment datePickerFragment;

    private Calendar allDay = Calendar.getInstance();
    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();

    private SelectedNotificationTime selectedNotificationTime;

    private boolean isAllDay = false;

    public static final int ADD_LOCATION_ACTIVITY = 0;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            getIntent().putExtras(msg.getData());
            setResult(RESULT_OK, getIntent());
            finish();
        }
    };


    public void clickedOkButton(long timeMilliSec, DATE_PICKER_CATEGORY datePickerCategory)
    {
        switch (datePickerCategory)
        {
            case START:
                startDate.setTimeInMillis(timeMilliSec);
                startDateValueTextView.setText(Clock.dateFormat2.format(startDate.getTime()));
                break;

            case END:
                endDate.setTimeInMillis(timeMilliSec);
                endDateValueTextView.setText(Clock.dateFormat2.format(endDate.getTime()));
                break;

            case ALL_DAY:
                allDay.setTimeInMillis(timeMilliSec);
                allDayValueTextView.setText(Clock.dateFormat3.format(allDay.getTime()));
                break;
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

        allDayValueTextView = (TextView) findViewById(R.id.allday_value_textview);
        startDateValueTextView = (TextView) findViewById(R.id.startdate_value_textview);
        endDateValueTextView = (TextView) findViewById(R.id.enddate_value_textview);

        contentEditText = (EditText) findViewById(R.id.content_multiline);
        addLocationButton = (Button) findViewById(R.id.add_location_button);
        locationTextView = (TextView) findViewById(R.id.location_right_textview);
        notiValueTextView = (TextView) findViewById(R.id.alarm_value_textview);

        allDayLayout = (LinearLayout) findViewById(R.id.allday_layout);
        startDateLayout = (LinearLayout) findViewById(R.id.startdate_layout);
        endDateLayout = (LinearLayout) findViewById(R.id.enddate_layout);

        allDayLayout.setVisibility(View.GONE);
        startDateLayout.setVisibility(View.VISIBLE);
        endDateLayout.setVisibility(View.VISIBLE);

        setAccountSpinner();
        setAllDaySwitch();
        setDateTextView();
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
                ScheduleDTO scheduleDTO = new ScheduleDTO();

                if (accountSpinner.getSelectedItemPosition() == 0)
                {
                    scheduleDTO.setCategory(ScheduleDTO.GOOGLE_CATEGORY);
                } else
                {
                    scheduleDTO.setCategory(ScheduleDTO.LOCAL_CATEGORY);
                }

                scheduleDTO.setSubject(subjectEditText.getText().toString());
                scheduleDTO.setContent(contentEditText.getText().toString());

                if (isAllDay)
                {
                    scheduleDTO.setStartDate((float) allDay.getTimeInMillis());
                    scheduleDTO.setEndDate((float) allDay.getTimeInMillis());
                } else
                {
                    scheduleDTO.setStartDate((float) startDate.getTimeInMillis());
                    scheduleDTO.setEndDate((float) endDate.getTimeInMillis());
                }

                Calendar calendar = Calendar.getInstance();
                long notificationTime = 0L;
                if (selectedNotificationTime != null)
                {
                    notificationTime = selectedNotificationTime.getTimeInMillis(calendar);
                }
                scheduleDTO.setNotiTime((float) notificationTime);
                scheduleDTO.setInsertedDate((float) calendar.getTimeInMillis());
                scheduleDTO.setUpdatedDate((float) calendar.getTimeInMillis());

                DBThread dbThread = new DBThread();
                dbThread.schedule = scheduleDTO;
                dbThread.start();

                break;
            case android.R.id.home:
                setResult(RESULT_CANCELED);
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

                if (isAllDay)
                {
                    // 하루 종일
                    allDayLayout.setVisibility(View.VISIBLE);
                    startDateLayout.setVisibility(View.GONE);
                    endDateLayout.setVisibility(View.GONE);
                } else
                {
                    allDayLayout.setVisibility(View.GONE);
                    startDateLayout.setVisibility(View.VISIBLE);
                    endDateLayout.setVisibility(View.VISIBLE);
                }

                startDateValueTextView.setText("시작");
                endDateValueTextView.setText("종료");
                allDayValueTextView.setText("시작/종료");

                startDate.clear();
                endDate.clear();
                allDay.clear();

                datePickerFragment = DatePickerFragment.getInstance();
                datePickerFragment.clearAllDate();
            }
        });
    }

    private void setDateTextView()
    {
        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //날짜 설정 다이얼로그 표시
                //하루종일인 경우 : 연월일, 아닌 경우 : 연월일시분
                datePickerFragment = DatePickerFragment.getInstance();

                switch (view.getId())
                {
                    case R.id.startdate_value_textview:
                        datePickerFragment.setDatePickerCategory(DATE_PICKER_CATEGORY.START);
                        break;
                    case R.id.enddate_value_textview:
                        datePickerFragment.setDatePickerCategory(DATE_PICKER_CATEGORY.END);
                        break;
                    case R.id.allday_value_textview:
                        datePickerFragment.setDatePickerCategory(DATE_PICKER_CATEGORY.ALL_DAY);
                        break;
                }

                datePickerFragment.show(getSupportFragmentManager(), DatePickerFragment.TAG);
            }
        };

        allDayValueTextView.setOnClickListener(onClickListener);
        startDateValueTextView.setOnClickListener(onClickListener);
        endDateValueTextView.setOnClickListener(onClickListener);
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
                NotificationFragment notificationFragment = NotificationFragment.getInstance();
                if (selectedNotificationTime != null)
                {
                    notificationFragment.setSelectedNotificationTime(selectedNotificationTime);
                }
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

                switch (locType)
                {
                    case DownloadData.ADDRESS:
                        addressDTO = bundle.getParcelable("addressDTO");
                        locName = addressDTO.getAddressName();
                        break;

                    case DownloadData.PLACE_KEYWORD:
                    case DownloadData.PLACE_CATEGORY:
                        placeDTO = bundle.getParcelable("placeDTO");
                        locName = placeDTO.getPlaceName();
                        break;
                }
                locationTextView.setText(locName);
                locationTextView.setVisibility(View.VISIBLE);

            } else if (resultCode == RESULT_CANCELED)
            {

            }
        }
    }

    class DBThread extends Thread
    {
        ScheduleDTO schedule;

        @Override
        public void run()
        {
            AppDb appDb = AppDb.getInstance(AddScheduleActivity.this);
            ScheduleDAO scheduleDAO = appDb.scheduleDAO();
            LocationDAO locationDAO = null;

            long scheduleId = scheduleDAO.insertNewSchedule(schedule);

            if (placeDTO != null)
            {
                locationDAO = appDb.locationDAO();

                placeDTO.setScheduleId((int) scheduleId);
                long placeId = locationDAO.insertPlace(placeDTO);
                scheduleDAO.updatePlaceId((int) scheduleId, (int) placeId);
            }
            if (addressDTO != null)
            {
                locationDAO = appDb.locationDAO();

                addressDTO.setScheduleId((int) scheduleId);
                long addressId = locationDAO.insertAddress(addressDTO);
                scheduleDAO.updateAddressId((int) scheduleId, (int) addressId);
            }

            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putLong("startDate", (long) schedule.getStartDate());
            bundle.putInt("scheduleId", (int) scheduleId);

            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }
}