package com.zerodsoft.scheduleweather.calendar;

import android.content.ContentValues;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.callback.EventCallback;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class CalendarRepository
{
    private MutableLiveData<DataWrapper<ContentValues>> calendarLiveData;
    private MutableLiveData<DataWrapper<List<ContentValues>>> calendarListLiveData;
    private MutableLiveData<DataWrapper<ContentValues>> eventLiveData;
    private MutableLiveData<DataWrapper<List<ContentValues>>> eventListLiveData;
    private MutableLiveData<DataWrapper<List<ContentValues>>> instanceListLiveData;
    private MutableLiveData<DataWrapper<List<ContentValues>>> reminderListLiveData;
    private MutableLiveData<DataWrapper<List<ContentValues>>> attendeeListLiveData;

    private CalendarProvider calendarProvider;
    private Context context;

    public CalendarRepository(Context context)
    {
        this.context = context;
        calendarProvider = CalendarProvider.newInstance(context);

        eventListLiveData = new MutableLiveData<>();
        calendarLiveData = new MutableLiveData<>();
        calendarListLiveData = new MutableLiveData<>();
        eventLiveData = new MutableLiveData<>();
        reminderListLiveData = new MutableLiveData<>();
        attendeeListLiveData = new MutableLiveData<>();
        instanceListLiveData = new MutableLiveData<>();
    }


    public void requestInstances(List<ContentValues> calendarList, long startDate, long endDate, EventCallback<List<CalendarInstance>> callback)
    {
        calendarProvider.getInstances(calendarList, startDate, endDate, callback);
    }

    public MutableLiveData<DataWrapper<ContentValues>> getCalendarLiveData()
    {
        return calendarLiveData;
    }

    public MutableLiveData<DataWrapper<List<ContentValues>>> getCalendarListLiveData()
    {
        return calendarListLiveData;
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
}