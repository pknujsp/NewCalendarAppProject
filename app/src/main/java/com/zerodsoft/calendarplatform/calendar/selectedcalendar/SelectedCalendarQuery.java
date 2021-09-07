package com.zerodsoft.calendarplatform.calendar.selectedcalendar;

import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.room.dto.SelectedCalendarDTO;

import javax.annotation.Nonnull;

public interface SelectedCalendarQuery {
	void add(SelectedCalendarDTO selectedCalendarDTO);

	void getSelectedCalendarList();

	void delete(Integer calendarId);

	void delete(@Nonnull DbQueryCallback<Boolean> callback, Integer... calendarId);
}
