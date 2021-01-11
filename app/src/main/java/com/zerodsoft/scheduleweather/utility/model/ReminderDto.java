package com.zerodsoft.scheduleweather.utility.model;

public class ReminderDto
{
    private int week;
    private int day;
    private int hour;
    private int minute;

    public ReminderDto(int week, int day, int hour, int minute)
    {
        this.week = week;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public int getWeek()
    {
        return week;
    }

    public ReminderDto setWeek(int week)
    {
        this.week = week;
        return this;
    }

    public int getDay()
    {
        return day;
    }

    public ReminderDto setDay(int day)
    {
        this.day = day;
        return this;
    }

    public int getHour()
    {
        return hour;
    }

    public ReminderDto setHour(int hour)
    {
        this.hour = hour;
        return this;
    }

    public int getMinute()
    {
        return minute;
    }

    public ReminderDto setMinute(int minute)
    {
        this.minute = minute;
        return this;
    }
}
