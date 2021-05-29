package com.zerodsoft.scheduleweather.calendar.interfaces;

import android.content.ContentValues;

import com.zerodsoft.scheduleweather.calendar.dto.AccountDto;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;

import java.util.List;
import java.util.Map;

public interface ICalendarProvider {
	// account
	public List<AccountDto> getGoogleAccounts();

	// event - crud
	public ContentValues getEvent(Long eventId);

	public List<ContentValues> getEvents(Integer calendarId);

	public long addEvent(ContentValues event);

	public int deleteEvent(Long eventId);

	public int deleteEvents(Long[] eventIds);

	public int updateEvent(ContentValues event);

	// calendar - select
	public List<ContentValues> getAllCalendars();

	public List<ContentValues> getCalendars();

	public ContentValues getCalendar(Integer calendarId);

	// reminder - crud
	public List<ContentValues> getReminders(Long eventId);

	public int updateReminder(ContentValues reminder);

	public int deleteReminders(Long eventId, Long[] reminderIds);

	public int deleteAllReminders(Long eventId);

	public int addReminders(List<ContentValues> reminders);

	// instance - read, update, delete
	public Map<Integer, CalendarInstance> getInstances(Long begin, Long end);

	public ContentValues getInstance(Long instanceId, Long begin, Long end);

	public long updateAllFutureInstances(ContentValues modifiedInstance, ContentValues previousInstance);

	public int updateOneInstance(ContentValues modifiedInstance, ContentValues previousInstance);

	public int deleteInstance(Long begin, Long eventId);

	// attendee - crud
	public int addAttendees(List<ContentValues> attendeeList);

	public List<ContentValues> getAttendees(Long eventId);

	public int updateAttendees(List<ContentValues> attendeeList);

	public int deleteAllAttendees(Long eventId);

	public int deleteAttendees(Long eventId, Long[] attendeeIds);

	// recurrence
	public ContentValues getRecurrence(Long eventId);

	// calendar color
	public int getCalendarColor(String accountName, String accountType);

	public ContentValues getCalendarColor(Integer calendarId);

	public List<ContentValues> getCalendarColors(String accountName, String accountType);

	public List<ContentValues> getEventColors(String accountName);

	public int updateCalendarColor(Integer calendarId, Integer color, String colorKey);

	public ContentValues getValuesOfEvent(Long eventId, String... keys);
}