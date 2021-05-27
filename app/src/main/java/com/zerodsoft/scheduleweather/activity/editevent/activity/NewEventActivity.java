package com.zerodsoft.scheduleweather.activity.editevent.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.enums.EventIntentCode;

import java.util.Date;
import java.util.TimeZone;

public class NewEventActivity extends EditEventActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadInitData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void loadInitData() {
		calendarList = calendarViewModel.getCalendars();
		ActionBar actionBar = getSupportActionBar();

		actionBar.setTitle(R.string.new_event);

		//캘린더도 기본 값 설정
		ContentValues defaultCalendar = dataController.getEventDefaultValue().getDefaultCalendar();

		dataController.setCalendarValue(defaultCalendar);
		setCalendarText(defaultCalendar.getAsInteger(CalendarContract.Calendars.CALENDAR_COLOR),
				defaultCalendar.getAsString(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME),
				defaultCalendar.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));

		setDefaultEventColor(defaultCalendar.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));

		// 기기 시각으로 설정
		Date[] defaultDateTimes = dataController.getEventDefaultValue().getDefaultDateTime();
		dataController.putEventValue(CalendarContract.Events.DTSTART, defaultDateTimes[0].getTime());
		dataController.putEventValue(CalendarContract.Events.DTEND, defaultDateTimes[1].getTime());

		setDateText(DateTimeType.START, defaultDateTimes[0].getTime());
		setDateText(DateTimeType.END, defaultDateTimes[1].getTime());
		setTimeText(DateTimeType.START, defaultDateTimes[0].getTime());
		setTimeText(DateTimeType.END, defaultDateTimes[1].getTime());

		// 기기 시간대로 설정
		TimeZone defaultTimeZone = dataController.getEventDefaultValue().getDefaultTimeZone();
		dataController.putEventValue(CalendarContract.Events.EVENT_TIMEZONE, defaultTimeZone.getID());
		setTimeZoneText(defaultTimeZone.getID());

		// 알림
		dataController.putEventValue(CalendarContract.Events.HAS_ALARM, 0);

		// 접근 범위(기본)
		dataController.putEventValue(CalendarContract.Events.ACCESS_LEVEL, dataController.getEventDefaultValue().getDefaultAccessLevel());
		setAccessLevelText(dataController.getEventDefaultValue().getDefaultAccessLevel());

		// 유효성(바쁨)
		dataController.putEventValue(CalendarContract.Events.AVAILABILITY, dataController.getEventDefaultValue().getDefaultAvailability());
		setAvailabilityText(dataController.getEventDefaultValue().getDefaultAvailability());

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

}
