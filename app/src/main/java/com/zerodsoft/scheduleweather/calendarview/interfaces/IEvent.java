package com.zerodsoft.scheduleweather.calendarview.interfaces;

import android.content.ContentValues;

import java.util.List;

public interface IEvent
{
    void setInstances(List<ContentValues> instances);

    void setEventTable();
}
