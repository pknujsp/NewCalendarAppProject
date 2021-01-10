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

    public static final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat HH = new SimpleDateFormat("HH");
    public static final SimpleDateFormat H = new SimpleDateFormat("H");
    public static final SimpleDateFormat MdE_FORMAT = new SimpleDateFormat("M/d E");
    public static final SimpleDateFormat DATE_FORMAT_NOT_ALLDAY = new SimpleDateFormat("yyyy년 M월 d일 E a h시 m분");
    public static final SimpleDateFormat YYYY_년_M_월_D_일_E = new SimpleDateFormat("yyyy년 M월 d일 E");
    public static final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat D_E = new SimpleDateFormat("d E");
    public static final SimpleDateFormat D = new SimpleDateFormat("d");
    public static final SimpleDateFormat YEAR_MONTH_FORMAT = new SimpleDateFormat("yyyy/MM");
    public static final SimpleDateFormat E = new SimpleDateFormat("E");
    public static final SimpleDateFormat M_월_D_일 = new SimpleDateFormat("M월 d일");

    public static final SimpleDateFormat HOURS_12 = new SimpleDateFormat("a h:mm");
    public static final SimpleDateFormat HOURS_24 = new SimpleDateFormat("H:mm");

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
