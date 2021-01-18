package com.zerodsoft.scheduleweather.calendarview.viewmodel;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.calendarfragment.EventTransactionFragment;
import com.zerodsoft.scheduleweather.room.AppDb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarRepository
{
    private ScheduleDAO scheduleDAO;
    public MutableLiveData<List<ScheduleDTO>> schedules;


    public CalendarRepository(Application application)
    {
        scheduleDAO = AppDb.getInstance(application.getApplicationContext()).scheduleDAO();
        Calendar calendar = Calendar.getInstance();
        calendar.set(1970, 5, 5);
        schedules = new MutableLiveData<>();
        schedules.setValue(new ArrayList<ScheduleDTO>());
    }

    public void selectSchedules(Date startDate, Date endDate)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if (EventTransactionFragment.accountCategory == ScheduleDTO.ALL_CATEGORY)
                {
                    schedules.postValue(scheduleDAO.selectSchedulesNotLive(startDate, endDate));
                } else
                {
                    schedules.postValue(scheduleDAO.selectSchedulesNotLive(EventTransactionFragment.accountCategory, startDate, endDate));
                }
            }
        }).start();

    }

    public MutableLiveData<List<ScheduleDTO>> getSchedules()
    {
        return schedules;
    }
}