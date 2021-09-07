package com.zerodsoft.calendarplatform.activity.editevent.activity;

import android.content.ContentValues;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public interface IEventDataViewModel {
	void setTitle(@NonNull String title);

	void setEventColor(@Nullable Integer color, @Nullable String colorKey);

	void setCalendar(@NonNull Integer calendarId);

	void setIsAllDay(@NonNull Boolean isAllDay);

	void setTimezone(@NonNull String timeZoneId);

	boolean addReminder(@NonNull Integer minutes, @NonNull Integer method);

	void modifyReminder(@NonNull Integer previousMinutes, @NonNull Integer newMinutes, @NonNull Integer method);

	void removeReminder(@NonNull Integer minutes);

	void setDescription(@NonNull String description);

	void setEventLocation(@NonNull String eventLocation);

	/**
	 * ATTENDEE_EMAIL, ATTENDEE_RELATIONSHIP, ATTENDEE_TYPE, ATTENDEE_STATUS 필수
	 * EVENT_ID는 새로운 이벤트 추가후 반환 받은 ID를 추가하거나, 수정할 이벤트의 ID값을 지정
	 *
	 * @param attendeeList
	 */
	void setAttendees(@NonNull List<ContentValues> attendeeList, @NonNull Boolean guestsCanModify, @NonNull Boolean guestsCanInviteOthers,
	                  @NonNull Boolean guestsCanSeeGuests);

	void removeAttendee(@NonNull String attendeeEmail);

	void setAccessLevel(@NonNull Integer accessLevel);

	void setAvailability(@NonNull Integer availability);
}
