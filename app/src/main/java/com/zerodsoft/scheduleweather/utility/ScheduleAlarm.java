package com.zerodsoft.scheduleweather.utility;

import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

import java.util.Calendar;

public class ScheduleAlarm
{
    private static int day = 0;
    private static int hour = 0;
    private static int minute = 0;

    private static final StringBuilder stringBuilder = new StringBuilder();
    private static final Calendar dateTime = Calendar.getInstance();


    public static void init(ScheduleDTO schedule)
    {
        ScheduleAlarm.day = schedule.getNotiDay();
        ScheduleAlarm.hour = schedule.getNotiHour();
        ScheduleAlarm.minute = schedule.getNotiMinute();
    }

    public static void init(int day, int hour, int minute)
    {
        ScheduleAlarm.day = day;
        ScheduleAlarm.hour = hour;
        ScheduleAlarm.minute = minute;
    }

    public static void setDay(int day)
    {
        ScheduleAlarm.day = day;
    }

    public static void setHour(int hour)
    {
        ScheduleAlarm.hour = hour;
    }

    public static void setMinute(int minute)
    {
        ScheduleAlarm.minute = minute;
    }

    public static int getDay()
    {
        return day;
    }

    public static int getHour()
    {
        return hour;
    }

    public static int getMinute()
    {
        return minute;
    }

    public static String getResultText()
    {
        if (stringBuilder.length() != 0)
        {
            stringBuilder.delete(0, stringBuilder.length());
        }

        if (day != 0 && hour != 0 && minute != 0)
        {
            if (day != 0)
            {
                stringBuilder.append(day).append("일 ");
            }
            if (hour != 0)
            {
                stringBuilder.append(hour).append("시간 ");
            }
            if (minute != 0)
            {
                stringBuilder.append(minute).append("분 ");
            }

            stringBuilder.append("전에 알림");
        } else
        {
            stringBuilder.append("알림 없음");
        }
        return stringBuilder.toString();
    }

    public static void setNotiData(ScheduleDTO schedule)
    {
        if (day != 0 && hour != 0 && minute != 0)
        {
            dateTime.setTime(schedule.getStartDate());

            dateTime.add(Calendar.DAY_OF_YEAR, -day);
            dateTime.add(Calendar.HOUR_OF_DAY, -hour);
            dateTime.add(Calendar.MINUTE, -minute);
            schedule.setNotiTime(dateTime.getTime());
        } else
        {
            schedule.setNotiTime(null);
        }
        schedule.setNotiDay(day);
        schedule.setNotiHour(hour);
        schedule.setNotiMinute(minute);
    }

    public static boolean isEmpty()
    {
        if (day != 0 && hour != 0 && minute != 0)
        {
            return true;
        } else
        {
            return false;
        }
    }

}
