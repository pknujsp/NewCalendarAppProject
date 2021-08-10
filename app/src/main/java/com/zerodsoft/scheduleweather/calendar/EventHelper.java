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
import android.text.format.DateUtils;
import android.text.format.Time;

import androidx.annotation.Nullable;

import com.zerodsoft.scheduleweather.calendar.calendarcommon2.*;
import com.zerodsoft.scheduleweather.common.enums.LocationIntentCode;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	                         List<ContentValues> newAttendeeList, LocationIntentCode locationIntentCode) {
		//반복 이벤트면 dtEnd를 삭제하고 duration추가
		ArrayList<ContentProviderOperation> contentProviderOperationList
				= new ArrayList<>();
		int eventIdIndex = 0;

		convertDtEndForAllDay(newEvent);
		setDuration(newEvent);
		contentProviderOperationList.add(ContentProviderOperation.newInsert(Events.CONTENT_URI).withValues(newEvent)
				.build());

		Date start = new Date(newEvent.getAsLong(Events.DTSTART));

		if (!newReminderList.isEmpty()) {
			saveRemindersWithBackRef(contentProviderOperationList, eventIdIndex, null,
					newReminderList);
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

		EditEventPrimaryValues editEventPrimaryValues = new EditEventPrimaryValues();
		editEventPrimaryValues.setBegin(newEvent.getAsLong(Events.DTSTART));
		editEventPrimaryValues.setEventEditType(EventEditType.SAVE_NEW_EVENT);
		editEventPrimaryValues.setNewLocationDto(locationIntentCode, locationDto);

		mService.startBatch(mService.getNextToken(), null, CalendarContract.AUTHORITY, contentProviderOperationList,
				0, editEventPrimaryValues);
	}


	public void updateEvent(EventEditType eventEditType
			, ContentValues originalEvent, ContentValues newEvent,
			                List<ContentValues> originalReminderList,
			                List<ContentValues> originalAttendeeList,
			                List<ContentValues> newReminderList,
			                List<ContentValues> newAttendeeList,
			                ContentValues selectedCalendar, LocationDTO newLocationDto, LocationIntentCode locationIntentCode) {
		Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI
				, originalEvent.getAsLong(Instances.EVENT_ID));
		ArrayList<ContentProviderOperation> contentProviderOperationList
				= new ArrayList<>();
		int eventIdIndex = -1;
		boolean forceSaveReminders = false;

		if (newEvent.size() != 0 || !isUnchanged(newEvent)) {
			convertDtEndForAllDay(newEvent);

			if (eventEditType == EventEditType.UPDATE_ONLY_THIS_EVENT) {
				Uri exceptionUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_EXCEPTION_URI,
						originalEvent.getAsLong(Instances.EVENT_ID));

				ContentValues exceptionValues = new ContentValues();
				exceptionValues.put(Events.ORIGINAL_INSTANCE_TIME, originalEvent.getAsLong(Instances.BEGIN));
				exceptionValues.put(Events.STATUS, Events.STATUS_CANCELED);

				contentProviderOperationList.add(ContentProviderOperation.newInsert(exceptionUri).withValues(exceptionValues)
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
					if (newEvent.get(Events.RRULE) != null) {
						hasRRuleInNewEvent = true;
					}
				}

				if (!hasRRuleInNewEvent) {
					if (isFirstInstance(originalEvent)) {
						contentProviderOperationList.add(ContentProviderOperation.newDelete(uri).build());
					} else {
						updatePastEvents(contentProviderOperationList, originalEvent, newEvent.getAsLong(Events.DTSTART));
					}
					eventIdIndex = contentProviderOperationList.size();
					newEvent.put(Events.STATUS, originalEvent.getAsInteger(Events.STATUS));
					setDuration(newEvent);

					contentProviderOperationList.add(ContentProviderOperation.newInsert(Events.CONTENT_URI).withValues(newEvent)
							.build());
				} else {
					if (isFirstInstance(originalEvent)) {
						checkTimeDependentFields(originalEvent, newEvent, eventEditType);
						setDuration(newEvent);

						ContentProviderOperation.Builder b = ContentProviderOperation.newUpdate(uri)
								.withValues(newEvent);
						contentProviderOperationList.add(b.build());
					} else {
						String newRRule = updatePastEvents(contentProviderOperationList, originalEvent, newEvent.getAsLong(Events.DTSTART));
						EventRecurrence newEventRecurrence = new EventRecurrence();
						EventRecurrence originalEventRecurrence = new EventRecurrence();

						newEventRecurrence.parse(newEvent.getAsString(Events.RRULE));
						originalEventRecurrence.parse(originalEvent.getAsString(Events.RRULE));

						if (newEventRecurrence.equals(originalEventRecurrence)) {
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
					if (newEvent.get(Events.RRULE) != null) {
						hasRRuleInNewEvent = true;
					}
				}
				setDuration(newEvent);

				if (hasRRuleInNewEvent) {
					checkTimeDependentFields(originalEvent, newEvent, eventEditType);
					contentProviderOperationList.add(ContentProviderOperation.newUpdate(uri).withValues(newEvent).build());
				} else {
					contentProviderOperationList.add(ContentProviderOperation.newUpdate(uri).withValues(newEvent)
							.build());
					forceSaveReminders = true;
				}
			}
		}

		final boolean isNewEvent = eventIdIndex != -1;

		//reminders
		if (isNewEvent) {
			saveRemindersWithBackRef(contentProviderOperationList, eventIdIndex, originalReminderList,
					newReminderList);
		} else {
			long eventId = ContentUris.parseId(uri);
			saveReminders(contentProviderOperationList, eventId, newReminderList
					, originalReminderList);
		}

		//attendees
		ContentValues values = new ContentValues();
		ContentProviderOperation.Builder attendeeOperationBuilder;

		HashMap<String, ContentValues> originalAttendeesMap = new HashMap<>();
		HashMap<String, ContentValues> newAttendeesMap = new HashMap<>();

		for (ContentValues newAttendee : newAttendeeList) {
			newAttendeesMap.put(newAttendee.getAsString(Attendees.ATTENDEE_EMAIL), newAttendee);
		}

		for (ContentValues originalAttendee : originalAttendeeList) {
			originalAttendeesMap.put(originalAttendee.getAsString(Attendees.ATTENDEE_EMAIL), originalAttendee);
		}

		Set<String> originalAttendeeEmailSet = originalAttendeesMap.keySet();
		Set<String> newAttendeeEmailSet = newAttendeesMap.keySet();

		Set<String> removedAttendeeEmailSet = new HashSet<>(originalAttendeeEmailSet);
		removedAttendeeEmailSet.removeAll(newAttendeeEmailSet);
		Set<String> addedAttendeeEmailSet = new HashSet<>(newAttendeeEmailSet);
		addedAttendeeEmailSet.removeAll(originalAttendeeEmailSet);

		if (isNewEvent) {
			for (ContentValues newAttendee : newAttendeesMap.values()) {
				newAttendee.put(Attendees.ATTENDEE_RELATIONSHIP,
						Attendees.RELATIONSHIP_ATTENDEE);
				newAttendee.put(Attendees.ATTENDEE_TYPE, Attendees.TYPE_OPTIONAL);
				newAttendee.put(Attendees.ATTENDEE_STATUS, Attendees.ATTENDEE_STATUS_INVITED);

				attendeeOperationBuilder = ContentProviderOperation.newInsert(Attendees.CONTENT_URI)
						.withValues(newAttendee);
				attendeeOperationBuilder.withValueBackReference(Attendees.EVENT_ID, eventIdIndex);
				contentProviderOperationList.add(attendeeOperationBuilder.build());
			}
		} else {
			if (addedAttendeeEmailSet.size() > 0) {
				for (ContentValues newAttendee : newAttendeesMap.values()) {
					if (addedAttendeeEmailSet.contains(newAttendee.getAsString(Attendees.ATTENDEE_EMAIL))) {
						newAttendee.put(Attendees.ATTENDEE_RELATIONSHIP,
								Attendees.RELATIONSHIP_ATTENDEE);
						newAttendee.put(Attendees.ATTENDEE_TYPE, Attendees.TYPE_OPTIONAL);
						newAttendee.put(Attendees.ATTENDEE_STATUS, Attendees.ATTENDEE_STATUS_INVITED);

						long eventId = originalEvent.getAsLong(Instances.EVENT_ID);
						newAttendee.put(Attendees.EVENT_ID, eventId);
						attendeeOperationBuilder = ContentProviderOperation.newInsert(Attendees.CONTENT_URI)
								.withValues(newAttendee);
						contentProviderOperationList.add(attendeeOperationBuilder.build());
					}

				}
			}

			if (removedAttendeeEmailSet.size() > 0) {
				attendeeOperationBuilder = ContentProviderOperation.newDelete(Attendees.CONTENT_URI);

				String[] args = new String[removedAttendeeEmailSet.size() + 1];
				args[0] = originalEvent.getAsString(Instances.EVENT_ID);
				int i = 1;
				StringBuilder deleteWhere = new StringBuilder(Attendees.EVENT_ID + "=? AND "
						+ Attendees.ATTENDEE_EMAIL + " IN (");
				for (String removedAttendee : removedAttendeeEmailSet) {
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

		EditEventPrimaryValues editEventPrimaryValues = new EditEventPrimaryValues();
		editEventPrimaryValues.setBegin(getValuesAsLong(originalEvent, newEvent, Events.DTSTART));
		editEventPrimaryValues.setEventEditType(eventEditType);
		editEventPrimaryValues.setNewLocationDto(locationIntentCode, newLocationDto);

		mService.startBatch(mService.getNextToken(), null, CalendarContract.AUTHORITY, contentProviderOperationList,
				0, editEventPrimaryValues);
	}


	public void removeEvent(EventEditType eventEditType, ContentValues originalEvent) {
		ArrayList<ContentProviderOperation> contentProviderOperationList
				= new ArrayList<>();

		final long eventId = originalEvent.getAsLong(Instances.EVENT_ID);
		final boolean hasAttendees = originalEvent.getAsInteger(Events.HAS_ATTENDEE_DATA) == 1;
		final boolean hasReminders = originalEvent.getAsInteger(Events.HAS_ALARM) == 1;

		if (eventEditType == EventEditType.REMOVE_ALL_EVENTS) {
			if (hasAttendees) {
				Uri attendeeUri = ContentUris.withAppendedId(Attendees.CONTENT_URI, eventId);
				contentProviderOperationList.add(ContentProviderOperation.newDelete(attendeeUri).build());
			}
			if (hasReminders) {
				Uri reminderUri = ContentUris.withAppendedId(Reminders.CONTENT_URI, eventId);
				contentProviderOperationList.add(ContentProviderOperation.newDelete(reminderUri).build());
			}

			Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
			contentProviderOperationList.add(ContentProviderOperation.newDelete(uri).build());
		} else if (eventEditType == EventEditType.REMOVE_ONLY_THIS_EVENT) {
			ContentValues exceptionValues = new ContentValues();
			exceptionValues.put(Events.ORIGINAL_INSTANCE_TIME, originalEvent.getAsLong(Instances.BEGIN));
			exceptionValues.put(Events.STATUS, Events.STATUS_CANCELED);

			Uri exceptionUri = ContentUris.withAppendedId(Events.CONTENT_EXCEPTION_URI, eventId);
			contentProviderOperationList.add(ContentProviderOperation.newInsert(exceptionUri).withValues(exceptionValues)
					.build());
		} else if (eventEditType == EventEditType.REMOVE_FOLLOWING_EVENTS) {
			boolean hasRRuleInNewEvent = false;
			if (originalEvent.containsKey(Events.RRULE)) {
				if (originalEvent.get(Events.RRULE) != null) {
					hasRRuleInNewEvent = true;
				}
			}

			if (hasRRuleInNewEvent) {
				if (isFirstInstance(originalEvent)) {
					Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
					contentProviderOperationList.add(ContentProviderOperation.newDelete(uri).build());
				} else {
					String newRRule = updatePastEvents(contentProviderOperationList, originalEvent, originalEvent.getAsLong(Instances.BEGIN));

					EventRecurrence originalRecur = new EventRecurrence();
					originalRecur.parse(originalEvent.getAsString(Events.RRULE));
					EventRecurrence modifiedRecur = new EventRecurrence();
					modifiedRecur.parse(newRRule);

					if (!originalRecur.equals(modifiedRecur)) {
						Uri updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
						ContentValues updateValues = new ContentValues();
						updateValues.put(Events.RRULE, newRRule);
						contentProviderOperationList.add(ContentProviderOperation.newUpdate(updateUri).withValues(updateValues)
								.build());
					}
				}
			} else {
				Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
				contentProviderOperationList.add(ContentProviderOperation.newDelete(uri).build());
			}
		}

		EditEventPrimaryValues editEventPrimaryValues = new EditEventPrimaryValues();
		editEventPrimaryValues.setEventEditType(eventEditType);

		mService.startBatch(mService.getNextToken(), null, CalendarContract.AUTHORITY, contentProviderOperationList,
				0, editEventPrimaryValues);
	}

	public String getValuesAsString(ContentValues originalEvent, ContentValues newEvent, String key) {
		if (newEvent.containsKey(key)) {
			if (newEvent.get(key) != null) {
				return newEvent.getAsString(key);
			} else {
				return null;
			}
		} else {
			return originalEvent.getAsString(key);
		}
	}

	public Long getValuesAsLong(ContentValues originalEvent, ContentValues newEvent, String key) {
		if (newEvent.containsKey(key)) {
			if (newEvent.get(key) != null) {
				return newEvent.getAsLong(key);
			} else {
				return null;
			}
		} else {
			return originalEvent.getAsLong(key);
		}
	}


	public boolean saveReminders(ArrayList<ContentProviderOperation> ops, long eventId,
	                             List<ContentValues> newReminderList, List<ContentValues> originalReminderList) {
		if (originalReminderList.size() > 0) {
			String where = Reminders.EVENT_ID + "=?";
			String[] args = new String[]{Long.toString(eventId)};
			ContentProviderOperation.Builder b = ContentProviderOperation.newDelete(Reminders.CONTENT_URI);
			b.withSelection(where, args);
			ops.add(b.build());
		}

		if (newReminderList.size() > 0) {
			for (ContentValues reminder : newReminderList) {
				reminder.put(Reminders.EVENT_ID, eventId);
				ContentProviderOperation.Builder b = ContentProviderOperation.newInsert(Reminders.CONTENT_URI).withValues(reminder);
				ops.add(b.build());
			}
		}
		return true;
	}

	private boolean saveRemindersWithBackRef(ArrayList<ContentProviderOperation> contentProviderOperationList, Integer eventIdIndex,
	                                         List<ContentValues> originalReminderList, List<ContentValues> newReminderList) {
		if (originalReminderList != null) {
			if (originalReminderList.equals(newReminderList)) {
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
	                                long rangeEndMillis) {
		final boolean originalAllDay = originalEvent.getAsInteger(Events.ALL_DAY) == 1;
		final String originalRRule = originalEvent.getAsString(Events.RRULE);
		String newRRule = originalRRule;

		final long originalStartTimeMillis = originalEvent.getAsLong(Events.DTSTART);
		Time originalDtStart = new Time();
		originalDtStart.timezone = originalEvent.getAsString(Events.EVENT_TIMEZONE);
		originalDtStart.set(originalStartTimeMillis);

		ContentValues updateValues = new ContentValues();

		EventRecurrence originalEventRecurrence = new EventRecurrence();
		originalEventRecurrence.parse(originalEvent.getAsString(Events.RRULE));

		if (originalEventRecurrence.count > 0) {
			RecurrenceSet recurSet = new RecurrenceSet(originalRRule, null, null, null);
			RecurrenceProcessor recurProc = new RecurrenceProcessor();
			long[] recurrences;
			try {
				recurrences = recurProc.expand(originalDtStart, recurSet, originalStartTimeMillis, rangeEndMillis);
			} catch (DateException de) {
				throw new RuntimeException(de);
			}

			EventRecurrence exceptRecurrence = new EventRecurrence();
			exceptRecurrence.parse(originalRRule);
			exceptRecurrence.count -= recurrences.length;
			newRRule = exceptRecurrence.toString();

			originalEventRecurrence.count = recurrences.length;
		} else {
			Time untilTime = new Time();
			untilTime.timezone = Time.TIMEZONE_UTC;

			untilTime.set(rangeEndMillis - 1000);
			if (originalAllDay) {
				untilTime.hour = 0;
				untilTime.minute = 0;
				untilTime.second = 0;
				untilTime.allDay = true;
				untilTime.normalize(false);

				// This should no longer be necessary -- DTSTART should already be in the correct
				// format for an all-day event.
				originalDtStart.hour = 0;
				originalDtStart.minute = 0;
				originalDtStart.second = 0;
				originalDtStart.allDay = true;
				originalDtStart.timezone = Time.TIMEZONE_UTC;
			}
			originalEventRecurrence.until = untilTime.format2445();
		}

		updateValues.put(Events.RRULE, originalEventRecurrence.toString());
		updateValues.put(Events.DTSTART, originalDtStart.normalize(true));

		Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI
				, originalEvent.getAsLong(Instances.EVENT_ID));
		ContentProviderOperation.Builder b =
				ContentProviderOperation.newUpdate(uri)
						.withValues(updateValues);
		contentProviderOperationList.add(b.build());

		return newRRule;
	}

	private void checkTimeDependentFields(ContentValues originalEvent, ContentValues newEvent, EventEditType eventEditType) {
		if (!newEvent.containsKey(Events.DTSTART)) {
			return;
		}

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

	private boolean isFirstInstance(ContentValues originalEvent) {
		return originalEvent.getAsLong(Events.DTSTART).equals(
				originalEvent.getAsLong(Instances.BEGIN));
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
			if (contentValues.containsKey(Events.ALL_DAY)) {
				if (contentValues.getAsInteger(Events.ALL_DAY) == 1) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(contentValues.getAsLong(Events.DTEND));
					calendar.add(Calendar.DAY_OF_YEAR, 1);

					contentValues.put(Events.DTEND, calendar.getTimeInMillis());
				}
			}
		}
	}

	public void setDuration(ContentValues newEvent) {
		//반복 이벤트면 dtEnd를 삭제하고 duration추가
		if (!newEvent.containsKey(Events.DTSTART)) {
			return;
		}
		newEvent.putNull(Events.DURATION);

		if (newEvent.get(Events.RRULE) != null) {
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
		}

	}
}

