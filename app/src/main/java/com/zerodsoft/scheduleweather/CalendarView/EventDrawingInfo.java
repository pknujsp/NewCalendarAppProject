package com.zerodsoft.scheduleweather.CalendarView;

import android.graphics.PointF;

import com.zerodsoft.scheduleweather.Room.DTO.ScheduleDTO;

public class EventDrawingInfo
{
    private int left;
    private int right;
    private int top;
    private int bottom;
    private PointF startPoint;
    private PointF endPoint;
    private ScheduleDTO schedule;
    private AccountType accountType;

    public EventDrawingInfo(int left, int right, int top, int bottom, ScheduleDTO schedule, AccountType accountType)
    {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
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

    public int getLeft()
    {
        return left;
    }

    public EventDrawingInfo setLeft(int left)
    {
        this.left = left;
        return this;
    }

    public int getRight()
    {
        return right;
    }

    public EventDrawingInfo setRight(int right)
    {
        this.right = right;
        return this;
    }

    public int getTop()
    {
        return top;
    }

    public EventDrawingInfo setTop(int top)
    {
        this.top = top;
        return this;
    }

    public int getBottom()
    {
        return bottom;
    }

    public EventDrawingInfo setBottom(int bottom)
    {
        this.bottom = bottom;
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
