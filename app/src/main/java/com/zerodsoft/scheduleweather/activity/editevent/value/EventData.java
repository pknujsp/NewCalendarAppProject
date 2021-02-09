package com.zerodsoft.scheduleweather.activity.editevent.value;

import android.content.ContentValues;
import android.provider.CalendarContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventData
{
    private final ContentValues EVENT = new ContentValues();
    private final List<ContentValues> REMINDERS = new ArrayList<>();
    private final List<ContentValues> ATTENDEES = new ArrayList<>();
    private final Calendar start = Calendar.getInstance();
    private final Calendar end = Calendar.getInstance();


    public EventData()
    {
    }

    public ContentValues getEVENT()
    {
        return EVENT;
    }

    public List<ContentValues> getATTENDEES()
    {
        return ATTENDEES;
    }

    public List<ContentValues> getREMINDERS()
    {
        return REMINDERS;
    }

    public Calendar getStart()
    {
        return (Calendar) start.clone();
    }

    public Calendar getEnd()
    {
        return (Calendar) end.clone();
    }

    public void setStart(long time)
    {
        this.start.setTimeInMillis(time);
        EVENT.put(CalendarContract.Events.DTSTART, time);
    }

    public void setEnd(long time)
    {
        this.end.setTimeInMillis(time);
        EVENT.put(CalendarContract.Events.DTEND, time);
    }
}
