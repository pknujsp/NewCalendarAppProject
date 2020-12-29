package com.zerodsoft.scheduleweather.googlecalendar;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.util.List;

public class CustomGoogleCalendar
{
    private final Calendar googleCalendar;
    private final Events events;

    public CustomGoogleCalendar(Calendar googleCalendar, Events events)
    {
        this.googleCalendar = googleCalendar;
        this.events = events;
    }


    public Calendar getGoogleCalendar()
    {
        return googleCalendar;
    }

    public Events getEvents()
    {
        return events;
    }
}
