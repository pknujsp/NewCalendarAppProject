package com.zerodsoft.calendarplatform.calendarview.interfaces;

import com.zerodsoft.calendarplatform.calendar.dto.CalendarInstance;

import java.util.Map;

public interface IControlEvent
{
    Map<Integer, CalendarInstance> getInstances(long begin, long end);
}