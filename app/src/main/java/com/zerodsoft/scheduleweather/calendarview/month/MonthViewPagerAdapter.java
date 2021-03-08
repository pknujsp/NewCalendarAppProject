package com.zerodsoft.scheduleweather.calendarview.month;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IToolbar;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.DateGetter;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MonthViewPagerAdapter extends RecyclerView.Adapter<MonthViewPagerAdapter.MonthViewHolder>
{
    private final SparseArray<MonthViewHolder> holderSparseArray = new SparseArray<>();
    private final Calendar CALENDAR;
    private final OnEventItemClickListener onEventItemClickListener;
    private final IControlEvent iControlEvent;
    private final IToolbar iToolbar;
    private final IConnectedCalendars iConnectedCalendars;

    public MonthViewPagerAdapter(IControlEvent iControlEvent, OnEventItemClickListener onEventItemClickListener, IToolbar iToolbar, IConnectedCalendars iConnectedCalendars)
    {
        this.onEventItemClickListener = onEventItemClickListener;
        this.iControlEvent = iControlEvent;
        this.iToolbar = iToolbar;
        this.iConnectedCalendars = iConnectedCalendars;
        CALENDAR = Calendar.getInstance(ClockUtil.TIME_ZONE);

        // 날짜를 이번 달 1일 0시 0분으로 설정
        CALENDAR.set(Calendar.DAY_OF_MONTH, 1);
        CALENDAR.set(Calendar.HOUR_OF_DAY, 0);
        CALENDAR.set(Calendar.MINUTE, 0);
        CALENDAR.set(Calendar.SECOND, 0);

        iToolbar.setMonth(CALENDAR.getTime());
    }

    public Calendar getCALENDAR()
    {
        return (Calendar) CALENDAR.clone();
    }

    public void refresh(int position)
    {
        holderSparseArray.get(position).monthCalendarView.refresh();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
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
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount()
    {
        return Integer.MAX_VALUE;
    }


    final class MonthViewHolder extends RecyclerView.ViewHolder
    {
        private final MonthCalendarView monthCalendarView;

        public MonthViewHolder(View view)
        {
            super(view);
            monthCalendarView = (MonthCalendarView) view.findViewById(R.id.month_calendar_view);
        }

        public void onBind()
        {
            Calendar copiedCalendar = (Calendar) CALENDAR.clone();
            copiedCalendar.add(Calendar.MONTH, getAdapterPosition() - EventTransactionFragment.FIRST_VIEW_POSITION);

            monthCalendarView.init(copiedCalendar, onEventItemClickListener, iControlEvent, iConnectedCalendars);
        }
    }
}
