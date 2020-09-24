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
    @Query("SELECT * FROM TB_SCHEDULE WHERE category = :accountCategory AND (Datetime(start_date) >= Datetime(:startDate) AND Datetime(start_date) < Datetime(:endDate)) " +
            "OR (Datetime(end_date) >= Datetime(:startDate) AND Datetime(end_date) < Datetime(:endDate)) " +
            "OR (Datetime(start_date) < Datetime(:startDate) AND Datetime(end_date) > Datetime(:endDate))")
    LiveData<List<ScheduleDTO>> selectSchedules(int accountCategory, Date startDate, Date endDate);

    @TypeConverters({TypeConverter.class})
    @Query("SELECT * FROM TB_SCHEDULE WHERE (Datetime(start_date) >= Datetime(:startDate) AND Datetime(start_date) < Datetime(:endDate)) " +
            "OR (Datetime(end_date) >= Datetime(:startDate) AND Datetime(end_date) < Datetime(:endDate)) " +
            "OR (Datetime(start_date) < Datetime(:startDate) AND Datetime(end_date) > Datetime(:endDate))")
    LiveData<List<ScheduleDTO>> selectSchedules(Date startDate, Date endDate);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateSchedule(ScheduleDTO scheduleDTO);

    @Query("DELETE FROM TB_SCHEDULE WHERE id = :id")
    void deleteSchedule(int id);
}
