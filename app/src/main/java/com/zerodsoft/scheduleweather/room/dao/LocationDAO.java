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
    public void insert(LocationDTO locationDTO);

    @Query("SELECT * FROM location_table WHERE calendar_id = :calendarId AND event_id = :eventId AND account_name = :accountName")
    public LocationDTO select(int calendarId, int eventId, String accountName);

    @Update(entity = LocationDTO.class, onConflict = OnConflictStrategy.IGNORE)
    public void update(LocationDTO locationDTO);

    @Delete(entity = LocationDTO.class)
    public void delete(LocationDTO locationDTO);
}
