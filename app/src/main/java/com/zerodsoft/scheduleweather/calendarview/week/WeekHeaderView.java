package com.zerodsoft.scheduleweather.calendarview.week;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.zerodsoft.scheduleweather.AppMainActivity;
import com.zerodsoft.scheduleweather.calendarview.dto.CoordinateInfo;
import com.zerodsoft.scheduleweather.calendarfragment.WeekFragment;
import com.zerodsoft.scheduleweather.calendarview.EventDrawingInfo;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.utility.DateHour;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class WeekHeaderView extends View implements WeekView.CoordinateInfoInterface
{
    public final static String TAG = "WeekHeaderView";
    private Context mContext;
    private static final Calendar today = Calendar.getInstance();

    private Calendar weekFirstDay;
    private Calendar weekLastDay;
    private int position;

    // 일요일(맨앞)과 오늘의 일수 차이 계산
    private int leftDaysWithGaps;
    // 시작 픽셀(일요일 부터 시작)
    private float startPixel;

    private PointF mCurrentOrigin = new PointF(0f, 0f);
    private PointF eventsStartCoordinate = new PointF(0f, 0f);
    private Paint mHeaderBackgroundPaint;
    private Paint mHeaderDateNormalPaint;
    private Paint mHeaderDateTodayPaint;
    private Paint mHeaderDayNormalPaint;
    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint dividingPaint;
    private int mHeaderDateHeight;
    private int mHeaderDayHeight;

    private int mHeaderHeight;
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
    private int spacingLineHeight;
    private int spacingBetweenEvent;
    private int weekHeaderEventTextSize;
    private int weekHeaderEventBoxHeight;

    private boolean isFirstDraw = true;
    private boolean haveEvents = false;

    private CoordinateInfo[] coordinateInfos = new CoordinateInfo[8];
    private int eventRowNum;

    private Date weekFirstDate;
    private Date weekLastDate;

    private List<ScheduleDTO> scheduleList;

    private boolean[][] eventMatrix = new boolean[EVENT_ROW_MAX][7];
    private List<EventDrawingInfo> eventDrawingInfoList = new ArrayList<>();

    private static final int EVENT_ROW_MAX = 20;
    private ViewHeightChangeListener viewHeightChangeListener;

    public interface ViewHeightChangeListener
    {
        void onHeightChanged(int height);
    }

    public void setViewHeightChangeListener(ViewHeightChangeListener viewHeightChangeListener)
    {
        this.viewHeightChangeListener = viewHeightChangeListener;
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

            weekHeaderEventTextSize = context.getResources().getDimensionPixelSize(R.dimen.week_header_view_day_event_text_size);
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

        spacingLineHeight = rect.height();

        mHeaderHeight = mHeaderDayHeight + mHeaderDateHeight + mHeaderRowMarginTop * 3;

        circlePaint.setColor(Color.GRAY);
        circlePaint.setStrokeWidth(3);
        circlePaint.setStyle(Paint.Style.STROKE);

        dividingPaint = new Paint();
        dividingPaint.setColor(Color.BLACK);

        Paint eventBoxPaint = new Paint();
        eventBoxPaint.setTextSize(weekHeaderEventTextSize);
        eventBoxPaint.getTextBounds("1", 0, 1, rect);
        weekHeaderEventBoxHeight = rect.height();

        spacingBetweenEvent = 2;
    }

    public int getmHeaderHeight()
    {
        return mHeaderHeight;
    }

    @Override
    public void requestLayout()
    {
        super.requestLayout();
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
    }

    private void setInitialData()
    {
        weekFirstDay = (Calendar) today.clone();
        weekFirstDay.set(weekFirstDay.get(Calendar.YEAR), weekFirstDay.get(Calendar.MONTH), weekFirstDay.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        weekFirstDay.add(Calendar.WEEK_OF_YEAR, (position - WeekViewPagerAdapter.FIRST_VIEW_NUMBER));

        // mFirstDayOfWeek : 1~7까지(일~토), mToday.get(Calendar.DAY_OF_WEEK) : mFirstDayOfWeek와 동일
        if (weekFirstDay.get(Calendar.DAY_OF_WEEK) != mFirstDayOfWeek)
        {
            // 오늘이 월요일(2)인데 첫주가 일요일(1)인 경우에만 수행
            // 위의 경우 difference가 1이다
            // 오늘 날짜의 위치를 파악
            mCurrentOrigin.x = WeekFragment.getSpacingBetweenDay() * ((weekFirstDay.get(Calendar.DAY_OF_WEEK) - mFirstDayOfWeek) % 7);
        }
        eventsStartCoordinate.set(0f, mHeaderHeight);

        // 일요일(맨앞)과 오늘의 일수 차이 계산
        leftDaysWithGaps = (int) -(Math.ceil((mCurrentOrigin.x) / WeekFragment.getSpacingBetweenDay()));
        // 시작 픽셀(일요일 부터 시작)
        startPixel = mCurrentOrigin.x + WeekFragment.getSpacingBetweenDay() * leftDaysWithGaps;

        // 일요일로 이동
        weekFirstDay.add(Calendar.DATE, leftDaysWithGaps);
        // 토요일로 설정
        weekLastDay = (Calendar) weekFirstDay.clone();
        weekLastDay.add(Calendar.DATE, 7);
        weekLastDay.add(Calendar.SECOND, -1);

        weekFirstDate = new Date(weekFirstDay.getTimeInMillis());
        // 툴바의 month 설정

        weekLastDate = new Date(weekLastDay.getTimeInMillis());

        // 날짜를 그림
        for (int i = 0; i <= 7; i++)
        {
            coordinateInfos[i] = new CoordinateInfo().setDate(weekFirstDay).setStartX(WeekFragment.getSpacingBetweenDay() * i)
                    .setEndX(WeekFragment.getSpacingBetweenDay() * (i + 1));
            weekFirstDay.add(Calendar.DATE, 1);
        }
        weekFirstDay.add(Calendar.DATE, -8);


    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        // 화면에 처음 보여질 때만 호출
        super.onDraw(canvas);

        // 헤더의 배경을 그림
        canvas.drawRect(0, 0, getWidth(), getHeight(), mHeaderBackgroundPaint);
        drawHeaderView(canvas);
    }

    private void drawHeaderView(Canvas canvas)
    {
        // 요일을 그림
        for (int i = 0; i < 7; i++)
        {
            canvas.drawText(DateHour.getDayString(i), WeekFragment.getSpacingBetweenDay() / 2 + WeekFragment.getSpacingBetweenDay() * i, mHeaderDayHeight + mHeaderRowMarginTop, mHeaderDateNormalPaint);
        }

        for (int i = 0; i < 7; i++)
        {
            // 날짜 사이의 구분선
            canvas.drawLine(WeekFragment.getSpacingBetweenDay() * i, getHeight() - spacingLineHeight, WeekFragment.getSpacingBetweenDay() * i, getHeight(), dividingPaint);
        }

        // 날짜를 그림
        for (int i = 0; i <= 6; i++)
        {
            boolean isToday = isSameDay(weekFirstDay, today);
            // 표시할 날짜를 가져옴
            String dateLabel = DateHour.getDate(weekFirstDay);

            float x = startPixel + WeekFragment.getSpacingBetweenDay() / 2 + WeekFragment.getSpacingBetweenDay() * i;
            float y = mHeaderDayHeight + mHeaderRowMarginTop * 2 + mHeaderDateHeight;

            if (dateLabel == null)
            {
                throw new IllegalStateException("A DateTimeInterpreter must not return null date");
            }
            canvas.drawText(dateLabel, x, y, isToday ? mHeaderDateTodayPaint : mHeaderDateNormalPaint);
            weekFirstDay.add(Calendar.DATE, 1);
        }
        weekFirstDay.add(Calendar.WEEK_OF_YEAR, -1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        /*
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
            ViewCompat.postInvalidateOnAnimation(this);
        }
         */
        return true;
    }

    /*
    private void selectDay(int position)
    {
        mSelectedDay = (Calendar) coordinateInfos[position].getDate().clone();
        selectedDayPosition = position;
    }
     */

    private boolean isSameDay(Calendar dayOne, Calendar dayTwo)
    {
        return dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR) && dayOne.get(Calendar.DAY_OF_YEAR) == dayTwo.get(Calendar.DAY_OF_YEAR);
    }

    public void setPosition(int position)
    {
        this.position = position;
        setInitialData();
    }

    public Date getWeekFirstDate()
    {
        return weekFirstDate;
    }

    public Date getWeekLastDate()
    {
        return weekLastDate;
    }
}
