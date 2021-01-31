package com.zerodsoft.scheduleweather.event.event;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.databinding.EventFragmentBinding;
import com.zerodsoft.scheduleweather.etc.CalendarUtil;
import com.zerodsoft.scheduleweather.etc.EventViewUtil;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.utility.CalendarEventUtil;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.RecurrenceRule;
import com.zerodsoft.scheduleweather.utility.model.ReminderDto;

import java.sql.Time;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class EventFragment extends Fragment
{
    private EventFragmentBinding binding;
    private ContentValues event;
    private boolean is24HourSystem = false;

    public EventFragment()
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        event = arguments.getParcelable("event");
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
        init();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
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
            binding.eventDatetimeView.eventTimezoneLayout.setVisibility(View.GONE);
        }
        // 반복
        if (event.getAsString(CalendarContract.Events.RRULE) != null)
        {
            setRecurrenceText(event.getAsString(CalendarContract.Events.RRULE));
        }

        // 알림
        if (event.getAsBoolean(CalendarContract.Events.HAS_ALARM))
        {
            //call reminder
        }
        // 설명
        binding.eventDescription.setText(event.getAsString(CalendarContract.Events.DESCRIPTION) != null ? event.getAsString(CalendarContract.Events.DESCRIPTION)
                : "");
        // 위치
        binding.eventLocation.setText(event.getAsString(CalendarContract.Events.EVENT_LOCATION) != null ? event.getAsString(CalendarContract.Events.EVENT_LOCATION)
                : "");

        // 참석자
        if (event.getAsBoolean(CalendarContract.Events.HAS_ATTENDEE_DATA))
        {
            //call attendees
        }

        // 공개 범위 표시
        setAccessLevelText();

        // 유효성 표시
        setAvailabilityText();

    }

    private void setAvailabilityText()
    {
        String[] availabilityItems = {getString(R.string.busy), getString(R.string.free)};

        switch (event.getAsInteger(CalendarContract.Events.AVAILABILITY))
        {
            case CalendarContract.Events.AVAILABILITY_BUSY:
                binding.eventAvailability.setText(availabilityItems[0]);
                break;
            case CalendarContract.Events.AVAILABILITY_FREE:
                binding.eventAvailability.setText(availabilityItems[1]);
                break;
            case CalendarContract.Events.AVAILABILITY_TENTATIVE:
                break;
        }
    }

    private void setAccessLevelText()
    {
        String[] accessLevelItems = {getString(R.string.access_default), getString(R.string.access_public), getString(R.string.access_private)};

        switch (event.getAsInteger(CalendarContract.Events.ACCESS_LEVEL))
        {
            case CalendarContract.Events.ACCESS_DEFAULT:
                binding.eventAccessLevel.setText(accessLevelItems[0]);
                break;
            case CalendarContract.Events.ACCESS_CONFIDENTIAL:
                break;
            case CalendarContract.Events.ACCESS_PRIVATE:
                binding.eventAccessLevel.setText(accessLevelItems[2]);
                break;
            case CalendarContract.Events.ACCESS_PUBLIC:
                binding.eventAccessLevel.setText(accessLevelItems[1]);
                break;
        }
    }


    private void setDateTimeText(long start, long end)
    {
        String startStr = null;
        String endStr = null;

        if (event.getAsBoolean(CalendarContract.Events.ALL_DAY))
        {
            startStr = ClockUtil.YYYY_년_M_월_D_일_E.format(new Date(start));
            endStr = ClockUtil.YYYY_년_M_월_D_일_E.format(new Date(end));
        } else
        {
            startStr = ClockUtil.YYYY_년_M_월_D_일_E.format(new Date(start)) + " " +
                    (is24HourSystem ? ClockUtil.HOURS_24.format(new Date(start))
                            : ClockUtil.HOURS_12.format(new Date(start)));

            endStr = ClockUtil.YYYY_년_M_월_D_일_E.format(new Date(end)) + " " +
                    (is24HourSystem ? ClockUtil.HOURS_24.format(new Date(end))
                            : ClockUtil.HOURS_12.format(new Date(end)));
        }

        binding.eventDatetimeView.eventStartdatetime.setText(startStr);
        binding.eventDatetimeView.eventEnddatetime.setText(endStr);
    }

    private void setTimeZoneText(TimeZone timeZone)
    {
        binding.eventDatetimeView.eventTimezone.setText(timeZone.getDisplayName(Locale.KOREAN));
    }

    private void setReminderText(int minutes)
    {
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
        binding.eventReminder.setText(stringBuilder.toString());
    }

    private void setRecurrenceText(String rRule)
    {
        RecurrenceRule recurrenceRule = new RecurrenceRule();
        recurrenceRule.separateValues(rRule);
        binding.eventRecurrence.setText(recurrenceRule.interpret(getContext()));
    }

    private void setCalendarText()
    {
        binding.eventCalendarView.calendarColor.setBackgroundColor(CalendarUtil.getColor(event.getAsInteger(CalendarContract.Events.CALENDAR_COLOR)));
        binding.eventCalendarView.calendarDisplayName.setText(event.getAsString(CalendarContract.Events.CALENDAR_DISPLAY_NAME));
        binding.eventCalendarView.calendarAccountName.setText(event.getAsString(CalendarContract.Events.ACCOUNT_NAME));
    }
}
