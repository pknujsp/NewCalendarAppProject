package com.zerodsoft.scheduleweather.calendarview.day;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.zerodsoft.scheduleweather.calendarfragment.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarfragment.WeekFragment;
import com.zerodsoft.scheduleweather.calendarview.month.EventData;
import com.zerodsoft.scheduleweather.calendarview.week.WeekHeaderView;
import com.zerodsoft.scheduleweather.calendarview.week.WeekView;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.utility.AppSettings;
import com.zerodsoft.scheduleweather.utility.Clock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class DayHeaderView extends ViewGroup
{
    private final Paint DAY_DATE_TEXT_PAINT;
    private final Paint GOOGLE_EVENT_PAINT;
    private final Paint GOOGLE_EVENT_TEXT_PAINT;
    private final Paint LOCAL_EVENT_PAINT;
    private final Paint LOCAL_EVENT_TEXT_PAINT;

    private final Paint EXTRA_PAINT;
    private final Paint EXTRA_TEXT_PAINT;

    // 구분선 paint
    protected final Paint DIVIDING_LINE_PAINT;

    private static final int SPACING_BETWEEN_EVENT = 8;
    private static final int DAY_DATE_TB_MARGIN = 16;
    private static final int TEXT_MARGIN = 4;
    public static final int EVENT_LR_MARGIN = 8;
    public static final int EVENT_COUNT = 6;

    private final float EVENT_HEIGHT;
    private final float DAY_DATE_SPACE_HEIGHT;

    private Date today;
    private Date tomorrow;
    private int rowNum = 0;

    private List<EventData> eventCellsList = new ArrayList<>();
    private OnEventItemClickListener onEventItemClickListener;
    private boolean[] rows = new boolean[EVENT_COUNT];

    public DayHeaderView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        DIVIDING_LINE_PAINT = new Paint();
        DIVIDING_LINE_PAINT.setColor(Color.GRAY);

        // 날짜, 요일 paint
        DAY_DATE_TEXT_PAINT = new Paint();
        DAY_DATE_TEXT_PAINT.setTextAlign(Paint.Align.CENTER);
        DAY_DATE_TEXT_PAINT.setColor(Color.BLACK);
        DAY_DATE_TEXT_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, context.getResources().getDisplayMetrics()));

        Rect rect = new Rect();
        DAY_DATE_TEXT_PAINT.getTextBounds("1", 0, 1, rect);

        DAY_DATE_SPACE_HEIGHT = DAY_DATE_TB_MARGIN * 2 + rect.height();

        // set background
        setBackgroundColor(Color.WHITE);

        // google event rect paint
        GOOGLE_EVENT_PAINT = new Paint();

        // local event rect paint
        LOCAL_EVENT_PAINT = new Paint();

        // google event text paint
        GOOGLE_EVENT_TEXT_PAINT = new Paint();
        GOOGLE_EVENT_TEXT_PAINT.setTextAlign(Paint.Align.LEFT);

        // local event text paint
        LOCAL_EVENT_TEXT_PAINT = new Paint();
        LOCAL_EVENT_TEXT_PAINT.setTextAlign(Paint.Align.LEFT);

        GOOGLE_EVENT_TEXT_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13, getContext().getResources().getDisplayMetrics()));
        LOCAL_EVENT_TEXT_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13, getContext().getResources().getDisplayMetrics()));

        GOOGLE_EVENT_TEXT_PAINT.getTextBounds("1", 0, 1, rect);
        EVENT_HEIGHT = rect.height() + TEXT_MARGIN * 2;

        EXTRA_PAINT = new Paint();
        EXTRA_PAINT.setColor(Color.LTGRAY);

        EXTRA_TEXT_PAINT = new TextPaint();
        EXTRA_TEXT_PAINT.setColor(Color.WHITE);
        EXTRA_TEXT_PAINT.setTextSize(LOCAL_EVENT_TEXT_PAINT.getTextSize());
        EXTRA_TEXT_PAINT.setTextAlign(Paint.Align.LEFT);

        setWillNotDraw(false);
    }

    public void setInitValue(Date today, Date tomorrow)
    {
        this.today = today;
        this.tomorrow = tomorrow;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int height = (int) (DAY_DATE_SPACE_HEIGHT + EVENT_HEIGHT * rowNum);
        if (rowNum >= 2)
        {
            height += SPACING_BETWEEN_EVENT * (rowNum - 1);
        }
        setMeasuredDimension(widthMeasureSpec, height);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3)
    {
        int childCount = getChildCount();

        if (!eventCellsList.isEmpty())
        {
            float left = 0;
            float right = 0;
            float top = 0;
            float bottom = 0;

            for (int row = 0; row < childCount; row++)
            {
                DayHeaderEventView child = (DayHeaderEventView) getChildAt(row);

                int leftMargin = 0;
                int rightMargin = 0;

                ScheduleDTO schedule = child.schedule;

                if (schedule.isEmpty())
                {
                    leftMargin = EVENT_LR_MARGIN;
                    rightMargin = EVENT_LR_MARGIN;
                } else
                {
                    // 시작/종료일이 date가 아니나, 일정에 포함되는 경우
                    if (schedule.getStartDate().before(today) && schedule.getEndDate().after(tomorrow))
                    {
                        leftMargin = 0;
                        rightMargin = 0;
                    }
                    // 시작일이 date인 경우, 종료일은 endDate 이후
                    else if (schedule.getEndDate().compareTo(tomorrow) >= 0 && schedule.getStartDate().compareTo(today) >= 0 && schedule.getStartDate().before(tomorrow))
                    {
                        leftMargin = EVENT_LR_MARGIN;
                        rightMargin = 0;
                    }
                    // 종료일이 date인 경우, 시작일은 startDate이전
                    else if (schedule.getEndDate().compareTo(today) >= 0 && schedule.getEndDate().before(tomorrow) && schedule.getStartDate().before(today))
                    {
                        leftMargin = 0;
                        rightMargin = EVENT_LR_MARGIN;
                    }
                    // 시작/종료일이 date인 경우
                    else if (schedule.getEndDate().compareTo(today) >= 0 && schedule.getEndDate().before(tomorrow) && schedule.getStartDate().compareTo(today) >= 0 && schedule.getStartDate().before(tomorrow))
                    {
                        leftMargin = EVENT_LR_MARGIN;
                        rightMargin = EVENT_LR_MARGIN;
                    }
                }

                top = DAY_DATE_SPACE_HEIGHT + (EVENT_HEIGHT + SPACING_BETWEEN_EVENT) * row;
                bottom = top + EVENT_HEIGHT;
                left = leftMargin;
                right = getWidth() - rightMargin;

                int width = (int) (right - left);
                int height = (int) (bottom - top);

                child.measure(width, height);
                child.layout((int) left, (int) top, (int) right, (int) bottom);
            }

        }
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        // 날짜와 요일 그리기
        canvas.drawText(Clock.DATE_DAY_OF_WEEK_FORMAT.format(today), getWidth() / 2, DAY_DATE_SPACE_HEIGHT - DAY_DATE_TB_MARGIN, DAY_DATE_TEXT_PAINT);
    }

    public void clear()
    {
        rows = null;
        eventCellsList.clear();
        rowNum = 0;
    }

    public void setSchedules(List<ScheduleDTO> list)
    {
        // 이벤트 테이블에 데이터를 표시할 위치 설정
        setEventTable(list);
        requestLayout();
        invalidate();
    }

    private void setEventTable(List<ScheduleDTO> list)
    {
        rows = new boolean[EVENT_COUNT];
        eventCellsList.clear();
        rowNum = 0;
        int availableRow = 0;

        // 달력 뷰의 셀에 아이템을 삽입
        for (ScheduleDTO schedule : list)
        {
            // 이벤트를 위치시킬 알맞은 행을 지정
            // 비어있는 행을 지정한다.
            // row 추가
            rowNum++;

            if (availableRow < EVENT_COUNT - 1)
            {
                // 셀에 삽입된 아이템의 위치를 알맞게 조정
                // 같은 일정은 같은 위치의 셀에 있어야 한다.

                rows[availableRow] = true;
                eventCellsList.add(new EventData(schedule, availableRow));
            } else
            {
                eventCellsList.add(new EventData(new ScheduleDTO(), availableRow));
                break;
            }
            availableRow++;
        }

        removeAllViews();

        GOOGLE_EVENT_PAINT.setColor(AppSettings.getGoogleEventBackgroundColor());
        LOCAL_EVENT_PAINT.setColor(AppSettings.getLocalEventBackgroundColor());
        GOOGLE_EVENT_TEXT_PAINT.setColor(AppSettings.getGoogleEventTextColor());
        LOCAL_EVENT_TEXT_PAINT.setColor(AppSettings.getLocalEventTextColor());

        for (EventData eventData : eventCellsList)
        {
            DayHeaderEventView child = new DayHeaderEventView(getContext(), eventData.getSchedule());
            addView(child);
        }
    }

    class DayHeaderEventView extends View
    {
        public ScheduleDTO schedule;

        public DayHeaderEventView(Context context, ScheduleDTO schedule)
        {
            super(context);
            this.schedule = schedule;
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);

            if (schedule.getCategory() == ScheduleDTO.GOOGLE_CATEGORY)
            {
                canvas.drawRect(0, 0, getWidth(), getHeight(), GOOGLE_EVENT_PAINT);
                canvas.drawText(schedule.getSubject(), TEXT_MARGIN, getHeight() - TEXT_MARGIN, GOOGLE_EVENT_TEXT_PAINT);
            } else if (schedule.getCategory() == ScheduleDTO.LOCAL_CATEGORY)
            {
                canvas.drawRect(0, 0, getWidth(), getHeight(), LOCAL_EVENT_PAINT);
                canvas.drawText(schedule.getSubject(), TEXT_MARGIN, getHeight() - TEXT_MARGIN, LOCAL_EVENT_TEXT_PAINT);
            } else
            {
                canvas.drawRect(EVENT_LR_MARGIN, 0, getWidth() - EVENT_LR_MARGIN, getHeight(), LOCAL_EVENT_PAINT);
                canvas.drawText("More", TEXT_MARGIN + EVENT_LR_MARGIN, getHeight() - TEXT_MARGIN, LOCAL_EVENT_TEXT_PAINT);
            }
        }
    }
}



