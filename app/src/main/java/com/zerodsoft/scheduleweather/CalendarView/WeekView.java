package com.zerodsoft.scheduleweather.CalendarView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.OverScroller;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;

import com.zerodsoft.scheduleweather.DayFragment;
import com.zerodsoft.scheduleweather.R;

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
    private int hourTextHeight;
    private Rect hourTextBoxRect = new Rect();
    private int scheduleLayoutWidth;
    private int width;
    private float tableHeight = 0f;

    private Paint weekDayHourTextPaint;
    private Paint weekDayHorizontalLinePaint;
    private Paint weekDayVerticalLinePaint;
    private Paint weekDayBackgroundPaint;

    private OverScroller overScroller;
    private GestureDetectorCompat gestureDetector;
    private static PointF currentCoordinate = new PointF(0f, 0f);
    private PointF lastCoordinate = new PointF(0f, 0f);

    private float mDistanceX;
    private float mDistanceY;
    private float startX = 0f;
    private float startY = 0f;
    private boolean isScrolling = false;


    private DIRECTION currentScrollDirection = DIRECTION.NONE;

    private MoveWeekListener moveWeekListener;
    private static int moveAmount = 0;

    enum DIRECTION
    {NONE, HORIZONTAL, VERTICAL, CANCEL, FINISHED}

    private WeekViewInterface weekViewInterface;
    private OnViewTouchListener onTouchListener;

    public interface WeekViewInterface
    {
        void refreshSideView(int position);
    }

    public interface OnViewTouchListener
    {
        int getPosition();
    }


    public void setMoveWeekListener(MoveWeekListener moveWeekListener)
    {
        this.moveWeekListener = moveWeekListener;
    }

    @SuppressLint("ResourceType")
    public WeekView(Context context, WeekViewPagerAdapter adapter, DayFragment dayFragment)
    {
        super(context);
        this.context = context;
        weekDayHourTextColor = Color.BLACK;
        weekDayHourTextSize = context.getResources().getDimensionPixelSize(R.dimen.weekview_textsize);
        weekDayViewBackgroundColor = Color.WHITE;
        weekDayViewLineThickness = context.getResources().getDimensionPixelSize(R.dimen.line_thickness);
        weekDayViewLineColor = Color.LTGRAY;

        init();
        this.weekViewInterface = adapter;
        this.onTouchListener = dayFragment;
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
        } finally
        {
            a.recycle();
        }
        init();

    }

    public interface MoveWeekListener
    {
        void moveWeek(int amount, float lastX);
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
                isScrolling = true;
                currentScrollDirection = DIRECTION.VERTICAL;

                return true;
            }

            if (currentScrollDirection == DIRECTION.VERTICAL)
            {
                currentCoordinate.y -= distanceY;
                mDistanceY = distanceY;
                weekViewInterface.refreshSideView(onTouchListener.getPosition());
            }
            ViewCompat.postInvalidateOnAnimation(WeekView.this);
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

        if (currentCoordinate.y == 0f)
        {
            currentCoordinate.y = (float) hourTextHeight;
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
    }

    @Override
    public void layout(int l, int t, int r, int b)
    {
        super.layout(l, t, r, b);
        scheduleLayoutWidth = getWidth() - getWidth() / 8;
        spacingLinesBetweenDay = scheduleLayoutWidth / 7;
        width = getWidth();
    }

    private void drawDayView(Canvas canvas)
    {
        canvas.drawRect(0f, 0f, getWidth(), getHeight(), weekDayBackgroundPaint);


        if (currentScrollDirection == DIRECTION.VERTICAL)
        {
            if (currentCoordinate.y >= 0f)
            {
                currentCoordinate.y = hourTextHeight;
            } else if (currentCoordinate.y <= -(spacingLinesBetweenHour * 23 + hourTextHeight * 2 - getHeight()))
            {
                currentCoordinate.y = -(spacingLinesBetweenHour * 23 + hourTextHeight * 2 - getHeight());
            }
        }

        startX = currentCoordinate.x;
        startY = currentCoordinate.y;
        tableHeight = (float) (startY + spacingLinesBetweenHour * 23);

        for (int i = 0; i <= 23; i++)
        {

            // 이번 주 시간
            canvas.drawText(Integer.toString(i), startX, startY + (spacingLinesBetweenHour * i) + hourTextHeight / 2, weekDayHourTextPaint);
            // 가로 선
            canvas.drawLine(startX + spacingLinesBetweenDay, startY + (spacingLinesBetweenHour * i), startX + width, startY + (spacingLinesBetweenHour * i), weekDayHorizontalLinePaint);
        }

        for (int i = 1; i <= 8; i++)
        {
            // 이번 주 세로 선
            canvas.drawLine(startX + (spacingLinesBetweenDay * i), startY - hourTextHeight, startX + (spacingLinesBetweenDay * i), tableHeight, weekDayVerticalLinePaint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (!overScroller.isFinished())
        {
            Toast.makeText(context, "스크롤 중", Toast.LENGTH_SHORT).show();
        }
        if (event.getAction() == MotionEvent.ACTION_UP && currentCoordinate.y != lastCoordinate.y)
        {
            if (currentScrollDirection == DIRECTION.VERTICAL)
            {
                currentScrollDirection = DIRECTION.FINISHED;
            }
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
        }
    }

    public void refreshView()
    {
        invalidate();
    }


    public static float getCurrentCoordinateY()
    {
        return currentCoordinate.y;
    }


}