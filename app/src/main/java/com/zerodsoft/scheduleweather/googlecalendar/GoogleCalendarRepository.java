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
    private MutableLiveData<DataWrapper<List<CustomCalendar>>> eventsLiveData;
    private MutableLiveData<DataWrapper<Calendar>> calendarLiveData;
    private MutableLiveData<DataWrapper<List<CalendarListEntry>>> calendarListLiveData;

    private GoogleCalendar googleCalendar;
    private IGoogleCalendar iGoogleCalendar;

    public GoogleCalendarRepository(Activity activity)
    {
        this.iGoogleCalendar = (IGoogleCalendar) activity;

        eventsLiveData = new MutableLiveData<>();
        calendarLiveData = new MutableLiveData<>();
        calendarListLiveData = new MutableLiveData<>();

        googleCalendar = GoogleCalendar.newInstance(activity);
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

    public void connect(String accountName) throws IOException, GeneralSecurityException
    {
        googleCalendar.connect(accountName);
        iGoogleCalendar.onAccountSelectedState(true);
    }

    public void chooseAccount()
    {
        googleCalendar.requestAccountPicker();
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
                    List<CalendarListEntry> calendarList = googleCalendar.getCalendarList();
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
                        List<Event> events = googleCalendar.getEvents(calendarListEntry.getId());
                        Calendar calendar = googleCalendar.getCalendar(calendarListEntry.getId());

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
                    Calendar calendar = googleCalendar.getCalendar(calendarId);
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
        googleCalendar.disconnect();
    }
}