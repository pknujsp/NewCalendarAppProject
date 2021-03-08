package com.zerodsoft.scheduleweather.calendarview.month;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.provider.CalendarContract;
import android.text.TextPaint;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.calendarview.common.InstanceBarView;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IEvent;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MonthCalendarView extends ViewGroup implements IEvent
{
    /*인스턴스 데이터를 모두 가져온 상태에서 설정 값에 따라
    뷰를 다시 그린다
     */
    private Long firstDay;

    private Integer ITEM_WIDTH;
    private Integer ITEM_HEIGHT;

    private static Integer DAY_PADDING;
    private static Integer DAY_TEXTSIZE;
    private static Paint TODAY_PAINT = new Paint();

    private final int SPACING_BETWEEN_INSTANCE;
    private static Integer DAY_SPACE_HEIGHT;
    private float INSTANCE_BAR_HEIGHT;
    private final int INSTANCE_BAR_LR_MARGIN;

    public static final int MAX_ROWS_COUNT = 5;
    public static final int FIRST_DAY_INDEX = 0;
    public static final int LAST_DAY_INDEX = 41;

    private int start;
    private int end;

    private List<InstanceBar> instanceBarList = new ArrayList<>();
    private List<ContentValues> instances;
    private List<MonthCalendarItemView> monthCalendarItemViewList;
    private SparseArray<ItemCell> ITEM_LAYOUT_CELLS = new SparseArray<>(42);

    public MonthCalendarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        SPACING_BETWEEN_INSTANCE = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, getContext().getResources().getDisplayMetrics());
        INSTANCE_BAR_LR_MARGIN = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, getContext().getResources().getDisplayMetrics());

        DAY_PADDING = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getContext().getResources().getDisplayMetrics());
        DAY_TEXTSIZE = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, getContext().getResources().getDisplayMetrics());

        Rect rect = new Rect();

        TextPaint dayTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        dayTextPaint.setTextAlign(Paint.Align.CENTER);
        dayTextPaint.setTextSize(DAY_TEXTSIZE);
        dayTextPaint.getTextBounds("0", 0, 1, rect);

        DAY_SPACE_HEIGHT = rect.height() + DAY_PADDING * 2;

        TODAY_PAINT.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, getContext().getResources().getDisplayMetrics()));
        TODAY_PAINT.setStyle(Paint.Style.STROKE);
        TODAY_PAINT.setColor(Color.BLUE);

        setBackgroundColor(Color.WHITE);
        setWillNotDraw(false);
    }

    public void setMonthCalendarItemViewList(List<MonthCalendarItemView> monthCalendarItemViewList)
    {
        this.monthCalendarItemViewList = monthCalendarItemViewList;
    }

    public void setFirstDay(long firstDay)
    {
        this.firstDay = firstDay;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3)
    {
        ITEM_WIDTH = getWidth() / 7;
        ITEM_HEIGHT = getHeight() / 6;

        float height = (float) ((ITEM_HEIGHT - DAY_SPACE_HEIGHT) - SPACING_BETWEEN_INSTANCE * (MAX_ROWS_COUNT - 1)) / MAX_ROWS_COUNT;
        INSTANCE_BAR_HEIGHT = height;

        int left = 0;
        int right = 0;
        int top = 0;
        int bottom = 0;

        //monthcalendarviewitem 크기,위치 설정
        for (int index = 0; index < monthCalendarItemViewList.size(); index++)
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

            View childView = monthCalendarItemViewList.get(index);
            childView.measure(ITEM_WIDTH, ITEM_HEIGHT);
            childView.layout(left, top, right, bottom);
        }
        //instancebarview 크기,위치 설정
        for (int index = 0; index < instanceBarList.size(); index++)
        {
            final int BEGIN_INDEX = instanceBarList.get(index).beginIndex;
            final int END_INDEX = instanceBarList.get(index).endIndex;
            final int ROW = instanceBarList.get(index).row;
            final int WEEK = instanceBarList.get(index).week;
            final int LEFT_MARGIN = instanceBarList.get(index).leftMargin;
            final int RIGHT_MARGIN = instanceBarList.get(index).rightMargin;

            float startX = (BEGIN_INDEX % 7) * ITEM_WIDTH;
            float startY = ITEM_HEIGHT * WEEK + DAY_SPACE_HEIGHT;
            float endX = (END_INDEX % 7 + 1) * ITEM_WIDTH;

            left = (int) (startX + LEFT_MARGIN);
            right = (int) (endX - RIGHT_MARGIN);
            top = (int) (startY + (INSTANCE_BAR_HEIGHT + SPACING_BETWEEN_INSTANCE) * ROW);
            bottom = (int) (top + INSTANCE_BAR_HEIGHT);

            View childView = instanceBarList.get(index).instanceBarView;
            childView.measure((int) (right - left), (int) INSTANCE_BAR_HEIGHT);
            childView.layout(left, top, right, bottom);
        }
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
    }


    public void clear()
    {
        ITEM_LAYOUT_CELLS.clear();
        instanceBarList.clear();
    }

    @Override
    public void setInstances(List<ContentValues> instances)
    {
        // 이벤트 테이블에 데이터를 표시할 위치 설정
        this.instances = instances;
        setEventTable();
        requestLayout();
        invalidate();
    }


    @Override
    public void setEventTable()
    {
        ITEM_LAYOUT_CELLS.clear();
        instanceBarList.clear();
        removeAllViews();

        for (MonthCalendarItemView monthCalendarItemView : monthCalendarItemViewList)
        {
            addView(monthCalendarItemView);
        }

        start = Integer.MAX_VALUE;
        end = Integer.MIN_VALUE;

        boolean showCanceledInstance = App.isPreference_key_show_canceled_instances();

        // 달력 뷰의 셀에 아이템을 삽입
        for (ContentValues instance : instances)
        {
            if (!showCanceledInstance)
            {
                if (instance.getAsInteger(CalendarContract.Instances.STATUS) ==
                        CalendarContract.Instances.STATUS_CANCELED)
                {
                    // 취소(초대 거부)된 인스턴스인 경우..
                    continue;
                }
            }
            // 달력 내 위치를 계산
            int beginIndex = ClockUtil.calcBeginDayDifference(instance.getAsLong(CalendarContract.Instances.BEGIN), firstDay);
            int endIndex = ClockUtil.calcEndDayDifference(instance.getAsLong(CalendarContract.Instances.END), firstDay, instance.getAsBoolean(CalendarContract.Instances.ALL_DAY));

            if (beginIndex < FIRST_DAY_INDEX)
            {
                beginIndex = FIRST_DAY_INDEX;
            }
            if (endIndex > LAST_DAY_INDEX)
            {
                // 달력에 표시할 일자의 개수가 총 42개
                endIndex = LAST_DAY_INDEX;
            }

            if (start > beginIndex)
            {
                start = beginIndex;
            }

            if (end < endIndex)
            {
                end = endIndex;
            }

            // 이벤트를 위치시킬 알맞은 행을 지정
            // startDate부터 endDate까지 공통적으로 비어있는 행을 지정한다.
            Set<Integer> rowSet = new TreeSet<>();

            for (int index = beginIndex; index <= endIndex; index++)
            {
                if (ITEM_LAYOUT_CELLS.get(index) == null)
                {
                    ITEM_LAYOUT_CELLS.put(index, new ItemCell());
                }

                // 이벤트 개수 수정
                ITEM_LAYOUT_CELLS.get(index).eventsNum++;

                Set<Integer> set = new ArraySet<>();

                for (int row = 0; row < MAX_ROWS_COUNT; row++)
                {
                    if (!ITEM_LAYOUT_CELLS.get(index).row[row])
                    {
                        set.add(row);
                    }
                }

                if (index == beginIndex)
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

            if (!rowSet.isEmpty())
            {
                Iterator<Integer> iterator = rowSet.iterator();
                final int row = iterator.next();

                if (row < MAX_ROWS_COUNT - 1)
                {
                    // 셀에 삽입된 아이템의 위치를 알맞게 조정
                    // 같은 일정은 같은 위치의 셀에 있어야 한다.
                    // row가 MonthCalendarItemView.EVENT_COUNT - 1인 경우 빈 객체를 저장
                    for (int i = beginIndex; i <= endIndex; i++)
                    {
                        ITEM_LAYOUT_CELLS.get(i).row[row] = true;
                    }

                    final int firstWeek = beginIndex / 7;
                    final int endWeek = endIndex / 7;

                    long instanceBegin = instance.getAsLong(CalendarContract.Instances.BEGIN);
                    long instanceEnd = instance.getAsLong(CalendarContract.Instances.END);
                    long viewBegin = ((MonthCalendarItemView) getChildAt(beginIndex)).getStartDate().getTime();
                    long viewEnd = ((MonthCalendarItemView) getChildAt(endIndex)).getEndDate().getTime();

                    int[] margin = EventUtil.getViewSideMargin(instanceBegin, instanceEnd, viewBegin, viewEnd, INSTANCE_BAR_LR_MARGIN, instance.getAsBoolean(CalendarContract.Instances.ALL_DAY));

                    for (int week = firstWeek; week <= endWeek; week++)
                    {
                        int newBeginIndex = (week == firstWeek) ? beginIndex : 7 * week;
                        int newEndIndex = (week == endWeek) ? endIndex : 7 * (week + 1) - 1;

                        int leftMargin = (week == firstWeek) ? margin[0] : 0;
                        int rightMargin = (week == endWeek) ? margin[1] : 0;

                        InstanceBarView instanceBarView = new InstanceBarView(getContext(), instance);
                        addView(instanceBarView);

                        InstanceBar instanceBar = new InstanceBar(instanceBarView, newBeginIndex, newEndIndex, row, leftMargin, rightMargin, week);
                        instanceBarList.add(instanceBar);
                    }
                }

            }
        }
    }


    class ItemCell
    {
        boolean[] row;
        int eventsNum;

        public ItemCell()
        {
            row = new boolean[MAX_ROWS_COUNT];
        }
    }

    static class InstanceBar
    {
        InstanceBarView instanceBarView;
        int beginIndex;
        int endIndex;
        int row;
        int leftMargin;
        int rightMargin;
        int week;

        public InstanceBar(InstanceBarView instanceBarView, int beginIndex, int endIndex, int row, int leftMargin,
                           int rightMargin, int week)
        {
            this.instanceBarView = instanceBarView;
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
            this.row = row;
            this.leftMargin = leftMargin;
            this.rightMargin = rightMargin;
            this.week = week;
        }
    }

    public static class MonthCalendarItemView extends View
    {
        private float x;
        private final float y;
        private boolean isToday;
        private final TextPaint DAY_TEXT_PAINT;

        private Date startDate;
        private Date endDate;

        public MonthCalendarItemView(Context context, int dayTextColor)
        {
            super(context);
            DAY_TEXT_PAINT = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            DAY_TEXT_PAINT.setTextAlign(Paint.Align.CENTER);
            DAY_TEXT_PAINT.setTextSize(DAY_TEXTSIZE);
            DAY_TEXT_PAINT.setColor(dayTextColor);
            y = DAY_SPACE_HEIGHT / 2f + (DAY_SPACE_HEIGHT - (2 * DAY_PADDING)) / 2f;

            TypedValue backgroundValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, backgroundValue, true);
            setBackgroundResource(backgroundValue.resourceId);
        }

        public MonthCalendarItemView setDate(Date startDate, Date endDate, boolean isToday)
        {
            this.startDate = startDate;
            this.endDate = endDate;
            this.isToday = isToday;
            return this;
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom)
        {
            super.onLayout(changed, left, top, right, bottom);
            x = (float) getWidth() / 2f;
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);
            canvas.drawText(ClockUtil.D.format(startDate), x, y, DAY_TEXT_PAINT);

            if (isToday)
            {
                canvas.drawRect(0, 0, getWidth(), getHeight(), TODAY_PAINT);
            }
        }

        public Date getStartDate()
        {
            return startDate;
        }

        public Date getEndDate()
        {
            return endDate;
        }
    }
}


