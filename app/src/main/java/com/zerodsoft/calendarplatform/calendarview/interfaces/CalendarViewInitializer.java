package com.zerodsoft.calendarplatform.calendarview.interfaces;

import android.content.ContentValues;

import com.zerodsoft.calendarplatform.calendar.dto.CalendarInstance;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public interface CalendarViewInitializer
{
    void init(Calendar copiedCalendar, OnEventItemLongClickListener onEventItemLongClickListener, OnEventItemClickListener onEventItemClickListener, IControlEvent iControlEvent, IConnectedCalendars iConnectedCalendars);

    void setInstances(Map<Integer, CalendarInstance> resultMap);

    void setInstances(List<ContentValues> instances);

    void setEventTable();

    void refresh();
}