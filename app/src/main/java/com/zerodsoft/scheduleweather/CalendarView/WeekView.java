package com.zerodsoft.scheduleweather.CalendarView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
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
import com.zerodsoft.scheduleweather.Utility.Clock;

import java.util.Calendar;

public class WeekView extends View
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
    private int newScheduleRectColor;
    private int newScheduleRectThickness;
    private int hourTextHeight;
    private Rect hourTextBoxRect = new Rect();
    private int scheduleLayoutWidth;
    private int width;
    private float tableHeight = 0f;
    private int position;

    private Paint weekDayHourTextPaint;
    private Paint weekDayHorizontalLinePaint;
    private Paint weekDayVerticalLinePaint;
    private Paint weekDayBackgroundPaint;

    private OverScroller overScroller;
    private GestureDetectorCompat gestureDetector;
    public static PointF currentCoordinate = new PointF(0f, 0f);
    private PointF lastCoordinate = new PointF(0f, 0f);

    private float mDistanceY;
    private float startX = 0f;
    private float startY = 0f;

    private float minStartY;
    private float maxStartY;
    private int TABLE_LAYOUT_MARGIN;

    private Calendar startTime = Calendar.getInstance();
    private Calendar endTime = Calendar.getInstance();

    private DIRECTION currentScrollDirection = DIRECTION.NONE;

    enum DIRECTION
    {NONE, VERTICAL, CANCEL, FINISHED}

    private OnRefreshChildViewListener onRefreshChildViewListener;
    private OnRefreshHoursViewListener onRefreshHoursViewListener;

    public interface OnRefreshHoursViewListener
    {
        void refreshHoursView();
    }

    public interface OnRefreshChildViewListener
    {
        void refreshChildView();
    }

    public WeekView setOnRefreshChildViewListener(OnRefreshChildViewListener onRefreshChildViewListener)
    {
        this.onRefreshChildViewListener = onRefreshChildViewListener;
        return this;
    }

    public WeekView setOnRefreshHoursViewListener(OnRefreshHoursViewListener onRefreshHoursViewListener)
    {
        this.onRefreshHoursViewListener = onRefreshHoursViewListener;
        return this;
    }

    public WeekView(Context context, WeekViewPagerAdapter adapter, HoursView hoursView)
    {
        super(context);
        this.context = context;
        weekDayHourTextColor = Color.BLACK;
        weekDayHourTextSize = context.getResources().getDimensionPixelSize(R.dimen.weekview_textsize);
        weekDayViewBackgroundColor = Color.WHITE;
        weekDayViewLineThickness = context.getResources().getDimensionPixelSize(R.dimen.line_thickness);
        weekDayViewLineColor = Color.LTGRAY;
        newScheduleRectColor = Color.BLUE;
        newScheduleRectThickness = context.getResources().getDimensionPixelSize(R.dimen.new_schedule_line_thickness);
        TABLE_LAYOUT_MARGIN = weekDayHourTextSize;

        init();
    }

    public WeekView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WeekView, 0, 0);

        try
        {
            weekDayHourTextColor = a.getColor(R.styleable.WeekView_WeekDayHourTextColor, weekDayHourTextColor);
            weekDayHourTextSize = a.getDimensionPixelSize(R.styleable.WeekView_WeekDayHourTextSize, weekDayHourTextSize);
            weekDayViewBackgroundColor = a.getColor(R.styleable.WeekView_WeekDayViewBackgroundColor, weekDayViewBackgroundColor);
            weekDayViewLineThickness = a.getDimensionPixelSize(R.styleable.WeekView_WeekDayViewLineThickness, weekDayViewLineThickness);
            weekDayViewLineColor = a.getColor(R.styleable.WeekView_WeekDayViewLineColor, weekDayViewLineColor);
            newScheduleRectColor = a.getColor(R.styleable.WeekView_WeekDayViewNewScheduleRectColor, newScheduleRectColor);
            newScheduleRectThickness = a.getDimensionPixelSize(R.styleable.WeekView_WeekDayViewNewScheduleRectThickness, newScheduleRectThickness);
            TABLE_LAYOUT_MARGIN = weekDayHourTextSize;
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

            if (currentScrollDirection == DIRECTION.FINISHED)
            {
                currentScrollDirection = DIRECTION.NONE;
            } else if (currentScrollDirection == DIRECTION.NONE)
            {
                currentScrollDirection = DIRECTION.VERTICAL;
            }

            if (currentScrollDirection == DIRECTION.VERTICAL)
            {
                currentCoordinate.y -= distanceY;
                mDistanceY = distanceY;

                if (currentCoordinate.y >= maxStartY)
                {
                    currentCoordinate.y = maxStartY;
                } else if (currentCoordinate.y <= minStartY)
                {
                    currentCoordinate.y = minStartY;
                }
            }
            ViewCompat.postInvalidateOnAnimation(WeekView.this);
            onRefreshHoursViewListener.refreshHoursView();
            return true;

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            fling(velocityX, velocityY);
            return true;
        }

        private void fling(float velocityX, float velocityY)
        {
            overScroller.fling(0, (int) currentCoordinate.y, 0, (int) velocityY, 0, 0, (int) minStartY, (int) maxStartY, 0, 0);
            onRefreshHoursViewListener.refreshHoursView();
            ViewCompat.postInvalidateOnAnimation(WeekView.this);
        }

        @Override
        public boolean onDown(MotionEvent e)
        {
            lastCoordinate.y = currentCoordinate.y;
            Log.e(TAG, Float.toString(e.getX()));
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e)
        {
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e)
        {
            setStartTime(e.getX(), e.getY());
            Log.e(TAG, Clock.timeFormat2.format(getTime(e.getY()).getTime()));
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

        if (currentCoordinate.y == 0f)
        {
            currentCoordinate.y = (float) TABLE_LAYOUT_MARGIN;
        }
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
        onRefreshChildViewListener.refreshChildView();
    }


    @Override
    public void layout(int l, int t, int r, int b)
    {
        super.layout(l, t, r, b);
        width = getWidth();
        spacingLinesBetweenDay = width / 7;
        minStartY = -(spacingLinesBetweenHour * 24 + TABLE_LAYOUT_MARGIN * 2 - getHeight());
        maxStartY = TABLE_LAYOUT_MARGIN;
        tableHeight = (float) (spacingLinesBetweenHour * 24);
    }


    private void drawDayView(Canvas canvas)
    {
        canvas.drawRect(0f, 0f, getWidth(), getHeight(), weekDayBackgroundPaint);

        startX = currentCoordinate.x;
        startY = currentCoordinate.y;

        for (int i = 0; i <= 23; i++)
        {
            // 가로 선
            canvas.drawLine(0f, startY + (spacingLinesBetweenHour * i), width, startY + (spacingLinesBetweenHour * i), weekDayHorizontalLinePaint);
        }

        for (int i = 0; i <= 7; i++)
        {
            // 이번 주 세로 선
            canvas.drawLine(startX + (spacingLinesBetweenDay * i), startY - hourTextHeight, startX + (spacingLinesBetweenDay * i), startY + tableHeight, weekDayVerticalLinePaint);
        }
    }

    private Calendar getTime(float y)
    {
        float startHour, endHour;

        for (int i = 0; i <= 23; i++)
        {
            startHour = currentCoordinate.y + spacingLinesBetweenHour * i;
            endHour = currentCoordinate.y + spacingLinesBetweenHour * (i + 1);

            if (y >= startHour && y < endHour)
            {
                float minute15Height = (endHour - startHour) / 4f;
                y = y - startHour;

                for (int j = 0; j <= 3; j++)
                {
                    if (y >= minute15Height * j && y <= minute15Height * (j + 1))
                    {
                        int year = startTime.get(Calendar.YEAR), month = startTime.get(Calendar.MONTH), date = startTime.get(Calendar.DAY_OF_MONTH);
                        startTime.set(year, month, date, i, j * 15);
                        return startTime;
                    }
                }
            }
        }
        return startTime;
    }

    private void setStartTime(float x, float y)
    {
        for (int i = 0; i <= 6; i++)
        {
            if (x >= WeekHeaderView.coordinateInfos[i].getStartX() && x < WeekHeaderView.coordinateInfos[i].getEndX())
            {
                startTime = WeekHeaderView.coordinateInfos[i].getDate();
                break;
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_UP && currentScrollDirection == DIRECTION.VERTICAL)
        {
            currentScrollDirection = DIRECTION.FINISHED;
        }
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public void computeScroll()
    {
        super.computeScroll();
        if (overScroller.computeScrollOffset())
        {
            currentCoordinate.x = overScroller.getCurrX();
            currentCoordinate.y = overScroller.getCurrY();

            ViewCompat.postInvalidateOnAnimation(WeekView.this);
            onRefreshHoursViewListener.refreshHoursView();
        }
    }


    public WeekView setPosition(int position)
    {
        this.position = position;
        return this;
    }

    public int getPosition()
    {
        return position;
    }
}