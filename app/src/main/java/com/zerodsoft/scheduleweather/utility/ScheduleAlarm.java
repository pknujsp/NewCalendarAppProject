package com.zerodsoft.scheduleweather.utility;

import android.content.Context;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

import java.util.Calendar;

public class ScheduleAlarm
{
    private static int DAY;
    private static int HOUR;
    private static int MINUTE;
    private static final Calendar DATE_TIME;
    private static String result;

    private ScheduleAlarm()
    {
    }

    static
    {
        DAY = 0;
        HOUR = 0;
        MINUTE = 0;
        DATE_TIME = Calendar.getInstance(ClockUtil.TIME_ZONE);
    }

    public static void init(ScheduleDTO schedule)
    {
        DAY = schedule.getNotiDay();
        HOUR = schedule.getNotiHour();
        MINUTE = schedule.getNotiMinute();
    }

    public static void init(int day, int hour, int minute)
    {
        ScheduleAlarm.DAY = day;
        ScheduleAlarm.HOUR = hour;
        ScheduleAlarm.MINUTE = minute;
    }

    public static void setDAY(int DAY)
    {
        ScheduleAlarm.DAY = DAY;
    }

    public static void setHOUR(int HOUR)
    {
        ScheduleAlarm.HOUR = HOUR;
    }

    public static void setMINUTE(int MINUTE)
    {
        ScheduleAlarm.MINUTE = MINUTE;
    }

    public static int getDAY()
    {
        return DAY;
    }

    public static int getHOUR()
    {
        return HOUR;
    }

    public static int getMINUTE()
    {
        return MINUTE;
    }

    public static String getResultText(Context context)
    {
        result = "";
        if (ScheduleAlarm.getDAY() != 0)
        {
            result += ScheduleAlarm.getDAY() + context.getString(R.string.notification_type_day) + " ";
        }
        if (ScheduleAlarm.getHOUR() != 0)
        {
            result += ScheduleAlarm.getHOUR() + context.getString(R.string.notification_type_hour) + " ";
        }
        if (ScheduleAlarm.getMINUTE() != 0)
        {
            result += ScheduleAlarm.getMINUTE() + context.getString(R.string.notification_type_minute) + " ";
        }

        return result += context.getString(R.string.notification_before);
    }


    public static void setNotiData(ScheduleDTO schedule)
    {
        if (DAY != 0 || HOUR != 0 || MINUTE != 0)
        {
            DATE_TIME.setTime(schedule.getStartDate());

            DATE_TIME.add(Calendar.DAY_OF_YEAR, -DAY);
            DATE_TIME.add(Calendar.HOUR_OF_DAY, -HOUR);
            DATE_TIME.add(Calendar.MINUTE, -MINUTE);
            schedule.setNotiTime(DATE_TIME.getTime());
        } else
        {
            schedule.setNotiTime(null);
        }
        schedule.setNotiDay(DAY);
        schedule.setNotiHour(HOUR);
        schedule.setNotiMinute(MINUTE);
    }

    public static boolean isEmpty()
    {
        if (DAY == 0 && HOUR == 0 && MINUTE == 0)
        {
            return true;
        } else
        {
            return false;
        }
    }

}
