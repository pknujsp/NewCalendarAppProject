package com.zerodsoft.scheduleweather.calendar.dto;

import android.content.ContentValues;

import java.util.List;

public class CalendarInstance
{
    private List<ContentValues> instanceList;
    private long calendarId;

    public CalendarInstance(List<ContentValues> instanceList, long calendarId)
    {
        this.instanceList = instanceList;
        this.calendarId = calendarId;
    }

    public List<ContentValues> getInstanceList()
    {
        return instanceList;
    }

    public void setInstanceList(List<ContentValues> instanceList)
    {
        this.instanceList = instanceList;
    }

    public long getCalendarId()
    {
        return calendarId;
    }

    public void setCalendarId(long calendarId)
    {
        this.calendarId = calendarId;
    }
}
