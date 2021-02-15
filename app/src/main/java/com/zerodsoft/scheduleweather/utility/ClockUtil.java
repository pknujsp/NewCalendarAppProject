package com.zerodsoft.scheduleweather.utility;

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

    public static final int MONTH = 0;
    public static final int WEEK = 1;
    public static final int DAY = 2;

    private ClockUtil()
    {
    }

    public static int calcDayDifference(long dt1, long dt2, boolean allDay)
    {
        GregorianCalendar dt1GregorianCalendar = new GregorianCalendar();
        dt1GregorianCalendar.setTimeInMillis(dt1);
        GregorianCalendar dt2GregorianCalendar = new GregorianCalendar();
        dt2GregorianCalendar.setTimeInMillis(dt2);

        // 윤년을 고려해서 계산한다
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/M/d", Locale.KOREAN);
        simpleDateFormat.setTimeZone(allDay ? UTC_TIME_ZONE : TIME_ZONE);

        String dt1Str = dt1GregorianCalendar.get(Calendar.YEAR) + "/"
                + (dt1GregorianCalendar.get(Calendar.MONTH) + 1) + "/" +
                dt1GregorianCalendar.get(Calendar.DAY_OF_MONTH);

        String dt2Str = dt2GregorianCalendar.get(Calendar.YEAR) + "/"
                + (dt2GregorianCalendar.get(Calendar.MONTH) + 1) + "/" +
                dt2GregorianCalendar.get(Calendar.DAY_OF_MONTH);

        long dt1Days = 0;
        long dt2Days = 0;

        try
        {
            dt1Days = simpleDateFormat.parse(dt1Str).getTime();
            dt2Days = simpleDateFormat.parse(dt2Str).getTime();
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        int difference = (int) TimeUnit.DAYS.convert(dt1Days - dt2Days, TimeUnit.MILLISECONDS);
        return difference;
    }


}
