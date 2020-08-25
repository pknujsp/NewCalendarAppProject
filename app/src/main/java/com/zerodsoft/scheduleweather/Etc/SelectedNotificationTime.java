package com.zerodsoft.scheduleweather.Etc;


import com.zerodsoft.scheduleweather.Room.DTO.ScheduleDTO;

import java.util.Calendar;
import java.util.Date;

public class SelectedNotificationTime
{
    private int day;
    private int hour;
    private int minute;
    private int mainType;
    private String resultStr;

    private StringBuilder stringBuilder = new StringBuilder();

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

    public SelectedNotificationTime setMainType(int mainType)
    {
        this.mainType = mainType;
        return this;
    }

    public int getMainType()
    {
        return mainType;
    }

    public SelectedNotificationTime setResultStr()
    {
        if (stringBuilder.length() != 0)
        {
            stringBuilder.delete(0, stringBuilder.length());
        }
        switch (mainType)
        {
            case ScheduleDTO.MAIN_DAY:
                stringBuilder.append(Integer.toString(day)).append(" 일 ");
                stringBuilder.append(Integer.toString(hour)).append(" 시간 ");
                stringBuilder.append(Integer.toString(minute)).append(" 분");
                break;
            case ScheduleDTO.MAIN_MINUTE:
                stringBuilder.append(Integer.toString(minute)).append(" 분");
                break;
            case ScheduleDTO.MAIN_HOUR:
                stringBuilder.append(Integer.toString(hour)).append(" 시간 ");
                stringBuilder.append(Integer.toString(minute)).append(" 분");
                break;
        }
        stringBuilder.append(" 전에 알림");
        resultStr = stringBuilder.toString();
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
            case ScheduleDTO.MAIN_DAY:
                calendar.add(day, Calendar.DATE);
                calendar.add(hour, Calendar.HOUR_OF_DAY);
                calendar.add(minute, Calendar.MINUTE);
                break;
            case ScheduleDTO.MAIN_HOUR:
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
            case ScheduleDTO.MAIN_MINUTE:
                calendar.add(minute, Calendar.MINUTE);
                break;
        }
        return calendar.getTime();
    }
}


