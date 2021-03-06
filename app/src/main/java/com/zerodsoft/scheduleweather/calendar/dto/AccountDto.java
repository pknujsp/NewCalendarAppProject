package com.zerodsoft.scheduleweather.calendar.dto;

import android.content.ContentValues;
import android.provider.CalendarContract;

import java.util.ArrayList;
import java.util.List;

public class AccountDto
{
    private List<ContentValues> calendars = new ArrayList<>();
    private String accountName;
    private String accountType;
    private String ownerAccount;

    public AccountDto()
    {
    }

    public List<ContentValues> getCalendars()
    {
        return calendars;
    }

    public AccountDto setCalendars(List<ContentValues> calendars)
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

    public AccountDto setOwnerAccount(String ownerAccount)
    {
        this.ownerAccount = ownerAccount;
        return this;
    }

    public String getOwnerAccount()
    {
        return ownerAccount;
    }

    public void addCalendar(ContentValues calendar)
    {
        calendars.add(calendar);
    }
}
