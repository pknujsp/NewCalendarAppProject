package com.zerodsoft.scheduleweather.CalendarView.Dto;

import java.util.Calendar;

public class CoordinateInfo
{
    private Calendar date;
    private float startX;
    private float endX;

    public Calendar getDate()
    {
        return date;
    }

    public CoordinateInfo setDate(Calendar date)
    {
        try
        {
            this.date = (Calendar) date.clone();
        } catch (NullPointerException e)
        {
            this.date = null;
        }

        return this;
    }

    public float getStartX()
    {
        return startX;
    }

    public CoordinateInfo setStartX(float startX)
    {
        this.startX = startX;
        return this;
    }

    public float getEndX()
    {
        return endX;
    }

    public CoordinateInfo setEndX(float endX)
    {
        this.endX = endX;
        return this;
    }
}
