package com.zerodsoft.scheduleweather.Room.DAO;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.zerodsoft.scheduleweather.Room.DTO.ScheduleDTO;
import com.zerodsoft.scheduleweather.Room.DTO.TypeConverter;

import java.util.Date;
import java.util.List;

@Dao
public interface ScheduleDAO
{
    @Insert
    long insertNewSchedule(ScheduleDTO scheduleDTO);

    @Query("UPDATE TB_SCHEDULE SET place_id_to_be_visited = :placeId WHERE id = :scheduleId")
    void updatePlaceId(int scheduleId, int placeId);

    @Query("UPDATE TB_SCHEDULE SET address_id_to_be_visited = :addressId WHERE id = :scheduleId")
    void updateAddressId(int scheduleId, int addressId);

    @Query("SELECT * FROM TB_SCHEDULE WHERE id = :scheduleId")
    ScheduleDTO selectSchedule(int scheduleId);

    @TypeConverters({TypeConverter.class})
    @Query("SELECT * FROM TB_SCHEDULE WHERE category = :category AND ((Datetime(start_date) BETWEEN Datetime(:weekFirstDate) AND Datetime(:weekLastDate)) OR (Datetime(end_date) BETWEEN Datetime(:weekFirstDate) AND Datetime(:weekLastDate)))")
    List<ScheduleDTO> selectSchedules(int category, Date weekFirstDate, Date weekLastDate);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateSchedule(ScheduleDTO scheduleDTO);

    @Query("DELETE FROM TB_SCHEDULE WHERE id = :id")
    void deleteSchedule(int id);
}
