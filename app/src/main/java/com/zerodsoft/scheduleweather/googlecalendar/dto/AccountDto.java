package com.zerodsoft.scheduleweather.googlecalendar.dto;

import java.util.List;

public class AccountDto
{
    private List<CalendarDto> calendars;

    public AccountDto()
    {
    }

    public List<CalendarDto> getCalendars()
    {
        return calendars;
    }

    public AccountDto setCalendars(List<CalendarDto> calendars)
    {
        this.calendars = calendars;
        return this;
    }
}
