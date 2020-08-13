package com.zerodsoft.scheduleweather.CalendarView.ViewModel;

import android.app.Application;
import android.graphics.Paint;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.CalendarView.AccountType;
import com.zerodsoft.scheduleweather.Room.AppDb;
import com.zerodsoft.scheduleweather.Room.DAO.ScheduleDAO;
import com.zerodsoft.scheduleweather.Room.DTO.ScheduleDTO;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeekViewModel extends AndroidViewModel
{
    private ScheduleDAO scheduleDAO;
    private ExecutorService executorService;

    private LiveData<List<ScheduleDTO>> schedules = new MutableLiveData<>();
    private int pagePosition;

    public WeekViewModel(@NonNull Application application)
    {
        super(application);
        scheduleDAO = AppDb.getInstance(application.getApplicationContext()).scheduleDAO();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<ScheduleDTO>> selectSchedules(AccountType accountType, long startDate, long endDate)
    {
        schedules = scheduleDAO.selectSchedules(accountType.ordinal(), startDate, endDate);
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
