package com.zerodsoft.scheduleweather.calendarview.month;

import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventData
{
    private int startIndex;
    private int endIndex;
    private int row;
    private ScheduleDTO schedule;


    public EventData(ScheduleDTO schedule, int startIndex, int endIndex)
    {
        this.schedule = schedule;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public EventData(ScheduleDTO schedule, int startIndex, int endIndex, int row)
    {
        this.schedule = schedule;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.row = row;
    }

    public void setIndex(int startIndex, int endIndex)
    {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public void setRow(int row)
    {
        this.row = row;
    }

    public int getDateLength()
    {
        return endIndex - startIndex + 1;
    }

    public ScheduleDTO getSchedule()
    {
        return schedule;
    }

    public int getStartIndex()
    {
        return startIndex;
    }

    public int getEndIndex()
    {
        return endIndex;
    }

    public int getRow()
    {
        return row;
    }
}