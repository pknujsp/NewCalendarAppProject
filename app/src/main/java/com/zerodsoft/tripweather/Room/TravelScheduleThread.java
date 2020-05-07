package com.zerodsoft.tripweather.Room;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.tripweather.AddScheduleActivity;
import com.zerodsoft.tripweather.DataCommunication.DownloadData;
import com.zerodsoft.tripweather.R;
import com.zerodsoft.tripweather.Room.DAO.ScheduleDao;
import com.zerodsoft.tripweather.Room.DAO.TravelDao;
import com.zerodsoft.tripweather.Room.DTO.Schedule;
import com.zerodsoft.tripweather.Room.DTO.Travel;
import com.zerodsoft.tripweather.ScheduleList.TravelScheduleListAdapter;
import com.zerodsoft.tripweather.TravelScheduleActivity;
import com.zerodsoft.tripweather.Utility.Actions;
import com.zerodsoft.tripweather.Utility.Clock;
import com.zerodsoft.tripweather.WeatherData.ForecastAreaData;

import java.util.ArrayList;
import java.util.List;

public class TravelScheduleThread extends Thread
{
    private AppDb appDb;
    private Activity activity;
    private ArrayList<Schedule> travelSchedules;
    private Travel travel;
    private int action;
    private int travelId = 0;
    private Handler handler;
    private int downloadDataInt = 0;

    public TravelScheduleThread(Activity activity, int action)
    {
        this.activity = activity;
        appDb = AppDb.getInstance(activity.getApplicationContext());
        this.action = action;
    }

    public TravelScheduleThread(Activity activity, int travelId, int action)
    {
        this.activity = activity;
        appDb = AppDb.getInstance(activity.getApplicationContext());
        this.action = action;
        this.travelId = travelId;
    }


    public TravelScheduleThread(Activity activity, String travelName, ArrayList<Schedule> travelSchedules, int action)
    {
        appDb = AppDb.getInstance(activity.getApplicationContext());
        this.activity = activity;
        this.travelSchedules = travelSchedules;
        this.travel = new Travel();
        this.travel.setName(travelName);
        this.travel.setId(0);
        this.action = action;
    }

    @Override
    public void run()
    {
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        ScheduleDao scheduleDao = appDb.scheduleDao();
        TravelDao travelDao = appDb.travelDao();

        if (action == AddScheduleActivity.INSERT_TRAVEL)
        {
            // 일정 추가 완료 시
            // 여행정보를 INSERT하고 travelId를 반환받음
            long travelId = travelDao.insertTravel(travel);
            ArrayList<Integer> scheduleIdList = new ArrayList<>();

            for (Schedule scheduleData : travelSchedules)
            {
                Schedule schedule = new Schedule();

                schedule.setParentId((int) travelId);
                schedule.setAreaId(scheduleData.getArea().getArea_id());
                schedule.setAreaName(scheduleData.getArea().toString());
                schedule.setAreaX(scheduleData.getArea().getX());
                schedule.setAreaY(scheduleData.getArea().getY());
                schedule.setStartDate(Clock.dateFormatSlash.format(scheduleData.getStartDateObj().getTime()));
                schedule.setEndDate(Clock.dateFormatSlash.format(scheduleData.getEndDateObj().getTime()));

                // 여행 일정을 INSERT하고 scheduleId를 반환받음
                long scheduleId = scheduleDao.insertSchedule(schedule);
                scheduleIdList.add((int) scheduleId);
            }

            Bundle bundle = new Bundle();
            bundle.putInt("travelId", (int) travelId);

            Message msg = handler.obtainMessage();
            msg.setData(bundle);
            msg.what = Actions.DOWNLOAD_NFORECAST_DATA;
            handler.sendMessage(msg);
        } else if (action == Actions.SET_MAINACTIVITY_VIEW)
        {
            // 앱 실행 직후
            List<Travel> travelList = travelDao.getAllTravels();

            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    RecyclerView recyclerView = (RecyclerView) activity.findViewById(R.id.recycler_view_schedule);
                    recyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));

                    TravelScheduleListAdapter adapter = new TravelScheduleListAdapter(activity, travelList);
                    recyclerView.setAdapter(adapter);
                }
            });
        } else if (action == Actions.CLICKED_TRAVEL_ITEM)
        {
            // 아이템을 클릭 했을때
            ArrayList<Schedule> scheduleList = (ArrayList<Schedule>) scheduleDao.getAllSchedules(travelId);
            Travel travelInfo = travelDao.getTravelInfo(travelId);

            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Intent intent = new Intent(activity.getApplicationContext(), TravelScheduleActivity.class);

                    Bundle bundle = new Bundle();

                    bundle.putString("travelName", travelInfo.getName());
                    bundle.putInt("travelId", travelId);
                    bundle.putInt("download", downloadDataInt);
                    bundle.putSerializable("scheduleList", scheduleList);
                    intent.putExtras(bundle);

                    activity.startActivity(intent);
                }
            });
        }
    }

    public void setHandler(Handler handler)
    {
        this.handler = handler;
    }

    public void setDownloadDataInt(int downloadDataInt)
    {
        this.downloadDataInt = downloadDataInt;
    }
}
