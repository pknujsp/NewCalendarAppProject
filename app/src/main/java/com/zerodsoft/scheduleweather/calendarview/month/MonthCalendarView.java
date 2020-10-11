package com.zerodsoft.scheduleweather.calendarview.month;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MonthCalendarView extends ViewGroup
{
    private Calendar calendar;
    private final float HEADER_DAY_TEXT_SIZE;
    private final TextPaint HEADER_DAY_PAINT;
    private SparseArray<EventDto> schedules;

    private int ITEM_WIDTH;
    private int ITEM_HEIGHT;

    public MonthCalendarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        HEADER_DAY_TEXT_SIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, context.getResources().getDisplayMetrics());

        HEADER_DAY_PAINT = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        HEADER_DAY_PAINT.setTextAlign(Paint.Align.CENTER);
        HEADER_DAY_PAINT.setTextSize(HEADER_DAY_TEXT_SIZE);
        HEADER_DAY_PAINT.setColor(Color.BLACK);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    public MonthCalendarView setCalendar(Calendar calendar)
    {
        this.calendar = calendar;
        return this;
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3)
    {
        // resolveSize : 실제 설정할 크기를 계산
        ITEM_WIDTH = getWidth() / 7;
        ITEM_HEIGHT = getHeight() / 6;
        // childview의 크기 설정
        measureChildren(ITEM_WIDTH, ITEM_HEIGHT);

        int childCount = getChildCount();
        int left = 0;
        int right = 0;
        int top = 0;
        int bottom = 0;

        for (int index = 0; index < childCount; index++)
        {
            if (index % 7 == 0)
            {
                // 마지막 열 인경우 다음 행으로 넘어감
                left = 0;
                right = ITEM_WIDTH;
            } else
            {
                left = ITEM_WIDTH * (index % 7);
                right = ITEM_WIDTH * ((index % 7) + 1);
            }
            top = ITEM_HEIGHT * (index / 7);
            bottom = ITEM_HEIGHT * ((index / 7) + 1);

            View childView = getChildAt(index);
            childView.layout(left, top, right, bottom);
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
    }

    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);
    }

    public void setSchedules(SparseArray<EventDto> list)
    {
        schedules = list;
        // 각각의 ITEMVIEW에 데이터 전달하고 다시 그림
        int childCount = getChildCount();

        for (int index = 0; index < childCount; index++)
        {
            if (list.get(index) != null)
            {
                ((MonthCalendarItemView) getChildAt(index)).setSchedules(list.get(index).schedulesList);
            }
        }
        invalidate();
    }

    private List<ScheduleDTO> getSchedulesList(Date startDate, Date endDate)
    {
        /*
        (Datetime(start_date) >= Datetime(:startDate) AND Datetime(start_date) < Datetime(:endDate))
        OR
        (Datetime(end_date) >= Datetime(:startDate) AND Datetime(end_date) < Datetime(:endDate))
        OR
        (Datetime(start_date) < Datetime(:startDate) AND Datetime(end_date) > Datetime(:endDate))
         */
        List<ScheduleDTO> schedules = new ArrayList<>();


        return schedules;
    }

}
