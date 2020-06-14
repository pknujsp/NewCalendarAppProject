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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;

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
    private PointF currentCoordinate = new PointF(0f, 0f);
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

    public void setMoveWeekListener(MoveWeekListener moveWeekListener)
    {
        this.moveWeekListener = moveWeekListener;
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
            Log.e(TAG, "onScroll");

            if (overScroller.isFinished())
            {
                if (currentScrollDirection == DIRECTION.FINISHED)
                {
                    currentScrollDirection = DIRECTION.NONE;
                    Log.e(TAG, "DIRECTION FINISHED");
                } else if (currentScrollDirection == DIRECTION.NONE)
                {
                    Log.e(TAG, "DIRECTION NONE");
                    isScrolling = true;
                    currentScrollDirection = (Math.abs(distanceX) > Math.abs(distanceY)) ? DIRECTION.HORIZONTAL : DIRECTION.VERTICAL;

                    if (currentScrollDirection == DIRECTION.HORIZONTAL)
                    {
                        currentCoordinate.x -= distanceX;
                        mDistanceX = distanceX;

                        if (lastCoordinate.x - currentCoordinate.x > 0)
                        {
                            moveAmount++;
                        } else
                        {
                            moveAmount--;
                        }
                    }

                    return true;
                }
            }
            if (isScrolling)
            {
                if (currentScrollDirection == DIRECTION.HORIZONTAL)
                {
                    Log.e(TAG, "DIRECTION HORIZONTAL");
                    currentCoordinate.x -= distanceX;
                    mDistanceX = distanceX;
                } else if (currentScrollDirection == DIRECTION.VERTICAL)
                {
                    Log.e(TAG, "DIRECTION VERTICAL");
                    currentCoordinate.y -= distanceY;
                    mDistanceY = distanceY;
                }
                ViewCompat.postInvalidateOnAnimation(WeekView.this);
                return true;
            } else
            {
                return false;
            }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onDown(MotionEvent e)
        {
            if (overScroller.isFinished() && !isScrolling)
            {
                lastCoordinate.x = currentCoordinate.x;
                lastCoordinate.y = currentCoordinate.y;
                Log.e(TAG, "onDown");
                return true;
            } else
            {
                return false;
            }
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
            if (currentCoordinate.y > 0f)
            {
                currentCoordinate.y = 0f;
            } else if (currentCoordinate.y - mDistanceY < -(spacingLinesBetweenHour * 23 - getHeight() + hourTextHeight * 2))
            {
                currentCoordinate.y = -(spacingLinesBetweenHour * 23 - getHeight() + hourTextHeight * 2);
            }
        }

        startX = currentCoordinate.x + moveAmount * width;
        startY = currentCoordinate.y + hourTextHeight;
        tableHeight = (float) (startY + spacingLinesBetweenHour * 23);
        Log.e(TAG, "startX : " + Float.toString(startX));

        for (int i = 0; i <= 23; i++)
        {
            // 이전 주 시간
            canvas.drawText(Integer.toString(i), startX - width, startY + (spacingLinesBetweenHour * i) + hourTextHeight / 2, weekDayHourTextPaint);
            // 가로 선
            canvas.drawLine(startX + spacingLinesBetweenDay - width, startY + (spacingLinesBetweenHour * i), startX, startY + (spacingLinesBetweenHour * i), weekDayHorizontalLinePaint);

            // 이번 주 시간
            canvas.drawText(Integer.toString(i), startX, startY + (spacingLinesBetweenHour * i) + hourTextHeight / 2, weekDayHourTextPaint);
            // 가로 선
            canvas.drawLine(startX + spacingLinesBetweenDay, startY + (spacingLinesBetweenHour * i), startX + width, startY + (spacingLinesBetweenHour * i), weekDayHorizontalLinePaint);

            // 다음 주 시간
            canvas.drawText(Integer.toString(i), startX + width, startY + (spacingLinesBetweenHour * i) + hourTextHeight / 2, weekDayHourTextPaint);
            // 가로 선
            canvas.drawLine(startX + spacingLinesBetweenDay + width, startY + (spacingLinesBetweenHour * i), startX + width * 2, startY + (spacingLinesBetweenHour * i), weekDayHorizontalLinePaint);
        }

        for (int i = 1; i <= 8; i++)
        {
            // 이전 주 세로 선
            canvas.drawLine(startX + (spacingLinesBetweenDay * i) - width, startY - hourTextHeight, startX + (spacingLinesBetweenDay * i) - width, tableHeight, weekDayVerticalLinePaint);

            // 이번 주 세로 선
            canvas.drawLine(startX + (spacingLinesBetweenDay * i), startY - hourTextHeight, startX + (spacingLinesBetweenDay * i), tableHeight, weekDayVerticalLinePaint);

            // 다음 주 세로 선
            canvas.drawLine(startX + (spacingLinesBetweenDay * i) + width, startY - hourTextHeight, startX + (spacingLinesBetweenDay * i) + width, tableHeight, weekDayVerticalLinePaint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (!overScroller.isFinished())
        {
            Toast.makeText(context, "스크롤 중", Toast.LENGTH_SHORT).show();
        }
        if (event.getAction() == MotionEvent.ACTION_UP && (currentCoordinate.x != lastCoordinate.x || currentCoordinate.y != lastCoordinate.y))
        {
            // 스크롤 후 손가락을 떼는 경우
            if (currentScrollDirection == DIRECTION.HORIZONTAL)
            {
                isScrolling = false;
                currentScrollDirection = DIRECTION.FINISHED;
                mDistanceX = 0;
                float dx = lastCoordinate.x - currentCoordinate.x;

                if (Math.abs(dx) < spacingLinesBetweenDay)
                {
                    // 스크롤 길이 부족으로 주 이동하지 않음
                    currentCoordinate.x = lastCoordinate.x;
                    currentCoordinate.y = lastCoordinate.y;

                    if (dx > 0)
                    {
                        moveAmount--;
                    } else
                    {
                        moveAmount++;
                    }
                    Log.e(TAG, "onTouchEvent cancel");
                } else
                {
                    // 다른 주로 넘어감
                    // dx<0인 경우 : 이전 주, dx>0인 경우 : 다음 주
                    int scrollDx = (int) (dx > 0 ? -width : width);
                    overScroller.startScroll((int) lastCoordinate.x, (int) lastCoordinate.y, scrollDx, 0);
                    Log.e(TAG, "startScroll");

                    if (dx > 0)
                    {
                        // 헤더 뷰의 달력을 다음 주로 이동
                        moveWeekListener.moveWeek(1, lastCoordinate.x - spacingLinesBetweenDay);
                    } else
                    {
                        moveWeekListener.moveWeek(-1, lastCoordinate.x - spacingLinesBetweenDay);
                    }
                    Log.e(TAG, "SCROLL AMOUNT : " + Integer.toString(moveAmount));
                }
            } else if (currentScrollDirection == DIRECTION.VERTICAL)
            {
                currentScrollDirection = DIRECTION.FINISHED;
            }
            ViewCompat.postInvalidateOnAnimation(WeekView.this);
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
            Log.e(TAG, "computeScroll");
        }
    }
}