package com.zerodsoft.scheduleweather.calendarview.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.ScheduleDAO;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

import java.util.Date;
import java.util.List;

public class CalendarRepository
{
    private ScheduleDAO scheduleDAO;
    private LiveData<List<ScheduleDTO>> schedulesLiveData;

    public CalendarRepository(Application application)
    {
        scheduleDAO = AppDb.getInstance(application.getApplicationContext()).scheduleDAO();
    }

    public void selectSchedules(int accountCategory, Date startDate, Date endDate)
    {
        if (accountCategory == ScheduleDTO.ALL_CATEGORY)
        {
            schedulesLiveData = scheduleDAO.selectSchedules(startDate, endDate);
        } else
        {
            schedulesLiveData = scheduleDAO.selectSchedules(accountCategory, startDate, endDate);
        }
    }


    public LiveData<List<ScheduleDTO>> getSchedulesLiveData()
    {
        return schedulesLiveData;
    }
}
