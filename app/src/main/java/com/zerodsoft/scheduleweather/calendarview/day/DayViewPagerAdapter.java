package com.zerodsoft.scheduleweather.calendarview.day;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.interfaces.DateGetter;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class DayViewPagerAdapter extends RecyclerView.Adapter<DayViewPagerAdapter.DayViewPagerHolder> implements DateGetter
{
    private DayFragment dayFragment;
    private final SparseArray<DayViewPagerHolder> holderSparseArray = new SparseArray<>();
    private final Calendar CALENDAR;
    public static final int FIRST_DAY = -1;
    public static final int LAST_DAY = -2;

    public DayViewPagerAdapter(DayFragment dayFragment)
    {
        this.dayFragment = dayFragment;
        CALENDAR = Calendar.getInstance(ClockUtil.TIME_ZONE);
        // 날짜를 오늘 0시0분0초로 설정
        CALENDAR.set(Calendar.HOUR_OF_DAY, 0);
        CALENDAR.set(Calendar.MINUTE, 0);
        CALENDAR.set(Calendar.SECOND, 0);
        dayFragment.setMonth(CALENDAR.getTime());
    }

    public void setData(int position, List<ScheduleDTO> schedules)
    {
        holderSparseArray.get(position).setData(schedules);
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
    public Date getDate(int position, int dateType)
    {
        Date date = null;
        if (dateType == FIRST_DAY)
        {
            date = (Date) holderSparseArray.get(position).startDate.clone();
        } else
        {
            date = (Date) holderSparseArray.get(position).endDate.clone();
        }
        return date;
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
            dayView = (DayView) view.findViewById(R.id.dayview);
            dayView.setAdapter(DayViewPagerAdapter.this);
        }

        public void onBind()
        {
            Calendar copiedCalendar = (Calendar) CALENDAR.clone();
            copiedCalendar.add(Calendar.DAY_OF_YEAR, getAdapterPosition() - EventTransactionFragment.FIRST_VIEW_POSITION);

            startDate = copiedCalendar.getTime();
            copiedCalendar.add(Calendar.DAY_OF_YEAR, 1);
            endDate = copiedCalendar.getTime();

            dayHeaderView.setInitValue(startDate, endDate);

            dayHeaderView.requestLayout();
            dayHeaderView.invalidate();

            dayFragment.requestSchedules(getAdapterPosition(), startDate, endDate);
        }

        public void clear()
        {
            dayHeaderView.clear();
            dayView.clear();
        }

        public void setData(List<ScheduleDTO> schedules)
        {
            // 데이터를 일정 길이의 내림차순으로 정렬
            Collections.sort(schedules, comparator);
            dayHeaderView.setSchedules(schedules);
            dayView.setSchedules(schedules);
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

    public void showSchedule(int scheduleId)
    {
        dayFragment.showSchedule(scheduleId);
    }
}
