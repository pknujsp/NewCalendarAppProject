package com.zerodsoft.scheduleweather.calendar;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zerodsoft.scheduleweather.calendar.dto.CalendarDto;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendar.dto.EventDto;
import com.zerodsoft.scheduleweather.calendarview.callback.EventCallback;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;

public class CalendarViewModel extends ViewModel
{
    private MutableLiveData<DataWrapper<List<EventDto>>> eventsLiveData;
    private MutableLiveData<DataWrapper<CalendarDto>> calendarLiveData;
    private MutableLiveData<DataWrapper<List<CalendarDto>>> calendarListLiveData;
    private MutableLiveData<DataWrapper<ContentValues>> eventLiveData;
    private MutableLiveData<DataWrapper<List<ContentValues>>> reminderLiveData;
    private CalendarRepository repository;

    public CalendarViewModel()
    {

    }

    public void init(Context context)
    {
        repository = new CalendarRepository(context);
        eventsLiveData = repository.getEventsLiveData();
        calendarLiveData = repository.getCalendarLiveData();
        calendarListLiveData = repository.getCalendarListLiveData();
        eventLiveData = repository.getEventLiveData();
        reminderLiveData = repository.getReminderLiveData();
    }

    public MutableLiveData<DataWrapper<List<EventDto>>> getEventsLiveData()
    {
        return eventsLiveData;
    }

    public MutableLiveData<DataWrapper<CalendarDto>> getCalendarLiveData()
    {
        return calendarLiveData;
    }

    public MutableLiveData<DataWrapper<List<CalendarDto>>> getCalendarListLiveData()
    {
        return calendarListLiveData;
    }

    public MutableLiveData<DataWrapper<ContentValues>> getEventLiveData()
    {
        return eventLiveData;
    }

    public void getCalendarList()
    {
        repository.getAllCalendars();
    }

    public void getEvents()
    {
        repository.getAllEvents();
    }


    public void connect(String accountName) throws IOException, GeneralSecurityException
    {
        repository.connect(accountName);
    }

    public void requestAccountPicker()
    {
        repository.chooseAccount();
    }

    public void disconnect()
    {
        repository.disconnect();
    }

    public void getEvent(int calendarId, int eventId, String accountName)
    {
        repository.getEvent(calendarId, eventId, accountName);
    }

    public void getReminders(long eventId)
    {
        repository.getReminders(eventId);
    }

    public MutableLiveData<DataWrapper<List<ContentValues>>> getReminderLiveData()
    {
        return reminderLiveData;
    }

    public void addEvent(ContentValues event)
    {
        repository.addEvent(event);
    }

    public void modifyEvent(ContentValues event)
    {
        repository.modifyEvent(event);
    }

    public List<ContentValues> getCalendars()
    {
        return repository.getCalendars();
    }

    public void requestInstances(List<ContentValues> calendarList, long startDate, long endDate, EventCallback<List<CalendarInstance>> callback)
    {
        repository.requestInstances(calendarList, startDate, endDate, callback);
    }
}
