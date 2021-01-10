package com.zerodsoft.scheduleweather.activity.editevent.value;

public class ReminderValue
{
    private int month;
    private int week;
    private int day;
    private int hour;
    private int minute;

    public ReminderValue()
    {
    }

    public int getMonth()
    {
        return month;
    }

    public ReminderValue setMonth(int month)
    {
        this.month = month;
        return this;
    }

    public int getWeek()
    {
        return week;
    }

    public ReminderValue setWeek(int week)
    {
        this.week = week;
        return this;
    }

    public int getDay()
    {
        return day;
    }

    public ReminderValue setDay(int day)
    {
        this.day = day;
        return this;
    }

    public int getHour()
    {
        return hour;
    }

    public ReminderValue setHour(int hour)
    {
        this.hour = hour;
        return this;
    }

    public int getMinute()
    {
        return minute;
    }

    public ReminderValue setMinute(int minute)
    {
        this.minute = minute;
        return this;
    }
}
