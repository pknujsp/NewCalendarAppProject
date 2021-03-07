package com.zerodsoft.scheduleweather.calendarview.interfaces;

import android.os.RemoteException;

import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;

import java.util.List;

public interface IControlEvent
{
    List<CalendarInstance> getInstances(int viewPosition, long begin, long end);
}