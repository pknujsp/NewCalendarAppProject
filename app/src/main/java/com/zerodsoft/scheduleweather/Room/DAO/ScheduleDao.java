package com.zerodsoft.scheduleweather.Room.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.zerodsoft.scheduleweather.Room.DTO.Schedule;
import com.zerodsoft.scheduleweather.Room.ScheduleIdTuple;

import java.util.List;

@Dao
public interface ScheduleDao
{
    @Query("SELECT * FROM schedule_table WHERE schedule_parent_id = :parentId")
    List<Schedule> getAllSchedules(int parentId);

    @Query("SELECT schedule_id FROM schedule_table WHERE schedule_parent_id = :travelId")
    List<ScheduleIdTuple> getScheduleIdList(int travelId);

    @Insert
    long insertSchedule(Schedule schedule);

    @Update
    void updateSchedule(Schedule schedule);

    @Query("DELETE FROM schedule_table WHERE schedule_id = :id")
    void deleteSchedule(int id);

    @Query("DELETE FROM schedule_table WHERE schedule_parent_id = :travelId")
    int deleteSchedules(int travelId);
}
