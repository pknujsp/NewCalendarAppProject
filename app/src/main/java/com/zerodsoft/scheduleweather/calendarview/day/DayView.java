package com.zerodsoft.scheduleweather.calendarview.day;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.OverScroller;

import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;

import com.zerodsoft.scheduleweather.calendarview.week.WeekFragment;
import com.zerodsoft.scheduleweather.calendarview.hourside.HourEventsView;
import com.zerodsoft.scheduleweather.utility.AppSettings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DayView extends HourEventsView
{
    private boolean createdAddScheduleRect = false;
    private boolean changingStartTime = false;
    private boolean changingEndTime = false;

    private OverScroller overScroller;
    private GestureDetectorCompat gestureDetector;
    private Calendar date;
    private List<ItemCell> itemCells = new ArrayList<>();
    private final int SPACING_BETWEEN_EVENTS = 5;

    private DayViewPagerAdapter adapter;

    public DayView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        //  gestureDetector = new GestureDetectorCompat(context, onGestureListener);
        //  overScroller = new OverScroller(context);
    }

    public void setAdapter(DayViewPagerAdapter adapter)
    {
        this.adapter = adapter;
    }

    public void setDate(Date date)
    {
        this.date = Calendar.getInstance();
        this.date.setTime(date);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);

        if (!itemCells.isEmpty())
        {
            Calendar calendar = Calendar.getInstance();

            float left = 0f;
            float top = 0f;
            float right = 0f;
            float bottom = 0f;
            float cellWidth = 0f;
            int childCount = getChildCount();

            for (int i = 0; i < childCount; i++)
            {
                DayItemView childView = (DayItemView) getChildAt(i);
                ItemCell itemCell = childView.itemCell;

                int column = itemCell.column;
                int columnCount = itemCell.columnCount;

                calendar.setTime(itemCell.schedule.getStartDate());
                PointF startPoint = getPoint(calendar);
                calendar.setTime(itemCell.schedule.getEndDate());
                PointF endPoint = getPoint(calendar);

                cellWidth = (getWidth() - WeekFragment.getColumnWidth() - (SPACING_BETWEEN_EVENTS * (columnCount - 1))) / columnCount;

                top = startPoint.y;
                bottom = endPoint.y;
                if (column == ItemCell.NOT_OVERLAP)
                {
                    left = startPoint.x;
                } else
                {
                    left = startPoint.x + ((cellWidth + SPACING_BETWEEN_EVENTS) * column);
                }
                right = left + cellWidth;

                int width = (int) (right - left);
                int height = (int) (bottom - top);

                childView.measure(width, height);
                childView.layout((int) left, (int) top, (int) right, (int) bottom);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawView(canvas);
    }

    private PointF getPoint(Calendar time)
    {
        // y
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);

        float hourY = SPACING_BETWEEN_HOURS * hour + TABLE_TB_MARGIN;
        float heightPerMinute = SPACING_BETWEEN_HOURS / 60f;

        return new PointF(WeekFragment.getColumnWidth(), hourY + heightPerMinute * minute);
    }


    private void drawView(Canvas canvas)
    {
        if (createdAddScheduleRect)
        {
            // 일정 추가 사각형 코드
            rectStartPoint = getTimePoint(TIME_CATEGORY.START);
            rectEndPoint = getTimePoint(TIME_CATEGORY.END);

            // NEW_SCHEDULE_RECT_DRAWABLE.setBounds(HOUR_TEXT_BOX_RECT.width(), (int) rectStartPoint.y, VIEW_WIDTH, (int) rectEndPoint.y);
            NEW_SCHEDULE_RECT_DRAWABLE.draw(canvas);
        }
    }

    private PointF getTimePoint(TIME_CATEGORY timeCategory)
    {
        // START또는 END TIME에 해당하는 좌표값을 반환
        Calendar time = null;

        if (timeCategory == TIME_CATEGORY.START)
        {
            time = (Calendar) startTime.clone();
        } else
        {
            time = (Calendar) endTime.clone();
        }

        PointF point = new PointF(0f, 0f);

        // y
        float startHour = currentTouchedPoint.y + SPACING_BETWEEN_HOURS * time.get(Calendar.HOUR_OF_DAY);
        float endHour = currentTouchedPoint.y + SPACING_BETWEEN_HOURS * (time.get(Calendar.HOUR_OF_DAY) + 1);

        if (time.get(Calendar.HOUR_OF_DAY) == 0 && timeCategory == TIME_CATEGORY.END)
        {
            startHour = currentTouchedPoint.y + SPACING_BETWEEN_HOURS * 24;
            // 다음 날 오전1시
            endHour = currentTouchedPoint.y + SPACING_BETWEEN_HOURS * 25;
        }
        float minute15Height = (endHour - startHour) / 4f;

        for (int j = 0; j <= 3; j++)
        {
            if (time.get(Calendar.MINUTE) == j * 15)
            {
                point.y = startHour + minute15Height * j;
                break;
            }
        }
        return point;
    }

    public void clear()
    {
        itemCells.clear();
        removeAllViews();
    }

    public void setSchedules(List<ScheduleDTO> list)
    {
        // 이벤트 테이블에 데이터를 표시할 위치 설정
        // 데이터가 없는 경우 진행하지 않음
        itemCells.clear();
        setEventTable(list);
        requestLayout();
        invalidate();
    }

    private void setEventTable(List<ScheduleDTO> list)
    {
        // 저장된 데이터가 표시될 위치를 설정
        for (ScheduleDTO scheduleDTO : list)
        {
            itemCells.add(new ItemCell(scheduleDTO));
        }

        for (int i = 0; i < itemCells.size() - 1; i++)
        {
            if (itemCells.get(i).column != ItemCell.NOT_OVERLAP)
            {
                continue;
            }
            int col = 0;
            int overlappingCount = 0;
            List<ItemCell> overlappingList = null;

            for (int j = i + 1; j < itemCells.size(); j++)
            {
                if (isOverlapping(itemCells.get(i).schedule, itemCells.get(j).schedule))
                {
                    // 시간이 겹치는 경우
                    if (itemCells.get(i).column == ItemCell.NOT_OVERLAP)
                    {
                        itemCells.get(i).column = col++;
                        overlappingList = new ArrayList<>();
                        overlappingList.add(itemCells.get(i));
                    }
                    itemCells.get(j).column = col++;
                    overlappingList.add(itemCells.get(j));
                    overlappingCount++;
                }
            }

            if (overlappingCount == 0)
            {
                // 시간이 겹치지 않는 경우
                itemCells.get(i).column = ItemCell.NOT_OVERLAP;
            } else
            {
                for (ItemCell cell : overlappingList)
                {
                    cell.columnCount = overlappingCount + 1;
                }
            }
        }

        removeAllViews();

        GOOGLE_EVENT_PAINT.setColor(AppSettings.getGoogleEventBackgroundColor());
        LOCAL_EVENT_PAINT.setColor(AppSettings.getLocalEventBackgroundColor());
        GOOGLE_EVENT_TEXT_PAINT.setColor(AppSettings.getGoogleEventTextColor());
        LOCAL_EVENT_TEXT_PAINT.setColor(AppSettings.getLocalEventTextColor());

        for (int i = 0; i < itemCells.size(); i++)
        {
            DayItemView child = new DayItemView(context, itemCells.get(i));
            this.addView(child);
            child.setClickable(true);
            child.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    adapter.showSchedule(((DayItemView) view).itemCell.schedule.getId());
                }
            });
        }
    }

    private boolean isOverlapping(ScheduleDTO schedule1, ScheduleDTO schedule2)
    {
        long start1 = schedule1.getStartDate().getTime();
        long end1 = schedule1.getEndDate().getTime();

        long start2 = schedule2.getStartDate().getTime();
        long end2 = schedule2.getEndDate().getTime();

        if ((start1 > start2 && start1 < end2) || (end1 > start2 && end1 < end2)
                || (start1 < start2 && end1 > end2))
        {
            return true;
        } else
        {
            return false;
        }
    }

    class ItemCell
    {
        public static final int NOT_OVERLAP = -1;
        public int column;
        public int columnCount;

        public ScheduleDTO schedule;

        public ItemCell(ScheduleDTO schedule)
        {
            this.column = NOT_OVERLAP;
            this.columnCount = 1;
            this.schedule = schedule;
        }
    }

    class DayItemView extends View
    {
        public ItemCell itemCell;

        public DayItemView(Context context, ItemCell itemCell)
        {
            super(context);
            this.itemCell = itemCell;
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);

            if (itemCell.schedule.getCategory() == ScheduleDTO.GOOGLE_CATEGORY)
            {
                canvas.drawRect(0, 0, getWidth(), getHeight(), GOOGLE_EVENT_PAINT);
                canvas.drawText(itemCell.schedule.getSubject(), TEXT_MARGIN, EVENT_TEXT_HEIGHT + TEXT_MARGIN, GOOGLE_EVENT_TEXT_PAINT);
            } else
            {
                canvas.drawRect(0, 0, getWidth(), getHeight(), LOCAL_EVENT_PAINT);
                canvas.drawText(itemCell.schedule.getSubject(), TEXT_MARGIN, EVENT_TEXT_HEIGHT + TEXT_MARGIN, LOCAL_EVENT_TEXT_PAINT);
            }
        }
    }

}
