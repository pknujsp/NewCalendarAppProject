package com.zerodsoft.scheduleweather.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;

import com.zerodsoft.scheduleweather.R;

import java.util.Calendar;

public class DatePickerView extends View
{
    private Context context;
    private int unselectedTextColor;
    private int selectedTextColor;
    private int backgroundColor;
    private int textSize;
    private int spacingBetweenElement;
    private int spacingYBetweenText;

    private Paint backgroundPaint;
    private Paint unselectedTextPaint;
    private Paint selectedTextPaint;
    private Paint divisionLinePaint;

    private OverScroller overScroller;
    private GestureDetectorCompat gestureDetector;

    private Calendar calendar;

    private PointF currentYearPoint;
    private PointF currentMonthPoint;
    private PointF currentDayPoint;
    private PointF currentMeridiemPoint;
    private PointF currentHourPoint;
    private PointF currentMinutePoint;

    public DatePickerView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DatePickerView, 0, 0);

        try
        {
            unselectedTextColor = typedArray.getColor(R.styleable.DatePickerView_DatePickerViewUnSelectedTextColor, 0);
            selectedTextColor = typedArray.getColor(R.styleable.DatePickerView_DatePickerViewSelectedTextColor, 0);
            backgroundColor = typedArray.getColor(R.styleable.DatePickerView_DatePickerViewBackgroundColor, 0);
            textSize = typedArray.getDimensionPixelSize(R.styleable.DatePickerView_DatePickerViewTextSize, 0);
        } finally
        {
            typedArray.recycle();
        }

        init();
    }

    private void init()
    {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);

        unselectedTextPaint = new Paint();
        unselectedTextPaint.setColor(unselectedTextColor);
        unselectedTextPaint.setTextSize(textSize);

        Rect rect = new Rect();
        selectedTextPaint = new Paint();
        selectedTextPaint.setColor(selectedTextColor);
        selectedTextPaint.setTextSize(textSize);
        selectedTextPaint.getTextBounds("2", 0, 1, rect);

        spacingBetweenElement = rect.height() / 2;
        spacingYBetweenText = spacingBetweenElement * 2;

        divisionLinePaint = new Paint();
        divisionLinePaint.setColor(Color.GRAY);

        overScroller = new OverScroller(context);
        gestureDetector = new GestureDetectorCompat(context, onGestureListener);

        calendar = Calendar.getInstance();
    }

    @Override
    public void layout(int l, int t, int r, int b)
    {
        super.layout(l, t, r, b);

        float x = getWidth() / 6;
        float y = getHeight() / 2;

        currentYearPoint = new PointF(0f, y);
        currentMonthPoint = new PointF(x, y);
        currentDayPoint = new PointF(x * 2, y);
        currentMeridiemPoint = new PointF(x * 3, y);
        currentHourPoint = new PointF(x * 4, y);
        currentMinutePoint = new PointF(x * 5, y);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawView(canvas);
    }

    private void drawView(Canvas canvas)
    {
        // 2020	    7	 11 토	 오전	1  	 00
        for (int i = -3; i <= 3; i++)
        {
            if (i == 0)
            {
                // selected
                canvas.drawLine(0f, getHeight() / 2, getWidth(), getHeight() / 2, divisionLinePaint);
            }
            canvas.drawText(Integer.toString(calendar.get(Calendar.YEAR)), currentYearPoint.x, currentYearPoint.y + spacingYBetweenText * i, unselectedTextPaint);
            canvas.drawText(Integer.toString(calendar.get(Calendar.MONTH)), currentMonthPoint.x, currentMonthPoint.y + spacingYBetweenText * i, unselectedTextPaint);
            canvas.drawText(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)), currentDayPoint.x, currentDayPoint.y + spacingYBetweenText * i, unselectedTextPaint);
            canvas.drawText(Integer.toString(calendar.get(Calendar.HOUR)), currentHourPoint.x, currentHourPoint.y + spacingYBetweenText * i, unselectedTextPaint);
            canvas.drawText(Integer.toString(calendar.get(Calendar.MINUTE)), currentMinutePoint.x, currentMinutePoint.y + spacingYBetweenText * i, unselectedTextPaint);
        }
    }

    private final GestureDetector.SimpleOnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener()
    {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    };
}
