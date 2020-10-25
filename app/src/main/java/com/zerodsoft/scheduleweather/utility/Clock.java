package com.zerodsoft.scheduleweather.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class Clock
{
    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("Asia/Seoul");

    public static final SimpleDateFormat yyyyMMdd_FORMAT = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat WEATHER_TIME_FORMAT = new SimpleDateFormat("HH");
    public static final SimpleDateFormat WEATHER_DATE_FORMAT = new SimpleDateFormat("MMdd");
    public static final SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy년 M월 d일 E a h시 m분");
    public static final SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy년 M월 d일 E");
    public static final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat DATE_DAY_OF_WEEK_FORMAT = new SimpleDateFormat("d E");
    public static final SimpleDateFormat DAY_OF_MONTH_FORMAT = new SimpleDateFormat("d");
    public static final SimpleDateFormat YEAR_MONTH_FORMAT = new SimpleDateFormat("yyyy/MM");

    private Clock()
    {
    }

}
