package com.zerodsoft.scheduleweather.calendarview.viewmodel;

import android.app.Application;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.zerodsoft.scheduleweather.calendarfragment.CalendarTransactionFragment;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.ScheduleDAO;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

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
                if (CalendarTransactionFragment.accountCategory == ScheduleDTO.ALL_CATEGORY)
                {
                    schedules.postValue(scheduleDAO.selectSchedulesNotLive(startDate, endDate));
                } else
                {
                    schedules.postValue(scheduleDAO.selectSchedulesNotLive(CalendarTransactionFragment.accountCategory, startDate, endDate));
                }
            }
        }).start();

    }

    public MutableLiveData<List<ScheduleDTO>> getSchedules()
    {
        return schedules;
    }
}