package com.zerodsoft.scheduleweather.calendarview.interfaces;

import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.callback.EventCallback;

import java.util.List;

public interface IControlEvent
{
    void getInstances(int viewPosition, long start, long end, EventCallback<List<CalendarInstance>> callback);
}