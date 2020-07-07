package com.zerodsoft.scheduleweather.Fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;

import com.zerodsoft.scheduleweather.R;

class DatePickerView extends View
{
    private Context context;
    private int unselectedTextColor;
    private int selectedTextColor;
    private int backgroundColor;
    private int textSize;
    private int spacingBetweenElement;

    private Paint backgroundPaint;
    private Paint unselectedTextPaint;
    private Paint selectedTextPaint;
    private Paint divisionLinePaint;

    private OverScroller overScroller;
    private GestureDetectorCompat gestureDetector;

    public DatePickerView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DatePickerView, 0, 0);

        try
        {
            unselectedTextColor = typedArray.getDimensionPixelSize(R.styleable.DatePickerView_DatePickerViewUnSelectedTextColor, 0);
            selectedTextColor = typedArray.getDimensionPixelSize(R.styleable.DatePickerView_DatePickerViewSelectedTextColor, 0);
            backgroundColor = typedArray.getDimensionPixelSize(R.styleable.DatePickerView_DatePickerViewBackgroundColor, 0);
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

        divisionLinePaint = new Paint();
        divisionLinePaint.setColor(Color.GRAY);

        overScroller = new OverScroller(context);
        gestureDetector = new GestureDetectorCompat(context, onGestureListener);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
    }

    private void drawView(Canvas canvas)
    {

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
