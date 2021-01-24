package com.zerodsoft.scheduleweather.calendarview.month;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.callback.EventCallback;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IToolbar;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.DateGetter;
import com.zerodsoft.scheduleweather.etc.CalendarUtil;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MonthViewPagerAdapter extends RecyclerView.Adapter<MonthViewPagerAdapter.MonthViewHolder> implements DateGetter
{
    public static final int TOTAL_DAY_COUNT = 42;
    public static final int FIRST_DAY = -1;
    public static final int LAST_DAY = -2;

    private final SparseArray<MonthViewHolder> holderSparseArray = new SparseArray<>();
    private final Calendar calendar;
    private Context context;
    private final OnEventItemClickListener onEventItemClickListener;
    private final IControlEvent iControlEvent;
    private final IToolbar iToolbar;

    public MonthViewPagerAdapter(IControlEvent iControlEvent, OnEventItemClickListener onEventItemClickListener, IToolbar iToolbar)
    {
        this.onEventItemClickListener = onEventItemClickListener;
        this.iControlEvent = iControlEvent;
        this.iToolbar = iToolbar;
        calendar = Calendar.getInstance(ClockUtil.TIME_ZONE);

        // 날짜를 이번 달 1일 0시 0분으로 설정
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        iToolbar.setMonth(calendar.getTime());
    }


    public Calendar getCalendar()
    {
        return (Calendar) calendar.clone();
    }

    @Override
    public Date getDate(int position, int dateType)
    {
        return holderSparseArray.get(position).getDay(dateType).getTime();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();
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
        holder.onBind();
        holderSparseArray.put(holder.getAdapterPosition(), holder);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MonthViewHolder holder)
    {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MonthViewHolder holder)
    {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(@NonNull MonthViewHolder holder)
    {
        holderSparseArray.remove(holder.getOldPosition());
        holder.clearHolder();
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount()
    {
        return Integer.MAX_VALUE;
    }

    public Date getMonth(int position)
    {
        return holderSparseArray.get(position).currentMonthDays[0].getTime();
    }

    class MonthViewHolder extends RecyclerView.ViewHolder
    {
        private MonthCalendarView monthCalendarView;

        private Calendar[] previousMonthDays;
        private Calendar[] currentMonthDays;
        private Calendar[] nextMonthDays;
        private Calendar endDay;

        public MonthViewHolder(View view)
        {
            super(view);
            monthCalendarView = (MonthCalendarView) view.findViewById(R.id.month_calendar_view);
        }

        public void clearHolder()
        {
            monthCalendarView.removeAllViews();
            monthCalendarView.clear();

            previousMonthDays = null;
            currentMonthDays = null;
            nextMonthDays = null;
            endDay = null;
        }

        public void onBind()
        {
            Calendar copiedCalendar = (Calendar) calendar.clone();
            copiedCalendar.add(Calendar.MONTH, getAdapterPosition() - EventTransactionFragment.FIRST_VIEW_POSITION);
            setDays(copiedCalendar);
            monthCalendarView.setFirstDay(getDay(FIRST_DAY).getTimeInMillis());

            for (int i = 0; i < TOTAL_DAY_COUNT; i++)
            {
                Calendar currentDate = getDay(i);

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
                itemView.setDate(currentDate.getTime(), getDay(i + 1).getTime());
                itemView.setClickable(true);
                itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        onEventItemClickListener.onClicked(itemView.getStartDate().getTime(), itemView.getEndDate().getTime());
                    }
                });
                monthCalendarView.addView(itemView);
            }

            iControlEvent.getInstances(getAdapterPosition(), getDay(FIRST_DAY).getTime().getTime(), getDay(LAST_DAY).getTime().getTime(), new EventCallback<List<CalendarInstance>>()
            {
                @Override
                public void onResult(List<CalendarInstance> e)
                {
                    if (!e.isEmpty())
                    {
                        List<ContentValues> instances = new ArrayList<>();
                        // 인스턴스 목록 표시
                        for (CalendarInstance calendarInstance : e)
                        {
                            instances.addAll(calendarInstance.getInstanceList());
                            // 데이터를 일정 길이의 내림차순으로 정렬
                        }
                        Collections.sort(instances, CalendarUtil.INSTANCE_COMPARATOR);
                        monthCalendarView.setInstances(instances);
                    }
                }
            });
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
    }


}
