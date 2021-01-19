package com.zerodsoft.scheduleweather.calendarview.interfaces;

import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.callback.EventCallback;

import java.util.Date;
import java.util.List;

public interface IControlEvent
{
    void showEventOnDayDialog(int calendarId, int eventId, String accountName);

    void requestInstances(int viewPosition, Date startDate, Date endDate, EventCallback<List<CalendarInstance>> callback);

    void requestEvent(int calendarId, int eventId);

}