package com.zerodsoft.scheduleweather.activity.editevent;

public interface IEventTime
{
    void onSelectedTime(long dateTime, int dateType);

    long getDateTime(int dateType);
}
