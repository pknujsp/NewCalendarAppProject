package com.zerodsoft.scheduleweather.CalendarView.Dto;

import android.graphics.PointF;

import java.util.Calendar;

public class ColumnData
{
    private PointF columnPoint;
    private Calendar date;

    public PointF getColumnPoint()
    {
        return columnPoint;
    }

    public ColumnData setColumnPoint(PointF columnPoint)
    {
        this.columnPoint = columnPoint;
        return this;
    }

    public Calendar getDate()
    {
        return date;
    }

    public ColumnData setDate(Calendar date)
    {
        this.date = date;
        return this;
    }
}
