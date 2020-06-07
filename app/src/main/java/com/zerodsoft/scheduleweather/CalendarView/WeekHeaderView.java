package com.zerodsoft.scheduleweather.CalendarView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;
import android.widget.Scroller;
import android.widget.Toast;

import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;

import com.zerodsoft.scheduleweather.CalendarView.Dto.ColumnData;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Utility.DateTimeInterpreter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class WeekHeaderView extends View
{
    private final static String TAG = "WeekHeaderView";
    private Context mContext;
    private Calendar mToday;
    private Calendar mFirstVisibleDay;
    private Calendar mLastVisibleDay;
    private Calendar mSelectedDay;

    private GestureDetectorCompat mGestureDetector;
    private OverScroller mStickyScroller;
    private DateTimeInterpreter mDateTimeInterpreter;

    private PointF mCurrentOrigin = new PointF(0f, 0f);
    private PointF mLastOrigin = new PointF(0F, 0F);
    private Paint mHeaderBackgroundPaint;
    private Paint mHeaderDateNormalPaint;
    private Paint mHeaderDateTodayPaint;
    private Paint mHeaderDayNormalPaint;
    private Paint mHeaderDayTodayPaint;
    private Paint mHeaderWeekPaint;
    private Paint circlePaint = new Paint();
    private int mHeaderDateHeight;
    private int mHeaderDayHeight;
    private int mHeaderScheduleHeight;

    private float mDistanceX = 0;

    // Attributes and their default values.
    private int mHeaderHeight;
    private int mHeaderWidthPerDay;
    private int mFirstDayOfWeek = Calendar.SUNDAY;
    private int mHeaderAllBackgroundColor;
    private int mHeaderDateBackgroundColor;
    private int mHeaderDateTextColor;
    private int mHeaderDateTextSize;
    private int mHeaderDayBackgroundColor;
    private int mHeaderDayTextColor;
    private int mHeaderDayTextSize;
    private int mHeaderExtraBackgroundColor;
    private int mHeaderExtraButtonSize;
    private int mHeaderExtraTextColor;
    private int mHeaderExtraTextSize;
    private int mHeaderRowMarginBottom;
    private int mHeaderRowMarginTop;
    private int mHeaderTodayTextColor;
    private int mHeaderWeekBackgroundColor;
    private int mHeaderWeekTextColor;
    private int mHeaderWeekTextSize;
    private int mXScrollingSpeed = 1;
    private int mHeaderWeekWidth = 0;

    private ColumnData[] columnPoints = new ColumnData[7];

    private boolean mIsFirstDraw = true;
    private boolean mAreHeaderScrolling = false;

    //    Interface
    private ScrollListener mScrollListener;
    private DateSelectedChangeListener mDateSelectedChangeListener;

    private final GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener()
    {
        @Override
        public boolean onDown(MotionEvent e)
        {
            // 터치한 위치의 좌표값 저장
            mLastOrigin.x = mCurrentOrigin.x;
            mLastOrigin.y = mCurrentOrigin.y;
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            // 스크롤 시 onDraw()를 호출
            mAreHeaderScrolling = true;
            mDistanceX = distanceX * mXScrollingSpeed;
            ViewCompat.postInvalidateOnAnimation(WeekHeaderView.this);
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e)
        {
            Calendar newSelectedDay = getDateFromPoint(e.getX(), e.getY());
            SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd", Locale.getDefault());
            if (null != newSelectedDay)
                Toast.makeText(mContext, "time =====" + format.format(newSelectedDay.getTime()), Toast.LENGTH_LONG).show();
            if (null != newSelectedDay && mDateSelectedChangeListener != null)
            {
                mDateSelectedChangeListener.onDateSelectedChange(mSelectedDay, newSelectedDay);
            }
            mSelectedDay = (Calendar) newSelectedDay.clone();
            ViewCompat.postInvalidateOnAnimation(WeekHeaderView.this);
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e)
        {
            super.onLongPress(e);
        }
    };


    public WeekHeaderView(Context context)
    {
        this(context, null);
    }

    public WeekHeaderView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public WeekHeaderView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        mContext = context;

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WeekHeaderView, 0, 0);
        try
        {
            mFirstDayOfWeek = a.getInteger(R.styleable.WeekHeaderView_firstDayOfWeek2, mFirstDayOfWeek);
            mHeaderAllBackgroundColor = a.getColor(R.styleable.WeekHeaderView_headerAllBackgroundColor, mHeaderAllBackgroundColor);
            mHeaderDateBackgroundColor = a.getColor(R.styleable.WeekHeaderView_headerDateBackgroundColor, mHeaderDateBackgroundColor);
            mHeaderDateTextColor = a.getColor(R.styleable.WeekHeaderView_headerDateTextColor, mHeaderDateTextColor);
            mHeaderDateTextSize = a.getDimensionPixelSize(R.styleable.WeekHeaderView_headerDateTextSize, mHeaderDateTextSize);

            mHeaderDayBackgroundColor = a.getColor(R.styleable.WeekHeaderView_headerDayBackgroundColor, mHeaderDayBackgroundColor);
            mHeaderDayTextColor = a.getColor(R.styleable.WeekHeaderView_headerDayTextColor, mHeaderDayTextColor);
            mHeaderDayTextSize = a.getDimensionPixelSize(R.styleable.WeekHeaderView_headerDayTextSize, mHeaderDayTextSize);

            mHeaderExtraBackgroundColor = a.getColor(R.styleable.WeekHeaderView_headerExtraBackgroundColor, mHeaderExtraBackgroundColor);
            mHeaderExtraButtonSize = a.getDimensionPixelSize(R.styleable.WeekHeaderView_headerExtraButtonSize, mHeaderExtraButtonSize);
            mHeaderExtraTextColor = a.getColor(R.styleable.WeekHeaderView_headerExtraTextColor, mHeaderExtraTextColor);
            mHeaderExtraTextSize = a.getDimensionPixelSize(R.styleable.WeekHeaderView_headerExtraTextSize, mHeaderExtraTextSize);

            mHeaderRowMarginBottom = a.getDimensionPixelOffset(R.styleable.WeekHeaderView_headerRowMarginBottom, mHeaderRowMarginBottom);
            mHeaderRowMarginTop = a.getDimensionPixelOffset(R.styleable.WeekHeaderView_headerRowMarginTop, mHeaderRowMarginTop);

            mHeaderTodayTextColor = a.getColor(R.styleable.WeekHeaderView_headerTodayTextColor, mHeaderTodayTextColor);
            mHeaderWeekBackgroundColor = a.getColor(R.styleable.WeekHeaderView_headerWeekBackgroundColor, mHeaderWeekBackgroundColor);
            mHeaderWeekTextColor = a.getColor(R.styleable.WeekHeaderView_headerWeekTextColor, mHeaderWeekTextColor);
            mHeaderWeekTextSize = a.getDimensionPixelSize(R.styleable.WeekHeaderView_headerWeekTextSize, mHeaderWeekTextSize);
        } finally
        {
            a.recycle();
        }

        init();
    }

    private void init()
    {
        //Get the date today
        mToday = Calendar.getInstance();
        mToday.set(Calendar.HOUR_OF_DAY, 0);
        mToday.set(Calendar.MINUTE, 0);
        mToday.set(Calendar.SECOND, 0);


        mSelectedDay = (Calendar) mToday.clone();

        // Scrolling initialization.
        mGestureDetector = new GestureDetectorCompat(mContext, mGestureListener);
        mStickyScroller = new OverScroller(mContext);

        //prepare paint
        mHeaderBackgroundPaint = new Paint();
        mHeaderBackgroundPaint.setColor(mHeaderAllBackgroundColor);

        mHeaderWeekPaint = new Paint();
        mHeaderWeekPaint.setColor(mHeaderWeekTextColor);
        mHeaderWeekPaint.setTextSize(mHeaderWeekTextSize);

        Rect rect = new Rect();
        mHeaderDateNormalPaint = new Paint();
        mHeaderDateNormalPaint.setColor(mHeaderDateTextColor);
        mHeaderDateNormalPaint.setTextSize(mHeaderDateTextSize);
        mHeaderDateNormalPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mHeaderDateNormalPaint.setTextAlign(Paint.Align.CENTER);
        mHeaderDateNormalPaint.getTextBounds("10", 0, 2, rect);
        mHeaderDateHeight = rect.height();

        mHeaderDateTodayPaint = new Paint();
        mHeaderDateTodayPaint.setColor(mHeaderTodayTextColor);
        mHeaderDateTodayPaint.setTextSize(mHeaderDateTextSize);
        mHeaderDateTodayPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mHeaderDateTodayPaint.setTextAlign(Paint.Align.CENTER);

        mHeaderDayNormalPaint = new Paint();
        mHeaderDayNormalPaint.setColor(mHeaderDayTextColor);
        mHeaderDayNormalPaint.setTextSize(mHeaderDayTextSize);
        mHeaderDayNormalPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mHeaderDayNormalPaint.setTextAlign(Paint.Align.CENTER);
        mHeaderDayNormalPaint.getTextBounds("일", 0, "일".length(), rect);
        mHeaderDayHeight = rect.height();

        mHeaderHeight = mHeaderDayHeight + mHeaderDateHeight + mHeaderRowMarginTop * 2;

        circlePaint.setColor(Color.BLACK);
        circlePaint.setStrokeWidth(3);
        circlePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, mHeaderHeight);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawHeader(canvas);
    }

    //Draw date label and day label.
    private void drawHeader(Canvas canvas)
    {
        // 열 사이의 간격 (주차, 일, 월~토)
        mHeaderWidthPerDay = getWidth() / 8;
        // 주차를 표시할 공간을 확보
        mHeaderWeekWidth = mHeaderWidthPerDay;

        if (mIsFirstDraw)
        {
            // 처음 onDraw()가 호출된 경우 (앱 시작 직후)
            mIsFirstDraw = false;
            // 자동으로 오늘 날짜를 선택함
            mSelectedDay = (Calendar) mToday.clone();

            // mFirstDayOfWeek : 1~7까지(일~토), mToday.get(Calendar.DAY_OF_WEEK) : mFirstDayOfWeek와 동일
            if (mToday.get(Calendar.DAY_OF_WEEK) != mFirstDayOfWeek)
            {
                // 오늘이 월요일(2)인데 첫주가 일요일(1)인 경우에만 수행
                // 위의 경우 difference가 1이다
                int difference = (7 + (mToday.get(Calendar.DAY_OF_WEEK) - mFirstDayOfWeek)) % 7;
                // 오늘 날짜의 위치를 파악
                mCurrentOrigin.x += (mHeaderWidthPerDay) * difference + mHeaderWeekWidth;
            }
        }
        if (mAreHeaderScrolling)
        {
            // 스크롤 중인 경우
            mCurrentOrigin.x -= mDistanceX;
        }
        // 일요일(맨앞)과 오늘의 일수 차이 계산
        int leftDaysWithGaps = (int) -(Math.ceil((mCurrentOrigin.x - mHeaderWeekWidth) / mHeaderWidthPerDay));
        // 시작 픽셀(일요일 부터 시작)
        final float startPixel = mCurrentOrigin.x + mHeaderWidthPerDay * leftDaysWithGaps;

        // 오늘 날짜를 찾음
        Calendar date = (Calendar) mToday.clone();
        date.add(Calendar.HOUR, 6);

        // 한 주의 달력을 생성
        Calendar oldFirstVisibleDay = mFirstVisibleDay;
        mFirstVisibleDay = (Calendar) mToday.clone();
        // 일요일로 이동
        mFirstVisibleDay.add(Calendar.DATE, leftDaysWithGaps);
        mLastVisibleDay = (Calendar) mFirstVisibleDay.clone();
        // 토요일로 이동
        mLastVisibleDay.add(Calendar.DATE, 6);
        if (!mFirstVisibleDay.equals(oldFirstVisibleDay) && mScrollListener != null)
        {
            mScrollListener.onFirstVisibleDayChanged(mFirstVisibleDay, oldFirstVisibleDay);
        }
        // 헤더의 배경을 그림
        canvas.drawRect(0, 0, getWidth(), mHeaderHeight, mHeaderBackgroundPaint);

        mFirstDayOfWeek = (mFirstDayOfWeek > Calendar.SATURDAY || mFirstDayOfWeek < Calendar.SUNDAY) ? Calendar.SUNDAY : mFirstDayOfWeek;
        int dayOfWeek;
        // 요일을 그림
        for (int i = mFirstDayOfWeek; i < mFirstDayOfWeek + 7; i++)
        {
            dayOfWeek = i % 7;
            String weekLabel = getDateTimeInterpreter().interpretDay(dayOfWeek == 0 ? 7 : dayOfWeek);
            if (weekLabel == null)
            {
                throw new IllegalStateException("A DateTimeInterpreter must not return null date");
            }
            canvas.drawText(weekLabel, mHeaderWidthPerDay / 2 + (mHeaderWidthPerDay) * (i - mFirstDayOfWeek) + mHeaderWeekWidth, mHeaderDayHeight + mHeaderRowMarginTop, mHeaderDateNormalPaint);

        }

        // 날짜를 그림
        for (int index = 0, dateNum = leftDaysWithGaps; index <= 6; index++)
        {
            // 날짜가 오늘인지 확인
            date = (Calendar) mToday.clone();
            // 일요일로 이동
            date.add(Calendar.DATE, dateNum);

            boolean isToday = isSameDay(date, mToday);
            boolean selectedDay = isSameDay(date, mSelectedDay);
            // 표시할 날짜를 가져옴
            String dateLabel = getDateTimeInterpreter().interpretDate(date);

            float x = startPixel + mHeaderWeekWidth + mHeaderWidthPerDay / 2 + mHeaderWidthPerDay * index;
            float y = mHeaderDayHeight + mHeaderRowMarginTop * 2 + mHeaderDateHeight;

            if (dateLabel == null)
            {
                throw new IllegalStateException("A DateTimeInterpreter must not return null date");
            }
            if (selectedDay)
            {
                if (isToday)
                {
                    canvas.drawText(dateLabel, x, y, mHeaderDateTodayPaint);
                } else
                {
                    canvas.drawText(dateLabel, x, y, mHeaderDateNormalPaint);
                }
                canvas.drawCircle(x, y - mHeaderDateHeight / 2, 32, circlePaint);
            } else
            {
                canvas.drawText(dateLabel, x, y, isToday ? mHeaderDateTodayPaint : mHeaderDateNormalPaint);
            }

            dateNum++;
        }
    }

    private void drawSchedules(Canvas canvas)
    {

    }

    private void drawScheduleRect(Canvas canvas, float x, float y, int length)
    {
    }


    private Calendar getDateFromPoint(float x, float y)
    {
        int leftDaysWithGaps = (int) -(Math.ceil((mCurrentOrigin.x - mHeaderWeekWidth) / (mHeaderWidthPerDay)));
        float startPixel = mCurrentOrigin.x + (mHeaderWidthPerDay) * leftDaysWithGaps;

        for (int dayNumber = leftDaysWithGaps + 1;
             dayNumber <= leftDaysWithGaps + 8;
             dayNumber++)
        {
            float start = startPixel;
            if (mHeaderWidthPerDay + startPixel - start > 0
                    && x > start && x < startPixel + mHeaderWidthPerDay)
            {
                Calendar day = (Calendar) mToday.clone();
                day.add(Calendar.DATE, dayNumber - 1);
                return day;
            }
            startPixel += mHeaderWidthPerDay;
        }
        return null;
    }


    /**
     * Get the interpreter which provides the text to show in the header column and the header row.
     *
     * @return The date, time interpreter.
     */
    public DateTimeInterpreter getDateTimeInterpreter()
    {
        if (mDateTimeInterpreter == null)
        {
            mDateTimeInterpreter = new DateTimeInterpreter()
            {
                final String[] weekLabels = {"일", "월", "화", "수", "목", "금", "토"};

                @Override
                public String interpretDate(Calendar date)
                {
                    SimpleDateFormat format = new SimpleDateFormat("dd", Locale.getDefault());
                    String day = format.format(date.getTime());
                    return day;
                }

                @Override
                public String interpretTime(int hour)
                {
                    return null;
                }

                @Override
                public String interpretDay(int dayofweek)
                {
                    if (dayofweek > 7 || dayofweek < 1)
                    {
                        return null;
                    }
                    return weekLabels[dayofweek - 1];
                }
            };
        }
        return mDateTimeInterpreter;
    }

    /////////////////////////////////////////////////////////////////
    //
    //      Functions related to scrolling.
    //
    /////////////////////////////////////////////////////////////////


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mStickyScroller.computeScrollOffset())
        {
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            mAreHeaderScrolling = false;
            mDistanceX = 0;
            float dx = mLastOrigin.x - mCurrentOrigin.x;
            if (Math.abs(dx) < 1 * mHeaderWidthPerDay)
            {
                mCurrentOrigin.x = mLastOrigin.x;
                mCurrentOrigin.y = mLastOrigin.y;
            } else
            {
                // 다른 주로 넘어감
                // dx<0인 경우 : 이전 주, dx>0인 경우 : 다음 주
                int scrollDx = (int) (dx > 0 ? (-mHeaderWidthPerDay * 7 + dx) : (mHeaderWidthPerDay * 7 + dx));
                mSelectedDay.add(Calendar.DATE, dx > 0 ? 7 : -7);
                mStickyScroller.startScroll((int) mCurrentOrigin.x, 0, scrollDx + mHeaderWeekWidth, 0);
            }
            ViewCompat.postInvalidateOnAnimation(WeekHeaderView.this);
            return mGestureDetector.onTouchEvent(event);
        }
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public void computeScroll()
    {
        super.computeScroll();
        if (mStickyScroller.computeScrollOffset())
        {
            mCurrentOrigin.x = mStickyScroller.getCurrX();
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /////////////////////////////////////////////////////////////////
    //
    //      Functions related to setting and getting the properties.
    //
    /////////////////////////////////////////////////////////////////


    public ScrollListener getScrollListener()
    {
        return mScrollListener;
    }

    public void setScrollListener(ScrollListener mScrollListener)
    {
        this.mScrollListener = mScrollListener;
        invalidate();
    }

    public Calendar getSelectedDay()
    {
        return mSelectedDay;
    }


    public DateSelectedChangeListener getDateSelectedChangeListener()
    {
        return mDateSelectedChangeListener;
    }

    public void setDateSelectedChangeListener(DateSelectedChangeListener dateSelectedChangeListener)
    {
        this.mDateSelectedChangeListener = dateSelectedChangeListener;
    }


    public void setSelectedDay(Calendar selectedDay)
    {
        mStickyScroller.forceFinished(true);
        selectedDay.set(Calendar.HOUR_OF_DAY, 0);
        selectedDay.set(Calendar.MINUTE, 0);
        selectedDay.set(Calendar.SECOND, 0);
        Log.d(TAG, "setSelectedDay ==" + selectedDay.get(Calendar.DAY_OF_MONTH));
        this.mSelectedDay = (Calendar) selectedDay.clone();
        if (mSelectedDay.equals(mFirstVisibleDay) || mSelectedDay.equals(mLastVisibleDay))
        {
            ViewCompat.postInvalidateOnAnimation(this);
            return;
        }
        if (mSelectedDay.before(mFirstVisibleDay))
        {
            int intervalDay;
            if (mFirstVisibleDay.get(Calendar.YEAR) == mSelectedDay.get(Calendar.YEAR))
            {
                intervalDay = mFirstVisibleDay.get(Calendar.DAY_OF_YEAR) - mSelectedDay.get(Calendar.DAY_OF_YEAR);
            } else
            {
                intervalDay = Math.round((mFirstVisibleDay.getTimeInMillis() - mSelectedDay.getTimeInMillis()) / (1000 * 60 * 60 * 24));
            }
            Log.d(TAG, "   before    interval Day  ==" + intervalDay);
            if (intervalDay <= 7 && intervalDay > 0)
            {
                mStickyScroller.startScroll((int) mCurrentOrigin.x, 0, 7 * mHeaderWidthPerDay, 0);
            } else
            {
                mCurrentOrigin.x = (float) (mCurrentOrigin.x + Math.ceil(intervalDay / 7) * mHeaderWidthPerDay);
            }
        }
        if (mSelectedDay.after(mLastVisibleDay))
        {
            int intervalDay;
            if (mSelectedDay.get(Calendar.YEAR) == mLastVisibleDay.get(Calendar.YEAR))
            {
                intervalDay = mSelectedDay.get(Calendar.DAY_OF_YEAR) - mLastVisibleDay.get(Calendar.DAY_OF_YEAR);
            } else
            {
                intervalDay = Math.round((mSelectedDay.getTimeInMillis() - mLastVisibleDay.getTimeInMillis()) / (1000 * 60 * 60 * 24));
            }
            Log.d(TAG, "  after     interval Day  ==" + intervalDay);
            if (intervalDay <= 7 && intervalDay > 0)
            {
                mStickyScroller.startScroll((int) mCurrentOrigin.x, 0, -7 * mHeaderWidthPerDay, 0);
            } else
            {
                mCurrentOrigin.x = (float) (mCurrentOrigin.x - Math.ceil(intervalDay / 7) * mHeaderWidthPerDay);
            }
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /////////////////////////////////////////////////////////////////
    //
    //      Public methods.
    //
    /////////////////////////////////////////////////////////////////

    /**
     * Go to next week
     */
    public void goToNextWeek()
    {
        Calendar nextWeek = (Calendar) mLastVisibleDay.clone();
        nextWeek.add(Calendar.DATE, 7);
        setSelectedDay(nextWeek);
    }

    /**
     * Go to last week
     */
    public void goToLastWeek()
    {
        Calendar lastWeek = (Calendar) mFirstVisibleDay.clone();
        lastWeek.add(Calendar.DATE, -7);
        setSelectedDay(lastWeek);
    }

    ////////////////////////////////////////////////////////////////
    //
    //       InterFace
    //
    ///////////////////////////////////////////////////////////////
    public interface ScrollListener
    {
        /**
         * Called when the first visible day has changed.
         * <p/>
         * (this will also be called during the first draw of the WeekHeaderView)
         *
         * @param newFirstVisibleDay The new first visible day
         * @param oldFirstVisibleDay The old first visible day (is null on the first call).
         */
        public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay);
    }

    public interface DateSelectedChangeListener
    {
        public void onDateSelectedChange(Calendar oldSelectedDay, Calendar newSelectedDay);
    }

    //////////////////////////////////////////////////////////////
    //
    //        Helper Methods
    //
    /////////////////////////////////////////////////////////////

    /**
     * Checks if an integer array contains a particular value.
     *
     * @param list  The haystack.
     * @param value The needle.
     * @return True if the array contains the value. Otherwise returns false.
     */
    private boolean containsValue(int[] list, int value)
    {
        for (int i = 0; i < list.length; i++)
        {
            if (list[i] == value)
                return true;
        }
        return false;
    }

    /**
     * Checks if two times are on the same day.
     *
     * @param dayOne The first day.
     * @param dayTwo The second day.
     * @return Whether the times are on the same day.
     */
    private boolean isSameDay(Calendar dayOne, Calendar dayTwo)
    {
        return dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR) && dayOne.get(Calendar.DAY_OF_YEAR) == dayTwo.get(Calendar.DAY_OF_YEAR);
    }

}
