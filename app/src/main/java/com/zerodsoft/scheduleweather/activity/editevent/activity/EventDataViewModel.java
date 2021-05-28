package com.zerodsoft.scheduleweather.activity.editevent.activity;

import android.app.Application;
import android.content.ContentValues;
import android.provider.CalendarContract;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventDataViewModel extends AndroidViewModel implements IEventDataViewModel {
	private final ContentValues EVENT = new ContentValues();
	private final List<ContentValues> REMINDERS = new ArrayList<>();
	private final List<ContentValues> ATTENDEES = new ArrayList<>();

	public EventDataViewModel(@NonNull @NotNull Application application) {
		super(application);
	}

	public ContentValues getEVENT() {
		return EVENT;
	}

	public List<ContentValues> getATTENDEES() {
		return ATTENDEES;
	}

	public List<ContentValues> getREMINDERS() {
		return REMINDERS;
	}

	@Override
	public void setTitle(String title) {
		putOrRemoveValue(CalendarContract.Events.TITLE, title);
	}

	@Override
	public void setEventColor(Integer color, String colorKey) {
		putOrRemoveValue(CalendarContract.Events.EVENT_COLOR, color);
		putOrRemoveValue(CalendarContract.Events.EVENT_COLOR_KEY, colorKey);
	}

	@Override
	public void setCalendar(Integer calendarId) {
		putOrRemoveValue(CalendarContract.Events.CALENDAR_ID, calendarId);
	}

	@Override
	public void setIsAllDay(Boolean isAllDay) {
		putOrRemoveValue(CalendarContract.Events.ALL_DAY, isAllDay);
	}

	@Override
	public void setDtStart(Date date) {
		putOrRemoveValue(CalendarContract.Events.DTSTART, date.getTime());
	}

	@Override
	public void setDtEnd(Date date) {
		putOrRemoveValue(CalendarContract.Events.DTEND, date.getTime());
	}

	@Override
	public void setTimezone(String timezone) {
		putOrRemoveValue(CalendarContract.Events.EVENT_TIMEZONE, timezone);
	}

	@Override
	public void setRecurrence(String rRule) {
		putOrRemoveValue(CalendarContract.Events.RRULE, rRule);
	}

	@Override
	public boolean addReminder(Integer minutes, Integer method) {
		for (ContentValues contentValues : REMINDERS) {
			if (contentValues.getAsInteger(CalendarContract.Reminders.MINUTES).equals(minutes)) {
				return false;
			}
		}
		ContentValues reminderValues = new ContentValues();
		reminderValues.put(CalendarContract.Reminders.MINUTES, minutes);
		reminderValues.put(CalendarContract.Reminders.METHOD, method);
		REMINDERS.add(reminderValues);
		// putEventValue(CalendarContract.Events.HAS_ALARM, 1);
		return true;
	}

	@Override
	public void modifyReminder(Integer previousMinutes, Integer newMinutes, Integer method) {
		for (ContentValues contentValues : REMINDERS) {
			if (contentValues.getAsInteger(CalendarContract.Reminders.MINUTES).equals(previousMinutes)) {
				contentValues.put(CalendarContract.Reminders.MINUTES, newMinutes);
				contentValues.put(CalendarContract.Reminders.METHOD, method);
				break;
			}
		}
	}

	@Override
	public void removeReminder(Integer minutes) {
		for (int i = REMINDERS.size() - 1; i >= 0; i--) {
			if (REMINDERS.get(i).getAsInteger(CalendarContract.Reminders.MINUTES).equals(minutes)) {
				REMINDERS.remove(i);
				break;
			}
		}
	}

	@Override
	public void setDescription(String description) {
		putOrRemoveValue(CalendarContract.Events.DESCRIPTION, description);
	}

	@Override
	public void setEventLocation(String eventLocation) {
		putOrRemoveValue(CalendarContract.Events.EVENT_LOCATION, eventLocation);
	}

	@Override
	public void setAttendees(List<ContentValues> attendeeList, Boolean guestsCanModify, Boolean guestsCanInviteOthers, Boolean guestsCanSeeGuests) {
		putOrRemoveValue(CalendarContract.Events.GUESTS_CAN_MODIFY, guestsCanModify);
		putOrRemoveValue(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, guestsCanInviteOthers);
		putOrRemoveValue(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, guestsCanSeeGuests);

		ATTENDEES.clear();
		ATTENDEES.addAll(attendeeList);
	}

	@Override
	public void removeAttendee(String attendeeEmail) {
		for (int i = ATTENDEES.size() - 1; i >= 0; i--) {
			if (ATTENDEES.get(i).getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL).equals(attendeeEmail)) {
				ATTENDEES.remove(i);
				break;
			}
		}
	}

	@Override
	public void setAccessLevel(Integer accessLevel) {
		putOrRemoveValue(CalendarContract.Events.ACCESS_LEVEL, accessLevel);
	}

	@Override
	public void setAvailability(Integer availability) {
		putOrRemoveValue(CalendarContract.Events.AVAILABILITY, availability);
	}

	private void putOrRemoveValue(String key, String value) {
		if (value == null) {
			EVENT.remove(key);
		} else {
			EVENT.put(key, value);
		}
	}

	private void putOrRemoveValue(String key, Long value) {
		if (value == null) {
			EVENT.remove(key);
		} else {
			EVENT.put(key, value);
		}
	}

	private void putOrRemoveValue(String key, Integer value) {
		if (value == null) {
			EVENT.remove(key);
		} else {
			EVENT.put(key, value);
		}
	}

	private void putOrRemoveValue(String key, Boolean value) {
		if (value == null) {
			EVENT.remove(key);
		} else {
			EVENT.put(key, value ? 1 : 0);
		}
	}
}
