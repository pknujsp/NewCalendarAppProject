package com.zerodsoft.scheduleweather.CalendarView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;

import com.zerodsoft.scheduleweather.R;

import java.util.Calendar;

public class WeekDayView extends View
{
    private static final String TAG = "WEEK_DAY_VIEW_GESTURE";
    private Context context;

    private int weekDayHourTextColor;
    private int weekDayHourTextSize;
    private int weekDayViewBackgroundColor;
    private int weekDayViewLineThickness;
    private int weekDayViewLineColor;
    private int spacingLinesBetweenHour;
    private int spacingLinesBetweenDay;
    private int hourTextHeight;
    private Rect hourTextBoxRect = new Rect();
    private int scheduleLayoutWidth;

    private Paint weekDayHourTextPaint;
    private Paint weekDayHorizontalLinePaint;
    private Paint weekDayVerticalLinePaint;
    private Paint weekDayBackgroundPaint;

    private OverScroller overScroller;
    private GestureDetectorCompat gestureDetector;
    private PointF currentCoordinate = new PointF(0f, 0f);
    private PointF lastCoordinate = new PointF(0f, 0f);

    private float mDistanceX;
    private float mDistanceY;
    private float startY = 8f;

    private boolean areScrolling = false;

    private int currentScrollDirection;

    public WeekDayView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WeekDayView, 0, 0);

        try
        {
            weekDayHourTextColor = a.getColor(R.styleable.WeekDayView_WeekDayHourTextColor, weekDayHourTextColor);
            weekDayHourTextSize = a.getDimensionPixelSize(R.styleable.WeekDayView_WeekDayHourTextSize, weekDayHourTextSize);
            weekDayViewBackgroundColor = a.getColor(R.styleable.WeekDayView_WeekDayViewBackgroundColor, weekDayViewBackgroundColor);
            weekDayViewLineThickness = a.getDimensionPixelSize(R.styleable.WeekDayView_WeekDayViewLineThickness, weekDayViewLineThickness);
            weekDayViewLineColor = a.getColor(R.styleable.WeekDayView_WeekDayViewLineColor, weekDayViewLineColor);
        } finally
        {
            a.recycle();
        }
        init();
    }

    private final GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener()
    {


        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            areScrolling = true;
            if (currentScrollDirection == SCROLL_AXIS_NONE)
            {
                if (Math.abs(distanceX) > Math.abs(distanceY))
                {
                    // 가로 스크롤
                    currentScrollDirection = SCROLL_AXIS_HORIZONTAL;
                } else
                {
                    // 세로 스크롤
                    currentScrollDirection = SCROLL_AXIS_VERTICAL;
                }
            }
            mDistanceX = distanceX;
            mDistanceY = distanceY;
            ViewCompat.postInvalidateOnAnimation(WeekDayView.this);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onDown(MotionEvent e)
        {
            overScroller.forceFinished(true);
            lastCoordinate.x = currentCoordinate.x;
            lastCoordinate.y = currentCoordinate.y;
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e)
        {
            return super.onSingleTapConfirmed(e);
        }

    };

    private void init()
    {
        weekDayHourTextPaint = new Paint();
        weekDayHourTextPaint.setColor(weekDayHourTextColor);
        weekDayHourTextPaint.setTextSize(weekDayHourTextSize);
        weekDayHourTextPaint.setTextAlign(Paint.Align.LEFT);
        weekDayHourTextPaint.getTextBounds("오전 12", 0, "오전 12".length(), hourTextBoxRect);

        spacingLinesBetweenHour = hourTextBoxRect.height() * 4;
        hourTextHeight = hourTextBoxRect.height();

        weekDayBackgroundPaint = new Paint();
        weekDayBackgroundPaint.setColor(weekDayViewBackgroundColor);

        weekDayHorizontalLinePaint = new Paint();
        weekDayHorizontalLinePaint.setColor(weekDayViewLineColor);

        weekDayVerticalLinePaint = new Paint();
        weekDayVerticalLinePaint.setColor(weekDayViewLineColor);

        gestureDetector = new GestureDetectorCompat(context, onGestureListener);
        overScroller = new OverScroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawDayView(canvas);
        Log.e(TAG, "currentCoordinate X : " + Float.toString(currentCoordinate.x));
    }

    @Override
    public void layout(int l, int t, int r, int b)
    {
        super.layout(l, t, r, b);
        scheduleLayoutWidth = getWidth() - getWidth() / 8;
        spacingLinesBetweenDay = scheduleLayoutWidth / 7;
    }

    /*
    private void drawDayView(Canvas canvas)
    {
        Log.e(TAG, "drawDayView : " + Float.toString(currentCoordinate.x));
        canvas.drawRect(0, 0, getWidth(), getHeight(), weekDayBackgroundPaint);

        if (currentScrollDirection == SCROLL_AXIS_VERTICAL)
        {
            currentCoordinate.y -= mDistanceY;

            if (currentCoordinate.y - mDistanceY > 0)
            {
                currentCoordinate.y = 0f;
            } else if (currentCoordinate.y - mDistanceY < -(spacingLinesBetweenHour * 23 - getHeight() + hourTextHeight * 2))
            {
                currentCoordinate.y = -(spacingLinesBetweenHour * 23 - getHeight() + hourTextHeight * 2);
            }
        } else if (currentScrollDirection == SCROLL_AXIS_HORIZONTAL)
        {
            currentCoordinate.x -= mDistanceX;
        }

        float startX = currentCoordinate.x + 8f;
        Log.e(TAG, Float.toString(currentCoordinate.y));
        startY = currentCoordinate.y + hourTextHeight;
        float width = (float) getWidth();
        float tableHeight = (float) (startY + spacingLinesBetweenHour * 23);

        for (int i = 0; i <= 23; i++)
        {
            canvas.drawText(Integer.toString(i), 0f, startY + (spacingLinesBetweenHour * i) + hourTextHeight / 2, weekDayHourTextPaint);
            canvas.drawLine(startX, startY + (spacingLinesBetweenHour * i), width, startY + (spacingLinesBetweenHour * i), weekDayHorizontalLinePaint);
        }

        for (int i = 0; i < 7; i++)
        {
            canvas.drawLine(startX + (spacingLinesBetweenDay * i), startY, startX + (spacingLinesBetweenDay * i), tableHeight, weekDayVerticalLinePaint);
        }
    }


     */
    private void drawDayView(Canvas canvas)
    {
        canvas.drawRect(0f, 0f, getWidth(), getHeight(), weekDayBackgroundPaint);

        if (currentScrollDirection == SCROLL_AXIS_VERTICAL)
        {
            currentCoordinate.y -= mDistanceY;

            if (currentCoordinate.y - mDistanceY > 0)
            {
                currentCoordinate.y = 0f;
            } else if (currentCoordinate.y - mDistanceY < -(spacingLinesBetweenHour * 23 - getHeight() + hourTextHeight * 2))
            {
                currentCoordinate.y = -(spacingLinesBetweenHour * 23 - getHeight() + hourTextHeight * 2);
            }
        } else if (currentScrollDirection == SCROLL_AXIS_HORIZONTAL)
        {
            currentCoordinate.x -= mDistanceX;
        }

        if (currentCoordinate.x <= -spacingLinesBetweenDay * 7 || currentCoordinate.x >= spacingLinesBetweenDay * 7)
        {
            currentCoordinate.x = 0f;
            currentCoordinate.y = lastCoordinate.y;
        }

        float lineStartX = currentCoordinate.x + spacingLinesBetweenDay + mDistanceX;
        float startX = currentCoordinate.x + spacingLinesBetweenDay;
        startY = currentCoordinate.y + hourTextHeight;
        float width = (float) getWidth();
        float tableHeight = (float) (startY + spacingLinesBetweenHour * 23);

        canvas.drawLine(spacingLinesBetweenDay, 0f, spacingLinesBetweenDay, tableHeight, weekDayVerticalLinePaint);


        for (int i = 0; i <= 23; i++)
        {
            canvas.drawText(Integer.toString(i), 0f, startY + (spacingLinesBetweenHour * i) + hourTextHeight / 2, weekDayHourTextPaint);
            canvas.drawLine(spacingLinesBetweenDay, startY + (spacingLinesBetweenHour * i), width, startY + (spacingLinesBetweenHour * i), weekDayHorizontalLinePaint);
        }

        for (int i = 0; i <= 7; i++)
        {
            canvas.drawLine(lineStartX + (spacingLinesBetweenDay * i), startY, lineStartX + (spacingLinesBetweenDay * i), tableHeight, weekDayVerticalLinePaint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_UP && areScrolling)
        {
            // 스크롤 후 손가락을 떼는 경우
            if (currentScrollDirection == SCROLL_AXIS_HORIZONTAL)
            {
                mDistanceX = 0;
                float dx = lastCoordinate.x - currentCoordinate.x;
                if (Math.abs(dx) < spacingLinesBetweenDay)
                {
                    // 스크롤 길이 부족으로 주 이동하지 않음
                    currentCoordinate.x = lastCoordinate.x;
                    currentCoordinate.y = lastCoordinate.y;
                } else
                {
                    Log.e(TAG, "lastCoordinate X : " + Float.toString(lastCoordinate.x));
                    // 다른 주로 넘어감
                    // dx<0인 경우 : 이전 주, dx>0인 경우 : 다음 주
                    int scrollDx = (int) (dx > 0 ? (-spacingLinesBetweenDay * 7) : (spacingLinesBetweenDay * 7));
                    overScroller.startScroll((int) lastCoordinate.x, (int) lastCoordinate.y, scrollDx, (int) lastCoordinate.y);
                }
            }
            areScrolling = false;
            currentScrollDirection = SCROLL_AXIS_NONE;
            ViewCompat.postInvalidateOnAnimation(WeekDayView.this);
        }
        return this.gestureDetector.onTouchEvent(event);
    }


    @Override
    public void computeScroll()
    {
        super.computeScroll();
        if (overScroller.computeScrollOffset())
        {
            currentCoordinate.x = overScroller.getCurrX();
            currentCoordinate.y = overScroller.getCurrY();
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

}