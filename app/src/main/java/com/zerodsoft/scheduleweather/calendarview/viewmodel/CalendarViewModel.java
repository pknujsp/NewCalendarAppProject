package com.zerodsoft.scheduleweather.calendarview.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

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
