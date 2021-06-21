package com.zerodsoft.scheduleweather.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.zerodsoft.scheduleweather.room.dto.SelectedCalendarDTO;

import java.util.List;

@Dao
public interface SelectedCalendarDAO {
	@Insert
	int add(SelectedCalendarDTO selectedCalendarDTO);

	@Query("SELECT * FROM selected_calendars_table")
	List<SelectedCalendarDTO> getSelectedCalendarList();

	@Query("SELECT * FROM selected_calendars_table WHERE id = :id")
	SelectedCalendarDTO get(Integer id);

	@Delete
	void delete(SelectedCalendarDTO selectedCalendarDTO);

}
