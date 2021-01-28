package com.zerodsoft.scheduleweather.etc;

import android.graphics.Color;
import android.graphics.Paint;
import android.provider.CalendarContract;
import android.text.TextPaint;

public class EventViewUtil
{
    private EventViewUtil()
    {
    }

    public static Paint getEventColorPaint(int color)
    {
        Paint eventColorPaint = new Paint();
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        eventColorPaint.setColor(Color.HSVToColor(hsv));

        return eventColorPaint;
    }

    public static TextPaint getEventTextPaint(float textSize)
    {
        TextPaint eventTextPaint = new TextPaint();
        eventTextPaint.setTextAlign(Paint.Align.LEFT);
        eventTextPaint.setTextSize(textSize);
        eventTextPaint.setAntiAlias(true);
        eventTextPaint.setColor(Color.WHITE);

        return eventTextPaint;
    }

    public static int[] getViewSideMargin(long dataStart, long dataEnd, long viewStart, long viewEnd, int margin)
    {
        int leftMargin = 0;
        int rightMargin = 0;

        // 시작/종료일이 date가 아니나, 일정에 포함되는 경우
        if (dataStart < viewStart && dataEnd > viewEnd)
        {
            leftMargin = 0;
            rightMargin = 0;
        }
        // 시작일이 date인 경우, 종료일은 endDate 이후
        else if (dataEnd >= viewEnd && dataStart >= viewStart && dataStart < viewEnd)
        {
            leftMargin = margin;
            rightMargin = 0;
        }
        // 종료일이 date인 경우, 시작일은 startDate이전
        else if (dataEnd >= viewStart && dataEnd < viewEnd && dataStart < viewStart)
        {
            leftMargin = 0;
            rightMargin = margin;
        }
        // 시작/종료일이 date인 경우
        else if (dataEnd >= viewStart && dataEnd < viewEnd && dataStart >= viewStart && dataStart < viewEnd)
        {
            leftMargin = margin;
            rightMargin = margin;
        }

        return new int[]{leftMargin, rightMargin};
    }
}
