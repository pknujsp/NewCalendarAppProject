package com.zerodsoft.scheduleweather.calendarview.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.ScheduleDAO;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

import java.util.List;

public class WeekViewModel extends AndroidViewModel
{
    private ScheduleDAO scheduleDAO;
    private Context context;

    private MediatorLiveData<List<ScheduleDTO>> schedules = new MediatorLiveData<>();
    private int pagePosition;

    public WeekViewModel(@NonNull Application application)
    {
        super(application);
        context = application.getApplicationContext();
        scheduleDAO = AppDb.getInstance(application.getApplicationContext()).scheduleDAO();
    }

    /*
    public LiveData<List<ScheduleDTO>> selectSchedules(AccountType accountType, Date startDate, Date endDate)
    {

        final LiveData<List<ScheduleDTO>> resultList = scheduleDAO.selectSchedules(accountType.ordinal(), TypeConverter.dateToTime(startDate), TypeConverter.dateToTime(endDate));

        schedules.addSource(resultList, new Observer<List<ScheduleDTO>>()
        {
            @Override
            public void onChanged(List<ScheduleDTO> scheduleDTOS)
            {
                if (scheduleDTOS == null || scheduleDTOS.isEmpty())
                {

                } else
                {
                    schedules.removeSource(resultList);
                    schedules.setValue(scheduleDTOS);
                }
            }
        });
        return schedules;


    }

     */

    public LiveData<List<ScheduleDTO>> getSchedules()
    {
        return schedules;
    }

    public void setPagePosition(int position)
    {
        pagePosition = position;
    }

    public int getPagePosition()
    {
        return pagePosition;
    }
}
