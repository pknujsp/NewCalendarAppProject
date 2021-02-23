package com.zerodsoft.scheduleweather.calendarview.month;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.View;

import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.Date;

public class MonthCalendarItemView extends View
{
    private final TextPaint DAY_TEXT_PAINT;
    private static final Paint TODAY_PAINT = new Paint();

    private float x;
    private float y;
    private boolean isToday;

    private Date startDate;
    private Date endDate;

    public MonthCalendarItemView(Context context, int dayTextColor)
    {
        super(context);

        DAY_TEXT_PAINT = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        DAY_TEXT_PAINT.setTextAlign(Paint.Align.CENTER);
        DAY_TEXT_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, context.getResources().getDisplayMetrics()));
        DAY_TEXT_PAINT.setColor(dayTextColor);

        TODAY_PAINT.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, getContext().getResources().getDisplayMetrics()));
        TODAY_PAINT.setStyle(Paint.Style.STROKE);
        TODAY_PAINT.setColor(Color.BLUE);

        TypedValue backgroundValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, backgroundValue, true);
        setBackgroundResource(backgroundValue.resourceId);
    }

    public MonthCalendarItemView setDate(Date startDate, Date endDate, boolean isToday)
    {
        this.startDate = startDate;
        this.endDate = endDate;
        this.isToday = isToday;
        return this;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        Rect rect = new Rect();
        DAY_TEXT_PAINT.getTextBounds("31", 0, 1, rect);

        x = getWidth() / 2;
        y = rect.height() + 8;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawText(ClockUtil.D.format(startDate), x, y, DAY_TEXT_PAINT);

        if (isToday)
        {
            canvas.drawRect(0, 0, getWidth(), getHeight(), TODAY_PAINT);
        }
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }
}