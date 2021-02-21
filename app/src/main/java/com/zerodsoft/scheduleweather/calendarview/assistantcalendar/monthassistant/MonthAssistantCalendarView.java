package com.zerodsoft.scheduleweather.calendarview.assistantcalendar.monthassistant;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MonthAssistantCalendarView extends ViewGroup
{
    /*
    요일, 날짜, 이벤트 개수
     */
    protected static final TextPaint THIS_MONTH_DATE_TEXTPAINT = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    protected static final TextPaint NOT_THIS_MONTH_DATE_TEXTPAINT = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    protected static final TextPaint INSTANCE_COUNT_TEXTPAINT = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    protected static float TEXT_SIZE;
    protected static float DATE_TEXT_VIEW_HEIGHT;
    protected static float COUNT_TEXT_VIEW_HEIGHT;
    protected static float DATE_TEXT_PADDING_TB;
    protected static float COUNT_PADDING_TB;

    public MonthAssistantCalendarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        Rect rect = new Rect();

        TEXT_SIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, getContext().getResources().getDisplayMetrics());

        THIS_MONTH_DATE_TEXTPAINT.setTextSize(TEXT_SIZE);
        THIS_MONTH_DATE_TEXTPAINT.setTextAlign(Paint.Align.CENTER);
        THIS_MONTH_DATE_TEXTPAINT.setColor(Color.BLACK);

        NOT_THIS_MONTH_DATE_TEXTPAINT.setTextSize(TEXT_SIZE);
        NOT_THIS_MONTH_DATE_TEXTPAINT.setTextAlign(Paint.Align.CENTER);
        NOT_THIS_MONTH_DATE_TEXTPAINT.setColor(Color.GRAY);
        NOT_THIS_MONTH_DATE_TEXTPAINT.getTextBounds("3", 0, 1, rect);

        DATE_TEXT_VIEW_HEIGHT = rect.height();
        COUNT_TEXT_VIEW_HEIGHT = DATE_TEXT_VIEW_HEIGHT;

        INSTANCE_COUNT_TEXTPAINT.setTextSize(TEXT_SIZE);
        INSTANCE_COUNT_TEXTPAINT.setTextAlign(Paint.Align.CENTER);
        INSTANCE_COUNT_TEXTPAINT.setColor(Color.BLUE);

        DATE_TEXT_PADDING_TB = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics());
        COUNT_PADDING_TB = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, getResources().getDisplayMetrics());

        setBackgroundColor(Color.WHITE);
        setWillNotDraw(false);
    }

    public MonthAssistantCalendarView(Context context)
    {
        super(context);

    }


    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3)
    {
        // resolveSize : 실제 설정할 크기를 계산
        final int ITEM_WIDTH = getWidth() / 7;
        final int ITEM_HEIGHT = (int) (DATE_TEXT_PADDING_TB * 2 + DATE_TEXT_VIEW_HEIGHT + COUNT_PADDING_TB * 2 + COUNT_TEXT_VIEW_HEIGHT);

        // childview의 크기 설정
        measureChildren(ITEM_WIDTH, ITEM_HEIGHT);

        final int childCount = getChildCount();
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);
    }

    public static class MonthAssistantItemView extends View
    {
        private final Date beginDate;
        private final Date endDate;
        private final boolean thisMonthDate;
        private int count;

        public MonthAssistantItemView(Context context, boolean thisMonthDate, Date beginDate, Date endDate)
        {
            super(context);
            this.thisMonthDate = thisMonthDate;
            this.beginDate = beginDate;
            this.endDate = endDate;
        }

        public void setCount(int count)
        {
            this.count = count;
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom)
        {
            super.onLayout(changed, left, top, right, bottom);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);
            final float countSpaceTop = DATE_TEXT_VIEW_HEIGHT + DATE_TEXT_PADDING_TB * 2;
            final int cellWidth = getWidth();
            final int cellHeight = getHeight();

            //날짜
            canvas.drawText(ClockUtil.D.format(beginDate), (float) cellWidth / 2f, countSpaceTop / 2f + DATE_TEXT_VIEW_HEIGHT / 2f, thisMonthDate ? THIS_MONTH_DATE_TEXTPAINT
                    : NOT_THIS_MONTH_DATE_TEXTPAINT);

            //이벤트 개수
            if (count > 0)
            {
                canvas.drawText(Integer.toString(count),
                        (float) cellWidth / 2f, countSpaceTop + (cellHeight - countSpaceTop) / 2f + COUNT_TEXT_VIEW_HEIGHT / 2f, INSTANCE_COUNT_TEXTPAINT);
            }
        }
    }
}
