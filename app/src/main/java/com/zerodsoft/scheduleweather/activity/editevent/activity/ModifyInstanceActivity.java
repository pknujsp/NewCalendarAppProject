package com.zerodsoft.scheduleweather.activity.editevent.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.enums.EventIntentCode;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import java.util.Date;
import java.util.List;

public class ModifyInstanceActivity extends EditEventActivity {
	private ContentValues originalInstance = new ContentValues();
	private boolean originalHasAttendeeList;
	private boolean originalHasReminderList;

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
		actionBar.setTitle(R.string.modify_event);

		Intent intent = getIntent();
		final long eventId = intent.getLongExtra(CalendarContract.Instances.EVENT_ID, 0);
		final long instanceId = intent.getLongExtra(CalendarContract.Instances._ID, 0);
		final long begin = intent.getLongExtra(CalendarContract.Instances.BEGIN, 0);
		final long end = intent.getLongExtra(CalendarContract.Instances.END, 0);
		final String rrule = intent.getStringExtra(CalendarContract.Instances.RRULE);

		//이벤트와 인스턴스를 구분해서 데이터를 가져온다

		// 이벤트, 알림을 가져온다
		originalInstance = calendarViewModel.getInstance(instanceId, begin, end);
		ContentValues originalEventValues = calendarViewModel.getEvent(eventId);
		selectedCalendarValues = calendarViewModel.getCalendar(originalInstance.getAsInteger(CalendarContract.Instances.CALENDAR_ID));
		eventDataViewModel.getATTENDEES().addAll(calendarViewModel.getAttendees(eventId));

		if (!eventDataViewModel.getATTENDEES().isEmpty()) {
			originalHasAttendeeList = true;
			setAttendeesText(eventDataViewModel.getATTENDEES());
		} else {
			// 참석자 버튼 텍스트 수정
			binding.attendeeLayout.showAttendeesDetail.setText(getString(R.string.add_attendee));
		}

		//제목, 캘린더, 시간, 시간대, 반복, 알림, 설명, 위치, 공개범위, 유효성, 참석자
		//알림, 참석자 정보는 따로 불러온다.
		binding.titleLayout.eventColor.setBackgroundColor(EventUtil.getColor(originalInstance.getAsInteger(CalendarContract.Instances.EVENT_COLOR)));

		//제목
		binding.titleLayout.title.setText(originalInstance.getAsString(CalendarContract.Instances.TITLE));

		// allday switch
		final boolean isAllDay = originalInstance.getAsInteger(CalendarContract.Instances.ALL_DAY) == 1;
		binding.timeLayout.timeAlldaySwitch.setChecked(isAllDay);

		if (isAllDay) {
			binding.timeLayout.eventTimezone.setText(originalInstance.getAsString(CalendarContract.Events.CALENDAR_TIME_ZONE));
		} else {
			binding.timeLayout.eventTimezone.setText(originalInstance.getAsString(CalendarContract.Events.EVENT_TIMEZONE));
		}

		eventDataViewModel.setDtStart(new Date(begin));
		eventDataViewModel.setDtEnd(new Date(end));
		eventDataViewModel.setTimezone(originalInstance.getAsString(CalendarContract.Instances.EVENT_TIMEZONE));

		//시각
		setDateText(DateTimeType.START, begin);
		setDateText(DateTimeType.END, end);
		setTimeText(DateTimeType.START, begin);
		setTimeText(DateTimeType.END, end);

		//캘린더
		eventDataViewModel.setCalendar(selectedCalendarValues.getAsInteger(CalendarContract.Calendars._ID));
		setCalendarText(selectedCalendarValues.getAsInteger(CalendarContract.Calendars.CALENDAR_COLOR),
				selectedCalendarValues.getAsString(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME),
				selectedCalendarValues.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));

		// 반복
		setRecurrenceText(originalInstance.getAsString(CalendarContract.Instances.RRULE));

		// 알림
		if (originalInstance.getAsBoolean(CalendarContract.Instances.HAS_ALARM)) {
			originalHasReminderList = true;
			List<ContentValues> originalReminderList = calendarViewModel.getReminders(eventId);
			eventDataViewModel.getREMINDERS().addAll(originalReminderList);

			for (ContentValues reminder : originalReminderList) {
				addReminderItemView(reminder);
			}
		}

		// 설명
		binding.descriptionLayout.descriptionEdittext.setText(originalInstance.getAsString(CalendarContract.Instances.DESCRIPTION));

		// 위치
		binding.locationLayout.eventLocation.setText(originalInstance.getAsString(CalendarContract.Instances.EVENT_LOCATION));
		locationViewModel.getLocation(eventId, new DbQueryCallback<LocationDTO>() {
			@Override
			public void onResultSuccessful(LocationDTO result) {
				locationDTO = result;
			}

			@Override
			public void onResultNoData() {

			}
		});

		// 접근 범위
		setAccessLevelText(originalInstance.getAsInteger(CalendarContract.Instances.ACCESS_LEVEL));

		// 유효성
		setAvailabilityText(originalInstance.getAsInteger(CalendarContract.Instances.AVAILABILITY));
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.save_schedule_button:
				if (eventDataViewModel.getEVENT().getAsString(CalendarContract.Instances.RRULE) != null) {
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
											updateThisInstance();
											break;
										case 1:
											//현재 인스턴스 이후의 모든 인스턴스 변경
											// updateAfterInstanceIncludingThisInstance();
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

	protected void updateThisInstance() {
		final long originalEventId = originalInstance.getAsLong(CalendarContract.Instances.EVENT_ID);

		ContentValues modifiedInstance = eventDataViewModel.getEVENT();
		ContentValues newEvent = new ContentValues();
		newEvent.putAll(modifiedInstance);

		//인스턴스를 이벤트에서 제외
		calendarViewModel.deleteInstance(originalInstance.getAsLong(CalendarContract.Instances.BEGIN), originalEventId);

		//반복 삭제, 그외 값 초기화
		newEvent.remove(CalendarContract.Events.RRULE);
		newEvent.remove(CalendarContract.Events.HAS_ALARM);

		//인스턴스를 새로운 이벤트로 추가
		final long newEventId = calendarViewModel.addEvent(newEvent);
		final int newCalendarId = newEvent.getAsInteger(CalendarContract.Events.CALENDAR_ID);

		// 알람 목록 갱신
		if (!eventDataViewModel.getREMINDERS().isEmpty()) {
			List<ContentValues> reminders = eventDataViewModel.getREMINDERS();

			for (ContentValues reminder : reminders) {
				reminder.put(CalendarContract.Reminders.EVENT_ID, newEventId);
			}
			calendarViewModel.addReminders(reminders);
		}

		// 참석자 목록 갱신
		if (!eventDataViewModel.getATTENDEES().isEmpty()) {
			List<ContentValues> attendeeList = eventDataViewModel.getATTENDEES();

			for (ContentValues addedAttendee : attendeeList) {
				addedAttendee.put(CalendarContract.Attendees.EVENT_ID, newEventId);
			}
			calendarViewModel.addAttendees(attendeeList);
		}

		// 위치 추가
		if (locationDTO != null) {
			locationDTO.setEventId(newEventId);

			locationViewModel.addLocation(locationDTO, new DbQueryCallback<LocationDTO>() {
				@Override
				public void onResultSuccessful(LocationDTO result) {
					finish();
				}

				@Override
				public void onResultNoData() {

				}
			});
		}

		setResult(EventIntentCode.RESULT_MODIFIED_THIS_INSTANCE.value());
		finish();
	}

	protected void updateAfterInstanceIncludingThisInstance() {
		ContentValues event = eventDataViewModel.getEVENT();
		calendarViewModel.updateAllFutureInstances(event, originalInstance);

		setResult(EventIntentCode.RESULT_MODIFIED_AFTER_INSTANCE_INCLUDING_THIS_INSTANCE.value());
		finish();
	}

	protected void updateEvent() {
		ContentValues event = eventDataViewModel.getEVENT();
		final int calendarId = originalInstance.getAsInteger(CalendarContract.Instances.CALENDAR_ID);
		final long eventId = originalInstance.getAsInteger(CalendarContract.Instances.EVENT_ID);

		event.put(CalendarContract.Events._ID, eventId);
		calendarViewModel.updateEvent(event);

		if (originalHasAttendeeList) {
			calendarViewModel.deleteAllAttendees(eventId);
		}
		if (originalHasReminderList) {
			calendarViewModel.deleteAllReminders(eventId);
		}

		List<ContentValues> reminderList = eventDataViewModel.getREMINDERS();
		List<ContentValues> attendeeList = eventDataViewModel.getATTENDEES();

		// 알람 목록 갱신
		if (!reminderList.isEmpty()) {
			for (ContentValues reminder : reminderList) {
				reminder.put(CalendarContract.Reminders.EVENT_ID, eventId);
			}
			calendarViewModel.addReminders(reminderList);
		}

		// 참석자 목록 갱신
		if (!attendeeList.isEmpty()) {
			for (ContentValues addedAttendee : attendeeList) {
				addedAttendee.put(CalendarContract.Attendees.EVENT_ID, eventId);
			}
			calendarViewModel.addAttendees(attendeeList);
		}

		getIntent().putExtra(CalendarContract.Events._ID,
				event.getAsLong(CalendarContract.Events._ID));
		getIntent().putExtra(CalendarContract.Instances.BEGIN,
				event.getAsLong(CalendarContract.Events.DTSTART));

		setResult(EventIntentCode.RESULT_MODIFIED_EVENT.value(), getIntent());
		finish();
	}


	protected void modifyEvent(int action) {
		/*
		if (modifiedEventData.getEVENT().getAsString(CalendarContract.Events.EVENT_LOCATION) != null) {
			// 위치가 추가 | 변경된 경우
			locationDTO.setCalendarId(CALENDAR_ID);
			locationDTO.setEventId(ORIGINAL_EVENT_ID);

			//상세 위치가 지정되어 있는지 확인
			locationViewModel.hasDetailLocation(CALENDAR_ID, ORIGINAL_EVENT_ID, new CarrierMessagingService.ResultCallback<Boolean>() {
				@Override
				public void onReceiveResult(@NonNull Boolean aBoolean) {
					if (aBoolean) {
						// 상세위치가 지정되어 있고, 현재 위치를 변경하려는 상태
						locationViewModel.modifyLocation(locationDTO, new CarrierMessagingService.ResultCallback<Boolean>() {
							@Override
							public void onReceiveResult(@NonNull Boolean aBoolean) {
								setResult(RESULT_OK);
								finish();
							}
						});
					} else {
						// 상세위치를 추가하는 경우
						locationViewModel.addLocation(locationDTO, new CarrierMessagingService.ResultCallback<Boolean>() {
							@Override
							public void onReceiveResult(@NonNull Boolean aBoolean) {
								setResult(RESULT_OK);
								finish();
							}
						});
					}
				}
			});

		} else {
			if (savedEventData.getEVENT().getAsString(CalendarContract.Events.EVENT_LOCATION) != null) {
				// 현재 위치를 삭제하려고 하는 상태
				locationViewModel.hasDetailLocation(CALENDAR_ID, ORIGINAL_EVENT_ID, new CarrierMessagingService.ResultCallback<Boolean>() {
					@Override
					public void onReceiveResult(@NonNull Boolean aBoolean) {
						if (aBoolean) {
							// 기존의 상세 위치를 제거
							locationViewModel.removeLocation(CALENDAR_ID, ORIGINAL_EVENT_ID, new CarrierMessagingService.ResultCallback<Boolean>() {
								@Override
								public void onReceiveResult(@NonNull Boolean aBoolean) {
									setResult(RESULT_OK);
									finish();
								}
							});
						} else {
							// 상세 위치가 지정되어 있지 않음
							setResult(RESULT_OK);
							finish();
						}
					}
				});

			} else {
				//위치를 원래 설정하지 않은 경우
				setResult(RESULT_OK);
				finish();
			}
		}
*/
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
