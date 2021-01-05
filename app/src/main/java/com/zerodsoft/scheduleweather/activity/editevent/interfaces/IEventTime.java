package com.zerodsoft.scheduleweather.activity.editevent.interfaces;

public interface IEventTime
{
    void onSelectedTime(long dateTime, int dateType);

    long getDateTime(int dateType);
}
