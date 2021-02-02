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
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.activity.editevent.adapter.CalendarListAdapter;
import com.zerodsoft.scheduleweather.activity.editevent.value.EventDefaultValue;
import com.zerodsoft.scheduleweather.activity.editevent.interfaces.IEventRepeat;
import com.zerodsoft.scheduleweather.activity.map.SelectLocationActivity;
import com.zerodsoft.scheduleweather.databinding.ActivityEditEventBinding;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
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

    private ActivityEditEventBinding binding;
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_event);
        init();
    }

    private void init()
    {
        Toolbar toolbar = binding.eventToolbar;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        EditTextWatcher editTextWatcher = new EditTextWatcher();
        binding.titleLayout.title.addTextChangedListener(editTextWatcher);
        binding.titleLayout.title.setOnFocusChangeListener(editTextWatcher);
        binding.descriptionLayout.descriptionEdittext.addTextChangedListener(editTextWatcher);
        binding.descriptionLayout.descriptionEdittext.setOnFocusChangeListener(editTextWatcher);

        binding.timeLayout.startDate.setClickable(true);
        binding.timeLayout.startTime.setClickable(true);
        binding.timeLayout.endDate.setClickable(true);
        binding.timeLayout.endTime.setClickable(true);
        binding.timeLayout.eventTimezone.setClickable(true);

        binding.recurrenceLayout.eventRecurrence.setClickable(true);

        binding.reminderLayout.notReminder.setVisibility(View.GONE);

        binding.descriptionLayout.descriptionTextview.setVisibility(View.GONE);

        binding.locationLayout.eventLocation.setClickable(true);

        binding.attendeeLayout.notAttendees.setVisibility(View.GONE);

        binding.accesslevelLayout.eventAccessLevel.setClickable(true);

        binding.availabilityLayout.eventAvailability.setClickable(true);

        setOnClickListener();

        viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        viewModel.init(getApplicationContext());

        requestCode = getIntent().getIntExtra("requestCode", 0);

        switch (requestCode)
        {
            case NEW_EVENT:
                actionBar.setTitle(R.string.new_event);

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
                actionBar.setTitle(R.string.modity_event);
                Intent intent = getIntent();

                final int CALENDAR_ID = intent.getIntExtra("calendarId", 0);
                final long EVENT_ID = intent.getLongExtra("eventId", 0);

                // 이벤트, 알림을 가져온다
                viewModel.getEvent(CALENDAR_ID, EVENT_ID);
                viewModel.getReminders(CALENDAR_ID, EVENT_ID);

                viewModel.getEventLiveData().observe(this, eventDtoDataWrapper ->
                {
                    if (eventDtoDataWrapper.getData() != null)
                    {
                        savedEventValues = eventDtoDataWrapper.getData();
                        modifiedValues = new ContentValues();

                        // 제목, 캘린더, 시간, 시간대, 반복, 알림, 설명, 위치, 공개범위, 유효성, 참석자
                        // 캘린더, 시간대, 참석자 정보는 따로 불러온다.

                        //제목
                        binding.titleLayout.title.setText(savedEventValues.getAsString(CalendarContract.Events.TITLE));
                        //캘린더

                        //시간
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.setTimeInMillis(savedEventValues.getAsLong(CalendarContract.Events.DTSTART));
                        startDateMillis = calendar1.getTimeInMillis();
                        startTimeHour = calendar1.get(Calendar.HOUR_OF_DAY);
                        startTimeMinute = 0;

                        calendar1.setTimeInMillis(savedEventValues.getAsLong(CalendarContract.Events.DTEND));
                        endDateMillis = calendar1.getTimeInMillis();
                        endTimeHour = calendar1.get(Calendar.HOUR_OF_DAY);
                        endTimeMinute = 0;

                        binding.timeLayout.timeAlldaySwitch.setChecked(savedEventValues.getAsBoolean(CalendarContract.Events.ALL_DAY));

                        setDateText(START_DATETIME);
                        setDateText(END_DATETIME);
                        setTimeText(START_DATETIME);
                        setTimeText(END_DATETIME);
                        // 시간대

                        // 반복
                        if ((String) getValue(CalendarContract.Events.RRULE) != null)
                        {
                            setRecurrenceText();
                        }

                        // 알림
                        if ((Boolean) getValue(CalendarContract.Events.HAS_ALARM))
                        {
                            viewModel.getReminders(CALENDAR_ID, EVENT_ID);
                        }

                        // 설명
                        binding.descriptionLayout.descriptionEdittext.setText(savedEventValues.getAsString(CalendarContract.Events.DESCRIPTION));
                        // 위치
                        binding.locationLayout.eventLocation.setText(savedEventValues.getAsString(CalendarContract.Events.EVENT_LOCATION));

                        // 공개 범위 표시
                        binding.accesslevelLayout.eventAccessLevel.callOnClick();
                        // 유효성 표시
                        binding.availabilityLayout.eventAvailability.callOnClick();

                        modifiedValues.put(CalendarContract.Events.ACCESS_LEVEL, savedEventValues.getAsInteger(CalendarContract.Events.ACCESS_LEVEL));
                        modifiedValues.put(CalendarContract.Events.AVAILABILITY, savedEventValues.getAsInteger(CalendarContract.Events.AVAILABILITY));
                    }
                });

                viewModel.getAttendeeListLiveData().observe(this, new Observer<DataWrapper<List<ContentValues>>>()
                {
                    @Override
                    public void onChanged(DataWrapper<List<ContentValues>> listDataWrapper)
                    {
                        if (listDataWrapper.getData() != null)
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    if (!listDataWrapper.getData().isEmpty())
                                    {
                                        setAttendeesText(listDataWrapper.getData());
                                    }
                                }
                            });

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
                            // 알림
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    if (!listDataWrapper.getData().isEmpty())
                                    {
                                        setReminderText(listDataWrapper.getData());
                                    }
                                }
                            });

                        }
                    }
                });

                viewModel.getCalendarListLiveData().observe(this, listDataWrapper ->
                {
                    if (listDataWrapper.getData() != null)
                    {
                        calendarList = listDataWrapper.getData();
                    }
                });

                viewModel.getCalendars();
        }

    }

    private void setOnClickListener()
    {
        binding.timeLayout.timeAlldaySwitch.setOnCheckedChangeListener((compoundButton, isChecked) ->
        {
            if (isChecked)
            {
                binding.timeLayout.startTime.setVisibility(View.GONE);
                binding.timeLayout.endTime.setVisibility(View.GONE);
                binding.timeLayout.eventTimezoneLayout.setVisibility(View.GONE);
            } else
            {
                binding.timeLayout.startTime.setVisibility(View.VISIBLE);
                binding.timeLayout.endTime.setVisibility(View.VISIBLE);
                binding.timeLayout.eventTimezoneLayout.setVisibility(View.VISIBLE);
            }
        });

        binding.recurrenceLayout.eventRecurrence.setOnClickListener(view ->
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
        });

        binding.accesslevelLayout.eventAccessLevel.setOnClickListener(view ->
        {
            int checkedItem = getValue(CalendarContract.Events.ACCESS_LEVEL) != null ? (Integer) getValue(CalendarContract.Events.ACCESS_LEVEL)
                    : 0;

            if (checkedItem == 3)
            {
                checkedItem = 1;
            }

            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(EditEventActivity.this);
            dialogBuilder.setSingleChoiceItems(EventUtil.getAccessLevelItems(getApplicationContext()), checkedItem, (dialogInterface, item) ->
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

            }).setTitle(getString(R.string.accesslevel));
            accessLevelDialog = dialogBuilder.create();
            accessLevelDialog.show();
        });

        binding.availabilityLayout.eventAvailability.setOnClickListener(view ->
        {
            int checkedItem = getValue(CalendarContract.Events.AVAILABILITY) != null ? (Integer) getValue(CalendarContract.Events.AVAILABILITY)
                    : 1;

            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(EditEventActivity.this);
            dialogBuilder.setSingleChoiceItems(EventUtil.getAvailabilityItems(getApplicationContext()), checkedItem, (dialogInterface, item) ->
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
            }).setTitle(getString(R.string.availability));
            availabilityDialog = dialogBuilder.create();
            availabilityDialog.show();
        });

        binding.timeLayout.eventTimezone.setOnClickListener(view ->
        {
            Intent intent = new Intent(EditEventActivity.this, TimeZoneActivity.class);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(startDateMillis);
            calendar.set(Calendar.HOUR_OF_DAY, startTimeHour);
            calendar.set(Calendar.MINUTE, 0);
            intent.putExtra("startDate", calendar.getTime());
            startActivityForResult(intent, REQUEST_TIMEZONE);
        });

        binding.calendarLayout.eventCalendarValueView.setOnClickListener(view ->
        {
            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(EditEventActivity.this);
            dialogBuilder
                    .setTitle(getString(R.string.calendar))
                    .setAdapter(new CalendarListAdapter(getApplicationContext(), calendarList)
                            , (dialogInterface, position) ->
                            {
                                ContentValues calendar = (ContentValues) calendarDialog.getListView().getAdapter().getItem(position);
                                setCalendarValue(calendar);
                                setCalendarText();
                            });
            calendarDialog = dialogBuilder.create();
            calendarDialog.show();
        });

        binding.reminderLayout.addReminderButton.setOnClickListener(view ->
        {
            Intent intent = new Intent(EditEventActivity.this, ReminderActivity.class);
            intent.putExtra(CalendarContract.Events.HAS_ALARM, (Integer) getValue(CalendarContract.Events.HAS_ALARM));

            if ((Integer) getValue(CalendarContract.Events.HAS_ALARM) == 1)
            {
                intent.putExtra(CalendarContract.Reminders.MINUTES, (Integer) getValue(CalendarContract.Reminders.MINUTES));
            }
            startActivityForResult(intent, REQUEST_REMINDER);
        });

        View.OnClickListener dateTimeOnClickListener = view ->
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
        };

        binding.timeLayout.startDate.setOnClickListener(dateTimeOnClickListener);
        binding.timeLayout.startTime.setOnClickListener(dateTimeOnClickListener);
        binding.timeLayout.endDate.setOnClickListener(dateTimeOnClickListener);
        binding.timeLayout.endTime.setOnClickListener(dateTimeOnClickListener);

        binding.locationLayout.eventLocation.setOnClickListener(view ->
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
        });
    }


    private void setRecurrenceText()
    {
        if (containsKey(CalendarContract.Events.RRULE))
        {
            RecurrenceRule recurrenceRule = new RecurrenceRule();
            recurrenceRule.separateValues((String) getValue(CalendarContract.Events.RRULE));
            binding.recurrenceLayout.eventRecurrence.setText(recurrenceRule.interpret(getApplicationContext()));
        } else
        {
            binding.recurrenceLayout.eventRecurrence.setText("");
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
                if (data.getBooleanExtra(CalendarContract.Events.HAS_ALARM, false))
                {
                    final int minutes = data.getIntExtra("newMinutes", 0);
                    putValue(CalendarContract.Reminders.MINUTES, minutes);
                    putValue(CalendarContract.Events.HAS_ALARM, 1);
                } else
                {
                    putValue(CalendarContract.Events.HAS_ALARM, 0);
                    removeValue(CalendarContract.Reminders.MINUTES);
                }

            } else
            {

            }
        }
    }


    private void setReminderText(List<ContentValues> reminders)
    {
        LayoutInflater layoutInflater = getLayoutInflater();

        for (ContentValues reminder : reminders)
        {
            ReminderDto reminderDto = EventUtil.convertAlarmMinutes(reminder.getAsInteger(CalendarContract.Reminders.MINUTES));
            String alarmValueText = EventUtil.makeAlarmText(reminderDto, getApplicationContext());

            View row = layoutInflater.inflate(R.layout.event_reminder_item, null);
            ((TextView) row.findViewById(R.id.reminder_value)).setText(alarmValueText);

            binding.reminderLayout.remindersTable.addView(row, binding.reminderLayout.remindersTable.getChildCount() - 1);
        }
    }

    private void setAttendeesText(List<ContentValues> attendees)
    {
        // 참석자 수, 참석 여부
        LayoutInflater layoutInflater = getLayoutInflater();

        for (ContentValues attendee : attendees)
        {
            // 이름, 메일 주소, 상태
            // 조직자 - attendeeName, 그 외 - email

            // calendar display name
            final String attendeeName = attendee.getAsString(CalendarContract.Attendees.ATTENDEE_NAME);
            // calendar owner account
            final String attendeeEmail = attendee.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL);
            final int attendeeStatus = attendee.getAsInteger(CalendarContract.Attendees.ATTENDEE_STATUS);
            final int attendeeRelationship = attendee.getAsInteger(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP);

            String attendeeStatusStr = EventUtil.convertAttendeeStatus(attendeeStatus, getApplicationContext());
            String attendeeRelationshipStr = EventUtil.convertAttendeeRelationship(attendeeRelationship, getApplicationContext());

            LinearLayout row = (LinearLayout) layoutInflater.inflate(R.layout.event_attendee_item, null);
            // add row to table

            LinearLayout attendeeInfoLayout = row.findViewById(R.id.attendee_info_layout);
            TextView attendeeEmailView = (TextView) row.findViewById(R.id.attendee_name);
            TextView attendeeRelationshipView = (TextView) row.findViewById(R.id.attendee_relationship);
            TextView attendeeStatusView = (TextView) row.findViewById(R.id.attendee_status);
            ImageButton removeButton = (ImageButton) row.findViewById(R.id.remove_attendee_button);

            attendeeInfoLayout.setClickable(true);
            attendeeInfoLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    // logic for communications with attendee

                }
            });

            removeButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                }
            });

            attendeeEmailView.setText(attendeeRelationship == CalendarContract.Attendees.RELATIONSHIP_ORGANIZER ? attendeeName : attendeeEmail);
            attendeeRelationshipView.setText(attendeeRelationshipStr);
            attendeeStatusView.setText(attendeeStatusStr);

            binding.attendeeLayout.eventAttendeesTable.addView(row, binding.attendeeLayout.eventAttendeesTable.getChildCount() - 1);
        }
    }

    private void setDateText(int dateType)
    {
        if (dateType == START_DATETIME)
        {
            binding.timeLayout.startDate.setText(EventUtil.convertDate(startDateMillis));
        } else
        {
            binding.timeLayout.endDate.setText(EventUtil.convertDate(endDateMillis));
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
            binding.timeLayout.startTime.setText(EventUtil.convertTime(calendar.getTimeInMillis(), App.is24HourSystem));
        } else
        {
            binding.timeLayout.endTime.setText(EventUtil.convertTime(calendar.getTimeInMillis(), App.is24HourSystem));
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
        binding.calendarLayout.calendarColor.setBackgroundColor(EventUtil.getColor((Integer) getValue(CalendarContract.Events.CALENDAR_COLOR)));
        binding.calendarLayout.calendarDisplayName.setText((String) getValue(CalendarContract.Events.CALENDAR_DISPLAY_NAME));
        binding.calendarLayout.calendarAccountName.setText((String) getValue(CalendarContract.Events.ACCOUNT_NAME));
    }

    private void setTimeZoneText(TimeZone timeZone)
    {
        binding.timeLayout.eventTimezone.setText(timeZone.getDisplayName(Locale.KOREAN));
    }

    private void saveEvent()
    {
        // 시간이 바뀌는 경우, 반복과 알림 데이터도 변경해야함.
        if (requestCode == NEW_EVENT)
        {
            // 제목, 설명, 위치값 뷰에서 읽어오기, 데이터가 없는 경우 추가하지 않음(공백X)
            if (!binding.titleLayout.title.getText().toString().isEmpty())
            {
                newEventValues.put(CalendarContract.Events.TITLE, binding.titleLayout.title.getText().toString());
            }
            if (!binding.descriptionLayout.descriptionEdittext.getText().toString().isEmpty())
            {
                newEventValues.put(CalendarContract.Events.DESCRIPTION, binding.descriptionLayout.descriptionEdittext.getText().toString());
            }
            if (!binding.locationLayout.eventLocation.getText().toString().isEmpty())
            {
                newEventValues.put(CalendarContract.Events.EVENT_LOCATION, binding.locationLayout.eventLocation.getText().toString());
            }

            viewModel.addEvent(newEventValues);
        } else if (requestCode == MODIFY_EVENT)
        {
            //제목
            if (savedEventValues.containsKey(CalendarContract.Events.TITLE))
            {
                if (binding.titleLayout.title.getText().toString().isEmpty())
                {
                    // 삭제
                    modifiedValues.put(CalendarContract.Events.TITLE, "");
                } else if (!savedEventValues.getAsString(CalendarContract.Events.TITLE).equals(binding.titleLayout.title.getText().toString()))
                {
                    // 수정
                    modifiedValues.put(CalendarContract.Events.TITLE, binding.titleLayout.title.getText().toString());
                }

            } else
            {
                if (!binding.titleLayout.title.getText().toString().isEmpty())
                {
                    //추가
                    modifiedValues.put(CalendarContract.Events.TITLE, binding.titleLayout.title.getText().toString());
                }

            }

            //설명
            if (savedEventValues.containsKey(CalendarContract.Events.DESCRIPTION))
            {
                if (binding.descriptionLayout.descriptionEdittext.getText().toString().isEmpty())
                {
                    // 삭제
                    modifiedValues.put(CalendarContract.Events.DESCRIPTION, "");
                } else if (!savedEventValues.getAsString(CalendarContract.Events.DESCRIPTION).equals(binding.descriptionLayout.descriptionEdittext.getText().toString()))
                {
                    // 수정
                    modifiedValues.put(CalendarContract.Events.DESCRIPTION, binding.descriptionLayout.descriptionEdittext.getText().toString());
                }

            } else
            {
                if (!binding.descriptionLayout.descriptionEdittext.getText().toString().isEmpty())
                {
                    modifiedValues.put(CalendarContract.Events.DESCRIPTION, binding.descriptionLayout.descriptionEdittext.getText().toString());
                }

            }

            //위치
            if (savedEventValues.containsKey(CalendarContract.Events.EVENT_LOCATION))
            {
                if (binding.locationLayout.eventLocation.getText().toString().isEmpty())
                {
                    // 삭제
                    modifiedValues.put(CalendarContract.Events.EVENT_LOCATION, "");
                } else if (!savedEventValues.getAsString(CalendarContract.Events.EVENT_LOCATION).equals(binding.locationLayout.eventLocation.getText().toString()))
                {
                    // 수정
                    modifiedValues.put(CalendarContract.Events.EVENT_LOCATION, binding.locationLayout.eventLocation.getText().toString());
                }

            } else
            {
                if (!binding.locationLayout.eventLocation.getText().toString().isEmpty())
                {
                    modifiedValues.put(CalendarContract.Events.EVENT_LOCATION, binding.locationLayout.eventLocation.getText().toString());
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
        datePicker.addOnNegativeButtonClickListener(view ->
        {
            datePicker.dismiss();
        });

        datePicker.show(getSupportFragmentManager(), datePicker.toString());
    }

    private void showTimePicker(int dateType)
    {
        MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder();
        timePicker = builder.setTitleText((dateType == START_DATETIME ? getString(R.string.start) : getString(R.string.end)) + getString(R.string.timepicker))
                .setTimeFormat(App.is24HourSystem ? TimeFormat.CLOCK_24H : TimeFormat.CLOCK_12H)
                .setHour(dateType == START_DATETIME ? startTimeHour : endTimeHour)
                .setMinute(dateType == END_DATETIME ? startTimeMinute : endTimeMinute)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK).build();

        timePicker.addOnPositiveButtonClickListener(view ->
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

        });
        timePicker.addOnNegativeButtonClickListener(view ->
        {
            timePicker.dismiss();
        });
        timePicker.show(getSupportFragmentManager(), timePicker.toString());
    }


    private void setAccessLevelText()
    {
        binding.accesslevelLayout.eventAccessLevel.setText(EventUtil.convertAccessLevel((Integer) getValue(CalendarContract.Events.ACCESS_LEVEL), getApplicationContext()));
    }

    private void setAvailabilityText()
    {
        binding.availabilityLayout.eventAvailability.setText(EventUtil.convertAvailability((Integer) getValue(CalendarContract.Events.AVAILABILITY), getApplicationContext()));
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
                case R.id.description_edittext:
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

