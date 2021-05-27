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
		EVENT.put(CalendarContract.Events.TITLE, title);
	}

	@Override
	public void setEventColor(Integer color, String colorKey) {
		EVENT.put(CalendarContract.Events.EVENT_COLOR, color);
		EVENT.put(CalendarContract.Events.EVENT_COLOR_KEY, colorKey);
	}

	@Override
	public void setCalendar(Integer calendarId) {
		EVENT.put(CalendarContract.Events.CALENDAR_ID, calendarId);
	}

	@Override
	public void setIsAllDay(Boolean isAllDay) {
		EVENT.put(CalendarContract.Events.ALL_DAY, isAllDay ? 1 : 0);
	}

	@Override
	public void setStartDate(Date date) {
		EVENT.put(CalendarContract.Events.DTSTART, date.getTime());
	}

	@Override
	public void setEndDate(Date date) {
		EVENT.put(CalendarContract.Events.DTEND, date.getTime());
	}

	@Override
	public void setStartTime(Date date) {
		EVENT.put(CalendarContract.Events.DTSTART, date.getTime());
	}

	@Override
	public void setEndTime(Date date) {
		EVENT.put(CalendarContract.Events.DTEND, date.getTime());
	}

	@Override
	public void setTimezone(String timezone) {
		EVENT.put(CalendarContract.Events.EVENT_TIMEZONE, timezone);
	}

	@Override
	public void setRecurrence(String rRule) {
		EVENT.put(CalendarContract.Events.RRULE, rRule);
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
		EVENT.put(CalendarContract.Events.DESCRIPTION, description);
	}

	@Override
	public void setEventLocation(String eventLocation) {
		EVENT.put(CalendarContract.Events.EVENT_LOCATION, eventLocation);
	}

	@Override
	public void setAttendees(List<ContentValues> attendeeList, Boolean guestsCanModify, Boolean guestsCanInviteOthers, Boolean guestsCanSeeGuests) {
		EVENT.put(CalendarContract.Events.GUESTS_CAN_MODIFY, guestsCanModify ? 1 : 0);
		EVENT.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, guestsCanInviteOthers ? 1 : 0);
		EVENT.put(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, guestsCanSeeGuests ? 1 : 0);
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
		EVENT.put(CalendarContract.Events.ACCESS_LEVEL, accessLevel);
	}

	@Override
	public void setAvailability(Integer availability) {
		EVENT.put(CalendarContract.Events.AVAILABILITY, availability);
	}
}
