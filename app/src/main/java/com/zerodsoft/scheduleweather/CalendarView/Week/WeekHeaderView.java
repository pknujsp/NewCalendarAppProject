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
import androidx.core.view.ViewCompat;

import com.zerodsoft.scheduleweather.CalendarView.ACCOUNT_TYPE;
import com.zerodsoft.scheduleweather.CalendarView.Dto.CoordinateInfo;
import com.zerodsoft.scheduleweather.CalendarFragment.WeekFragment;
import com.zerodsoft.scheduleweather.CalendarView.EventDrawingInfo;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Room.DBController;
import com.zerodsoft.scheduleweather.Room.DTO.ScheduleCategoryDTO;
import com.zerodsoft.scheduleweather.Room.DTO.ScheduleDTO;
import com.zerodsoft.scheduleweather.Utility.AppSettings;
import com.zerodsoft.scheduleweather.Utility.DateHour;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class WeekHeaderView extends View implements WeekView.CoordinateInfoInterface
{
    private final static String TAG = "WeekHeaderView";
    private Context mContext;
    public static Calendar today = Calendar.getInstance();
    private Calendar mSelectedDay;
    private Calendar sunday;
    private Calendar weekFirstDay;
    private Calendar weekLastDay;
    private int position;

    private PointF mCurrentOrigin = new PointF(0f, 0f);
    private PointF mLastOrigin = new PointF(0F, 0F);
    private PointF eventsStartCoordinate = new PointF(0f, 0f);
    private Paint mHeaderBackgroundPaint;
    private Paint mHeaderDateNormalPaint;
    private Paint mHeaderDateTodayPaint;
    private Paint mHeaderDayNormalPaint;
    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint dividingPaint;
    private int mHeaderDateHeight;
    private int mHeaderDayHeight;

    // Attributes and their default values.
    private int mHeaderHeightNormal;
    private int mHeaderHeightEvents;
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
    private int spacingLineHeight;
    private int spacingBetweenEvent;
    private int weekHeaderEventTextSize;
    private int weekHeaderEventBoxHeight;

    private boolean firstDraw = true;

    private CoordinateInfo[] coordinateInfos = new CoordinateInfo[7];
    public static int selectedDayPosition = -1;
    private boolean haveEvents = false;
    private int eventRowNum;

    private long weekFirstDayMillis;
    private long weekLastDayMillis;

    private List<ScheduleDTO> scheduleList;

    private boolean[][] eventMatrix = new boolean[EVENT_ROW_MAX][7];
    private List<EventDrawingInfo> eventDrawingInfoList = new ArrayList<>();

    private static final int EVENT_ROW_MAX = 20;

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

        sunday = (Calendar) today.clone();
        sunday.set(sunday.get(Calendar.YEAR), sunday.get(Calendar.MONTH), sunday.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        sunday.add(Calendar.WEEK_OF_YEAR, (position - WeekFragment.FIRST_VIEW_NUMBER));

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
        eventsStartCoordinate.set(0f, mHeaderHeightNormal);
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
        canvas.drawRect(0, 0, getWidth(), getHeight(), mHeaderBackgroundPaint);

        // 일요일(맨앞)과 오늘의 일수 차이 계산
        final int leftDaysWithGaps = (int) -(Math.ceil((mCurrentOrigin.x) / mHeaderWidthPerDay));
        // 시작 픽셀(일요일 부터 시작)
        final float startPixel = mCurrentOrigin.x + mHeaderWidthPerDay * leftDaysWithGaps;

        // 요일을 그림
        for (int i = 0; i < 7; i++)
        {
            canvas.drawText(DateHour.getDayString(i), mHeaderWidthPerDay / 2 + mHeaderWidthPerDay * i, mHeaderDayHeight + mHeaderRowMarginTop, mHeaderDateNormalPaint);
        }

        if (firstDraw)
        {
            // 일요일로 이동

            sunday.add(Calendar.DATE, leftDaysWithGaps);

            if (weekFirstDay == null && weekLastDay == null)
            {
                weekFirstDay = (Calendar) sunday.clone();
                weekLastDay = (Calendar) sunday.clone();
                weekLastDay.add(Calendar.DATE, 6);
            }

            if (selectedDayPosition == -1)
            {
                selectedDayPosition = -leftDaysWithGaps;
            }
            firstDraw = false;
        } else
        {
            sunday.add(Calendar.DATE, -7);
        }

        for (int i = 0; i < 7; i++)
        {
            canvas.drawLine(mHeaderWidthPerDay * i, getHeight() - spacingLineHeight, mHeaderWidthPerDay * i, getHeight(), dividingPaint);
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

        if (haveEvents)
        {
            drawEvents(canvas);
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

        firstDraw = true;
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
        int googleEventColor = AppSettings.getGoogleEventColor();
        int localEventColor = AppSettings.getLocalEventColor();

        Paint googleEventPaint = new Paint();
        Paint localEventPaint = new Paint();
        googleEventPaint.setColor(googleEventColor);
        localEventPaint.setColor(localEventColor);

        for (EventDrawingInfo eventDrawingInfo : eventDrawingInfoList)
        {
            if (eventDrawingInfo.getAccountType() == ACCOUNT_TYPE.GOOGLE)
            {
                canvas.drawRect(eventDrawingInfo.getLeft() + spacingBetweenEvent, eventDrawingInfo.getTop(), eventDrawingInfo.getRight() - spacingBetweenEvent, eventDrawingInfo.getBottom(), googleEventPaint);
            } else
            {
                canvas.drawRect(eventDrawingInfo.getLeft() + spacingBetweenEvent, eventDrawingInfo.getTop(), eventDrawingInfo.getRight() - spacingBetweenEvent, eventDrawingInfo.getBottom(), localEventPaint);
            }
            canvas.drawText(eventDrawingInfo.getSchedule().getSubject(), (eventDrawingInfo.getRight() - eventDrawingInfo.getLeft()) / 2, eventDrawingInfo.getBottom(), mHeaderDateNormalPaint);
        }
    }

    private void setEventDrawingInfo()
    {
        weekFirstDayMillis = weekFirstDay.getTimeInMillis();
        weekLastDayMillis = weekLastDay.getTimeInMillis();

        Integer left = new Integer(0), top = new Integer(0), right = new Integer(0), bottom = new Integer(0);

        for (ScheduleDTO schedule : scheduleList)
        {
            calcEventPosition(left, right, top, bottom, schedule);
            if (schedule.getCategory() == ScheduleCategoryDTO.GOOGLE_SCHEDULE)
            {
                eventDrawingInfoList.add(new EventDrawingInfo(left, right, top, bottom, schedule, ACCOUNT_TYPE.GOOGLE));
            } else
            {
                eventDrawingInfoList.add(new EventDrawingInfo(left, right, top, bottom, schedule, ACCOUNT_TYPE.LOCAL));
            }
        }
    }

    private void calcEventPosition(Integer left, Integer right, Integer top, Integer bottom, ScheduleDTO schedule)
    {
        long startMillis = schedule.getStartDate();
        long endMillis = schedule.getEndDate();
        int startIndex = 0, endIndex = 0;

        if (endMillis < weekFirstDayMillis || startMillis > weekLastDayMillis)
        {
            return;
        } else if (startMillis >= weekFirstDayMillis && endMillis <= weekLastDayMillis)
        {
            // 이번주 내에 시작/종료
            for (int i = coordinateInfos.length - 1; i >= 0; i--)
            {
                if (schedule.getStartDate() >= coordinateInfos[i].getDate().getTimeInMillis())
                {
                    left = mHeaderWidthPerDay * i;
                    startIndex = i;
                }
                if (schedule.getEndDate() >= coordinateInfos[i].getDate().getTimeInMillis())
                {
                    right = mHeaderWidthPerDay * (i + 1);
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
                    right = mHeaderWidthPerDay * (i + 1);
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
                    left = mHeaderWidthPerDay * i;
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
    }

    public int getEventRowNum()
    {
        return eventRowNum;
    }

}
