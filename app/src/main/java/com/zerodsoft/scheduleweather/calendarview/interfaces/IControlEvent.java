package com.zerodsoft.scheduleweather.calendarview.interfaces;

import com.zerodsoft.scheduleweather.calendarview.callback.EventCallback;

import java.util.Date;

public interface IControlEvent
{
    void showEventOnDayDialog(int calendarId, int eventId, String accountName);

    void requestEvent(int viewPosition, Date startDate, Date endDate, EventCallback callback);
}