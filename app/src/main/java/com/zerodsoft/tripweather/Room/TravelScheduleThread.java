package com.zerodsoft.tripweather.Room;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.tripweather.R;
import com.zerodsoft.tripweather.Room.DAO.ScheduleDao;
import com.zerodsoft.tripweather.Room.DAO.TravelDao;
import com.zerodsoft.tripweather.Room.DTO.Area;
import com.zerodsoft.tripweather.Room.DTO.Schedule;
import com.zerodsoft.tripweather.Room.DTO.Travel;
import com.zerodsoft.tripweather.ScheduleData.TravelData;
import com.zerodsoft.tripweather.ScheduleData.TravelSchedule;
import com.zerodsoft.tripweather.ScheduleList.ScheduleListAdapter;
import com.zerodsoft.tripweather.ScheduleList.TravelScheduleListAdapter;
import com.zerodsoft.tripweather.TravelScheduleActivity;

import java.util.ArrayList;
import java.util.List;

public class TravelScheduleThread extends Thread
{
    private AppDb appDb;
    private Activity activity;
    private ArrayList<TravelSchedule> travelSchedules;
    private Travel travel;
    private int action;
    private int travelId = 0;

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


    public TravelScheduleThread(Activity activity, String travelName, ArrayList<TravelSchedule> travelSchedules, int action)
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

        if (action == 0)
        {
            // 일정 추가 완료 시
            long travelId = travelDao.insertTravel(travel);

            for (TravelSchedule scheduleData : travelSchedules)
            {
                Schedule schedule = new Schedule();

                schedule.setParentId((int) travelId);
                schedule.setAreaName(scheduleData.getTravelDestination().toString());
                schedule.setAreaX(scheduleData.getTravelDestination().getX());
                schedule.setAreaY(scheduleData.getTravelDestination().getY());
                schedule.setStartDate(scheduleData.getStartDate().getYear() + scheduleData.getStartDate().getMonth() + scheduleData.getStartDate().getDay());
                schedule.setEndDate(scheduleData.getEndDate().getYear() + scheduleData.getEndDate().getMonth() + scheduleData.getEndDate().getDay());

                scheduleDao.insertSchedule(schedule);
            }
            List<Travel> travels = travelDao.getAllTravels();

            ArrayList<TravelData> data = new ArrayList<>();

            for (Travel travel : travels)
            {
                TravelData travelData = new TravelData();
                travelData.setTravelName(travel.getName());
                travelData.setPeriod(travel.getName());
                travelData.setTravelId(travel.getId());
                data.add(travelData);
            }

            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    RecyclerView recyclerView = (RecyclerView) activity.findViewById(R.id.recycler_view_schedule);
                    recyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));

                    TravelScheduleListAdapter adapter = new TravelScheduleListAdapter(activity, data);
                    recyclerView.setAdapter(adapter);
                }
            });
        } else if (action == 1)
        {
            // 앱 실행 직후
            List<Travel> travelList = travelDao.getAllTravels();

            ArrayList<TravelData> data = new ArrayList<>();

            for (Travel travel : travelList)
            {
                TravelData travelData = new TravelData();
                travelData.setTravelName(travel.getName());
                travelData.setPeriod(travel.getName());
                travelData.setTravelId(travel.getId());
                data.add(travelData);
            }

            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    RecyclerView recyclerView = (RecyclerView) activity.findViewById(R.id.recycler_view_schedule);
                    recyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));

                    TravelScheduleListAdapter adapter = new TravelScheduleListAdapter(activity, data);
                    recyclerView.setAdapter(adapter);
                }
            });
        } else if (action == 2)
        {
            // 아이템을 클릭 했을때
            List<Schedule> scheduleList = scheduleDao.getAllSchedules(travelId);

            ArrayList<TravelSchedule> travelSchedules = new ArrayList<>();

            for (Schedule schedule : scheduleList)
            {
                TravelSchedule travelSchedule = new TravelSchedule();

                travelSchedule.setAreaName(schedule.getAreaName());
                travelSchedule.setStartDateStr(schedule.getStartDate());
                travelSchedule.setEndDateStr(schedule.getEndDate());

                travelSchedules.add(travelSchedule);
            }

            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Intent intent = new Intent(activity.getApplicationContext(), TravelScheduleActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("scheduleList", travelSchedules);
                    intent.putExtras(bundle);

                    activity.startActivity(intent);
                }
            });
        }
    }

}
