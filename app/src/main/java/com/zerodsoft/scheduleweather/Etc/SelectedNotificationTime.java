package com.zerodsoft.scheduleweather.Etc;

import com.zerodsoft.scheduleweather.Fragment.NotificationFragment;

public class SelectedNotificationTime
{
    private int day;
    private int hour;
    private int minute;
    private NotificationFragment.MainType mainType;
    private String resultStr;

    public int getDay()
    {
        return day;
    }

    public SelectedNotificationTime setDay(int day)
    {
        this.day = day;
        return this;
    }

    public int getHour()
    {
        return hour;
    }

    public SelectedNotificationTime setHour(int hour)
    {
        this.hour = hour;
        return this;
    }

    public int getMinute()
    {
        return minute;
    }

    public SelectedNotificationTime setMinute(int minute)
    {
        this.minute = minute;
        return this;
    }

    public SelectedNotificationTime setMainType(NotificationFragment.MainType mainType)
    {
        this.mainType = mainType;
        return this;
    }

    public NotificationFragment.MainType getMainType()
    {
        return mainType;
    }

    public SelectedNotificationTime setResultStr(String resultStr)
    {
        this.resultStr = resultStr;
        return this;
    }

    public String getResultStr()
    {
        return resultStr;
    }
}


