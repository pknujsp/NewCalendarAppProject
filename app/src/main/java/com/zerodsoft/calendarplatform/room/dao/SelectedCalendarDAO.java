package com.zerodsoft.calendarplatform.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.zerodsoft.calendarplatform.room.dto.SelectedCalendarDTO;

import java.util.List;

@Dao
public interface SelectedCalendarDAO {
	@Insert(onConflict = OnConflictStrategy.IGNORE, entity = SelectedCalendarDTO.class)
	long add(SelectedCalendarDTO selectedCalendarDTO);

	@Query("SELECT * FROM selected_calendars_table")
	List<SelectedCalendarDTO> getSelectedCalendarList();

	@Query("SELECT * FROM selected_calendars_table WHERE id = :id")
	SelectedCalendarDTO get(Integer id);

	@Query("DELETE FROM selected_calendars_table WHERE calendar_id = :calendarId")
	void delete(Integer calendarId);

}
