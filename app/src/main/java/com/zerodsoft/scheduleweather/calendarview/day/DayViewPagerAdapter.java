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
    private final SparseArray<DayViewPagerHolder> holderSparseArray = new SparseArray<>();
    private final Calendar CALENDAR;
    private Activity activity;
    public static final int FIRST_DAY = -1;
    public static final int LAST_DAY = -2;

    public DayViewPagerAdapter(Activity activity, IControlEvent iControlEvent, OnEventItemClickListener onEventItemClickListener, IToolbar iToolbar)
    {
        this.activity = activity;
        this.onEventItemClickListener = onEventItemClickListener;
        this.iControlEvent = iControlEvent;
        this.iToolbar = iToolbar;
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
        holder.clear();
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount()
    {
        return Integer.MAX_VALUE;
    }

    public Date getDay(int position)
    {
        return holderSparseArray.get(position).startDate;
    }

    @Override
    public Date getDate(int position, int index)
    {
        Date date = null;
        if (index == FIRST_DAY)
        {
            date = (Date) holderSparseArray.get(position).startDate.clone();
        } else
        {
            date = (Date) holderSparseArray.get(position).endDate.clone();
        }
        return date;
    }

    public void refresh(int position, List<CalendarInstance> e)
    {
        holderSparseArray.get(position).setResult(e);
    }

    class DayViewPagerHolder extends RecyclerView.ViewHolder
    {
        // 시간별 리스트 레이아웃 표시
        DayHeaderView dayHeaderView;
        DayView dayView;

        Date startDate = new Date();
        Date endDate = new Date();

        public DayViewPagerHolder(View view)
        {
            super(view);
            dayHeaderView = (DayHeaderView) view.findViewById(R.id.dayheaderview);
            dayHeaderView.setOnEventItemClickListener(onEventItemClickListener);
            dayView = (DayView) view.findViewById(R.id.dayview);
            dayView.setOnEventItemClickListener(onEventItemClickListener);
        }

        public void onBind()
        {
            Calendar copiedCalendar = (Calendar) CALENDAR.clone();
            copiedCalendar.add(Calendar.DAY_OF_YEAR, getAdapterPosition() - EventTransactionFragment.FIRST_VIEW_POSITION);

            startDate = copiedCalendar.getTime();
            copiedCalendar.add(Calendar.DAY_OF_YEAR, 1);
            endDate = copiedCalendar.getTime();

            dayHeaderView.setInitValue(startDate, endDate);

            // setResult(iControlEvent.getInstances(getAdapterPosition(), startDate.getTime(), endDate.getTime()));
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
            dayHeaderView.setInstances(instances);
            dayView.setInstances(instances);
        }

        public void clear()
        {
            dayHeaderView.clear();
            dayView.clear();
        }

    }

}
