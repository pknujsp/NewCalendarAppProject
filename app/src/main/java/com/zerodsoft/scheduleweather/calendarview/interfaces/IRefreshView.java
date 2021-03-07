package com.zerodsoft.scheduleweather.calendarview.interfaces;

import android.os.RemoteException;

import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.callback.EventCallback;
import com.zerodsoft.scheduleweather.calendarview.month.MonthViewPagerAdapter;

import java.util.List;

public interface IRefreshView
{
    public void refreshView();
}
