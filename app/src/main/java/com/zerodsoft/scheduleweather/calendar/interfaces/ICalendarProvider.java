package com.zerodsoft.scheduleweather.calendar.interfaces;

import android.content.ContentValues;

import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.callback.EventCallback;

import java.util.List;

public interface ICalendarProvider
{
    // event - crud
    public ContentValues getEvent(int calendarId, long eventId);

    public List<ContentValues> getEvents(int calendarId);

    public long addEvent(ContentValues event);

    public int deleteEvent(int calendarId, long eventId);

    public int deleteEvents(int calendarId, long[] eventIds);

    public int updateEvent(ContentValues event);

    // calendar - select
    public List<ContentValues> getAllCalendars();

    public List<ContentValues> getCalendars();

    public ContentValues getCalendar(int calendarId);

    // reminder - crud
    public List<ContentValues> getReminders(int calendarId, long eventId);

    public int updateReminder(ContentValues reminder);

    public int deleteReminders(int calendarId, long eventId, long[] reminderIds);

    public int deleteAllReminders(int calendarId, long eventId);

    public int addReminders(List<ContentValues> reminders);

    // instance - read, update, delete
    public void getInstances(List<ContentValues> calendarList, long startDate, long endDate, EventCallback<List<CalendarInstance>> callback);

    public ContentValues getInstance(int calendarId, long instanceId, long begin, long end);

    public long updateAllFutureInstances(ContentValues modifiedInstance, ContentValues previousInstance);

    public long updateOneInstance(ContentValues modifiedInstance, ContentValues previousInstance);

    public int deleteInstance(long begin, long end, long instanceId);

    // attendee - crud
    public int addAttendees(List<ContentValues> attendeeList);

    public List<ContentValues> getAttendees(int calendarId, long eventId);

    public int updateAttendees(List<ContentValues> attendeeList);

    public int deleteAllAttendees(int calendarId, long eventId);

    public int deleteAttendees(int calendarId, long eventId, long[] attendeeIds);

    // recurrence
    public ContentValues getRecurrence(int calendarId, long eventId);
}