package com.zerodsoft.scheduleweather.calendarview.month;

import android.util.SparseArray;

import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

public class ItemLayoutCell
{
    public ScheduleDTO[] rows;

    public ItemLayoutCell()
    {
        rows = new ScheduleDTO[MonthCalendarView.EVENT_COUNT];
    }
}
