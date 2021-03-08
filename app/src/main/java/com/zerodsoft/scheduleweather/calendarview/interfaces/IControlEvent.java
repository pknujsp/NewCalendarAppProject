package com.zerodsoft.scheduleweather.calendarview.interfaces;

import android.os.RemoteException;

import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;

import java.util.List;
import java.util.Map;

public interface IControlEvent
{
    Map<Integer, CalendarInstance> getInstances(long begin, long end);
}