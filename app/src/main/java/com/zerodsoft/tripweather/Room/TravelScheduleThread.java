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
import com.zerodsoft.tripweather.Room.DAO.NforecastDao;
import com.zerodsoft.tripweather.Room.DAO.ScheduleDao;
import com.zerodsoft.tripweather.Room.DAO.TravelDao;
import com.zerodsoft.tripweather.Room.DAO.WeatherUpdateTimeDao;
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
    private Handler scheduleActivityHandler;
    private Handler addScheduleActivityHandler;
    private Handler mainActivityHandler;

    public TravelScheduleThread(Activity activity)
    {
        this.activity = activity;
        appDb = AppDb.getInstance(activity.getApplicationContext());
    }

    @Override
    public void run()
    {
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        ScheduleDao scheduleDao = appDb.scheduleDao();
        TravelDao travelDao = appDb.travelDao();

        if (action == Actions.INSERT_TRAVEL)
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

            Message msg = addScheduleActivityHandler.obtainMessage();
            msg.setData(bundle);
            msg.what = Actions.DOWNLOAD_NFORECAST_DATA;
            addScheduleActivityHandler.sendMessage(msg);
        } else if (action == Actions.SET_MAINACTIVITY_VIEW)
        {
            // 앱 실행 직후
            ArrayList<Travel> travelList = (ArrayList<Travel>) travelDao.getAllTravels();

            Message msg = mainActivityHandler.obtainMessage();
            msg.what = Actions.SET_TRAVEL_RECYCLERVIEW_ADAPTER;
            Bundle bundle = new Bundle();
            bundle.putSerializable("travelList", travelList);
            msg.setData(bundle);

            mainActivityHandler.sendMessage(msg);
        } else if (action == Actions.CLICKED_TRAVEL_ITEM)
        {
            // 아이템을 클릭 했을때
            ArrayList<Schedule> scheduleList = (ArrayList<Schedule>) scheduleDao.getAllSchedules(travelId);
            Travel travelInfo = travelDao.getTravelInfo(travelId);

            // mainActivityHandler, scheduleActivityHandler 사용
            if (mainActivityHandler != null)
            {
                Message msg = mainActivityHandler.obtainMessage();
                Bundle bundle = new Bundle();

                msg.what = Actions.START_SCHEDULE_ACTIVITY;
                bundle.putString("travelName", travelInfo.getName());
                bundle.putInt("travelId", travelId);
                bundle.putSerializable("scheduleList", scheduleList);
                msg.setData(bundle);

                mainActivityHandler.sendMessage(msg);
            } else if (addScheduleActivityHandler != null)
            {
                Message msg = addScheduleActivityHandler.obtainMessage();
                Bundle bundle = new Bundle();

                msg.what = Actions.START_SCHEDULE_ACTIVITY;
                bundle.putString("travelName", travelInfo.getName());
                bundle.putInt("travelId", travelId);
                bundle.putSerializable("scheduleList", scheduleList);
                msg.setData(bundle);

                addScheduleActivityHandler.sendMessage(msg);
            }

        } else if (action == Actions.DELETE_TRAVEL)
        {
            // 업데이트 시각 데이터 제거, 동네예보 정보 제거, schedule 데이터 제거, travel 데이터 제거
            WeatherUpdateTimeDao weatherUpdateTimeDao = appDb.weatherUpdateTimeDao();
            NforecastDao nforecastDao = appDb.nforecastDao();
            ArrayList<ScheduleIdTuple> scheduleIdList = (ArrayList<ScheduleIdTuple>) scheduleDao.getScheduleIdList(travelId);

            // 업데이트 시각 데이터 제거
            if (weatherUpdateTimeDao.deleteTravelUpdateData(travelId) == 1)
            {
                int count = 0;

                for (ScheduleIdTuple tuple : scheduleIdList)
                {
                    // 동네예보 데이터 제거
                    nforecastDao.deleteNforecastData(tuple.getScheduleId());
                    count++;
                }

                if (count == scheduleIdList.size())
                {
                    // schedule 데이터 제거
                    if (scheduleDao.deleteSchedules(travelId) > 0)
                    {
                        // travel 데이터 제거
                        if (travelDao.deleteTravel(travelId) > 0)
                        {
                            // 모든 데이터 제거 완료
                            Message msg = mainActivityHandler.obtainMessage();

                            msg.what = Actions.FINISHED_DELETE_TRAVEL;
                            mainActivityHandler.sendMessage(msg);
                        }
                    }
                }
            }
        } else if (action == Actions.UPDATE_SCHEDULE)
        {
            ArrayList<Schedule> scheduleList = (ArrayList<Schedule>) scheduleDao.getAllSchedules(travelId);
            Travel travel = travelDao.getTravelInfo(travelId);
            Bundle bundle = new Bundle();
            Message msg = mainActivityHandler.obtainMessage();

            bundle.putSerializable("scheduleList", scheduleList);
            bundle.putSerializable("travel", travel);
            msg.what = Actions.START_EDIT_SCHEDULE_ACTIVITY;
            msg.setData(bundle);

            mainActivityHandler.sendMessage(msg);
        } else if (action == Actions.UPDATE_TRAVEL)
        {
            // 업데이트 시각 데이터 제거, 동네예보 정보 제거, schedule 데이터 제거
            WeatherUpdateTimeDao weatherUpdateTimeDao = appDb.weatherUpdateTimeDao();
            NforecastDao nforecastDao = appDb.nforecastDao();
            ArrayList<ScheduleIdTuple> scheduleIdList = (ArrayList<ScheduleIdTuple>) scheduleDao.getScheduleIdList(travelId);

            // 업데이트 시각 데이터 제거
            if (weatherUpdateTimeDao.deleteTravelUpdateData(travelId) == 1)
            {
                int count = 0;

                for (ScheduleIdTuple tuple : scheduleIdList)
                {
                    // 동네예보 데이터 제거
                    nforecastDao.deleteNforecastData(tuple.getScheduleId());
                    count++;
                }

                if (count == scheduleIdList.size())
                {
                    // schedule 데이터 제거
                    if (scheduleDao.deleteSchedules(travelId) > 0)
                    {
                        if (travel != null)
                        {
                            travelDao.updateTravelName(travel.getName(), travelId);
                        }

                        ArrayList<Integer> newScheduleIdList = new ArrayList<>();

                        for (Schedule scheduleData : travelSchedules)
                        {
                            Schedule schedule = new Schedule();

                            schedule.setParentId(travelId);
                            schedule.setAreaId(scheduleData.getArea().getArea_id());
                            schedule.setAreaName(scheduleData.getArea().toString());
                            schedule.setAreaX(scheduleData.getArea().getX());
                            schedule.setAreaY(scheduleData.getArea().getY());
                            schedule.setStartDate(Clock.dateFormatSlash.format(scheduleData.getStartDateObj().getTime()));
                            schedule.setEndDate(Clock.dateFormatSlash.format(scheduleData.getEndDateObj().getTime()));

                            // 여행 일정을 INSERT하고 scheduleId를 반환받음
                            long scheduleId = scheduleDao.insertSchedule(schedule);
                            newScheduleIdList.add((int) scheduleId);
                        }

                        Bundle bundle = new Bundle();
                        bundle.putInt("travelId", (int) travelId);

                        Message msg = addScheduleActivityHandler.obtainMessage();
                        msg.setData(bundle);
                        msg.what = Actions.DOWNLOAD_NFORECAST_DATA;
                        addScheduleActivityHandler.sendMessage(msg);
                    }
                }
            }
        }
    }

    public void setMainActivityHandler(Handler mainActivityHandler)
    {
        this.mainActivityHandler = mainActivityHandler;
    }

    public void setAction(int action)
    {
        this.action = action;
    }

    public int getAction()
    {
        return action;
    }

    public Handler getScheduleActivityHandler()
    {
        return scheduleActivityHandler;
    }

    public void setScheduleActivityHandler(Handler scheduleActivityHandler)
    {
        this.scheduleActivityHandler = scheduleActivityHandler;
    }

    public Handler getAddScheduleActivityHandler()
    {
        return addScheduleActivityHandler;
    }

    public void setAddScheduleActivityHandler(Handler addScheduleActivityHandler)
    {
        this.addScheduleActivityHandler = addScheduleActivityHandler;
    }

    public void setTravelId(int travelId)
    {
        this.travelId = travelId;
    }

    public int getTravelId()
    {
        return travelId;
    }

    public void setTravelSchedules(ArrayList<Schedule> travelSchedules)
    {
        this.travelSchedules = travelSchedules;
    }

    public ArrayList<Schedule> getTravelSchedules()
    {
        return travelSchedules;
    }

    public void setTravel(String travelName)
    {
        Travel newTravel = new Travel();

        newTravel.setName(travelName);
        this.travel = newTravel;
    }
}
