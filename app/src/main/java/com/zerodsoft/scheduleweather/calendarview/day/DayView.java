package com.zerodsoft.scheduleweather.calendarview.day;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.OverScroller;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;

import com.zerodsoft.scheduleweather.calendarview.HourEventsView;
import com.zerodsoft.scheduleweather.calendarview.dto.CoordinateInfo;
import com.zerodsoft.scheduleweather.calendarview.week.WeekView;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.utility.DateHour;

import java.util.Calendar;
import java.util.Date;

public class DayView extends HourEventsView
{
    private boolean createdAddScheduleRect = false;
    private boolean changingStartTime = false;
    private boolean changingEndTime = false;

    private OverScroller overScroller;
    private GestureDetectorCompat gestureDetector;

    private Calendar date;

    public DayView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
      //  gestureDetector = new GestureDetectorCompat(context, onGestureListener);
      //  overScroller = new OverScroller(context);
    }

    public void setDate(Date date)
    {
        this.date = Calendar.getInstance();
        this.date.setTime(date);
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawView(canvas);
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

    private PointF getPoint(Calendar time, int dayOfWeek)
    {
        PointF point = new PointF(0f, 0f);

        // y
        float hourY = currentTouchedPoint.y + SPACING_BETWEEN_HOURS * time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);
        float minute1Height = SPACING_BETWEEN_HOURS / 60f;

        point.y = hourY + minute1Height * minute;

        return point;
    }

    private boolean setStartTime(float y)
    {
        // 터치한 y좌표에 해당하는 start시간을 설정한다
        startTime = (Calendar) date.clone();
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
                        int year = startTime.get(Calendar.YEAR);
                        int month = startTime.get(Calendar.MONTH);
                        int date = startTime.get(Calendar.DAY_OF_MONTH);

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


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        /*
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            if (currentScrollDirection == SCROLL_DIRECTION.VERTICAL)
            {
                currentScrollDirection = SCROLL_DIRECTION.FINISHED;
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

         */
        return true;
    }

    private final GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener()
    {
        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            // 한번 빠르게 터치했다가 뗐을때 실행됨
            if (createdAddScheduleRect)
            {
                createdAddScheduleRect = false;
                DayView.this.invalidate();
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
                    // 새로운 일정 생성 박스가 생성되었으나, 스크롤이 첫 진행중인 경우
                    if (e1.getY() >= rectStartPoint.y - 30f && e1.getY() < rectStartPoint.y + 30f)
                    {
                        changingStartTime = true;
                        return true;
                    } else if (e1.getY() >= rectEndPoint.y - 30f && e1.getY() < rectEndPoint.y + 30f)
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
                    ViewCompat.postInvalidateOnAnimation(DayView.this);
                    return true;
                }
            }

            if (currentScrollDirection == SCROLL_DIRECTION.FINISHED)
            {
                currentScrollDirection = SCROLL_DIRECTION.NONE;
            } else if (currentScrollDirection == SCROLL_DIRECTION.NONE)
            {
                currentScrollDirection = SCROLL_DIRECTION.VERTICAL;
            }


            ViewCompat.postInvalidateOnAnimation(DayView.this);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            fling(velocityX, velocityY);
            return true;
        }

        private void fling(float velocityX, float velocityY)
        {
          //  overScroller.fling(0, (int) currentTouchedPoint.y, 0, (int) velocityY, 0, 0, (int) minStartY, (int) maxStartY, 0, 0);
         //   ViewCompat.postInvalidateOnAnimation(DayView.this);
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
            // 롱 클릭을 했을 때 실행됨
            // 롱 클릭한 좌표의 X가 테이블 내로 들어와있어야 함
            if (e.getX() >= HOUR_TEXT_BOX_RECT.width())
            {
                if (setStartTime(e.getY()))
                {
                    createdAddScheduleRect = true;
                    invalidate();
                }
            }

             */
        }
    };

    @Override
    public void computeScroll()
    {
        super.computeScroll();
        /*
        if (overScroller.computeScrollOffset())
        {
            currentTouchedPoint.y = overScroller.getCurrY();
            ViewCompat.postInvalidateOnAnimation(this);
        }

         */
    }

    private void drawEvents(Canvas canvas)
    {
        RectF rect = new RectF();

        /*
        for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++)
        {
            for (EventDrawingInfo eventDrawingInfo : eventDrawingInfoList.get(dayOfWeek))
            {
                if (eventDrawingInfo.getAccountType() == AccountType.GOOGLE)
                {
                    rect.set(eventDrawingInfo.getStartPoint().x, eventDrawingInfo.getStartPoint().y,
                            eventDrawingInfo.getEndPoint().x, eventDrawingInfo.getEndPoint().y);
                    canvas.drawRect(rect, GOOGLE_EVENT_PAINT);
                    canvas.drawText(eventDrawingInfo.getSchedule().getSubject(),
                            (eventDrawingInfo.getEndPoint().x - eventDrawingInfo.getStartPoint().x) / 2, eventDrawingInfo.getEndPoint().y, GOOGLE_EVENT_TEXT_PAINT);
                } else
                {
                    rect.set(eventDrawingInfo.getStartPoint().x, eventDrawingInfo.getStartPoint().y,
                            eventDrawingInfo.getEndPoint().x, eventDrawingInfo.getEndPoint().y);
                    canvas.drawRect(rect, LOCAL_EVENT_PAINT);
                    canvas.drawText(eventDrawingInfo.getSchedule().getSubject(),
                            (eventDrawingInfo.getEndPoint().x - eventDrawingInfo.getStartPoint().x) / 2, eventDrawingInfo.getEndPoint().y, LOCAL_EVENT_TEXT_PAINT);
                }

            }
        }

         */
    }

    public void setEventDrawingInfo()
    {
        /*
        int dayOfWeek;
        PointF startPoint = new PointF();
        PointF endPoint = new PointF();

        for (ScheduleDTO schedule : scheduleList)
        {
            dayOfWeek = calcEventPosition(startPoint, endPoint, schedule);
            if (schedule.getCategory() == ScheduleDTO.GOOGLE_CATEGORY)
            {
                eventDrawingInfoList.get(dayOfWeek).add(new EventDrawingInfo(new PointF(startPoint.x, startPoint.y), new PointF(endPoint.x, endPoint.y), schedule, AccountType.GOOGLE));
            } else
            {
                eventDrawingInfoList.get(dayOfWeek).add(new EventDrawingInfo(new PointF(startPoint.x, startPoint.y), new PointF(endPoint.x, endPoint.y), schedule, AccountType.LOCAL));
            }
        }


         */
    }

    private int calcEventPosition(PointF startPoint, PointF endPoint, ScheduleDTO schedule)
    {
        /*
        int dayOfWeek = 0;
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();

        startTime.setTimeInMillis(schedule.getStartDate().getTime());
        endTime.setTimeInMillis(schedule.getEndDate().getTime());

        if (isSameDay(startTime, endTime))
        {
            for (int i = 0; i < 7; i++)
            {
                if (isSameDay(startTime, coordinateInfos[i].getDate()))
                {
                    dayOfWeek = i;
                    break;
                }
            }
            startPoint = getPoint(startTime, dayOfWeek);
            endPoint = getPoint(endTime, dayOfWeek);

            // 다른 이벤트와 시간대가 겹치는 부분이 있는지 여부 확인
            for (EventDrawingInfo eventDrawingInfo : eventDrawingInfoList.get(dayOfWeek))
            {
                // 추가할 이벤트의 진행 시각이 다른 이벤트의 진행 시각을 모두 포함 하는 경우
                if (startTime.getTimeInMillis() <= eventDrawingInfo.getSchedule().getStartDate().getTime()
                        && endTime.getTimeInMillis() >= eventDrawingInfo.getSchedule().getEndDate().getTime())
                {
                    startPoint.x = startPoint.x + 3f;
                }
                // 추가할 이벤트의 시작시각 또는 종료시각이 다른 이벤트의 진행 시각 사이에 있는 경우
                else if ((startTime.getTimeInMillis() >= eventDrawingInfo.getSchedule().getStartDate().getTime()
                        && startTime.getTimeInMillis() < eventDrawingInfo.getSchedule().getEndDate().getTime())
                        || (endTime.getTimeInMillis() > eventDrawingInfo.getSchedule().getStartDate().getTime()
                        && endTime.getTimeInMillis() <= eventDrawingInfo.getSchedule().getEndDate().getTime()))
                {
                    startPoint.x = startPoint.x + 3f;
                }
            }

            return dayOfWeek;
        } else
        {
            // 시작 시각과 종료 시각이 같은 날이 아니므로 그리지않음
            return -1;
        }

         */
        return 1;
    }

}
