package com.zerodsoft.scheduleweather.calendarview.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

import java.util.Date;
import java.util.List;

public class CalendarViewModel extends AndroidViewModel
{
    private LiveData<List<ScheduleDTO>> schedulesLiveData;
    private CalendarRepository calendarRepository;

    public CalendarViewModel(@NonNull Application application)
    {
        super(application);
        calendarRepository = new CalendarRepository(application);
    }

    public LiveData<List<ScheduleDTO>> getSchedulesLiveData()
    {
        return schedulesLiveData;
    }

    public void selectSchedules(int accountCategory, Date startDate, Date endDate)
    {
        calendarRepository.selectSchedules(accountCategory, startDate, endDate);
    }

}
