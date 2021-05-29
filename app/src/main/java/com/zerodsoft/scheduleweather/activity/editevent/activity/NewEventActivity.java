package com.zerodsoft.scheduleweather.activity.editevent.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.activity.editevent.value.EventData;
import com.zerodsoft.scheduleweather.activity.editevent.value.EventDefaultValue;
import com.zerodsoft.scheduleweather.common.enums.EventIntentCode;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class NewEventActivity extends EditEventActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadInitData();
		initializing = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void loadInitData() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(R.string.new_event);

		//캘린더도 기본 값 설정
		List<ContentValues> calendars = calendarViewModel.getCalendars();
		//기본 캘린더 확인
		ContentValues defaultCalendar = calendars.get(0);
		selectedCalendarValues = defaultCalendar;
		eventDataViewModel.setCalendar(defaultCalendar.getAsInteger(CalendarContract.Calendars._ID));

		setCalendarText(defaultCalendar.getAsInteger(CalendarContract.Calendars.CALENDAR_COLOR),
				defaultCalendar.getAsString(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME),
				defaultCalendar.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));

		//event color
		List<ContentValues> colors = calendarViewModel.getEventColors(defaultCalendar.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));
		int color = colors.get(0).getAsInteger(CalendarContract.Colors.COLOR);
		String colorKey = colors.get(0).getAsString(CalendarContract.Colors.COLOR_KEY);

		eventDataViewModel.setEventColor(color, colorKey);
		binding.titleLayout.eventColor.setBackgroundColor(EventUtil.getColor(color));

		// 기기 시각으로 설정
		// 설정에서 기본 일정 시간 길이 설정가능
		int defaultHourRange = 60;

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		Date[] defaultDateTimes = new Date[2];
		defaultDateTimes[0] = calendar.getTime();
		calendar.add(Calendar.MINUTE, defaultHourRange);
		defaultDateTimes[1] = calendar.getTime();
		calendar.add(Calendar.MINUTE, -defaultHourRange);

		eventDataViewModel.setDtStart(defaultDateTimes[0]);
		eventDataViewModel.setDtEnd(defaultDateTimes[1]);

		setDateText(DateTimeType.START, defaultDateTimes[0].getTime());
		setDateText(DateTimeType.END, defaultDateTimes[1].getTime());
		setTimeText(DateTimeType.START, defaultDateTimes[0].getTime());
		setTimeText(DateTimeType.END, defaultDateTimes[1].getTime());

		// 기기 시간대로 설정
		TimeZone timeZone = null;
		if (App.isPreference_key_using_timezone_of_device()) {
			timeZone = TimeZone.getDefault();
		} else {
			timeZone = App.getPreference_key_custom_timezone();
		}
		eventDataViewModel.setTimezone(timeZone.getID());
		setTimeZoneText(timeZone.getID());

		// 접근 범위(기본)
		eventDataViewModel.setAccessLevel(CalendarContract.Events.ACCESS_DEFAULT);
		setAccessLevelText(CalendarContract.Events.ACCESS_DEFAULT);

		// 유효성(바쁨)
		eventDataViewModel.setAvailability(CalendarContract.Events.AVAILABILITY_BUSY);
		setAvailabilityText(CalendarContract.Events.AVAILABILITY_BUSY);

		// 참석자 버튼 텍스트 수정
		binding.attendeeLayout.showAttendeesDetail.setText(getString(R.string.add_attendee));
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.save_schedule_button:
				saveNewEvent();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void saveNewEvent() {
		// 시간이 바뀌는 경우, 알림 데이터도 변경해야함.
		// 알림 재설정
		ContentValues event = eventDataViewModel.getEVENT();
		List<ContentValues> reminderList = eventDataViewModel.getREMINDERS();
		List<ContentValues> attendeeList = eventDataViewModel.getATTENDEES();

		if (event.getAsInteger(CalendarContract.Events.ALL_DAY) == 1) {
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			calendar.setTimeInMillis(event.getAsLong(CalendarContract.Events.DTEND));
			calendar.add(Calendar.DAY_OF_YEAR, 1);

			event.put(CalendarContract.Events.DTEND, calendar.getTimeInMillis());
		}

		final int CALENDAR_ID = event.getAsInteger(CalendarContract.Events.CALENDAR_ID);
		final long NEW_EVENT_ID = calendarViewModel.addEvent(event);

		getIntent().putExtra(CalendarContract.Events._ID, NEW_EVENT_ID);
		getIntent().putExtra(CalendarContract.Instances.BEGIN, event.getAsLong(CalendarContract.Events.DTSTART));
		setResult(EventIntentCode.RESULT_SAVED.value(), getIntent());

		if (!reminderList.isEmpty()) {
			for (ContentValues reminder : reminderList) {
				reminder.put(CalendarContract.Reminders.EVENT_ID, NEW_EVENT_ID);
			}
			calendarViewModel.addReminders(reminderList);
		}

		if (!attendeeList.isEmpty()) {
			for (ContentValues attendee : attendeeList) {
				attendee.put(CalendarContract.Attendees.EVENT_ID, NEW_EVENT_ID);
			}
			calendarViewModel.addAttendees(attendeeList);
		}

		if (event.containsKey(CalendarContract.Events.EVENT_LOCATION)) {
			locationDTO.setEventId(NEW_EVENT_ID);
			locationViewModel.addLocation(locationDTO, new DbQueryCallback<LocationDTO>() {
				@Override
				public void onResultSuccessful(LocationDTO result) {
					finish();
				}

				@Override
				public void onResultNoData() {

				}
			});
		} else {
			finish();
		}
	}

	@Override
	protected void removeAttendee(String email) {
		super.removeAttendee(email);
		eventDataViewModel.removeAttendee(email);
	}

	@Override
	protected void removeReminderItemView(int minutes) {
		super.removeReminderItemView(minutes);
		eventDataViewModel.removeReminder(minutes);
	}
}
