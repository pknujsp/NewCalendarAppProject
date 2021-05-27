package com.zerodsoft.scheduleweather.activity.editevent.activity;

import android.content.ContentValues;

import java.util.Date;
import java.util.List;

public interface IEventDataViewModel {
	void setTitle(String title);

	void setEventColor(Integer color, String colorKey);

	void setCalendar(Integer calendarId);

	void setIsAllDay(Boolean isAllDay);

	void setDtStart(Date date);

	void setDtEnd(Date date);

	void setTimezone(String timezone);

	void setRecurrence(String rRule);

	boolean addReminder(Integer minutes, Integer method);

	void modifyReminder(Integer previousMinutes, Integer newMinutes, Integer method);

	void removeReminder(Integer minutes);

	void setDescription(String description);

	void setEventLocation(String eventLocation);

	/**
	 * ATTENDEE_EMAIL, ATTENDEE_RELATIONSHIP, ATTENDEE_TYPE, ATTENDEE_STATUS 필수
	 * EVENT_ID는 새로운 이벤트 추가후 반환 받은 ID를 추가하거나, 수정할 이벤트의 ID값을 지정
	 *
	 * @param attendeeList
	 */
	void setAttendees(List<ContentValues> attendeeList, Boolean guestsCanModify, Boolean guestsCanInviteOthers, Boolean guestsCanSeeGuests);

	void removeAttendee(String attendeeEmail);

	void setAccessLevel(Integer accessLevel);

	void setAvailability(Integer availability);
}
