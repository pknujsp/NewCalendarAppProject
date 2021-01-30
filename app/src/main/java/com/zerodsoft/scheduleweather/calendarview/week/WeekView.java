package com.zerodsoft.scheduleweather.calendarview.week;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.provider.CalendarContract;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.zerodsoft.scheduleweather.calendarview.hourside.HourEventsView;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.etc.EventViewUtil;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WeekView extends HourEventsView implements IEvent
{
    private boolean createdAddScheduleRect = false;
    private boolean changingStartTime = false;
    private boolean changingEndTime = false;

    private OnSwipeListener onSwipeListener;
    private OnEventItemClickListener onEventItemClickListener;

    private Calendar[] daysOfWeek;
    private List<ContentValues> instances;
    private SparseArray<List<ItemCell>> eventSparseArr = new SparseArray<>(7);
    private final int SPACING_BETWEEN_EVENTS = 5;


    public WeekView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void setOnEventItemClickListener(OnEventItemClickListener onEventItemClickListener)
    {
        this.onEventItemClickListener = onEventItemClickListener;
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
    public void setInstances(List<ContentValues> instances)
    {
        // 1일 이하의 일정만 표시
        this.instances = new ArrayList<>();
        for (ContentValues instance : instances)
        {
            if (ClockUtil.calcDateDifference(ClockUtil.DAY, instance.getAsLong(CalendarContract.Instances.BEGIN), instance.getAsLong(CalendarContract.Instances.END)) == 0)
            {
                this.instances.add(instance);
            }
        }
        setEventTable();
        requestLayout();
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);

        if (eventSparseArr.size() >= 1)
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
                WeekItemView childView = (WeekItemView) getChildAt(i);
                ItemCell itemCell = childView.itemCell;

                int column = itemCell.column;
                int index = itemCell.index;
                int columnCount = itemCell.columnCount;

                calendar.setTimeInMillis(itemCell.instance.getAsLong(CalendarContract.Instances.BEGIN));
                PointF startPoint = getPoint(calendar, index);
                calendar.setTimeInMillis(itemCell.instance.getAsLong(CalendarContract.Instances.END));
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

                int width = (int) (right - left);
                int height = (int) (bottom - top);

                childView.measure(width, height);
                childView.layout((int) left, (int) top, (int) right, (int) bottom);
                childView.setClickable(true);
                childView.setOnClickListener(itemOnClickListener);
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
            canvas.drawLine(WeekFragment.getColumnWidth() * i, 0, WeekFragment.getColumnWidth() * i, VIEW_HEIGHT - TABLE_TB_MARGIN, DIVIDING_LINE_PAINT);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);
    }


    private PointF getPoint(Calendar time, int index)
    {
        PointF point = new PointF(0f, 0f);

        // x
        point.x = WeekFragment.getColumnWidth() * (index + 1);

        // y
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);

        float hourY = SPACING_BETWEEN_HOURS * hour + TABLE_TB_MARGIN;
        float heightPerMinute = SPACING_BETWEEN_HOURS / 60f;
        point.y = hourY + heightPerMinute * minute;

        return point;
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
        removeAllViews();
    }


    @Override
    public void setEventTable()
    {
        eventSparseArr.clear();
        removeAllViews();

        // 데이터를 리스트에 저장
        for (ContentValues instance : instances)
        {
            int index = ClockUtil.calcDateDifference(ClockUtil.DAY, instance.getAsLong(CalendarContract.Instances.BEGIN), daysOfWeek[0].getTimeInMillis());
            if (eventSparseArr.get(index) == null)
            {
                eventSparseArr.put(index, new ArrayList<>());
            }

            ItemCell itemCell = new ItemCell(instance, index);
            eventSparseArr.get(index).add(itemCell);
        }

        // 저장된 데이터가 표시될 위치를 설정
        if (!instances.isEmpty())
        {
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
                                if (isOverlapping(itemCells.get(i).instance, itemCells.get(j).instance))
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

            for (int index = 0; index < 7; index++)
            {
                List<ItemCell> itemCells = eventSparseArr.get(index);

                if (itemCells != null)
                {
                    for (int i = 0; i < itemCells.size(); i++)
                    {
                        WeekItemView child = new WeekItemView(context, itemCells.get(i));
                        this.addView(child);
                    }
                }
            }
        }
    }

    private boolean isOverlapping(ContentValues event1, ContentValues event2)
    {
        long start1 = event1.getAsLong(CalendarContract.Instances.BEGIN);
        long end1 = event1.getAsLong(CalendarContract.Instances.END);

        long start2 = event2.getAsLong(CalendarContract.Instances.BEGIN);
        long end2 = event2.getAsLong(CalendarContract.Instances.END);

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
        /*
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

         */
        return true;
    }

    @Override
    public void computeScroll()
    {
        super.computeScroll();
        /*
        if (overScroller.computeScrollOffset())
        {
            startY = overScroller.getCurrY();
            ViewCompat.postInvalidateOnAnimation(WeekView.this);
        }

         */
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
            // overScroller.fling(0, (int) startY, 0, (int) velocityY, 0, 0, (int) minStartY, (int) maxStartY, 0, 0);
            // ViewCompat.postInvalidateOnAnimation(WeekView.this);
        }

        @Override
        public boolean onDown(MotionEvent e)
        {
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


        }
    };

    static class ItemCell
    {
        public static final int NOT_OVERLAP = -1;
        public int column;
        public int columnCount;
        public int index;
        public ContentValues instance;
        public Paint eventColorPaint;
        public TextPaint eventTextPaint;

        public ItemCell(ContentValues instance, int index)
        {
            this.column = NOT_OVERLAP;
            this.columnCount = 1;
            this.index = index;
            this.instance = instance;
        }
    }

    private final Comparator<ItemCell> cellComparator = new Comparator<ItemCell>()
    {
        @Override
        public int compare(ItemCell t1, ItemCell t2)
        {
            long start1 = t1.instance.getAsLong(CalendarContract.Instances.BEGIN);
            long end1 = t1.instance.getAsLong(CalendarContract.Instances.END);

            long start2 = t2.instance.getAsLong(CalendarContract.Instances.BEGIN);
            long end2 = t2.instance.getAsLong(CalendarContract.Instances.END);

            if ((end1 - start1) <=
                    (end2 - start2))
            {
                return 1;
            } else
            {
                return -1;
            }
        }
    };

    private View.OnClickListener itemOnClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            ContentValues instance = ((WeekItemView) view).itemCell.instance;

            onEventItemClickListener.onClicked(instance.getAsInteger(CalendarContract.Instances.CALENDAR_ID)
                    , instance.getAsLong(CalendarContract.Instances.EVENT_ID));
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

            itemCell.eventColorPaint = EventViewUtil.getEventColorPaint(itemCell.instance.getAsInteger(CalendarContract.Instances.EVENT_COLOR));
            itemCell.eventTextPaint = EventViewUtil.getEventTextPaint(EVENT_TEXT_HEIGHT);

            canvas.drawRect(EVENT_RECT_MARGIN, 0, getWidth() - EVENT_RECT_MARGIN, getHeight(), itemCell.eventColorPaint);
            canvas.drawText(itemCell.instance.getAsString(CalendarContract.Instances.TITLE), TEXT_MARGIN + EVENT_RECT_MARGIN, EVENT_TEXT_HEIGHT + TEXT_MARGIN, itemCell.eventTextPaint);
        }
    }
}