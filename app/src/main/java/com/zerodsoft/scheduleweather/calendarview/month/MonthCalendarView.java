package com.zerodsoft.scheduleweather.calendarview.month;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.calendarfragment.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.month.EventsInfoFragment.MonthEventsInfoFragment;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.utility.AppSettings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class MonthCalendarView extends ViewGroup
{
    private Calendar calendar;
    private final float HEADER_DAY_TEXT_SIZE;
    private final TextPaint HEADER_DAY_PAINT;

    private int ITEM_WIDTH;
    private int ITEM_HEIGHT;

    private Paint GOOGLE_EVENT_PAINT;
    private Paint LOCAL_EVENT_PAINT;
    private Paint GOOGLE_EVENT_TEXT_PAINT;
    private Paint LOCAL_EVENT_TEXT_PAINT;

    private static final int SPACING_BETWEEN_EVENT = 12;
    private static final int TEXT_MARGIN = 8;
    public static final int EVENT_MARGIN = 4;
    public static final int EVENT_COUNT = 5;

    float DAY_SPACE_HEIGHT;
    float EVENT_HEIGHT;

    private Integer[] eventsIndexes;

    private OnEventItemClickListener onEventItemClickListener;

    private final SparseArray<ItemLayoutCell> ITEM_LAYOUT_CELLS = new SparseArray<>(42);

    public MonthCalendarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        HEADER_DAY_TEXT_SIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, context.getResources().getDisplayMetrics());

        HEADER_DAY_PAINT = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        HEADER_DAY_PAINT.setTextAlign(Paint.Align.CENTER);
        HEADER_DAY_PAINT.setTextSize(HEADER_DAY_TEXT_SIZE);
        HEADER_DAY_PAINT.setColor(Color.BLACK);

        Rect rect = new Rect();
        Paint DAY_TEXT_PAINT = new TextPaint();
        DAY_TEXT_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getContext().getResources().getDisplayMetrics()));
        DAY_TEXT_PAINT.getTextBounds("31", 0, 1, rect);

        DAY_SPACE_HEIGHT = rect.height() + 24;

        GOOGLE_EVENT_PAINT = new Paint();
        LOCAL_EVENT_PAINT = new Paint();

        GOOGLE_EVENT_TEXT_PAINT = new Paint();
        LOCAL_EVENT_TEXT_PAINT = new Paint();

        GOOGLE_EVENT_TEXT_PAINT.setTextAlign(Paint.Align.LEFT);

        LOCAL_EVENT_TEXT_PAINT.setTextAlign(Paint.Align.LEFT);

        setWillNotDraw(false);
    }

    public void setOnEventItemClickListener(OnEventItemClickListener onEventItemClickListener)
    {
        this.onEventItemClickListener = onEventItemClickListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    public MonthCalendarView setCalendar(Calendar calendar)
    {
        this.calendar = calendar;
        return this;
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3)
    {
        // resolveSize : 실제 설정할 크기를 계산
        ITEM_WIDTH = getWidth() / 7;
        ITEM_HEIGHT = getHeight() / 6;
        // childview의 크기 설정
        measureChildren(ITEM_WIDTH, ITEM_HEIGHT);

        int childCount = getChildCount();
        int left = 0;
        int right = 0;
        int top = 0;
        int bottom = 0;

        for (int index = 0; index < childCount; index++)
        {
            if (index % 7 == 0)
            {
                // 마지막 열 인경우 다음 행으로 넘어감
                left = 0;
                right = ITEM_WIDTH;
            } else
            {
                left = ITEM_WIDTH * (index % 7);
                right = ITEM_WIDTH * ((index % 7) + 1);
            }
            top = ITEM_HEIGHT * (index / 7);
            bottom = ITEM_HEIGHT * ((index / 7) + 1);

            View childView = getChildAt(index);
            childView.layout(left, top, right, bottom);
        }
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        if (eventsIndexes != null)
        {
            EVENT_HEIGHT = (ITEM_HEIGHT - DAY_SPACE_HEIGHT - SPACING_BETWEEN_EVENT * 4) / EVENT_COUNT;

            GOOGLE_EVENT_TEXT_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, EVENT_HEIGHT - TEXT_MARGIN, getContext().getResources().getDisplayMetrics()));
            LOCAL_EVENT_TEXT_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, EVENT_HEIGHT - TEXT_MARGIN, getContext().getResources().getDisplayMetrics()));

            GOOGLE_EVENT_PAINT.setColor(AppSettings.getGoogleEventBackgroundColor());
            LOCAL_EVENT_PAINT.setColor(AppSettings.getLocalEventBackgroundColor());
            GOOGLE_EVENT_TEXT_PAINT.setColor(AppSettings.getGoogleEventTextColor());
            LOCAL_EVENT_TEXT_PAINT.setColor(AppSettings.getLocalEventTextColor());

            drawEvents(canvas);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);
    }

    private void drawEvents(Canvas canvas)
    {
        for (int index = eventsIndexes[0]; index <= eventsIndexes[1]; index++)
        {
            ItemLayoutCell itemLayoutCell = ITEM_LAYOUT_CELLS.get(index);

            int leftMargin = 0;
            int rightMargin = 0;

            float startX = index % 7 == 0 ? 0 : ITEM_WIDTH * (index % 7);
            float startY = ITEM_HEIGHT * (index / 7) + DAY_SPACE_HEIGHT;

            float left = 0;
            float right = 0;
            float top = 0;
            float bottom = 0;

            Date startDate = ((MonthCalendarItemView) getChildAt(index)).getStartDate();
            Date endDate = ((MonthCalendarItemView) getChildAt(index)).getEndDate();

            for (int count = 0; count < itemLayoutCell.rows.length; count++)
            {
                ScheduleDTO schedule = itemLayoutCell.rows[count];

                if (schedule != null)
                {
                    top = startY + ((EVENT_HEIGHT + SPACING_BETWEEN_EVENT) * count);
                    bottom = top + EVENT_HEIGHT;

                    if (count == EVENT_COUNT - 1)
                    {
                        Paint extraPaint = new Paint();
                        extraPaint.setColor(Color.LTGRAY);

                        TextPaint textPaint = new TextPaint();
                        textPaint.setColor(Color.WHITE);
                        textPaint.setTextSize(LOCAL_EVENT_TEXT_PAINT.getTextSize());
                        textPaint.setTextAlign(Paint.Align.LEFT);

                        canvas.drawRect(startX + EVENT_MARGIN, top, startX + ITEM_WIDTH - EVENT_MARGIN, bottom, extraPaint);
                        canvas.drawText("More", startX + EVENT_MARGIN + TEXT_MARGIN, bottom - TEXT_MARGIN, textPaint);

                        break;
                    } else
                    {
                        // 시작/종료일이 date가 아니나, 일정에 포함되는 경우
                        if (schedule.getStartDate().before(startDate) && schedule.getEndDate().after(endDate))
                        {
                            leftMargin = 0;
                            rightMargin = 0;
                        }
                        // 시작일이 date인 경우, 종료일은 endDate 이후
                        else if (schedule.getEndDate().compareTo(endDate) >= 0 && schedule.getStartDate().compareTo(startDate) >= 0 && schedule.getStartDate().before(endDate))
                        {
                            leftMargin = EVENT_MARGIN;
                            rightMargin = 0;
                        }
                        // 종료일이 date인 경우, 시작일은 startDate이전
                        else if (schedule.getEndDate().compareTo(startDate) >= 0 && schedule.getEndDate().before(endDate) && schedule.getStartDate().before(startDate))
                        {
                            leftMargin = 0;
                            rightMargin = EVENT_MARGIN;
                        }
                        // 시작/종료일이 date인 경우
                        else if (schedule.getEndDate().compareTo(startDate) >= 0 && schedule.getEndDate().before(endDate) && schedule.getStartDate().compareTo(startDate) >= 0 && schedule.getStartDate().before(endDate))
                        {
                            leftMargin = EVENT_MARGIN;
                            rightMargin = EVENT_MARGIN;
                        }

                        left = startX + leftMargin;
                        right = startX + ITEM_WIDTH - rightMargin;

                        if (schedule.getCategory() == ScheduleDTO.GOOGLE_CATEGORY)
                        {
                            canvas.drawRect(left, top, right, bottom, GOOGLE_EVENT_PAINT);
                            canvas.drawText(schedule.getSubject(), left + TEXT_MARGIN, bottom - TEXT_MARGIN, GOOGLE_EVENT_TEXT_PAINT);
                        } else
                        {
                            canvas.drawRect(left, top, right, bottom, LOCAL_EVENT_PAINT);
                            canvas.drawText(schedule.getSubject(), left + TEXT_MARGIN, bottom - TEXT_MARGIN, LOCAL_EVENT_TEXT_PAINT);
                        }
                    }
                }
            }
        }


    }

    public void clear()
    {
        ITEM_LAYOUT_CELLS.clear();
        eventsIndexes = null;
    }

    public void setSchedules(List<EventData> list)
    {
        // 이벤트 테이블에 데이터를 표시할 위치 설정
        eventsIndexes = setEventTable(list);
        invalidate();
    }

    private Integer[] setEventTable(List<EventData> list)
    {
        int start = Integer.MAX_VALUE;
        int end = Integer.MIN_VALUE;
        ITEM_LAYOUT_CELLS.clear();
        eventsIndexes = null;

        // 달력 뷰의 셀에 아이템을 삽입
        for (EventData eventData : list)
        {
            int startIndex = eventData.getStartIndex();
            int endIndex = eventData.getEndIndex();

            if (startIndex == Integer.MIN_VALUE)
            {
                startIndex = 0;
            }
            if (endIndex == Integer.MAX_VALUE)
            {
                endIndex = 41;
            }

            if (start > startIndex)
            {
                start = startIndex;
            }
            if (end < endIndex)
            {
                end = endIndex;
            }

            for (int index = startIndex; index <= endIndex; index++)
            {
                if (ITEM_LAYOUT_CELLS.get(index) == null)
                {
                    ITEM_LAYOUT_CELLS.put(index, new ItemLayoutCell());
                }
            }

            // 이벤트를 위치시킬 알맞은 행을 지정
            // startDate부터 endDate까지 공통적으로 비어있는 행을 지정한다.
            Set<Integer> rowSet = new TreeSet<>();

            for (int index = startIndex; index <= endIndex; index++)
            {
                Set<Integer> set = new HashSet<>();
                for (int row = 0; row < EVENT_COUNT; row++)
                {
                    if (ITEM_LAYOUT_CELLS.get(index).rows[row] == null)
                    {
                        set.add(row);
                    }
                }
                if (index == startIndex)
                {
                    rowSet.addAll(set);
                } else
                {
                    rowSet.retainAll(set);
                    // 가능한 행이 없으면 종료
                    if (rowSet.isEmpty())
                    {
                        break;
                    }
                }
            }

            if (rowSet.isEmpty())
            {
                // 가능한 행이 없는 경우
                // 미 표시
            } else
            {
                Iterator<Integer> iterator = rowSet.iterator();
                int row = iterator.next();

                // 셀에 삽입된 아이템의 위치를 알맞게 조정
                // 같은 일정은 같은 위치의 셀에 있어야 한다.
                // row가 MonthCalendarItemView.EVENT_COUNT - 1인 경우 빈 객체를 저장
                for (int index = startIndex; index <= endIndex; index++)
                {
                    if (row == EVENT_COUNT - 1)
                    {
                        ITEM_LAYOUT_CELLS.get(index).rows[row] = new ScheduleDTO();
                    } else
                    {
                        ITEM_LAYOUT_CELLS.get(index).rows[row] = eventData.getSchedule();
                    }
                }
            }
        }
        return new Integer[]{start, end};
    }

}

