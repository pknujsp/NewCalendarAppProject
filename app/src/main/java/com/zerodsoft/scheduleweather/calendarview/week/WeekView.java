package com.zerodsoft.scheduleweather.calendarview.week;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;

import com.zerodsoft.scheduleweather.calendarfragment.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarfragment.WeekFragment;
import com.zerodsoft.scheduleweather.calendarview.HourEventsView;
import com.zerodsoft.scheduleweather.calendarview.month.EventData;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.utility.AppSettings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class WeekView extends HourEventsView
{
    private boolean createdAddScheduleRect = false;
    private boolean changingStartTime = false;
    private boolean changingEndTime = false;

    private OverScroller overScroller;
    private GestureDetectorCompat gestureDetector;
    private OnSwipeListener onSwipeListener;

    private Calendar[] daysOfWeek;
    private SparseArray<List<ItemCell>> eventSparseArr = new SparseArray<>(7);
    private final int SPACING_BETWEEN_EVENTS = 5;


    public WeekView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        gestureDetector = new GestureDetectorCompat(context, onGestureListener);
        overScroller = new OverScroller(context);
    }

    public void setOnSwipeListener(OnSwipeListener onSwipeListener)
    {
        this.onSwipeListener = onSwipeListener;
    }

    public void setDaysOfWeek(Calendar[] daysOfWeek)
    {
        this.daysOfWeek = daysOfWeek;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);
        /*
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
         */

        Calendar calendar = Calendar.getInstance();

        float left = 0f;
        float top = 0f;
        float right = 0f;
        float bottom = 0f;
        float cellWidth = 0f;

        for (int index = 0; index < 7; index++)
        {
            List<ItemCell> itemCells = eventSparseArr.get(index);
            if (itemCells != null)
            {
                for (int i = 0; i < itemCells.size(); i++)
                {
                    int column = itemCells.get(i).column;
                    int columnCount = itemCells.get(i).columnCount;

                    calendar.setTime(itemCells.get(i).schedule.getStartDate());
                    PointF startPoint = getPoint(calendar, index);
                    calendar.setTime(itemCells.get(i).schedule.getEndDate());
                    PointF endPoint = getPoint(calendar, index);

                    cellWidth = (WeekFragment.getColumnWidth() - (SPACING_BETWEEN_EVENTS * (columnCount - 1))) / columnCount;

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

                    View childView = getChildAt(index);
                    childView.measure((int) (right - left), (int) (bottom - top));
                    childView.layout((int) left, (int) top, (int) right, (int) bottom);
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        for (int i = 2; i <= 7; i++)
        {
            // 세로 선
            canvas.drawLine(WeekFragment.getColumnWidth() * i, startY, WeekFragment.getColumnWidth() * i, startY + VIEW_HEIGHT - TABLE_TB_MARGIN, DIVIDING_LINE_PAINT);
        }
        if (eventSparseArr.size() >= 1)
        {
            GOOGLE_EVENT_PAINT.setColor(AppSettings.getGoogleEventBackgroundColor());
            LOCAL_EVENT_PAINT.setColor(AppSettings.getLocalEventBackgroundColor());
            GOOGLE_EVENT_TEXT_PAINT.setColor(AppSettings.getGoogleEventTextColor());
            LOCAL_EVENT_TEXT_PAINT.setColor(AppSettings.getLocalEventTextColor());

            drawEvents(canvas);
            for (int i = 0; i < getChildCount(); i++)
            {
                getChildAt(i).invalidate();
            }
        }
        drawWeekView(canvas);
    }

    private void drawEvents(Canvas canvas)
    {
        /*
        Calendar calendar = Calendar.getInstance();

        float left = 0f;
        float top = 0f;
        float right = 0f;
        float bottom = 0f;
        float cellWidth = 0f;

        for (int index = 0; index < 7; index++)
        {
            List<ItemCell> itemCells = eventSparseArr.get(index);
            if (itemCells != null)
            {
                for (int i = 0; i < itemCells.size(); i++)
                {
                    int column = itemCells.get(i).column;
                    int columnCount = itemCells.get(i).columnCount;

                    calendar.setTime(itemCells.get(i).schedule.getStartDate());
                    PointF startPoint = getPoint(calendar, index);
                    calendar.setTime(itemCells.get(i).schedule.getEndDate());
                    PointF endPoint = getPoint(calendar, index);

                    cellWidth = (WeekFragment.getColumnWidth() - (SPACING_BETWEEN_EVENTS * (columnCount - 1))) / columnCount;

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

                    if (itemCells.get(i).schedule.getCategory() == ScheduleDTO.GOOGLE_CATEGORY)
                    {
                        canvas.drawRect(left, top, right, bottom, GOOGLE_EVENT_PAINT);
                        canvas.drawText(itemCells.get(i).schedule.getSubject(), left + TEXT_MARGIN, top + EVENT_TEXT_HEIGHT + TEXT_MARGIN, GOOGLE_EVENT_TEXT_PAINT);
                    } else
                    {
                        canvas.drawRect(left, top, right, bottom, LOCAL_EVENT_PAINT);
                        canvas.drawText(itemCells.get(i).schedule.getSubject(), left + TEXT_MARGIN, top + EVENT_TEXT_HEIGHT + TEXT_MARGIN, LOCAL_EVENT_TEXT_PAINT);
                    }
                }
            }
        }

         */
    }

    private PointF getPoint(Calendar time, int index)
    {
        PointF point = new PointF(0f, 0f);

        // x
        point.x = WeekFragment.getColumnWidth() * (index + 1);

        /* 
           가로선 그리기
          canvas.drawLine(WeekFragment.getColumnWidth(), startY + (SPACING_BETWEEN_HOURS * i) + TABLE_TB_MARGIN, VIEW_WIDTH,
           startY + (SPACING_BETWEEN_HOURS * i) + TABLE_TB_MARGIN, DIVIDING_LINE_PAINT);
         */

        // y
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);

        float hourY = startY + (SPACING_BETWEEN_HOURS * hour) + TABLE_TB_MARGIN;
        float heightPerMinute = SPACING_BETWEEN_HOURS / 60f;

        point.y = hourY + heightPerMinute * minute;

        return point;
    }

    private void drawWeekView(Canvas canvas)
    {
        /*
        if (createdAddScheduleRect)
        {
            // 일정 추가 사각형 코드
            rectStartPoint = getTimePoint(TIME_CATEGORY.START);
            rectEndPoint = getTimePoint(TIME_CATEGORY.END);
            NEW_SCHEDULE_RECT_DRAWABLE.setBounds((int) rectStartPoint.x, (int) rectStartPoint.y, (int) rectEndPoint.x + WeekFragment.getColumnWidth(), (int) rectEndPoint.y);
            NEW_SCHEDULE_RECT_DRAWABLE.draw(canvas);
        }
         */
    }

    /*
    private PointF getTimePoint(TIME_CATEGORY timeCategory)
    {
        Calendar time = null;

        if (timeCategory == TIME_CATEGORY.START)
        {
            time = (Calendar) startTime.clone();
        } else
        {
            time = (Calendar) endTime.clone();
        }

        PointF point = new PointF(0f, 0f);

        // x
        for (int i = 0; i <= 6; i++)
        {
            if (isSameDay(time, coordinateInfos[i].getDate()))
            {
                point.x = coordinateInfos[i].getStartX();
                break;
            } else if (time.get(Calendar.HOUR_OF_DAY) == 0 && timeCategory == TIME_CATEGORY.END)
            {
                // endTime이 다음 날 오전12시 이후인 경우
                Calendar t = (Calendar) endTime.clone();
                t.add(Calendar.DATE, -1);
                if (t.get(Calendar.DATE) == coordinateInfos[i].getDate().get(Calendar.DATE))
                {
                    point.x = coordinateInfos[i].getStartX();
                    break;
                }
            }
        }

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




    private boolean setStartTime(float x, float y)
    {
        if (coordinateInfos == null)
        {
            coordinateInfos = coordinateInfoInterface.getArray();
        }

        for (int i = 0; i <= 6; i++)
        {
            if (x >= coordinateInfos[i].getStartX() && x < coordinateInfos[i].getEndX())
            {
                startTime = (Calendar) coordinateInfos[i].getDate().clone();
                break;
            }
        }

        float startHour, endHour;

        for (int i = 0; i <= 23; i++)
        {
            startHour = currentTouchedPoint.y + SPACING_BETWEEN_HOURS * i;
            endHour = currentTouchedPoint.y + SPACING_BETWEEN_HOURS * (i + 1);

            if (y >= startHour && y < endHour)
            {
                float minute15Height = (endHour - startHour) / 4f;
                y = y - startHour;

                for (int j = 0; j <= 3; j++)
                {
                    if (y >= minute15Height * j && y <= minute15Height * (j + 1))
                    {
                        int year = startTime.get(Calendar.YEAR), month = startTime.get(Calendar.MONTH), date = startTime.get(Calendar.DAY_OF_MONTH);
                        startTime.set(year, month, date, i, j * 15);
                        endTime = (Calendar) startTime.clone();
                        endTime.add(Calendar.HOUR_OF_DAY, 1);

                        return true;
                    }
                }
            }
        }
        return false;

    }

     */

    public void clear()
    {
        eventSparseArr.clear();
    }

    public void setSchedules(List<EventData> list)
    {
        // 이벤트 테이블에 데이터를 표시할 위치 설정
        // 데이터가 없는 경우 진행하지 않음
        eventSparseArr.clear();

        if (!list.isEmpty())
        {
            setEventTable(list);
            requestLayout();
            invalidate();
        }
    }

    private void setEventTable(List<EventData> list)
    {
        // 데이터를 리스트에 저장
        for (EventData eventData : list)
        {
            int index = eventData.getStartIndex();
            if (eventSparseArr.get(index) == null)
            {
                eventSparseArr.put(index, new ArrayList<>());
            }
            eventSparseArr.get(index).add(new ItemCell(eventData.getSchedule()));
        }

        // 저장된 데이터가 표시될 위치를 설정
        for (int index = 0; index < 7; index++)
        {
            List<ItemCell> itemCells = eventSparseArr.get(index);

            if (itemCells != null)
            {
                if (itemCells.size() >= 2)
                {
                    // 일정 길이의 내림차순으로 정렬
                    Collections.sort(itemCells, cellComparator);

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
                }
            }
        }

        removeAllViews();

        for (int index = 0; index < 7; index++)
        {
            List<ItemCell> itemCells = eventSparseArr.get(index);

            if (itemCells != null)
            {
                for (int i = 0; i < itemCells.size(); i++)
                {
                    WeekItemView child = new WeekItemView(context, itemCells.get(i));
                    addView(child);
                    child.setClickable(true);
                    child.setOnClickListener(new OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            String subject = ((WeekItemView) view).itemCell.schedule.getSubject();
                            Toast.makeText(context, subject, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
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

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        currentTouchedPoint.x = event.getX();
        currentTouchedPoint.y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            if (currentScrollDirection == SCROLL_DIRECTION.VERTICAL)
            {
                currentScrollDirection = SCROLL_DIRECTION.FINISHED;
                onSwipeListener.onSwiped(false);
            } else if (changingStartTime || changingEndTime)
            {
                if (changingStartTime)
                {
                    changingStartTime = false;
                } else
                {
                    changingEndTime = false;
                }
                invalidate();
                Toast.makeText(context, "start : " + startTime.get(Calendar.HOUR_OF_DAY) + " : " + startTime.get(Calendar.MINUTE) + "\n"
                        + "end : " + endTime.get(Calendar.HOUR_OF_DAY) + " : " + endTime.get(Calendar.MINUTE), Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll()
    {
        super.computeScroll();
        if (overScroller.computeScrollOffset())
        {
            startY = overScroller.getCurrY();
            ViewCompat.postInvalidateOnAnimation(WeekView.this);
        }
    }


    private final GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener()
    {
        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            if (createdAddScheduleRect)
            {
                createdAddScheduleRect = false;
                invalidate();
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            // e1 firstDown, e2 move
            if (createdAddScheduleRect)
            {
                // 시작, 종료 날짜를 조절하는 코드
                if (!changingStartTime && !changingEndTime)
                {
                    if ((e1.getX() >= rectStartPoint.x && e1.getX() < rectStartPoint.x + WeekFragment.getColumnWidth()) &&
                            (e1.getY() >= rectStartPoint.y - 30f && e1.getY() < rectStartPoint.y + 30f))
                    {
                        changingStartTime = true;
                        return true;
                    } else if ((e1.getX() >= rectEndPoint.x && e1.getX() < rectEndPoint.x + WeekFragment.getColumnWidth()) &&
                            (e1.getY() >= rectEndPoint.y - 30f && e1.getY() < rectEndPoint.y + 30f))
                    {
                        changingEndTime = true;
                        return true;
                    }
                } else
                {
                    // start or endtime을 수정중인 경우
                    if (changingStartTime)
                    {
                        changeTime(e2.getY(), TIME_CATEGORY.START);
                    } else if (changingEndTime)
                    {
                        changeTime(e2.getY(), TIME_CATEGORY.END);
                    }
                    ViewCompat.postInvalidateOnAnimation(WeekView.this);
                    return true;
                }
            }

            if (currentScrollDirection == SCROLL_DIRECTION.FINISHED)
            {
                currentScrollDirection = SCROLL_DIRECTION.NONE;
            } else if (currentScrollDirection == SCROLL_DIRECTION.NONE)
            {
                currentScrollDirection = SCROLL_DIRECTION.VERTICAL;
                onSwipeListener.onSwiped(true);
            }

            if (currentScrollDirection == SCROLL_DIRECTION.VERTICAL)
            {
                startY -= distanceY;

                if (startY >= maxStartY)
                {
                    startY = maxStartY;
                } else if (startY <= minStartY)
                {
                    startY = minStartY;
                }
            }
            ViewCompat.postInvalidateOnAnimation(WeekView.this);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            fling(0f, velocityY);
            Log.e("velocity : ", String.valueOf(velocityY));
            return true;
        }

        private void fling(float velocityX, float velocityY)
        {
            overScroller.fling(0, (int) startY, 0, (int) velocityY, 0, 0, (int) minStartY, (int) maxStartY, 0, 0);
            ViewCompat.postInvalidateOnAnimation(WeekView.this);
        }

        @Override
        public boolean onDown(MotionEvent e)
        {
            overScroller.forceFinished(true);
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e)
        {
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e)
        {
            /*
            if (setStartTime(e.getX(), e.getY()))
            {
                createdAddScheduleRect = true;
                invalidate();
            }
             */
        }
    };

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

    private final Comparator<ItemCell> cellComparator = new Comparator<ItemCell>()
    {
        @Override
        public int compare(ItemCell t1, ItemCell t2)
        {
            if ((t1.schedule.getEndDate().getTime() - t1.schedule.getStartDate().getTime()) <=
                    (t2.schedule.getEndDate().getTime() - t2.schedule.getStartDate().getTime()))
            {
                return 1;
            } else
            {
                return -1;
            }
        }
    };

    class WeekItemView extends View
    {
        public ItemCell itemCell;

        public WeekItemView(Context context, ItemCell itemCell)
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
                canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), GOOGLE_EVENT_PAINT);
                canvas.drawText(itemCell.schedule.getSubject(), getLeft() + TEXT_MARGIN, getTop() + EVENT_TEXT_HEIGHT + TEXT_MARGIN, GOOGLE_EVENT_TEXT_PAINT);
            } else
            {
                canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), LOCAL_EVENT_PAINT);
                canvas.drawText(itemCell.schedule.getSubject(), getLeft() + TEXT_MARGIN, getTop() + EVENT_TEXT_HEIGHT + TEXT_MARGIN, LOCAL_EVENT_TEXT_PAINT);
            }
        }
    }
}