package com.zerodsoft.scheduleweather.calendar;

import android.app.Application;
import android.content.ContentValues;

import androidx.lifecycle.AndroidViewModel;

import com.zerodsoft.scheduleweather.calendar.dto.AccountDto;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendar.interfaces.ICalendarProvider;

import java.util.List;
import java.util.Map;

public class CalendarViewModel extends AndroidViewModel implements ICalendarProvider {
	private CalendarProvider calendarProvider;

	public CalendarViewModel(Application application) {
		super(application);
		if (CalendarProvider.getInstance() == null) {
			this.calendarProvider = CalendarProvider.newInstance(application.getApplicationContext());
		} else {
			this.calendarProvider = CalendarProvider.getInstance();
		}
	}

	@Override
	public List<AccountDto> getGoogleAccounts() {
		return calendarProvider.getGoogleAccounts();
	}

	@Override
	public ContentValues getEvent(int calendarId, long eventId) {
		return calendarProvider.getEvent(calendarId, eventId);
	}

	@Override
	public List<ContentValues> getEvents(int calendarId) {
		return calendarProvider.getEvents(calendarId);
	}

	@Override
	public long addEvent(ContentValues event) {
		return calendarProvider.addEvent(event);
	}

	@Override
	public int deleteEvent(int calendarId, long eventId) {
		return calendarProvider.deleteEvent(calendarId, eventId);
	}

	@Override
	public int deleteEvents(int calendarId, long[] eventIds) {
		return calendarProvider.deleteEvents(calendarId, eventIds);
	}

	@Override
	public int updateEvent(ContentValues event) {
		return calendarProvider.updateEvent(event);
	}

	@Override
	public List<ContentValues> getAllCalendars() {
		return calendarProvider.getAllCalendars();
	}

	@Override
	public List<ContentValues> getCalendars() {
		return calendarProvider.getCalendars();
	}

	@Override
	public ContentValues getCalendar(int calendarId) {
		return calendarProvider.getCalendar(calendarId);
	}

	@Override
	public List<ContentValues> getReminders(int calendarId, long eventId) {
		return calendarProvider.getReminders(calendarId, eventId);
	}

	@Override
	public int updateReminder(ContentValues reminder) {
		return calendarProvider.updateReminder(reminder);
	}

	@Override
	public int deleteReminders(int calendarId, long eventId, long[] reminderIds) {
		return calendarProvider.deleteReminders(calendarId, eventId, reminderIds);
	}

	@Override
	public int deleteAllReminders(int calendarId, long eventId) {
		return calendarProvider.deleteAllReminders(calendarId, eventId);
	}

	@Override
	public int addReminders(List<ContentValues> reminders) {
		return calendarProvider.addReminders(reminders);
	}

	@Override
	public Map<Integer, CalendarInstance> getInstances(long begin, long end) {
		return calendarProvider.getInstances(begin, end);
	}

	@Override
	public ContentValues getInstance(int calendarId, long instanceId, long begin, long end) {
		return calendarProvider.getInstance(calendarId, instanceId, begin, end);
	}

	@Override
	public long updateAllFutureInstances(ContentValues modifiedInstance, ContentValues previousInstance) {
		return calendarProvider.updateAllFutureInstances(modifiedInstance, previousInstance);
	}

	@Override
	public int updateOneInstance(ContentValues modifiedInstance, ContentValues previousInstance) {
		return calendarProvider.updateOneInstance(modifiedInstance, previousInstance);
	}

	@Override
	public int deleteInstance(long begin, long eventId) {
		return calendarProvider.deleteInstance(begin, eventId);
	}

	@Override
	public int addAttendees(List<ContentValues> attendeeList) {
		return calendarProvider.addAttendees(attendeeList);
	}

	@Override
	public List<ContentValues> getAttendees(int calendarId, long eventId) {
		return calendarProvider.getAttendees(calendarId, eventId);
	}

	@Override
	public int updateAttendees(List<ContentValues> attendeeList) {
		return calendarProvider.updateAttendees(attendeeList);
	}

	@Override
	public int deleteAllAttendees(int calendarId, long eventId) {
		return calendarProvider.deleteAllAttendees(calendarId, eventId);
	}

	@Override
	public int deleteAttendees(int calendarId, long eventId, long[] attendeeIds) {
		return calendarProvider.deleteAttendees(calendarId, eventId, attendeeIds);
	}

	@Override
	public ContentValues getRecurrence(int calendarId, long eventId) {
		return calendarProvider.getRecurrence(calendarId, eventId);
	}

	@Override
	public int getCalendarColor(String accountName, String accountType) {
		return calendarProvider.getCalendarColor(accountName, accountType);
	}

	@Override
	public ContentValues getCalendarColor(int calendarId) {
		return calendarProvider.getCalendarColor(calendarId);
	}

	@Override
	public List<ContentValues> getCalendarColors(String accountName, String accountType) {
		return calendarProvider.getCalendarColors(accountName, accountType);
	}

	@Override
	public List<ContentValues> getEventColors(String accountName) {
		return calendarProvider.getEventColors(accountName);
	}

	@Override
	public int updateCalendarColor(int calendarId, int color, String colorKey) {
		return calendarProvider.updateCalendarColor(calendarId, color, colorKey);
	}


	public void syncCalendars() {
		calendarProvider.syncCalendars();
	}
}
