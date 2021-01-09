package com.zerodsoft.scheduleweather.activity.editevent.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.activity.editevent.interfaces.IEventRepeat;
import com.zerodsoft.scheduleweather.activity.editevent.interfaces.IEventTime;
import com.zerodsoft.scheduleweather.activity.map.MapActivity;
import com.zerodsoft.scheduleweather.databinding.ActivityEventBinding;
import com.zerodsoft.scheduleweather.activity.editevent.fragment.DatePickerFragment;
import com.zerodsoft.scheduleweather.activity.editevent.fragment.ReminderFragment;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.googlecalendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.RecurrenceRule;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class EventActivity extends AppCompatActivity implements ReminderFragment.OnNotificationTimeListener, IEventTime, IEventRepeat
{
    /*
    필요한 프래그먼트 다이얼로그 : 시간(o), 시간대, 반복, 참석자, 알림, 캘린더
    필요한  다이얼로그 : 접근 범위(o), 유효성(o)
     */
    public static final int MODIFY_EVENT = 60;
    public static final int NEW_EVENT = 50;
    public static final int MODIFY_LOCATION = 70;
    public static final int NEW_LOCATION = 20;
    public static final int LOCATION_DELETED = 80;
    public static final int LOCATION_SELECTED = 90;
    public static final int LOCATION_RESELECTED = 100;

    public static final int REQUEST_RECURRENCE = 110;
    public static final int REQUEST_TIMEZONE = 120;

    private ActivityEventBinding activityBinding;
    private CalendarViewModel viewModel;
    private DatePickerFragment datePickerFragment;
    private ReminderFragment reminderFragment;

    private ContentValues modifiedValues;
    private ContentValues newEventValues;
    private ContentValues savedEventValues;

    private AlertDialog accessLevelDialog;
    private AlertDialog availabilityDialog;
    private AlertDialog repeaterDialog;

    private long startDatetimeMillis;
    private long endDateMillis;

    private String[] repeaterList;

    public EventActivity()
    {

    }

    @Override
    public void onReminderSelected()
    {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.schuedule_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.save_schedule_button:
                // 제목, 날짜 필수 입력
                setResult(RESULT_CANCELED);
                finish();
                break;
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_event);
        init();

        activityBinding.recurrenceLayout.recurrenceValue.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(EventActivity.this, RecurrenceActivity.class);
                // 반복 룰과 이벤트의 시작 시간 전달
                String recurrenceRule = null;

                if (newEventValues != null)
                {
                    recurrenceRule = newEventValues.getAsString(CalendarContract.Events.RRULE);
                } else if (modifiedValues != null)
                {
                    recurrenceRule = modifiedValues.getAsString(CalendarContract.Events.RRULE);
                }

                if (recurrenceRule == null)
                {
                    recurrenceRule = "";
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(startDatetimeMillis);

                intent.putExtra("recurrenceRule", recurrenceRule);
                intent.putExtra("eventStartDateTime", calendar);
                startActivityForResult(intent, REQUEST_RECURRENCE);
            }
        });

        activityBinding.accesslevelLayout.accesslevel.setOnClickListener(new View.OnClickListener()
        {
            final String[] items = {getString(R.string.access_default), getString(R.string.access_public), getString(R.string.access_private)};

            @Override
            public void onClick(View view)
            {
                int checkedItem = 0;

                if (newEventValues != null)
                {
                    if (newEventValues.getAsInteger(CalendarContract.Events.ACCESS_LEVEL) != null)
                    {
                        checkedItem = getAccessLevelItemIndex(newEventValues.getAsInteger(CalendarContract.Events.ACCESS_LEVEL));
                    }
                } else if (modifiedValues != null)
                {
                    if (modifiedValues.getAsInteger(CalendarContract.Events.ACCESS_LEVEL) != null)
                    {
                        checkedItem = getAccessLevelItemIndex(modifiedValues.getAsInteger(CalendarContract.Events.ACCESS_LEVEL));
                    }
                }

                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(EventActivity.this);
                dialogBuilder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item)
                    {
                        int accessLevel = 0;

                        switch (item)
                        {
                            case 0:
                                accessLevel = CalendarContract.Events.ACCESS_DEFAULT;
                                break;
                            case 1:
                                accessLevel = CalendarContract.Events.ACCESS_PUBLIC;
                                break;
                            case 2:
                                accessLevel = CalendarContract.Events.ACCESS_PRIVATE;
                                break;
                        }

                        if (newEventValues != null)
                        {
                            newEventValues.put(CalendarContract.Events.ACCESS_LEVEL, accessLevel);
                        } else if (modifiedValues != null)
                        {
                            modifiedValues.put(CalendarContract.Events.ACCESS_LEVEL, accessLevel);
                        }
                        activityBinding.accesslevelLayout.accesslevel.setText(items[item]);
                        accessLevelDialog.dismiss();
                    }
                }).setTitle(getString(R.string.accesslevel));
                accessLevelDialog = dialogBuilder.create();
                accessLevelDialog.show();
            }
        });

        activityBinding.availabilityLayout.availability.setOnClickListener(new View.OnClickListener()
        {
            final String[] items = {getString(R.string.busy), getString(R.string.free)};

            @Override
            public void onClick(View view)
            {
                int checkedItem = 1;

                if (newEventValues != null)
                {
                    if (newEventValues.getAsInteger(CalendarContract.Events.AVAILABILITY) != null)
                    {
                        checkedItem = newEventValues.getAsInteger(CalendarContract.Events.AVAILABILITY);
                    }
                } else if (modifiedValues != null)
                {
                    if (modifiedValues.getAsInteger(CalendarContract.Events.AVAILABILITY) != null)
                    {
                        checkedItem = modifiedValues.getAsInteger(CalendarContract.Events.AVAILABILITY);
                    }
                }

                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(EventActivity.this);
                dialogBuilder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item)
                    {
                        int availability = 0;

                        switch (item)
                        {
                            case 0:
                                availability = CalendarContract.Events.AVAILABILITY_BUSY;
                                break;
                            case 1:
                                availability = CalendarContract.Events.AVAILABILITY_FREE;
                                break;
                        }

                        if (newEventValues != null)
                        {
                            newEventValues.put(CalendarContract.Events.AVAILABILITY, availability);
                        } else if (modifiedValues != null)
                        {
                            modifiedValues.put(CalendarContract.Events.AVAILABILITY, availability);
                        }
                        activityBinding.availabilityLayout.availability.setText(items[item]);
                        availabilityDialog.dismiss();
                    }
                }).setTitle(getString(R.string.availability));
                availabilityDialog = dialogBuilder.create();
                availabilityDialog.show();
            }
        });

        activityBinding.timeLayout.timezone.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(EventActivity.this, TimeZoneActivity.class);
                intent.putExtra("startDate", new Date(startDatetimeMillis));
                startActivityForResult(intent, REQUEST_TIMEZONE);
            }
        });

        viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);

        switch (getIntent().getIntExtra("requestCode", 0))
        {
            case NEW_EVENT:
                newEventValues = new ContentValues();
                Calendar calendar = Calendar.getInstance(ClockUtil.TIME_ZONE);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

                startDatetimeMillis = calendar.getTimeInMillis();
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                endDateMillis = calendar.getTimeInMillis();
                setDateTimeText();

                calendar = null;

                // 시간, 시간대, 반복, 알림, 공개범위, 유효성 기본값 설정
                newEventValues.put(CalendarContract.Events.DTSTART, startDatetimeMillis);
                newEventValues.put(CalendarContract.Events.DTEND, startDatetimeMillis);
                newEventValues.put(CalendarContract.Events.EVENT_TIMEZONE, startDatetimeMillis);
                newEventValues.put(CalendarContract.Events.EVENT_END_TIMEZONE, startDatetimeMillis);
                newEventValues.put(CalendarContract.Events.RRULE, "");
                newEventValues.put(CalendarContract.Events.RDATE, "");
                newEventValues.put(CalendarContract.Events.HAS_ATTENDEE_DATA, 0);
                newEventValues.put(CalendarContract.Events.DURATION, "");
                newEventValues.put(CalendarContract.Events.HAS_ALARM, 0);
                newEventValues.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_DEFAULT);
                newEventValues.put(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE);
                break;

            case MODIFY_EVENT:
                Intent intent = getIntent();

                int calendarId = intent.getIntExtra("calendarId", 0);
                int eventId = intent.getIntExtra("eventId", 0);
                String accountName = intent.getStringExtra("accountName");

                // 이벤트, 알림을 가져온다
                viewModel.getEvent(calendarId, eventId, accountName);
                viewModel.getReminders(savedEventValues.getAsLong(CalendarContract.Events._ID));

                viewModel.getEventLiveData().observe(this, new Observer<DataWrapper<ContentValues>>()
                {
                    @Override
                    public void onChanged(DataWrapper<ContentValues> eventDtoDataWrapper)
                    {
                        if (eventDtoDataWrapper.getData() != null)
                        {
                            savedEventValues = eventDtoDataWrapper.getData();
                            modifiedValues = new ContentValues();

                            // 제목, 캘린더, 시간, 시간대, 반복, 알림, 설명, 위치, 공개범위, 유효성, 참석자
                            // 캘린더, 시간대, 참석자 정보는 따로 불러온다.

                            //제목
                            activityBinding.titleLayout.title.setText(savedEventValues.getAsString(CalendarContract.Events.TITLE));
                            //캘린더

                            //시간
                            startDatetimeMillis = savedEventValues.getAsLong(CalendarContract.Events.DTSTART);
                            endDateMillis = savedEventValues.getAsLong(CalendarContract.Events.DTEND);
                            activityBinding.timeLayout.timeAlldaySwitch.setChecked(savedEventValues.getAsBoolean(CalendarContract.Events.ALL_DAY));
                            setDateTimeText();
                            // 시간대

                            // 반복
                            // 알림
                            // 설명
                            activityBinding.descriptionLayout.description.setText(savedEventValues.getAsString(CalendarContract.Events.DESCRIPTION));
                            // 위치
                            activityBinding.locationLayout.location.setText(savedEventValues.getAsString(CalendarContract.Events.EVENT_LOCATION));
                            // 참석자

                            // 공개 범위 표시
                            activityBinding.accesslevelLayout.accesslevel.callOnClick();
                            // 유효성 표시
                            activityBinding.availabilityLayout.availability.callOnClick();

                            modifiedValues.put(CalendarContract.Events.ACCESS_LEVEL, savedEventValues.getAsInteger(CalendarContract.Events.ACCESS_LEVEL));
                            modifiedValues.put(CalendarContract.Events.AVAILABILITY, savedEventValues.getAsInteger(CalendarContract.Events.AVAILABILITY));
                        }
                    }
                });

                viewModel.getReminderLiveData().observe(this, new Observer<DataWrapper<List<ContentValues>>>()
                {
                    @Override
                    public void onChanged(DataWrapper<List<ContentValues>> listDataWrapper)
                    {
                        if (listDataWrapper.getData() != null)
                        {
                            List<ContentValues> reminders = listDataWrapper.getData();
                            if (!reminders.isEmpty())
                            {
                                ContentValues reminder = reminders.get(0);

                                int minute = reminder.getAsInteger(CalendarContract.Reminders.MINUTES);
                            }
                        }
                    }
                });
        }


    }

    private void init()
    {
        Toolbar toolbar = activityBinding.eventToolbar;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.new_event);
        actionBar.setDisplayHomeAsUpEnabled(true);

        EditTextWatcher editTextWatcher = new EditTextWatcher();
        activityBinding.titleLayout.title.addTextChangedListener(editTextWatcher);
        activityBinding.titleLayout.title.setOnFocusChangeListener(editTextWatcher);
        activityBinding.descriptionLayout.description.addTextChangedListener(editTextWatcher);
        activityBinding.descriptionLayout.description.setOnFocusChangeListener(editTextWatcher);

        setAllDaySwitch();
        setTimeView();
        setLocationView();
        setReminder();

        datePickerFragment = DatePickerFragment.newInstance(EventActivity.this);
        reminderFragment = ReminderFragment.newInstance();
    }

    private int getRruleItemIndex(String rrule)
    {
        // 설정 안함, 매일, 매주, 매월, 매년, 직접설정
        // FREQ=MONTHLY
        switch (rrule)
        {
            case "":
                return 0;
            case "FREQ=DAILY":
                return 1;
            case "FREQ=WEEKLY":
                return 2;
            case "FREQ=MONTHLY":
                return 3;
            case "FREQ=YEARLY":
                return 4;
            default:
                return 5;
        }
    }

    private int getAccessLevelItemIndex(int level)
    {
        if (level == CalendarContract.Events.ACCESS_DEFAULT)
        {
            return 0;
        } else if (level == CalendarContract.Events.ACCESS_PUBLIC)
        {
            return 1;
        } else if (level == CalendarContract.Events.ACCESS_PRIVATE)
        {
            return 2;
        } else
        {
            return 3;
        }
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
        DatePickerFragment.close();
    }

    private void setAllDaySwitch()
    {
        activityBinding.timeLayout.timeAlldaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                setDateTimeText();
            }
        });
    }

    private void setTimeView()
    {
        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onClick(View view)
            {
                //날짜 설정 다이얼로그 표시
                //하루종일인 경우 : 연월일, 아닌 경우 : 연월일시분
                switch (view.getId())
                {
                    case R.id.startdate:
                        datePickerFragment.initData(startDatetimeMillis
                                , DatePickerFragment.START, activityBinding.timeLayout.timeAlldaySwitch.isChecked());
                        break;
                    case R.id.enddate:
                        datePickerFragment.initData(endDateMillis
                                , DatePickerFragment.END, activityBinding.timeLayout.timeAlldaySwitch.isChecked());
                        break;
                }
                datePickerFragment.show(getSupportFragmentManager(), DatePickerFragment.TAG);
            }
        };

        activityBinding.timeLayout.startdate.setOnClickListener(onClickListener);
        activityBinding.timeLayout.enddate.setOnClickListener(onClickListener);
    }

    private void setReminder()
    {
        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // reminderFragment.init(activityBinding.getScheduleDto());
                // reminderFragment.show(getSupportFragmentManager(), ReminderFragment.TAG);
            }
        };

        activityBinding.reminderLayout.reminder.setOnClickListener(onClickListener);
    }

    private void setLocationView()
    {
        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //위치를 설정하는 액티비티 표시
                Intent intent = new Intent(EventActivity.this, MapActivity.class);

                String location = null;
                if (newEventValues != null)
                {
                    location = newEventValues.getAsString(CalendarContract.Events.EVENT_LOCATION);
                } else if (modifiedValues != null)
                {
                    location = modifiedValues.getAsString(CalendarContract.Events.EVENT_LOCATION);
                }
                int requestCode = 0;

                if (location != null)
                {
                    requestCode = MODIFY_LOCATION;
                    intent.putExtra("location", location);
                } else
                {
                    requestCode = NEW_LOCATION;
                }

                intent.putExtra("requestCode", requestCode);
                startActivityForResult(intent, requestCode);
            }
        };
        activityBinding.locationLayout.location.setOnClickListener(onClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_RECURRENCE)
        {
            if (resultCode == RESULT_OK)
            {
                String rrule = data.getStringExtra("recurrenceRule");
                RecurrenceRule recurrenceRule = new RecurrenceRule();
                recurrenceRule.separateValues(rrule);
                activityBinding.recurrenceLayout.recurrenceValue.setText(recurrenceRule.interpret(getApplicationContext()));

                if (newEventValues != null)
                {
                    newEventValues.put(CalendarContract.Events.RRULE, rrule);
                } else if (modifiedValues != null)
                {
                    modifiedValues.put(CalendarContract.Events.RRULE, rrule);
                }
            } else
            {

            }
        } else if (requestCode == NEW_LOCATION)
        {
        } else if (requestCode == MODIFY_LOCATION)
        {
        } else if (requestCode == REQUEST_TIMEZONE)
        {
            TimeZone timeZone = (TimeZone) data.getSerializableExtra("timeZone");
            activityBinding.timeLayout.timezone.setText(timeZone.getID());

            if (newEventValues != null)
            {
                newEventValues.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
            } else if (modifiedValues != null)
            {
                modifiedValues.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
            }
        }
    }

    @Override
    public void onSelectedTime(long dateTime, int dateType)
    {
        if (dateType == DatePickerFragment.START)
        {
            startDatetimeMillis = dateTime;
            if (newEventValues != null)
            {
                newEventValues.put(CalendarContract.Events.DTSTART, startDatetimeMillis);
            } else if (modifiedValues != null)
            {
                modifiedValues.put(CalendarContract.Events.DTSTART, startDatetimeMillis);
            }
        } else
        {
            endDateMillis = dateTime;
            if (newEventValues != null)
            {
                newEventValues.put(CalendarContract.Events.DTEND, endDateMillis);
            } else if (modifiedValues != null)
            {
                modifiedValues.put(CalendarContract.Events.DTEND, endDateMillis);
            }
        }
        setDateTimeText();
    }

    private void setDateTimeText()
    {
        Date start = new Date(startDatetimeMillis);
        Date end = new Date(endDateMillis);

        activityBinding.timeLayout.startdate.setText(activityBinding.timeLayout.timeAlldaySwitch.isChecked()
                ? ClockUtil.YYYY_년_M_월_D_일_E.format(start)
                : ClockUtil.DATE_FORMAT_NOT_ALLDAY.format(start));

        activityBinding.timeLayout.enddate.setText(activityBinding.timeLayout.timeAlldaySwitch.isChecked()
                ? ClockUtil.YYYY_년_M_월_D_일_E.format(end)
                : ClockUtil.DATE_FORMAT_NOT_ALLDAY.format(end));
    }

    @Override
    public long getDateTime(int dateType)
    {
        return dateType == DatePickerFragment.START ? startDatetimeMillis : endDateMillis;
    }

    class EditTextWatcher implements TextWatcher, View.OnFocusChangeListener
    {
        int focusedViewId = View.NO_ID;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {
            // 텍스트가 변경될 때 마다 수행
        }

        @Override
        public void afterTextChanged(Editable editable)
        {
            // 텍스트가 변경된 후 수행
            switch (focusedViewId)
            {
                case R.id.title:
                    newEventValues.put(CalendarContract.Events.TITLE, editable.toString());
                    break;
                case R.id.description:
                    newEventValues.put(CalendarContract.Events.DESCRIPTION, editable.toString());
                    break;
            }
        }

        @Override
        public void onFocusChange(View view, boolean b)
        {
            if (b)
            {
                // focusing
                focusedViewId = view.getId();
            }
        }

    }
}

