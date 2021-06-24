package com.zerodsoft.scheduleweather.activity.editevent.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.RecurrenceRule;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ModifyInstanceFragment extends EventBaseFragment {
	private OnModifyInstanceResultListener onModifyInstanceResultListener;
	private ContentValues originalInstance;

	public ModifyInstanceFragment(OnModifyInstanceResultListener onModifyInstanceResultListener) {
		this.onModifyInstanceResultListener = onModifyInstanceResultListener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
		calendarViewModel = new ViewModelProvider(requireActivity()).get(CalendarViewModel.class);
		eventDataViewModel = new ViewModelProvider(this).get(EventDataViewModel.class);
	}

	@Override
	public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		loadInitData();
		initializing = false;

		binding.saveBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!checkEdited()) {
					Toast.makeText(getContext(), R.string.not_edited, Toast.LENGTH_SHORT).show();
					return;
				}
				if (originalInstance.getAsString(CalendarContract.Instances.RRULE) != null) {
					String[] dialogMenus = {
							getString(R.string.save_only_current_event),
							getString(R.string.save_all_future_events_including_current_event),
							getString(R.string.save_all_events)
					};

					new MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.save_event_title)
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
											updateAfterInstanceIncludingThisInstance();
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
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private boolean checkEdited() {
		//allday ,dtstart, dtend, eventtimezone으로 확인
		if (eventDataViewModel.getNEW_EVENT().getAsLong(CalendarContract.Events.DTSTART).equals(originalInstance.getAsLong(CalendarContract.Instances.DTSTART)) &&
				eventDataViewModel.getNEW_EVENT().getAsLong(CalendarContract.Events.DTEND).equals(originalInstance.getAsLong(CalendarContract.Instances.DTEND))) {
			if (eventDataViewModel.getNEW_EVENT().getAsString(CalendarContract.Events.EVENT_TIMEZONE).equals(originalInstance.getAsString(CalendarContract.Instances.EVENT_TIMEZONE))) {
				if (eventDataViewModel.getNEW_EVENT().getAsInteger(CalendarContract.Events.ALL_DAY).equals(originalInstance.getAsInteger(CalendarContract.Instances.ALL_DAY))) {
					if (eventDataViewModel.getNEW_EVENT().size() == 4) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	protected void loadInitData() {
		Bundle arguments = getArguments();

		final long eventId = arguments.getLong(CalendarContract.Instances.EVENT_ID, 0);
		final long instanceId = arguments.getLong(CalendarContract.Instances._ID, 0);
		final long begin = arguments.getLong(CalendarContract.Instances.BEGIN, 0);
		final long end = arguments.getLong(CalendarContract.Instances.END, 0);

		//이벤트와 인스턴스를 구분해서 데이터를 가져온다

		// 이벤트, 알림을 가져온다
		originalInstance = calendarViewModel.getInstance(instanceId, begin, end);
		selectedCalendarValues =
				calendarViewModel.getCalendar(originalInstance.getAsInteger(CalendarContract.Instances.CALENDAR_ID));

		eventDataViewModel.getREMINDERS().addAll(calendarViewModel.getReminders(eventId));
		eventDataViewModel.getATTENDEES().addAll(calendarViewModel.getAttendees(eventId));

		if (!eventDataViewModel.getATTENDEES().isEmpty()) {
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
		eventDataViewModel.setIsAllDay(isAllDay);
		binding.timeLayout.timeAlldaySwitch.setChecked(isAllDay);

		eventDataViewModel.setTimezone(originalInstance.getAsString(CalendarContract.Instances.EVENT_TIMEZONE));
		eventDataViewModel.setDtStart(new Date(originalInstance.getAsLong(CalendarContract.Instances.BEGIN)));
		eventDataViewModel.setDtEnd(new Date(originalInstance.getAsLong(CalendarContract.Instances.END)));

		if (isAllDay) {
			int startDay = originalInstance.getAsInteger(CalendarContract.Instances.START_DAY);
			int endDay = originalInstance.getAsInteger(CalendarContract.Instances.END_DAY);
			int dayDifference = endDay - startDay;

			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			calendar.setTimeInMillis(originalInstance.getAsLong(CalendarContract.Instances.BEGIN));

			setTimeZoneText(originalInstance.getAsString(CalendarContract.Events.CALENDAR_TIME_ZONE));

			setDateText(DateTimeType.START, calendar.getTime().getTime());
			setTimeText(DateTimeType.START, calendar.getTime().getTime());

			calendar.add(Calendar.DAY_OF_YEAR, dayDifference);

			setDateText(DateTimeType.END, calendar.getTime().getTime());
			setTimeText(DateTimeType.END, calendar.getTime().getTime());
		} else {
			setTimeZoneText(originalInstance.getAsString(CalendarContract.Events.EVENT_TIMEZONE));

			setDateText(DateTimeType.START, originalInstance.getAsLong(CalendarContract.Instances.BEGIN));
			setDateText(DateTimeType.END, originalInstance.getAsLong(CalendarContract.Instances.END));
			setTimeText(DateTimeType.START, originalInstance.getAsLong(CalendarContract.Instances.BEGIN));
			setTimeText(DateTimeType.END, originalInstance.getAsLong(CalendarContract.Instances.END));
		}


		//캘린더
		setCalendarText(originalInstance.getAsInteger(CalendarContract.Instances.CALENDAR_COLOR),
				originalInstance.getAsString(CalendarContract.Instances.CALENDAR_DISPLAY_NAME),
				selectedCalendarValues.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));

		// 반복
		if (originalInstance.getAsString(CalendarContract.Instances.RRULE) != null) {
			setRecurrenceText(originalInstance.getAsString(CalendarContract.Instances.RRULE));
		}

		// 알림
		if (originalInstance.getAsInteger(CalendarContract.Instances.HAS_ALARM) == 1) {
			List<ContentValues> originalReminderList = eventDataViewModel.getREMINDERS();

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

	//이번 일정만 변경
	protected void updateThisInstance() {
		//인스턴스를 이벤트에서 제외
		ContentValues exceptionEvent = new ContentValues();
		exceptionEvent.put(CalendarContract.Events.ORIGINAL_INSTANCE_TIME, originalInstance.getAsLong(CalendarContract.Instances.BEGIN));
		exceptionEvent.put(CalendarContract.Events.STATUS, CalendarContract.Events.STATUS_CANCELED);

		Uri exceptionUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_EXCEPTION_URI,
				originalInstance.getAsLong(CalendarContract.Instances.EVENT_ID));
		ContentResolver contentResolver = getContext().getContentResolver();
		Uri result = contentResolver.insert(exceptionUri, exceptionEvent);

		//수정한 인스턴스를 새로운 이벤트로 추가
		//반복 규칙 없음!
		ContentValues modifiedInstance = eventDataViewModel.getNEW_EVENT();
		ContentValues newEventValues = new ContentValues();

		setNewEventValues(CalendarContract.Events.TITLE, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.EVENT_COLOR_KEY, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.EVENT_COLOR, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.CALENDAR_ID, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.ALL_DAY, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.DTSTART, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.DTEND, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.EVENT_TIMEZONE, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.DESCRIPTION, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.EVENT_LOCATION, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.AVAILABILITY, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.ACCESS_LEVEL, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.GUESTS_CAN_MODIFY, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, newEventValues, modifiedInstance);

		if (modifiedInstance.containsKey(CalendarContract.Events.RRULE)) {
			setNewEventValues(CalendarContract.Events.RRULE, newEventValues, modifiedInstance);
		}

		if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
			return;
		}
		Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, newEventValues);
		final long newEventId = Long.parseLong(uri.getLastPathSegment());

		List<ContentValues> modifiedReminderList = eventDataViewModel.getREMINDERS();
		List<ContentValues> modifiedAttendeeList = eventDataViewModel.getATTENDEES();

		// 알람 목록 갱신
		if (!modifiedReminderList.isEmpty()) {
			for (ContentValues reminder : modifiedReminderList) {
				reminder.put(CalendarContract.Reminders.EVENT_ID, newEventId);
			}
			calendarViewModel.addReminders(modifiedReminderList);
		}

		if (!modifiedAttendeeList.isEmpty()) {
			for (ContentValues addedAttendee : modifiedAttendeeList) {
				addedAttendee.put(CalendarContract.Attendees.EVENT_ID, newEventId);
			}
			calendarViewModel.addAttendees(modifiedAttendeeList);
		}

		if (newEventValues.containsKey(CalendarContract.Events.EVENT_LOCATION)) {
			if (locationDTO == null) {
				//위치를 바꾸지 않고, 기존 이벤트의 값을 그대로 유지
				locationViewModel.getLocation(originalInstance.getAsLong(CalendarContract.Instances.EVENT_ID),
						new DbQueryCallback<LocationDTO>() {
							@Override
							public void onResultSuccessful(LocationDTO savedLocationDto) {
								savedLocationDto.setEventId(newEventId);
								locationViewModel.addLocation(savedLocationDto, locationDbQueryCallback);
							}

							@Override
							public void onResultNoData() {
								onModifyInstanceResultListener.onResultModifiedAfterAllInstancesIncludingThisInstance();
								getParentFragmentManager().popBackStack();
							}
						});
			} else {
				//위치를 변경함
				locationDTO.setEventId(newEventId);
				locationViewModel.addLocation(locationDTO, locationDbQueryCallback);
			}
		}
	}

	//이번 일정을 포함한 이후 모든 일정 변경
	protected void updateAfterInstanceIncludingThisInstance() {
		/*
		이벤트의 반복 종료일을 수정한 인스턴스의 일정 종료일로 설정
		수정한 인스턴스를 새로운 인스턴스로 추가
		 */
		ContentValues modifiedInstance = eventDataViewModel.getNEW_EVENT();
		List<ContentValues> modifiedReminderList = eventDataViewModel.getREMINDERS();
		List<ContentValues> modifiedAttendeeList = eventDataViewModel.getATTENDEES();

		final long eventId = originalInstance.getAsInteger(CalendarContract.Instances.EVENT_ID);
		RecurrenceRule recurrenceRule = new RecurrenceRule();
		recurrenceRule.separateValues(originalInstance.getAsString(CalendarContract.Instances.RRULE));

		final int originalCount = Integer.parseInt(recurrenceRule.getValue(RecurrenceRule.COUNT));
		final long instanceId = originalInstance.getAsLong(CalendarContract.Instances._ID);
		int instanceCount = 0;

		String[] projection = {CalendarContract.Instances._ID, CalendarContract.Instances.EVENT_ID};
		Cursor cursor = CalendarContract.Instances.query(getContext().getContentResolver(), projection,
				originalInstance.getAsLong(CalendarContract.Instances.DTSTART),
				originalInstance.getAsLong(CalendarContract.Instances.END));

		while (cursor.moveToNext()) {
			if (cursor.getLong(1) == eventId) {
				instanceCount++;
			}
			if (cursor.getLong(0) == instanceId) {
				break;
			}
		}

		cursor.close();

		//특정 날짜까지 반복인 경우
		if (recurrenceRule.containsKey(RecurrenceRule.UNTIL)) {
			//수정한 인스턴스의 종료일 가져오기
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(modifiedInstance.getAsLong(CalendarContract.Instances.BEGIN));
			final Date beginOfModifiedInstance = calendar.getTime();

			//기존 이벤트의 반복 종료일을 수정한 인스턴스의 종료일로 설정
			//기존 이벤트의 rrule을 수정
			recurrenceRule.putValue(RecurrenceRule.UNTIL, ClockUtil.yyyyMMdd.format(beginOfModifiedInstance));
		}//n회 반복 이벤트인 경우 or 계속 반복인 경우
		else {
			int newCount = instanceCount - 1;
			if (newCount == 0) {
				//remove recurrence
				recurrenceRule.clear();
			} else {
				recurrenceRule.putValue(RecurrenceRule.COUNT, newCount);
			}
		}

		ContentValues originalEventValues = new ContentValues();
		if (!recurrenceRule.isEmpty()) {
			originalEventValues.put(CalendarContract.Events.RRULE, recurrenceRule.getRule());
		}
		getContext().getContentResolver().update(ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId),
				originalEventValues, null, null);

		//수정된 인스턴스를 새로운 이벤트로 저장
		if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			return;
		}

		ContentValues newEventValues = new ContentValues();

		/*
			title, calendarId, allDay, dtStart, dtEnd, eventTimeZone,
		rrule, reminders, description, eventLocation, attendees,
		guestCan~~ 3개, availability, accessLevel
		 */

		setNewEventValues(CalendarContract.Events.TITLE, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.EVENT_COLOR_KEY, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.EVENT_COLOR, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.CALENDAR_ID, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.ALL_DAY, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.DTSTART, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.DTEND, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.EVENT_TIMEZONE, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.DESCRIPTION, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.EVENT_LOCATION, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.AVAILABILITY, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.ACCESS_LEVEL, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.GUESTS_CAN_MODIFY, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, newEventValues, modifiedInstance);


		//rrule 수정
		if (originalInstance.getAsString(CalendarContract.Instances.RRULE) != null && modifiedInstance.containsKey(CalendarContract.Events.RRULE)) {
			RecurrenceRule newEventRrule = new RecurrenceRule();
			newEventRrule.separateValues(originalInstance.getAsString(CalendarContract.Instances.RRULE));

			//until이 이벤트의 종료 날짜 이전인 경우 - 반복 삭제
			if (newEventRrule.containsKey(RecurrenceRule.UNTIL)) {
				String until = newEventRrule.getValue(RecurrenceRule.UNTIL);

				Calendar untilCalendar = EventRecurrenceFragment.convertDate(until);
				if (untilCalendar.getTime().before(new Date(newEventValues.getAsLong(CalendarContract.Events.DTEND)))) {
					recurrenceRule.clear();
				}
			} else if (newEventRrule.containsKey(RecurrenceRule.COUNT)) {
				int count = originalCount - instanceCount;
				if (count == 0) {
					//remove recurrence
					recurrenceRule.clear();
				} else {
					recurrenceRule.putValue(RecurrenceRule.COUNT, count);
				}
			}

			if (!recurrenceRule.isEmpty()) {
				newEventValues.put(CalendarContract.Events.RRULE, recurrenceRule.getRule());
			} else {
				newEventValues.remove(CalendarContract.Events.RRULE);
			}
		} else if (modifiedInstance.containsKey(CalendarContract.Events.RRULE)) {
			newEventValues.put(CalendarContract.Events.RRULE, modifiedInstance.getAsString(CalendarContract.Events.RRULE));

		}
		Uri uri = getContext().getContentResolver().insert(CalendarContract.Events.CONTENT_URI, newEventValues);
		final long newEventId = Long.parseLong(uri.getLastPathSegment());

		// 알람 목록 갱신
		if (!modifiedReminderList.isEmpty()) {
			for (ContentValues reminder : modifiedReminderList) {
				reminder.put(CalendarContract.Reminders.EVENT_ID, newEventId);
			}
			calendarViewModel.addReminders(modifiedReminderList);
		}

		if (!modifiedAttendeeList.isEmpty()) {
			for (ContentValues addedAttendee : modifiedAttendeeList) {
				addedAttendee.put(CalendarContract.Attendees.EVENT_ID, newEventId);
			}
			calendarViewModel.addAttendees(modifiedAttendeeList);
		}

		if (newEventValues.containsKey(CalendarContract.Events.EVENT_LOCATION)) {
			if (locationDTO == null) {
				//위치를 바꾸지 않고, 기존 이벤트의 값을 그대로 유지
				locationViewModel.getLocation(eventId, new DbQueryCallback<LocationDTO>() {
					@Override
					public void onResultSuccessful(LocationDTO savedLocationDto) {
						savedLocationDto.setEventId(newEventId);
						locationViewModel.addLocation(savedLocationDto, locationDbQueryCallback);
					}

					@Override
					public void onResultNoData() {
						onModifyInstanceResultListener.onResultModifiedAfterAllInstancesIncludingThisInstance();
						getParentFragmentManager().popBackStack();
					}
				});
			} else {
				//위치를 변경함
				locationDTO.setEventId(newEventId);
				locationViewModel.addLocation(locationDTO, locationDbQueryCallback);
			}
		}
	}

	private final DbQueryCallback<LocationDTO> locationDbQueryCallback = new DbQueryCallback<LocationDTO>() {
		@Override
		public void onResultSuccessful(LocationDTO result) {
			onModifyInstanceResultListener.onResultModifiedAfterAllInstancesIncludingThisInstance();
			getParentFragmentManager().popBackStack();
		}

		@Override
		public void onResultNoData() {

		}
	};

	//모든 일정 변경
	protected void updateEvent() {
		/*
		수정가능한 column :
		title, calendarId, allDay, dtStart, dtEnd, eventTimeZone,
		rrule, reminders, description, eventLocation, attendees,
		guestCan~~ 3개, availability, accessLevel
		 */
		ContentValues modifiedEvent = eventDataViewModel.getNEW_EVENT();
		List<ContentValues> reminderList = eventDataViewModel.getREMINDERS();
		List<ContentValues> attendeeList = eventDataViewModel.getATTENDEES();

		//recurrence가 계속 반복이면 dtend변경하지 않는다
		if (modifiedEvent.containsKey(CalendarContract.Events.RRULE) || originalInstance.containsKey(CalendarContract.Instances.RRULE)) {
			RecurrenceRule recurrenceRule = new RecurrenceRule();

			if (originalInstance.getAsString(CalendarContract.Instances.RRULE) != null) {
				recurrenceRule.separateValues(originalInstance.getAsString(CalendarContract.Instances.RRULE));

				if (!recurrenceRule.containsKey(RecurrenceRule.UNTIL) && !recurrenceRule.containsKey(RecurrenceRule.COUNT)) {
					modifiedEvent.remove(CalendarContract.Events.DTEND);
				}
			} else if (modifiedEvent.containsKey(CalendarContract.Events.RRULE)) {
				recurrenceRule.separateValues(modifiedEvent.getAsString(CalendarContract.Events.RRULE));

				if (!recurrenceRule.containsKey(RecurrenceRule.UNTIL) && !recurrenceRule.containsKey(RecurrenceRule.COUNT)) {
					modifiedEvent.remove(CalendarContract.Events.DTEND);
				}
			}
		}

		final long eventId = originalInstance.getAsInteger(CalendarContract.Instances.EVENT_ID);
		modifiedEvent.put(CalendarContract.Events._ID, eventId);

		calendarViewModel.updateEvent(modifiedEvent);

		// 알람 목록 갱신
		calendarViewModel.deleteAllReminders(eventId);
		if (!reminderList.isEmpty()) {
			for (ContentValues reminder : reminderList) {
				reminder.put(CalendarContract.Reminders.EVENT_ID, eventId);
			}
			calendarViewModel.addReminders(reminderList);
		}

		// 참석자 목록 갱신
		calendarViewModel.deleteAllAttendees(eventId);
		if (!attendeeList.isEmpty()) {
			for (ContentValues addedAttendee : attendeeList) {
				addedAttendee.put(CalendarContract.Attendees.EVENT_ID, eventId);
			}
			calendarViewModel.addAttendees(attendeeList);
		}

		locationViewModel.removeLocation(eventId, null);

		if (modifiedEvent.containsKey(CalendarContract.Events.EVENT_LOCATION)) {
			locationDTO.setEventId(eventId);
			locationViewModel.addLocation(locationDTO, new DbQueryCallback<LocationDTO>() {
				@Override
				public void onResultSuccessful(LocationDTO result) {
					onModifyInstanceResultListener.onResultModifiedEvent(modifiedEvent.getAsLong(CalendarContract.Events._ID), modifiedEvent.getAsLong(CalendarContract.Events.DTSTART));
					getParentFragmentManager().popBackStack();
				}

				@Override
				public void onResultNoData() {

				}
			});
		} else {
			onModifyInstanceResultListener.onResultModifiedEvent(modifiedEvent.getAsLong(CalendarContract.Events._ID), modifiedEvent.getAsLong(CalendarContract.Events.DTSTART));
			getParentFragmentManager().popBackStack();
		}


	}

	private void setNewEventValues(String key, ContentValues newEventValues, ContentValues modifiedInstance) {
		if (modifiedInstance.containsKey(key)) {
			newEventValues.put(key, modifiedInstance.getAsString(key));
		} else if (originalInstance.containsKey(key)) {
			newEventValues.put(key, originalInstance.getAsString(key));
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

	public interface OnModifyInstanceResultListener {

		void onResultModifiedEvent(long eventId, long begin);

		void onResultModifiedThisInstance();

		void onResultModifiedAfterAllInstancesIncludingThisInstance();
	}
}
