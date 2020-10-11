package com.zerodsoft.scheduleweather.calendarview.month;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarfragment.CalendarTransactionFragment;
import com.zerodsoft.scheduleweather.calendarfragment.MonthFragment;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.utility.Clock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonthViewPagerAdapter extends RecyclerView.Adapter<MonthViewPagerAdapter.MonthViewHolder>
{
    public static final int TOTAL_DAY_COUNT = 42;
    public static final int FIRST_DAY = -1;
    public static final int LAST_DAY = -2;

    private SparseArray<MonthViewHolder> holderSparseArray = new SparseArray<>();
    private Calendar calendar;
    private MonthFragment monthFragment;
    private Context context;
    public static int CELL_HEIGHT;

    public MonthViewPagerAdapter(MonthFragment monthFragment)
    {
        this.monthFragment = monthFragment;
        context = monthFragment.getContext();
        calendar = Calendar.getInstance();

        // 날짜를 이번 달 1일 0시 0분으로 설정
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
    }

    public void setData(int position, List<ScheduleDTO> schedules)
    {
        holderSparseArray.get(position).setData(schedules);
    }

    @NonNull
    @Override
    public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new MonthViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.monthview_viewpager_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MonthViewHolder holder, int position)
    {
        Log.e(getClass().getSimpleName(), "onBindViewHolder : " + position);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MonthViewHolder holder)
    {
        holder.onBind(holder.getAdapterPosition());
        holderSparseArray.put(holder.getAdapterPosition(), holder);
        Log.e(getClass().getSimpleName(), "onViewAttachedToWindow : " + holder.getAdapterPosition());
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MonthViewHolder holder)
    {
        super.onViewDetachedFromWindow(holder);
        Log.e(getClass().getSimpleName(), "onViewDetachedFromWindow : " + holder.getAdapterPosition());
    }

    @Override
    public int getItemCount()
    {
        return Integer.MAX_VALUE;
    }

    class MonthViewHolder extends RecyclerView.ViewHolder
    {
        private int position;
        private LinearLayout header;
        private MonthCalendarView monthCalendarView;

        private Date[] previousMonthDays;
        private Date[] currentMonthDays;
        private Date[] nextMonthDays;
        private Date endDay;

        public MonthViewHolder(View view)
        {
            super(view);
            header = (LinearLayout) view.findViewById(R.id.month_header_days);
            monthCalendarView = (MonthCalendarView) view.findViewById(R.id.month_calendar_view);
        }

        public void setData(List<ScheduleDTO> schedulesList)
        {
            // 데이터를 일자별로 분류
            SparseArray<EventDto> list = new SparseArray<>();

            for (int i = 0; i < TOTAL_DAY_COUNT; i++)
            {
                Date startDate = getDay(i);
                Date endDate = getDay(i + 1);

                EventDto eventDto = new EventDto(startDate);
                list.put(i, eventDto);

                for (ScheduleDTO schedule : schedulesList)
                {
                    // 종료일이 date인 경우
                    if (schedule.getEndDate().compareTo(startDate) >= 0 && schedule.getEndDate().before(endDate))
                    {
                        eventDto.schedulesList.add(schedule);
                    }
                    // 시작일이 date인 경우
                    if (schedule.getStartDate().compareTo(startDate) >= 0 && schedule.getStartDate().before(endDate))
                    {
                        eventDto.schedulesList.add(schedule);
                    }
                    // 시작/종료일이 date가 아니나, 일정에 포함되는 경우
                    if (schedule.getStartDate().before(startDate) && schedule.getEndDate().after(endDate))
                    {
                        eventDto.schedulesList.add(schedule);
                    }
                }
                if (eventDto.schedulesList.isEmpty())
                {
                    list.remove(i);
                }
            }
            monthCalendarView.setSchedules(list);
        }

        public void onBind(int position)
        {
            this.position = position;

            Calendar copiedCalendar = (Calendar) calendar.clone();
            copiedCalendar.add(Calendar.MONTH, position - CalendarTransactionFragment.FIRST_VIEW_POSITION);
            monthCalendarView.setCalendar(copiedCalendar);
            setDays(copiedCalendar);

            for (int i = 0; i < TOTAL_DAY_COUNT; i++)
            {
                Date currentDate = getDay(i);

                int dayTextColor = 0;
                if (currentDate.before(currentMonthDays[0]) || currentDate.after(currentMonthDays[currentMonthDays.length - 1]))
                {
                    dayTextColor = Color.GRAY;
                } else
                {
                    dayTextColor = Color.BLACK;
                }

                // 날짜 설정
                MonthCalendarItemView itemView = new MonthCalendarItemView(context, dayTextColor);
                itemView.setDate(currentDate, getDay(i + 1));
                monthCalendarView.addView(itemView);
            }
            monthFragment.requestSchedules(position, getDay(FIRST_DAY), getDay(LAST_DAY));
        }

        private void setDays(Calendar calendar)
        {
            // 일요일 부터 토요일까지
            // 이번 달이 2020/10인 경우 1일이 목요일이므로, 그리드 뷰는 9/27 일요일 부터 시작하고
            // 10/31 토요일에 종료
            // SUNDAY : 1, SATURDAY : 7  (getFirstDayOfWeek)
            // 다음 달 일수 계산법 : 42 - 이번 달 - 이전 달

            int previousCount = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            int currentCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            int nextCount = TOTAL_DAY_COUNT - currentCount - previousCount;

            previousMonthDays = new Date[previousCount];
            currentMonthDays = new Date[currentCount];
            nextMonthDays = new Date[nextCount];

            // 이전 달 일수 만큼 이동 ex) 20201001에서 20200927로 이동
            calendar.add(Calendar.DATE, -previousCount);

            for (int i = 0; i < previousCount; i++)
            {
                previousMonthDays[i] = calendar.getTime();
                calendar.add(Calendar.DATE, 1);
            }

            for (int i = 0; i < currentCount; i++)
            {
                currentMonthDays[i] = calendar.getTime();
                calendar.add(Calendar.DATE, 1);
            }

            for (int i = 0; i < nextCount; i++)
            {
                nextMonthDays[i] = calendar.getTime();
                calendar.add(Calendar.DATE, 1);
            }

            endDay = calendar.getTime();
        }

        public Date getDay(int position)
        {
            if (position == FIRST_DAY)
            {
                if (previousMonthDays.length > 0)
                {
                    return previousMonthDays[0];
                } else
                {
                    return currentMonthDays[0];
                }
            } else if (position == LAST_DAY)
            {
                return endDay;
            } else if (position < previousMonthDays.length)
            {
                return previousMonthDays[position];
            } else if (position < currentMonthDays.length + previousMonthDays.length)
            {
                return currentMonthDays[position - previousMonthDays.length];
            } else if (position < TOTAL_DAY_COUNT)
            {
                return nextMonthDays[position - currentMonthDays.length - previousMonthDays.length];
            } else if (position == TOTAL_DAY_COUNT)
            {
                return endDay;
            } else
            {
                return null;
            }
        }


        /*
        ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                // CELL_HEIGHT = gridView.getHeight() / 6;
                //리스너 제거 (해당 뷰의 상태가 변할때 마다 호출되므로)
                removeOnGlobalLayoutListener(header.getViewTreeObserver(), mGlobalLayoutListener);
            }
        };


        private void removeOnGlobalLayoutListener(ViewTreeObserver observer, ViewTreeObserver.OnGlobalLayoutListener listener)
        {
            if (observer == null)
            {
                return;
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            {
                observer.removeGlobalOnLayoutListener(listener);
            } else
            {
                observer.removeOnGlobalLayoutListener(listener);
            }
        }
         */
    }
}
