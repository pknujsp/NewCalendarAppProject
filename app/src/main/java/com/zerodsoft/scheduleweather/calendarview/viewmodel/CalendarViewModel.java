package com.zerodsoft.scheduleweather.calendarview.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.zerodsoft.scheduleweather.calendarfragment.DayFragment;
import com.zerodsoft.scheduleweather.calendarfragment.MonthFragment;
import com.zerodsoft.scheduleweather.calendarfragment.WeekFragment;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarViewModel extends AndroidViewModel
{
    private MutableLiveData<List<ScheduleDTO>> schedulesLiveData;
    private CalendarRepository calendarRepository;

    public CalendarViewModel(@NonNull Application application)
    {
        super(application);
        // 마지막으로 사용된 달력의 종류 가져오기
        /*
        int accountCategory = ScheduleDTO.ALL_CATEGORY;
        String calendarTag = DayFragment.TAG;

        Date startDate = null, endDate = null;
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

        switch (calendarTag)
        {
            case MonthFragment.TAG:
                // startDate를 이번 달의 1일로 설정
                // endDate를 이번 달의 말일로 설정

                // DAY_OF_MONTH가 2인 경우 : -1을 add한다
                calendar.add(Calendar.DATE, -(calendar.get(Calendar.DAY_OF_MONTH) + 1));
                startDate = calendar.getTime();

                calendar.add(Calendar.DATE, (calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - 1));
                endDate = calendar.getTime();
                break;
            case WeekFragment.TAG:
                // startDate를 이번 주 일요일로 0시0분으로 설정
                // endDate를 다음 주 일요일 0시0분으로 설정
                calendar.add(Calendar.DATE, -(calendar.get(Calendar.DAY_OF_WEEK) - 1));
                startDate = calendar.getTime();

                calendar.add(Calendar.DATE, 7);
                endDate = calendar.getTime();
                break;
            case DayFragment.TAG:
                // startDate를 오늘 0시0분으로 설정
                // endDate를 다음 날 0시0분으로 설정
                startDate = calendar.getTime();

                calendar.add(Calendar.DATE, 1);
                endDate = calendar.getTime();
                break;
        }

         */
        calendarRepository = new CalendarRepository(application);
        schedulesLiveData = calendarRepository.getSchedules();
    }

    public MutableLiveData<List<ScheduleDTO>> getSchedulesLiveData()
    {
        return schedulesLiveData;
    }

    public void selectSchedules(Date startDate, Date endDate)
    {
        calendarRepository.selectSchedules(startDate, endDate);
    }
}
