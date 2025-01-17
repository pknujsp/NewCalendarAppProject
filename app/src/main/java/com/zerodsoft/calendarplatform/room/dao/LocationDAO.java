package com.zerodsoft.calendarplatform.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.zerodsoft.calendarplatform.room.dto.LocationDTO;

@Dao
public interface LocationDAO {
	@Insert(entity = LocationDTO.class)
	public long insert(LocationDTO locationDTO);

	@Query("SELECT * FROM location_table WHERE event_id = :eventId")
	public LocationDTO select(long eventId);

	@Query("SELECT * FROM location_table WHERE id = :id")
	public LocationDTO getLocation(int id);

//select EXISTS (select * from 테이블이름 where 컬럼=찾는 값) as success;

	@Query("SELECT EXISTS (SELECT * FROM location_table WHERE event_id = :eventId) AS SUCCESS")
	public int hasLocation(long eventId);

	@Update(entity = LocationDTO.class, onConflict = OnConflictStrategy.IGNORE)
	public void update(LocationDTO locationDTO);

	@Delete(entity = LocationDTO.class)
	public void delete(LocationDTO locationDTO);

	@Query("DELETE FROM location_table WHERE event_id = :eventId")
	public void delete(long eventId);
}
