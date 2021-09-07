package com.zerodsoft.calendarplatform.calendarview.month;

import android.content.ContentValues;
import android.graphics.Paint;
import android.text.TextPaint;

public class EventData
{
    public static final int BEFORE_AFTER = 0;
    public static final int BEFORE_THISWEEK = 1;
    public static final int THISWEEK_AFTER = 2;
    public static final int THISWEEK_THISWEEK = 3;
    public static final int DAY = 4;
    public static final int WEEK = 5;

    private int startIndex;
    private int endIndex;
    private int dateLength;
    private int row;
    private int leftMargin;
    private int rightMargin;
    private int type = DAY;

    private Paint eventColorPaint;
    private TextPaint eventTextPaint;
    private ContentValues event;

    public EventData(ContentValues event, int row)
    {
        this.event = event;
        this.row = row;
    }


    public EventData(ContentValues event, int startIndex, int endIndex, int row)
    {
        this.event = event;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.row = row;
    }

    public EventData(ContentValues event, int startIndex, int endIndex, int row, int dateLength)
    {
        this.event = event;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.row = row;
        this.dateLength = dateLength;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public int getDateLength()
    {
        return dateLength;
    }

    public boolean isAllDayEvent()
    {
        return endIndex == startIndex;
    }

    public ContentValues getEvent()
    {
        return event;
    }

    public int getStartIndex()
    {
        return startIndex;
    }

    public int getEndIndex()
    {
        return endIndex;
    }

    public int getRow()
    {
        return row;
    }

    public int getLeftMargin()
    {
        return leftMargin;
    }

    public int getRightMargin()
    {
        return rightMargin;
    }

    public EventData setRow(int row)
    {
        this.row = row;
        return this;
    }

    public EventData setDateLength(int dateLength)
    {
        this.dateLength = dateLength;
        return this;
    }

    public EventData setStartIndex(int startIndex)
    {
        this.startIndex = startIndex;
        return this;
    }

    public EventData setEndIndex(int endIndex)
    {
        this.endIndex = endIndex;
        return this;
    }

    public void setLeftMargin(int leftMargin)
    {
        this.leftMargin = leftMargin;
    }

    public void setRightMargin(int rightMargin)
    {
        this.rightMargin = rightMargin;
    }

    public EventData setEventColorPaint(Paint eventColorPaint)
    {
        this.eventColorPaint = eventColorPaint;
        return this;
    }

    public Paint getEventColorPaint()
    {
        return eventColorPaint;
    }

    public EventData setEventTextPaint(TextPaint eventTextPaint)
    {
        this.eventTextPaint = eventTextPaint;
        return this;
    }

    public TextPaint getEventTextPaint()
    {
        return eventTextPaint;
    }
}