package com.zerodsoft.scheduleweather.utility;

import android.provider.CalendarContract;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ClockUtil
{
    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("Asia/Seoul");
    public static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");

    public static final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd", Locale.KOREAN);
    public static final SimpleDateFormat HH = new SimpleDateFormat("HH", Locale.KOREAN);
    public static final SimpleDateFormat H = new SimpleDateFormat("H", Locale.KOREAN);
    public static final SimpleDateFormat MdE_FORMAT = new SimpleDateFormat("M/d E", Locale.KOREAN);
    public static final SimpleDateFormat DATE_FORMAT_NOT_ALLDAY = new SimpleDateFormat("yyyy년 M월 d일 E a h시 m분", Locale.KOREAN);
    public static final SimpleDateFormat YYYY_M_D_E = new SimpleDateFormat("yyyy년 M월 d일 E", Locale.KOREAN);
    public static final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREAN);
    public static final SimpleDateFormat D_E = new SimpleDateFormat("d E", Locale.KOREAN);
    public static final SimpleDateFormat D = new SimpleDateFormat("d", Locale.KOREAN);
    public static final SimpleDateFormat YEAR_MONTH_FORMAT = new SimpleDateFormat("yyyy/MM", Locale.KOREAN);
    public static final SimpleDateFormat E = new SimpleDateFormat("E", Locale.KOREAN);
    public static final SimpleDateFormat M_D = new SimpleDateFormat("M월 d일", Locale.KOREAN);
    public static final SimpleDateFormat HOURS_12 = new SimpleDateFormat("a h:mm", Locale.KOREAN);
    public static final SimpleDateFormat HOURS_24 = new SimpleDateFormat("H:mm", Locale.KOREAN);
    public static final SimpleDateFormat yyyyMd = new SimpleDateFormat("yyyy/M/d", Locale.KOREAN);

    public static final int MONTH = 0;
    public static final int WEEK = 1;
    public static final int DAY = 2;

    private ClockUtil()
    {
    }

    public static boolean areSameDate(long dt1, long dt2)
    {
        GregorianCalendar dt1Calendar = new GregorianCalendar();
        dt1Calendar.setTimeInMillis(dt1);
        GregorianCalendar dt2Calendar = new GregorianCalendar();
        dt2Calendar.setTimeInMillis(dt2);

        if (dt1Calendar.get(Calendar.YEAR) == dt2Calendar.get(Calendar.YEAR) &&
                dt1Calendar.get(Calendar.MONTH) == dt2Calendar.get(Calendar.MONTH) &&
                dt1Calendar.get(Calendar.DAY_OF_MONTH) == dt2Calendar.get(Calendar.DAY_OF_MONTH))
        {
            return true;
        } else
        {
            return false;
        }
    }

    public static boolean areSameHourMinute(long dt1, long dt2)
    {
        GregorianCalendar dt1Calendar = new GregorianCalendar();
        dt1Calendar.setTimeInMillis(dt1);
        GregorianCalendar dt2Calendar = new GregorianCalendar();
        dt2Calendar.setTimeInMillis(dt2);

        if (dt1Calendar.get(Calendar.YEAR) == dt2Calendar.get(Calendar.YEAR) &&
                dt1Calendar.get(Calendar.MONTH) == dt2Calendar.get(Calendar.MONTH) &&
                dt1Calendar.get(Calendar.DAY_OF_MONTH) == dt2Calendar.get(Calendar.DAY_OF_MONTH)
                && dt1Calendar.get(Calendar.HOUR_OF_DAY) == dt2Calendar.get(Calendar.HOUR_OF_DAY)
                && dt1Calendar.get(Calendar.MINUTE) == dt2Calendar.get(Calendar.MINUTE))
        {
            return true;
        } else
        {
            return false;
        }
    }

    public static int calcDayDifference(long dt1, long dt2)
    {
        GregorianCalendar dt1Calendar = new GregorianCalendar();
        dt1Calendar.setTimeInMillis(dt1);
        GregorianCalendar dt2Calendar = new GregorianCalendar();
        dt2Calendar.setTimeInMillis(dt2);

        yyyyMd.setTimeZone(TIME_ZONE);

        String dt1Str = dt1Calendar.get(Calendar.YEAR) + "/"
                + (dt1Calendar.get(Calendar.MONTH) + 1) + "/" +
                dt1Calendar.get(Calendar.DAY_OF_MONTH);

        String dt2Str = dt2Calendar.get(Calendar.YEAR) + "/"
                + (dt2Calendar.get(Calendar.MONTH) + 1) + "/" +
                dt2Calendar.get(Calendar.DAY_OF_MONTH);

        long dt1Days = 0;
        long dt2Days = 0;

        try
        {
            dt1Days = yyyyMd.parse(dt1Str).getTime();
            dt2Days = yyyyMd.parse(dt2Str).getTime();
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        return (int) TimeUnit.DAYS.convert(dt1Days - dt2Days, TimeUnit.MILLISECONDS);
    }

    public static int calcBeginDayDifference(long instanceBegin, long view)
    {
        GregorianCalendar instanceBeginCalendar = new GregorianCalendar();
        GregorianCalendar viewCalendar = new GregorianCalendar();

        instanceBeginCalendar.setTimeInMillis(instanceBegin);
        viewCalendar.setTimeInMillis(view);

        // 윤년을 고려해서 계산한다
        yyyyMd.setTimeZone(TIME_ZONE);

        String instanceBeginStr = instanceBeginCalendar.get(Calendar.YEAR) + "/"
                + (instanceBeginCalendar.get(Calendar.MONTH) + 1) + "/" +
                instanceBeginCalendar.get(Calendar.DAY_OF_MONTH);

        String viewStr = viewCalendar.get(Calendar.YEAR) + "/"
                + (viewCalendar.get(Calendar.MONTH) + 1) + "/" +
                viewCalendar.get(Calendar.DAY_OF_MONTH);

        long instanceBeginDays = 0;
        long viewDays = 0;

        try
        {
            instanceBeginDays = yyyyMd.parse(instanceBeginStr).getTime();
            viewDays = yyyyMd.parse(viewStr).getTime();
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        return (int) TimeUnit.DAYS.convert(instanceBeginDays - viewDays, TimeUnit.MILLISECONDS);
    }

    public static int calcEndDayDifference(long instanceEnd, long view, boolean allDay)
    {
        GregorianCalendar instanceEndCalendar = new GregorianCalendar();
        instanceEndCalendar.setTimeInMillis(instanceEnd);
        if (allDay)
        {
            instanceEndCalendar.add(Calendar.HOUR_OF_DAY, -9);
        }
        GregorianCalendar viewCalendar = new GregorianCalendar();
        viewCalendar.setTimeInMillis(view);

        // 윤년을 고려해서 계산한다
        yyyyMd.setTimeZone(TIME_ZONE);

        String instanceEndStr = instanceEndCalendar.get(Calendar.YEAR) + "/"
                + (instanceEndCalendar.get(Calendar.MONTH) + 1) + "/" +
                instanceEndCalendar.get(Calendar.DAY_OF_MONTH);

        String viewStr = viewCalendar.get(Calendar.YEAR) + "/"
                + (viewCalendar.get(Calendar.MONTH) + 1) + "/" +
                viewCalendar.get(Calendar.DAY_OF_MONTH);

        long instanceEndDays = 0;
        long viewDays = 0;

        try
        {
            instanceEndDays = yyyyMd.parse(instanceEndStr).getTime();
            viewDays = yyyyMd.parse(viewStr).getTime();
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        int difference = (int) TimeUnit.DAYS.convert(instanceEndDays - viewDays, TimeUnit.MILLISECONDS);

        if (instanceEndCalendar.get(Calendar.HOUR_OF_DAY) == 0 && instanceEndCalendar.get(Calendar.MINUTE) == 0)
        {
            difference--;
        }
        return difference;
    }
}
