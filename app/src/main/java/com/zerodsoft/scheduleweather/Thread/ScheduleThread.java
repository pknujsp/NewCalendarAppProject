package com.zerodsoft.scheduleweather.Thread;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;

import androidx.lifecycle.LiveData;

import com.zerodsoft.scheduleweather.CalendarView.AccountType;
import com.zerodsoft.scheduleweather.CalendarView.CalendarType;
import com.zerodsoft.scheduleweather.Room.AppDb;
import com.zerodsoft.scheduleweather.Room.DAO.ScheduleDAO;
import com.zerodsoft.scheduleweather.Room.DTO.ScheduleDTO;
import com.zerodsoft.scheduleweather.Room.DTO.TypeConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduleThread extends Thread
{
    private Activity activity;
    private Handler handler;
    private CalendarType calendarType;
    private AccountType accountType;

    private Date startDate;
    private Date endDate;

    public void setInitialData(Activity activity, Handler handler, CalendarType calendarType, AccountType accountType, Date startDate, Date endDate)
    {
        this.activity = activity;
        this.handler = handler;
        this.calendarType = calendarType;
        this.accountType = accountType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public void run()
    {
        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();

        switch (calendarType)
        {
            case DAY:
                // bundle.putParcelableArrayList("schedules", (ArrayList<? extends Parcelable>) selectDaySchedules());
                break;
            case WEEK:
                //  bundle.putParcelableArrayList("schedules", (ArrayList<? extends Parcelable>) selectWeekSchedules());
              //  msg.obj = selectWeekSchedules();
                break;
            case MONTH:
                //  bundle.putParcelableArrayList("schedules", (ArrayList<? extends Parcelable>) selectMonthSchedules());
                break;
        }

        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    private List<ScheduleDTO> selectDaySchedules()
    {
        return null;
    }

/*
    private LiveData<List<ScheduleDTO>> selectWeekSchedules()
    {
        AppDb appDb = AppDb.getInstance(activity);
        ScheduleDAO scheduleDAO = appDb.scheduleDAO();

        return scheduleDAO.selectSchedules(accountType.ordinal(), TypeConverter.dateToTime(startDate), TypeConverter.dateToTime(endDate));
    }
*/

    private List<ScheduleDTO> selectMonthSchedules()
    {
        return null;
    }
}