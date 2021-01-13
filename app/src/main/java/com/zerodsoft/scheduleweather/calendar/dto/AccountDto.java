package com.zerodsoft.scheduleweather.calendar.dto;

import java.util.ArrayList;
import java.util.List;

public class AccountDto
{
    private List<CalendarDto> calendars = new ArrayList<>();
    private String accountName;
    private String accountType;

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

    public String getAccountName()
    {
        return accountName;
    }

    public AccountDto setAccountName(String accountName)
    {
        this.accountName = accountName;
        return this;
    }

    public String getAccountType()
    {
        return accountType;
    }

    public AccountDto setAccountType(String accountType)
    {
        this.accountType = accountType;
        return this;
    }

    public void addCalendar(CalendarDto calendarDto)
    {
        calendars.add(calendarDto);
    }
}
