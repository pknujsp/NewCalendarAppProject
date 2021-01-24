package com.zerodsoft.scheduleweather.calendarview.day;

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

import androidx.annotation.Nullable;

import com.zerodsoft.scheduleweather.calendarview.interfaces.IEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.month.EventData;
import com.zerodsoft.scheduleweather.etc.EventViewUtil;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DayHeaderView extends ViewGroup implements IEvent
{
    private final Paint DAY_DATE_TEXT_PAINT;

    private final Paint EXTRA_PAINT;
    private final Paint EXTRA_TEXT_PAINT;
    private final float EVENT_TEXT_HEIGHT;

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
    private List<ContentValues> instances;

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

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13, getContext().getResources().getDisplayMetrics()));
        textPaint.getTextBounds("0", 0, 1, rect);
        EVENT_HEIGHT = rect.height() + TEXT_MARGIN * 2;
        EVENT_TEXT_HEIGHT = rect.height();

        EXTRA_PAINT = new Paint();
        EXTRA_PAINT.setColor(Color.LTGRAY);

        EXTRA_TEXT_PAINT = new TextPaint();
        EXTRA_TEXT_PAINT.setColor(Color.WHITE);
        EXTRA_TEXT_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13, getContext().getResources().getDisplayMetrics()));
        EXTRA_TEXT_PAINT.setTextAlign(Paint.Align.LEFT);

        setWillNotDraw(false);
    }

    public void setInitValue(Date today, Date tomorrow)
    {
        this.today = today;
        this.tomorrow = tomorrow;
    }

    public void setOnEventItemClickListener(OnEventItemClickListener onEventItemClickListener)
    {
        this.onEventItemClickListener = onEventItemClickListener;
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

                ContentValues instance = child.eventData.getEvent();

                if (instance.size() == 0)
                {
                    leftMargin = EVENT_LR_MARGIN;
                    rightMargin = EVENT_LR_MARGIN;
                } else
                {
                    int[] margin = EventViewUtil.getViewSideMargin(instance.getAsLong(CalendarContract.Instances.BEGIN)
                            , instance.getAsLong(CalendarContract.Instances.END)
                            , today.getTime()
                            , tomorrow.getTime(), EVENT_LR_MARGIN);

                    leftMargin = margin[0];
                    rightMargin = margin[1];
                }

                top = DAY_DATE_SPACE_HEIGHT + (EVENT_HEIGHT + SPACING_BETWEEN_EVENT) * row;
                bottom = top + EVENT_HEIGHT;
                left = leftMargin;
                right = getWidth() - rightMargin;

                int width = (int) (right - left);
                int height = (int) (bottom - top);

                child.measure(width, height);
                child.layout((int) left, (int) top, (int) right, (int) bottom);
                child.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        onEventItemClickListener.onClicked(((DayHeaderEventView) view).eventData.getEvent().getAsLong(CalendarContract.Instances.BEGIN)
                                , ((DayHeaderEventView) view).eventData.getEvent().getAsLong(CalendarContract.Instances.END));
                    }
                });
            }

        }
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        // 날짜와 요일 그리기
        canvas.drawText(ClockUtil.D_E.format(today), getWidth() / 2, DAY_DATE_SPACE_HEIGHT - DAY_DATE_TB_MARGIN, DAY_DATE_TEXT_PAINT);
    }

    public void clear()
    {
        rows = null;
        eventCellsList.clear();
        rowNum = 0;
    }


    @Override
    public void setInstances(List<ContentValues> instances)
    {
        this.instances = instances;
        setEventTable();
        requestLayout();
        invalidate();
    }

    @Override
    public void setEventTable()
    {
        rows = new boolean[EVENT_COUNT];
        eventCellsList.clear();
        rowNum = 0;
        int availableRow = 0;

        // 달력 뷰의 셀에 아이템을 삽입
        for (ContentValues instance : instances)
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
                eventCellsList.add(new EventData(instance, availableRow));
            } else
            {
                eventCellsList.add(new EventData(new ContentValues(), availableRow));
                break;
            }
            availableRow++;
        }

        removeAllViews();

        for (EventData eventData : eventCellsList)
        {
            DayHeaderEventView child = new DayHeaderEventView(getContext(), eventData);
            addView(child);
        }
    }

    class DayHeaderEventView extends View
    {
        public EventData eventData;

        public DayHeaderEventView(Context context, EventData eventData)
        {
            super(context);
            this.eventData = eventData;
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);

            if (eventData.getEvent().size() > 0)
            {
                eventData.setEventTextPaint(EventViewUtil.getEventTextPaint(EVENT_TEXT_HEIGHT));
                eventData.setEventColorPaint(EventViewUtil.getEventColorPaint(eventData.getEvent().getAsInteger(CalendarContract.Instances.EVENT_COLOR)));

                canvas.drawRect(0, 0, getWidth(), getHeight(), eventData.getEventTextPaint());
                canvas.drawText(eventData.getEvent().getAsString(CalendarContract.Instances.TITLE), TEXT_MARGIN, getHeight() - TEXT_MARGIN, eventData.getEventColorPaint());
            } else
            {
                canvas.drawRect(EVENT_LR_MARGIN, 0, getWidth() - EVENT_LR_MARGIN, getHeight(), EXTRA_PAINT);
                canvas.drawText("More", TEXT_MARGIN + EVENT_LR_MARGIN, getHeight() - TEXT_MARGIN, EXTRA_TEXT_PAINT);
            }
        }
    }
}



