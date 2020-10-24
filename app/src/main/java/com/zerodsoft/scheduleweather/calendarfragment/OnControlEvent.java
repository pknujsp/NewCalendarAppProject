package com.zerodsoft.scheduleweather.calendarfragment;

import androidx.fragment.app.Fragment;

import java.util.Date;

public interface OnControlEvent
{
    void showSchedule(int scheduleId);

    void setToolbarMonth(Date date);

    void requestSchedules(Fragment fragment, int viewPosition, Date startDate, Date endDate);
}