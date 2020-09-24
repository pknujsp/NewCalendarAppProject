package com.zerodsoft.scheduleweather.calendarview.day;

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

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarfragment.DayFragment;
import com.zerodsoft.scheduleweather.calendarview.viewmodel.CalendarViewModel;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.utility.Clock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DayViewPagerAdapter extends RecyclerView.Adapter<DayViewPagerAdapter.DayViewPagerHolder>
{
    private int lastPosition = DayFragment.firstViewPosition;
    private DayFragment dayFragment;
    private Calendar calendar;

    public DayViewPagerAdapter(DayFragment dayFragment, Calendar calendar)
    {
        this.dayFragment = dayFragment;
        this.calendar = calendar;
        this.calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
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
        holder.onBind(position);
    }

    @Override
    public int getItemCount()
    {
        return Integer.MAX_VALUE;
    }


    class DayViewPagerHolder extends RecyclerView.ViewHolder
    {
        TextView headerDate;
        TextView eventTotalNum;
        ListView allDayList;

        EventListAdapter eventListAdapter;
        List<ScheduleDTO> schedules;
        List<ScheduleDTO> allDaySchedules;
        CalendarViewModel calendarViewModel;

        int accountCategory = ScheduleDTO.ALL_CATEGORY;
        Date startDate = new Date();
        Date endDate = new Date();

        int position;

        public DayViewPagerHolder(View view)
        {
            super(view);
            allDayList = (ListView) view.findViewById(R.id.dayview_eventlist);
            headerDate = (TextView) view.findViewById(R.id.dayview_date);
            eventTotalNum = (TextView) view.findViewById(R.id.dayview_eventtotalnum);

            eventListAdapter = new EventListAdapter();
            allDayList.setAdapter(eventListAdapter);
        }

        public void onBind(int position)
        {
            // 시작 날짜와 종료 날짜 비교하는 코드 수정 필요
            this.position = position;

            // 날짜와 요일 표시
            headerDate.setText(Clock.DATE_DAY_OF_WEEK_FORMAT.format(calendar.getTime()));

            if (position < lastPosition)
            {
                // 전날로 이동
                calendar.add(Calendar.DATE, -1);
            } else if (position > lastPosition)
            {
                // 다음날로 이동
                calendar.add(Calendar.DATE, 1);
            }
            lastPosition = position;

            startDate = calendar.getTime();
            // 24시간 뒤로 설정
            calendar.add(Calendar.DATE, 1);
            endDate = calendar.getTime();
            // 원상복구
            calendar.add(Calendar.DATE, -1);

            // 이벤트 목록을 가져옴
            calendarViewModel = new ViewModelProvider(dayFragment).get(CalendarViewModel.class);
            calendarViewModel.selectSchedules(accountCategory, startDate, endDate);
            calendarViewModel.getSchedulesLiveData().observe(dayFragment, new Observer<List<ScheduleDTO>>()
            {
                @Override
                public void onChanged(List<ScheduleDTO> scheduleList)
                {
                    if (scheduleList != null)
                    {
                        schedules = scheduleList;
                        allDaySchedules = new ArrayList<>();

                        for (ScheduleDTO schedule : schedules)
                        {
                            if (schedule.getDateType() == ScheduleDTO.DATE_ALLDAY)
                            {
                                allDaySchedules.add(schedule);
                            }
                        }
                    }
                }
            });
            // 하루종일 일정 리스트를 리스트 어댑터에 전달
            eventListAdapter.setAllDaySchedules(allDaySchedules);
            eventListAdapter.notifyDataSetChanged();

            // 전체 일정의 개수를 헤더 텍스트뷰에 표시
            eventTotalNum.setText(Integer.toString(schedules.size()));
            eventListAdapter.notifyDataSetChanged();
        }
    }
}
