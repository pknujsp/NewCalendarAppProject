package com.zerodsoft.tripweather.Room.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.zerodsoft.tripweather.Room.DTO.Schedule;

import java.util.List;

@Dao
public interface ScheduleDao
{
    @Query("SELECT * FROM schedule_table WHERE schedule_parent_id = :parentId")
    List<Schedule> getAllSchedules(int parentId);

    @Insert
    void insertSchedule(Schedule schedule);

    @Update
    void updateSchedule(Schedule schedule);

    @Delete
    void deleteSchedule(Schedule schedule);

    @Query("DELETE FROM schedule_table WHERE schedule_id = :id")
    void deleteSchedule(int id);

    @Query("DELETE FROM schedule_table WHERE schedule_parent_id = :parentId")
    void deleteSchedules(int parentId);
}
