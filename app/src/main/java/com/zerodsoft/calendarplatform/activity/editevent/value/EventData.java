package com.zerodsoft.calendarplatform.activity.editevent.value;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

public class EventData
{
    private final ContentValues EVENT = new ContentValues();
    private final List<ContentValues> REMINDERS = new ArrayList<>();
    private final List<ContentValues> ATTENDEES = new ArrayList<>();

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

}
