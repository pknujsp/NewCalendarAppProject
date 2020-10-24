package com.zerodsoft.scheduleweather.calendarview.day;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarfragment.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarfragment.DayFragment;
import com.zerodsoft.scheduleweather.calendarfragment.OnControlEvent;
import com.zerodsoft.scheduleweather.calendarfragment.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class DayViewPagerAdapter extends RecyclerView.Adapter<DayViewPagerAdapter.DayViewPagerHolder>
{
    private DayFragment dayFragment;
    private final SparseArray<DayViewPagerHolder> holderSparseArray = new SparseArray<>();
    private final Calendar calendar;

    public DayViewPagerAdapter(DayFragment dayFragment)
    {
        this.dayFragment = dayFragment;
        calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
    }

    public void setData(int position, List<ScheduleDTO> schedules)
    {
        holderSparseArray.get(position).setData(schedules);
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
        // 툴바의 month 설정
        dayFragment.setMonth(holder.startDate);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull DayViewPagerHolder holder)
    {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(@NonNull DayViewPagerHolder holder)
    {
        holderSparseArray.remove(holder.getAdapterPosition());
        holder.clear();
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount()
    {
        return Integer.MAX_VALUE;
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
            Calendar copiedCalendar = (Calendar) calendar.clone();
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
