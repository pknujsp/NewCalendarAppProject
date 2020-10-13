package com.zerodsoft.scheduleweather.calendarview.month;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

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

    private final SparseArray<ItemLayoutCell> ITEM_LAYOUT_CELLS = new SparseArray<>(42);

    public MonthCalendarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        HEADER_DAY_TEXT_SIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, context.getResources().getDisplayMetrics());

        HEADER_DAY_PAINT = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        HEADER_DAY_PAINT.setTextAlign(Paint.Align.CENTER);
        HEADER_DAY_PAINT.setTextSize(HEADER_DAY_TEXT_SIZE);
        HEADER_DAY_PAINT.setColor(Color.BLACK);
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
    }

    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);
    }

    public void clear()
    {
        ITEM_LAYOUT_CELLS.clear();
    }

    public void setSchedules(List<EventData> list)
    {
        // 이벤트 테이블에 데이터를 표시할 위치 설정
        Integer[] indexes = setEventTable(list);

        // 각각의 ITEMVIEW에 데이터 전달하고 다시 그림
        for (int index = indexes[0]; index <= indexes[1]; index++)
        {
            ((MonthCalendarItemView) getChildAt(index)).setItemLayoutCell(ITEM_LAYOUT_CELLS.get(index));
            getChildAt(index).invalidate();
        }
    }

    private Integer[] setEventTable(List<EventData> list)
    {
        int start = Integer.MAX_VALUE;
        int end = Integer.MIN_VALUE;
        ITEM_LAYOUT_CELLS.clear();

        // 달력 뷰의 셀에 아이템을 삽입
        for (EventData eventData : list)
        {
            for (int index = eventData.getStartIndex(); index <= eventData.getEndIndex(); index++)
            {
                if (ITEM_LAYOUT_CELLS.get(index) == null)
                {
                    ITEM_LAYOUT_CELLS.put(index, new ItemLayoutCell());
                }
            }
            int startIndex = eventData.getStartIndex();
            int endIndex = eventData.getEndIndex();

            if (start > startIndex)
            {
                start = startIndex;
            }
            if (end < endIndex)
            {
                end = endIndex;
            }

            // 이벤트를 위치시킬 알맞은 행을 지정
            // startDate부터 endDate까지 공통적으로 비어있는 행을 지정한다.
            Set<Integer> rowSet = new TreeSet<>();

            for (int index = startIndex; index <= endIndex; index++)
            {
                Set<Integer> set = new HashSet<>();
                for (int row = 0; row < MonthCalendarItemView.EVENT_COUNT; row++)
                {
                    if (ITEM_LAYOUT_CELLS.get(index).rows.get(row).isEmpty())
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
                    if (row == MonthCalendarItemView.EVENT_COUNT - 1)
                    {
                        ITEM_LAYOUT_CELLS.get(index).rows.put(row, new ScheduleDTO());
                    } else
                    {
                        ITEM_LAYOUT_CELLS.get(index).rows.put(row, eventData.getSchedule());
                    }
                }
            }
        }
        return new Integer[]{start, end};
    }
}