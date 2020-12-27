package com.zerodsoft.scheduleweather.googlecalendar;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;

import java.util.List;

public class GoogleCalendarViewModel extends ViewModel
{
    private MutableLiveData<DataWrapper<List<CustomCalendar>>> eventsLiveData;
    private MutableLiveData<DataWrapper<Calendar>> calendarLiveData;
    private MutableLiveData<DataWrapper<List<CalendarListEntry>>> calendarListLiveData;
    private GoogleCalendarRepository repository;

    public GoogleCalendarViewModel()
    {
        repository = new GoogleCalendarRepository();

        eventsLiveData = repository.getEventsLiveData();
        calendarLiveData = repository.getCalendarLiveData();
        calendarListLiveData = repository.getCalendarListLiveData();
    }

    public MutableLiveData<DataWrapper<List<CustomCalendar>>> getEventsLiveData()
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
}
