package com.zerodsoft.scheduleweather.calendarview.day;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.AppMainActivity;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarfragment.CalendarTransactionFragment;
import com.zerodsoft.scheduleweather.calendarfragment.DayFragment;
import com.zerodsoft.scheduleweather.calendarview.viewmodel.CalendarViewModel;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.utility.Clock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class DayViewPagerAdapter extends RecyclerView.Adapter<DayViewPagerAdapter.DayViewPagerHolder>
{
    private DayFragment dayFragment;
    private SparseArray<DayViewPagerHolder> holderSparseArray = new SparseArray<>();
    private List<ScheduleDTO> firstSchedules;

    private int currentPosition = CalendarTransactionFragment.FIRST_VIEW_POSITION;
    private int lastPosition = CalendarTransactionFragment.FIRST_VIEW_POSITION;

    private Calendar calendar;
    private Date startDate = new Date();
    private Date endDate = new Date();

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
    }

    @Override
    public void onViewAttachedToWindow(@NonNull DayViewPagerHolder holder)
    {
        holder.onBind(holder.getAdapterPosition());
        holderSparseArray.put(holder.getAdapterPosition(), holder);
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public int getItemCount()
    {
        return Integer.MAX_VALUE;
    }

    class DayViewPagerHolder extends RecyclerView.ViewHolder
    {
        // 시간별 리스트 레이아웃 표시
        TextView headerDate;
        TextView eventTotalNum;
        ListView allDayList;
        DayView dayView;

        List<ScheduleDTO> schedules;
        List<ScheduleDTO> allDaySchedules;

        int position;

        public DayViewPagerHolder(View view)
        {
            super(view);
            headerDate = (TextView) view.findViewById(R.id.dayview_date);
            eventTotalNum = (TextView) view.findViewById(R.id.dayview_eventtotalnum);
            dayView = (DayView) view.findViewById(R.id.dayview);
        }

        public void onBind(int position)
        {
            this.position = position;

            // drag 성공 시에만 SETTLING 직후 호출
            lastPosition = currentPosition;
            currentPosition = position;

            if (currentPosition < lastPosition)
            {
                // 전날로 이동
                calendar.add(Calendar.DATE, -1);
            } else if (position > lastPosition)
            {
                // 다음날로 이동
                calendar.add(Calendar.DATE, 1);
            }
            startDate = calendar.getTime();
            // 헤더의 날짜 설정
            setDate(startDate);
            // 툴바의 month 설정
            dayFragment.setMonth(startDate);

            // 24시간 뒤로 설정
            calendar.add(Calendar.DATE, 1);
            endDate = calendar.getTime();
            // 원상복구
            calendar.add(Calendar.DATE, -1);

            dayFragment.requestSchedules(currentPosition, startDate, endDate);
        }

        public void setDate(Date date)
        {
            // 날짜와 요일 표시
            headerDate.setText(Clock.DATE_DAY_OF_WEEK_FORMAT.format(date));
            dayView.setDate(date);
        }

        public void setData(List<ScheduleDTO> schedules)
        {
            this.schedules = schedules;
            allDaySchedules = new ArrayList<>();

            for (ScheduleDTO schedule : schedules)
            {
                if (schedule.getDateType() == ScheduleDTO.DATE_ALLDAY)
                {
                    allDaySchedules.add(schedule);
                }
            }


            // 전체 일정의 개수를 헤더 텍스트뷰에 표시
            eventTotalNum.setText(Integer.toString(schedules.size()));

            // DayView에 이벤트를 표시
          //  dayView.setScheduleList(schedules);
            dayView.invalidate();
        }
    }
}
