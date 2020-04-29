package com.zerodsoft.tripweather.ScheduleList;

import com.zerodsoft.tripweather.Room.DTO.Schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ScheduleHeader
{
    private Date date;
    private ArrayList<ScheduleNode> schedules;

    public ScheduleHeader setDate(Date date)
    {
        this.date = date;
        return this;
    }

    public Date getDate()
    {
        return date;
    }

    public ScheduleHeader setSchedules(ArrayList<ScheduleNode> schedules)
    {
        this.schedules = schedules;
        return this;
    }

    public ArrayList<ScheduleNode> getSchedules()
    {
        return schedules;
    }
}
