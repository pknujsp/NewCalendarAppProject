package com.zerodsoft.scheduleweather.calendarview.day;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.provider.CalendarContract;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.common.HeaderInstancesView;
import com.zerodsoft.scheduleweather.calendarview.common.InstanceView;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.month.EventData;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DayHeaderView extends ViewGroup implements IEvent
{
    private final Paint DAY_DATE_TEXT_PAINT;
    private final float DAY_DATE_SPACE_HEIGHT;

    private Date today;
    private Date tomorrow;
    private int ROWS_LIMIT;
    private int rowNum;

    private List<EventData> eventCellsList = new ArrayList<>();
    private OnEventItemClickListener onEventItemClickListener;
    private List<ContentValues> instances;
    private HeaderInstancesView headerInstancesView;

    public DayHeaderView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        // 날짜, 요일 paint
        DAY_DATE_TEXT_PAINT = new Paint();
        DAY_DATE_TEXT_PAINT.setTextAlign(Paint.Align.CENTER);
        DAY_DATE_TEXT_PAINT.setColor(Color.BLACK);
        DAY_DATE_TEXT_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, context.getResources().getDisplayMetrics()));

        Rect rect = new Rect();
        DAY_DATE_TEXT_PAINT.getTextBounds("1", 0, 1, rect);

        DAY_DATE_SPACE_HEIGHT = rect.height() * 2;

        ROWS_LIMIT = context.getResources().getInteger(R.integer.rows_limit);
        // set background
        setBackgroundColor(Color.WHITE);

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
        int height = (int) (DAY_DATE_SPACE_HEIGHT + headerInstancesView.VIEW_HEIGHT * rowNum);
        if (rowNum >= 2)
        {
            height += headerInstancesView.SPACING_BETWEEN_INSTANCE_VIEWS * (rowNum - 1);
        }
        setMeasuredDimension(widthMeasureSpec, height);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3)
    {
        if (getChildCount() > 0)
        {
            getChildAt(0).measure(getWidth(), (int) (getHeight() - DAY_DATE_SPACE_HEIGHT));
            getChildAt(0).layout(0, (int) DAY_DATE_SPACE_HEIGHT, getWidth(), getHeight());
        }
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        // 날짜와 요일 그리기
        canvas.drawText(ClockUtil.D_E.format(today), getWidth() / 2, DAY_DATE_SPACE_HEIGHT - 5, DAY_DATE_TEXT_PAINT);
    }


    public void clear()
    {
        eventCellsList.clear();
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
        eventCellsList.clear();
        rowNum = 0;
        int availableRow = 0;
        removeAllViews();

        headerInstancesView = new HeaderInstancesView(getContext(), onEventItemClickListener);
        headerInstancesView.setClickable(true);
        addView(headerInstancesView);

        // 달력 뷰의 셀에 아이템을 삽입
        for (ContentValues instance : instances)
        {
            if (instance.getAsBoolean(CalendarContract.Instances.ALL_DAY))
            {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(instance.getAsLong(CalendarContract.Instances.END));
                calendar.add(Calendar.DAY_OF_YEAR, -1);

                if (today.after(calendar.getTime()))
                {
                    continue;
                }
            }
            // 이벤트를 위치시킬 알맞은 행을 지정
            // 비어있는 행을 지정한다.
            // row 추가
            rowNum++;
            if (availableRow < ROWS_LIMIT - 1)
            {
                // 셀에 삽입된 아이템의 위치를 알맞게 조정
                // 같은 일정은 같은 위치의 셀에 있어야 한다.

                int[] margin = EventUtil.getViewSideMargin(instance.getAsLong(CalendarContract.Instances.BEGIN)
                        , instance.getAsLong(CalendarContract.Instances.END)
                        , today.getTime()
                        , tomorrow.getTime(), 16, instance.getAsBoolean(CalendarContract.Instances.ALL_DAY));

                int leftMargin = margin[0];
                int rightMargin = margin[1];

                EventData eventData = new EventData(instance, availableRow);
                eventData.setLeftMargin(leftMargin);
                eventData.setRightMargin(rightMargin);

                eventCellsList.add(eventData);
            } else
            {
                eventCellsList.add(new EventData(new ContentValues(), availableRow));
                break;
            }
            availableRow++;
        }

        headerInstancesView.setEventCellsList(eventCellsList);

        for (EventData eventData : eventCellsList)
        {
            InstanceView instanceView = new InstanceView(getContext());
            instanceView.init(eventData.getEvent());
            headerInstancesView.addView(instanceView);
        }
    }

}



