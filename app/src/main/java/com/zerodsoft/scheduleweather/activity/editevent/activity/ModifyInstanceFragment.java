package com.zerodsoft.scheduleweather.activity.editevent.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

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
				if (eventDataViewModel.getNEW_EVENT().getAsString(CalendarContract.Instances.RRULE) != null) {
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
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
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
		binding.timeLayout.timeAlldaySwitch.setChecked(isAllDay);

		if (originalInstance.getAsString(CalendarContract.Instances.EVENT_TIMEZONE) != null) {
			eventDataViewModel.setTimezone(originalInstance.getAsString(CalendarContract.Instances.EVENT_TIMEZONE));
		}
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


	protected void updateThisInstance() {
		final long originalEventId = eventDataViewModel.getNEW_EVENT().getAsLong(CalendarContract.Instances.EVENT_ID);

		ContentValues modifiedInstance = eventDataViewModel.getNEW_EVENT();
		ContentValues newEvent = new ContentValues();
		newEvent.putAll(modifiedInstance);

		//인스턴스를 이벤트에서 제외
		calendarViewModel.deleteInstance(eventDataViewModel.getNEW_EVENT().getAsLong(CalendarContract.Instances.BEGIN), originalEventId);

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
					onModifyInstanceResultListener.onResultModifiedThisInstance();
					getParentFragmentManager().popBackStack();
				}

				@Override
				public void onResultNoData() {

				}
			});
		}

		onModifyInstanceResultListener.onResultModifiedThisInstance();
		getParentFragmentManager().popBackStack();
	}

	protected void updateAfterInstanceIncludingThisInstance() {
		ContentValues event = eventDataViewModel.getNEW_EVENT();
		calendarViewModel.updateAllFutureInstances(event, eventDataViewModel.getNEW_EVENT());

		onModifyInstanceResultListener.onResultModifiedAfterAllInstancesIncludingThisInstance();
		getParentFragmentManager().popBackStack();
	}

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

		final long eventId = originalInstance.getAsInteger(CalendarContract.Instances.EVENT_ID);

		modifiedEvent.put(CalendarContract.Events._ID, eventId);
		if (!modifiedEvent.containsKey(CalendarContract.Events.CALENDAR_ID)) {
			modifiedEvent.put(CalendarContract.Events._ID, originalInstance.getAsLong(CalendarContract.Instances.EVENT_ID));
		}

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

	public interface OnModifyInstanceResultListener {

		void onResultModifiedEvent(long eventId, long begin);

		void onResultModifiedThisInstance();

		void onResultModifiedAfterAllInstancesIncludingThisInstance();
	}
}
