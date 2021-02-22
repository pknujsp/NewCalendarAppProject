package com.zerodsoft.scheduleweather.calendarview.interfaces;

public interface OnEventItemClickListener
{
    void onClicked(long viewBegin, long viewEnd);

    void onClicked(int calendarId, long instanceId, long eventId, long viewBegin, long viewEnd);

    void onClickedOnDialog(int calendarId, long instanceId, long eventId, long viewBegin, long viewEnd);
}

