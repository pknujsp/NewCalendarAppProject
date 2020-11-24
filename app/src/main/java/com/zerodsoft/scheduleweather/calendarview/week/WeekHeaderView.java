package com.zerodsoft.scheduleweather.calendarview.week;

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

import androidx.annotation.Nullable;

import com.zerodsoft.scheduleweather.AppMainActivity;
import com.zerodsoft.scheduleweather.calendarfragment.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.month.EventData;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.utility.AppSettings;
import com.zerodsoft.scheduleweather.utility.DateHour;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class WeekHeaderView extends ViewGroup
{
    private Calendar weekFirstDay;
    private Calendar weekLastDay;

    private final TextPaint DAY_TEXT_PAINT;
    private final TextPaint DATE_TEXT_PAINT;
    private final TextPaint WEEK_OF_YEAR_TEXT_PAINT;
    private final Paint WEEK_OF_YEAR_RECT_PAINT;
    private final Paint GOOGLE_EVENT_PAINT;
    private final TextPaint GOOGLE_EVENT_TEXT_PAINT;
    private final Paint LOCAL_EVENT_PAINT;
    private final TextPaint LOCAL_EVENT_TEXT_PAINT;

    private final TextPaint MORE_EVENTS_TEXT_PAINT;
    private final Paint MORE_EVENTS_RECT_PAINT;

    private final Rect WEEK_OF_YEAR_RECT;

    // 구분선 paint
    protected final Paint DIVIDING_LINE_PAINT;

    private final int SPACING_EVENT_BAR_TB;
    private final int EVENT_BAR_LR_MARGIN;
    private final float EVENT_BAR_HEIGHT;
    private final float TEXT_MARGIN_TB;
    private final int SPACING_BETWEEN_DATE_AND_DAY;
    private final float HEADER_TEXT_SIZE;
    private final float EVENT_TEXT_SIZE;
    private final float COLUMN_NORMAL_HEIGHT;
    private final int COLUMN_WIDTH;
    private final int EVENT_TEXT_MARGIN;
    private final int EVENTS_MAX_COUNTS = 6;
    private final int HEADER_TEXT_HEIGHT;

    private int START_INDEX;
    private int END_INDEX;
    private int ROWS_COUNT = 0;

    private Calendar[] daysOfWeek;

    private List<EventData> eventCellsList = new ArrayList<>();
    private OnEventItemClickListener onEventItemClickListener;
    private SparseArray<ItemCell> ITEM_LAYOUT_CELLS = new SparseArray<>(7);

    public WeekHeaderView setOnEventItemClickListener(OnEventItemClickListener onEventItemClickListener)
    {
        this.onEventItemClickListener = onEventItemClickListener;
        return this;
    }

    public WeekHeaderView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        setBackgroundColor(Color.WHITE);
        setWillNotDraw(false);

        TEXT_MARGIN_TB = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics());
        SPACING_BETWEEN_DATE_AND_DAY = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, getResources().getDisplayMetrics());
        HEADER_TEXT_SIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13f, getResources().getDisplayMetrics());
        EVENT_TEXT_SIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, getResources().getDisplayMetrics());
        SPACING_EVENT_BAR_TB = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, getResources().getDisplayMetrics());
        EVENT_BAR_LR_MARGIN = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics());
        EVENT_TEXT_MARGIN = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics());
        COLUMN_NORMAL_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 34f, getResources().getDisplayMetrics());
        int dp6 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, getResources().getDisplayMetrics());

        DIVIDING_LINE_PAINT = new Paint();
        DIVIDING_LINE_PAINT.setColor(Color.GRAY);

        // 날짜 paint
        DATE_TEXT_PAINT = new TextPaint();
        DATE_TEXT_PAINT.setTextAlign(Paint.Align.CENTER);
        DATE_TEXT_PAINT.setColor(Color.GRAY);
        DATE_TEXT_PAINT.setTextSize(HEADER_TEXT_SIZE);

        // 요일 paint
        DAY_TEXT_PAINT = new TextPaint();
        DAY_TEXT_PAINT.setTextAlign(Paint.Align.CENTER);
        DAY_TEXT_PAINT.setColor(Color.GRAY);
        DAY_TEXT_PAINT.setTextSize(HEADER_TEXT_SIZE);

        // 주차 paint
        WEEK_OF_YEAR_TEXT_PAINT = new TextPaint();
        WEEK_OF_YEAR_TEXT_PAINT.setTextAlign(Paint.Align.CENTER);
        WEEK_OF_YEAR_TEXT_PAINT.setColor(Color.WHITE);
        WEEK_OF_YEAR_TEXT_PAINT.setTextSize(HEADER_TEXT_SIZE);

        WEEK_OF_YEAR_RECT_PAINT = new Paint();
        WEEK_OF_YEAR_RECT_PAINT.setColor(Color.GRAY);

        Rect rect = new Rect();
        DATE_TEXT_PAINT.getTextBounds("1", 0, 1, rect);
        HEADER_TEXT_HEIGHT = rect.height();

        // google event rect paint
        GOOGLE_EVENT_PAINT = new Paint();

        // local event rect paint
        LOCAL_EVENT_PAINT = new Paint();

        // google event text paint
        GOOGLE_EVENT_TEXT_PAINT = new TextPaint();
        GOOGLE_EVENT_TEXT_PAINT.setTextAlign(Paint.Align.LEFT);

        // local event text paint
        LOCAL_EVENT_TEXT_PAINT = new TextPaint();
        LOCAL_EVENT_TEXT_PAINT.setTextAlign(Paint.Align.LEFT);

        GOOGLE_EVENT_TEXT_PAINT.setTextSize(EVENT_TEXT_SIZE);
        LOCAL_EVENT_TEXT_PAINT.setTextSize(EVENT_TEXT_SIZE);

        GOOGLE_EVENT_TEXT_PAINT.getTextBounds("1", 0, 1, rect);

        EVENT_BAR_HEIGHT = rect.height() + EVENT_TEXT_MARGIN * 2;
        COLUMN_WIDTH = AppMainActivity.getDisplayWidth() / 8;

        WEEK_OF_YEAR_RECT = new Rect();
        WEEK_OF_YEAR_RECT.left = dp6;
        WEEK_OF_YEAR_RECT.right = COLUMN_WIDTH - dp6;
        WEEK_OF_YEAR_RECT.top = dp6;
        WEEK_OF_YEAR_RECT.bottom = (int) (WEEK_OF_YEAR_RECT.top + HEADER_TEXT_HEIGHT + TEXT_MARGIN_TB);

        MORE_EVENTS_RECT_PAINT = new Paint();
        MORE_EVENTS_RECT_PAINT.setColor(Color.LTGRAY);

        MORE_EVENTS_TEXT_PAINT = new TextPaint();
        MORE_EVENTS_TEXT_PAINT.setColor(Color.WHITE);
        MORE_EVENTS_TEXT_PAINT.setTextSize(EVENT_TEXT_SIZE);
        MORE_EVENTS_TEXT_PAINT.setTextAlign(Paint.Align.LEFT);

        TypedValue backgroundValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, backgroundValue, true);

        //열 추가
        for (int i = 0; i < 8; i++)
        {
            WeekHeaderColumnView columnView = new WeekHeaderColumnView(context, i - 1);
            columnView.setClickable(true);
            columnView.setBackgroundResource(backgroundValue.resourceId);
            columnView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    int position = ((WeekHeaderColumnView) view).getPosition();
                    onEventItemClickListener.onClicked(daysOfWeek[position].getTime(), daysOfWeek[position + 1].getTime());
                }
            });
            addView(columnView);
        }
        View firstChild = getChildAt(0);
        firstChild.setClickable(false);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int eventsRowHeight = 0;
        if (ROWS_COUNT > 0)
        {
            eventsRowHeight = (int) ((EVENT_BAR_HEIGHT + SPACING_EVENT_BAR_TB) * (ROWS_COUNT - 1) + EVENT_BAR_HEIGHT);
        }
        setMeasuredDimension(widthMeasureSpec, (int) (COLUMN_NORMAL_HEIGHT + eventsRowHeight));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        measureChildren(COLUMN_WIDTH, bottom);
        for (int i = 0; i < 8; i++)
        {
            View child = getChildAt(i);
            child.layout(COLUMN_WIDTH * i, top, COLUMN_WIDTH * (i + 1), bottom);
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawHeaderView(canvas);

        if (!eventCellsList.isEmpty())
        {
            GOOGLE_EVENT_PAINT.setColor(AppSettings.getGoogleEventBackgroundColor());
            LOCAL_EVENT_PAINT.setColor(AppSettings.getLocalEventBackgroundColor());
            GOOGLE_EVENT_TEXT_PAINT.setColor(AppSettings.getGoogleEventTextColor());
            LOCAL_EVENT_TEXT_PAINT.setColor(AppSettings.getLocalEventTextColor());

            drawEvents(canvas);
        }
    }

    private void drawHeaderView(Canvas canvas)
    {
        final int START_X = getWidth() / 8;
        // 몇 번째 주인지 표시
        canvas.drawRect(WEEK_OF_YEAR_RECT, WEEK_OF_YEAR_RECT_PAINT);
        canvas.drawText(Integer.toString(weekFirstDay.get(Calendar.WEEK_OF_YEAR)),
                WEEK_OF_YEAR_RECT.centerX(), WEEK_OF_YEAR_RECT.centerY() + HEADER_TEXT_HEIGHT / 2, WEEK_OF_YEAR_TEXT_PAINT);

        final float dayY = COLUMN_NORMAL_HEIGHT / 2 - SPACING_BETWEEN_DATE_AND_DAY / 2 - DAY_TEXT_PAINT.descent();
        final float dateY = COLUMN_NORMAL_HEIGHT / 2 + SPACING_BETWEEN_DATE_AND_DAY / 2 + HEADER_TEXT_HEIGHT;

        // 요일, 날짜를 그림
        for (int i = 0; i < 7; i++)
        {
            canvas.drawText(DateHour.getDayString(i), START_X + COLUMN_WIDTH / 2 + COLUMN_WIDTH * i, dayY, DAY_TEXT_PAINT);
            canvas.drawText(DateHour.getDate(daysOfWeek[i].getTime()), START_X + COLUMN_WIDTH / 2 + COLUMN_WIDTH * i, dateY, DATE_TEXT_PAINT);
        }
    }

    private void drawEvents(Canvas canvas)
    {
        int leftMargin = 0;
        int rightMargin = 0;

        float startX = 0f;
        float startY = 0f;
        float endX = 0f;

        float left = 0;
        float right = 0;
        float top = 0;
        float bottom = 0;

        for (EventData eventData : eventCellsList)
        {
            ScheduleDTO schedule = eventData.getSchedule();
            int startIndex = eventData.getStartIndex();
            int endIndex = eventData.getEndIndex();
            int row = eventData.getRow();
            int dateLength = eventData.getDateLength();

            // 이번 주 이전 - 이번 주 이후
            if (dateLength == EventData.BEFORE_AFTER)
            {
                leftMargin = 0;
                rightMargin = 0;
            } else if (dateLength == EventData.BEFORE_THISWEEK)
            {
                // 이번 주 이전 - 이번 주 내
                leftMargin = 0;
                rightMargin = EVENT_BAR_LR_MARGIN;
            } else if (dateLength == EventData.THISWEEK_AFTER)
            {
                // 이번 주 내 - 이번 주 이후
                leftMargin = EVENT_BAR_LR_MARGIN;
                rightMargin = 0;
            } else if (dateLength == EventData.THISWEEK_THISWEEK)
            {
                // 이번 주 내 - 이번 주 내
                leftMargin = EVENT_BAR_LR_MARGIN;
                rightMargin = EVENT_BAR_LR_MARGIN;
            }

            startX = COLUMN_WIDTH * (startIndex + 1);
            endX = COLUMN_WIDTH * (endIndex + 2);
            startY = COLUMN_NORMAL_HEIGHT + (EVENT_BAR_HEIGHT + SPACING_EVENT_BAR_TB) * row;

            top = startY;
            bottom = top + EVENT_BAR_HEIGHT;
            left = startX + leftMargin;
            right = endX - rightMargin;

            if (schedule.getCategory() == ScheduleDTO.GOOGLE_CATEGORY)
            {
                canvas.drawRect(left, top, right, bottom, GOOGLE_EVENT_PAINT);
                canvas.drawText(schedule.getSubject(), left + EVENT_TEXT_MARGIN, bottom - EVENT_TEXT_MARGIN, GOOGLE_EVENT_TEXT_PAINT);
            } else
            {
                canvas.drawRect(left, top, right, bottom, LOCAL_EVENT_PAINT);
                canvas.drawText(schedule.getSubject(), left + EVENT_TEXT_MARGIN, bottom - EVENT_TEXT_MARGIN, LOCAL_EVENT_TEXT_PAINT);
            }
        }

        // more 표시
        for (int index = START_INDEX; index <= END_INDEX; index++)
        {
            if (ITEM_LAYOUT_CELLS.get(index) == null)
            {
                continue;
            } else
            {
                // 날짜의 이벤트 개수 > 뷰에 표시된 이벤트의 개수 인 경우 마지막 행에 More를 표시
                if (ITEM_LAYOUT_CELLS.get(index).eventsCount >= EVENTS_MAX_COUNTS)
                {
                    startX = COLUMN_WIDTH * (index + 1);
                    endX = COLUMN_WIDTH * (index + 2);
                    startY = COLUMN_NORMAL_HEIGHT + (EVENT_BAR_HEIGHT + SPACING_EVENT_BAR_TB) * (EVENTS_MAX_COUNTS - 1);

                    top = startY;
                    bottom = top + EVENT_BAR_HEIGHT;
                    left = startX + EVENT_BAR_LR_MARGIN;
                    right = endX - EVENT_BAR_LR_MARGIN;

                    canvas.drawRect(left, top, right, bottom, MORE_EVENTS_RECT_PAINT);
                    canvas.drawText("More", startX + EVENT_TEXT_MARGIN, bottom - EVENT_TEXT_MARGIN, MORE_EVENTS_TEXT_PAINT);
                }
            }
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
    }

    public void setSchedules(List<EventData> list)
    {
        // 이벤트 테이블에 데이터를 표시할 위치 설정
        setEventTable(list);
        requestLayout();
        invalidate();
    }

    private void setEventTable(List<EventData> list)
    {
        ITEM_LAYOUT_CELLS.clear();
        eventCellsList.clear();

        START_INDEX = Integer.MAX_VALUE;
        END_INDEX = Integer.MIN_VALUE;

        ROWS_COUNT = 0;

        // 달력 뷰의 셀에 아이템을 삽입
        for (EventData eventData : list)
        {
            int startIndex = eventData.getStartIndex();
            int endIndex = eventData.getEndIndex();
            int dateLength = 0;

            if (startIndex == -1 && endIndex == 7)
            {
                dateLength = EventData.BEFORE_AFTER;
            } else if (startIndex == -1 && endIndex <= 6)
            {
                // 이번 주 이전 - 이번 주 내
                dateLength = EventData.BEFORE_THISWEEK;
            } else if (startIndex >= 0 && endIndex == 7)
            {
                // 이번 주 내 - 이번 주 이후
                dateLength = EventData.THISWEEK_AFTER;
            } else if (startIndex >= 0 && endIndex <= 6)
            {
                // 이번 주 내 - 이번 주 내
                dateLength = EventData.THISWEEK_THISWEEK;
            }

            if (startIndex == -1)
            {
                startIndex = 0;
            }
            if (endIndex == 7)
            {
                endIndex = 6;
            }

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

                for (int row = 0; row < EVENTS_MAX_COUNTS; row++)
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

            if (rowSet.isEmpty())
            {
                // 가능한 행이 없는 경우
                // 미 표시
            } else
            {
                Iterator<Integer> iterator = rowSet.iterator();
                int row = iterator.next();

                if (row > ROWS_COUNT)
                {
                    ROWS_COUNT = row;
                }

                if (row < EVENTS_MAX_COUNTS - 1)
                {
                    // 셀에 삽입된 아이템의 위치를 알맞게 조정
                    // 같은 일정은 같은 위치의 셀에 있어야 한다.
                    // row가 MonthCalendarItemView.EVENT_COUNT - 1인 경우 빈 객체를 저장
                    for (int i = startIndex; i <= endIndex; i++)
                    {
                        ITEM_LAYOUT_CELLS.get(i).rows[row] = true;
                    }
                    eventCellsList.add(eventData.setStartIndex(startIndex).setEndIndex(endIndex).setRow(row).setDateLength(dateLength));
                }
            }
        }
        ROWS_COUNT++;
    }

    class ItemCell
    {
        boolean[] rows;
        int eventsCount;

        public ItemCell()
        {
            rows = new boolean[EVENTS_MAX_COUNTS];
        }
    }

    class WeekHeaderColumnView extends View
    {
        private int position;

        public WeekHeaderColumnView(Context context, int position)
        {
            super(context);
            this.position = position;
        }

        public int getPosition()
        {
            return position;
        }
    }
}
