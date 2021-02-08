package com.zerodsoft.scheduleweather.activity.editevent.activity;

import androidx.annotation.NonNull;
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
import android.os.Parcelable;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.service.carrier.CarrierMessagingService;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.activity.editevent.adapter.CalendarListAdapter;
import com.zerodsoft.scheduleweather.activity.editevent.value.EventDataController;
import com.zerodsoft.scheduleweather.activity.editevent.interfaces.IEventRepeat;
import com.zerodsoft.scheduleweather.activity.map.SelectLocationActivity;
import com.zerodsoft.scheduleweather.databinding.ActivityEditEventBinding;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.utility.RecurrenceRule;
import com.zerodsoft.scheduleweather.utility.model.ReminderDto;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class EditEventActivity extends AppCompatActivity implements IEventRepeat
{
    // 추가 작업 필요 : 저장, 수정
    public static final int REQUEST_RECURRENCE = 110;
    public static final int REQUEST_TIMEZONE = 120;
    public static final int REQUEST_LOCATION = 130;

    private ActivityEditEventBinding binding;
    private CalendarViewModel viewModel;
    private EventDataController dataController;
    private LocationViewModel locationViewModel;

    private AlertDialog accessLevelDialog;
    private AlertDialog availabilityDialog;
    private AlertDialog calendarDialog;

    private MaterialTimePicker timePicker;
    private MaterialDatePicker<Pair<Long, Long>> datePicker;

    private static final int START_DATETIME = 0;
    private static final int END_DATETIME = 1;

    private Integer requestCode;
    private List<ContentValues> calendarList;
    private LocationDTO locationDTO;

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
                if (requestCode == EventDataController.NEW_EVENT)
                {
                    // saveEvent();
                } else if (requestCode == EventDataController.MODIFY_EVENT)
                {
                    // modifyEvent();
                }
                setResult(RESULT_CANCELED);
                finish();
                // 새로 생성하는 이벤트이고, 위치가 지정되어 있으면 카카오맵에서 가져온 위치 정보를 DB에 등록한다.
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

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        viewModel.init(getApplicationContext());
        requestCode = getIntent().getIntExtra("requestCode", 0);

        dataController = new EventDataController(getApplicationContext(), requestCode);

        switch (requestCode)
        {
            case EventDataController.NEW_EVENT:
            {
                actionBar.setTitle(R.string.new_event);

                //캘린더도 기본 값 설정
                dataController.setCalendarValue(dataController.getEventDefaultValue().getDefaultCalendar());
                setCalendarText();

                // 기기 시각으로 설정
                Date[] defaultDateTimes = dataController.getEventDefaultValue().getDefaultDateTime();
                dataController.setStart(defaultDateTimes[0].getTime());
                dataController.setEnd(defaultDateTimes[1].getTime());

                setDateText(START_DATETIME);
                setDateText(END_DATETIME);
                setTimeText(START_DATETIME);
                setTimeText(END_DATETIME);

                // 기기 시간대로 설정
                TimeZone defaultTimeZone = dataController.getEventDefaultValue().getDefaultTimeZone();
                dataController.putEventValue(CalendarContract.Events.EVENT_TIMEZONE, defaultTimeZone.getID());
                setTimeZoneText();

                // 알림
                dataController.putEventValue(CalendarContract.Events.HAS_ALARM, 0);

                // 접근 범위(기본)
                dataController.putEventValue(CalendarContract.Events.ACCESS_LEVEL, dataController.getEventDefaultValue().getDefaultAccessLevel());
                setAccessLevelText();

                // 유효성(바쁨)
                dataController.putEventValue(CalendarContract.Events.AVAILABILITY, dataController.getEventDefaultValue().getDefaultAvailability());
                setAvailabilityText();

                // 참석자 버튼 텍스트 수정
                binding.attendeeLayout.showAttendeesDetail.setText(getString(R.string.add_attendee));
                break;
            }
            case EventDataController.MODIFY_EVENT:
            {
                actionBar.setTitle(R.string.modify_event);

                Intent intent = getIntent();
                final int CALENDAR_ID = intent.getIntExtra("calendarId", 0);
                final long EVENT_ID = intent.getLongExtra("eventId", 0);

                // 이벤트, 알림을 가져온다
                viewModel.getEvent(CALENDAR_ID, EVENT_ID);
                viewModel.getAttendees(CALENDAR_ID, EVENT_ID);

                viewModel.getEventLiveData().observe(this, eventDtoDataWrapper ->
                {
                    if (eventDtoDataWrapper.getData() != null)
                    {
                        dataController.getSavedEventData().getEVENT().putAll(eventDtoDataWrapper.getData());
                        // 제목, 캘린더, 시간, 시간대, 반복, 알림, 설명, 위치, 공개범위, 유효성, 참석자
                        // 알림, 참석자 정보는 따로 불러온다.

                        //제목
                        binding.titleLayout.title.setText(dataController.getEventValueAsString(CalendarContract.Events.TITLE));

                        //캘린더
                        setCalendarText();

                        // allday switch
                        binding.timeLayout.timeAlldaySwitch.setChecked(dataController.getEventValueAsBoolean(CalendarContract.Events.ALL_DAY));

                        //시각
                        setDateText(START_DATETIME);
                        setDateText(END_DATETIME);
                        setTimeText(START_DATETIME);
                        setTimeText(END_DATETIME);

                        // 시간대
                        setTimeZoneText();

                        // 반복
                        if (dataController.getEventValueAsString(CalendarContract.Events.RRULE) != null)
                        {
                            setRecurrenceText();
                        }

                        // 알림
                        if (dataController.getEventValueAsBoolean(CalendarContract.Events.HAS_ALARM))
                        {
                            viewModel.getReminders(CALENDAR_ID, EVENT_ID);
                        }

                        // 설명
                        binding.descriptionLayout.descriptionEdittext.setText(dataController.getEventValueAsString(CalendarContract.Events.DESCRIPTION));
                        // 위치
                        binding.locationLayout.eventLocation.setText(dataController.getEventValueAsString(CalendarContract.Events.EVENT_LOCATION));
                        // 공개 범위 표시
                        binding.accesslevelLayout.eventAccessLevel.callOnClick();
                        // 유효성 표시
                        binding.availabilityLayout.eventAvailability.callOnClick();
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
                                        dataController.getSavedEventData().getATTENDEES().addAll(listDataWrapper.getData());
                                        setAttendeesText();
                                    } else
                                    {
                                        // 참석자 버튼 텍스트 수정
                                        binding.attendeeLayout.showAttendeesDetail.setText(getString(R.string.add_attendee));
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
                                        dataController.getSavedEventData().getREMINDERS().addAll(listDataWrapper.getData());
                                        setReminderText();
                                    }
                                }
                            });

                        }
                    }
                });
                break;
            }
        }

        viewModel.getCalendarListLiveData().observe(this, listDataWrapper ->
        {
            if (listDataWrapper.getData() != null)
            {
                calendarList = listDataWrapper.getData();
            }
        });

        viewModel.getCalendars();
    }

    private void setOnClickListener()
    {
        /*
        시간 allday 스위치
         */
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

            dataController.putEventValue(CalendarContract.Events.ALL_DAY, isChecked ? 1 : 0);
        });

        /*
        시간대
         */
        binding.timeLayout.eventTimezone.setOnClickListener(view ->
        {
            Intent intent = new Intent(EditEventActivity.this, TimeZoneActivity.class);
            intent.putExtra(CalendarContract.Events.DTSTART, dataController.getStart());
            startActivityForResult(intent, REQUEST_TIMEZONE);
        });

        /*
        반복
         */
        binding.recurrenceLayout.eventRecurrence.setOnClickListener(view ->
        {
            Intent intent = new Intent(EditEventActivity.this, RecurrenceActivity.class);
            // 반복 룰과 이벤트의 시작 시간 전달
            String recurrenceRule = dataController.getEventValueAsString(CalendarContract.Events.RRULE) != null
                    ? dataController.getEventValueAsString(CalendarContract.Events.RRULE) : "";

            intent.putExtra(CalendarContract.Events.RRULE, recurrenceRule);
            intent.putExtra(CalendarContract.Events.DTSTART, dataController.getStart());
            startActivityForResult(intent, REQUEST_RECURRENCE);
        });

        /*
        접근수준
         */
        binding.accesslevelLayout.eventAccessLevel.setOnClickListener(view ->
        {
            int checkedItem = dataController.getEventValueAsInt(CalendarContract.Events.ACCESS_LEVEL) != null ? dataController.getEventValueAsInt(CalendarContract.Events.ACCESS_LEVEL)
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

                dataController.putEventValue(CalendarContract.Events.ACCESS_LEVEL, accessLevel);
                setAccessLevelText();
                accessLevelDialog.dismiss();

            }).setTitle(getString(R.string.accesslevel));
            accessLevelDialog = dialogBuilder.create();
            accessLevelDialog.show();
        });

        /*
        유효성
         */
        binding.availabilityLayout.eventAvailability.setOnClickListener(view ->
        {
            int checkedItem = dataController.getEventValueAsInt(CalendarContract.Events.AVAILABILITY) != null ?
                    dataController.getEventValueAsInt(CalendarContract.Events.AVAILABILITY)
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

                dataController.putEventValue(CalendarContract.Events.AVAILABILITY, availability);
                setAvailabilityText();
                availabilityDialog.dismiss();

            }).setTitle(getString(R.string.availability));
            availabilityDialog = dialogBuilder.create();
            availabilityDialog.show();
        });

        /*
        캘린더 선택
         */
        binding.calendarLayout.eventCalendarValueView.setOnClickListener(view ->
        {
            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(EditEventActivity.this);
            dialogBuilder
                    .setTitle(getString(R.string.calendar))
                    .setAdapter(new CalendarListAdapter(getApplicationContext(), calendarList)
                            , (dialogInterface, position) ->
                            {
                                ContentValues calendar = (ContentValues) calendarDialog.getListView().getAdapter().getItem(position);
                                dataController.setCalendarValue(calendar);
                                setCalendarText();
                            });
            calendarDialog = dialogBuilder.create();
            calendarDialog.show();
        });

        /*
        알람
         */
        binding.reminderLayout.addReminderButton.setOnClickListener(view ->
        {
            Intent intent = new Intent(EditEventActivity.this, ReminderActivity.class);
            intent.putExtra("requestCode", ReminderActivity.ADD_REMINDER);
            startActivityForResult(intent, ReminderActivity.ADD_REMINDER);
        });

        @SuppressLint("NonConstantResourceId") View.OnClickListener dateTimeOnClickListener = view ->
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

        /*
        위치
         */
        binding.locationLayout.eventLocation.setOnClickListener(view ->
        {
            //위치를 설정하는 액티비티 표시
            Intent intent = new Intent(EditEventActivity.this, SelectLocationActivity.class);
            String location = "";

            if (dataController.getEventValueAsString(CalendarContract.Events.EVENT_LOCATION) != null)
            {
                location = dataController.getEventValueAsString(CalendarContract.Events.EVENT_LOCATION);
            }

            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, location);
            startActivityForResult(intent, REQUEST_LOCATION);
        });

        /*
        참석자 상세정보 버튼
         */
        binding.attendeeLayout.showAttendeesDetail.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ContentValues selectedCalendar = new ContentValues();
                selectedCalendar.put(CalendarContract.Attendees.ATTENDEE_NAME, dataController.getEventValueAsString(CalendarContract.Events.ACCOUNT_NAME));
                selectedCalendar.put(CalendarContract.Attendees.ATTENDEE_EMAIL, dataController.getEventValueAsString(CalendarContract.Events.OWNER_ACCOUNT));

                Intent intent = new Intent(EditEventActivity.this, AttendeesActivity.class);
                intent.putParcelableArrayListExtra("attendeeList", (ArrayList<? extends Parcelable>) dataController.getAttendees());
                intent.putExtra("selectedCalendar", selectedCalendar);
                startActivityForResult(intent, AttendeesActivity.SHOW_DETAILS_FOR_ATTENDEES);
            }
        });
    }


    private void setRecurrenceText()
    {
        if (dataController.getEventValueAsString(CalendarContract.Events.RRULE) != null)
        {
            RecurrenceRule recurrenceRule = new RecurrenceRule();
            recurrenceRule.separateValues(dataController.getEventValueAsString(CalendarContract.Events.RRULE));
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

        switch (requestCode)
        {
            case REQUEST_RECURRENCE:
            {
                if (resultCode == RESULT_OK)
                {
                    dataController.putEventValue(CalendarContract.Events.RRULE, data.getStringExtra(CalendarContract.Events.RRULE));
                    setRecurrenceText();
                } else
                {

                }
                break;
            }

            case REQUEST_LOCATION:
            {
                if (resultCode == RESULT_OK)
                {
                    if (data.getStringExtra(CalendarContract.Events.EVENT_LOCATION) != null)
                    {
                        dataController.putEventValue(CalendarContract.Events.EVENT_LOCATION, data.getStringExtra(CalendarContract.Events.EVENT_LOCATION));
                        locationDTO = data.getParcelableExtra("location");
                        binding.locationLayout.eventLocation.setText(data.getStringExtra(CalendarContract.Events.EVENT_LOCATION));
                    } else
                    {
                        dataController.removeEventValue(CalendarContract.Events.EVENT_LOCATION);
                        locationDTO = null;
                        binding.locationLayout.eventLocation.setText("");
                    }

                } else if (resultCode == RESULT_CANCELED)
                {

                }
                break;
            }
            case AttendeesActivity.SHOW_DETAILS_FOR_ATTENDEES:
            {
                List<ContentValues> resultAttendeeList = data.getParcelableArrayListExtra("attendeeList");

                if (resultAttendeeList.isEmpty())
                {
                    // 리스트가 비어있으면 참석자 삭제 | 취소
                    dataController.removeAttendees();
                } else
                {
                    // 참석자 추가 | 변경
                    dataController.putAttendees(resultAttendeeList,
                            data.getBooleanExtra(CalendarContract.Events.GUESTS_CAN_MODIFY, false),
                            data.getBooleanExtra(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, false)
                            , data.getBooleanExtra(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, false));
                }
                setAttendeesText();
                break;
            }

            case REQUEST_TIMEZONE:
            {
                if (resultCode == RESULT_OK)
                {
                    TimeZone timeZone = (TimeZone) data.getSerializableExtra(CalendarContract.Events.EVENT_TIMEZONE);
                    dataController.putEventValue(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
                    setTimeZoneText();
                }
                break;
            }

            case ReminderActivity.ADD_REMINDER:
            {
                if (resultCode == ReminderActivity.RESULT_ADDED_REMINDER)
                {
                    ContentValues reminder = data.getParcelableExtra("reminder");
                    // reminder values는 분, 메소드값을 담고 있어야 한다
                    if (!isDuplicateReminder(reminder.getAsInteger(CalendarContract.Reminders.MINUTES)))
                    {
                        dataController.putReminder(reminder);
                        addReminder(reminder);
                    }
                } else if (resultCode == RESULT_CANCELED)
                {

                }
                break;
            }

            case ReminderActivity.MODIFY_REMINDER:
            {
                int previousMinutes = data.getIntExtra("previousMinutes", 0);

                // 중복되는 경우 진행하지 않음
                if (!isDuplicateReminder(previousMinutes))
                {
                    if (resultCode == ReminderActivity.RESULT_MODIFIED_REMINDER)
                    {
                        ContentValues reminder = data.getParcelableExtra("reminder");
                        modifyReminder(reminder, previousMinutes);
                    } else if (resultCode == ReminderActivity.RESULT_REMOVED_REMINDER)
                    {
                        removeReminder(previousMinutes);
                    }
                }
                break;

            }
        }

    }

    private boolean isDuplicateReminder(int newMinutes)
    {
        List<ContentValues> reminders = dataController.getReminders();

        for (ContentValues reminder : reminders)
        {
            if (reminder.getAsInteger(CalendarContract.Reminders.MINUTES) == newMinutes)
            {
                return true;
            }
        }
        return false;
    }

    private final View.OnClickListener reminderItemOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            ReminderItemHolder holder = (ReminderItemHolder) view.getTag();
            // modify
            Intent intent = new Intent(EditEventActivity.this, ReminderActivity.class);
            intent.putExtra("previousMinutes", holder.minutes);
            intent.putExtra("requestCode", ReminderActivity.MODIFY_REMINDER);
            startActivityForResult(intent, ReminderActivity.MODIFY_REMINDER);
        }
    };

    private final View.OnClickListener removeReminderOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            ReminderItemHolder holder = (ReminderItemHolder) view.getTag();
            removeReminder(holder.minutes);
        }
    };

    static class ReminderItemHolder
    {
        protected int minutes;

        protected ReminderItemHolder(int minutes)
        {
            this.minutes = minutes;
        }
    }


    private void addReminder(ContentValues reminder)
    {
        final int minutes = reminder.getAsInteger(CalendarContract.Reminders.MINUTES);
        final int method = reminder.getAsInteger(CalendarContract.Reminders.METHOD);

        TableRow tableRow = new TableRow(getApplicationContext());
        View row = getLayoutInflater().inflate(R.layout.event_reminder_item, null);

        TextView reminderValueTextView = ((TextView) row.findViewById(R.id.reminder_value));
        ImageButton removeButton = ((ImageButton) row.findViewById(R.id.remove_reminder_button));

        reminderValueTextView.setOnClickListener(reminderItemOnClickListener);
        removeButton.setOnClickListener(removeReminderOnClickListener);

        final ReminderItemHolder holder = new ReminderItemHolder(minutes);
        row.setTag(holder);
        removeButton.setTag(holder);

        ReminderDto reminderDto = EventUtil.convertAlarmMinutes(minutes);
        String alarmValueText = EventUtil.makeAlarmText(reminderDto, getApplicationContext());

        String text = alarmValueText + "(" + EventUtil.getReminderMethod(getApplicationContext(), method) + ")";
        reminderValueTextView.setText(text);

        tableRow.addView(row);
        tableRow.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36f, getResources().getDisplayMetrics())));
        binding.reminderLayout.remindersTable.addView(tableRow, binding.reminderLayout.remindersTable.getChildCount() - 1);
    }


    private void removeReminder(int minutes)
    {
        dataController.removeReminder(minutes);
        final int rowCount = binding.reminderLayout.remindersTable.getChildCount();

        // 아이템 삭제
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++)
        {
            ReminderItemHolder holder = (ReminderItemHolder) binding.reminderLayout.remindersTable.getChildAt(rowIndex).findViewById(R.id.reminder_value).getTag();
            if (holder.minutes == minutes)
            {
                binding.reminderLayout.remindersTable.removeViewAt(rowIndex);
                break;
            }
        }
    }

    private void modifyReminder(ContentValues reminder, int previousMinutes)
    {
        dataController.modifyReminder(reminder, previousMinutes);
        final int rowCount = binding.reminderLayout.remindersTable.getChildCount();

        // 아이템 수정
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++)
        {
            ReminderItemHolder holder = (ReminderItemHolder) binding.reminderLayout.remindersTable.getChildAt(rowIndex).findViewById(R.id.remove_reminder_button).getTag();

            if (holder.minutes == previousMinutes)
            {
                final int newMinutes = reminder.getAsInteger(CalendarContract.Reminders.MINUTES);
                final int newMethod = reminder.getAsInteger(CalendarContract.Reminders.METHOD);

                holder.minutes = newMinutes;

                ReminderDto reminderDto = EventUtil.convertAlarmMinutes(newMinutes);
                String alarmValueText = EventUtil.makeAlarmText(reminderDto, getApplicationContext());

                String text = alarmValueText + "(" + EventUtil.getReminderMethod(getApplicationContext(), newMethod) + ")";

                View row = binding.reminderLayout.remindersTable.getChildAt(rowIndex);
                ((TextView) row.findViewById(R.id.reminder_value)).setText(text);

                break;
            }
        }
    }


    private void setReminderText()
    {
        List<ContentValues> reminders = dataController.getReminders();

        for (ContentValues reminder : reminders)
        {
            addReminder(reminder);
        }
    }

    static class AttendeeItemHolder
    {
        protected final String email;

        public AttendeeItemHolder(String email)
        {
            this.email = email;
        }

    }

    private void addAttendee(ContentValues attendee)
    {
        TableRow tableRow = new TableRow(getApplicationContext());
        RelativeLayout row = (RelativeLayout) getLayoutInflater().inflate(R.layout.event_attendee_item, null);

        // add row to table
        LinearLayout attendeeInfoLayout = row.findViewById(R.id.attendee_info_layout);
        TextView attendeeEmailView = (TextView) row.findViewById(R.id.attendee_name);
        ImageButton removeButton = (ImageButton) row.findViewById(R.id.remove_attendee_button);

        ((LinearLayout) attendeeInfoLayout.findViewById(R.id.attendee_relationship_status_layout)).setVisibility(View.GONE);

        final AttendeeItemHolder holder = new AttendeeItemHolder(attendee.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL));
        row.setTag(holder);
        removeButton.setTag(holder);

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
                AttendeeItemHolder holder = (AttendeeItemHolder) view.getTag();
                removeAttendee(holder.email);
            }
        });
        // 이름, 메일 주소, 상태
        // 조직자 - attendeeName, 그 외 - email
        // email값을 항상 존재
        // 요약 - 캘린더명과 나
        // 상세 - 나(이메일), 캘린더(이메일)
        /*
        참석자 : a(organizer), b

        <구글 캘린더>
        주최자의 캘린더에서 이벤트를 볼때 : 참석자 1명, a(주최자), b
        수정 할때 : b

        참석자의 캘린더에서 이벤트를 볼때 : 참석자 1명, a(주최자), b
        수정 할때 : a, b(나)
         */
        final String selectedCalendarName = dataController.getEventValueAsString(CalendarContract.Events.CALENDAR_DISPLAY_NAME);
        final String selectedCalendarOwnerAccount = dataController.getEventValueAsString(CalendarContract.Events.OWNER_ACCOUNT);
        String attendeeName = null;

        // 참석자 목록에서 읽어온 경우
        if (attendee.containsKey(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP))
        {
            if (attendee.getAsInteger(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP) == CalendarContract.Attendees.RELATIONSHIP_ORGANIZER)
            {
                attendeeName = attendee.getAsString(CalendarContract.Attendees.ATTENDEE_NAME);
                if (attendeeName.equals(selectedCalendarName))
                {
                    attendeeName += "(ME)";
                    removeButton.setVisibility(View.GONE);
                }
            } else
            {
                attendeeName = attendee.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL);
                if (attendeeName.equals(selectedCalendarOwnerAccount))
                {
                    attendeeName += "(ME)";
                    removeButton.setVisibility(View.GONE);
                }
            }
        } else
        {
            // 추가한 경우
            attendeeName = attendee.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL);
            if (attendeeName.equals(selectedCalendarOwnerAccount))
            {
                attendeeName += "(ME)";
                removeButton.setVisibility(View.GONE);
            }
        }
        attendeeEmailView.setText(attendeeName);

        tableRow.addView(row);
        tableRow.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, getResources().getDisplayMetrics())));
        binding.attendeeLayout.eventAttendeesTable.addView(tableRow, binding.attendeeLayout.eventAttendeesTable.getChildCount() - 1);
    }


    private void removeAttendee(String email)
    {
        dataController.removeAttendee(email);

        // 아이템 삭제
        int rowCount = binding.attendeeLayout.eventAttendeesTable.getChildCount();
        if (rowCount == 2)
        {
            binding.attendeeLayout.eventAttendeesTable.removeViewAt(0);
            binding.attendeeLayout.showAttendeesDetail.setText(getString(R.string.add_attendee));
        } else if (rowCount >= 3)
        {
            for (int row = 0; row < rowCount - 1; row++)
            {
                AttendeeItemHolder holder = (AttendeeItemHolder) binding.attendeeLayout.eventAttendeesTable.getChildAt(row).getTag();

                if (holder.email.equals(email))
                {
                    binding.attendeeLayout.eventAttendeesTable.removeViewAt(row);
                    break;
                }
            }
        }
    }

    private void setAttendeesText()
    {
        List<ContentValues> attendees = dataController.getAttendees();

        if (binding.attendeeLayout.eventAttendeesTable.getChildCount() >= 2)
        {
            binding.attendeeLayout.eventAttendeesTable.removeViews(0, binding.attendeeLayout.eventAttendeesTable.getChildCount() - 1);
        }

        if (attendees.isEmpty())
        {
            // 참석자 버튼 텍스트 수정

            binding.attendeeLayout.showAttendeesDetail.setText(getString(R.string.add_attendee));
        } else
        {
            binding.attendeeLayout.showAttendeesDetail.setText(getString(R.string.show_attendees));

            for (ContentValues attendee : attendees)
            {
                addAttendee(attendee);
            }
        }

    }

    private void setDateText(int dateType)
    {
        if (dateType == START_DATETIME)
        {
            binding.timeLayout.startDate.setText(EventUtil.convertDate(dataController.getStart().getTimeInMillis()));
        } else
        {
            binding.timeLayout.endDate.setText(EventUtil.convertDate(dataController.getEnd().getTimeInMillis()));
        }
    }

    private void setTimeText(int dateType)
    {
        // 설정에 12시간, 24시간 단위 변경 가능
        if (dateType == START_DATETIME)
        {
            binding.timeLayout.startTime.setText(EventUtil.convertTime(dataController.getStart().getTimeInMillis(), App.is24HourSystem));
        } else
        {
            binding.timeLayout.endTime.setText(EventUtil.convertTime(dataController.getEnd().getTimeInMillis(), App.is24HourSystem));
        }
    }

    private void setCalendarText()
    {
        binding.calendarLayout.calendarColor.setBackgroundColor(EventUtil.getColor(dataController.getEventValueAsInt(CalendarContract.Events.CALENDAR_COLOR)));
        binding.calendarLayout.calendarDisplayName.setText(dataController.getEventValueAsString(CalendarContract.Events.CALENDAR_DISPLAY_NAME));
        binding.calendarLayout.calendarAccountName.setText(dataController.getEventValueAsString(CalendarContract.Events.ACCOUNT_NAME));
    }

    private void setTimeZoneText()
    {
        TimeZone timeZone = TimeZone.getTimeZone(dataController.getEventValueAsString(CalendarContract.Events.EVENT_TIMEZONE));
        binding.timeLayout.eventTimezone.setText(timeZone.getDisplayName(Locale.KOREAN));
    }

    private void saveEvent()
    {
        // 시간이 바뀌는 경우, 알림 데이터도 변경해야함.
        // 알림 재설정
        final int calendarId = dataController.getEventValueAsInt(CalendarContract.Events.CALENDAR_ID);
        final long newEventId = viewModel.addEvent(dataController.getNewEventData().getEVENT());

        if (dataController.getEventValueAsBoolean(CalendarContract.Events.HAS_ALARM))
        {
            List<ContentValues> reminders = dataController.getReminders();

            for (ContentValues reminder : reminders)
            {
                reminder.put(CalendarContract.Reminders.CALENDAR_ID, calendarId);
                reminder.put(CalendarContract.Reminders.EVENT_ID, newEventId);
            }

            viewModel.addReminders(reminders);
        }

        // 참석자 재설정
        if (!dataController.getAttendees().isEmpty())
        {
            List<ContentValues> attendees = dataController.getAttendees();
            for (ContentValues attendee : attendees)
            {
                attendee.put(CalendarContract.Attendees.CALENDAR_ID, calendarId);
                attendee.put(CalendarContract.Attendees.EVENT_ID, newEventId);
            }
            viewModel.addAttendees(attendees);
            //초대여부 다이얼로그 표시
        }

        // 상세 위치 데이터 저장
        if (locationDTO != null)
        {
            locationViewModel.addLocation(locationDTO, new CarrierMessagingService.ResultCallback<Boolean>()
            {
                @Override
                public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException
                {
                    setResult(RESULT_OK);
                    finish();
                }
            });
        } else
        {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void modifyEvent()
    {
        final int calendarId = dataController.getEventValueAsInt(CalendarContract.Events.CALENDAR_ID);
        final long eventId = dataController.getEventValueAsLong(CalendarContract.Events._ID);

        if (dataController.getModifiedEventData().getEVENT().size() > 0)
        {
            ContentValues event = dataController.getModifiedEventData().getEVENT();
            event.put(CalendarContract.Events.CALENDAR_ID, calendarId);
            event.put(CalendarContract.Events._ID, eventId);
            viewModel.updateEvent(event);
        }

        // 알람
        if (dataController.getEventValueAsBoolean(CalendarContract.Events.HAS_ALARM))
        {
            // 수정된 부분만 변경
            List<ContentValues> reminders = dataController.getReminders();
            for (ContentValues reminder : reminders)
            {
                reminder.put(CalendarContract.Reminders.CALENDAR_ID, calendarId);
                reminder.put(CalendarContract.Reminders.EVENT_ID, eventId);
            }
            viewModel.addReminders(reminders);
        }

        // 참석자
        if (!dataController.getAttendees().isEmpty())
        {
            // 수정된 부분만 변경
            List<ContentValues> attendees = dataController.getAttendees();
            for (ContentValues attendee : attendees)
            {
                attendee.put(CalendarContract.Attendees.CALENDAR_ID, calendarId);
                attendee.put(CalendarContract.Attendees.EVENT_ID, eventId);
            }
            viewModel.addAttendees(attendees);
            //초대여부 다이얼로그 표시
        }

        if (dataController.getEventValueAsString(CalendarContract.Events.EVENT_LOCATION) != null)
        {
            // 위치가 추가 || 변경된 경우

            //상세 위치가 지정되어 있는지 확인
            locationViewModel.hasDetailLocation(calendarId, eventId, new CarrierMessagingService.ResultCallback<Boolean>()
            {
                @Override
                public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException
                {
                    if (aBoolean)
                    {
                        // 상세위치가 지정되어 있고, 현재 위치를 변경하려는 상태
                        locationViewModel.modifyLocation(locationDTO, new CarrierMessagingService.ResultCallback<Boolean>()
                        {
                            @Override
                            public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException
                            {
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                    } else
                    {
                        // 추가하는 경우
                        locationViewModel.addLocation(locationDTO, new CarrierMessagingService.ResultCallback<Boolean>()
                        {
                            @Override
                            public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException
                            {
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                    }
                }
            });

        } else
        {

            if (dataController.getSavedEventData().getEVENT().getAsString(CalendarContract.Events.EVENT_LOCATION) != null)
            {
                // 현재 위치를 삭제하려고 하는 상태
                locationViewModel.removeLocation(calendarId, eventId, new CarrierMessagingService.ResultCallback<Boolean>()
                {
                    @Override
                    public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException
                    {
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            } else
            {
                //위치를 원래 설정하지 않은 경우
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    private void showDatePicker()
    {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();

        datePicker = builder.setTitleText(R.string.datepicker)
                .setSelection(new Pair<>(dataController.getStart().getTimeInMillis(), dataController.getEnd().getTimeInMillis()))
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .build();
        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>()
        {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection)
            {
                if (selection.first != null)
                {
                    dataController.setStart(selection.first);
                    setDateText(START_DATETIME);
                }
                if (selection.second != null)
                {
                    dataController.setEnd(selection.second);
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
        Calendar calendar = null;

        if (dateType == START_DATETIME)
        {
            calendar = dataController.getStart();
        } else if (dateType == END_DATETIME)
        {
            calendar = dataController.getEnd();
        }

        MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder();
        timePicker = builder.setTitleText((dateType == START_DATETIME ? getString(R.string.start) : getString(R.string.end)) + getString(R.string.timepicker))
                .setTimeFormat(App.is24HourSystem ? TimeFormat.CLOCK_24H : TimeFormat.CLOCK_12H)
                .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                .setMinute(calendar.get(Calendar.MINUTE))
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK).build();

        timePicker.addOnPositiveButtonClickListener(view ->
        {
            Calendar newCalendar = (dateType == START_DATETIME) ? dataController.getStart() : dataController.getEnd();
            newCalendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            newCalendar.set(Calendar.MINUTE, timePicker.getMinute());

            if (dateType == START_DATETIME)
            {
                dataController.setStart(newCalendar.getTimeInMillis());
                setTimeText(START_DATETIME);
            } else
            {
                dataController.setEnd(newCalendar.getTimeInMillis());
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
        binding.accesslevelLayout.eventAccessLevel.setText(EventUtil.convertAccessLevel(dataController.getEventValueAsInt(CalendarContract.Events.ACCESS_LEVEL), getApplicationContext()));
    }

    private void setAvailabilityText()
    {
        binding.availabilityLayout.eventAvailability.setText(EventUtil.convertAvailability(dataController.getEventValueAsInt(CalendarContract.Events.AVAILABILITY), getApplicationContext()));
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

        @SuppressLint("NonConstantResourceId")
        @Override
        public void afterTextChanged(Editable editable)
        {
            // 텍스트가 변경된 후 수행
            switch (focusedViewId)
            {
                case R.id.title:
                    if (editable.toString().isEmpty())
                    {
                        dataController.removeEventValue(CalendarContract.Events.TITLE);
                    } else
                    {
                        dataController.putEventValue(CalendarContract.Events.TITLE, editable.toString());
                    }
                    break;

                case R.id.description_edittext:
                    if (editable.toString().isEmpty())
                    {
                        dataController.removeEventValue(CalendarContract.Events.DESCRIPTION);
                    } else
                    {
                        dataController.putEventValue(CalendarContract.Events.DESCRIPTION, editable.toString());
                    }
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

