package com.zerodsoft.scheduleweather.calendarview.month;

import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventData
{
    public static final int BEFORE_AFTER = 0;
    public static final int BEFORE_THISWEEK = 1;
    public static final int THISWEEK_AFTER = 2;
    public static final int THISWEEK_THISWEEK = 3;

    private int startIndex;
    private int endIndex;
    private int dateLength;
    private int row;
    private ScheduleDTO schedule;

    public EventData(ScheduleDTO schedule, int row)
    {
        this.schedule = schedule;
        this.row = row;
    }

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


    public int getDateLength()
    {
        return dateLength;
    }

    public boolean isDaySchedule()
    {
        return endIndex == startIndex;
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

    public EventData setRow(int row)
    {
        this.row = row;
        return this;
    }

    public EventData setDateLength(int dateLength)
    {
        this.dateLength = dateLength;
        return this;
    }

    public EventData setStartIndex(int startIndex)
    {
        this.startIndex = startIndex;
        return this;
    }

    public EventData setEndIndex(int endIndex)
    {
        this.endIndex = endIndex;
        return this;
    }
}