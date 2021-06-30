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
	private final ContentValues NEW_EVENT = new ContentValues();
	private final List<ContentValues> REMINDERS = new ArrayList<>();
	private final List<ContentValues> ATTENDEES = new ArrayList<>();

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
		NEW_EVENT.put(CalendarContract.Events.TITLE, title);
	}

	@Override
	public void setEventColor(@NonNull Integer color, @NonNull String colorKey) {
		NEW_EVENT.put(CalendarContract.Events.EVENT_COLOR, color);
		NEW_EVENT.put(CalendarContract.Events.EVENT_COLOR_KEY, colorKey);
	}

	@Override
	public void setCalendar(@NonNull Integer calendarId) {
		NEW_EVENT.put(CalendarContract.Events.CALENDAR_ID, calendarId);
	}

	@Override
	public void setIsAllDay(@NonNull Boolean isAllDay) {
		NEW_EVENT.put(CalendarContract.Events.ALL_DAY, isAllDay ? 1 : 0);
	}

	@Override
	public void setDtStart(@NonNull Date date) {
		NEW_EVENT.put(CalendarContract.Events.DTSTART, date.getTime());
	}

	@Override
	public void setDtEnd(@NonNull Date date) {
		NEW_EVENT.put(CalendarContract.Events.DTEND, date.getTime());
	}

	@Override
	public void setTimezone(@NonNull String timezone) {
		NEW_EVENT.put(CalendarContract.Events.EVENT_TIMEZONE, timezone);
	}

	@Override
	public void setRecurrence(@NonNull String rRule) {
		NEW_EVENT.put(CalendarContract.Events.RRULE, rRule);
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
		NEW_EVENT.put(CalendarContract.Events.DESCRIPTION, description);
	}

	@Override
	public void setEventLocation(@NonNull String eventLocation) {
		NEW_EVENT.put(CalendarContract.Events.EVENT_LOCATION, eventLocation);
	}

	@Override
	public void setAttendees(@NonNull List<ContentValues> attendeeList, @NonNull Boolean guestsCanModify, @NonNull Boolean guestsCanInviteOthers,
	                         @NonNull Boolean guestsCanSeeGuests) {
		ATTENDEES.clear();
		ATTENDEES.addAll(attendeeList);

		if (ATTENDEES.isEmpty()) {
			NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_MODIFY, 0);
			NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, 0);
			NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, 0);
		} else {
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
			NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_MODIFY, 0);
			NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, 0);
			NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, 0);
		}
	}

	public void clearAttendees() {
		ATTENDEES.clear();
		NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_MODIFY, 0);
		NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, 0);
		NEW_EVENT.put(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, 0);
	}

	@Override
	public void setAccessLevel(@NonNull Integer accessLevel) {
		NEW_EVENT.put(CalendarContract.Events.ACCESS_LEVEL, accessLevel);
	}

	@Override
	public void setAvailability(@NonNull Integer availability) {
		NEW_EVENT.put(CalendarContract.Events.AVAILABILITY, availability);
	}

}
