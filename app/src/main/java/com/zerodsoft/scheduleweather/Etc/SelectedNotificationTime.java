package com.zerodsoft.scheduleweather.Etc;

import com.zerodsoft.scheduleweather.Fragment.NotificationFragment;

import java.util.Calendar;
import java.util.Date;

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

    public Date getTime()
    {
        Calendar originalCalendar = Calendar.getInstance();
        Calendar calendar = (Calendar) originalCalendar.clone();

        switch (mainType)
        {
            case DAY:
                calendar.add(day, Calendar.DATE);
                calendar.add(hour, Calendar.HOUR_OF_DAY);
                calendar.add(minute, Calendar.MINUTE);
                break;
            case HOUR:
                int quotient = (int) Math.floor(hour / 24);
                int remainder = hour % 24;

                if (quotient >= 1)
                {
                    calendar.add(quotient, Calendar.DATE);
                }
                if (remainder != 0)
                {
                    calendar.add(remainder, Calendar.HOUR_OF_DAY);
                }
                break;
            case MINUTE:
                calendar.add(minute, Calendar.MINUTE);
                break;
        }
        return calendar.getTime();
    }
}


