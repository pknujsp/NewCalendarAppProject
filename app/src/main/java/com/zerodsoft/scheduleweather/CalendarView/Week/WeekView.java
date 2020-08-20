package com.zerodsoft.scheduleweather.CalendarView.Week;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;

import com.zerodsoft.scheduleweather.CalendarView.AccountType;
import com.zerodsoft.scheduleweather.CalendarView.Dto.CoordinateInfo;
import com.zerodsoft.scheduleweather.CalendarView.EventDrawingInfo;
import com.zerodsoft.scheduleweather.CalendarView.HoursView;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Room.DTO.ScheduleDTO;
import com.zerodsoft.scheduleweather.Utility.AppSettings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WeekView extends View
{
    private static final String TAG = "WEEK_DAY_VIEW_GESTURE";
    private Context context;
    private boolean createdAddScheduleRect = false;
    private boolean changingStartTime = false;
    private boolean changingEndTime = false;

    private int weekDayHourTextColor;
    private int weekDayHourTextSize;
    private int weekDayViewBackgroundColor;
    private int weekDayViewLineThickness;
    private int weekDayViewLineColor;
    private int spacingLinesBetweenHour;
    private int spacingLinesBetweenDay;
    private int newScheduleRectColor;
    private int newScheduleRectThickness;
    private int hourTextHeight;
    private Rect hourTextBoxRect = new Rect();
    private int width;
    private float tableHeight = 0f;
    private int position;

    private int googleEventColor;
    private int localEventColor;
    private Paint googleEventPaint;
    private Paint localEventPaint;

    private PointF startPoint;
    private PointF endPoint;

    private Paint weekDayHourTextPaint;
    private Paint eventTextPaint;
    private Paint weekDayHorizontalLinePaint;
    private Paint weekDayVerticalLinePaint;
    private Paint weekDayBackgroundPaint;

    private Drawable newScheduleRect;
    private Drawable addDrawable;

    private OverScroller overScroller;
    private GestureDetectorCompat gestureDetector;
    public static PointF currentCoordinate = new PointF(0f, 0f);
    private PointF lastCoordinate = new PointF(0f, 0f);

    private float mDistanceY;
    private float startX = 0f;
    private float startY = 0f;

    private float minStartY;
    private float maxStartY;
    private int TABLE_LAYOUT_MARGIN;

    private Calendar startTime;
    private Calendar endTime;

    private DIRECTION currentScrollDirection = DIRECTION.NONE;
    private TIME_CATEGORY timeCategory = TIME_CATEGORY.NONE;

    private boolean haveEvents = false;
    private List<List<EventDrawingInfo>> eventDrawingInfoList = null;

    enum DIRECTION
    {NONE, VERTICAL, FINISHED}

    enum TIME_CATEGORY
    {NONE, START, END}

    private OnRefreshChildViewListener onRefreshChildViewListener;
    private OnRefreshHoursViewListener onRefreshHoursViewListener;
    private CoordinateInfoInterface coordinateInfoInterface;

    private List<ScheduleDTO> scheduleList;

    private CoordinateInfo[] coordinateInfos;


    public interface OnRefreshHoursViewListener
    {
        void refreshHoursView();
    }

    public interface OnRefreshChildViewListener
    {
        void refreshChildView(int position);
    }

    public interface CoordinateInfoInterface
    {
        CoordinateInfo[] getArray();
    }

    public WeekView setCoordinateInfoInterface(CoordinateInfoInterface coordinateInfoInterface)
    {
        this.coordinateInfoInterface = coordinateInfoInterface;
        return this;
    }

    public WeekView setOnRefreshChildViewListener(OnRefreshChildViewListener onRefreshChildViewListener)
    {
        this.onRefreshChildViewListener = onRefreshChildViewListener;
        return this;
    }

    public WeekView setOnRefreshHoursViewListener(OnRefreshHoursViewListener onRefreshHoursViewListener)
    {
        this.onRefreshHoursViewListener = onRefreshHoursViewListener;
        return this;
    }


    public WeekView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WeekView, 0, 0);

        try
        {
            weekDayHourTextColor = a.getColor(R.styleable.WeekView_WeekDayHourTextColor, weekDayHourTextColor);
            weekDayHourTextSize = a.getDimensionPixelSize(R.styleable.WeekView_WeekDayHourTextSize, weekDayHourTextSize);
            weekDayViewBackgroundColor = a.getColor(R.styleable.WeekView_WeekDayViewBackgroundColor, weekDayViewBackgroundColor);
            weekDayViewLineThickness = a.getDimensionPixelSize(R.styleable.WeekView_WeekDayViewLineThickness, weekDayViewLineThickness);
            weekDayViewLineColor = a.getColor(R.styleable.WeekView_WeekDayViewLineColor, weekDayViewLineColor);
            newScheduleRectColor = a.getColor(R.styleable.WeekView_WeekDayViewNewScheduleRectColor, newScheduleRectColor);
            newScheduleRectThickness = a.getDimensionPixelSize(R.styleable.WeekView_WeekDayViewNewScheduleRectThickness, newScheduleRectThickness);
            TABLE_LAYOUT_MARGIN = weekDayHourTextSize;
            newScheduleRect = context.getDrawable(R.drawable.new_schedule_range_rect);
            addDrawable = context.getDrawable(R.drawable.add_schedule_blue);
        } finally
        {
            a.recycle();
        }
        init();
    }

    private final GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener()
    {

        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            Log.e(TAG, "onSingleTapUp");

            if (createdAddScheduleRect)
            {
                createdAddScheduleRect = false;
                WeekView.this.invalidate();
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
                    if ((e1.getX() >= startPoint.x && e1.getX() < startPoint.x + spacingLinesBetweenDay) &&
                            (e1.getY() >= startPoint.y - 30f && e1.getY() < startPoint.y + 30f))
                    {
                        changingStartTime = true;
                        return true;
                    } else if ((e1.getX() >= endPoint.x && e1.getX() < endPoint.x + spacingLinesBetweenDay) &&
                            (e1.getY() >= endPoint.y - 30f && e1.getY() < endPoint.y + 30f))
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

                    Log.e(TAG, "startTime : " + Integer.toString(startTime.get(Calendar.HOUR_OF_DAY)) + "시 " + Integer.toString(startTime.get(Calendar.MINUTE)) + "분");
                    Log.e(TAG, "endTime : " + Integer.toString(endTime.get(Calendar.HOUR_OF_DAY)) + "시 " + Integer.toString(endTime.get(Calendar.MINUTE)) + "분");
                    Log.e(TAG, "--------------------------");
                    return true;
                }
            }

            if (currentScrollDirection == DIRECTION.FINISHED)
            {
                currentScrollDirection = DIRECTION.NONE;
            } else if (currentScrollDirection == DIRECTION.NONE)
            {
                currentScrollDirection = DIRECTION.VERTICAL;
            }

            if (currentScrollDirection == DIRECTION.VERTICAL)
            {
                currentCoordinate.y -= distanceY;
                mDistanceY = distanceY;

                if (currentCoordinate.y >= maxStartY)
                {
                    currentCoordinate.y = maxStartY;
                } else if (currentCoordinate.y <= minStartY)
                {
                    currentCoordinate.y = minStartY;
                }
            }
            ViewCompat.postInvalidateOnAnimation(WeekView.this);
            onRefreshHoursViewListener.refreshHoursView();
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
            overScroller.fling(0, (int) currentCoordinate.y, 0, (int) velocityY, 0, 0, (int) minStartY, (int) maxStartY, 0, 0);
            onRefreshHoursViewListener.refreshHoursView();
            ViewCompat.postInvalidateOnAnimation(WeekView.this);
        }

        @Override
        public boolean onDown(MotionEvent e)
        {
            overScroller.forceFinished(true);
            lastCoordinate.y = currentCoordinate.y;
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
            if (setStartTime(e.getX(), e.getY()))
            {
                createdAddScheduleRect = true;
                invalidate();
            }
        }
    };


    private void init()
    {
        weekDayHourTextPaint = new Paint();
        weekDayHourTextPaint.setColor(weekDayHourTextColor);
        weekDayHourTextPaint.setTextSize(weekDayHourTextSize);
        weekDayHourTextPaint.setTextAlign(Paint.Align.LEFT);
        weekDayHourTextPaint.getTextBounds("오전 12", 0, "오전 12".length(), hourTextBoxRect);

        spacingLinesBetweenHour = hourTextBoxRect.height() * 4;
        hourTextHeight = hourTextBoxRect.height();

        if (currentCoordinate.y == 0f)
        {
            currentCoordinate.y = (float) TABLE_LAYOUT_MARGIN;
        }
        weekDayBackgroundPaint = new Paint();
        weekDayBackgroundPaint.setColor(weekDayViewBackgroundColor);

        weekDayHorizontalLinePaint = new Paint();
        weekDayHorizontalLinePaint.setColor(weekDayViewLineColor);

        weekDayVerticalLinePaint = new Paint();
        weekDayVerticalLinePaint.setColor(weekDayViewLineColor);

        eventTextPaint = new Paint();
        eventTextPaint.setColor(Color.WHITE);
        eventTextPaint.setTextSize(weekDayHourTextSize);

        googleEventColor = AppSettings.getGoogleEventBackgroundColor();
        localEventColor = AppSettings.getLocalEventBackgroundColor();

        googleEventPaint = new Paint();
        localEventPaint = new Paint();
        googleEventPaint.setColor(googleEventColor);
        localEventPaint.setColor(localEventColor);

        gestureDetector = new GestureDetectorCompat(context, onGestureListener);
        overScroller = new OverScroller(context);

        eventDrawingInfoList = new ArrayList<>();
        for (int i = 0; i < 7; i++)
        {
            // 일 ~ 토 까지 리스트 초기화 (0 ~ 6)
            eventDrawingInfoList.add(new ArrayList<EventDrawingInfo>());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawRect(0f, 0f, getWidth(), getHeight(), weekDayBackgroundPaint);
        drawWeekView(canvas);
    }


    @Override
    public void layout(int l, int t, int r, int b)
    {
        super.layout(l, t, r, b);
        width = getWidth();
        spacingLinesBetweenDay = width / 7;
        minStartY = -(spacingLinesBetweenHour * 24 + TABLE_LAYOUT_MARGIN * 2 - getHeight());
        maxStartY = TABLE_LAYOUT_MARGIN;
        tableHeight = (float) (spacingLinesBetweenHour * 24);
    }


    private void drawWeekView(Canvas canvas)
    {
        startX = currentCoordinate.x;
        startY = currentCoordinate.y;

        for (int i = 0; i <= 23; i++)
        {
            // 가로 선
            canvas.drawLine(0f, startY + (spacingLinesBetweenHour * i), width, startY + (spacingLinesBetweenHour * i), weekDayHorizontalLinePaint);
        }

        for (int i = 0; i <= 7; i++)
        {
            // 이번 주 세로 선
            canvas.drawLine(startX + (spacingLinesBetweenDay * i), startY - hourTextHeight, startX + (spacingLinesBetweenDay * i), startY + tableHeight, weekDayVerticalLinePaint);
        }

        if (createdAddScheduleRect)
        {
            // 일정 추가 사각형 코드
            startPoint = getTimePoint(TIME_CATEGORY.START);
            endPoint = getTimePoint(TIME_CATEGORY.END);

            newScheduleRect.setBounds((int) startPoint.x, (int) startPoint.y, (int) endPoint.x + spacingLinesBetweenDay, (int) endPoint.y);
            newScheduleRect.draw(canvas);
        }
        if (haveEvents)
        {
            drawEvents(canvas);
        }
    }

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
        float startHour = currentCoordinate.y + spacingLinesBetweenHour * time.get(Calendar.HOUR_OF_DAY);
        float endHour = currentCoordinate.y + spacingLinesBetweenHour * (time.get(Calendar.HOUR_OF_DAY) + 1);

        if (time.get(Calendar.HOUR_OF_DAY) == 0 && timeCategory == TIME_CATEGORY.END)
        {
            startHour = currentCoordinate.y + spacingLinesBetweenHour * 24;
            // 다음 날 오전1시
            endHour = currentCoordinate.y + spacingLinesBetweenHour * 25;
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

        // x
        point.x = coordinateInfos[dayOfWeek].getStartX();

        // y
        float hourY = currentCoordinate.y + spacingLinesBetweenHour * time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);
        float minute1Height = spacingLinesBetweenHour / 60f;

        point.y = hourY + minute1Height * minute;

        return point;
    }

    private boolean isSameDay(Calendar day1, Calendar day2)
    {
        return day1.get(Calendar.YEAR) == day2.get(Calendar.YEAR) && day1.get(Calendar.DAY_OF_YEAR) == day2.get(Calendar.DAY_OF_YEAR);
    }

    private boolean isSameClock(Calendar clock1, Calendar clock2)
    {
        return clock1.equals(clock2);
    }

    private void fixTimeError(TIME_CATEGORY timeCategory, Calendar originalTime)
    {
        if (startTime.equals(endTime))
        {
            if (timeCategory == TIME_CATEGORY.START)
            {
                startTime.add(Calendar.MINUTE, -15);
            } else
            {
                endTime.add(Calendar.MINUTE, 15);
            }
        } else if (startTime.after(endTime))
        {
            if (timeCategory == TIME_CATEGORY.START)
            {
                startTime.setTime(originalTime.getTime());
            } else
            {
                endTime.setTime(originalTime.getTime());
            }
        }
    }


    private boolean changeTime(float y, TIME_CATEGORY timeCategory)
    {
        Calendar time = null;

        if (timeCategory == TIME_CATEGORY.START)
        {
            time = startTime;
        } else
        {
            time = endTime;
        }

        float startHour, endHour;
        Calendar originalTime = (Calendar) time.clone();

        for (int i = 0; i <= 23; i++)
        {
            startHour = currentCoordinate.y + spacingLinesBetweenHour * i;
            endHour = currentCoordinate.y + spacingLinesBetweenHour * (i + 1);

            if (y >= startHour && y < endHour)
            {
                float minute15Height = (endHour - startHour) / 4f;
                y = y - startHour;

                for (int j = 0; j <= 3; j++)
                {
                    if (y >= minute15Height * j && y <= minute15Height * (j + 1))
                    {
                        int year = time.get(Calendar.YEAR), month = time.get(Calendar.MONTH), date = time.get(Calendar.DAY_OF_MONTH);
                        int hour = i, minute = j * 15;
                        time.set(year, month, date, hour, minute);
                        fixTimeError(timeCategory, originalTime);

                        return true;
                    }
                }
            }
        }

        return false;
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
            startHour = currentCoordinate.y + spacingLinesBetweenHour * i;
            endHour = currentCoordinate.y + spacingLinesBetweenHour * (i + 1);

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


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            if (currentScrollDirection == DIRECTION.VERTICAL)
            {
                currentScrollDirection = DIRECTION.FINISHED;
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
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public void computeScroll()
    {
        super.computeScroll();
        if (overScroller.computeScrollOffset())
        {
            currentCoordinate.x = overScroller.getCurrX();
            currentCoordinate.y = overScroller.getCurrY();

            ViewCompat.postInvalidateOnAnimation(this);
            onRefreshHoursViewListener.refreshHoursView();
        }
    }

    private void drawEvents(Canvas canvas)
    {
        RectF rect = new RectF();

        for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++)
        {
            for (EventDrawingInfo eventDrawingInfo : eventDrawingInfoList.get(dayOfWeek))
            {
                if (eventDrawingInfo.getAccountType() == AccountType.GOOGLE)
                {
                    rect.set(eventDrawingInfo.getStartPoint().x, eventDrawingInfo.getStartPoint().y,
                            eventDrawingInfo.getEndPoint().x, eventDrawingInfo.getEndPoint().y);
                    canvas.drawRect(rect, googleEventPaint);
                } else
                {
                    rect.set(eventDrawingInfo.getStartPoint().x, eventDrawingInfo.getStartPoint().y,
                            eventDrawingInfo.getEndPoint().x, eventDrawingInfo.getEndPoint().y);
                    canvas.drawRect(rect, localEventPaint);
                }
                canvas.drawText(eventDrawingInfo.getSchedule().getSubject(),
                        (eventDrawingInfo.getEndPoint().x - eventDrawingInfo.getStartPoint().x) / 2, eventDrawingInfo.getEndPoint().y, eventTextPaint);
            }
        }
    }

    public void setEventDrawingInfo()
    {
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
    }

    private int calcEventPosition(PointF startPoint, PointF endPoint, ScheduleDTO schedule)
    {
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
    }


    public WeekView setPosition(int position)
    {
        this.position = position;
        return this;
    }

    public int getPosition()
    {
        return position;
    }


    public void setScheduleList(List<ScheduleDTO> scheduleList)
    {
        this.scheduleList = scheduleList;
        setEventDrawingInfo();
    }
}