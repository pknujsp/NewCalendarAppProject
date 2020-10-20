package com.zerodsoft.scheduleweather.utility;

import java.util.Calendar;
import java.util.Date;

public class DateHour
{
    public final static String[] hourStrings = {"오전 12", "오전 1", "오전 2", "오전 3", "오전 4"
            , "오전 5", "오전 6", "오전 7", "오전 8", "오전 9", "오전 10", "오전 11", "오후 12"
            , "오후 1", "오후 2", "오후 3", "오후 4", "오후 5", "오후 6", "오후 7", "오후 8"
            , "오후 9", "오후 10", "오후 11"};
    public final static String[] dayStrings = {"일", "월", "화", "수", "목", "금", "토"};


    private DateHour()
    {
    }

    public static String[] getHourStrings()
    {
        return hourStrings;
    }

    public static String[] getDayStrings()
    {
        return dayStrings;
    }

    public static String getDayString(int index)
    {
        return dayStrings[index];
    }

    public static String getHourString(int index)
    {
        return hourStrings[index];
    }

    public static String getDate(Date date)
    {
        return Clock.DAY_OF_MONTH_FORMAT.format(date);
    }
}
