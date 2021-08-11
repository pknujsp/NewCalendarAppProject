package com.zerodsoft.scheduleweather.activity.editevent.activity;

import android.content.ContentValues;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;

import androidx.annotation.NonNull;

import com.zerodsoft.scheduleweather.calendar.dto.DateTimeObj;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

public class EventModel implements IEventDataViewModel {
	private final ContentValues NEW_EVENT = new ContentValues();
	private final List<ContentValues> NEW_REMINDERS = new ArrayList<>();
	private final List<ContentValues> NEW_ATTENDEES = new ArrayList<>();
	private final Set<String> modifiedValueSet = new HashSet<>();
	private boolean isModifiedAttendees = false;
	private boolean isModifiedReminders = false;

	private DateTimeObj beginDateTimeObj = new DateTimeObj();
	private DateTimeObj endDateTimeObj = new DateTimeObj();
	private TimeZone eventTimeZone;
	private TimeZone calendarTimeZone;

	public ContentValues getNEW_EVENT() {
		return NEW_EVENT;
	}

	public List<ContentValues> getNEW_ATTENDEES() {
		return NEW_ATTENDEES;
	}

	public List<ContentValues> getNEW_REMINDERS() {
		return NEW_REMINDERS;
	}

	public boolean isModifiedAttendees() {
		return isModifiedAttendees;
	}

	public boolean isModifiedReminders() {
		return isModifiedReminders;
	}

	public boolean isModified(String key) {
		return modifiedValueSet.contains(key);
	}

	public DateTimeObj getBeginDateTimeObj() {
		return beginDateTimeObj;
	}

	public DateTimeObj getEndDateTimeObj() {
		return endDateTimeObj;
	}

	public TimeZone getCalendarTimeZone() {
		return calendarTimeZone;
	}

	public TimeZone getEventTimeZone() {
		return eventTimeZone;
	}

	public void setCalendarTimeZone(TimeZone calendarTimeZone) {
		this.calendarTimeZone = calendarTimeZone;
	}

	public void setEventTimeZone(TimeZone eventTimeZone) {
		this.eventTimeZone = eventTimeZone;
	}

	@Override
	public void setTitle(@NonNull String title) {
		if (title.isEmpty()) {
			NEW_EVENT.put(Events.TITLE, (String) null);
		} else {
			NEW_EVENT.put(Events.TITLE, title);
		}
		modifiedValueSet.add(Events.TITLE);
	}

	@Override
	public void setEventColor(@NonNull Integer color, @NonNull String colorKey) {
		modifiedValueSet.add(Events.EVENT_COLOR);
		modifiedValueSet.add(Events.EVENT_COLOR_KEY);
		NEW_EVENT.put(Events.EVENT_COLOR, color);
		NEW_EVENT.put(Events.EVENT_COLOR_KEY, colorKey);
	}

	@Override
	public void setCalendar(@NonNull Integer calendarId) {
		modifiedValueSet.add(Events.CALENDAR_ID);
		NEW_EVENT.put(Events.CALENDAR_ID, calendarId);
	}

	@Override
	public void setIsAllDay(@NonNull Boolean isAllDay) {
		modifiedValueSet.add(Events.ALL_DAY);
		NEW_EVENT.put(Events.ALL_DAY, isAllDay ? 1 : 0);
	}
	

	@Override
	public void setTimezone(@NonNull String timeZoneId) {
		modifiedValueSet.add(Events.EVENT_TIMEZONE);
		NEW_EVENT.put(Events.EVENT_TIMEZONE, timeZoneId);
	}

	@Override
	public void setRecurrence(@NonNull String rRule) {
		if (rRule.isEmpty()) {
			NEW_EVENT.put(Events.RRULE, (String) null);
		} else {
			NEW_EVENT.put(CalendarContract.Events.RRULE, rRule);
		}
		modifiedValueSet.add(Events.RRULE);
	}

	@Override
	public boolean addReminder(@NonNull Integer minutes, @NonNull Integer method) {
		for (ContentValues contentValues : NEW_REMINDERS) {
			if (contentValues.getAsInteger(CalendarContract.Reminders.MINUTES).equals(minutes)) {
				return false;
			}
		}
		ContentValues reminderValues = new ContentValues();
		reminderValues.put(CalendarContract.Reminders.MINUTES, minutes);
		reminderValues.put(CalendarContract.Reminders.METHOD, method);

		NEW_REMINDERS.add(reminderValues);
		isModifiedReminders = true;
		return true;
	}

	@Override
	public void modifyReminder(@NonNull Integer previousMinutes, @NonNull Integer newMinutes, @NonNull Integer method) {
		for (ContentValues contentValues : NEW_REMINDERS) {
			if (contentValues.getAsInteger(CalendarContract.Reminders.MINUTES).equals(previousMinutes)) {
				contentValues.put(CalendarContract.Reminders.MINUTES, newMinutes);
				contentValues.put(CalendarContract.Reminders.METHOD, method);

				break;
			}
		}
		isModifiedReminders = true;
	}

	@Override
	public void removeReminder(@NonNull Integer minutes) {
		for (int i = NEW_REMINDERS.size() - 1; i >= 0; i--) {
			if (NEW_REMINDERS.get(i).getAsInteger(CalendarContract.Reminders.MINUTES).equals(minutes)) {
				NEW_REMINDERS.remove(i);
				break;
			}
		}
		isModifiedReminders = true;
	}

	@Override
	public void setDescription(@NonNull String description) {
		if (description.isEmpty()) {
			NEW_EVENT.put(Events.DESCRIPTION, (String) null);
		} else {
			NEW_EVENT.put(CalendarContract.Events.DESCRIPTION, description);
		}
		modifiedValueSet.add(Events.DESCRIPTION);

	}

	@Override
	public void setEventLocation(@NonNull String eventLocation) {
		if (eventLocation.isEmpty()) {
			NEW_EVENT.put(Events.EVENT_LOCATION, (String) null);
		} else {
			NEW_EVENT.put(CalendarContract.Events.EVENT_LOCATION, eventLocation);
		}
		modifiedValueSet.add(Events.EVENT_LOCATION);

	}

	@Override
	public void setAttendees(@NonNull List<ContentValues> attendeeList, @NonNull Boolean guestsCanModify, @NonNull Boolean guestsCanInviteOthers,
	                         @NonNull Boolean guestsCanSeeGuests) {
		NEW_ATTENDEES.clear();
		NEW_ATTENDEES.addAll(attendeeList);

		if (NEW_ATTENDEES.isEmpty()) {
			NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_MODIFY, 0);
			NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, 0);
			NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, 0);
		} else {
			NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_MODIFY, guestsCanModify ? 1 : 0);
			NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, guestsCanInviteOthers ? 1 : 0);
			NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, guestsCanSeeGuests ? 1 : 0);
		}

		modifiedValueSet.add(Events.GUESTS_CAN_MODIFY);
		modifiedValueSet.add(Events.GUESTS_CAN_INVITE_OTHERS);
		modifiedValueSet.add(Events.GUESTS_CAN_SEE_GUESTS);

		isModifiedAttendees = true;
	}

	@Override
	public void removeAttendee(@NonNull String attendeeEmail) {
		for (int i = NEW_ATTENDEES.size() - 1; i >= 0; i--) {
			if (NEW_ATTENDEES.get(i).getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL).equals(attendeeEmail)) {
				NEW_ATTENDEES.remove(i);
				break;
			}
		}
		if (NEW_ATTENDEES.isEmpty()) {
			NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_MODIFY, 0);
			NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, 0);
			NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, 0);

			modifiedValueSet.add(Events.GUESTS_CAN_MODIFY);
			modifiedValueSet.add(Events.GUESTS_CAN_INVITE_OTHERS);
			modifiedValueSet.add(Events.GUESTS_CAN_SEE_GUESTS);
		}
		isModifiedAttendees = true;
	}

	public void clearAttendees() {
		NEW_ATTENDEES.clear();

		modifiedValueSet.add(Events.GUESTS_CAN_MODIFY);
		modifiedValueSet.add(Events.GUESTS_CAN_INVITE_OTHERS);
		modifiedValueSet.add(Events.GUESTS_CAN_SEE_GUESTS);

		NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_MODIFY, 0);
		NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, 0);
		NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, 0);

		isModifiedAttendees = true;
	}

	@Override
	public void setAccessLevel(@NonNull Integer accessLevel) {
		modifiedValueSet.add(Events.ACCESS_LEVEL);

		NEW_EVENT.put(Events.ACCESS_LEVEL, accessLevel);
	}

	@Override
	public void setAvailability(@NonNull Integer availability) {
		modifiedValueSet.add(Events.AVAILABILITY);
		NEW_EVENT.put(Events.AVAILABILITY, availability);
	}

	public Set<String> getModifiedValueSet() {
		return modifiedValueSet;
	}

	public void setBeginDateTimeObj(DateTimeObj beginDateTimeObj) {
		this.beginDateTimeObj = beginDateTimeObj;
	}

	public void setEndDateTimeObj(DateTimeObj endDateTimeObj) {
		this.endDateTimeObj = endDateTimeObj;
	}
}
