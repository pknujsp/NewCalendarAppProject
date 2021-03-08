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
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
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
import java.util.Map;


public class WeekViewPagerAdapter extends RecyclerView.Adapter<WeekViewPagerAdapter.WeekViewPagerHolder> implements DateGetter
{
    private SparseArray<WeekViewPagerHolder> holderSparseArray = new SparseArray<>();
    private final Calendar CALENDAR;
    private final IToolbar iToolbar;
    private final IControlEvent iControlEvent;
    private final OnEventItemClickListener onEventItemClickListener;
    private final IConnectedCalendars iConnectedCalendars;

    public WeekViewPagerAdapter(IControlEvent iControlEvent, OnEventItemClickListener onEventItemClickListener, IToolbar iToolbar, IConnectedCalendars iConnectedCalendars)
    {
        this.iControlEvent = iControlEvent;
        this.iToolbar = iToolbar;
        this.onEventItemClickListener = onEventItemClickListener;
        this.iConnectedCalendars = iConnectedCalendars;
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
        holder.onBind();
        holderSparseArray.put(holder.getAdapterPosition(), holder);
    }

    @Override
    public void onViewRecycled(@NonNull WeekViewPagerHolder holder)
    {
        holderSparseArray.remove(holder.getOldPosition());
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
        return holderSparseArray.get(position).weekCalendarView.getDaysOfWeek()[index];
    }

    public void refresh(int position)
    {
        holderSparseArray.get(position).weekCalendarView.refresh();
    }

    class WeekViewPagerHolder extends RecyclerView.ViewHolder
    {
        private final WeekCalendarView weekCalendarView;

        public WeekViewPagerHolder(View view)
        {
            super(view);

            WeekView weekView = (WeekView) view.findViewById(R.id.week_view);
            WeekHeaderView weekHeaderView = (WeekHeaderView) view.findViewById(R.id.week_header);

            weekCalendarView = new WeekCalendarView(weekHeaderView, weekView);
        }

        public void onBind()
        {

            Calendar copiedCalendar = (Calendar) CALENDAR.clone();
            copiedCalendar.add(Calendar.WEEK_OF_YEAR, getAdapterPosition() - EventTransactionFragment.FIRST_VIEW_POSITION);
            weekCalendarView.init(copiedCalendar, onEventItemClickListener, iControlEvent, iConnectedCalendars);
        }

    }

}