package com.zerodsoft.scheduleweather.calendar;

import android.content.ContentValues;
import android.content.Context;
import android.telephony.IccOpenLogicalChannelResponse;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
    private MutableLiveData<DataWrapper<ContentValues>> instanceLiveData;
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
        instanceLiveData = new MutableLiveData<>();
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

    public MutableLiveData<DataWrapper<ContentValues>> getInstanceLiveData()
    {
        return instanceLiveData;
    }

    public void getEvent(int calendarId, long eventId)
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

    public long addEvent(ContentValues event)
    {
        return calendarProvider.addEvent(event);
    }

    public int deleteEvent(int calendarId, long eventId)
    {
        return calendarProvider.deleteEvent(calendarId, eventId);
    }

    public int deleteEvents(int calendarId, long[] eventIds)
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

    public void getReminders(int calendarId, long eventId)
    {
        List<ContentValues> reminders = calendarProvider.getReminders(calendarId, eventId);
        DataWrapper<List<ContentValues>> dataWrapper = new DataWrapper<>(reminders);
        reminderListLiveData.setValue(dataWrapper);
    }


    public int updateReminder(ContentValues reminder)
    {
        return calendarProvider.updateReminder(reminder);
    }

    public int deleteReminders(int calendarId, long eventId, long[] reminderIds)
    {
        return calendarProvider.deleteReminders(calendarId, eventId, reminderIds);
    }

    public int deleteAllReminders(int calendarId, long eventId)
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

    public void getInstance(int calendarId, long instanceId, long begin, long end)
    {
        ContentValues instance = calendarProvider.getInstance(calendarId, instanceId, begin, end);
        DataWrapper<ContentValues> dataWrapper = new DataWrapper<>(instance);
        instanceLiveData.setValue(dataWrapper);
    }

    public long updateAllFutureInstances(ContentValues modifiedInstance, ContentValues previousInstance)
    {
        return calendarProvider.updateAllFutureInstances(modifiedInstance, previousInstance);
    }

    public long updateOneInstance(ContentValues modifiedInstance, ContentValues previousInstance)
    {
        return calendarProvider.updateOneInstance(modifiedInstance, previousInstance);
    }

    public int deleteInstance(long begin, long eventId)
    {
        return calendarProvider.deleteInstance(begin, eventId);
    }


    public int addAttendees(List<ContentValues> attendeeList)
    {
        return calendarProvider.addAttendees(attendeeList);
    }


    public void getAttendees(int calendarID, long eventId)
    {
        List<ContentValues> attendees = calendarProvider.getAttendees(calendarID, eventId);
        DataWrapper<List<ContentValues>> dataWrapper = new DataWrapper<>(attendees);
        attendeeListLiveData.setValue(dataWrapper);
    }

    public int updateAttendees(List<ContentValues> attendeeList)
    {
        return calendarProvider.updateAttendees(attendeeList);
    }


    public int deleteAllAttendees(int calendarId, long eventId)
    {
        return calendarProvider.deleteAllAttendees(calendarId, eventId);
    }


    public int deleteAttendees(int calendarId, long eventId, long[] attendeeIds)
    {
        return calendarProvider.deleteAttendees(calendarId, eventId, attendeeIds);
    }

    // sync
    public void syncCalendars()
    {
        calendarProvider.syncCalendars();
    }

    public ContentValues getRecurrence(int calendarId, long eventId)
    {
        return calendarProvider.getRecurrence(calendarId, eventId);
    }

    //color
    public int getCalendarColor(String accountName, String accountType)
    {
        return calendarProvider.getCalendarColor(accountName, accountType);
    }

    public int updateCalendarColor(String accountName, String accountType, int color, int colorKey)
    {
        return calendarProvider.updateCalendarColor(accountName, accountType, color, colorKey);
    }
}
