package com.zerodsoft.scheduleweather.calendarview.week;

import android.content.ContentValues;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IToolbar;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.interfaces.DateGetter;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class WeekViewPagerAdapter extends RecyclerView.Adapter<WeekViewPagerAdapter.WeekViewPagerHolder> implements DateGetter
{
    private SparseArray<WeekViewPagerHolder> holderSparseArray = new SparseArray<>();
    final private Calendar CALENDAR;
    private IToolbar iToolbar;
    private IControlEvent iControlEvent;
    private OnEventItemClickListener onEventItemClickListener;

    public WeekViewPagerAdapter(IControlEvent iControlEvent, OnEventItemClickListener onEventItemClickListener, IToolbar iToolbar)
    {
        this.iControlEvent = iControlEvent;
        this.iToolbar = iToolbar;
        this.onEventItemClickListener = onEventItemClickListener;
        CALENDAR = Calendar.getInstance(ClockUtil.TIME_ZONE);

        // 날짜를 이번 주 일요일 0시 0분으로 설정
        int amount = -(CALENDAR.get(Calendar.DAY_OF_WEEK) - 1);

        CALENDAR.add(Calendar.DAY_OF_YEAR, amount);
        CALENDAR.set(Calendar.HOUR_OF_DAY, 0);
        CALENDAR.set(Calendar.MINUTE, 0);
        CALENDAR.set(Calendar.SECOND, 0);

        iToolbar.setMonth(CALENDAR.getTime());
    }

    public Calendar getCALENDAR()
    {
        return (Calendar) CALENDAR.clone();
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
    public Date getDate(int position, int index)
    {
        return holderSparseArray.get(position).currentWeekDays[index].getTime();
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

            Calendar copiedCalendar = (Calendar) CALENDAR.clone();
            copiedCalendar.add(Calendar.WEEK_OF_YEAR, position - EventTransactionFragment.FIRST_VIEW_POSITION);
            setDays(copiedCalendar);

            weekHeaderView.setInitValue(currentWeekDays);
            weekView.setDaysOfWeek(currentWeekDays);

            setResult(iControlEvent.getInstances(position, currentWeekDays[0].getTimeInMillis()
                    , currentWeekDays[7].getTimeInMillis()));
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

    }


}