package com.zerodsoft.scheduleweather.calendarfragment.EventsInfoFragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.Date;
import java.util.List;

public class EventsInfoViewModel extends AndroidViewModel
{
    private EventsInfoRepository repository;
    private MutableLiveData<List<ScheduleDTO>> schedulesMutableLiveData;

    public EventsInfoViewModel(@NonNull Application application)
    {
        super(application);
        repository = new EventsInfoRepository(application);
    }

    public MutableLiveData<List<ScheduleDTO>> getSchedulesMutableLiveData()
    {
        schedulesMutableLiveData = repository.getSchedulesMutableLiveData();
        return schedulesMutableLiveData;
    }

    public void selectSchedules(Date startDate, Date endDate)
    {
        repository.selectSchedules(startDate, endDate);
    }
}
