package com.zerodsoft.tripweather.Calendar;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SelectedDate implements Serializable
{
    private String year;
    private String month;
    private String day;

    public SelectedDate()
    {

    }

    public String getYear()
    {
        return year;
    }

    public SelectedDate setYear(String year)
    {
        this.year = year;
        return this;
    }

    public String getMonth()
    {
        return month;
    }

    public SelectedDate setMonth(String month)
    {
        this.month = month;
        return this;
    }

    public String getDay()
    {
        return day;
    }

    public SelectedDate setDay(String day)
    {
        this.day = day;
        return this;
    }

    public int toInt()
    {
        return Integer.valueOf(year).intValue() + Integer.valueOf(month).intValue() + Integer.valueOf(day).intValue();
    }
}
