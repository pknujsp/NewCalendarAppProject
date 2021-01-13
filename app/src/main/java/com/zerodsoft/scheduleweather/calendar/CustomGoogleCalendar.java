package com.zerodsoft.scheduleweather.calendar;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Events;

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
