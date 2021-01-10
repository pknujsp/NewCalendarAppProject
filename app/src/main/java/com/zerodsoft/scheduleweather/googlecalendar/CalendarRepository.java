package com.zerodsoft.scheduleweather.googlecalendar;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.zerodsoft.scheduleweather.googlecalendar.dto.CalendarDto;
import com.zerodsoft.scheduleweather.googlecalendar.dto.EventDto;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CalendarRepository
{
    private MutableLiveData<DataWrapper<List<EventDto>>> eventsLiveData;
    private MutableLiveData<DataWrapper<CalendarDto>> calendarLiveData;
    private MutableLiveData<DataWrapper<List<CalendarDto>>> calendarListLiveData;
    private MutableLiveData<DataWrapper<ContentValues>> eventLiveData;
    private MutableLiveData<DataWrapper<List<ContentValues>>> reminderLiveData;

    private GoogleCalendarApi googleCalendarApi;
    private CalendarProvider calendarProvider;
    private IGoogleCalendar iGoogleCalendar;
    private Context context;

    public CalendarRepository(Activity activity)
    {
        // this.iGoogleCalendar = (IGoogleCalendar) activity;
        this.context = activity.getApplicationContext();

        eventsLiveData = new MutableLiveData<>();
        calendarLiveData = new MutableLiveData<>();
        calendarListLiveData = new MutableLiveData<>();
        eventLiveData = new MutableLiveData<>();
        reminderLiveData = new MutableLiveData<>();

        googleCalendarApi = GoogleCalendarApi.newInstance(activity);
        calendarProvider = CalendarProvider.newInstance(activity.getApplicationContext());
    }

    public MutableLiveData<DataWrapper<CalendarDto>> getCalendarLiveData()
    {
        return calendarLiveData;
    }

    public MutableLiveData<DataWrapper<List<CalendarDto>>> getCalendarListLiveData()
    {
        return calendarListLiveData;
    }

    public MutableLiveData<DataWrapper<List<EventDto>>> getEventsLiveData()
    {
        return eventsLiveData;
    }

    public MutableLiveData<DataWrapper<List<ContentValues>>> getReminderLiveData()
    {
        return reminderLiveData;
    }

    public void connect(String accountName) throws IOException, GeneralSecurityException
    {
        googleCalendarApi.connect(accountName);
        iGoogleCalendar.onAccountSelectedState(true);
    }

    public void disconnect()
    {
        googleCalendarApi.disconnect();
    }

    public void chooseAccount()
    {
        googleCalendarApi.requestAccountPicker();
    }


    public void getEvents()
    {
        // sharedpreferences에서 선택된 캘린더를 가져옴
    }

    public void getAllCalendars()
    {
        /*
        구글 캘린더 api 이용법
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
         */
        DataWrapper<List<CalendarDto>> dataWrapper = null;

        try
        {
            List<CalendarDto> calendarList = calendarProvider.getAllCalendars();
            dataWrapper = new DataWrapper<>(calendarList);
        } catch (Exception e)
        {
            dataWrapper = new DataWrapper<>(e);
        }
        calendarListLiveData.setValue(dataWrapper);
    }

    public void getAllEvents()
    {
        /*
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
                        Events events = googleCalendarApi.getEvents(calendarListEntry.getId());
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
         */
        DataWrapper<List<EventDto>> dataWrapper = null;

        try
        {
            List<EventDto> eventsList = new ArrayList<>();
            List<CalendarDto> calendarsList = calendarListLiveData.getValue().getData();
            for (CalendarDto calendar : calendarsList)
            {
                EventDto eventDto = new EventDto();
                eventsList.add(eventDto);
            }

            dataWrapper = new DataWrapper<>(eventsList);
        } catch (Exception e)
        {
            dataWrapper = new DataWrapper<>(e);
        }
        eventsLiveData.setValue(dataWrapper);
    }

    public void getCalendar(String calendarId)
    {
        /*
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

         */
    }

    public void getEvent(int calendarId, int eventId, String accountName)
    {
        eventLiveData.setValue(new DataWrapper<>(calendarProvider.getEvent(calendarId, eventId, accountName)));
    }

    public MutableLiveData<DataWrapper<ContentValues>> getEventLiveData()
    {
        return eventLiveData;
    }

    public void getReminders(long eventId)
    {
        reminderLiveData.setValue(new DataWrapper<>(calendarProvider.getReminder(eventId)));
    }

    public void addEvent(ContentValues event)
    {
        // calendarProvider.add(event);
    }

    public void modifyEvent(ContentValues event)
    {
        calendarProvider.modifyEvent(event);
    }

    public List<ContentValues> getCalendars()
    {
        return calendarProvider.getCalendars();
    }
}