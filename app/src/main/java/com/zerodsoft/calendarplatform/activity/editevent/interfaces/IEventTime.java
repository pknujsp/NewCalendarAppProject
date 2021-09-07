package com.zerodsoft.calendarplatform.activity.editevent.interfaces;

public interface IEventTime
{
    void onSelectedTime(long dateTime, int dateType);

    long getDateTime(int dateType);
}
