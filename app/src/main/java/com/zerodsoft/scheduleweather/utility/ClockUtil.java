package com.zerodsoft.scheduleweather.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ClockUtil
{
    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("Asia/Seoul");

    public static final SimpleDateFormat yyyyMMdd_FORMAT = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat WEATHER_TIME_FORMAT = new SimpleDateFormat("HH");
    public static final SimpleDateFormat H_FORMAT = new SimpleDateFormat("H");
    public static final SimpleDateFormat WEATHER_DATE_FORMAT = new SimpleDateFormat("MMdd");
    public static final SimpleDateFormat MdE_FORMAT = new SimpleDateFormat("M/d E");
    public static final SimpleDateFormat DATE_FORMAT_NOT_ALLDAY = new SimpleDateFormat("yyyy년 M월 d일 E a h시 m분");
    public static final SimpleDateFormat DATE_FORMAT_ALLDAY = new SimpleDateFormat("yyyy년 M월 d일 E");
    public static final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat DATE_DAY_OF_WEEK_FORMAT = new SimpleDateFormat("d E");
    public static final SimpleDateFormat DAY_OF_MONTH_FORMAT = new SimpleDateFormat("d");
    public static final SimpleDateFormat YEAR_MONTH_FORMAT = new SimpleDateFormat("yyyy/MM");
    public static final SimpleDateFormat DAY_OF_WEEK_FORMAT = new SimpleDateFormat("E");
    public static final SimpleDateFormat Md_FORMAT = new SimpleDateFormat("M월 d일");

    public static final int MONTH = 0;
    public static final int WEEK = 1;
    public static final int DAY = 2;

    private ClockUtil()
    {
    }

    public static int calcDateDifference(int calendarType, Date scheduleStartDate, Calendar viewCalendar)
    {
        int difference = 0;
        Calendar startDateCalendar = Calendar.getInstance();
        startDateCalendar.setTime(scheduleStartDate);

        if (calendarType == MONTH)
        {
            difference = (startDateCalendar.get(Calendar.YEAR) * 12 + startDateCalendar.get(Calendar.MONTH)) -
                    (viewCalendar.get(Calendar.YEAR) * 12 + viewCalendar.get(Calendar.MONTH));
        } else if (calendarType == WEEK)
        {
            difference = (int) (TimeUnit.MILLISECONDS.toDays(startDateCalendar.getTimeInMillis()) -
                    TimeUnit.MILLISECONDS.toDays(viewCalendar.getTimeInMillis())) / 7;
        } else if (calendarType == DAY)
        {
            long start = TimeUnit.MILLISECONDS.toDays(startDateCalendar.getTimeInMillis());
            long view = TimeUnit.MILLISECONDS.toDays(viewCalendar.getTimeInMillis());
            Date viewDate = viewCalendar.getTime();
            difference = (int) (start - view);
        }

        return difference;
    }

}
