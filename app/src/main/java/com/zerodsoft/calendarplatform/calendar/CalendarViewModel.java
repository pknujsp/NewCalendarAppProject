package com.zerodsoft.calendarplatform.calendar;

import android.app.Application;
import android.content.ContentValues;
import android.os.Parcel;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.calendarplatform.activity.editevent.interfaces.OnEditEventResultListener;
import com.zerodsoft.calendarplatform.calendar.dto.CalendarInstance;
import com.zerodsoft.calendarplatform.calendar.interfaces.ICalendarProvider;

import java.util.List;
import java.util.Map;

public class CalendarViewModel extends AndroidViewModel implements ICalendarProvider, OnEditEventResultListener {
	private CalendarProvider calendarProvider;

	private MutableLiveData<Long> onAddedNewEventLiveData = new MutableLiveData<>();
	private MutableLiveData<Boolean> onRemovedEventLiveData = new MutableLiveData<>();
	private MutableLiveData<Boolean> onExceptedInstanceLiveData = new MutableLiveData<>();
	private MutableLiveData<Boolean> onRemovedFutureInstancesLiveData = new MutableLiveData<>();
	private MutableLiveData<Long> onUpdatedOnlyThisEventLiveData = new MutableLiveData<>();
	private MutableLiveData<Long> onUpdatedAllEventsLiveData = new MutableLiveData<>();
	private MutableLiveData<Long> onUpdatedFollowingEventsLiveData = new MutableLiveData<>();

	public CalendarViewModel(Application application) {
		super(application);

		this.calendarProvider = new CalendarProvider(application.getApplicationContext());
	}

	public LiveData<Long> getOnAddedNewEventLiveData() {
		return onAddedNewEventLiveData;
	}

	public LiveData<Boolean> getOnRemovedEventLiveData() {
		return onRemovedEventLiveData;
	}

	public LiveData<Boolean> getOnExceptedInstanceLiveData() {
		return onExceptedInstanceLiveData;
	}

	public LiveData<Boolean> getOnRemovedFutureInstancesLiveData() {
		return onRemovedFutureInstancesLiveData;
	}

	public LiveData<Long> getOnUpdatedOnlyThisEventLiveData() {
		return onUpdatedOnlyThisEventLiveData;
	}

	public LiveData<Long> getOnUpdatedAllEventsLiveData() {
		return onUpdatedAllEventsLiveData;
	}

	public LiveData<Long> getOnUpdatedFollowingEventsLiveData() {
		return onUpdatedFollowingEventsLiveData;
	}


	@Override
	public void updateEventStatus(Long eventId, Integer newStatus) {
		calendarProvider.updateEventStatus(eventId, newStatus);
	}

	@Override
	public List<ContentValues> getGoogleAccounts() {
		return calendarProvider.getGoogleAccounts();
	}

	@Override
	public ContentValues getEvent(Long eventId) {
		return calendarProvider.getEvent(eventId);
	}

	@Override
	public List<ContentValues> getEvents(Integer calendarId) {
		return calendarProvider.getEvents(calendarId);
	}

	@Override
	public long addEvent(ContentValues event) {
		return calendarProvider.addEvent(event);
	}

	@Override
	public int deleteEvent(Long eventId) {
		return calendarProvider.deleteEvent(eventId);
	}

	@Override
	public int deleteEvents(Long[] eventIds) {
		return calendarProvider.deleteEvents(eventIds);
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
	public ContentValues getCalendar(Integer calendarId) {
		return calendarProvider.getCalendar(calendarId);
	}

	@Override
	public List<ContentValues> getReminders(Long eventId) {
		return calendarProvider.getReminders(eventId);
	}

	@Override
	public int updateReminder(ContentValues reminder) {
		return calendarProvider.updateReminder(reminder);
	}

	@Override
	public int deleteReminders(Long eventId, Long[] reminderIds) {
		return calendarProvider.deleteReminders(eventId, reminderIds);
	}

	@Override
	public int deleteAllReminders(Long eventId) {
		return calendarProvider.deleteAllReminders(eventId);
	}

	@Override
	public int addReminders(List<ContentValues> reminders) {
		return calendarProvider.addReminders(reminders);
	}

	@Override
	public Map<Integer, CalendarInstance> getInstances(Long begin, Long end) {
		return calendarProvider.getInstances(begin, end);
	}

	@Override
	public ContentValues getInstance(Long instanceId, Long begin, Long end) {
		return calendarProvider.getInstance(instanceId, begin, end);
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
	public int deleteInstance(Long begin, Long eventId) {
		return calendarProvider.deleteInstance(begin, eventId);
	}

	@Override
	public int addAttendees(List<ContentValues> attendeeList) {
		return calendarProvider.addAttendees(attendeeList);
	}

	@Override
	public List<ContentValues> getAttendees(Long eventId) {
		return calendarProvider.getAttendees(eventId);
	}

	@Override
	public List<ContentValues> getAttendeeListForEdit(Long eventId) {
		return calendarProvider.getAttendeeListForEdit(eventId);
	}

	@Override
	public int updateAttendees(List<ContentValues> attendeeList) {
		return calendarProvider.updateAttendees(attendeeList);
	}

	@Override
	public int deleteAllAttendees(Long eventId) {
		return calendarProvider.deleteAllAttendees(eventId);
	}

	@Override
	public int deleteAttendees(Long eventId, Long[] attendeeIds) {
		return calendarProvider.deleteAttendees(eventId, attendeeIds);
	}

	@Override
	public ContentValues getRecurrence(Long eventId) {
		return calendarProvider.getRecurrence(eventId);
	}

	@Override
	public int getCalendarColor(String accountName, String accountType) {
		return calendarProvider.getCalendarColor(accountName, accountType);
	}

	@Override
	public ContentValues getCalendarColor(Integer calendarId) {
		return calendarProvider.getCalendarColor(calendarId);
	}

	@Override
	public List<ContentValues> getCalendarColors(String accountName, String accountType) {
		return calendarProvider.getCalendarColors(accountName, accountType);
	}

	@Override
	public List<ContentValues> getEventColors(String accountName, String accountType) {
		return calendarProvider.getEventColors(accountName, accountType);
	}

	@Override
	public int updateCalendarColor(Integer calendarId, Integer color, String colorKey) {
		return calendarProvider.updateCalendarColor(calendarId, color, colorKey);
	}

	@Override
	public ContentValues getValuesOfEvent(Long eventId, String... keys) {
		return calendarProvider.getValuesOfEvent(eventId, keys);
	}

	public void syncCalendars() {
		calendarProvider.syncCalendars();
	}

	@Override
	public void onSavedNewEvent(long dtStart) {
		onAddedNewEventLiveData.setValue(dtStart);
	}

	@Override
	public void onUpdatedOnlyThisEvent(long dtStart) {
		onUpdatedOnlyThisEventLiveData.setValue(dtStart);
	}

	@Override
	public void onUpdatedFollowingEvents(long dtStart) {
		onUpdatedFollowingEventsLiveData.setValue(dtStart);
	}

	@Override
	public void onUpdatedAllEvents(long dtStart) {
		onUpdatedAllEventsLiveData.setValue(dtStart);
	}

	@Override
	public void onRemovedAllEvents() {
		onRemovedEventLiveData.setValue(true);
	}

	@Override
	public void onRemovedFollowingEvents() {
		onRemovedFutureInstancesLiveData.setValue(true);
	}

	@Override
	public void onRemovedOnlyThisEvents() {
		onExceptedInstanceLiveData.setValue(true);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

	}
}
