package com.zerodsoft.scheduleweather.calendarview.interfaces;

import android.os.RemoteException;

import java.util.Date;

public interface CalendarDateOnClickListener
{
    void onClickedDate(Date date);
    void onClickedMonth(Date date);
}
