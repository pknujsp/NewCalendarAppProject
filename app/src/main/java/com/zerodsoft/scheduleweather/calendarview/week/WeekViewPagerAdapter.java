package com.zerodsoft.scheduleweather.calendarview.week;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.calendarfragment.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarfragment.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarfragment.WeekFragment;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.month.EventData;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class WeekViewPagerAdapter extends RecyclerView.Adapter<WeekViewPagerAdapter.WeekViewPagerHolder> implements OnSwipeListener
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

        // 날짜를 이번 달 1일 0시 0분으로 설정
        int amount = -(calendar.get(Calendar.DAY_OF_WEEK) - 1);
        calendar.add(Calendar.DAY_OF_YEAR, amount);
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
    public WeekViewPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new WeekViewPagerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.weekview_viewpager_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WeekViewPagerHolder holder, int position)
    {
        Log.e("ONBIND : ", String.valueOf(position));
        holder.onBind(holder.getAdapterPosition());
        holderSparseArray.put(holder.getAdapterPosition(), holder);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull WeekViewPagerHolder holder)
    {
        super.onViewAttachedToWindow(holder);
        // toolbar의 년월 설정
        weekFragment.setMonth(holder.getDay(FIRST_DAY).getTime());
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull WeekViewPagerHolder holder)
    {
        holderSparseArray.remove(holder.getAdapterPosition());
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(@NonNull WeekViewPagerHolder holder)
    {
        holder.clear();
        super.onViewRecycled(holder);
        Log.e("RECYCLED : ", String.valueOf(holder.getAdapterPosition()));
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

    class WeekViewPagerHolder extends RecyclerView.ViewHolder
    {
        private WeekView weekView;
        private WeekHeaderView weekHeaderView;

        private int position;
        private Calendar[] currentWeekDays;
        private Calendar endDay;

        public WeekViewPagerHolder(View view)
        {
            super(view);
            weekView = (WeekView) view.findViewById(R.id.week_view);
            weekHeaderView = (WeekHeaderView) view.findViewById(R.id.week_header);
            weekView.setOnSwipeListener(WeekViewPagerAdapter.this::onSwiped);
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
                if (eventData.getDateLength() == 1 && !eventData.getSchedule().getStartDate().equals(eventData.getSchedule().getEndDate()))
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
            endDay = null;
        }

        public void onBind(int position)
        {
            this.position = position;

            Calendar copiedCalendar = (Calendar) calendar.clone();
            copiedCalendar.add(Calendar.WEEK_OF_YEAR, position - EventTransactionFragment.FIRST_VIEW_POSITION);
            setDays(copiedCalendar);


            weekHeaderView.setInitValue(currentWeekDays, WeekFragment.getColumnWidth());
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
            currentWeekDays = new Calendar[7];

            for (int i = 0; i < 7; i++)
            {
                currentWeekDays[i] = (Calendar) calendar.clone();
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            endDay = (Calendar) calendar.clone();
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
        }

        public int getDateToIndex(Calendar date)
        {
            int index = 0;

            for (int i = 0; i < currentWeekDays.length; i++)
            {
                if (currentWeekDays[i].get(Calendar.YEAR) == date.get(Calendar.YEAR) && currentWeekDays[i].get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR))
                {
                    index = i;
                    return index;
                }
            }

            // 달력에 표시된 첫 날짜 이전 인 경우
            if (date.before(currentWeekDays[0]))
            {
                // 이전 달 날짜가 들어가지 않을 때
                return Integer.MIN_VALUE;
            }

            // 달력에 표시된 마지막 날짜 이후 인 경우
            else if (date.compareTo(endDay) >= 0)
            {
                return Integer.MAX_VALUE;
            }
            return -1;
        }

        public Calendar getDay(int position)
        {
            if (position == FIRST_DAY)
            {
                return currentWeekDays[0];
            } else if (position == LAST_DAY)
            {
                return endDay;
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