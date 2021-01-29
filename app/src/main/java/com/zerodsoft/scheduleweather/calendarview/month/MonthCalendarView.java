package com.zerodsoft.scheduleweather.calendarview.month;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.provider.CalendarContract;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.activity.main.AppMainActivity;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.etc.EventViewUtil;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MonthCalendarView extends ViewGroup implements IEvent
{
    private long firstDay;
    private final float HEADER_DAY_TEXT_SIZE;
    private final TextPaint HEADER_DAY_PAINT;

    private int ITEM_WIDTH;
    private int ITEM_HEIGHT;

    private static final int SPACING_BETWEEN_EVENT = 12;
    private static final int TEXT_MARGIN = 8;
    public static final int EVENT_LR_MARGIN = 4;
    public static final int EVENT_COUNT = 5;
    private float EVENT_TEXT_HEIGHT;

    private int start;
    private int end;

    float DAY_SPACE_HEIGHT;
    float EVENT_HEIGHT;

    private List<EventData> eventCellsList = new ArrayList<>();

    private List<ContentValues> instances;

    private SparseArray<ItemCell> ITEM_LAYOUT_CELLS = new SparseArray<>(42);

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

        setWillNotDraw(false);
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
        // resolveSize : 실제 설정할 크기를 계산
        ITEM_WIDTH = getWidth() / 7;
        ITEM_HEIGHT = getHeight() / 6;

        EVENT_HEIGHT = (ITEM_HEIGHT - DAY_SPACE_HEIGHT - SPACING_BETWEEN_EVENT * 4) / EVENT_COUNT;
        EVENT_TEXT_HEIGHT = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, EVENT_HEIGHT - TEXT_MARGIN, getContext().getResources().getDisplayMetrics());

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

        if (!eventCellsList.isEmpty())
        {
            drawEvents(canvas);
        }
    }


    private void drawEvents(Canvas canvas)
    {
        for (EventData eventData : eventCellsList)
        {
            final int BEGIN_INDEX = eventData.getStartIndex();
            final int END_INDEX = eventData.getEndIndex();
            final int ROW = eventData.getRow();

            float startX = 0f;
            float startY = 0f;
            float endX = 0f;
            float endY = 0f;

            int eventRowCount = END_INDEX / 7 - BEGIN_INDEX / 7 + 1;

            float left = 0;
            float right = 0;
            float top = 0;
            float bottom = 0;

            ContentValues event = eventData.getEvent();

            final long INSTANCE_BEGIN = event.getAsLong(CalendarContract.Instances.BEGIN);
            long INSTANCE_END = event.getAsLong(CalendarContract.Instances.END);
            final long VIEW_START = ((MonthCalendarItemView) getChildAt(BEGIN_INDEX)).getStartDate().getTime();
            final long VIEW_END = ((MonthCalendarItemView) getChildAt(END_INDEX)).getEndDate().getTime();


            if (event.size() > 0)
            {
                // all day인 경우 instance end를 하루전으로 변경
                if (event.getAsBoolean(CalendarContract.Instances.ALL_DAY))
                {
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(INSTANCE_END);
                    c.add(Calendar.DAY_OF_YEAR, -1);

                    INSTANCE_END = c.getTimeInMillis();
                }

                String viewBeginStr = ClockUtil.DB_DATE_FORMAT.format(((MonthCalendarItemView) getChildAt(BEGIN_INDEX)).getStartDate());
                String viewEndStr = ClockUtil.DB_DATE_FORMAT.format(((MonthCalendarItemView) getChildAt(BEGIN_INDEX)).getEndDate());
                String dataBeginStr = ClockUtil.DB_DATE_FORMAT.format(new Date(INSTANCE_BEGIN));
                String dataEndStr = ClockUtil.DB_DATE_FORMAT.format(new Date(INSTANCE_END));

                int[] margin = EventViewUtil.getViewSideMargin(INSTANCE_BEGIN, INSTANCE_END, VIEW_START, VIEW_END, 4);

                for (int currentRowNum = 1; currentRowNum <= eventRowCount; currentRowNum++)
                {
                    if (currentRowNum == 1)
                    {
                        startX = BEGIN_INDEX % 7 == 0 ? 0 : ITEM_WIDTH * (BEGIN_INDEX % 7);

                        if (eventRowCount == 1)
                        {
                            endX = END_INDEX % 7 == 0 ? 0 : ITEM_WIDTH * (END_INDEX % 7);
                        } else
                        {
                            endX = getWidth() - ITEM_WIDTH;
                        }
                    } else if (currentRowNum == eventRowCount)
                    {
                        startX = 0;
                        endX = END_INDEX % 7 == 0 ? 0 : ITEM_WIDTH * (END_INDEX % 7);
                    }

                    if (currentRowNum != 1 && currentRowNum != eventRowCount)
                    {
                        startX = 0;
                        endX = getWidth() - ITEM_WIDTH;
                    }

                    int week = BEGIN_INDEX / 7 + currentRowNum - 1;
                    startY = ITEM_HEIGHT * (week) + DAY_SPACE_HEIGHT;

                    top = startY + ((EVENT_HEIGHT + SPACING_BETWEEN_EVENT) * ROW);
                    bottom = top + EVENT_HEIGHT;
                    left = startX + margin[0];
                    right = endX + ITEM_WIDTH - margin[1];

                    eventData.setEventColorPaint(EventViewUtil.getEventColorPaint(event.getAsInteger(CalendarContract.Instances.EVENT_COLOR)));
                    eventData.setEventTextPaint(EventViewUtil.getEventTextPaint(EVENT_TEXT_HEIGHT));

                    canvas.drawRect(left, top, right, bottom, eventData.getEventColorPaint());
                    canvas.drawText(event.getAsString(CalendarContract.Instances.TITLE) != null ? event.getAsString(CalendarContract.Instances.TITLE) : "empty"
                            , left + TEXT_MARGIN, bottom - TEXT_MARGIN, eventData.getEventTextPaint());
                }
            }
        }

        float startX = 0f;
        float startY = 0f;

        float top = 0f;
        float bottom = 0f;
        float left = 0f;
        float right = 0f;

        Paint extraPaint = new Paint();
        extraPaint.setColor(Color.LTGRAY);

        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(EVENT_TEXT_HEIGHT);
        textPaint.setTextAlign(Paint.Align.LEFT);

        // more 표시
        for (int index = start; index <= end; index++)
        {
            if (ITEM_LAYOUT_CELLS.get(index) != null)
            {
                int eventsNum = ITEM_LAYOUT_CELLS.get(index).eventsNum;
                int displayedEventsNum = 0;
                int lastRow = -1;

                for (int row = EVENT_COUNT - 1; row >= 0; row--)
                {
                    if (!ITEM_LAYOUT_CELLS.get(index).row[row])
                    {
                        if (lastRow == -1)
                        {
                            lastRow = row;
                        }
                    } else
                    {
                        displayedEventsNum++;
                    }
                }

                // 날짜의 이벤트 개수 > 뷰에 표시된 이벤트의 개수 인 경우 마지막 행에 More를 표시
                if (eventsNum > displayedEventsNum)
                {
                    startX = index % 7 == 0 ? 0 : ITEM_WIDTH * (index % 7);
                    startY = ITEM_HEIGHT * (index / 7) + DAY_SPACE_HEIGHT;

                    top = startY + ((EVENT_HEIGHT + SPACING_BETWEEN_EVENT) * lastRow);
                    bottom = top + EVENT_HEIGHT;
                    left = startX + EVENT_LR_MARGIN;
                    right = startX + ITEM_WIDTH - EVENT_LR_MARGIN;

                    canvas.drawRect(left, top, right, bottom, extraPaint);
                    canvas.drawText("More", startX + EVENT_LR_MARGIN + TEXT_MARGIN, bottom - TEXT_MARGIN, textPaint);
                }
            }
        }
    }

    public void clear()
    {
        ITEM_LAYOUT_CELLS.clear();
        eventCellsList.clear();
    }

    @Override
    public void setInstances(List<ContentValues> instances)
    {
        // 이벤트 테이블에 데이터를 표시할 위치 설정
        this.instances = instances;
        setEventTable();
        invalidate();
    }


    @Override
    public void setEventTable()
    {
        ITEM_LAYOUT_CELLS.clear();
        eventCellsList.clear();

        start = Integer.MAX_VALUE;
        end = Integer.MIN_VALUE;

        // 달력 뷰의 셀에 아이템을 삽입
        for (ContentValues event : instances)
        {
            // 달력 내 위치를 계산
            int beginIndex = ClockUtil.calcDateDifference(ClockUtil.DAY, event.getAsLong(CalendarContract.Instances.BEGIN), firstDay);
            int endIndex = ClockUtil.calcDateDifference(ClockUtil.DAY, event.getAsLong(CalendarContract.Instances.END), firstDay);

            if (event.getAsBoolean(CalendarContract.Instances.ALL_DAY))
            {
                endIndex--;
            }

            if (beginIndex < 0)
            {
                beginIndex = 0;
            }
            if (endIndex > 41)
            {
                // 달력에 표시할 일자의 개수가 총 42개
                endIndex = 41;
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

                Set<Integer> set = new HashSet<>();

                for (int row = 0; row < EVENT_COUNT; row++)
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

            if (rowSet.isEmpty())
            {
                // 가능한 행이 없는 경우
                // 미 표시
            } else
            {
                Iterator<Integer> iterator = rowSet.iterator();
                int row = iterator.next();

                if (row != EVENT_COUNT - 1)
                {
                    // 셀에 삽입된 아이템의 위치를 알맞게 조정
                    // 같은 일정은 같은 위치의 셀에 있어야 한다.
                    // row가 MonthCalendarItemView.EVENT_COUNT - 1인 경우 빈 객체를 저장
                    for (int i = beginIndex; i <= endIndex; i++)
                    {
                        ITEM_LAYOUT_CELLS.get(i).row[row] = true;
                    }
                    EventData eventData = new EventData(event, beginIndex, endIndex, row);
                    eventCellsList.add(eventData);
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
            row = new boolean[EVENT_COUNT];
        }

    }
}


