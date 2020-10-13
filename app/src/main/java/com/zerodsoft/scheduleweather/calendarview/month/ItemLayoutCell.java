package com.zerodsoft.scheduleweather.calendarview.month;

import android.util.SparseArray;

import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

public class ItemLayoutCell
{
    public SparseArray<ScheduleDTO> rows;

    public ItemLayoutCell()
    {
        rows = new SparseArray<>(MonthCalendarItemView.EVENT_COUNT);
        for (int i = 0; i < MonthCalendarItemView.EVENT_COUNT; i++)
        {
            rows.put(i, new ScheduleDTO());
        }
    }
}
