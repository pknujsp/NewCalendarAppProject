package com.zerodsoft.scheduleweather.activity.editevent.activity;

import android.app.Application;
import android.content.ContentValues;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventDataViewModel extends AndroidViewModel implements IEventDataViewModel {
	private final ContentValues NEW_EVENT = new ContentValues();
	private final List<ContentValues> REMINDERS = new ArrayList<>();
	private final List<ContentValues> ATTENDEES = new ArrayList<>();
	private final Set<String> removedValueSet = new HashSet<>();
	private final Set<String> addedValueSet = new HashSet<>();

	public EventDataViewModel(@NonNull @NotNull Application application) {
		super(application);
	}

	public ContentValues getNEW_EVENT() {
		return NEW_EVENT;
	}

	public List<ContentValues> getATTENDEES() {
		return ATTENDEES;
	}

	public List<ContentValues> getREMINDERS() {
		return REMINDERS;
	}

	@Override
	public void setTitle(@NonNull String title) {
		if (title.isEmpty()) {
			NEW_EVENT.remove(Events.TITLE);

			removedValueSet.add(Events.TITLE);
			addedValueSet.remove(Events.TITLE);
		} else {
			NEW_EVENT.put(Events.TITLE, title);

			removedValueSet.remove(Events.TITLE);
			addedValueSet.add(Events.TITLE);
		}
	}

	@Override
	public void setEventColor(@NonNull Integer color, @NonNull String colorKey) {
		addedValueSet.add(Events.EVENT_COLOR);
		addedValueSet.add(Events.EVENT_COLOR_KEY);

		NEW_EVENT.put(Events.EVENT_COLOR, color);
		NEW_EVENT.put(Events.EVENT_COLOR_KEY, colorKey);
	}

	@Override
	public void setCalendar(@NonNull Integer calendarId) {
		addedValueSet.add(Events.CALENDAR_ID);

		NEW_EVENT.put(Events.CALENDAR_ID, calendarId);
	}

	@Override
	public void setIsAllDay(@NonNull Boolean isAllDay) {
		addedValueSet.add(Events.ALL_DAY);

		NEW_EVENT.put(Events.ALL_DAY, isAllDay ? 1 : 0);
	}

	@Override
	public void setDtStart(@NonNull Date date) {
		addedValueSet.add(Events.DTSTART);

		NEW_EVENT.put(Events.DTSTART, date.getTime());
	}

	@Override
	public void setDtEnd(@NonNull Date date) {
		addedValueSet.add(Events.DTEND);

		NEW_EVENT.put(Events.DTEND, date.getTime());
	}

	@Override
	public void setTimezone(@NonNull String timezone) {
		addedValueSet.add(Events.EVENT_TIMEZONE);

		NEW_EVENT.put(Events.EVENT_TIMEZONE, timezone);
	}

	@Override
	public void setRecurrence(@NonNull String rRule) {
		if (rRule.isEmpty()) {
			NEW_EVENT.remove(Events.RRULE);

			removedValueSet.add(Events.RRULE);
			addedValueSet.remove(Events.RRULE);
		} else {
			NEW_EVENT.put(CalendarContract.Events.RRULE, rRule);

			removedValueSet.remove(Events.RRULE);
			addedValueSet.add(Events.RRULE);
		}
	}

	@Override
	public boolean addReminder(@NonNull Integer minutes, @NonNull Integer method) {
		for (ContentValues contentValues : REMINDERS) {
			if (contentValues.getAsInteger(CalendarContract.Reminders.MINUTES).equals(minutes)) {
				return false;
			}
		}
		ContentValues reminderValues = new ContentValues();
		reminderValues.put(CalendarContract.Reminders.MINUTES, minutes);
		reminderValues.put(CalendarContract.Reminders.METHOD, method);

		REMINDERS.add(reminderValues);
		return true;
	}

	@Override
	public void modifyReminder(@NonNull Integer previousMinutes, @NonNull Integer newMinutes, @NonNull Integer method) {
		for (ContentValues contentValues : REMINDERS) {
			if (contentValues.getAsInteger(CalendarContract.Reminders.MINUTES).equals(previousMinutes)) {
				contentValues.put(CalendarContract.Reminders.MINUTES, newMinutes);
				contentValues.put(CalendarContract.Reminders.METHOD, method);

				break;
			}
		}
	}

	@Override
	public void removeReminder(@NonNull Integer minutes) {
		for (int i = REMINDERS.size() - 1; i >= 0; i--) {
			if (REMINDERS.get(i).getAsInteger(CalendarContract.Reminders.MINUTES).equals(minutes)) {
				REMINDERS.remove(i);
				break;
			}
		}
	}

	@Override
	public void setDescription(@NonNull String description) {
		if (description.isEmpty()) {
			NEW_EVENT.remove(Events.DESCRIPTION);

			removedValueSet.add(Events.DESCRIPTION);
			addedValueSet.remove(Events.DESCRIPTION);
		} else {
			NEW_EVENT.put(CalendarContract.Events.DESCRIPTION, description);

			removedValueSet.remove(Events.DESCRIPTION);
			addedValueSet.add(Events.DESCRIPTION);
		}
	}

	@Override
	public void setEventLocation(@NonNull String eventLocation) {
		if (eventLocation.isEmpty()) {
			NEW_EVENT.remove(Events.EVENT_LOCATION);

			removedValueSet.add(Events.EVENT_LOCATION);
			addedValueSet.remove(Events.EVENT_LOCATION);
		} else {
			NEW_EVENT.put(CalendarContract.Events.EVENT_LOCATION, eventLocation);

			removedValueSet.remove(Events.EVENT_LOCATION);
			addedValueSet.add(Events.EVENT_LOCATION);
		}
	}

	@Override
	public void setAttendees(@NonNull List<ContentValues> attendeeList, @NonNull Boolean guestsCanModify, @NonNull Boolean guestsCanInviteOthers,
	                         @NonNull Boolean guestsCanSeeGuests) {
		ATTENDEES.clear();
		ATTENDEES.addAll(attendeeList);

		if (ATTENDEES.isEmpty()) {
			removedValueSet.add(Events.GUESTS_CAN_MODIFY);
			removedValueSet.add(Events.GUESTS_CAN_INVITE_OTHERS);
			removedValueSet.add(Events.GUESTS_CAN_SEE_GUESTS);

			addedValueSet.remove(Events.GUESTS_CAN_MODIFY);
			addedValueSet.remove(Events.GUESTS_CAN_INVITE_OTHERS);
			addedValueSet.remove(Events.GUESTS_CAN_SEE_GUESTS);

			NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_MODIFY, 0);
			NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, 0);
			NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, 0);
		} else {
			removedValueSet.remove(Events.GUESTS_CAN_MODIFY);
			removedValueSet.remove(Events.GUESTS_CAN_INVITE_OTHERS);
			removedValueSet.remove(Events.GUESTS_CAN_SEE_GUESTS);

			addedValueSet.add(Events.GUESTS_CAN_MODIFY);
			addedValueSet.add(Events.GUESTS_CAN_INVITE_OTHERS);
			addedValueSet.add(Events.GUESTS_CAN_SEE_GUESTS);

			NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_MODIFY, guestsCanModify ? 1 : 0);
			NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, guestsCanInviteOthers ? 1 : 0);
			NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, guestsCanSeeGuests ? 1 : 0);
		}
	}

	@Override
	public void removeAttendee(@NonNull String attendeeEmail) {
		for (int i = ATTENDEES.size() - 1; i >= 0; i--) {
			if (ATTENDEES.get(i).getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL).equals(attendeeEmail)) {
				ATTENDEES.remove(i);
				break;
			}
		}
		if (ATTENDEES.isEmpty()) {
			removedValueSet.add(Events.GUESTS_CAN_MODIFY);
			removedValueSet.add(Events.GUESTS_CAN_INVITE_OTHERS);
			removedValueSet.add(Events.GUESTS_CAN_SEE_GUESTS);

			addedValueSet.remove(Events.GUESTS_CAN_MODIFY);
			addedValueSet.remove(Events.GUESTS_CAN_INVITE_OTHERS);
			addedValueSet.remove(Events.GUESTS_CAN_SEE_GUESTS);

			NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_MODIFY, 0);
			NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, 0);
			NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, 0);
		}
	}

	public void clearAttendees() {
		ATTENDEES.clear();

		removedValueSet.add(Events.GUESTS_CAN_MODIFY);
		removedValueSet.add(Events.GUESTS_CAN_INVITE_OTHERS);
		removedValueSet.add(Events.GUESTS_CAN_SEE_GUESTS);

		addedValueSet.remove(Events.GUESTS_CAN_MODIFY);
		addedValueSet.remove(Events.GUESTS_CAN_INVITE_OTHERS);
		addedValueSet.remove(Events.GUESTS_CAN_SEE_GUESTS);

		NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_MODIFY, 0);
		NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, 0);
		NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, 0);
	}

	@Override
	public void setAccessLevel(@NonNull Integer accessLevel) {
		addedValueSet.remove(Events.ACCESS_LEVEL);

		NEW_EVENT.put(Events.ACCESS_LEVEL, accessLevel);
	}

	@Override
	public void setAvailability(@NonNull Integer availability) {
		addedValueSet.remove(Events.AVAILABILITY);

		NEW_EVENT.put(Events.AVAILABILITY, availability);
	}

	public Set<String> getAddedValueSet() {
		return addedValueSet;
	}

	public Set<String> getRemovedValueSet() {
		return removedValueSet;
	}
}
