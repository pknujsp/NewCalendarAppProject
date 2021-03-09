package com.zerodsoft.scheduleweather.calendarview.day;

import android.app.Activity;
import android.content.ContentValues;
import android.os.RemoteException;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.interfaces.DateGetter;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IToolbar;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import lombok.SneakyThrows;


public class DayViewPagerAdapter extends RecyclerView.Adapter<DayViewPagerAdapter.DayViewPagerHolder> implements DateGetter
{
    private final OnEventItemClickListener onEventItemClickListener;
    private final IControlEvent iControlEvent;
    private final IToolbar iToolbar;
    private final IConnectedCalendars iConnectedCalendars;
    private SparseArray<DayViewPagerHolder> holderSparseArray = new SparseArray<>();
    private final Calendar CALENDAR;

    public static final int FIRST_DAY = -1;
    public static final int LAST_DAY = -2;

    public DayViewPagerAdapter(IControlEvent iControlEvent, OnEventItemClickListener onEventItemClickListener, IToolbar iToolbar, IConnectedCalendars iConnectedCalendars)
    {
        this.onEventItemClickListener = onEventItemClickListener;
        this.iControlEvent = iControlEvent;
        this.iToolbar = iToolbar;
        this.iConnectedCalendars = iConnectedCalendars;

        CALENDAR = Calendar.getInstance(ClockUtil.TIME_ZONE);
        // 날짜를 오늘 0시0분0초로 설정
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
    public DayViewPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new DayViewPagerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dayview_viewpager_item, parent, false));
    }

    @SneakyThrows
    @Override
    public void onBindViewHolder(@NonNull DayViewPagerHolder holder, int position)
    {
        holder.onBind();
        holderSparseArray.put(holder.getAdapterPosition(), holder);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull DayViewPagerHolder holder)
    {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull DayViewPagerHolder holder)
    {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(@NonNull DayViewPagerHolder holder)
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
        if (index == FIRST_DAY)
        {
            return holderSparseArray.get(position).dayCalendarView.getViewStartDate();
        } else
        {
            return holderSparseArray.get(position).dayCalendarView.getViewEndDate();
        }
    }

    public void refresh(int position)
    {
        holderSparseArray.get(position).dayCalendarView.refresh();
    }

    class DayViewPagerHolder extends RecyclerView.ViewHolder
    {
        private DayCalendarView dayCalendarView;

        public DayViewPagerHolder(View view)
        {
            super(view);
            DayHeaderView dayHeaderView = (DayHeaderView) view.findViewById(R.id.dayheaderview);
            DayView dayView = (DayView) view.findViewById(R.id.dayview);
            dayCalendarView = new DayCalendarView(dayHeaderView, dayView);
        }

        public void onBind()
        {
            Calendar copiedCalendar = (Calendar) CALENDAR.clone();
            copiedCalendar.add(Calendar.DAY_OF_YEAR, getAdapterPosition() - EventTransactionFragment.FIRST_VIEW_POSITION);
            dayCalendarView.init(copiedCalendar, onEventItemClickListener, iControlEvent, iConnectedCalendars);
        }

    }

}
