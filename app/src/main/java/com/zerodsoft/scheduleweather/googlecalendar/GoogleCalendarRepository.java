package com.zerodsoft.scheduleweather.googlecalendar;

import androidx.lifecycle.MutableLiveData;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GoogleCalendarRepository
{
    private MutableLiveData<DataWrapper<List<CustomCalendar>>> eventsLiveData;
    private MutableLiveData<DataWrapper<Calendar>> calendarLiveData;
    private MutableLiveData<DataWrapper<List<CalendarListEntry>>> calendarListLiveData;

    public GoogleCalendarRepository()
    {
        eventsLiveData = new MutableLiveData<>();
        calendarLiveData = new MutableLiveData<>();
        calendarListLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<DataWrapper<Calendar>> getCalendarLiveData()
    {
        return calendarLiveData;
    }

    public MutableLiveData<DataWrapper<List<CalendarListEntry>>> getCalendarListLiveData()
    {
        return calendarListLiveData;
    }

    public MutableLiveData<DataWrapper<List<CustomCalendar>>> getEventsLiveData()
    {
        return eventsLiveData;
    }

    public void getCalendarList()
    {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                DataWrapper<List<CalendarListEntry>> dataWrapper = null;
                try
                {
                    List<CalendarListEntry> calendarList = GoogleCalendar.getCalendarList();
                    dataWrapper = new DataWrapper<>(calendarList);
                } catch (IOException e)
                {
                    dataWrapper = new DataWrapper<>(e);
                }
                calendarListLiveData.postValue(dataWrapper);
            }
        });
    }

    public void getEvents(String calendarId)
    {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                DataWrapper<List<CustomCalendar>> dataWrapper = null;
                try
                {
                    List<CalendarListEntry> calendarList = calendarListLiveData.getValue().getData();
                    List<CustomCalendar> customCalendarList = new ArrayList<>();

                    for (CalendarListEntry calendarListEntry : calendarList)
                    {
                        List<Event> events = GoogleCalendar.getEvents(calendarListEntry.getId());
                        Calendar calendar = GoogleCalendar.getCalendar(calendarListEntry.getId());

                        customCalendarList.add(new CustomCalendar(calendar, events));
                    }

                    dataWrapper = new DataWrapper<>(customCalendarList);
                } catch (Exception e)
                {
                    dataWrapper = new DataWrapper<>(e);
                }
                eventsLiveData.postValue(dataWrapper);
            }
        });
    }

    public void getCalendar(String calendarId)
    {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                DataWrapper<Calendar> dataWrapper = null;
                try
                {
                    Calendar calendar = GoogleCalendar.getCalendar(calendarId);
                    dataWrapper = new DataWrapper<>(calendar);
                } catch (IOException e)
                {
                    dataWrapper = new DataWrapper<>(e);
                }
                calendarLiveData.postValue(dataWrapper);
            }
        });
    }
}