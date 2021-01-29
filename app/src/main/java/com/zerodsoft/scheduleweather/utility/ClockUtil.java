package com.zerodsoft.scheduleweather.utility;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
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

    public static int calcDateDifference(int calendarType, long dt1, long dt2)
    {
        int difference = 0;

        GregorianCalendar dt1GregorianCalendar = new GregorianCalendar(ClockUtil.TIME_ZONE);
        dt1GregorianCalendar.setTimeInMillis(dt1);
        GregorianCalendar dt2GregorianCalendar = new GregorianCalendar(ClockUtil.TIME_ZONE);
        dt2GregorianCalendar.setTimeInMillis(dt2);

        if (calendarType == MONTH)
        {
            difference = (dt1GregorianCalendar.get(Calendar.YEAR) * 12 + dt1GregorianCalendar.get(Calendar.MONTH)) -
                    (dt2GregorianCalendar.get(Calendar.YEAR) * 12 + dt2GregorianCalendar.get(Calendar.MONTH));
        } else if (calendarType == WEEK)
        {
            difference = (int) (TimeUnit.MILLISECONDS.toDays(dt1) - TimeUnit.MILLISECONDS.toDays(dt2)) / 7;
        } else if (calendarType == DAY)
        {
            // 윤년을 고려해서 계산한다
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/M/d", Locale.KOREAN);

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

            difference = (int) TimeUnit.DAYS.convert(dt1Days - dt2Days, TimeUnit.MILLISECONDS);
        }
        return difference;
    }

}
