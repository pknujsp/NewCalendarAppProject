package com.zerodsoft.scheduleweather.calendarview.model;

import android.graphics.PointF;

import com.zerodsoft.scheduleweather.calendarview.enums.AccountType;

public class EventDrawingInfo
{
    private int startCol;
    private int endCol;
    private int row;
    private PointF startPoint;
    private PointF endPoint;
    private ScheduleDTO schedule;
    private AccountType accountType;

    public EventDrawingInfo(int startCol, int endCol, int row, ScheduleDTO schedule, AccountType accountType)
    {
        this.startCol = startCol;
        this.endCol = endCol;
        this.row = row;
        this.schedule = schedule;
        this.accountType = accountType;
    }

    public EventDrawingInfo(PointF startPoint, PointF endPoint, ScheduleDTO schedule, AccountType accountType)
    {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.schedule = schedule;
        this.accountType = accountType;
    }

    public int getStartCol()
    {
        return startCol;
    }

    public EventDrawingInfo setStartCol(int startCol)
    {
        this.startCol = startCol;
        return this;
    }

    public int getEndCol()
    {
        return endCol;
    }

    public EventDrawingInfo setEndCol(int endCol)
    {
        this.endCol = endCol;
        return this;
    }

    public int getRow()
    {
        return row;
    }

    public EventDrawingInfo setRow(int row)
    {
        this.row = row;
        return this;
    }

    public PointF getStartPoint()
    {
        return startPoint;
    }

    public EventDrawingInfo setStartPoint(PointF startPoint)
    {
        this.startPoint = startPoint;
        return this;
    }

    public PointF getEndPoint()
    {
        return endPoint;
    }

    public EventDrawingInfo setEndPoint(PointF endPoint)
    {
        this.endPoint = endPoint;
        return this;
    }

    public ScheduleDTO getSchedule()
    {
        return schedule;
    }

    public EventDrawingInfo setSchedule(ScheduleDTO schedule)
    {
        this.schedule = schedule;
        return this;
    }

    public AccountType getAccountType()
    {
        return accountType;
    }

    public EventDrawingInfo setAccountType(AccountType accountType)
    {
        this.accountType = accountType;
        return this;
    }
}
