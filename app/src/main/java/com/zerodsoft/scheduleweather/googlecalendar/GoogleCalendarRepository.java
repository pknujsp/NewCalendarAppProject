package com.zerodsoft.scheduleweather.googlecalendar;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GoogleCalendarRepository
{
    private MutableLiveData<DataWrapper<List<CustomGoogleCalendar>>> eventsLiveData;
    private MutableLiveData<DataWrapper<Calendar>> calendarLiveData;
    private MutableLiveData<DataWrapper<List<CalendarListEntry>>> calendarListLiveData;

    private GoogleCalendarApi googleCalendarApi;
    private IGoogleCalendar iGoogleCalendar;

    public GoogleCalendarRepository(Activity activity)
    {
        this.iGoogleCalendar = (IGoogleCalendar) activity;

        eventsLiveData = new MutableLiveData<>();
        calendarLiveData = new MutableLiveData<>();
        calendarListLiveData = new MutableLiveData<>();

        googleCalendarApi = GoogleCalendarApi.newInstance(activity);
    }

    public MutableLiveData<DataWrapper<Calendar>> getCalendarLiveData()
    {
        return calendarLiveData;
    }

    public MutableLiveData<DataWrapper<List<CalendarListEntry>>> getCalendarListLiveData()
    {
        return calendarListLiveData;
    }

    public MutableLiveData<DataWrapper<List<CustomGoogleCalendar>>> getEventsLiveData()
    {
        return eventsLiveData;
    }

    public void connect(String accountName) throws IOException, GeneralSecurityException
    {
        googleCalendarApi.connect(accountName);
        iGoogleCalendar.onAccountSelectedState(true);
    }

    public void chooseAccount()
    {
        googleCalendarApi.requestAccountPicker();
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
                    List<CalendarListEntry> calendarList = googleCalendarApi.getCalendarList();
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
                DataWrapper<List<CustomGoogleCalendar>> dataWrapper = null;
                try
                {
                    List<CalendarListEntry> calendarList = calendarListLiveData.getValue().getData();
                    List<CustomGoogleCalendar> customGoogleCalendarList = new ArrayList<>();

                    for (CalendarListEntry calendarListEntry : calendarList)
                    {
                        List<Event> events = googleCalendarApi.getEvents(calendarListEntry.getId());
                        Calendar calendar = googleCalendarApi.getCalendar(calendarListEntry.getId());

                        customGoogleCalendarList.add(new CustomGoogleCalendar(calendar, events));
                    }

                    dataWrapper = new DataWrapper<>(customGoogleCalendarList);
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
                    Calendar calendar = googleCalendarApi.getCalendar(calendarId);
                    dataWrapper = new DataWrapper<>(calendar);
                } catch (IOException e)
                {
                    dataWrapper = new DataWrapper<>(e);
                }
                calendarLiveData.postValue(dataWrapper);
            }
        });
    }

    public void disconnect()
    {
        googleCalendarApi.disconnect();
    }
}