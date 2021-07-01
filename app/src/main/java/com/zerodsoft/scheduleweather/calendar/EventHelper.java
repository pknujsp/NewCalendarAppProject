package com.zerodsoft.scheduleweather.calendar;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.provider.CalendarContract.Reminders;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Calendars;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;

import com.zerodsoft.scheduleweather.activity.editevent.activity.ModifyInstanceFragment;
import com.zerodsoft.scheduleweather.calendar.calendarcommon2.*;
import com.zerodsoft.scheduleweather.common.enums.EventIntentCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import biweekly.property.Attendee;

public class EventHelper {
	private final AsyncQueryService mService;

	public EventHelper(AsyncQueryService asyncQueryService) {
		mService = asyncQueryService;
	}

	public void saveFollowingEvents(ContentValues originalEvent, ContentValues newEvent,
	                                List<ContentValues> originalReminderList,
	                                List<ContentValues> originalAttendeeList,
	                                List<ContentValues> newReminderList,
	                                List<ContentValues> newAttendeeList,
	                                ContentValues selectedCalendar) {
		if (newEvent.size() == 0) {
			return;
		}

		if (originalEvent != null && !isSameEvent(originalEvent, newEvent)) {
			return;
		}

		if (originalEvent != null && isUnchanged(newEvent)) {
			return;
		}

		int eventIdIndex = -1;
		Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI
				, originalEvent.getAsLong(Instances.EVENT_ID));

		newEvent.remove(Events._ID);

		ArrayList<ContentProviderOperation> contentProviderOperationList
				= new ArrayList<>();

		boolean hasRRuleInNewEvent = false;
		if (newEvent.containsKey(Events.RRULE)) {
			hasRRuleInNewEvent = true;
		}

		if (!hasRRuleInNewEvent) {
			if (isFirstInstance(originalEvent, newEvent)) {
				contentProviderOperationList.add(
						ContentProviderOperation.newDelete(uri).build());
			} else {
				updatePastEvents(contentProviderOperationList,
						originalEvent, newEvent);
			}

			eventIdIndex = contentProviderOperationList.size();
			newEvent.put(Events.STATUS, originalEvent.getAsInteger(Events.STATUS));
			contentProviderOperationList.add(ContentProviderOperation.newInsert(Events.CONTENT_URI).withValues(newEvent)
					.build());
		} else {
			if (isFirstInstance(originalEvent, newEvent)) {
				checkTimeDependentFields(originalEvent, newEvent);

				ContentProviderOperation.Builder b = ContentProviderOperation.newUpdate(uri)
						.withValues(newEvent);
				contentProviderOperationList.add(b.build());
			} else {
				String newRRule = updatePastEvents(contentProviderOperationList, originalEvent, newEvent);
				if (newEvent.getAsString(Events.RRULE).equals(originalEvent.getAsString(Events.RRULE))) {
					newEvent.put(Events.RRULE, newRRule);
				}
				eventIdIndex = contentProviderOperationList.size();
				newEvent.put(Events.STATUS, originalEvent.getAsInteger(Events.STATUS));
				contentProviderOperationList.add(ContentProviderOperation.newInsert(Events.CONTENT_URI).withValues(newEvent)
						.build());
			}

		}

		//reminders
		saveRemindersWithBackRef(contentProviderOperationList, eventIdIndex, originalReminderList,
				newReminderList);

		//attendees
		ContentValues values = new ContentValues();
		ContentProviderOperation.Builder builder;
		final boolean hasAttendeeData = newAttendeeList.size() != 0;
		Long ownerAttendeeId = null;

		if (newEvent.getAsString(Events.IS_ORGANIZER).equals("1")) {
			ownerAttendeeId = newEvent.getAsLong(Instances.EVENT_ID);
		}

		if (hasAttendeeData &&
				newEvent.getAsInteger(Events.SELF_ATTENDEE_STATUS) != originalEvent.getAsInteger(Events.SELF_ATTENDEE_STATUS) &&
				ownerAttendeeId != null) {
			Uri attUri = ContentUris.withAppendedId(Attendees.CONTENT_URI, ownerAttendeeId);

			values.clear();
			values.put(Attendees.ATTENDEE_STATUS, newEvent.getAsInteger(Events.SELF_ATTENDEE_STATUS));
			values.put(Attendees.EVENT_ID, ownerAttendeeId);
			builder = ContentProviderOperation.newUpdate(attUri).withValues(values);
			contentProviderOperationList.add(builder.build());
		} else if (hasAttendeeData && ownerAttendeeId == null) {
			String ownerEmail = selectedCalendar.getAsString(Calendars.OWNER_ACCOUNT);

			if (!newAttendeeList.isEmpty()) {
				values.clear();
				values.put(Attendees.ATTENDEE_EMAIL, ownerEmail);
				values.put(Attendees.ATTENDEE_RELATIONSHIP, Attendees.RELATIONSHIP_ORGANIZER);
				values.put(Attendees.ATTENDEE_TYPE, Attendees.TYPE_REQUIRED);
				values.put(Attendees.ATTENDEE_STATUS, Attendees.ATTENDEE_STATUS_ACCEPTED);

				builder = ContentProviderOperation.newInsert(Attendees.CONTENT_URI)
						.withValues(values);
				builder.withValueBackReference(Attendees.EVENT_ID, eventIdIndex);

				contentProviderOperationList.add(builder.build());
			}
		}

		if (hasAttendeeData) {
			Map<String, ContentValues> newAttendees = new HashMap<>();
			for (ContentValues newAttendee : newAttendeeList) {
				newAttendees.put(newAttendee.getAsString(Attendees.ATTENDEE_EMAIL)
						, newAttendee);
			}
			if (!newAttendees.isEmpty()) {
				for (ContentValues attendee : newAttendees.values()) {
					attendee.put(Attendees.ATTENDEE_RELATIONSHIP,
							Attendees.RELATIONSHIP_ATTENDEE);
					attendee.put(Attendees.ATTENDEE_TYPE, Attendees.TYPE_REQUIRED);
					attendee.put(Attendees.ATTENDEE_STATUS, Attendees.ATTENDEE_STATUS_NONE);

					builder = ContentProviderOperation.newInsert(Attendees.CONTENT_URI)
							.withValues(attendee);
					builder.withValueBackReference(Attendees.EVENT_ID, eventIdIndex);

					contentProviderOperationList.add(builder.build());
				}
			}
		}

		mService.startBatch(mService.getNextToken(), null, CalendarContract.AUTHORITY, contentProviderOperationList,
				0);
	}

	private boolean saveRemindersWithBackRef(ArrayList<ContentProviderOperation> contentProviderOperationList, Integer eventIdIndex,
	                                         List<ContentValues> originalReminderList, List<ContentValues> newReminderList) {
		if (originalReminderList.equals(newReminderList)) {
			return false;
		}

		ContentProviderOperation.Builder b = ContentProviderOperation
				.newDelete(Reminders.CONTENT_URI);
		b.withSelection(Reminders.EVENT_ID + "=?", new String[1]);
		b.withSelectionBackReference(0, eventIdIndex);
		contentProviderOperationList.add(b.build());

		for (ContentValues reminder : newReminderList) {
			b = ContentProviderOperation.newInsert(Reminders.CONTENT_URI).withValues(reminder);
			b.withValueBackReference(Reminders.EVENT_ID, eventIdIndex);
			contentProviderOperationList.add(b.build());
		}
		return true;
	}

	private String updatePastEvents(ArrayList<ContentProviderOperation> contentProviderOperationList, ContentValues originalEvent,
	                                ContentValues newEvent) {
		boolean originalAllDay = originalEvent.getAsInteger(Events.ALL_DAY) == 1;
		String originalRRule = originalEvent.getAsString(Events.RRULE);
		String newRRule = originalRRule;

		EventRecurrence originalEventRecurrence = new EventRecurrence();
		originalEventRecurrence.parse(originalEvent.getAsString(Events.RRULE));

		long startTimeMillis = originalEvent.getAsLong(Events.DTSTART);
		Time dtstart = new Time();
		dtstart.timezone = originalEvent.getAsString(Events.EVENT_TIMEZONE);
		dtstart.set(startTimeMillis);

		ContentValues updateValues = new ContentValues();

		if (originalEventRecurrence.count > 0) {
			RecurrenceSet recurSet = new RecurrenceSet(originalRRule, null, null, null);
			RecurrenceProcessor recurProc = new RecurrenceProcessor();
			long[] recurrences;
			try {
				recurrences = recurProc.expand(dtstart, recurSet, startTimeMillis, newEvent.getAsLong(Events.DTSTART));
			} catch (DateException de) {
				throw new RuntimeException(de);
			}

			if (recurrences.length == 0) {
				throw new RuntimeException("can't use this method on first instance");
			}

			EventRecurrence exceptRecurrence = new EventRecurrence();
			exceptRecurrence.parse(originalRRule);
			exceptRecurrence.count -= recurrences.length;
			newRRule = exceptRecurrence.toString();

			originalEventRecurrence.count = recurrences.length;
		} else {
			Time untilTime = new Time();
			untilTime.timezone = Time.TIMEZONE_UTC;

			untilTime.set(newEvent.getAsLong(Events.DTSTART) - 1000);
			if (originalAllDay) {
				untilTime.hour = 0;
				untilTime.minute = 0;
				untilTime.second = 0;
				untilTime.allDay = true;
				untilTime.normalize(false);

				// This should no longer be necessary -- DTSTART should already be in the correct
				// format for an all-day event.
				dtstart.hour = 0;
				dtstart.minute = 0;
				dtstart.second = 0;
				dtstart.allDay = true;
				dtstart.timezone = Time.TIMEZONE_UTC;
			}
			originalEventRecurrence.until = untilTime.format2445();
		}

		updateValues.put(Events.RRULE, originalEventRecurrence.toString());
		updateValues.put(Events.DTSTART, dtstart.normalize(true));

		Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI
				, originalEvent.getAsLong(Instances.EVENT_ID));
		ContentProviderOperation.Builder b =
				ContentProviderOperation.newUpdate(uri)
						.withValues(updateValues);
		contentProviderOperationList.add(b.build());

		return newRRule;
	}

	private void checkTimeDependentFields(ContentValues originalEvent, ContentValues newEvent) {
		final long originalBegin = originalEvent.getAsLong(Instances.BEGIN);
		final long originalEnd = originalEvent.getAsLong(Instances.END);
		final boolean originalAllDay = originalEvent.getAsInteger(Events.ALL_DAY) == 1;
		final String originalRRule = originalEvent.getAsString(Events.RRULE);
		final String originalTimeZone = originalEvent.getAsString(Events.EVENT_TIMEZONE);

		final long newBegin = newEvent.getAsLong(Instances.BEGIN);
		final long newEnd = newEvent.getAsLong(Instances.END);
		final boolean newAllDay = newEvent.getAsInteger(Events.ALL_DAY) == 1;
		final String newRRule = newEvent.getAsString(Events.RRULE);
		final String newTimeZone = newEvent.getAsString(Events.EVENT_TIMEZONE);

		if (originalBegin == newBegin && originalEnd == newEnd &&
				originalAllDay == newAllDay && originalRRule == newRRule
				&& originalTimeZone == newTimeZone) {
			newEvent.remove(Events.DTSTART);
			newEvent.remove(Events.DTEND);
			newEvent.remove(Events.DURATION);
			newEvent.remove(Events.ALL_DAY);
			newEvent.remove(Events.RRULE);
			newEvent.remove(Events.EVENT_TIMEZONE);
		}
	}

	private boolean isFirstInstance(ContentValues originalEvent, ContentValues newEvent) {
		return originalEvent.getAsLong(Events.DTSTART).equals(
				newEvent.getAsLong(Events.DTSTART));
	}

	public boolean isSameEvent(ContentValues originalEvent, ContentValues newEvent) {
		if (!originalEvent.getAsInteger(CalendarContract.Events.CALENDAR_ID)
				.equals(newEvent.getAsInteger(CalendarContract.Events.CALENDAR_ID))) {
			return false;
		} else if (!originalEvent.getAsLong(CalendarContract.Instances.EVENT_ID)
				.equals(newEvent.getAsLong(CalendarContract.Events._ID))) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isUnchanged(ContentValues newEvent) {
		if (newEvent.size() == 0) {
			return true;
		} else {
			return false;
		}
	}
}

