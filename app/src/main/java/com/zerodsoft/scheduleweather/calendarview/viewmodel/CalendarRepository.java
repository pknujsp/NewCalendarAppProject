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
    private MutableLiveData<List<ScheduleDTO>> schedulesLiveData = new MutableLiveData<>();
    private LiveData<List<ScheduleDTO>> schedules;

    public CalendarRepository(Application application)
    {
        scheduleDAO = AppDb.getInstance(application.getApplicationContext()).scheduleDAO();
    }

    public void selectSchedules(Date startDate, Date endDate)
    {
        if (CalendarTransactionFragment.accountCategory == ScheduleDTO.ALL_CATEGORY)
        {
            schedules = scheduleDAO.selectSchedules(startDate, endDate);
        } else
        {
            schedules = scheduleDAO.selectSchedules(CalendarTransactionFragment.accountCategory, startDate, endDate);
        }
        schedulesLiveData.postValue(schedules.getValue() != null ? schedules.getValue() : new ArrayList<ScheduleDTO>());
    }

    public MutableLiveData<List<ScheduleDTO>> getSchedulesLiveData()
    {
        return schedulesLiveData;
    }
}