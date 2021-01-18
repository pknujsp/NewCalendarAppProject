package com.zerodsoft.scheduleweather.calendarfragment.EventsInfoFragment;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.room.AppDb;

import java.util.Date;
import java.util.List;

public class EventsInfoRepository
{
    private MutableLiveData<List<ScheduleDTO>> schedulesMutableLiveData;
    private ScheduleDAO scheduleDAO;

    public EventsInfoRepository(Application application)
    {
        scheduleDAO = AppDb.getInstance(application.getApplicationContext()).scheduleDAO();
        schedulesMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<List<ScheduleDTO>> getSchedulesMutableLiveData()
    {
        return schedulesMutableLiveData;
    }

    public void selectSchedules(Date startDate, Date endDate)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                schedulesMutableLiveData.postValue(scheduleDAO.selectSchedulesNotLive(startDate, endDate));
            }
        }).start();
    }
}
