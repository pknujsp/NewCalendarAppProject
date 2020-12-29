package com.zerodsoft.scheduleweather.googlecalendar;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class GoogleCalendarViewModel extends ViewModel
{
    private MutableLiveData<DataWrapper<List<CustomGoogleCalendar>>> eventsLiveData;
    private MutableLiveData<DataWrapper<Calendar>> calendarLiveData;
    private MutableLiveData<DataWrapper<List<CalendarListEntry>>> calendarListLiveData;
    private GoogleCalendarRepository repository;

    public GoogleCalendarViewModel()
    {

    }

    public void init(Activity activity)
    {
        repository = new GoogleCalendarRepository(activity);

        eventsLiveData = repository.getEventsLiveData();
        calendarLiveData = repository.getCalendarLiveData();
        calendarListLiveData = repository.getCalendarListLiveData();
    }

    public MutableLiveData<DataWrapper<List<CustomGoogleCalendar>>> getEventsLiveData()
    {
        return eventsLiveData;
    }

    public MutableLiveData<DataWrapper<List<CalendarListEntry>>> getCalendarListLiveData()
    {
        return calendarListLiveData;
    }

    public MutableLiveData<DataWrapper<Calendar>> getCalendarLiveData()
    {
        return calendarLiveData;
    }

    public void getCalendarList()
    {
        repository.getCalendarList();
    }

    public void getEvents(String calendarId)
    {
        repository.getEvents(calendarId);
    }

    public void getCalendar(String calendarId)
    {
        repository.getCalendar(calendarId);
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
}
