package com.zerodsoft.scheduleweather.calendarview.interfaces;

import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.callback.EventCallback;

import java.util.List;

public interface IControlEvent
{
    void showEventOnDayDialog(int calendarId, int eventId, String accountName);

    void requestInstances(int viewPosition, long start, long end, EventCallback<List<CalendarInstance>> callback);

    void requestEvent(int calendarId, int eventId);

}