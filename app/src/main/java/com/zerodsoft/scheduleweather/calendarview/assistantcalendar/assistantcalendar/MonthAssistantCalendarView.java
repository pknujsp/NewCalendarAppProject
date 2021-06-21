package com.zerodsoft.scheduleweather.calendarview.assistantcalendar.assistantcalendar;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.provider.CalendarContract;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.interfaces.CalendarDateOnClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.CalendarViewInitializer;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.scheduleweather.calendarview.month.MonthCalendarView;
import com.zerodsoft.scheduleweather.room.dto.SelectedCalendarDTO;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MonthAssistantCalendarView extends ViewGroup implements CalendarViewInitializer
{
    /*
    요일, 날짜, 이벤트 개수
     */
    protected static final TextPaint THIS_MONTH_DATE_TEXTPAINT = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    protected static final TextPaint NOT_THIS_MONTH_DATE_TEXTPAINT = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    protected static final TextPaint INSTANCE_COUNT_TEXTPAINT = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    protected static final Paint TODAY_PAINT = new Paint();
    protected static float TEXT_SIZE;
    protected static float DATE_TEXT_VIEW_HEIGHT;
    protected static float COUNT_TEXT_VIEW_HEIGHT;
    protected static float SPACING_BETWEEN_DATE_COUNT;
    protected static float MARGIN;
    protected static Integer ITEM_HEIGHT;
    private Date viewFirstDateTime;
    private Date viewLastDateTime;

    private Map<Integer, CalendarInstance> calendarInstanceMap;

    private IConnectedCalendars iConnectedCalendars;
    private CalendarDateOnClickListener calendarDateOnClickListener;
    private IControlEvent iControlEvent;

    public MonthAssistantCalendarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        Rect rect = new Rect();

        TEXT_SIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, getContext().getResources().getDisplayMetrics());
        TODAY_PAINT.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, getContext().getResources().getDisplayMetrics()));
        TODAY_PAINT.setStyle(Paint.Style.STROKE);
        TODAY_PAINT.setColor(Color.BLUE);

        THIS_MONTH_DATE_TEXTPAINT.setTextSize(TEXT_SIZE);
        THIS_MONTH_DATE_TEXTPAINT.setTextAlign(Paint.Align.CENTER);
        THIS_MONTH_DATE_TEXTPAINT.setColor(Color.BLACK);

        NOT_THIS_MONTH_DATE_TEXTPAINT.setTextSize(TEXT_SIZE);
        NOT_THIS_MONTH_DATE_TEXTPAINT.setTextAlign(Paint.Align.CENTER);
        NOT_THIS_MONTH_DATE_TEXTPAINT.setColor(Color.GRAY);
        NOT_THIS_MONTH_DATE_TEXTPAINT.getTextBounds("3", 0, 1, rect);

        DATE_TEXT_VIEW_HEIGHT = rect.height();
        COUNT_TEXT_VIEW_HEIGHT = DATE_TEXT_VIEW_HEIGHT;

        INSTANCE_COUNT_TEXTPAINT.setTextSize(TEXT_SIZE);
        INSTANCE_COUNT_TEXTPAINT.setTextAlign(Paint.Align.CENTER);
        INSTANCE_COUNT_TEXTPAINT.setColor(Color.BLUE);

        SPACING_BETWEEN_DATE_COUNT = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics());
        MARGIN = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, getResources().getDisplayMetrics());

        setBackgroundColor(Color.WHITE);
        setWillNotDraw(false);
    }

    public MonthAssistantCalendarView(Context context)
    {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        ITEM_HEIGHT = (int) (MARGIN * 2 + SPACING_BETWEEN_DATE_COUNT + DATE_TEXT_VIEW_HEIGHT + COUNT_TEXT_VIEW_HEIGHT);
        setMeasuredDimension(widthMeasureSpec, ITEM_HEIGHT * 6);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3)
    {
        // resolveSize : 실제 설정할 크기를 계산
        final int ITEM_WIDTH = getWidth() / 7;

        // childview의 크기 설정
        measureChildren(ITEM_WIDTH, ITEM_HEIGHT);

        final int childCount = getChildCount();
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
    }

    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);
    }

    private final View.OnClickListener dateItemOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            MonthAssistantItemView itemView = ((MonthAssistantItemView) view);
            calendarDateOnClickListener.onClickedDate(itemView.beginDate);
        }
    };


    public void init(Calendar calendar, CalendarDateOnClickListener calendarDateOnClickListener, IControlEvent iControlEvent, IConnectedCalendars iConnectedCalendars)
    {
        this.iConnectedCalendars = iConnectedCalendars;
        this.calendarDateOnClickListener = calendarDateOnClickListener;
        this.iControlEvent = iControlEvent;

        removeAllViews();

        final int previousMonthDaysCount = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        final int thisMonthDaysCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        final int nextMonthDaysCount = MonthCalendarView.TOTAL_DAY_COUNT - thisMonthDaysCount - previousMonthDaysCount;

        final int thisMonthFirstIndex = previousMonthDaysCount;
        final int thisMonthLastIndex = MonthCalendarView.TOTAL_DAY_COUNT - nextMonthDaysCount - 1;

        // 이전 달 일수 만큼 이동 ex) 20201001에서 20200927로 이동
        calendar.add(Calendar.DATE, -previousMonthDaysCount);

        Calendar calendar2 = (Calendar) calendar.clone();
        calendar2.add(Calendar.DATE, 1);

        viewFirstDateTime = calendar.getTime();

        for (int index = 0; index < MonthCalendarView.TOTAL_DAY_COUNT; index++)
        {
            boolean thisMonthDate = (index <= thisMonthLastIndex) && (index >= thisMonthFirstIndex);

            MonthAssistantItemView itemView = new MonthAssistantItemView(getContext(),
                    thisMonthDate, calendar.getTime(), calendar2.getTime());

            itemView.setClickable(true);
            itemView.setOnClickListener(dateItemOnClickListener);
            addView(itemView);

            calendar.add(Calendar.DATE, 1);
            calendar2.add(Calendar.DATE, 1);
        }

        viewLastDateTime = calendar.getTime();

        setInstances(iControlEvent.getInstances(viewFirstDateTime.getTime(), viewLastDateTime.getTime()));
        setEventTable();
    }
    

    @Override
    public void init(Calendar copiedCalendar, OnEventItemLongClickListener onEventItemLongClickListener, OnEventItemClickListener onEventItemClickListener, IControlEvent iControlEvent, IConnectedCalendars iConnectedCalendars)
    {

    }

    @Override
    public void setInstances(Map<Integer, CalendarInstance> resultMap)
    {
        calendarInstanceMap = resultMap;
    }

    @Override
    public void setInstances(List<ContentValues> instances)
    {

    }

    @Override
    public void setEventTable()
    {
        if (getChildCount() > MonthCalendarView.TOTAL_DAY_COUNT)
        {
            removeViews(42, getChildCount() - 42);
        }

        //선택되지 않은 캘린더는 제외
        List<SelectedCalendarDTO> connectedCalendars = iConnectedCalendars.getConnectedCalendars();
        Set<Integer> connectedCalendarIdSet = new HashSet<>();

        for (SelectedCalendarDTO selectedCalendarDTO : connectedCalendars)
        {
            connectedCalendarIdSet.add(selectedCalendarDTO.getCalendarId());
        }

        List<ContentValues> instances = new ArrayList<>();
        for (Integer calendarIdKey : connectedCalendarIdSet)
        {
            if (calendarInstanceMap.containsKey(calendarIdKey))
            {
                instances.addAll(calendarInstanceMap.get(calendarIdKey).getInstanceList());
            }
        }

        Map<Integer, Integer> countMap = new HashMap<>();
        boolean showCanceledInstance = App.isPreference_key_show_canceled_instances();

        // 인스턴스를 날짜 별로 분류
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

            Date beginDate = ClockUtil.instanceDateTimeToDate(instance.getAsLong(CalendarContract.Instances.BEGIN));
            Date endDate = ClockUtil.instanceDateTimeToDate(instance.getAsLong(CalendarContract.Instances.END), instance.getAsBoolean(CalendarContract.Instances.ALL_DAY));
            int beginIndex = ClockUtil.calcDayDifference(beginDate, viewFirstDateTime);
            int endIndex = ClockUtil.calcDayDifference(endDate, viewFirstDateTime);

            if (beginIndex < MonthCalendarView.FIRST_DAY_INDEX)
            {
                beginIndex = MonthCalendarView.FIRST_DAY_INDEX;
            }
            if (endIndex > MonthCalendarView.LAST_DAY_INDEX)
            {
                // 달력에 표시할 일자의 개수가 총 42개
                endIndex = MonthCalendarView.LAST_DAY_INDEX;
            }

            for (int index = beginIndex; index <= endIndex; index++)
            {
                if (!countMap.containsKey(index))
                {
                    countMap.put(index, 0);
                }
                countMap.put(index, countMap.get(index) + 1);
            }
        }

        Set<Integer> mapKeySet = countMap.keySet();
        for (int index : mapKeySet)
        {
            MonthAssistantItemView childView =
                    (MonthAssistantItemView) getChildAt(index);
            childView.setCount(countMap.get(index));
        }

        requestLayout();
        invalidate();
    }

    public void refresh()
    {
        setInstances(iControlEvent.getInstances(viewFirstDateTime.getTime(), viewLastDateTime.getTime()));
        setEventTable();
    }

    public static class MonthAssistantItemView extends View
    {
        private final Date beginDate;
        private final Date endDate;
        private final boolean thisMonthDate;
        private int count;
        private boolean isToday;

        public MonthAssistantItemView(Context context, boolean thisMonthDate, Date beginDate, Date endDate)
        {
            super(context);
            this.thisMonthDate = thisMonthDate;
            this.beginDate = beginDate;
            this.endDate = endDate;

            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            setBackgroundResource(outValue.resourceId);
        }

        public void setCount(int count)
        {
            this.count = count;
        }

        public void setToday(boolean today)
        {
            isToday = today;
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom)
        {
            super.onLayout(changed, left, top, right, bottom);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);
            final int cellWidth = getWidth();
            final int cellHeight = getHeight();

            //날짜
            canvas.drawText(ClockUtil.D.format(beginDate), (float) cellWidth / 2f, MARGIN + DATE_TEXT_VIEW_HEIGHT, thisMonthDate ? THIS_MONTH_DATE_TEXTPAINT
                    : NOT_THIS_MONTH_DATE_TEXTPAINT);

            //이벤트 개수
            if (count > 0)
            {
                canvas.drawText(Integer.toString(count),
                        (float) cellWidth / 2f, cellHeight - MARGIN, INSTANCE_COUNT_TEXTPAINT);
            }
            if (isToday)
            {
                canvas.drawRect(0, 0, cellWidth, cellHeight, TODAY_PAINT);
            }

        }
    }
}
