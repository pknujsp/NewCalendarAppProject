package com.zerodsoft.scheduleweather.calendarview.week;

import android.content.ContentValues;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.callback.EventCallback;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IToolbar;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.interfaces.DateGetter;
import com.zerodsoft.scheduleweather.event.util.EventUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class WeekViewPagerAdapter extends RecyclerView.Adapter<WeekViewPagerAdapter.WeekViewPagerHolder> implements OnSwipeListener, DateGetter
{
    private SparseArray<WeekViewPagerHolder> holderSparseArray = new SparseArray<>();
    private WeekFragment weekFragment;
    private Context context;
    private Calendar calendar;
    private IToolbar iToolbar;
    private IControlEvent iControlEvent;
    private OnEventItemClickListener onEventItemClickListener;

    public static final int TOTAL_DAY_COUNT = 7;
    public static final int FIRST_DAY = -1;
    public static final int LAST_DAY = -2;


    public WeekViewPagerAdapter(IControlEvent iControlEvent, OnEventItemClickListener onEventItemClickListener, IToolbar iToolbar)
    {
        this.iControlEvent = iControlEvent;
        this.iToolbar = iToolbar;
        this.onEventItemClickListener = onEventItemClickListener;
        calendar = Calendar.getInstance();

        // 날짜를 이번 주 일요일 0시 0분으로 설정
        int amount = -(calendar.get(Calendar.DAY_OF_WEEK) - 1);
        calendar.add(Calendar.DAY_OF_YEAR, amount);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        iToolbar.setMonth(calendar.getTime());
    }

    public Calendar getCalendar()
    {
        return (Calendar) calendar.clone();
    }

    @NonNull
    @Override
    public WeekViewPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new WeekViewPagerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.weekview_viewpager_item, parent, false));
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();
    }

    @Override
    public void onBindViewHolder(@NonNull WeekViewPagerHolder holder, int position)
    {
        holder.onBind(holder.getAdapterPosition());
        holderSparseArray.put(holder.getAdapterPosition(), holder);
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

    public void refresh(int position, List<CalendarInstance> e)
    {
        holderSparseArray.get(position).setResult(e);
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
            weekView.setOnEventItemClickListener(onEventItemClickListener);
            weekHeaderView.setOnEventItemClickListener(onEventItemClickListener);
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

            iControlEvent.getInstances(getAdapterPosition(), getDay(FIRST_DAY).getTime().getTime(), getDay(LAST_DAY).getTime().getTime(),
                    new EventCallback<List<CalendarInstance>>()
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
                // 데이터를 일정 길이의 내림차순으로 정렬
            }
            Collections.sort(instances, EventUtil.INSTANCE_COMPARATOR);
            // weekview에는 1일 이하의 이벤트만 표시
            weekView.setInstances(instances);
            // weekheaderview에는 모든 이벤트 표시
            weekHeaderView.setInstances(instances);
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


}

interface OnSwipeListener
{
    void onSwiped(boolean value);
}