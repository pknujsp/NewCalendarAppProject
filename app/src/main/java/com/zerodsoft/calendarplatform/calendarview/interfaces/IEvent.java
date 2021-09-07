package com.zerodsoft.calendarplatform.calendarview.interfaces;

import com.zerodsoft.calendarplatform.calendar.dto.CalendarInstance;

import java.util.Map;

public interface IEvent
{
    void setInstances(Map<Integer, CalendarInstance> resultMap);

    void setEventTable();
}
