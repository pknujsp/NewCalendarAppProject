package com.zerodsoft.scheduleweather.googlecalendar;

import android.app.Activity;
import android.content.ContentValues;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.zerodsoft.scheduleweather.googlecalendar.dto.CalendarDto;
import com.zerodsoft.scheduleweather.googlecalendar.dto.EventDto;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class CalendarViewModel extends ViewModel
{
    private MutableLiveData<DataWrapper<List<EventDto>>> eventsLiveData;
    private MutableLiveData<DataWrapper<CalendarDto>> calendarLiveData;
    private MutableLiveData<DataWrapper<List<CalendarDto>>> calendarListLiveData;
    private MutableLiveData<DataWrapper<ContentValues>> eventLiveData;
    private CalendarRepository repository;

    public CalendarViewModel()
    {

    }

    public void init(Activity activity)
    {
        repository = new CalendarRepository(activity);

        eventsLiveData = repository.getEventsLiveData();
        calendarLiveData = repository.getCalendarLiveData();
        calendarListLiveData = repository.getCalendarListLiveData();
        eventLiveData = repository.getEventLiveData();
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
}
