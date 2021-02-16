package com.zerodsoft.scheduleweather.event.event;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.databinding.EventFragmentBinding;
import com.zerodsoft.scheduleweather.event.common.interfaces.IFab;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.utility.RecurrenceRule;
import com.zerodsoft.scheduleweather.utility.model.ReminderDto;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class EventFragment extends Fragment
{
    // 참석자가 있는 경우 참석 여부 표시
    // 알림 값을 클릭하면 알림표시를 하는 시각을 보여준다
    private EventFragmentBinding binding;
    private ContentValues instance;
    private List<ContentValues> attendeeList;
    private CalendarViewModel viewModel;
    private Integer calendarId;
    private Long instanceId;
    private Long eventId;
    private Long begin;
    private Long end;

    private IFab iFab;

    private AlertDialog attendeeDialog;

    public EventFragment(IFab iFab)
    {
        this.iFab = iFab;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        calendarId = arguments.getInt("calendarId");
        instanceId = arguments.getLong("instanceId");
        eventId = arguments.getLong("eventId");
        begin = arguments.getLong("begin");
        end = arguments.getLong("end");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = EventFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        binding.eventRemindersView.addReminderButton.setVisibility(View.GONE);
        binding.eventAttendeesView.showAttendeesDetail.setVisibility(View.GONE);
        binding.eventDatetimeView.allDaySwitchLayout.setVisibility(View.GONE);
        binding.eventDatetimeView.startTime.setVisibility(View.GONE);
        binding.eventDatetimeView.endTime.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);

        viewModel.init(getContext());
        viewModel.getInstance(calendarId, instanceId, begin, end);

        viewModel.getInstanceLiveData().observe(getViewLifecycleOwner(), new Observer<DataWrapper<ContentValues>>()
        {
            @Override
            public void onChanged(DataWrapper<ContentValues> contentValuesDataWrapper)
            {
                if (contentValuesDataWrapper.getData() != null)
                {
                    instance = contentValuesDataWrapper.getData();
                    init();
                }
            }
        });

        viewModel.getAttendeeListLiveData().observe(getViewLifecycleOwner(), new Observer<DataWrapper<List<ContentValues>>>()
        {
            @Override
            public void onChanged(DataWrapper<List<ContentValues>> listDataWrapper)
            {
                if (listDataWrapper.getData() != null)
                {
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            attendeeList = listDataWrapper.getData();
                            // 참석자가 없는 경우 - 테이블 숨김, 참석자 없음 텍스트 표시
                            if (listDataWrapper.getData().isEmpty())
                            {
                                binding.eventAttendeesView.notAttendees.setVisibility(View.VISIBLE);
                                binding.eventAttendeesView.eventAttendeesTable.setVisibility(View.GONE);
                            } else
                            {
                                binding.eventAttendeesView.notAttendees.setVisibility(View.GONE);
                                binding.eventAttendeesView.eventAttendeesTable.setVisibility(View.VISIBLE);

                                setAttendeesText(listDataWrapper.getData());
                            }
                        }
                    });

                }
            }
        });

        viewModel.getReminderListLiveData().observe(getViewLifecycleOwner(), new Observer<DataWrapper<List<ContentValues>>>()
        {
            @Override
            public void onChanged(DataWrapper<List<ContentValues>> listDataWrapper)
            {
                if (listDataWrapper.getData() != null)
                {
                    // 알림
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            // 알람이 없는 경우 - 알람 테이블 숨김, 알람 없음 텍스트 표시
                            if (listDataWrapper.getData().isEmpty())
                            {
                                binding.eventRemindersView.notReminder.setVisibility(View.VISIBLE);
                                binding.eventRemindersView.remindersTable.setVisibility(View.GONE);
                            } else
                            {
                                binding.eventRemindersView.notReminder.setVisibility(View.GONE);
                                binding.eventRemindersView.remindersTable.setVisibility(View.VISIBLE);
                                setReminderText(listDataWrapper.getData());
                            }
                        }
                    });

                }
            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    private void setAttendeesText(List<ContentValues> attendees)
    {
        // 참석자 수, 참석 여부
        LayoutInflater layoutInflater = getLayoutInflater();

        for (ContentValues attendee : attendees)
        {
            TableRow tableRow = new TableRow(getContext());
            View row = layoutInflater.inflate(R.layout.event_attendee_item, null);
            // add row to table
            // 이름, 메일 주소, 상태
            // 조직자 - attendeeName, 그 외 - email
            final String attendeeName = attendee.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL);
            final int attendeeStatus = attendee.getAsInteger(CalendarContract.Attendees.ATTENDEE_STATUS);
            final int attendeeRelationship = attendee.getAsInteger(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP);

            final String attendeeStatusStr = EventUtil.convertAttendeeStatus(attendeeStatus, getContext());
            final String attendeeRelationshipStr = EventUtil.convertAttendeeRelationship(attendeeRelationship, getContext());

            LinearLayout attendeeInfoLayout = (LinearLayout) row.findViewById(R.id.attendee_info_layout);

            TextView attendeeEmailView = (TextView) row.findViewById(R.id.attendee_name);
            TextView attendeeRelationshipView = (TextView) row.findViewById(R.id.attendee_relationship);
            TextView attendeeStatusView = (TextView) row.findViewById(R.id.attendee_status);
            // 삭제버튼 숨기기
            row.findViewById(R.id.remove_attendee_button).setVisibility(View.GONE);

            attendeeInfoLayout.setClickable(true);
            attendeeInfoLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    // logic for communications with attendee
                    if (attendeeDialog == null)
                    {
                        final String[] itemList = {"기능 구성중"};
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext())
                                .setTitle(attendeeName + "(" + attendeeRelationshipStr + ", " + attendeeStatusStr + ")")
                                .setItems(itemList, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {

                                    }
                                });
                        attendeeDialog = builder.create();
                    }
                    attendeeDialog.setTitle(attendeeName + "(" + attendeeRelationshipStr + ", " + attendeeStatusStr + ")");
                    attendeeDialog.show();
                    // 기능목록 파악중
                }
            });

            attendeeEmailView.setText(attendeeName);
            attendeeRelationshipView.setText(attendeeRelationshipStr);
            attendeeStatusView.setText(attendeeStatusStr);

            tableRow.addView(row);
            binding.eventAttendeesView.eventAttendeesTable.addView(tableRow,
                    new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }


    private void init()
    {
        // 제목, 캘린더, 시간, 시간대, 반복, 알림, 설명, 위치, 공개범위, 유효성, 참석자
        // 캘린더, 시간대, 참석자 정보는 따로 불러온다.
        //제목
        binding.eventTitle.setText(instance.getAsString(CalendarContract.Instances.TITLE) == null ? "EMPTY" :
                instance.getAsString(CalendarContract.Instances.TITLE));
        //캘린더
        setCalendarText();

        //시간 , allday구분
        setDateTimeText(instance.getAsLong(CalendarContract.Instances.BEGIN), instance.getAsLong(CalendarContract.Instances.END));

        // 시간대
        if (!instance.getAsBoolean(CalendarContract.Instances.ALL_DAY))
        {
            String timeZoneStr = instance.getAsString(CalendarContract.Instances.EVENT_TIMEZONE);
            TimeZone timeZone = TimeZone.getTimeZone(timeZoneStr);
            setTimeZoneText(timeZone);
        } else
        {
            // allday이면 시간대 뷰를 숨긴다
            binding.eventDatetimeView.eventTimezoneLayout.setVisibility(View.GONE);
        }

        // 반복
        if (instance.getAsString(CalendarContract.Instances.RRULE) != null)
        {
            setRecurrenceText(instance.getAsString(CalendarContract.Instances.RRULE));
        }

        // 알람
        if (instance.getAsBoolean(CalendarContract.Instances.HAS_ALARM))
        {
            viewModel.getReminders(calendarId, eventId);
        } else
        {
            // 알람이 없으면 알람 테이블을 숨기고, 알람 없음 텍스트를 표시한다.
            binding.eventRemindersView.notReminder.setVisibility(View.VISIBLE);
            binding.eventRemindersView.remindersTable.setVisibility(View.GONE);
        }

        // 설명
        binding.eventDescriptionView.descriptionEdittext.setVisibility(View.GONE);
        binding.eventDescriptionView.descriptionTextview.setText(instance.getAsString(CalendarContract.Instances.DESCRIPTION) != null ? instance.getAsString(CalendarContract.Instances.DESCRIPTION)
                : "");
        // 위치
        binding.eventLocationView.eventLocation.setText(instance.getAsString(CalendarContract.Instances.EVENT_LOCATION));

        //fab설정
        if (instance.containsKey(CalendarContract.Instances.EVENT_LOCATION))
        {
            iFab.setVisibility(IFab.TYPE_SELECT_LOCATION, View.VISIBLE);
        } else
        {
            iFab.setVisibility(IFab.TYPE_SELECT_LOCATION, View.GONE);
        }
        // 참석자
        viewModel.getAttendees(calendarId, eventId);

        // 공개 범위 표시
        setAccessLevelText();

        // 유효성 표시
        setAvailabilityText();
    }

    private void setAvailabilityText()
    {
        binding.eventAvailabilityView.eventAvailability.setText(EventUtil.convertAvailability(instance.getAsInteger(CalendarContract.Instances.AVAILABILITY), getContext()));
    }

    private void setAccessLevelText()
    {
        binding.eventAccessLevelView.eventAccessLevel.setText(EventUtil.convertAccessLevel(instance.getAsInteger(CalendarContract.Instances.ACCESS_LEVEL), getContext()));
    }


    private void setDateTimeText(long start, long end)
    {
        boolean allDay = instance.getAsBoolean(CalendarContract.Instances.ALL_DAY);
        String startStr = EventUtil.convertDateTime(start, allDay, App.is24HourSystem);
        String endStr = EventUtil.convertDateTime(end, allDay, App.is24HourSystem);

        binding.eventDatetimeView.startDate.setText(startStr);
        binding.eventDatetimeView.endDate.setText(endStr);
    }

    private void setTimeZoneText(TimeZone timeZone)
    {
        binding.eventDatetimeView.eventTimezone.setText(timeZone.getDisplayName(Locale.KOREAN));
    }

    private void setReminderText(List<ContentValues> reminders)
    {
        LayoutInflater layoutInflater = getLayoutInflater();

        for (ContentValues reminder : reminders)
        {
            ReminderDto reminderDto = EventUtil.convertAlarmMinutes(reminder.getAsInteger(CalendarContract.Reminders.MINUTES));
            String alarmValueText = EventUtil.makeAlarmText(reminderDto, getContext());

            TableRow tableRow = new TableRow(getContext());
            View row = layoutInflater.inflate(R.layout.event_reminder_item, null);

            // 삭제 버튼 숨기기
            row.findViewById(R.id.remove_reminder_button).setVisibility(View.GONE);
            ((TextView) row.findViewById(R.id.reminder_value)).setText(alarmValueText);
            row.findViewById(R.id.reminder_value).setClickable(false);

            tableRow.addView(row);
            binding.eventRemindersView.remindersTable.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    private void setRecurrenceText(String rRule)
    {
        RecurrenceRule recurrenceRule = new RecurrenceRule();
        recurrenceRule.separateValues(rRule);
        binding.eventRecurrenceView.eventRecurrence.setText(recurrenceRule.interpret(getContext()));
    }

    private void setCalendarText()
    {
        binding.eventCalendarView.calendarColor.setBackgroundColor(EventUtil.getColor(instance.getAsInteger(CalendarContract.Instances.CALENDAR_COLOR)));
        binding.eventCalendarView.calendarDisplayName.setText(instance.getAsString(CalendarContract.Instances.CALENDAR_DISPLAY_NAME));
        binding.eventCalendarView.calendarAccountName.setText(instance.getAsString(CalendarContract.Instances.OWNER_ACCOUNT));
    }

    public ContentValues getInstance()
    {
        return instance;
    }

    public List<ContentValues> getAttendeeList()
    {
        return attendeeList;
    }
}
