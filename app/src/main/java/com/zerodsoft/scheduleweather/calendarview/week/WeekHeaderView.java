package com.zerodsoft.scheduleweather.calendarview.week;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import android.graphics.RectF;
import android.provider.CalendarContract;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.main.AppMainActivity;
import com.zerodsoft.scheduleweather.calendarview.common.HeaderInstancesView;
import com.zerodsoft.scheduleweather.calendarview.common.InstanceView;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.month.EventData;
import com.zerodsoft.scheduleweather.etc.EventViewUtil;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.DateHour;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class WeekHeaderView extends ViewGroup implements IEvent
{
    private Calendar weekFirstDay;
    private Calendar weekLastDay;

    private final int ROWS_LIMIT;

    private final TextPaint DAY_TEXT_PAINT;
    private final TextPaint DATE_TEXT_PAINT;
    private final TextPaint WEEK_OF_YEAR_TEXT_PAINT;
    private final Paint WEEK_OF_YEAR_RECT_PAINT;


    private int START_INDEX;
    private int END_INDEX;
    private int ROWS_COUNT = 0;
    private final int DATE_DAY_SPACE_HEIGHT;
    private final int TEXT_SIZE;
    private final int SPACING_BETWEEN_TEXT;

    private Calendar[] daysOfWeek;

    private HeaderInstancesView headerInstancesView;

    private List<EventData> eventCellsList = new ArrayList<>();
    private OnEventItemClickListener onEventItemClickListener;
    private SparseArray<ItemCell> ITEM_LAYOUT_CELLS = new SparseArray<>(7);
    private List<ContentValues> instances;

    public void setOnEventItemClickListener(OnEventItemClickListener onEventItemClickListener)
    {
        this.onEventItemClickListener = onEventItemClickListener;
    }

    public WeekHeaderView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        setBackgroundColor(Color.WHITE);
        setWillNotDraw(false);

        SPACING_BETWEEN_TEXT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics());
        TEXT_SIZE = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13f, getResources().getDisplayMetrics());
        DATE_DAY_SPACE_HEIGHT = TEXT_SIZE * 2 + SPACING_BETWEEN_TEXT * 3;

        // 날짜 paint
        DATE_TEXT_PAINT = new TextPaint();
        DATE_TEXT_PAINT.setTextAlign(Paint.Align.CENTER);
        DATE_TEXT_PAINT.setColor(Color.GRAY);
        DATE_TEXT_PAINT.setTextSize(TEXT_SIZE);

        // 요일 paint
        DAY_TEXT_PAINT = new TextPaint();
        DAY_TEXT_PAINT.setTextAlign(Paint.Align.CENTER);
        DAY_TEXT_PAINT.setColor(Color.GRAY);
        DAY_TEXT_PAINT.setTextSize(TEXT_SIZE);

        // 주차 paint
        WEEK_OF_YEAR_TEXT_PAINT = new TextPaint();
        WEEK_OF_YEAR_TEXT_PAINT.setTextAlign(Paint.Align.CENTER);
        WEEK_OF_YEAR_TEXT_PAINT.setColor(Color.WHITE);
        WEEK_OF_YEAR_TEXT_PAINT.setTextSize(TEXT_SIZE);

        WEEK_OF_YEAR_RECT_PAINT = new Paint();
        WEEK_OF_YEAR_RECT_PAINT.setColor(Color.GRAY);
        WEEK_OF_YEAR_RECT_PAINT.setAntiAlias(true);

        ROWS_LIMIT = context.getResources().getInteger(R.integer.rows_limit);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int height = (int) (DATE_DAY_SPACE_HEIGHT + headerInstancesView.VIEW_HEIGHT * ROWS_COUNT);
        if (ROWS_COUNT >= 2)
        {
            height += headerInstancesView.SPACING_BETWEEN_INSTANCE_VIEWS * (ROWS_COUNT - 1);
        }

        setMeasuredDimension(widthMeasureSpec, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        if (getChildCount() > 0)
        {
            getChildAt(0).measure(getWidth() - getWidth() / 8, (int) (getHeight() - DATE_DAY_SPACE_HEIGHT));
            getChildAt(0).layout(getWidth() / 8, (int) DATE_DAY_SPACE_HEIGHT, getWidth(), getHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawHeaderView(canvas);
    }

    private void drawHeaderView(Canvas canvas)
    {
        final int COLUMN_WIDTH = getWidth() / 8;
        // 몇 번째 주인지 표시

        RectF weekOfYearRect = new RectF();
        weekOfYearRect.left = COLUMN_WIDTH * (1f / 4f);
        weekOfYearRect.right = COLUMN_WIDTH * (3f / 4f);
        weekOfYearRect.top = (DATE_DAY_SPACE_HEIGHT - TEXT_SIZE) / 2;
        weekOfYearRect.bottom = weekOfYearRect.top + TEXT_SIZE;

        Rect rect = new Rect();
        WEEK_OF_YEAR_TEXT_PAINT.getTextBounds("0", 0, 1, rect);

        canvas.drawRoundRect(weekOfYearRect, 10, 10, WEEK_OF_YEAR_RECT_PAINT);
        canvas.drawText(Integer.toString(weekFirstDay.get(Calendar.WEEK_OF_YEAR)),
                weekOfYearRect.centerX(), weekOfYearRect.centerY() + rect.height() / 2f, WEEK_OF_YEAR_TEXT_PAINT);

        final float dayY = SPACING_BETWEEN_TEXT + TEXT_SIZE - DAY_TEXT_PAINT.descent();
        final float dateY = dayY + DAY_TEXT_PAINT.descent() + SPACING_BETWEEN_TEXT + TEXT_SIZE - DATE_TEXT_PAINT.descent();

        // 요일, 날짜를 그림
        for (int i = 0; i < 7; i++)
        {
            canvas.drawText(DateHour.getDayString(i), COLUMN_WIDTH + COLUMN_WIDTH / 2 + COLUMN_WIDTH * i, dayY, DAY_TEXT_PAINT);
            canvas.drawText(DateHour.getDate(daysOfWeek[i].getTime()), COLUMN_WIDTH + COLUMN_WIDTH / 2 + COLUMN_WIDTH * i, dateY, DATE_TEXT_PAINT);
        }
    }


    public void setInitValue(Calendar[] daysOfWeek)
    {
        // 뷰 설정
        // 이번 주의 날짜 배열 생성
        this.daysOfWeek = daysOfWeek;
        // 주 첫번째 요일(일요일)과 마지막 요일(토요일) 설정
        weekFirstDay = (Calendar) daysOfWeek[0].clone();
        weekLastDay = (Calendar) daysOfWeek[6].clone();
    }

    public void clear()
    {
        ITEM_LAYOUT_CELLS.clear();
        eventCellsList.clear();
        ROWS_COUNT = 0;
        removeAllViews();
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
        clear();
        headerInstancesView = new HeaderInstancesView(getContext(), onEventItemClickListener);
        addView(headerInstancesView);

        START_INDEX = Integer.MAX_VALUE;
        END_INDEX = Integer.MIN_VALUE;

        // 달력 뷰의 셀에 아이템을 삽입
        for (ContentValues instance : instances)
        {
            Date instanceEnd = null;

            if (instance.getAsBoolean(CalendarContract.Instances.ALL_DAY))
            {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(instance.getAsLong(CalendarContract.Instances.END));
                calendar.add(Calendar.DAY_OF_YEAR, -1);

                instanceEnd = calendar.getTime();
            } else
            {
                instanceEnd = new Date(instance.getAsLong(CalendarContract.Instances.END));
            }

            //일요일과의 일수차이 계산
            int startIndex = ClockUtil.calcDateDifference(ClockUtil.DAY, instance.getAsLong(CalendarContract.Instances.BEGIN), weekFirstDay.getTimeInMillis());
            int endIndex = ClockUtil.calcDateDifference(ClockUtil.DAY, instanceEnd.getTime(), weekFirstDay.getTimeInMillis());

            if (startIndex < 0)
            {
                startIndex = 0;
            }

            if (endIndex >= 7)
            {
                endIndex = 6;
            } else if (endIndex < 0)
            {
                continue;
            }

            int[] margin = EventViewUtil.getViewSideMargin(instance.getAsLong(CalendarContract.Instances.BEGIN)
                    , instanceEnd.getTime()
                    , daysOfWeek[startIndex].getTimeInMillis()
                    , daysOfWeek[endIndex + 1].getTimeInMillis(), 8);

            if (START_INDEX > startIndex)
            {
                START_INDEX = startIndex;
            }
            if (END_INDEX < endIndex)
            {
                END_INDEX = endIndex;
            }
            // 이벤트를 위치시킬 알맞은 행을 지정
            // startDate부터 endDate까지 공통적으로 비어있는 행을 지정한다.
            Set<Integer> rowSet = new TreeSet<>();

            for (int index = startIndex; index <= endIndex; index++)
            {
                if (ITEM_LAYOUT_CELLS.get(index) == null)
                {
                    ITEM_LAYOUT_CELLS.put(index, new ItemCell());
                }
                // 이벤트 개수 증가
                ITEM_LAYOUT_CELLS.get(index).eventsCount++;

                Set<Integer> set = new HashSet<>();

                for (int row = 0; row < ROWS_LIMIT; row++)
                {
                    if (!ITEM_LAYOUT_CELLS.get(index).rows[row])
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
                }
            }

            if (!rowSet.isEmpty())
            {
                Iterator<Integer> iterator = rowSet.iterator();
                int row = iterator.next();

                if (row > ROWS_COUNT)
                {
                    ROWS_COUNT = row;
                }

                if (row < ROWS_LIMIT - 1)
                {
                    // 셀에 삽입된 아이템의 위치를 알맞게 조정
                    // 같은 일정은 같은 위치의 셀에 있어야 한다.
                    // row가 MonthCalendarItemView.EVENT_COUNT - 1인 경우 빈 객체를 저장
                    for (int i = startIndex; i <= endIndex; i++)
                    {
                        ITEM_LAYOUT_CELLS.get(i).rows[row] = true;
                    }

                    EventData eventData = new EventData(instance, startIndex, endIndex, row);
                    eventData.setType(EventData.WEEK);
                    eventData.setLeftMargin(margin[0]);
                    eventData.setRightMargin(margin[1]);
                    eventCellsList.add(eventData);

                    InstanceView instanceView = new InstanceView(getContext(), onEventItemClickListener);
                    instanceView.init(eventData.getEvent());
                    headerInstancesView.addView(instanceView);
                }
            }
        }
        headerInstancesView.setEventCellsList(eventCellsList);
        ROWS_COUNT++;
    }

    class ItemCell
    {
        boolean[] rows;
        int eventsCount;

        public ItemCell()
        {
            rows = new boolean[ROWS_LIMIT];
        }
    }
}
