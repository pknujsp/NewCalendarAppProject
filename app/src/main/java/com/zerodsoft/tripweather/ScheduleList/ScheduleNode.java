package com.zerodsoft.tripweather.ScheduleList;

import com.zerodsoft.tripweather.Room.DTO.Schedule;

public class ScheduleNode
{
    private Schedule schedule;
    private int viewType;

    public Schedule getSchedule()
    {
        return schedule;
    }

    public ScheduleNode setSchedule(Schedule schedule)
    {
        this.schedule = schedule;
        return this;
    }

    public int getViewType()
    {
        return viewType;
    }

    public ScheduleNode setViewType(int viewType)
    {
        this.viewType = viewType;
        return this;
    }
}
