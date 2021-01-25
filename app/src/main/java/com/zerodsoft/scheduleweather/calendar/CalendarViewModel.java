package com.zerodsoft.scheduleweather.calendar;

import android.content.ContentValues;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.callback.EventCallback;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;

import java.util.List;

public class CalendarViewModel extends ViewModel
{
    private MutableLiveData<DataWrapper<ContentValues>> calendarLiveData;
    private MutableLiveData<DataWrapper<List<ContentValues>>> calendarListLiveData;
    private MutableLiveData<DataWrapper<List<ContentValues>>> allCalendarListLiveData;
    private MutableLiveData<DataWrapper<ContentValues>> eventLiveData;
    private MutableLiveData<DataWrapper<List<ContentValues>>> eventListLiveData;
    private MutableLiveData<DataWrapper<List<ContentValues>>> instanceListLiveData;
    private MutableLiveData<DataWrapper<List<ContentValues>>> reminderListLiveData;
    private MutableLiveData<DataWrapper<List<ContentValues>>> attendeeListLiveData;
    private Context context;

    private CalendarProvider calendarProvider;

    public CalendarViewModel()
    {

    }

    public void init(Context context)
    {
        this.context = context;
        this.calendarProvider = CalendarProvider.newInstance(context);

        calendarLiveData = new MutableLiveData<>();
        calendarListLiveData = new MutableLiveData<>();
        allCalendarListLiveData = new MutableLiveData<>();
        eventLiveData = new MutableLiveData<>();
        eventListLiveData = new MutableLiveData<>();
        instanceListLiveData = new MutableLiveData<>();
        reminderListLiveData = new MutableLiveData<>();
        attendeeListLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<DataWrapper<ContentValues>> getCalendarLiveData()
    {
        return calendarLiveData;
    }

    public MutableLiveData<DataWrapper<List<ContentValues>>> getCalendarListLiveData()
    {
        return calendarListLiveData;
    }

    public MutableLiveData<DataWrapper<List<ContentValues>>> getAllCalendarListLiveData()
    {
        return allCalendarListLiveData;
    }

    public MutableLiveData<DataWrapper<ContentValues>> getEventLiveData()
    {
        return eventLiveData;
    }

    public MutableLiveData<DataWrapper<List<ContentValues>>> getEventListLiveData()
    {
        return eventListLiveData;
    }

    public MutableLiveData<DataWrapper<List<ContentValues>>> getInstanceListLiveData()
    {
        return instanceListLiveData;
    }

    public MutableLiveData<DataWrapper<List<ContentValues>>> getReminderListLiveData()
    {
        return reminderListLiveData;
    }

    public MutableLiveData<DataWrapper<List<ContentValues>>> getAttendeeListLiveData()
    {
        return attendeeListLiveData;
    }

    public void getEvent(int calendarId, int eventId)
    {
        ContentValues event = calendarProvider.getEvent(calendarId, eventId);
        DataWrapper<ContentValues> dataWrapper = new DataWrapper<>(event);
        eventLiveData.setValue(dataWrapper);
    }

    public void getEvents(int calendarId)
    {
        List<ContentValues> events = calendarProvider.getEvents(calendarId);
        DataWrapper<List<ContentValues>> dataWrapper = new DataWrapper<>(events);
        eventListLiveData.setValue(dataWrapper);
    }

    public void addEvent(ContentValues event)
    {
        calendarProvider.addEvent(event);
    }

    public int deleteEvent(int calendarId, int eventId)
    {
        return calendarProvider.deleteEvent(calendarId, eventId);
    }

    public int deleteEvents(int calendarId, int[] eventIds)
    {
        return calendarProvider.deleteEvents(calendarId, eventIds);
    }

    public int updateEvent(ContentValues event)
    {
        return calendarProvider.updateEvent(event);
    }

    public void getAllCalendars()
    {
        List<ContentValues> calendars = calendarProvider.getAllCalendars();
        DataWrapper<List<ContentValues>> dataWrapper = new DataWrapper<>(calendars);
        allCalendarListLiveData.setValue(dataWrapper);
    }

    public void getCalendars()
    {
        List<ContentValues> calendars = calendarProvider.getCalendars();
        DataWrapper<List<ContentValues>> dataWrapper = new DataWrapper<>(calendars);
        calendarListLiveData.setValue(dataWrapper);
    }

    public void getCalendar(int calendarId)
    {
        ContentValues calendar = calendarProvider.getCalendar(calendarId);
        DataWrapper<ContentValues> dataWrapper = new DataWrapper<>(calendar);
        calendarLiveData.setValue(dataWrapper);
    }

    public void getReminders(int calendarId, int eventId)
    {
        List<ContentValues> reminders = calendarProvider.getReminders(calendarId, eventId);
        DataWrapper<List<ContentValues>> dataWrapper = new DataWrapper<>(reminders);
        reminderListLiveData.setValue(dataWrapper);
    }


    public int updateReminder(ContentValues reminder)
    {
        return calendarProvider.updateReminder(reminder);
    }

    public int deleteReminders(int calendarId, int eventId, int[] reminderIds)
    {
        return calendarProvider.deleteReminders(calendarId, eventId, reminderIds);
    }

    public int deleteAllReminders(int calendarId, int eventId)
    {
        return calendarProvider.deleteAllReminders(calendarId, eventId);
    }

    public int addReminders(List<ContentValues> reminders)
    {
        return calendarProvider.addReminders(reminders);
    }

    public void getInstanceList(List<ContentValues> calendarList, long startDate, long endDate, EventCallback<List<CalendarInstance>> callback)
    {
                calendarProvider.getInstances(calendarList, startDate, endDate, callback);

    }

    public int addAttendees(List<ContentValues> attendeeList)
    {
        return calendarProvider.addAttendees(attendeeList);
    }


    public void getAttendees(int calendarID, int eventId)
    {
        List<ContentValues> attendees = calendarProvider.getAttendees(calendarID, eventId);
        DataWrapper<List<ContentValues>> dataWrapper = new DataWrapper<>(attendees);
        attendeeListLiveData.setValue(dataWrapper);
    }

    public int updateAttendees(List<ContentValues> attendeeList)
    {
        return calendarProvider.updateAttendees(attendeeList);
    }


    public int deleteAllAttendees(int calendarId, int eventId)
    {
        return calendarProvider.deleteAllAttendees(calendarId, eventId);
    }


    public int deleteAttendees(int calendarId, int eventId, int[] attendeeIds)
    {
        return calendarProvider.deleteAttendees(calendarId, eventId, attendeeIds);
    }
}
