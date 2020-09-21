package com.zerodsoft.scheduleweather.room.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.room.dto.TypeConverter;

import java.util.Date;
import java.util.List;

@Dao
public interface ScheduleDAO
{
    @Insert
    long insertNewSchedule(ScheduleDTO scheduleDTO);

    @Query("SELECT * FROM TB_SCHEDULE WHERE id = :scheduleId")
    LiveData<ScheduleDTO> selectSchedule(int scheduleId);

    @TypeConverters({TypeConverter.class})
    @Query("SELECT * FROM TB_SCHEDULE WHERE category = :category AND ((Datetime(start_date) BETWEEN Datetime(:weekFirstDate) AND Datetime(:weekLastDate)) OR (Datetime(end_date) BETWEEN Datetime(:weekFirstDate) AND Datetime(:weekLastDate)))")
    List<ScheduleDTO> selectSchedules(int category, Date weekFirstDate, Date weekLastDate);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateSchedule(ScheduleDTO scheduleDTO);

    @Query("DELETE FROM TB_SCHEDULE WHERE id = :id")
    void deleteSchedule(int id);
}
