package com.zerodsoft.scheduleweather.event.event;

import android.content.ContentValues;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.databinding.EventFragmentBinding;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.RecurrenceRule;
import com.zerodsoft.scheduleweather.utility.model.ReminderDto;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class EventFragment extends Fragment
{
    // 참석자가 있는 경우 참석 여부 표시
    // 알림 값을 클릭하면 알림표시를 하는 시각을 보여준다
    private EventFragmentBinding binding;
    private ContentValues event;
    private CalendarViewModel viewModel;
    private Integer calendarId;
    private Long eventId;

    public EventFragment()
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        calendarId = arguments.getInt("calendarId");
        eventId = arguments.getLong("eventId");
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
        binding.eventRemindersView.remindersTable.removeAllViews();
        binding.eventAttendeesView.eventAttendeesTable.removeAllViews();
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
        viewModel.getEvent(calendarId, eventId);

        viewModel.getEventLiveData().observe(getViewLifecycleOwner(), new Observer<DataWrapper<ContentValues>>()
        {
            @Override
            public void onChanged(DataWrapper<ContentValues> contentValuesDataWrapper)
            {
                if (contentValuesDataWrapper.getData() != null)
                {
                    event = contentValuesDataWrapper.getData();
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
                            // 참석자가 없는 경우 - 테이블 숨김, 참석자 없음 텍스트 표시
                            if (listDataWrapper.getData().isEmpty())
                            {
                                binding.eventAttendeesView.notAttendees.setVisibility(View.VISIBLE);
                                binding.eventAttendeesView.eventAttendeesTable.setVisibility(View.GONE);
                            } else
                            {
                                binding.eventAttendeesView.notAttendees.setVisibility(View.GONE);
                                binding.eventAttendeesView.eventAttendeesTable.setVisibility(View.VISIBLE);
                                binding.eventAttendeesView.eventAttendeesTable.removeAllViews();
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
                                binding.eventRemindersView.remindersTable.removeAllViews();
                                setReminderText(listDataWrapper.getData());
                            }
                        }
                    });

                }
            }
        });
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

            String attendeeStatusStr = EventUtil.convertAttendeeStatus(attendeeStatus, getContext());
            String attendeeRelationshipStr = EventUtil.convertAttendeeRelationship(attendeeRelationship, getContext());

            View row = layoutInflater.inflate(R.layout.event_attendee_item, null);
            // add row to table

            LinearLayout attendeeInfoLayout = row.findViewById(R.id.attendee_info_layout);
            TextView attendeeEmailView = (TextView) row.findViewById(R.id.attendee_name);
            TextView attendeeRelationshipView = (TextView) row.findViewById(R.id.attendee_relationship);
            TextView attendeeStatusView = (TextView) row.findViewById(R.id.attendee_status);
            ImageButton removeButton = (ImageButton) row.findViewById(R.id.remove_attendee_button);

            attendeeInfoLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    // logic for communications with attendee

                }
            });
            // 삭제버튼 숨기기
            removeButton.setVisibility(View.GONE);

            attendeeEmailView.setText(attendeeRelationship == CalendarContract.Attendees.RELATIONSHIP_ORGANIZER ? attendeeName : attendeeEmail);
            attendeeRelationshipView.setText(attendeeRelationshipStr);
            attendeeStatusView.setText(attendeeStatusStr);

            binding.eventAttendeesView.eventAttendeesTable.addView(row, binding.eventAttendeesView.eventAttendeesTable.getChildCount() - 1);
        }
    }


    private void init()
    {
        // 제목, 캘린더, 시간, 시간대, 반복, 알림, 설명, 위치, 공개범위, 유효성, 참석자
        // 캘린더, 시간대, 참석자 정보는 따로 불러온다.
        //제목
        binding.eventTitle.setText(event.getAsString(CalendarContract.Events.TITLE) == null ? "EMPTY" :
                event.getAsString(CalendarContract.Events.TITLE));
        //캘린더
        setCalendarText();

        //시간 , allday구분
        setDateTimeText(event.getAsLong(CalendarContract.Events.DTSTART), event.getAsLong(CalendarContract.Events.DTEND));

        // 시간대
        if (!event.getAsBoolean(CalendarContract.Events.ALL_DAY))
        {
            String timeZoneStr = event.getAsString(CalendarContract.Events.EVENT_TIMEZONE);
            TimeZone timeZone = TimeZone.getTimeZone(timeZoneStr);
            setTimeZoneText(timeZone);
        } else
        {
            // allday이면 시간대 뷰를 숨긴다
            binding.eventDatetimeView.eventTimezoneLayout.setVisibility(View.GONE);
        }

        // 반복
        if (event.getAsString(CalendarContract.Events.RRULE) != null)
        {
            setRecurrenceText(event.getAsString(CalendarContract.Events.RRULE));
        }

        // 알람
        if (event.getAsBoolean(CalendarContract.Events.HAS_ALARM))
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
        binding.eventDescriptionView.descriptionTextview.setText(event.getAsString(CalendarContract.Events.DESCRIPTION) != null ? event.getAsString(CalendarContract.Events.DESCRIPTION)
                : "");
        // 위치
        binding.eventLocationView.eventLocation.setText(event.getAsString(CalendarContract.Events.EVENT_LOCATION) != null ? event.getAsString(CalendarContract.Events.EVENT_LOCATION)
                : "");

        // 참석자
        viewModel.getAttendees(calendarId, eventId);

        // 공개 범위 표시
        setAccessLevelText();

        // 유효성 표시
        setAvailabilityText();

    }

    private void setAvailabilityText()
    {
        binding.eventAvailabilityView.eventAvailability.setText(EventUtil.convertAvailability(event.getAsInteger(CalendarContract.Events.AVAILABILITY), getContext()));
    }

    private void setAccessLevelText()
    {
        binding.eventAccessLevelView.eventAccessLevel.setText(EventUtil.convertAccessLevel(event.getAsInteger(CalendarContract.Events.ACCESS_LEVEL), getContext()));
    }


    private void setDateTimeText(long start, long end)
    {
        boolean allDay = event.getAsBoolean(CalendarContract.Events.ALL_DAY);
        String startStr = EventUtil.convertDateTime(start, allDay,  App.is24HourSystem);
        String endStr = EventUtil.convertDateTime(end, allDay,  App.is24HourSystem);

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

            View row = layoutInflater.inflate(R.layout.event_reminder_item, null);
            // 삭제 버튼 숨기기
            row.findViewById(R.id.remove_reminder_button).setVisibility(View.GONE);
            ((TextView) row.findViewById(R.id.reminder_value)).setText(alarmValueText);
            binding.eventRemindersView.remindersTable.addView(row, binding.eventRemindersView.remindersTable.getChildCount() - 1);
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
        binding.eventCalendarView.calendarColor.setBackgroundColor(EventUtil.getColor(event.getAsInteger(CalendarContract.Events.CALENDAR_COLOR)));
        binding.eventCalendarView.calendarDisplayName.setText(event.getAsString(CalendarContract.Events.CALENDAR_DISPLAY_NAME));
        binding.eventCalendarView.calendarAccountName.setText(event.getAsString(CalendarContract.Events.ACCOUNT_NAME));
    }

    public ContentValues getEvent()
    {
        return event;
    }
}
