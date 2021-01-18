package com.zerodsoft.scheduleweather.calendarview.week;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.interfaces.DateGetter;
import com.zerodsoft.scheduleweather.calendarview.month.EventData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class WeekViewPagerAdapter extends RecyclerView.Adapter<WeekViewPagerAdapter.WeekViewPagerHolder> implements OnSwipeListener, DateGetter
{
    private SparseArray<WeekViewPagerHolder> holderSparseArray = new SparseArray<>();
    private WeekFragment weekFragment;
    private Context context;
    private Calendar calendar;

    public static final int TOTAL_DAY_COUNT = 7;
    public static final int FIRST_DAY = -1;
    public static final int LAST_DAY = -2;

    private OnEventItemClickListener onEventItemClickListener;

    public WeekViewPagerAdapter(WeekFragment weekFragment)
    {
        this.weekFragment = weekFragment;
        this.onEventItemClickListener = (OnEventItemClickListener) weekFragment;
        context = weekFragment.getContext();
        calendar = Calendar.getInstance();

        // 날짜를 이번 주 일요일 0시 0분으로 설정
        int amount = -(calendar.get(Calendar.DAY_OF_WEEK) - 1);
        calendar.add(Calendar.DAY_OF_YEAR, amount);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        weekFragment.setMonth(calendar.getTime());
    }

    public Calendar getCalendar()
    {
        return (Calendar) calendar.clone();
    }

    public void setData(int position, List<ScheduleDTO> schedules)
    {
        holderSparseArray.get(position).setData(schedules);
    }


    @NonNull
    @Override
    public WeekViewPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new WeekViewPagerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.weekview_viewpager_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WeekViewPagerHolder holder, int position)
    {
        holder.onBind(holder.getAdapterPosition());
        holderSparseArray.put(holder.getAdapterPosition(), holder);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull WeekViewPagerHolder holder)
    {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull WeekViewPagerHolder holder)
    {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(@NonNull WeekViewPagerHolder holder)
    {
        holderSparseArray.remove(holder.getOldPosition());
        holder.clear();
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount()
    {
        return Integer.MAX_VALUE;
    }


    @Override
    public void onSwiped(boolean value)
    {
        weekFragment.setViewPagerSwipe(value);
    }

    @Override
    public Date getDate(int position, int dateType)
    {
        return holderSparseArray.get(position).getDay(dateType).getTime();
    }

    class WeekViewPagerHolder extends RecyclerView.ViewHolder
    {
        private WeekView weekView;
        private WeekHeaderView weekHeaderView;

        private int position;
        private Calendar[] currentWeekDays;

        public WeekViewPagerHolder(View view)
        {
            super(view);
            weekView = (WeekView) view.findViewById(R.id.week_view);
            weekHeaderView = (WeekHeaderView) view.findViewById(R.id.week_header);
            weekView.setOnSwipeListener(WeekViewPagerAdapter.this::onSwiped);
            weekHeaderView.setOnEventItemClickListener(weekFragment);
        }

        public void setData(List<ScheduleDTO> schedulesList)
        {
            // 데이터를 일정 길이의 내림차순으로 정렬
            Collections.sort(schedulesList, comparator);

            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();

            // 데이터를 일자별로 분류
            List<EventData> list = new ArrayList<>();

            for (ScheduleDTO schedule : schedulesList)
            {
                startDate.setTime(schedule.getStartDate());
                endDate.setTime(schedule.getEndDate());
                list.add(new EventData(schedule, getDateToIndex(startDate), getDateToIndex(endDate)));
            }

            List<EventData> list2 = new ArrayList<>();

            // weekview에 표시하기 위해 하루 내의 일정만 저장
            for (EventData eventData : list)
            {
                if (eventData.isDaySchedule())
                {
                    list2.add(eventData);
                }
            }
            weekHeaderView.setSchedules(list);
            weekView.setSchedules(list2);
        }


        public void clear()
        {
            weekHeaderView.clear();
            weekView.clear();
            currentWeekDays = null;
        }

        public void onBind(int position)
        {
            this.position = position;

            Calendar copiedCalendar = (Calendar) calendar.clone();
            copiedCalendar.add(Calendar.WEEK_OF_YEAR, position - EventTransactionFragment.FIRST_VIEW_POSITION);
            setDays(copiedCalendar);

            weekHeaderView.setInitValue(currentWeekDays);
            weekView.setDaysOfWeek(currentWeekDays);

            weekHeaderView.requestLayout();
            weekHeaderView.invalidate();
            weekView.requestLayout();
            weekView.invalidate();

            weekFragment.requestSchedules(position, getDay(FIRST_DAY).getTime(), getDay(LAST_DAY).getTime());
        }

        private void setDays(Calendar calendar)
        {
            // 일요일 부터 토요일까지
            currentWeekDays = new Calendar[8];

            for (int i = 0; i < 8; i++)
            {
                currentWeekDays[i] = (Calendar) calendar.clone();
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
        }

        public int getDateToIndex(Calendar date)
        {
            int i = 0;
            // 달력에 표시된 첫 날짜 이전 인 경우
            if (date.before(currentWeekDays[0]))
            {
                // 이번 주 이전인 경우
                i = -1;
            } else if (date.compareTo(currentWeekDays[7]) >= 0)
            {
                // 이번 주 이후인 경우
                i = 7;
            } else
            {
                for (int index = 0; index <= 6; index++)
                {
                    if (currentWeekDays[index].get(Calendar.YEAR) == date.get(Calendar.YEAR) && currentWeekDays[index].get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR))
                    {
                        i = index;
                        break;
                    }
                }
            }
            return i;
        }

        public Calendar getDay(int position)
        {
            if (position == FIRST_DAY)
            {
                return currentWeekDays[0];
            } else if (position == LAST_DAY)
            {
                return currentWeekDays[7];
            } else
            {
                return currentWeekDays[position];
            }
        }

    }

    private final Comparator<ScheduleDTO> comparator = new Comparator<ScheduleDTO>()
    {
        @Override
        public int compare(ScheduleDTO t1, ScheduleDTO t2)
        {
            /*
            음수 또는 0이면 객체의 자리가 그대로 유지되며, 양수인 경우에는 두 객체의 자리가 변경된다.
             */
            if ((t1.getEndDate().getTime() - t1.getStartDate().getTime()) < (t2.getEndDate().getTime() - t2.getStartDate().getTime()))
            {
                return 1;
            } else
            {
                return 0;
            }
        }
    };
}

interface OnSwipeListener
{
    void onSwiped(boolean value);
}