package com.zerodsoft.scheduleweather.activity.editevent.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.zerodsoft.scheduleweather.activity.editevent.adapter.CalendarListAdapter;
import com.zerodsoft.scheduleweather.activity.editevent.value.EventDefaultValue;
import com.zerodsoft.scheduleweather.activity.editevent.interfaces.IEventRepeat;
import com.zerodsoft.scheduleweather.activity.map.SelectLocationActivity;
import com.zerodsoft.scheduleweather.databinding.ActivityEventBinding;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.utility.CalendarEventUtil;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.RecurrenceRule;
import com.zerodsoft.scheduleweather.utility.model.ReminderDto;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class EditEventActivity extends AppCompatActivity implements IEventRepeat
{
    // 추가 작업 필요 : 알림, 참석자
    public static final int MODIFY_EVENT = 60;
    public static final int NEW_EVENT = 50;
    public static final int MODIFY_LOCATION = 70;
    public static final int NEW_LOCATION = 20;
    public static final int LOCATION_DELETED = 80;
    public static final int LOCATION_SELECTED = 90;
    public static final int LOCATION_RESELECTED = 100;

    public static final int REQUEST_RECURRENCE = 110;
    public static final int REQUEST_TIMEZONE = 120;
    public static final int REQUEST_REMINDER = 130;

    private static final int START_DATETIME = 200;
    private static final int END_DATETIME = 210;

    private boolean is24HourSystem = true;

    private String[] accessLevelItems;
    private String[] availabilityItems;

    private ActivityEventBinding activityBinding;
    private CalendarViewModel viewModel;

    private ContentValues modifiedValues;
    private ContentValues newEventValues;
    private ContentValues savedEventValues;

    private AlertDialog accessLevelDialog;
    private AlertDialog availabilityDialog;
    private AlertDialog calendarDialog;

    private MaterialTimePicker timePicker;
    private MaterialDatePicker<Pair<Long, Long>> datePicker;

    private int requestCode;

    private long startDateMillis;
    private long endDateMillis;
    private int startTimeHour;
    private int startTimeMinute;
    private int endTimeHour;
    private int endTimeMinute;

    private EventDefaultValue eventDefaultValue;

    private String[] repeaterList;
    private List<ContentValues> calendarList;


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.schuedule_edit_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.save_schedule_button:

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

        accessLevelItems = new String[]{getString(R.string.access_default), getString(R.string.access_public), getString(R.string.access_private)};
        availabilityItems = new String[]{getString(R.string.busy), getString(R.string.free)};

        viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        viewModel.init(this);

        activityBinding.recurrenceLayout.recurrenceValue.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(EditEventActivity.this, RecurrenceActivity.class);
                // 반복 룰과 이벤트의 시작 시간 전달
                String recurrenceRule = getValue(CalendarContract.Events.RRULE) != null ? (String) getValue(CalendarContract.Events.RRULE)
                        : "";

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(startDateMillis);
                calendar.set(Calendar.HOUR_OF_DAY, startTimeHour);
                calendar.set(Calendar.MINUTE, 0);

                intent.putExtra("recurrenceRule", recurrenceRule);
                intent.putExtra("eventStartDateTime", calendar);
                startActivityForResult(intent, REQUEST_RECURRENCE);
            }
        });

        activityBinding.accesslevelLayout.accesslevel.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                int checkedItem = getValue(CalendarContract.Events.ACCESS_LEVEL) != null ? (Integer) getValue(CalendarContract.Events.ACCESS_LEVEL)
                        : 0;

                if (checkedItem == 3)
                {
                    checkedItem = 1;
                }

                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(EditEventActivity.this);
                dialogBuilder.setSingleChoiceItems(accessLevelItems, checkedItem, new DialogInterface.OnClickListener()
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

                        putValue(CalendarContract.Events.ACCESS_LEVEL, accessLevel);
                        setAccessLevelText();
                        accessLevelDialog.dismiss();
                    }
                }).setTitle(getString(R.string.accesslevel));
                accessLevelDialog = dialogBuilder.create();
                accessLevelDialog.show();
            }
        });

        activityBinding.availabilityLayout.availability.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                int checkedItem = getValue(CalendarContract.Events.AVAILABILITY) != null ? (Integer) getValue(CalendarContract.Events.AVAILABILITY)
                        : 1;

                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(EditEventActivity.this);
                dialogBuilder.setSingleChoiceItems(availabilityItems, checkedItem, new DialogInterface.OnClickListener()
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

                        putValue(CalendarContract.Events.AVAILABILITY, availability);
                        setAvailabilityText();
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
                Intent intent = new Intent(EditEventActivity.this, TimeZoneActivity.class);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(startDateMillis);
                calendar.set(Calendar.HOUR_OF_DAY, startTimeHour);
                calendar.set(Calendar.MINUTE, 0);
                intent.putExtra("startDate", calendar.getTime());
                startActivityForResult(intent, REQUEST_TIMEZONE);
            }
        });

        activityBinding.calendarLayout.scheduleValueLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(EditEventActivity.this);
                dialogBuilder
                        .setTitle(getString(R.string.calendar))
                        .setAdapter(new CalendarListAdapter(getApplicationContext(), calendarList)
                                , new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int position)
                                    {
                                        ContentValues calendar = (ContentValues) calendarDialog.getListView().getAdapter().getItem(position);
                                        setCalendarValue(calendar);
                                        setCalendarText();
                                    }
                                });
                calendarDialog = dialogBuilder.create();
                calendarDialog.show();
            }

        });

        activityBinding.reminderLayout.reminder.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(EditEventActivity.this, ReminderActivity.class);
                intent.putExtra("hasAlarm", (Integer) getValue(CalendarContract.Events.HAS_ALARM));

                if ((Integer) getValue(CalendarContract.Events.HAS_ALARM) == 1)
                {
                    intent.putExtra("minutes", (Integer) getValue(CalendarContract.Reminders.MINUTES));
                }
                startActivityForResult(intent, REQUEST_REMINDER);
            }
        });

        requestCode = getIntent().getIntExtra("requestCode", 0);

        switch (requestCode)
        {
            case NEW_EVENT:
                newEventValues = new ContentValues();
                eventDefaultValue = new EventDefaultValue(getApplicationContext());

                // 시간, 시간대 표시값 설정
                Date[] defaultDateTimes = eventDefaultValue.getDefaultDateTime();

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(defaultDateTimes[0].getTime());
                startDateMillis = calendar.getTimeInMillis();
                startTimeHour = calendar.get(Calendar.HOUR_OF_DAY);
                startTimeMinute = 0;

                calendar.setTimeInMillis(defaultDateTimes[1].getTime());
                endDateMillis = calendar.getTimeInMillis();
                endTimeHour = calendar.get(Calendar.HOUR_OF_DAY);
                endTimeMinute = 0;

                setDateText(START_DATETIME);
                setDateText(END_DATETIME);
                setTimeText(START_DATETIME);
                setTimeText(END_DATETIME);

                TimeZone defaultTimeZone = eventDefaultValue.getDefaultTimeZone();

                putValue(CalendarContract.Events.EVENT_TIMEZONE, defaultTimeZone.getID());
                setTimeZoneText(defaultTimeZone);

                //캘린더도 기본 값 설정
                setCalendarValue(eventDefaultValue.getDefaultCalendar());
                setCalendarText();

                // 알림
                putValue(CalendarContract.Events.HAS_ALARM, 0);

                // 접근 범위
                putValue(CalendarContract.Events.ACCESS_LEVEL, eventDefaultValue.getDefaultAccessLevel());
                setAccessLevelText();

                // 유효성
                putValue(CalendarContract.Events.AVAILABILITY, eventDefaultValue.getDefaultAvailability());
                setAvailabilityText();
                break;

            case MODIFY_EVENT:
                Intent intent = getIntent();

                int calendarId = intent.getIntExtra("calendarId", 0);
                long eventId = intent.getLongExtra("eventId", 0);

                // 이벤트, 알림을 가져온다
                viewModel.getEvent(calendarId, eventId);
                viewModel.getReminders(calendarId, eventId);

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
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(savedEventValues.getAsLong(CalendarContract.Events.DTSTART));
                            startDateMillis = calendar.getTimeInMillis();
                            startTimeHour = calendar.get(Calendar.HOUR_OF_DAY);
                            startTimeMinute = 0;

                            calendar.setTimeInMillis(savedEventValues.getAsLong(CalendarContract.Events.DTEND));
                            endDateMillis = calendar.getTimeInMillis();
                            endTimeHour = calendar.get(Calendar.HOUR_OF_DAY);
                            endTimeMinute = 0;

                            activityBinding.timeLayout.timeAlldaySwitch.setChecked(savedEventValues.getAsBoolean(CalendarContract.Events.ALL_DAY));

                            setDateText(START_DATETIME);
                            setDateText(END_DATETIME);
                            setTimeText(START_DATETIME);
                            setTimeText(END_DATETIME);
                            // 시간대

                            // 반복
                            // 알림
                            setReminderText();
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

                viewModel.getReminderListLiveData().observe(this, new Observer<DataWrapper<List<ContentValues>>>()
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

                viewModel.getCalendarListLiveData().observe(this, new Observer<DataWrapper<List<ContentValues>>>()
                {
                    @Override
                    public void onChanged(DataWrapper<List<ContentValues>> listDataWrapper)
                    {
                        if (listDataWrapper.getData() != null)
                        {
                            calendarList = listDataWrapper.getData();
                        }
                    }
                });

                viewModel.getCalendars();
        }

    }

    private int getRecurrenceRuleItemIndex(String rrule)
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
    }

    private void setAllDaySwitch()
    {
        activityBinding.timeLayout.timeAlldaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if (isChecked)
                {
                    activityBinding.timeLayout.startTime.setVisibility(View.GONE);
                    activityBinding.timeLayout.endTime.setVisibility(View.GONE);
                    activityBinding.timeLayout.timezoneLayout.setVisibility(View.GONE);
                } else
                {
                    activityBinding.timeLayout.startTime.setVisibility(View.VISIBLE);
                    activityBinding.timeLayout.endTime.setVisibility(View.VISIBLE);
                    activityBinding.timeLayout.timezoneLayout.setVisibility(View.VISIBLE);
                }
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
                switch (view.getId())
                {
                    case R.id.start_date:
                    case R.id.end_date:
                        showDatePicker();
                        break;
                    case R.id.start_time:
                        showTimePicker(START_DATETIME);
                        break;
                    case R.id.end_time:
                        showTimePicker(END_DATETIME);
                        break;
                }
            }
        };

        activityBinding.timeLayout.startDate.setOnClickListener(onClickListener);
        activityBinding.timeLayout.startTime.setOnClickListener(onClickListener);
        activityBinding.timeLayout.endDate.setOnClickListener(onClickListener);
        activityBinding.timeLayout.endTime.setOnClickListener(onClickListener);
    }


    private void setLocationView()
    {
        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //위치를 설정하는 액티비티 표시
                Intent intent = new Intent(EditEventActivity.this, SelectLocationActivity.class);

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

    private void setRecurrenceText()
    {
        if (containsKey(CalendarContract.Events.RRULE))
        {
            RecurrenceRule recurrenceRule = new RecurrenceRule();
            recurrenceRule.separateValues((String) getValue(CalendarContract.Events.RRULE));
            activityBinding.recurrenceLayout.recurrenceValue.setText(recurrenceRule.interpret(getApplicationContext()));
        } else
        {
            activityBinding.recurrenceLayout.recurrenceValue.setText("");
        }
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
                putValue(CalendarContract.Events.RRULE, rrule);
                setRecurrenceText();
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
            putValue(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
            setTimeZoneText(timeZone);
        } else if (requestCode == REQUEST_REMINDER)
        {
            if (resultCode == RESULT_OK)
            {
                if (data.getBooleanExtra("hasAlarm", false))
                {
                    final int minutes = data.getIntExtra("newMinutes", 0);
                    putValue(CalendarContract.Reminders.MINUTES, minutes);
                    putValue(CalendarContract.Events.HAS_ALARM, 1);
                } else
                {
                    putValue(CalendarContract.Events.HAS_ALARM, 0);
                    removeValue(CalendarContract.Reminders.MINUTES);
                }
                setReminderText();
            } else
            {

            }
        }
    }

    private void setReminderText()
    {
        if ((Integer) getValue(CalendarContract.Events.HAS_ALARM) == 1)
        {
            final int minutes = (Integer) getValue(CalendarContract.Reminders.MINUTES);
            ReminderDto reminderDto = CalendarEventUtil.convertAlarmMinutes(minutes);

            StringBuilder stringBuilder = new StringBuilder();
            if (reminderDto.getWeek() > 0)
            {
                stringBuilder.append(reminderDto.getWeek()).append(getString(R.string.week)).append(" ");
            }
            if (reminderDto.getDay() > 0)
            {
                stringBuilder.append(reminderDto.getDay()).append(getString(R.string.day)).append(" ");
            }
            if (reminderDto.getHour() > 0)
            {
                stringBuilder.append(reminderDto.getHour()).append(getString(R.string.hour)).append(" ");
            }
            if (reminderDto.getMinute() > 0)
            {
                stringBuilder.append(reminderDto.getMinute()).append(getString(R.string.minute)).append(" ");
            }

            if (reminderDto.getMinute() == 0)
            {
                stringBuilder.append(getString(R.string.notification_on_time));
            } else
            {
                stringBuilder.append(getString(R.string.remind_before));
            }
            activityBinding.reminderLayout.reminder.setText(stringBuilder.toString());
        } else
        {
            activityBinding.reminderLayout.reminder.setText("");
        }
    }

    private void setDateText(int dateType)
    {
        if (dateType == START_DATETIME)
        {
            activityBinding.timeLayout.startDate.setText(ClockUtil.YYYY_년_M_월_D_일_E.format(new Date(startDateMillis)));
        } else
        {
            activityBinding.timeLayout.endDate.setText(ClockUtil.YYYY_년_M_월_D_일_E.format(new Date(endDateMillis)));
        }
    }

    private void setTimeText(int dateType)
    {
        // 설정에 12시간, 24시간 단위 변경 가능
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, dateType == START_DATETIME ? startTimeHour : endTimeHour);
        calendar.set(Calendar.MINUTE, dateType == START_DATETIME ? startTimeMinute : endTimeMinute);

        if (dateType == START_DATETIME)
        {
            activityBinding.timeLayout.startTime.setText(is24HourSystem
                    ? ClockUtil.HOURS_24.format(calendar.getTime())
                    : ClockUtil.HOURS_12.format(calendar.getTime()));
        } else
        {
            activityBinding.timeLayout.endTime.setText(is24HourSystem
                    ? ClockUtil.HOURS_24.format(calendar.getTime())
                    : ClockUtil.HOURS_12.format(calendar.getTime()));
        }
    }

    private void putValue(String dataType, String value)
    {
        if (newEventValues != null)
        {
            newEventValues.put(dataType, value);
        } else
        {
            modifiedValues.put(dataType, value);
        }
    }

    private void putValue(String dataType, long value)
    {
        if (newEventValues != null)
        {
            newEventValues.put(dataType, value);
        } else
        {
            modifiedValues.put(dataType, value);
        }
    }

    private void putValue(String dataType, int value)
    {
        if (newEventValues != null)
        {
            newEventValues.put(dataType, value);
        } else
        {
            modifiedValues.put(dataType, value);
        }
    }

    private void removeValue(String dataType)
    {
        if (newEventValues != null)
        {
            newEventValues.remove(dataType);
        } else
        {
            modifiedValues.remove(dataType);
        }
    }

    private Object getValue(String dataType)
    {
        if (newEventValues != null)
        {
            return newEventValues.get(dataType);
        } else
        {
            return modifiedValues.get(dataType);
        }
    }

    private boolean containsKey(String dataType)
    {
        if (newEventValues != null)
        {
            if (newEventValues.containsKey(dataType))
            {
                return true;
            } else
            {
                return false;
            }
        } else
        {
            if (modifiedValues.containsKey(dataType))
            {
                return true;
            } else
            {
                return false;
            }
        }
    }

    private void setCalendarValue(ContentValues calendar)
    {
        putValue(CalendarContract.Events.CALENDAR_ID, calendar.getAsInteger(CalendarContract.Calendars._ID));
        putValue(CalendarContract.Events.CALENDAR_DISPLAY_NAME, calendar.getAsString(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME));
        putValue(CalendarContract.Events.ACCOUNT_NAME, calendar.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));
        putValue(CalendarContract.Events.CALENDAR_COLOR, calendar.getAsInteger(CalendarContract.Calendars.CALENDAR_COLOR));
    }

    private void setCalendarText()
    {
        float[] hsv = new float[3];
        Color.colorToHSV((Integer) getValue(CalendarContract.Events.CALENDAR_COLOR), hsv);

        activityBinding.calendarLayout.calendarColor.setBackgroundColor(Color.HSVToColor(hsv));
        activityBinding.calendarLayout.calendarDisplayName.setText((String) getValue(CalendarContract.Events.CALENDAR_DISPLAY_NAME));
        activityBinding.calendarLayout.calendarAccountName.setText((String) getValue(CalendarContract.Events.ACCOUNT_NAME));
    }

    private void setTimeZoneText(TimeZone timeZone)
    {
        activityBinding.timeLayout.timezone.setText(timeZone.getDisplayName(Locale.KOREAN));
    }

    private void saveEvent()
    {
        // 시간이 바뀌는 경우, 반복과 알림 데이터도 변경해야함.
        if (requestCode == NEW_EVENT)
        {
            // 제목, 설명, 위치값 뷰에서 읽어오기, 데이터가 없는 경우 추가하지 않음(공백X)
            if (!activityBinding.titleLayout.title.getText().toString().isEmpty())
            {
                newEventValues.put(CalendarContract.Events.TITLE, activityBinding.titleLayout.title.getText().toString());
            }
            if (!activityBinding.descriptionLayout.description.getText().toString().isEmpty())
            {
                newEventValues.put(CalendarContract.Events.DESCRIPTION, activityBinding.descriptionLayout.description.getText().toString());
            }
            if (!activityBinding.locationLayout.location.getText().toString().isEmpty())
            {
                newEventValues.put(CalendarContract.Events.EVENT_LOCATION, activityBinding.locationLayout.location.getText().toString());
            }

            viewModel.addEvent(newEventValues);
        } else if (requestCode == MODIFY_EVENT)
        {
            //제목
            if (savedEventValues.containsKey(CalendarContract.Events.TITLE))
            {
                if (activityBinding.titleLayout.title.getText().toString().isEmpty())
                {
                    // 삭제
                    modifiedValues.put(CalendarContract.Events.TITLE, "");
                } else if (!savedEventValues.getAsString(CalendarContract.Events.TITLE).equals(activityBinding.titleLayout.title.getText().toString()))
                {
                    // 수정
                    modifiedValues.put(CalendarContract.Events.TITLE, activityBinding.titleLayout.title.getText().toString());
                }

            } else
            {
                if (!activityBinding.titleLayout.title.getText().toString().isEmpty())
                {
                    //추가
                    modifiedValues.put(CalendarContract.Events.TITLE, activityBinding.titleLayout.title.getText().toString());
                }

            }

            //설명
            if (savedEventValues.containsKey(CalendarContract.Events.DESCRIPTION))
            {
                if (activityBinding.descriptionLayout.description.getText().toString().isEmpty())
                {
                    // 삭제
                    modifiedValues.put(CalendarContract.Events.DESCRIPTION, "");
                } else if (!savedEventValues.getAsString(CalendarContract.Events.DESCRIPTION).equals(activityBinding.descriptionLayout.description.getText().toString()))
                {
                    // 수정
                    modifiedValues.put(CalendarContract.Events.DESCRIPTION, activityBinding.descriptionLayout.description.getText().toString());
                }

            } else
            {
                if (!activityBinding.descriptionLayout.description.getText().toString().isEmpty())
                {
                    modifiedValues.put(CalendarContract.Events.DESCRIPTION, activityBinding.descriptionLayout.description.getText().toString());
                }

            }

            //위치
            if (savedEventValues.containsKey(CalendarContract.Events.EVENT_LOCATION))
            {
                if (activityBinding.locationLayout.location.getText().toString().isEmpty())
                {
                    // 삭제
                    modifiedValues.put(CalendarContract.Events.EVENT_LOCATION, "");
                } else if (!savedEventValues.getAsString(CalendarContract.Events.EVENT_LOCATION).equals(activityBinding.locationLayout.location.getText().toString()))
                {
                    // 수정
                    modifiedValues.put(CalendarContract.Events.EVENT_LOCATION, activityBinding.locationLayout.location.getText().toString());
                }

            } else
            {
                if (!activityBinding.locationLayout.location.getText().toString().isEmpty())
                {
                    modifiedValues.put(CalendarContract.Events.EVENT_LOCATION, activityBinding.locationLayout.location.getText().toString());
                }

            }

            viewModel.updateEvent(modifiedValues);
        }
    }

    private void showDatePicker()
    {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();

        datePicker = builder.setTitleText(R.string.datepicker)
                .setSelection(new Pair<>(startDateMillis, endDateMillis))
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .build();
        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>()
        {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection)
            {
                if (selection.first != null)
                {
                    startDateMillis = selection.first;
                    setDateText(START_DATETIME);
                }
                if (selection.second != null)
                {
                    endDateMillis = selection.second;
                    setDateText(END_DATETIME);
                }

                datePicker.dismiss();
            }
        });
        datePicker.addOnNegativeButtonClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                datePicker.dismiss();
            }
        });
        datePicker.show(getSupportFragmentManager(), datePicker.toString());
    }

    private void showTimePicker(int dateType)
    {
        MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder();
        timePicker = builder.setTitleText((dateType == START_DATETIME ? getString(R.string.start) : getString(R.string.end)) + getString(R.string.timepicker))
                .setTimeFormat(is24HourSystem ? TimeFormat.CLOCK_24H : TimeFormat.CLOCK_12H)
                .setHour(dateType == START_DATETIME ? startTimeHour : endTimeHour)
                .setMinute(dateType == END_DATETIME ? startTimeMinute : endTimeMinute)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK).build();
        timePicker.addOnPositiveButtonClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (dateType == START_DATETIME)
                {
                    startTimeHour = timePicker.getHour();
                    startTimeMinute = timePicker.getMinute();
                    setTimeText(START_DATETIME);
                } else
                {
                    endTimeHour = timePicker.getHour();
                    endTimeMinute = timePicker.getMinute();
                    setTimeText(END_DATETIME);
                }
                timePicker.dismiss();
            }
        });
        timePicker.addOnNegativeButtonClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                timePicker.dismiss();
            }
        });
        timePicker.show(getSupportFragmentManager(), timePicker.toString());
    }


    private void setAccessLevelText()
    {
        switch ((Integer) getValue(CalendarContract.Events.ACCESS_LEVEL))
        {
            case CalendarContract.Events.ACCESS_DEFAULT:
                activityBinding.accesslevelLayout.accesslevel.setText(accessLevelItems[0]);
                break;
            case CalendarContract.Events.ACCESS_PUBLIC:
                activityBinding.accesslevelLayout.accesslevel.setText(accessLevelItems[1]);
                break;
            case CalendarContract.Events.ACCESS_PRIVATE:
                activityBinding.accesslevelLayout.accesslevel.setText(accessLevelItems[2]);
                break;
        }
    }

    private void setAvailabilityText()
    {
        switch ((Integer) getValue(CalendarContract.Events.AVAILABILITY))
        {
            case CalendarContract.Events.AVAILABILITY_BUSY:
                activityBinding.availabilityLayout.availability.setText(availabilityItems[0]);
                break;
            case CalendarContract.Events.AVAILABILITY_FREE:
                activityBinding.availabilityLayout.availability.setText(availabilityItems[1]);
                break;
        }
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

