package com.zerodsoft.scheduleweather.calendar.interfaces;

import android.content.ContentValues;

import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.callback.EventCallback;

import java.util.List;

public interface ICalendarProvider
{
    // event - crud
    public ContentValues getEvent(int calendarId, int eventId);

    public List<ContentValues> getEvents(int calendarId);

    public void addEvent(ContentValues event);

    public int deleteEvent(int calendarId, int eventId);

    public int deleteEvents(int calendarId, int[] eventIds);

    public int updateEvent(ContentValues event);

    // calendar - select
    public List<ContentValues> getAllCalendars();

    public List<ContentValues> getCalendars();

    public ContentValues getCalendar(int calendarId);

    // reminder - crud
    public List<ContentValues> getReminders(int calendarId, int eventId);

    public int updateReminder(ContentValues reminder);

    public int deleteReminders(int calendarId, int eventId, int[] reminderIds);

    public int deleteAllReminders(int calendarId, int eventId);

    public int addReminders(List<ContentValues> reminders);

    // instance - select
    public void getInstances(List<ContentValues> calendarList, long startDate, long endDate, EventCallback<List<CalendarInstance>> callback);

    // attendee - crud
    public int addAttendees(List<ContentValues> attendeeList);

    public List<ContentValues> getAttendees(int calendarId, int eventId);

    public int updateAttendees(List<ContentValues> attendeeList);

    public int deleteAllAttendees(int calendarId, int eventId);

    public int deleteAttendees(int calendarId, int eventId, int[] attendeeIds);
}
