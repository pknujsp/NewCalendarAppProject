package com.zerodsoft.scheduleweather.calendarview.interfaces;

import android.content.ContentValues;

public interface ICalendarCheckBox
{
    void onCheckedBox(String key, ContentValues calendar, boolean state);
}
