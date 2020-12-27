package com.zerodsoft.scheduleweather.googlecalendar;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;

import java.util.List;

public class CustomCalendar
{
    private final Calendar googleCalendar;
    private final List<Event> events;

    public CustomCalendar(Calendar googleCalendar, List<Event> events)
    {
        this.googleCalendar = googleCalendar;
        this.events = events;
    }

    public Calendar getGoogleCalendar()
    {
        return googleCalendar;
    }

    public List<Event> getEvents()
    {
        return events;
    }
}
