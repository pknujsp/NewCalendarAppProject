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
