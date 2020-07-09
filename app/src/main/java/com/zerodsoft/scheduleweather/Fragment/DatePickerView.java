package com.zerodsoft.scheduleweather.Fragment;

import android.content.Context;
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
    private int textHeight;
    private int spacingBetweenText;

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

    private String[] yearArr;
    private String[] monthArr;
    private String[] hourArr;
    private String[] minuteArr;

    private boolean isFirstDraw;

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

        textHeight = rect.height();
        spacingBetweenText = textHeight * 2;

        divisionLinePaint = new Paint();
        divisionLinePaint.setColor(Color.GRAY);

        overScroller = new OverScroller(context);
        gestureDetector = new GestureDetectorCompat(context, onGestureListener);

        calendar = Calendar.getInstance();

        yearArr = new String[11];
        monthArr = new String[12];
        hourArr = new String[12];
        minuteArr = new String[60];

        for (int year = calendar.get(Calendar.YEAR) - 5, i = 0; i <= 10; i++)
        {
            yearArr[i] = Integer.toString(year++);
        }

        for (int i = 0; i <= 11; i++)
        {
            monthArr[i] = Integer.toString(i + 1);
            hourArr[i] = Integer.toString(i + 1);
        }

        for (int i = 0; i <= 59; i++)
        {
            minuteArr[i] = Integer.toString(i);
        }

        isFirstDraw = true;
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
        drawDivisionLine(canvas);
        drawView(canvas);
    }

    private void drawDivisionLine(Canvas canvas)
    {
        int topLineY = (int) (getHeight() / 2 - textHeight * 1.5);
        int bottomLineY = getHeight() / 2 + textHeight / 2;

        canvas.drawLine(0f, topLineY, getWidth(), topLineY, divisionLinePaint);
        canvas.drawLine(0f, bottomLineY, getWidth(), bottomLineY, divisionLinePaint);
    }

    private void drawView(Canvas canvas)
    {
        // 2020	    7	 11 토	 오전	1  	 00
        int yearIndex;
        int monthIndex;
        int hourIndex;
        int minuteIndex;

        if (isFirstDraw)
        {
            yearIndex = 5;
            monthIndex = calendar.get(Calendar.MONTH);
            hourIndex = calendar.get(Calendar.HOUR) - 1;
            minuteIndex = calendar.get(Calendar.MINUTE);

            isFirstDraw = false;
        }

        for (int i = 0; i < yearArr.length; i++)
        {
            canvas.drawText(yearArr[i], currentYearPoint.x, currentYearPoint.y + spacingBetweenText * i, unselectedTextPaint);
        }

        for (int i = 0; i < monthArr.length; i++)
        {
            canvas.drawText(monthArr[i], currentMonthPoint.x, currentMonthPoint.y + spacingBetweenText * i, unselectedTextPaint);
        }

        for (int i = 0; i < hourArr.length; i++)
        {
            canvas.drawText(hourArr[i], currentHourPoint.x, currentHourPoint.y + spacingBetweenText * i, unselectedTextPaint);
        }

        for (int i = 0; i < minuteArr.length; i++)
        {
            canvas.drawText(minuteArr[i], currentMinutePoint.x, currentMinutePoint.y + spacingBetweenText * i, unselectedTextPaint);
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

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public void computeScroll()
    {
        super.computeScroll();
        if (overScroller.computeScrollOffset())
        {

        }
    }
}
