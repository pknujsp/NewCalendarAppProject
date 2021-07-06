package com.zerodsoft.scheduleweather.calendar;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.provider.CalendarContract.Reminders;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Calendars;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;

import androidx.annotation.Nullable;

import com.zerodsoft.scheduleweather.calendar.calendarcommon2.*;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class EventHelper implements Serializable {
	private final AsyncQueryService mService;

	public enum EventEditType implements Serializable {
		UPDATE_ONLY_THIS_EVENT,
		UPDATE_FOLLOWING_EVENTS,
		UPDATE_ALL_EVENTS,
		SAVE_NEW_EVENT,
		REMOVE_ONLY_THIS_EVENT,
		REMOVE_FOLLOWING_EVENTS,
		REMOVE_ALL_EVENTS
	}

	public EventHelper(AsyncQueryService asyncQueryService) {
		mService = asyncQueryService;
	}

	public void saveNewEvent(ContentValues newEvent, @Nullable LocationDTO locationDto, List<ContentValues> newReminderList,
	                         List<ContentValues> newAttendeeList) {
		//반복 이벤트면 dtEnd를 삭제하고 duration추가
		ArrayList<ContentProviderOperation> contentProviderOperationList
				= new ArrayList<>();
		int eventIdIndex = 0;

		convertDtEndForAllDay(newEvent);
		setDuration(newEvent);
		contentProviderOperationList.add(ContentProviderOperation.newInsert(Events.CONTENT_URI).withValues(newEvent)
				.build());

		if (!newReminderList.isEmpty()) {
			saveRemindersWithBackRef(contentProviderOperationList, eventIdIndex, null,
					newReminderList, true);
		}

		if (!newAttendeeList.isEmpty()) {
			ContentProviderOperation.Builder attendeeOperationBuilder = null;

			for (ContentValues attendee : newAttendeeList) {
				attendee.put(Attendees.ATTENDEE_RELATIONSHIP,
						Attendees.RELATIONSHIP_ATTENDEE);
				attendee.put(Attendees.ATTENDEE_TYPE, Attendees.TYPE_OPTIONAL);
				attendee.put(Attendees.ATTENDEE_STATUS, Attendees.ATTENDEE_STATUS_INVITED);

				attendeeOperationBuilder = ContentProviderOperation.newInsert(Attendees.CONTENT_URI)
						.withValues(attendee);
				attendeeOperationBuilder.withValueBackReference(Attendees.EVENT_ID, eventIdIndex);
				contentProviderOperationList.add(attendeeOperationBuilder.build());
			}

		}

		/*
		if (newEvent.containsKey(CalendarContract.Events.EVENT_LOCATION)) {
			locationDto.setEventId(newEventId);
			locationViewModel.addLocation(locationDTO, new DbQueryCallback<LocationDTO>() {
				@Override
				public void onResultSuccessful(LocationDTO result) {

				}

				@Override
				public void onResultNoData() {

				}
			});
		} else {

		}

		 */
		UpdatedEventPrimaryValues updatedEventPrimaryValues = new UpdatedEventPrimaryValues();
		updatedEventPrimaryValues.setBegin(newEvent.getAsLong(Events.DTSTART));
		updatedEventPrimaryValues.setEventEditType(EventEditType.SAVE_NEW_EVENT);

		mService.startBatch(mService.getNextToken(), null, CalendarContract.AUTHORITY, contentProviderOperationList,
				0, updatedEventPrimaryValues);
	}


	public void updateEvent(EventEditType eventEditType
			, ContentValues originalEvent, ContentValues newEvent,
			                List<ContentValues> originalReminderList,
			                List<ContentValues> originalAttendeeList,
			                List<ContentValues> newReminderList,
			                List<ContentValues> newAttendeeList,
			                ContentValues selectedCalendar) {
		if (newEvent.size() == 0) {
			return;
		} else if (isUnchanged(newEvent)) {
			return;
		}

		Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI
				, originalEvent.getAsLong(Instances.EVENT_ID));
		ArrayList<ContentProviderOperation> contentProviderOperationList
				= new ArrayList<>();
		int eventIdIndex = -1;
		boolean forceSaveReminders = false;

		convertDtEndForAllDay(newEvent);

		if (eventEditType == EventEditType.UPDATE_ONLY_THIS_EVENT) {
			ContentValues values = new ContentValues();
			values.put(Events.ORIGINAL_INSTANCE_TIME, originalEvent.getAsLong(Instances.BEGIN));
			values.put(Events.STATUS, Events.STATUS_CANCELED);

			Uri exceptionUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_EXCEPTION_URI,
					originalEvent.getAsLong(CalendarContract.Instances.EVENT_ID));
			contentProviderOperationList.add(ContentProviderOperation.newInsert(exceptionUri).withValues(values)
					.build());

			eventIdIndex = contentProviderOperationList.size();
			newEvent.put(Events.STATUS, originalEvent.getAsInteger(Events.STATUS));
			setDuration(newEvent);
			contentProviderOperationList.add(ContentProviderOperation.newInsert(Events.CONTENT_URI).withValues(newEvent)
					.build());
			forceSaveReminders = true;
		} else if (eventEditType == EventEditType.UPDATE_FOLLOWING_EVENTS) {
			newEvent.remove(Events._ID);

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
				setDuration(newEvent);

				contentProviderOperationList.add(ContentProviderOperation.newInsert(Events.CONTENT_URI).withValues(newEvent)
						.build());
			} else {
				if (isFirstInstance(originalEvent, newEvent)) {
					checkTimeDependentFields(originalEvent, newEvent, eventEditType);
					setDuration(newEvent);

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
					setDuration(newEvent);

					contentProviderOperationList.add(ContentProviderOperation.newInsert(Events.CONTENT_URI).withValues(newEvent)
							.build());
				}
			}

			forceSaveReminders = true;
		} else if (eventEditType == EventEditType.UPDATE_ALL_EVENTS) {
			boolean hasRRuleInNewEvent = false;
			if (newEvent.containsKey(Events.RRULE)) {
				hasRRuleInNewEvent = true;
			}

			if (!hasRRuleInNewEvent) {
				contentProviderOperationList.add(ContentProviderOperation.newDelete(uri).build());

				eventIdIndex = contentProviderOperationList.size();
				setDuration(newEvent);
				contentProviderOperationList.add(ContentProviderOperation.newInsert(Events.CONTENT_URI).withValues(newEvent)
						.build());
				forceSaveReminders = true;
			} else {
				checkTimeDependentFields(originalEvent, newEvent, eventEditType);
				contentProviderOperationList.add(ContentProviderOperation.newUpdate(uri).withValues(newEvent).build());
			}
		}

		final boolean isNewEvent = eventIdIndex != -1;

		//reminders
		if (isNewEvent) {
			saveRemindersWithBackRef(contentProviderOperationList, eventIdIndex, originalReminderList,
					newReminderList, forceSaveReminders);
		} else {
			long eventId = ContentUris.parseId(uri);
			saveReminders(contentProviderOperationList, eventId, newReminderList
					, originalReminderList, forceSaveReminders);
		}

		//attendees
		ContentValues values = new ContentValues();
		ContentProviderOperation.Builder attendeeOperationBuilder;
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
			attendeeOperationBuilder = ContentProviderOperation.newUpdate(attUri).withValues(values);
			contentProviderOperationList.add(attendeeOperationBuilder.build());
		} else if (hasAttendeeData && ownerAttendeeId == null) {
			String ownerEmail = selectedCalendar.getAsString(Calendars.OWNER_ACCOUNT);

			if (!newAttendeeList.isEmpty()) {
				values.clear();
				values.put(Attendees.ATTENDEE_EMAIL, ownerEmail);
				values.put(Attendees.ATTENDEE_RELATIONSHIP, Attendees.RELATIONSHIP_ORGANIZER);
				values.put(Attendees.ATTENDEE_TYPE, Attendees.TYPE_REQUIRED);
				values.put(Attendees.ATTENDEE_STATUS, Attendees.ATTENDEE_STATUS_ACCEPTED);

				if (isNewEvent) {
					attendeeOperationBuilder = ContentProviderOperation.newInsert(Attendees.CONTENT_URI)
							.withValues(values);
					attendeeOperationBuilder.withValueBackReference(Attendees.EVENT_ID, eventIdIndex);
				} else {
					values.put(Attendees.EVENT_ID, originalEvent.getAsLong(Instances.EVENT_ID));
					attendeeOperationBuilder = ContentProviderOperation.newInsert(Attendees.CONTENT_URI)
							.withValues(values);
				}
				contentProviderOperationList.add(attendeeOperationBuilder.build());
			}
		}

		if (hasAttendeeData && (isNewEvent || uri != null)) {
			StringBuilder newAttendeesString = new StringBuilder();
			StringBuilder originalAttendeesString = new StringBuilder();

			for (ContentValues newAttendee : newAttendeeList) {
				newAttendeesString.append(newAttendee.getAsString(Attendees.ATTENDEE_EMAIL));
			}
			for (ContentValues originalAttendee : originalAttendeeList) {
				originalAttendeesString.append(originalAttendee.getAsString(Attendees.ATTENDEE_EMAIL));
			}

			if (isNewEvent || !TextUtils.equals(originalAttendeesString.toString(), newAttendeesString.toString())) {
				HashMap<String, ContentValues> newAttendeesMap = new HashMap<>();

				for (ContentValues newAttendee : newAttendeeList) {
					newAttendeesMap.put(newAttendee.getAsString(Attendees.ATTENDEE_EMAIL), newAttendee);
				}

				LinkedList<String> removedAttendeeList = new LinkedList<>();
				long eventId = uri != null ? ContentUris.parseId(uri) : -1;

				if (!isNewEvent) {
					HashMap<String, ContentValues> originalAttendeesMap = new HashMap<>();
					for (ContentValues originalAttendee : originalAttendeeList) {
						originalAttendeesMap.put(originalAttendee.getAsString(Attendees.ATTENDEE_EMAIL), originalAttendee);
					}

					for (String originalEmail : originalAttendeesMap.keySet()) {
						if (newAttendeesMap.containsKey(originalEmail)) {
							newAttendeesMap.remove(originalEmail);
						} else {
							removedAttendeeList.add(originalEmail);
						}
					}

					if (removedAttendeeList.size() > 0) {
						attendeeOperationBuilder = ContentProviderOperation.newDelete(Attendees.CONTENT_URI);

						String[] args = new String[removedAttendeeList.size() + 1];
						args[0] = Long.toString(eventId);
						int i = 1;
						StringBuilder deleteWhere = new StringBuilder(Attendees.EVENT_ID + "=? AND "
								+ Attendees.ATTENDEE_EMAIL + " IN (");
						for (String removedAttendee : removedAttendeeList) {
							if (i > 1) {
								deleteWhere.append(",");
							}
							deleteWhere.append("?");
							args[i++] = removedAttendee;
						}
						deleteWhere.append(")");
						attendeeOperationBuilder.withSelection(deleteWhere.toString(), args);
						contentProviderOperationList.add(attendeeOperationBuilder.build());
					}
				}

				if (newAttendeesMap.size() > 0) {
					for (ContentValues newAttendee : newAttendeesMap.values()) {
						newAttendee.put(Attendees.ATTENDEE_RELATIONSHIP,
								Attendees.RELATIONSHIP_ATTENDEE);
						newAttendee.put(Attendees.ATTENDEE_TYPE, Attendees.TYPE_REQUIRED);
						newAttendee.put(Attendees.ATTENDEE_STATUS, Attendees.ATTENDEE_STATUS_NONE);

						if (isNewEvent) {
							attendeeOperationBuilder = ContentProviderOperation.newInsert(Attendees.CONTENT_URI)
									.withValues(values);
							attendeeOperationBuilder.withValueBackReference(Attendees.EVENT_ID, eventIdIndex);
						} else {
							values.put(Attendees.EVENT_ID, eventId);
							attendeeOperationBuilder = ContentProviderOperation.newInsert(Attendees.CONTENT_URI)
									.withValues(values);
						}
						contentProviderOperationList.add(attendeeOperationBuilder.build());
					}
				}
			}
		}

		UpdatedEventPrimaryValues updatedEventPrimaryValues = new UpdatedEventPrimaryValues();
		updatedEventPrimaryValues.setOriginalEventId(originalEvent.getAsLong(Instances.EVENT_ID));
		updatedEventPrimaryValues.setBegin((Long) getValues(originalEvent, newEvent, Events.DTSTART));
		updatedEventPrimaryValues.setEventEditType(eventEditType);

		mService.startBatch(mService.getNextToken(), null, CalendarContract.AUTHORITY, contentProviderOperationList,
				0, updatedEventPrimaryValues);
	}

	public Object getValues(ContentValues originalEvent, ContentValues newEvent, String key) {
		if (newEvent.containsKey(key)) {
			if (newEvent.get(key) != null) {
				return newEvent.get(key);
			} else {
				return null;
			}
		} else {
			return originalEvent.get(key);
		}
	}


	public boolean saveReminders(ArrayList<ContentProviderOperation> ops, long eventId,
	                             List<ContentValues> newReminderList, List<ContentValues> originalReminderList,
	                             boolean forceSave) {
		if (newReminderList.equals(originalReminderList) && !forceSave) {
			return false;
		}

		String where = Reminders.EVENT_ID + "=?";
		String[] args = new String[]{Long.toString(eventId)};
		ContentProviderOperation.Builder b = ContentProviderOperation
				.newDelete(Reminders.CONTENT_URI);
		b.withSelection(where, args);
		ops.add(b.build());

		for (ContentValues reminder : newReminderList) {
			reminder.put(Reminders.EVENT_ID, eventId);
			b = ContentProviderOperation.newInsert(Reminders.CONTENT_URI).withValues(reminder);
			ops.add(b.build());
		}
		return true;
	}

	private boolean saveRemindersWithBackRef(ArrayList<ContentProviderOperation> contentProviderOperationList, Integer eventIdIndex,
	                                         List<ContentValues> originalReminderList, List<ContentValues> newReminderList, boolean forceSave) {
		if (originalReminderList != null) {
			if (originalReminderList.equals(newReminderList) && !forceSave) {
				return false;
			}
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

	private void checkTimeDependentFields(ContentValues originalEvent, ContentValues newEvent, EventEditType eventEditType) {
		final long originalBegin = originalEvent.getAsLong(Instances.DTSTART);
		final boolean originalAllDay = originalEvent.getAsInteger(Events.ALL_DAY) == 1;
		final long originalEnd = originalAllDay ? originalEvent.getAsLong(Instances.END) : originalEvent.getAsLong(Instances.DTEND);
		final String originalTimeZone = originalEvent.getAsString(Events.EVENT_TIMEZONE);

		final long newBegin = newEvent.getAsLong(Events.DTSTART);
		final long newEnd = newEvent.getAsLong(Events.DTEND);
		final boolean newAllDay = newEvent.getAsInteger(Events.ALL_DAY) == 1;
		final String newTimeZone = newEvent.getAsString(Events.EVENT_TIMEZONE);

		if (originalBegin == newBegin && originalEnd == newEnd &&
				originalAllDay == newAllDay && originalTimeZone.equals(newTimeZone)) {
			newEvent.remove(Events.DTSTART);
			newEvent.remove(Events.DTEND);
			newEvent.remove(Events.DURATION);
			newEvent.remove(Events.ALL_DAY);
			newEvent.remove(Events.EVENT_TIMEZONE);
		} else if (eventEditType == EventEditType.UPDATE_ALL_EVENTS) {
			long oldStartMillis = originalEvent.getAsLong(Events.DTSTART);
			if (originalBegin != newBegin) {
				// The user changed the start time of this event
				long offset = newBegin - originalBegin;
				oldStartMillis += offset;
			}
			if (newAllDay) {
				Time time = new Time(Time.TIMEZONE_UTC);
				time.set(oldStartMillis);
				time.hour = 0;
				time.minute = 0;
				time.second = 0;
				oldStartMillis = time.toMillis(false);
			}
			newEvent.put(Events.DTSTART, oldStartMillis);
		}


	}

	private boolean isFirstInstance(ContentValues originalEvent, ContentValues newEvent) {
		return originalEvent.getAsLong(Events.DTSTART).equals(
				newEvent.getAsLong(Events.DTSTART));
	}


	public boolean isUnchanged(ContentValues newEvent) {
		if (newEvent.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public void convertDtEndForAllDay(ContentValues contentValues) {
		if (!contentValues.containsKey(Events.RRULE) && contentValues.containsKey(Events.DTSTART)) {
			if (contentValues.containsKey(CalendarContract.Events.ALL_DAY)) {
				if (contentValues.getAsInteger(CalendarContract.Events.ALL_DAY) == 1) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(contentValues.getAsLong(CalendarContract.Events.DTEND));
					calendar.add(Calendar.DAY_OF_YEAR, 1);

					contentValues.put(CalendarContract.Events.DTEND, calendar.getTimeInMillis());
				}
			}
		}
	}

	public void setDuration(ContentValues newEvent) {
		//반복 이벤트면 dtEnd를 삭제하고 duration추가
		if (newEvent.containsKey(Events.RRULE)) {
			final long start = newEvent.getAsLong(Events.DTSTART);
			final long end = newEvent.getAsLong(Events.DTEND);
			final boolean isAllDay = newEvent.getAsInteger(Events.ALL_DAY) == 1;

			String duration = null;

			if (isAllDay) {
				// if it's all day compute the duration in days
				long days = (end - start + DateUtils.DAY_IN_MILLIS - 1)
						/ DateUtils.DAY_IN_MILLIS;
				duration = "P" + days + "D";
			} else {
				// otherwise compute the duration in seconds
				long seconds = (end - start) / DateUtils.SECOND_IN_MILLIS;
				duration = "P" + seconds + "S";
			}
			// recurring events should have a duration and dtend set to null
			newEvent.put(Events.DURATION, duration);
			newEvent.put(Events.DTEND, (String) null);
		} else {
			newEvent.remove(Events.DURATION);
		}

	}
}

