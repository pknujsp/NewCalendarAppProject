package com.zerodsoft.tripweather.ScheduleList;

import com.zerodsoft.tripweather.Room.DTO.Schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ScheduleHeader
{
    private Date date;
    private ArrayList<ScheduleNode> schedules;
    private int viewType;

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

    public void addSchedule(ScheduleNode node)
    {
        schedules.add(node);
    }

    public ScheduleNode getScheduleNode(int index)
    {
        return schedules.get(index);
    }

    public ScheduleHeader setViewType(int viewType)
    {
        this.viewType = viewType;
        return this;
    }

    public int getViewType()
    {
        return viewType;
    }
}
