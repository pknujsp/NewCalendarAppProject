package com.zerodsoft.scheduleweather.CalendarView.ViewModel;

import android.app.Application;
import android.content.Context;
import android.graphics.Paint;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.zerodsoft.scheduleweather.CalendarView.AccountType;
import com.zerodsoft.scheduleweather.Room.AppDb;
import com.zerodsoft.scheduleweather.Room.DAO.ScheduleDAO;
import com.zerodsoft.scheduleweather.Room.DTO.ScheduleDTO;
import com.zerodsoft.scheduleweather.Room.DTO.TypeConverter;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeekViewModel extends AndroidViewModel
{
    private ScheduleDAO scheduleDAO;
    private ExecutorService executorService;
    private Context context;

    private MediatorLiveData<List<ScheduleDTO>> schedules = new MediatorLiveData<>();
    private int pagePosition;

    public WeekViewModel(@NonNull Application application)
    {
        super(application);
        context = application.getApplicationContext();
        scheduleDAO = AppDb.getInstance(application.getApplicationContext()).scheduleDAO();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<ScheduleDTO>> selectSchedules(AccountType accountType, Date startDate, Date endDate)
    {
        LiveData<List<ScheduleDTO>> resultList = scheduleDAO.selectSchedules(accountType.ordinal(), startDate, endDate);
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
