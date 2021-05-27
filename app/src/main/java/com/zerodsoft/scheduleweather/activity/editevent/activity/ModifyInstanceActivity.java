package com.zerodsoft.scheduleweather.activity.editevent.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.value.EventData;

import java.util.List;

public class ModifyInstanceActivity extends EditEventActivity {

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
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(R.string.modify_event);

		Intent intent = getIntent();
		final int CALENDAR_ID = intent.getIntExtra(CalendarContract.Instances.CALENDAR_ID, 0);
		final long EVENT_ID = intent.getLongExtra(CalendarContract.Instances.EVENT_ID, 0);
		final long INSTANCE_ID = intent.getLongExtra(CalendarContract.Instances._ID, 0);
		final long BEGIN = intent.getLongExtra(CalendarContract.Instances.BEGIN, 0);
		final long END = intent.getLongExtra(CalendarContract.Instances.END, 0);

		dataController.putEventValue(CalendarContract.Instances._ID, EVENT_ID);

		// 이벤트, 알림을 가져온다
		ContentValues instance = calendarViewModel.getInstance(CALENDAR_ID, INSTANCE_ID, BEGIN, END);
		ContentValues event = calendarViewModel.getEvent(CALENDAR_ID, EVENT_ID);
		List<ContentValues> attendeeList = calendarViewModel.getAttendees(CALENDAR_ID, EVENT_ID);
		// 이벤트, 알림을 가져온다

		dataController.getSavedEventData().getEVENT().putAll(instance);
		// 제목, 캘린더, 시간, 시간대, 반복, 알림, 설명, 위치, 공개범위, 유효성, 참석자
		// 알림, 참석자 정보는 따로 불러온다.

		EventData savedEventData = dataController.getSavedEventData();
		ContentValues savedEvent = savedEventData.getEVENT();

		setDefaultEventColor(event.getAsString(CalendarContract.Events.ACCOUNT_NAME));
		//제목
		binding.titleLayout.title.setText(savedEvent.getAsString(CalendarContract.Instances.TITLE));

		//캘린더 수정 불가
		binding.calendarLayout.getRoot().setVisibility(View.GONE);

		// allday switch
		binding.timeLayout.timeAlldaySwitch.setChecked(savedEvent.getAsBoolean(CalendarContract.Instances.ALL_DAY));

		final long begin = savedEvent.getAsLong(CalendarContract.Instances.BEGIN);
		final long end = savedEvent.getAsLong(CalendarContract.Instances.END);
		dataController.putEventValue(CalendarContract.Instances.BEGIN, begin);
		dataController.putEventValue(CalendarContract.Instances.END, end);

		//시각
		setDateText(DateTimeType.START, begin);
		setDateText(DateTimeType.END, end);
		setTimeText(DateTimeType.START, begin);
		setTimeText(DateTimeType.END, end);

		// 시간대
		setTimeZoneText(savedEvent.getAsString(CalendarContract.Instances.EVENT_TIMEZONE));

		// 반복
		setRecurrenceText(savedEvent.getAsString(CalendarContract.Instances.RRULE));

		// 알림
		if (savedEvent.getAsBoolean(CalendarContract.Instances.HAS_ALARM)) {
			List<ContentValues> reminderList = calendarViewModel.getReminders(CALENDAR_ID, EVENT_ID);
			dataController.getSavedEventData().getREMINDERS().addAll(reminderList);
			dataController.getModifiedEventData().getREMINDERS().addAll(reminderList);
			setReminderText(reminderList);
		}

		// 설명
		binding.descriptionLayout.descriptionEdittext.setText(savedEvent.getAsString(CalendarContract.Instances.DESCRIPTION));

		// 위치
		binding.locationLayout.eventLocation.setText(savedEvent.getAsString(CalendarContract.Instances.EVENT_LOCATION));

		// 접근 범위
		setAccessLevelText(savedEvent.getAsInteger(CalendarContract.Instances.ACCESS_LEVEL));

		// 유효성
		setAvailabilityText(savedEvent.getAsInteger(CalendarContract.Instances.AVAILABILITY));

		if (!attendeeList.isEmpty()) {
			dataController.getSavedEventData().getATTENDEES().addAll(attendeeList);
			dataController.getModifiedEventData().getATTENDEES().addAll(attendeeList);
			setAttendeesText(attendeeList);
		} else {
			// 참석자 버튼 텍스트 수정
			binding.attendeeLayout.showAttendeesDetail.setText(getString(R.string.add_attendee));
		}
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.save_schedule_button:
				if (dataController.getSavedEventData().getEVENT().getAsString(CalendarContract.Events.RRULE) != null) {
					String[] dialogMenus = {
							getString(R.string.save_only_current_event),
							getString(R.string.save_all_future_events_including_current_event),
							getString(R.string.save_all_events)
					};

					new MaterialAlertDialogBuilder(getApplicationContext()).setTitle(R.string.save_event_title)
							.setItems(dialogMenus, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int index) {
									switch (index) {
										case 0:
											//현재 인스턴스만 변경
											//  updateThisInstance();
											break;
										case 1:
											//현재 인스턴스 이후의 모든 인스턴스 변경
											//  updateAfterInstanceIncludingThisInstance();
											break;
										case 2:
											//모든 일정이면 event를 변경
											updateEvent();
											break;
									}

								}
							}).create().show();
				} else {
					updateEvent();
				}
		}
		return super.onOptionsItemSelected(item);
	}
}
