package com.zerodsoft.scheduleweather.calendarview.interfaces;

public interface OnEventItemClickListener
{
    void onClicked(long start, long end);

    void onClicked(int calendarId, long eventId, long begin, long end);
}

