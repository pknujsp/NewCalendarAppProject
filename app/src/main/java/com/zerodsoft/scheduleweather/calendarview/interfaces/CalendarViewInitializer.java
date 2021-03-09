package com.zerodsoft.scheduleweather.calendarview.interfaces;

import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;

import java.util.Calendar;
import java.util.Map;

public interface CalendarViewInitializer
{
    void init(Calendar copiedCalendar, OnEventItemClickListener onEventItemClickListener, IControlEvent iControlEvent, IConnectedCalendars iConnectedCalendars);

    void setInstances(Map<Integer, CalendarInstance> resultMap);

    void setEventTable();

    void refresh();
}

