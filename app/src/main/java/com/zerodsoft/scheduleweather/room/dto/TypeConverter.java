package com.zerodsoft.scheduleweather.room.dto;

import com.zerodsoft.scheduleweather.utility.Clock;

import java.text.ParseException;
import java.util.Date;

public class TypeConverter
{
    @androidx.room.TypeConverter
    public static Date timeToDate(String value)
    {
        if (value != null)
        {
            try
            {
                return Clock.DB_DATE_FORMAT.parse(value);
            } catch (ParseException e)
            {

            }
        } else
        {
        }
        return null;
    }

    @androidx.room.TypeConverter
    public static String dateToTime(Date value)
    {
        return value != null ? Clock.DB_DATE_FORMAT.format(value) : null;
    }
}
