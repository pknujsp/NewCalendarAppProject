package com.zerodsoft.scheduleweather.calendarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.Nullable;

import com.zerodsoft.scheduleweather.AppMainActivity;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarfragment.WeekFragment;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.utility.DateHour;

import java.util.Calendar;
import java.util.List;

public class HourEventsView extends ViewGroup
{
    // 구분선 paint
    protected final Paint DIVIDING_LINE_PAINT;
    // 시각 paint
    protected final Paint HOUR_PAINT;
    // 일정 추가 rect drawable
    protected final Drawable NEW_SCHEDULE_RECT_DRAWABLE;
    protected final Drawable ADD_DRAWABLE;
    // google event paint
    // google event text paint
    // local event paint
    // local event text paint
    protected final Paint GOOGLE_EVENT_PAINT;
    protected final Paint LOCAL_EVENT_PAINT;
    protected final Paint GOOGLE_EVENT_TEXT_PAINT;
    protected final Paint LOCAL_EVENT_TEXT_PAINT;
    protected final int LINE_THICKNESS;
    protected final int SPACING_BETWEEN_HOURS;
    protected final int TABLE_TB_MARGIN = 32;

    protected final int HOUR_TEXT_HEIGHT;
    protected final int TEXT_MARGIN = 4;
    protected final int EVENT_TEXT_HEIGHT;

    protected Context context;

    protected final int VIEW_WIDTH;
    protected final int VIEW_HEIGHT;

    protected PointF rectStartPoint;
    protected PointF rectEndPoint;

    protected Calendar startTime;
    protected Calendar endTime;

    protected SCROLL_DIRECTION currentScrollDirection = SCROLL_DIRECTION.NONE;

    protected final PointF currentTouchedPoint = new PointF(0f, 0f);

    protected enum SCROLL_DIRECTION
    {NONE, VERTICAL, FINISHED}

    protected enum TIME_CATEGORY
    {NONE, START, END}

    public HourEventsView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;

        NEW_SCHEDULE_RECT_DRAWABLE = context.getDrawable(R.drawable.new_schedule_range_rect);
        ADD_DRAWABLE = context.getDrawable(R.drawable.add_schedule_blue);

        LINE_THICKNESS = 2;

        DIVIDING_LINE_PAINT = new Paint();
        DIVIDING_LINE_PAINT.setColor(Color.GRAY);

        HOUR_PAINT = new Paint();
        HOUR_PAINT.setTextAlign(Paint.Align.LEFT);
        HOUR_PAINT.setColor(Color.GRAY);
        HOUR_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        Rect rect = new Rect();
        HOUR_PAINT.getTextBounds("0", 0, 1, rect);
        HOUR_TEXT_HEIGHT = rect.height();

        GOOGLE_EVENT_PAINT = new Paint();
        LOCAL_EVENT_PAINT = new Paint();
        GOOGLE_EVENT_TEXT_PAINT = new Paint();
        LOCAL_EVENT_TEXT_PAINT = new Paint();

        GOOGLE_EVENT_TEXT_PAINT.setTextAlign(Paint.Align.LEFT);
        LOCAL_EVENT_TEXT_PAINT.setTextAlign(Paint.Align.LEFT);

        GOOGLE_EVENT_TEXT_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 11, getResources().getDisplayMetrics()));
        LOCAL_EVENT_TEXT_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 11, getResources().getDisplayMetrics()));

        GOOGLE_EVENT_TEXT_PAINT.getTextBounds("0", 0, 1, rect);
        EVENT_TEXT_HEIGHT = rect.height();

        SPACING_BETWEEN_HOURS = AppMainActivity.getDisplayHeight() * 2 / 24;

        VIEW_WIDTH = AppMainActivity.getDisplayWidth();
        VIEW_HEIGHT = SPACING_BETWEEN_HOURS * 24 + TABLE_TB_MARGIN * 2;

        setBackgroundColor(Color.WHITE);
        setWillNotDraw(false);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3)
    {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(widthMeasureSpec, VIEW_HEIGHT);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        for (int i = 0; i < 24; i++)
        {
            // 시간 표시
            canvas.drawText(DateHour.getHourString(i), 0, (SPACING_BETWEEN_HOURS * i) + HOUR_TEXT_HEIGHT / 2 + TABLE_TB_MARGIN, HOUR_PAINT);
            // 가로 선 표시
            canvas.drawLine(WeekFragment.getColumnWidth(), (SPACING_BETWEEN_HOURS * i) + TABLE_TB_MARGIN, VIEW_WIDTH, (SPACING_BETWEEN_HOURS * i) + TABLE_TB_MARGIN, DIVIDING_LINE_PAINT);
        }
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
            startHour = currentTouchedPoint.y + SPACING_BETWEEN_HOURS * i;
            endHour = currentTouchedPoint.y + SPACING_BETWEEN_HOURS * (i + 1);

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

}