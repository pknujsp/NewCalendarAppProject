package com.zerodsoft.scheduleweather.CalendarView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;

import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;

import com.zerodsoft.scheduleweather.CalendarView.Dto.CoordinateInfo;
import com.zerodsoft.scheduleweather.DayFragment;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Utility.DateHour;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class WeekHeaderView extends View implements WeekView.CoordinateInfoInterface
{
    private final static String TAG = "WeekHeaderView";
    private Context mContext;
    public static Calendar today = Calendar.getInstance();
    private Calendar mSelectedDay;
    private Calendar sunday;
    private int position;

    private PointF mCurrentOrigin = new PointF(0f, 0f);
    private PointF mLastOrigin = new PointF(0F, 0F);
    private Paint mHeaderBackgroundPaint;
    private Paint mHeaderDateNormalPaint;
    private Paint mHeaderDateTodayPaint;
    private Paint mHeaderDayNormalPaint;
    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint dividingPaint;
    private int mHeaderDateHeight;
    private int mHeaderDayHeight;

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

    private boolean firstDraw = true;

    private WeekView.OnRefreshChildViewListener onRefreshChildViewListener;
    private CoordinateInfo[] coordinateInfos = new CoordinateInfo[7];
    public static int selectedDayPosition = -1;

    public WeekHeaderView setOnRefreshChildViewListener(WeekView.OnRefreshChildViewListener onRefreshChildViewListener)
    {
        this.onRefreshChildViewListener = onRefreshChildViewListener;
        return this;
    }

    @Override
    public CoordinateInfo[] getArray()
    {
        return coordinateInfos;
    }



    public WeekHeaderView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
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
        mHeaderBackgroundPaint = new Paint();
        mHeaderBackgroundPaint.setColor(mHeaderAllBackgroundColor);

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

        mHeaderHeight = mHeaderDayHeight + mHeaderDateHeight + mHeaderRowMarginTop * 3;

        circlePaint.setColor(Color.GRAY);
        circlePaint.setStrokeWidth(3);
        circlePaint.setStyle(Paint.Style.STROKE);

        dividingPaint = new Paint();
        dividingPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(widthMeasureSpec, mHeaderHeight);
    }

    @Override
    public void layout(int l, int t, int r, int b)
    {
        super.layout(l, t, r, b);
        //   mToday.set(Calendar.HOUR_OF_DAY, 0);
        //   mToday.set(Calendar.MINUTE, 0);
        //   mToday.set(Calendar.SECOND, 0);

        sunday = (Calendar) today.clone();
        sunday.add(Calendar.WEEK_OF_YEAR, (position - DayFragment.FIRST_VIEW_NUMBER));

        mSelectedDay = (Calendar) sunday.clone();

        mHeaderWidthPerDay = getWidth() / 7;
        for (int i = 0; i <= 6; i++)
        {
            coordinateInfos[i] = new CoordinateInfo().setDate(null).setStartX(mHeaderWidthPerDay * i)
                    .setEndX(mHeaderWidthPerDay * (i + 1));
        }
        // mFirstDayOfWeek : 1~7까지(일~토), mToday.get(Calendar.DAY_OF_WEEK) : mFirstDayOfWeek와 동일
        if (sunday.get(Calendar.DAY_OF_WEEK) != mFirstDayOfWeek)
        {
            // 오늘이 월요일(2)인데 첫주가 일요일(1)인 경우에만 수행
            // 위의 경우 difference가 1이다
            int difference = (sunday.get(Calendar.DAY_OF_WEEK) - mFirstDayOfWeek) % 7;
            // 오늘 날짜의 위치를 파악
            mCurrentOrigin.x = mHeaderWidthPerDay * difference;
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawHeaderView(canvas);
        canvas.drawLine(0f, getHeight() - 1, getWidth(), getHeight() - 1, dividingPaint);
    }

    private void drawHeaderView(Canvas canvas)
    {
        // 헤더의 배경을 그림
        canvas.drawRect(0, 0, getWidth(), mHeaderHeight, mHeaderBackgroundPaint);

        // 일요일(맨앞)과 오늘의 일수 차이 계산
        final int leftDaysWithGaps = (int) -(Math.ceil((mCurrentOrigin.x) / mHeaderWidthPerDay));
        // 시작 픽셀(일요일 부터 시작)
        final float startPixel = mCurrentOrigin.x + mHeaderWidthPerDay * leftDaysWithGaps;

        // 요일을 그림
        for (int i = 0; i < 7; i++)
        {
            canvas.drawText(DateHour.getDayString(i), mHeaderWidthPerDay / 2 + mHeaderWidthPerDay * i, mHeaderDayHeight + mHeaderRowMarginTop, mHeaderDateNormalPaint);
        }

        if (!firstDraw)
        {
            sunday.add(Calendar.DATE, -7);
        } else
        {
            // 일요일로 이동
            sunday.add(Calendar.DATE, leftDaysWithGaps);
            if (selectedDayPosition == -1)
            {
                selectedDayPosition = -leftDaysWithGaps;
            }
            firstDraw = false;
        }

        for (int i = 0; i < 7; i++)
        {
            coordinateInfos[i].setDate(sunday);
            sunday.add(Calendar.DATE, 1);
        }

        sunday.add(Calendar.DATE, -7);

        // 날짜를 그림
        for (int i = 0; i <= 6; i++)
        {
            boolean isToday = isSameDay(sunday, today);
            boolean selectedDay = false;

            if (i == selectedDayPosition)
            {
                selectedDay = true;
            }


            // 표시할 날짜를 가져옴
            String dateLabel = DateHour.getDate(sunday);

            float x = startPixel + mHeaderWidthPerDay / 2 + mHeaderWidthPerDay * i;
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
            sunday.add(Calendar.DATE, 1);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            for (int i = 0; i < coordinateInfos.length; i++)
            {
                if (event.getX() >= coordinateInfos[i].getStartX() && event.getX() < coordinateInfos[i].getEndX())
                {
                    selectDay(i);
                    break;
                }
            }
            onRefreshChildViewListener.refreshChildView();
            ViewCompat.postInvalidateOnAnimation(this);
        }
        return true;
    }

    private void selectDay(int position)
    {
        mSelectedDay = (Calendar) coordinateInfos[position].getDate().clone();
        selectedDayPosition = position;
    }

    private boolean isSameDay(Calendar dayOne, Calendar dayTwo)
    {
        return dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR) && dayOne.get(Calendar.DAY_OF_YEAR) == dayTwo.get(Calendar.DAY_OF_YEAR);
    }

    public WeekHeaderView setPosition(int position)
    {
        this.position = position;
        return this;
    }

    public int getPosition()
    {
        return position;
    }
}
