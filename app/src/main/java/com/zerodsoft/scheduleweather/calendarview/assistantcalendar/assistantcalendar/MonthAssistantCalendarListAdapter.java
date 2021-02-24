package com.zerodsoft.scheduleweather.calendarview.assistantcalendar.assistantcalendar;

import android.content.ContentValues;
import android.content.Context;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.callback.EventCallback;
import com.zerodsoft.scheduleweather.calendarview.interfaces.CalendarDateOnClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.month.MonthCalendarView;
import com.zerodsoft.scheduleweather.calendarview.month.MonthViewPagerAdapter;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MonthAssistantCalendarListAdapter extends RecyclerView.Adapter<MonthAssistantCalendarListAdapter.MonthAssistantViewHolder>
{
    private final IControlEvent iControlEvent;
    private final CalendarDateOnClickListener calendarDateOnClickListener;
    private final Calendar CALENDAR;
    private final Date TODAY;
    private Context context;

    public MonthAssistantCalendarListAdapter(IControlEvent iControlEvent, CalendarDateOnClickListener calendarDateOnClickListener)
    {
        this.iControlEvent = iControlEvent;
        this.calendarDateOnClickListener = calendarDateOnClickListener;
        CALENDAR = Calendar.getInstance(ClockUtil.TIME_ZONE);
        TODAY = CALENDAR.getTime();

        // 날짜를 이번 달 1일 0시 0분으로 설정
        CALENDAR.set(Calendar.DAY_OF_MONTH, 1);
        CALENDAR.set(Calendar.HOUR_OF_DAY, 0);
        CALENDAR.set(Calendar.MINUTE, 0);
        CALENDAR.set(Calendar.SECOND, 0);
    }

    public Date getAsOfDate()
    {
        return CALENDAR.getTime();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public MonthAssistantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context = parent.getContext();
        return new MonthAssistantViewHolder(LayoutInflater.from(context).inflate(R.layout.month_assistant_itemview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MonthAssistantViewHolder holder, int position)
    {
        holder.onBind();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MonthAssistantViewHolder holder)
    {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MonthAssistantViewHolder holder)
    {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(@NonNull MonthAssistantViewHolder holder)
    {
        holder.clearHolder();
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount()
    {
        return Integer.MAX_VALUE;
    }


    class MonthAssistantViewHolder extends RecyclerView.ViewHolder
    {
        private final MonthAssistantCalendarView monthCalendarView;

        private Calendar[] previousMonthDays;
        private Calendar[] currentMonthDays;
        private Calendar[] nextMonthDays;
        private Calendar endDay;

        public MonthAssistantViewHolder(View view)
        {
            super(view);
            monthCalendarView = (MonthAssistantCalendarView) view.findViewById(R.id.month_assistant_calendar_view);
        }

        public void clearHolder()
        {
            monthCalendarView.removeAllViews();

            previousMonthDays = null;
            currentMonthDays = null;
            nextMonthDays = null;
            endDay = null;
        }

        public void onBind()
        {
            Calendar copiedCalendar = (Calendar) CALENDAR.clone();
            copiedCalendar.add(Calendar.MONTH, getAdapterPosition() - EventTransactionFragment.FIRST_VIEW_POSITION);
            setDays(copiedCalendar);
            boolean thisMonthDate = false;

            for (int i = 0; i < MonthViewPagerAdapter.TOTAL_DAY_COUNT; i++)
            {
                Calendar currentDate = getDay(i);

                if (currentDate.before(currentMonthDays[0]) || currentDate.after(currentMonthDays[currentMonthDays.length - 1]))
                {
                    thisMonthDate = false;
                } else
                {
                    thisMonthDate = true;
                }

                // 날짜 설정
                MonthAssistantCalendarView.MonthAssistantItemView itemView =
                        new MonthAssistantCalendarView.MonthAssistantItemView(context, thisMonthDate, currentDate.getTime(), getDay(i + 1).getTime());
                itemView.setClickable(true);
                itemView.setToday(ClockUtil.areSameDate(currentDate.getTime().getTime(), TODAY.getTime()));
                itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        calendarDateOnClickListener.onClickedDate(currentDate.getTime());
                    }
                });
                monthCalendarView.addView(itemView);
            }

            iControlEvent.getInstances(getAdapterPosition(), getDay(MonthViewPagerAdapter.FIRST_DAY).getTime().getTime(),
                    getDay(MonthViewPagerAdapter.LAST_DAY).getTime().getTime(), new EventCallback<List<CalendarInstance>>()
                    {
                        @Override
                        public void onResult(List<CalendarInstance> e)
                        {
                            setResult(e);
                        }
                    });

        }

        public void setResult(List<CalendarInstance> e)
        {
            List<ContentValues> instances = new ArrayList<>();
            // 인스턴스 목록 표시
            for (CalendarInstance calendarInstance : e)
            {
                instances.addAll(calendarInstance.getInstanceList());
            }

            final Date asOfDate = getDay(MonthViewPagerAdapter.FIRST_DAY).getTime();
            Map<Integer, Integer> countMap = new HashMap<>();

            // 인스턴스를 날짜 별로 분류
            for (ContentValues instance : instances)
            {
                Date beginDate = ClockUtil.instanceDateTimeToDate(instance.getAsLong(CalendarContract.Instances.BEGIN));
                Date endDate = ClockUtil.instanceDateTimeToDate(instance.getAsLong(CalendarContract.Instances.END), instance.getAsBoolean(CalendarContract.Instances.ALL_DAY));
                int beginIndex = ClockUtil.calcDayDifference(beginDate, asOfDate);
                int endIndex = ClockUtil.calcDayDifference(endDate, asOfDate);

                if (beginIndex < MonthCalendarView.FIRST_DAY_INDEX)
                {
                    beginIndex = MonthCalendarView.FIRST_DAY_INDEX;
                }
                if (endIndex > MonthCalendarView.LAST_DAY_INDEX)
                {
                    // 달력에 표시할 일자의 개수가 총 42개
                    endIndex = MonthCalendarView.LAST_DAY_INDEX;
                }

                for (int index = beginIndex; index <= endIndex; index++)
                {
                    if (!countMap.containsKey(index))
                    {
                        countMap.put(index, 0);
                    }
                    countMap.put(index, countMap.get(index) + 1);
                }
            }

            Set<Integer> mapKeySet = countMap.keySet();
            for (int index : mapKeySet)
            {
                MonthAssistantCalendarView.MonthAssistantItemView childView =
                        (MonthAssistantCalendarView.MonthAssistantItemView) monthCalendarView.getChildAt(index);
                childView.setCount(countMap.get(index));
            }

            monthCalendarView.requestLayout();
            monthCalendarView.invalidate();
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
            int nextCount = MonthViewPagerAdapter.TOTAL_DAY_COUNT - currentCount - previousCount;

            previousMonthDays = new Calendar[previousCount];
            currentMonthDays = new Calendar[currentCount];
            nextMonthDays = new Calendar[nextCount];

            // 이전 달 일수 만큼 이동 ex) 20201001에서 20200927로 이동
            calendar.add(Calendar.DATE, -previousCount);

            for (int i = 0; i < previousCount; i++)
            {
                previousMonthDays[i] = (Calendar) calendar.clone();
                calendar.add(Calendar.DATE, 1);
            }

            for (int i = 0; i < currentCount; i++)
            {
                currentMonthDays[i] = (Calendar) calendar.clone();
                calendar.add(Calendar.DATE, 1);
            }

            for (int i = 0; i < nextCount; i++)
            {
                nextMonthDays[i] = (Calendar) calendar.clone();
                calendar.add(Calendar.DATE, 1);
            }
            endDay = (Calendar) calendar.clone();
        }


        public Calendar getDay(int position)
        {
            if (position == MonthViewPagerAdapter.FIRST_DAY)
            {
                if (previousMonthDays.length > 0)
                {
                    return previousMonthDays[0];
                } else
                {
                    return currentMonthDays[0];
                }
            } else if (position == MonthViewPagerAdapter.LAST_DAY)
            {
                return endDay;
            } else if (position < previousMonthDays.length)
            {
                return previousMonthDays[position];
            } else if (position < currentMonthDays.length + previousMonthDays.length)
            {
                return currentMonthDays[position - previousMonthDays.length];
            } else if (position < MonthViewPagerAdapter.TOTAL_DAY_COUNT)
            {
                return nextMonthDays[position - currentMonthDays.length - previousMonthDays.length];
            } else if (position == MonthViewPagerAdapter.TOTAL_DAY_COUNT)
            {
                return endDay;
            } else
            {
                return null;
            }
        }
    }
}
