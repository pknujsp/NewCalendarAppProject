package com.zerodsoft.scheduleweather.CalendarView.Week;

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

import com.zerodsoft.scheduleweather.CalendarView.AccountType;
import com.zerodsoft.scheduleweather.CalendarView.Dto.CoordinateInfo;
import com.zerodsoft.scheduleweather.CalendarFragment.WeekFragment;
import com.zerodsoft.scheduleweather.CalendarView.EventDrawingInfo;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Room.DTO.ScheduleDTO;
import com.zerodsoft.scheduleweather.Utility.AppSettings;
import com.zerodsoft.scheduleweather.Utility.DateHour;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WeekHeaderView extends View implements WeekView.CoordinateInfoInterface
{
    public final static String TAG = "WeekHeaderView";
    private Context mContext;
    public static final Calendar today = Calendar.getInstance();
    private static final int WEEK_HEADER_WIDTH_PER_DAY = (WeekFragment.DISPLAY_WIDTH - WeekFragment.SPACING_BETWEEN_DAY) / 7;

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

    private int mHeaderHeightNormal;
    private int mHeaderHeightEvents;
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

    private CoordinateInfo[] coordinateInfos = new CoordinateInfo[7];
    private int eventRowNum;

    private long weekFirstDayMillis;
    private long weekLastDayMillis;

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

        mHeaderHeightNormal = mHeaderDayHeight + mHeaderDateHeight + mHeaderRowMarginTop * 3;

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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        if (haveEvents)
        {
            setMeasuredDimension(widthMeasureSpec, mHeaderHeightEvents);
        } else
        {
            setMeasuredDimension(widthMeasureSpec, mHeaderHeightNormal);
        }
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
            mCurrentOrigin.x = WEEK_HEADER_WIDTH_PER_DAY * ((weekFirstDay.get(Calendar.DAY_OF_WEEK) - mFirstDayOfWeek) % 7);
        }
        eventsStartCoordinate.set(0f, mHeaderHeightNormal);

        // 일요일(맨앞)과 오늘의 일수 차이 계산
        leftDaysWithGaps = (int) -(Math.ceil((mCurrentOrigin.x) / WEEK_HEADER_WIDTH_PER_DAY));
        // 시작 픽셀(일요일 부터 시작)
        startPixel = mCurrentOrigin.x + WEEK_HEADER_WIDTH_PER_DAY * leftDaysWithGaps;

        // 일요일로 이동
        weekFirstDay.add(Calendar.DATE, leftDaysWithGaps);
        // 토요일로 설정
        weekLastDay = (Calendar) weekFirstDay.clone();
        weekLastDay.add(Calendar.DATE, 6);
        weekLastDay.set(weekLastDay.get(Calendar.YEAR), weekLastDay.get(Calendar.MONTH), weekLastDay.get(Calendar.DAY_OF_MONTH), 23, 59, 59);

        // 날짜를 그림
        for (int i = 0; i <= 6; i++)
        {
            coordinateInfos[i] = new CoordinateInfo().setDate(weekFirstDay).setStartX(WEEK_HEADER_WIDTH_PER_DAY * i)
                    .setEndX(WEEK_HEADER_WIDTH_PER_DAY * (i + 1));
            weekFirstDay.add(Calendar.DATE, 1);
        }
        weekFirstDay.add(Calendar.WEEK_OF_YEAR, -1);

        weekFirstDayMillis = weekFirstDay.getTimeInMillis();
        weekLastDayMillis = weekLastDay.getTimeInMillis();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        // 화면에 처음 보여질 때만 호출
        super.onDraw(canvas);

        // 헤더의 배경을 그림
        canvas.drawRect(0, 0, getWidth(), getHeight(), mHeaderBackgroundPaint);
        canvas.drawLine(0f, getHeight() - 1, getWidth(), getHeight() - 1, dividingPaint);
        drawHeaderView(canvas);
    }

    private void drawHeaderView(Canvas canvas)
    {
        // 요일을 그림
        for (int i = 0; i < 7; i++)
        {
            canvas.drawText(DateHour.getDayString(i), WEEK_HEADER_WIDTH_PER_DAY / 2 + WEEK_HEADER_WIDTH_PER_DAY * i, mHeaderDayHeight + mHeaderRowMarginTop, mHeaderDateNormalPaint);
        }

        for (int i = 0; i < 7; i++)
        {
            // 날짜 사이의 구분선
            canvas.drawLine(WEEK_HEADER_WIDTH_PER_DAY * i, getHeight() - spacingLineHeight, WEEK_HEADER_WIDTH_PER_DAY * i, getHeight(), dividingPaint);
        }

        // 날짜를 그림
        for (int i = 0; i <= 6; i++)
        {
            boolean isToday = isSameDay(weekFirstDay, today);
            // 표시할 날짜를 가져옴
            String dateLabel = DateHour.getDate(weekFirstDay);

            float x = startPixel + WEEK_HEADER_WIDTH_PER_DAY / 2 + WEEK_HEADER_WIDTH_PER_DAY * i;
            float y = mHeaderDayHeight + mHeaderRowMarginTop * 2 + mHeaderDateHeight;

            if (dateLabel == null)
            {
                throw new IllegalStateException("A DateTimeInterpreter must not return null date");
            }
            canvas.drawText(dateLabel, x, y, isToday ? mHeaderDateTodayPaint : mHeaderDateNormalPaint);
            weekFirstDay.add(Calendar.DATE, 1);
        }
        weekFirstDay.add(Calendar.WEEK_OF_YEAR, -1);

        if (haveEvents)
        {
            drawEvents(canvas);
        }
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

    public int getPosition()
    {
        return position;
    }

    public void setEventsNum()
    {
        for (int row = 0; row < EVENT_ROW_MAX; row++)
        {
            for (int col = 0; col < 7; col++)
            {
                if (eventMatrix[row][col])
                {
                    eventRowNum = row + 1;
                    break;
                }
            }
            if (eventRowNum == row)
            {
                break;
            }
        }
        if (eventRowNum > 2)
        {
            mHeaderHeightEvents = mHeaderHeightNormal + (weekHeaderEventBoxHeight + mHeaderRowMarginTop) * 2;
        } else
        {
            mHeaderHeightEvents = mHeaderHeightNormal + (weekHeaderEventBoxHeight + mHeaderRowMarginTop) * eventRowNum;
        }

        isFirstDraw = true;
        viewHeightChangeListener.onHeightChanged(mHeaderHeightEvents);
        requestLayout();
        invalidate();
    }

    public void setScheduleList(List<ScheduleDTO> scheduleList)
    {
        this.scheduleList = scheduleList;
        if (!scheduleList.isEmpty())
        {
            this.haveEvents = true;
            setEventDrawingInfo();
            setEventsNum();
        }
    }

    private void drawEvents(Canvas canvas)
    {
        Paint googleEventPaint = new Paint();
        Paint localEventPaint = new Paint();
        Paint googleEventTextPaint = new Paint();
        Paint localEventTextPaint = new Paint();

        googleEventPaint.setColor(AppSettings.getGoogleEventBackgroundColor());
        localEventPaint.setColor(AppSettings.getLocalEventBackgroundColor());

        googleEventTextPaint.setColor(AppSettings.getGoogleEventTextColor());
        googleEventTextPaint.setTextSize(weekHeaderEventTextSize);
        localEventTextPaint.setColor(AppSettings.getLocalEventTextColor());
        localEventTextPaint.setTextSize(weekHeaderEventTextSize);


        for (EventDrawingInfo eventDrawingInfo : eventDrawingInfoList)
        {
            if (eventDrawingInfo.getAccountType() == AccountType.GOOGLE)
            {
                canvas.drawRect(eventDrawingInfo.getLeft() + spacingBetweenEvent, eventDrawingInfo.getTop(), eventDrawingInfo.getRight() - spacingBetweenEvent, eventDrawingInfo.getBottom(), googleEventPaint);
                canvas.drawText(eventDrawingInfo.getSchedule().getSubject(), (eventDrawingInfo.getRight() - eventDrawingInfo.getLeft()) / 2, eventDrawingInfo.getBottom(), googleEventTextPaint);
            } else
            {
                canvas.drawRect(eventDrawingInfo.getLeft() + spacingBetweenEvent, eventDrawingInfo.getTop(), eventDrawingInfo.getRight() - spacingBetweenEvent, eventDrawingInfo.getBottom(), localEventPaint);
                canvas.drawText(eventDrawingInfo.getSchedule().getSubject(), (eventDrawingInfo.getRight() - eventDrawingInfo.getLeft()) / 2, eventDrawingInfo.getBottom(), localEventTextPaint);
            }
        }
    }

    private void setEventDrawingInfo()
    {
        for (ScheduleDTO schedule : scheduleList)
        {
            Map<String, Integer> map = calcEventPosition(schedule);
            if (map != null)
            {
                if (schedule.getCategory() == ScheduleDTO.GOOGLE_CATEGORY)
                {
                    eventDrawingInfoList.add(new EventDrawingInfo(map.get("left"), map.get("right"), map.get("top"), map.get("bottom"), schedule, AccountType.GOOGLE));
                } else
                {
                    eventDrawingInfoList.add(new EventDrawingInfo(map.get("left"), map.get("right"), map.get("top"), map.get("bottom"), schedule, AccountType.LOCAL));
                }
            }
        }
    }

    private Map<String, Integer> calcEventPosition(ScheduleDTO schedule)
    {
        long startMillis = (long) schedule.getStartDate();
        long endMillis = (long) schedule.getEndDate();
        int startIndex = 0, endIndex = 0;

        int top = 0;
        int bottom = 0;
        int right = 0;
        int left = 0;

        if (endMillis < weekFirstDayMillis || startMillis > weekLastDayMillis)
        {
            return null;
        } else if (startMillis >= weekFirstDayMillis && endMillis <= weekLastDayMillis)
        {
            // 이번주 내에 시작/종료
            for (int i = coordinateInfos.length - 1; i >= 0; i--)
            {
                if (schedule.getStartDate() >= coordinateInfos[i].getDate().getTimeInMillis())
                {
                    left = WEEK_HEADER_WIDTH_PER_DAY * i;
                    startIndex = i;
                }
                if (schedule.getEndDate() >= coordinateInfos[i].getDate().getTimeInMillis())
                {
                    right = WEEK_HEADER_WIDTH_PER_DAY * (i + 1);
                    endIndex = i;
                }
            }

            int row = 0;

            RowLoop:
            for (; row < EVENT_ROW_MAX; row++)
            {
                for (int col = startIndex; col <= endIndex; col++)
                {
                    if (eventMatrix[row][col])
                    {
                        break;
                    } else if (col == endIndex)
                    {
                        if (!eventMatrix[row][col])
                        {
                            break RowLoop;
                        }
                    }
                }
            }

            for (int col = startIndex; col <= endIndex; col++)
            {
                eventMatrix[row][col] = true;
                // eventMatrix의 해당 부분이 false일 경우 draw
            }
            top = (int) eventsStartCoordinate.y + row * mHeaderRowMarginBottom;
            bottom = top + weekHeaderEventBoxHeight + row * mHeaderRowMarginBottom;
        } else if (startMillis < weekFirstDayMillis && endMillis <= weekLastDayMillis)
        {
            // 이전 주 부터 시작되어 이번 주 중에 종료
            left = 0;
            startIndex = 0;

            for (int i = coordinateInfos.length - 1; i >= 0; i--)
            {
                if (schedule.getEndDate() >= coordinateInfos[i].getDate().getTimeInMillis())
                {
                    right = WEEK_HEADER_WIDTH_PER_DAY * (i + 1);
                    endIndex = i;
                    break;
                }
            }

            int row = 0;

            RowLoop:
            for (; row < EVENT_ROW_MAX; row++)
            {
                for (int col = startIndex; col <= endIndex; col++)
                {
                    if (eventMatrix[row][col])
                    {
                        break;
                    } else if (col == endIndex)
                    {
                        if (!eventMatrix[row][col])
                        {
                            break RowLoop;
                        }
                    }
                }
            }

            for (int col = startIndex; col <= endIndex; col++)
            {
                eventMatrix[row][col] = true;
                // eventMatrix의 해당 부분이 false일 경우 draw
            }
            top = (int) eventsStartCoordinate.y + row * mHeaderRowMarginBottom;
            bottom = top + weekHeaderEventBoxHeight + row * mHeaderRowMarginBottom;
        } else if (startMillis < weekFirstDayMillis && endMillis > weekLastDayMillis)
        {
            // 이전 주 부터 시작되어 이번 주 이후에 종료

            left = 0;
            right = getWidth();

            startIndex = 0;
            endIndex = 6;

            int row = 0;

            RowLoop:
            for (; row < EVENT_ROW_MAX; row++)
            {
                for (int col = startIndex; col <= endIndex; col++)
                {
                    if (eventMatrix[row][col])
                    {
                        break;
                    } else if (col == endIndex)
                    {
                        if (!eventMatrix[row][col])
                        {
                            break RowLoop;
                        }
                    }
                }
            }

            for (int col = startIndex; col <= endIndex; col++)
            {
                eventMatrix[row][col] = true;
                // eventMatrix의 해당 부분이 false일 경우 draw
            }
            top = (int) eventsStartCoordinate.y + row * mHeaderRowMarginBottom;
            bottom = top + weekHeaderEventBoxHeight + row * mHeaderRowMarginBottom;
        } else if (startMillis >= weekFirstDayMillis && endMillis > weekLastDayMillis)
        {
            // 이번 주 부터 시작되어 이번 주 이후에 종료

            for (int i = coordinateInfos.length - 1; i >= 0; i--)
            {
                if (schedule.getStartDate() >= coordinateInfos[i].getDate().getTimeInMillis())
                {
                    left = WEEK_HEADER_WIDTH_PER_DAY * i;
                    startIndex = i;
                    break;
                }
            }
            right = getWidth();
            endIndex = 6;

            int row = 0;

            RowLoop:
            for (; row < EVENT_ROW_MAX; row++)
            {
                for (int col = startIndex; col <= endIndex; col++)
                {
                    if (eventMatrix[row][col])
                    {
                        break;
                    } else if (col == endIndex)
                    {
                        if (!eventMatrix[row][col])
                        {
                            break RowLoop;
                        }
                    }
                }
            }

            for (int col = startIndex; col <= endIndex; col++)
            {
                eventMatrix[row][col] = true;
                // eventMatrix의 해당 부분이 false일 경우 draw
            }
            top = (int) eventsStartCoordinate.y + row * mHeaderRowMarginBottom;
            bottom = top + weekHeaderEventBoxHeight + row * mHeaderRowMarginBottom;
        }
        Map<String, Integer> map = new HashMap<>();
        map.put("top", top);
        map.put("bottom", bottom);
        map.put("right", right);
        map.put("left", left);

        return map;
    }

    public void clearScheduleVars()
    {
        scheduleList = null;
        isFirstDraw = true;
        haveEvents = false;
    }

    public int getEventRowNum()
    {
        return eventRowNum;
    }

    public long getWeekFirstDayMillis()
    {
        return weekFirstDayMillis;
    }

    public long getWeekLastDayMillis()
    {
        return weekLastDayMillis;
    }
}
