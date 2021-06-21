package com.zerodsoft.scheduleweather.calendar.selectedcalendar;

import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.room.dto.SelectedCalendarDTO;

import javax.annotation.Nonnull;

public interface SelectedCalendarQuery {
	void add(SelectedCalendarDTO selectedCalendarDTO);

	void getSelectedCalendarList();

	void delete(Integer calendarId);

	void delete(@Nonnull DbQueryCallback<Boolean> callback, Integer... calendarId);
}
