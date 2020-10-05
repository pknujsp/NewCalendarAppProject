package com.zerodsoft.scheduleweather.calendarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.zerodsoft.scheduleweather.calendarview.dto.CoordinateInfo;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.utility.AppSettings;

import java.util.Calendar;
import java.util.List;

public class HourEventsView extends View
{
    protected final int HOUR_TEXT_COLOR;
    protected final int HOUR_TEXT_SIZE;
    protected final int EVENT_TEXT_SIZE;
    protected final int BACKGROUND_COLOR;
    protected final int LINE_THICKNESS;
    protected final int LINE_COLOR;
    protected final Drawable NEW_SCHEDULE_RECT_DRAWABLE;
    protected final Drawable ADD_DRAWABLE;
    protected final int TABLE_LAYOUT_MARGIN;
    protected final int NEW_SCHEDULE_RECT_COLOR;
    protected final int NEW_SCHEDULE_RECT_THICKNESS;

    protected final int HOUR_TEXT_HEIGHT;
    protected final Rect HOUR_TEXT_BOX_RECT;

    protected final Paint HOUR_TEXT_PAINT;
    protected final Paint EVENT_TEXT_PAINT;
    protected final Paint HORIZONTAL_LINE_PAINT;
    protected final Paint VERTICAL_LINE_PAINT;
    protected final Paint BACKGROUND_PAINT;

    protected final Paint GOOGLE_EVENT_PAINT;
    protected final Paint LOCAL_EVENT_PAINT;
    protected final Paint GOOGLE_EVENT_TEXT_PAINT;
    protected final Paint LOCAL_EVENT_TEXT_PAINT;

    protected final Rect NEW_SCHEDULE_RECT;
    protected final Paint NEW_SCHEDULE_PAINT;

    protected final int SPACING_LINES_BETWEEN_HOUR;

    protected int VIEW_WIDTH;
    protected int VIEW_HEIGHT;

    protected Context context;
    protected int position;

    protected float minStartY;
    protected float maxStartY;

    protected PointF startPoint;
    protected PointF endPoint;
    protected float mDistanceY;

    protected float startX;
    protected float startY;

    protected Calendar startTime;
    protected Calendar endTime;

    protected SCROLL_DIRECTION currentScrollDirection = SCROLL_DIRECTION.NONE;
    protected TIME_CATEGORY timeCategory = TIME_CATEGORY.NONE;

    protected final PointF currentTouchedPoint = new PointF(0f, 0f);
    protected final PointF lastTouchedPoint = new PointF(0f, 0f);

    protected List<ScheduleDTO> scheduleList;

    protected enum SCROLL_DIRECTION
    {NONE, VERTICAL, FINISHED}

    protected enum TIME_CATEGORY
    {NONE, START, END}

    public interface OnRefreshHoursViewListener
    {
        void refreshHoursView();
    }

    public interface OnRefreshChildViewListener
    {
        void refreshChildView(int position);
    }

    public interface CoordinateInfoInterface
    {
        CoordinateInfo[] getArray();
    }

    public HourEventsView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HourEventsView, 0, 0);

        try
        {
            HOUR_TEXT_COLOR = a.getColor(R.styleable.HourEventsView_HoursEventsHourTextColor, 0);
            HOUR_TEXT_SIZE = a.getDimensionPixelSize(R.styleable.HourEventsView_HoursEventsHourTextSize, 0);
            EVENT_TEXT_SIZE = a.getDimensionPixelSize(R.styleable.HourEventsView_HoursEventsEventTextSize, 0);
            BACKGROUND_COLOR = a.getColor(R.styleable.HourEventsView_HoursEventsBackgroundColor, 0);
            LINE_THICKNESS = a.getDimensionPixelSize(R.styleable.HourEventsView_HoursEventsLineThickness, 0);
            LINE_COLOR = a.getColor(R.styleable.HourEventsView_HoursEventsLineColor, 0);
            NEW_SCHEDULE_RECT_COLOR = a.getColor(R.styleable.HourEventsView_HoursEventsNewScheduleRectColor, 0);
            NEW_SCHEDULE_RECT_THICKNESS = a.getDimensionPixelSize(R.styleable.HourEventsView_HoursEventsNewScheduleRectThickness, 0);
            TABLE_LAYOUT_MARGIN = HOUR_TEXT_SIZE;
            NEW_SCHEDULE_RECT_DRAWABLE = context.getDrawable(R.drawable.new_schedule_range_rect);
            ADD_DRAWABLE = context.getDrawable(R.drawable.add_schedule_blue);
        } finally
        {
            a.recycle();
        }

        // initialize variable values of this view
        HOUR_TEXT_PAINT = new Paint();
        HOUR_TEXT_PAINT.setColor(HOUR_TEXT_COLOR);
        HOUR_TEXT_PAINT.setTextSize(HOUR_TEXT_SIZE);
        HOUR_TEXT_PAINT.setTextAlign(Paint.Align.LEFT);

        HOUR_TEXT_BOX_RECT = new Rect();
        HOUR_TEXT_PAINT.getTextBounds("오전 12", 0, "오전 12".length(), HOUR_TEXT_BOX_RECT);

        HOUR_TEXT_HEIGHT = HOUR_TEXT_BOX_RECT.height();

        BACKGROUND_PAINT = new Paint();
        BACKGROUND_PAINT.setColor(BACKGROUND_COLOR);

        HORIZONTAL_LINE_PAINT = new Paint();
        HORIZONTAL_LINE_PAINT.setColor(LINE_COLOR);

        VERTICAL_LINE_PAINT = new Paint();
        VERTICAL_LINE_PAINT.setColor(LINE_COLOR);

        EVENT_TEXT_PAINT = new Paint();
        EVENT_TEXT_PAINT.setColor(Color.WHITE);
        EVENT_TEXT_PAINT.setTextSize(EVENT_TEXT_SIZE);

        GOOGLE_EVENT_PAINT = new Paint();
        LOCAL_EVENT_PAINT = new Paint();
        GOOGLE_EVENT_PAINT.setColor(AppSettings.getGoogleEventBackgroundColor());
        LOCAL_EVENT_PAINT.setColor(AppSettings.getLocalEventBackgroundColor());

        LOCAL_EVENT_TEXT_PAINT = new Paint();
        GOOGLE_EVENT_TEXT_PAINT = new Paint();

        LOCAL_EVENT_TEXT_PAINT.setColor(AppSettings.getLocalEventTextColor());
        LOCAL_EVENT_TEXT_PAINT.setTextSize(EVENT_TEXT_SIZE);
        GOOGLE_EVENT_TEXT_PAINT.setColor(AppSettings.getGoogleEventTextColor());
        GOOGLE_EVENT_TEXT_PAINT.setTextSize(EVENT_TEXT_SIZE);

        NEW_SCHEDULE_RECT = new Rect();

        NEW_SCHEDULE_PAINT = new Paint();
        NEW_SCHEDULE_PAINT.setColor(NEW_SCHEDULE_RECT_COLOR);
        NEW_SCHEDULE_PAINT.setAntiAlias(true);
        NEW_SCHEDULE_PAINT.setStrokeWidth(NEW_SCHEDULE_RECT_THICKNESS);

        SPACING_LINES_BETWEEN_HOUR = HOUR_TEXT_BOX_RECT.height() * 4;
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
        canvas.drawRect(0f, 0f, getWidth(), getHeight(), BACKGROUND_PAINT);
    }


    @Override
    public void layout(int l, int t, int r, int b)
    {
        super.layout(l, t, r, b);
        VIEW_WIDTH = getWidth();
        VIEW_HEIGHT = SPACING_LINES_BETWEEN_HOUR * 24;
        minStartY = -(SPACING_LINES_BETWEEN_HOUR * 24 + TABLE_LAYOUT_MARGIN * 2 - getHeight());
        maxStartY = TABLE_LAYOUT_MARGIN;
    }

    protected boolean isSameDay(Calendar day1, Calendar day2)
    {
        return day1.get(Calendar.YEAR) == day2.get(Calendar.YEAR) && day1.get(Calendar.DAY_OF_YEAR) == day2.get(Calendar.DAY_OF_YEAR);
    }

    protected boolean isSameClock(Calendar clock1, Calendar clock2)
    {
        return clock1.equals(clock2);
    }

    protected void fixTimeError(TIME_CATEGORY timeCategory, Calendar originalTime)
    {
        if (startTime.equals(endTime))
        {
            if (timeCategory == TIME_CATEGORY.START)
            {
                startTime.add(Calendar.MINUTE, -15);
            } else
            {
                endTime.add(Calendar.MINUTE, 15);
            }
        } else if (startTime.after(endTime))
        {
            if (timeCategory == TIME_CATEGORY.START)
            {
                startTime.setTime(originalTime.getTime());
            } else
            {
                endTime.setTime(originalTime.getTime());
            }
        }
    }

    protected boolean changeTime(float y, TIME_CATEGORY timeCategory)
    {
        // 스크롤 중인 y좌표에 맞게 시간을 수정한다
        Calendar time = null;

        if (timeCategory == TIME_CATEGORY.START)
        {
            time = startTime;
        } else
        {
            time = endTime;
        }

        float startHour, endHour;
        Calendar originalTime = (Calendar) time.clone();

        for (int i = 0; i <= 23; i++)
        {
            startHour = currentTouchedPoint.y + SPACING_LINES_BETWEEN_HOUR * i;
            endHour = currentTouchedPoint.y + SPACING_LINES_BETWEEN_HOUR * (i + 1);

            if (y >= startHour && y < endHour)
            {
                float minute15Height = (endHour - startHour) / 4f;
                y = y - startHour;

                for (int j = 0; j <= 3; j++)
                {
                    if (y >= minute15Height * j && y <= minute15Height * (j + 1))
                    {
                        int year = time.get(Calendar.YEAR), month = time.get(Calendar.MONTH), date = time.get(Calendar.DAY_OF_MONTH);
                        int hour = i, minute = j * 15;
                        time.set(year, month, date, hour, minute);
                        fixTimeError(timeCategory, originalTime);

                        return true;
                    }
                }
            }
        }
        return false;
    }


    public HourEventsView setPosition(int position)
    {
        this.position = position;
        return this;
    }

    public int getPosition()
    {
        return position;
    }

    public void setScheduleList(List<ScheduleDTO> scheduleList)
    {
        this.scheduleList = scheduleList;
    }

    public List<ScheduleDTO> getScheduleList()
    {
        return scheduleList;
    }
}