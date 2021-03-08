package com.zerodsoft.scheduleweather.calendarview.interfaces;

import android.content.ContentValues;

import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;

import java.util.List;
import java.util.Map;

public interface IEvent
{
    void setInstances(Map<Integer, CalendarInstance> resultMap);

    void setEventTable();
}
