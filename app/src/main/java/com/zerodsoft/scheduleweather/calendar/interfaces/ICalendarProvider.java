package com.zerodsoft.scheduleweather.calendar.interfaces;

import android.content.ContentValues;

import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;

import java.util.List;
import java.util.Map;

public interface ICalendarProvider {
	// account
	List<ContentValues> getGoogleAccounts();

	//update event state
	void updateEventStatus(Long eventId, Integer newStatus);

	// event - crud
	ContentValues getEvent(Long eventId);

	List<ContentValues> getEvents(Integer calendarId);

	long addEvent(ContentValues event);

	int deleteEvent(Long eventId);

	int deleteEvents(Long[] eventIds);

	int updateEvent(ContentValues event);

	// calendar - select
	List<ContentValues> getAllCalendars();

	List<ContentValues> getCalendars();

	ContentValues getCalendar(Integer calendarId);

	// reminder - crud
	List<ContentValues> getReminders(Long eventId);

	int updateReminder(ContentValues reminder);

	int deleteReminders(Long eventId, Long[] reminderIds);

	int deleteAllReminders(Long eventId);

	int addReminders(List<ContentValues> reminders);

	// instance - read, update, delete
	Map<Integer, CalendarInstance> getInstances(Long begin, Long end);

	ContentValues getInstance(Long instanceId, Long begin, Long end);

	long updateAllFutureInstances(ContentValues modifiedInstance, ContentValues previousInstance);

	int updateOneInstance(ContentValues modifiedInstance, ContentValues previousInstance);

	int deleteInstance(Long begin, Long eventId);

	// attendee - crud
	int addAttendees(List<ContentValues> attendeeList);

	List<ContentValues> getAttendees(Long eventId);

	List<ContentValues> getAttendeeListForEdit(Long eventId);

	int updateAttendees(List<ContentValues> attendeeList);

	int deleteAllAttendees(Long eventId);

	int deleteAttendees(Long eventId, Long[] attendeeIds);

	// recurrence
	ContentValues getRecurrence(Long eventId);

	// calendar color
	int getCalendarColor(String accountName, String accountType);

	ContentValues getCalendarColor(Integer calendarId);

	List<ContentValues> getCalendarColors(String accountName, String accountType);

	List<ContentValues> getEventColors(String accountName);

	int updateCalendarColor(Integer calendarId, Integer color, String colorKey);

	ContentValues getValuesOfEvent(Long eventId, String... keys);
}