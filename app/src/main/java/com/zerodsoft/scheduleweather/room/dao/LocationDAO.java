package com.zerodsoft.scheduleweather.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

@Dao
public interface LocationDAO
{
    @Insert(entity = LocationDTO.class)
    public long insert(LocationDTO locationDTO);

    @Query("SELECT * FROM location_table WHERE calendar_id = :calendarId AND event_id = :eventId")
    public LocationDTO select(int calendarId, long eventId);

//select EXISTS (select * from 테이블이름 where 컬럼=찾는 값) as success;

    @Query("SELECT EXISTS (SELECT * FROM location_table WHERE calendar_id = :calendarId AND event_id = :eventId) AS SUCCESS")
    public int hasLocation(int calendarId, long eventId);

    @Update(entity = LocationDTO.class, onConflict = OnConflictStrategy.IGNORE)
    public void update(LocationDTO locationDTO);

    @Delete(entity = LocationDTO.class)
    public void delete(LocationDTO locationDTO);

    @Query("DELETE FROM location_table WHERE calendar_id = :calendarId AND event_id = :eventId")
    public void delete(int calendarId, long eventId);
}
