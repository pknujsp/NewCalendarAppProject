package com.zerodsoft.scheduleweather.activity.editevent.activity;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.AsyncQueryService;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendar.EventHelper;
import com.zerodsoft.scheduleweather.calendar.calendarcommon2.EventRecurrence;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import biweekly.property.RecurrenceRule;

public class ModifyInstanceFragment extends EventBaseFragment {
	private OnModifyInstanceResultListener onModifyInstanceResultListener;
	private ContentValues originalInstance;

	private long originalInstanceBeginDate;
	private long originalInstanceEndDate;
	private boolean firstModifiedDateTime = true;

	private long modifiedDtStart;
	private long modifiedDtEnd;

	private List<ContentValues> originalReminderList;
	private List<ContentValues> originalAttendeeList;

	protected AsyncQueryService mService;

	public synchronized AsyncQueryService getAsyncQueryService() {
		if (mService == null) {
			mService = new AsyncQueryService(getActivity());
		}
		return mService;
	}

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
	protected void setOriginalMainFragmentTitle() {
		binding.fragmentTitle.setText(R.string.modify_event);
	}

	@Override
	public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.fragmentTitle.setText(R.string.modify_event);

		binding.reminderLayout.notReminder.setVisibility(View.GONE);
		binding.descriptionLayout.notDescription.setVisibility(View.GONE);
		binding.attendeeLayout.notAttendees.setVisibility(View.GONE);

		setViewOnClickListeners();
		loadInitData();

		initializing = false;
		binding.saveBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
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
											//모든 일정이면 event를 변경
											updateEvent();
											break;
										case 1:
											//현재 인스턴스 이후의 모든 인스턴스 변경
											updateFollowingEvents();
											break;
										case 2:
											//현재 인스턴스만 변경
											updateThisInstance();
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


	protected void setViewOnClickListeners() {
        /*
        event color
         */
		binding.titleLayout.eventColor.setOnClickListener(new View.OnClickListener() {
			boolean firstClicked = true;

			@Override
			public void onClick(View view) {
				String accountName = selectedCalendarValues.getAsString(CalendarContract.Calendars.ACCOUNT_NAME);
				List<ContentValues> colors = calendarViewModel.getEventColors(accountName);
				String colorKey;

				if (firstClicked) {
					firstClicked = false;
					colorKey = originalInstance.getAsString(CalendarContract.Instances.EVENT_COLOR_KEY);
				} else {
					colorKey = eventDataViewModel.getNEW_EVENT().getAsString(CalendarContract.Events.EVENT_COLOR_KEY);
				}

				onClickedEventColor(colorKey, colors);
			}
		});

        /*
        시간 allday 스위치
         */

		binding.timeLayout.timeAlldaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			boolean firstChecked = true;

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				onCheckedAllDaySwitch(isChecked);

				if (firstChecked && !initializing) {
					firstChecked = false;
					if (originalInstance.getAsInteger(Events.ALL_DAY) == 1) {
						TimeZone timeZone = TimeZone.getTimeZone(originalInstance.getAsString(Events.CALENDAR_TIME_ZONE));
						eventDataViewModel.setTimezone(timeZone.getID());
						setTimeZoneText(timeZone.getID());
					}

					if (firstModifiedDateTime) {
						firstModifiedDateTime = false;
						firstModifiedDateTime();
					}
				}
			}
		});

        /*
        시간대
         */
		binding.timeLayout.eventTimezone.setOnClickListener(view ->
		{
			onClickedTimeZone();
		});

        /*
        반복
         */
		binding.recurrenceLayout.eventRecurrence.setOnClickListener(view ->
		{
			// 반복 룰과 이벤트의 시작 시간 전달
			String rRule = null;

			if (eventDataViewModel.getNEW_EVENT().containsKey(CalendarContract.Events.RRULE)) {
				rRule = eventDataViewModel.getNEW_EVENT().getAsString(CalendarContract.Events.RRULE);
			} else if (originalInstance.getAsString(CalendarContract.Instances.RRULE) != null) {
				rRule = originalInstance.getAsString(CalendarContract.Instances.RRULE);
			} else {
				rRule = "";
			}

			long dtStart = 0L;
			if (eventDataViewModel.getNEW_EVENT().containsKey(CalendarContract.Events.DTSTART)) {
				dtStart = eventDataViewModel.getNEW_EVENT().getAsLong(CalendarContract.Events.DTSTART);
			} else {
				dtStart = originalInstanceBeginDate;
			}

			onClickedRecurrence(rRule, dtStart);
		});

        /*
        접근수준
         */
		binding.accesslevelLayout.eventAccessLevel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int accessLevel = 0;

				if (eventDataViewModel.getNEW_EVENT().containsKey(CalendarContract.Events.ACCESS_LEVEL)) {
					accessLevel = eventDataViewModel.getNEW_EVENT().getAsInteger(CalendarContract.Events.ACCESS_LEVEL);
				} else {
					accessLevel = originalInstance.getAsInteger(CalendarContract.Instances.ACCESS_LEVEL);
				}

				onClickedAccessLevel(accessLevel);
			}
		});

        /*
        유효성
         */
		binding.availabilityLayout.eventAvailability.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int available;

				if (eventDataViewModel.getNEW_EVENT().containsKey(CalendarContract.Events.AVAILABILITY)) {
					available = eventDataViewModel.getNEW_EVENT().getAsInteger(CalendarContract.Events.AVAILABILITY);
				} else {
					available = originalInstance.getAsInteger(CalendarContract.Instances.AVAILABILITY);
				}

				onClickedAvailable(available);
			}
		});

        /*
        캘린더 선택
         */
		binding.calendarLayout.eventCalendarValueView.setOnClickListener(view ->
		{
			onClickedCalendar();
		});

        /*
        알람
         */
		binding.reminderLayout.addReminderButton.setOnClickListener(view ->
		{
			onClickedNewReminder();
		});


        /*
        위치
         */
		binding.locationLayout.eventLocation.setOnClickListener(view ->
		{
			String eventLocation = null;
			if (eventDataViewModel.getNEW_EVENT().containsKey(CalendarContract.Events.EVENT_LOCATION)) {
				eventLocation = eventDataViewModel.getNEW_EVENT().getAsString(CalendarContract.Events.EVENT_LOCATION);
			} else {
				if (originalInstance.getAsString(CalendarContract.Instances.EVENT_LOCATION) != null) {
					eventLocation = originalInstance.getAsString(CalendarContract.Instances.EVENT_LOCATION);
				}
			}
			onClickedLocation(eventLocation);
		});

        /*
        참석자 상세정보 버튼
         */
		binding.attendeeLayout.showAttendeesDetail.setOnClickListener(new View.OnClickListener() {
			boolean firstClicked = true;

			@Override
			public void onClick(View view) {
				boolean guestsCanModify;
				boolean guestsCanInviteOthers;
				boolean guestsCanSeeGuests;

				if (firstClicked) {
					firstClicked = false;
					guestsCanModify = originalInstance.getAsInteger(CalendarContract.Instances.GUESTS_CAN_MODIFY) == 1;
					guestsCanInviteOthers = originalInstance.getAsInteger(CalendarContract.Instances.GUESTS_CAN_INVITE_OTHERS) == 1;
					guestsCanSeeGuests = originalInstance.getAsInteger(CalendarContract.Instances.GUESTS_CAN_SEE_GUESTS) == 1;
				} else {
					guestsCanModify = eventDataViewModel.getNEW_EVENT().getAsInteger(CalendarContract.Instances.GUESTS_CAN_MODIFY) == 1;
					guestsCanInviteOthers = eventDataViewModel.getNEW_EVENT().getAsInteger(CalendarContract.Instances.GUESTS_CAN_INVITE_OTHERS) == 1;
					guestsCanSeeGuests = eventDataViewModel.getNEW_EVENT().getAsInteger(CalendarContract.Instances.GUESTS_CAN_SEE_GUESTS) == 1;
				}

				Bundle bundle = new Bundle();
				bundle.putBoolean(CalendarContract.Events.GUESTS_CAN_MODIFY, guestsCanModify);
				bundle.putBoolean(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, guestsCanInviteOthers);
				bundle.putBoolean(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, guestsCanSeeGuests);

				onClickedAttendeeList(bundle);
			}
		});
	}


	@Override
	protected final void initDatePicker() {
		long dtStart = 0L;
		long dtEnd = 0L;

		if (eventDataViewModel.getNEW_EVENT().containsKey(CalendarContract.Events.DTSTART)) {
			dtStart = eventDataViewModel.getNEW_EVENT().getAsLong(CalendarContract.Events.DTSTART);
		} else {
			dtStart = originalInstanceBeginDate;
		}

		if (eventDataViewModel.getNEW_EVENT().containsKey(CalendarContract.Events.DTEND)) {
			dtEnd = eventDataViewModel.getNEW_EVENT().getAsLong(CalendarContract.Events.DTEND);
		} else {
			dtEnd = originalInstanceEndDate;
		}

		showDatePicker(dtStart, dtEnd, new OnModifiedDateTimeCallback() {
			@Override
			public void onModified() {
				if (firstModifiedDateTime) {
					firstModifiedDateTime = false;
					firstModifiedDateTime();
				}
			}
		});
	}

	@Override
	protected void initTimePicker(DateTimeType dateType) {
		Calendar calendar = Calendar.getInstance();
		Calendar compareCalendar = Calendar.getInstance();

		if (dateType == DateTimeType.START) {
			if (eventDataViewModel.getNEW_EVENT().containsKey(CalendarContract.Events.DTSTART)) {
				calendar.setTimeInMillis(eventDataViewModel.getNEW_EVENT().getAsLong(CalendarContract.Events.DTSTART));
			} else {
				calendar.setTimeInMillis(originalInstanceBeginDate);
			}
		} else if (dateType == DateTimeType.END) {
			if (eventDataViewModel.getNEW_EVENT().containsKey(CalendarContract.Events.DTEND)) {
				calendar.setTimeInMillis(eventDataViewModel.getNEW_EVENT().getAsLong(CalendarContract.Events.DTEND));
			} else {
				calendar.setTimeInMillis(originalInstanceEndDate);
			}
		}

		if (dateType == DateTimeType.START) {
			if (eventDataViewModel.getNEW_EVENT().containsKey(CalendarContract.Events.DTEND)) {
				compareCalendar.setTimeInMillis(eventDataViewModel.getNEW_EVENT().getAsLong(CalendarContract.Events.DTEND));
			} else {
				compareCalendar.setTimeInMillis(originalInstanceEndDate);
			}
		} else if (dateType == DateTimeType.END) {
			if (eventDataViewModel.getNEW_EVENT().containsKey(CalendarContract.Events.DTSTART)) {
				compareCalendar.setTimeInMillis(eventDataViewModel.getNEW_EVENT().getAsLong(CalendarContract.Events.DTSTART));
			} else {
				compareCalendar.setTimeInMillis(originalInstanceBeginDate);
			}
		}
		showTimePicker(dateType, calendar, compareCalendar, new OnModifiedDateTimeCallback() {
			@Override
			public void onModified() {
				if (firstModifiedDateTime) {
					firstModifiedDateTime = false;
					firstModifiedDateTime();
				}
			}
		});
	}

	private void firstModifiedDateTime() {
		if (!eventDataViewModel.getNEW_EVENT().containsKey(CalendarContract.Events.DTSTART)) {
			eventDataViewModel.setDtStart(new Date(originalInstanceBeginDate));
		}
		if (!eventDataViewModel.getNEW_EVENT().containsKey(CalendarContract.Events.DTEND)) {
			eventDataViewModel.setDtEnd(new Date(originalInstanceEndDate));
		}
		if (!eventDataViewModel.getNEW_EVENT().containsKey(CalendarContract.Events.ALL_DAY)) {
			eventDataViewModel.setIsAllDay(binding.timeLayout.timeAlldaySwitch.isChecked());
		}
	}

	private void loadInitData() {
		Bundle arguments = getArguments();

		final long eventId = arguments.getLong(CalendarContract.Instances.EVENT_ID, 0);
		final long instanceId = arguments.getLong(CalendarContract.Instances._ID, 0);
		final long begin = arguments.getLong(CalendarContract.Instances.BEGIN, 0);
		final long end = arguments.getLong(CalendarContract.Instances.END, 0);

		// 인스턴스, 알림을 가져온다
		originalInstance = calendarViewModel.getInstance(instanceId, begin, end);
		selectedCalendarValues =
				calendarViewModel.getCalendar(originalInstance.getAsInteger(CalendarContract.Instances.CALENDAR_ID));

		eventDataViewModel.getREMINDERS().addAll(calendarViewModel.getReminders(eventId));
		eventDataViewModel.getATTENDEES().addAll(calendarViewModel.getAttendeeListForEdit(eventId));

		originalAttendeeList = eventDataViewModel.getATTENDEES();
		originalReminderList = eventDataViewModel.getREMINDERS();

		if (!eventDataViewModel.getATTENDEES().isEmpty()) {
			createAttendeeListView();
		} else {
			// 참석자 버튼 텍스트 수정
			binding.attendeeLayout.showAttendeesDetail.setText(getString(R.string.add_attendee));
		}

		//제목, 캘린더, 시간, 시간대, 반복, 알림, 설명, 위치, 공개범위, 유효성, 참석자
		//알림, 참석자 정보는 따로 불러온다.

		if (originalInstance.containsKey(CalendarContract.Events.EVENT_COLOR)) {
			binding.titleLayout.eventColor.setBackgroundColor(EventUtil.getColor(originalInstance.getAsInteger(CalendarContract.Instances.EVENT_COLOR)));
		}
		//제목
		binding.titleLayout.title.setText(originalInstance.getAsString(CalendarContract.Instances.TITLE));

		// allday switch
		final boolean isAllDay = originalInstance.getAsInteger(CalendarContract.Instances.ALL_DAY) == 1;
		binding.timeLayout.timeAlldaySwitch.setChecked(isAllDay);

		if (isAllDay) {
			int startDay = originalInstance.getAsInteger(CalendarContract.Instances.START_DAY);
			int endDay = originalInstance.getAsInteger(CalendarContract.Instances.END_DAY);
			final int dayDifference = endDay - startDay;

			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			calendar.setTimeInMillis(originalInstance.getAsLong(CalendarContract.Instances.BEGIN));

			setTimeZoneText(originalInstance.getAsString(CalendarContract.Events.CALENDAR_TIME_ZONE));

			originalInstanceBeginDate = calendar.getTimeInMillis();
			setDateText(DateTimeType.START, calendar.getTime().getTime());
			setTimeText(DateTimeType.START, calendar.getTime().getTime());

			calendar.add(Calendar.DAY_OF_YEAR, dayDifference);

			originalInstanceEndDate = calendar.getTimeInMillis();
			setDateText(DateTimeType.END, calendar.getTime().getTime());
			setTimeText(DateTimeType.END, calendar.getTime().getTime());
		} else {
			setTimeZoneText(originalInstance.getAsString(CalendarContract.Events.EVENT_TIMEZONE));

			setDateText(DateTimeType.START, originalInstance.getAsLong(CalendarContract.Instances.BEGIN));
			setDateText(DateTimeType.END, originalInstance.getAsLong(CalendarContract.Instances.END));
			setTimeText(DateTimeType.START, originalInstance.getAsLong(CalendarContract.Instances.BEGIN));
			setTimeText(DateTimeType.END, originalInstance.getAsLong(CalendarContract.Instances.END));

			originalInstanceBeginDate = originalInstance.getAsLong(CalendarContract.Instances.BEGIN);
			originalInstanceEndDate = originalInstance.getAsLong(CalendarContract.Instances.END);
		}

		//캘린더
		setCalendarText(originalInstance.getAsInteger(CalendarContract.Instances.CALENDAR_COLOR),
				originalInstance.getAsString(CalendarContract.Instances.CALENDAR_DISPLAY_NAME),
				selectedCalendarValues.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));

		// 반복
		if (originalInstance.getAsString(CalendarContract.Instances.RRULE) != null) {
			setRecurrenceText(originalInstance.getAsString(CalendarContract.Instances.RRULE), originalInstanceBeginDate);
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
		exceptionEvent.put(CalendarContract.Events.ORIGINAL_SYNC_ID, originalInstance.getAsString(CalendarContract.Instances.ORIGINAL_SYNC_ID));
		exceptionEvent.put(CalendarContract.Events.STATUS, CalendarContract.Events.STATUS_CANCELED);
		exceptionEvent.put(CalendarContract.Events.ORIGINAL_ALL_DAY, originalInstance.getAsInteger(CalendarContract.Instances.ALL_DAY));

		Uri exceptionUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_EXCEPTION_URI,
				originalInstance.getAsLong(CalendarContract.Instances.EVENT_ID));
		Uri result = getContext().getContentResolver().insert(exceptionUri, exceptionEvent);

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

		if (newEventValues.getAsInteger(CalendarContract.Events.ALL_DAY) == 1) {
			convertDtEndForAllDay(newEventValues);
		}

		if (modifiedInstance.containsKey(CalendarContract.Events.RRULE)) {
			newEventValues.put(CalendarContract.Events.RRULE, modifiedInstance.getAsString(CalendarContract.Events.RRULE));
		}

		if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
			return;
		}

		Uri uri = getContext().getContentResolver().insert(CalendarContract.Events.CONTENT_URI, newEventValues);
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
								locationViewModel.addLocation(savedLocationDto, new DbQueryCallback<LocationDTO>() {
									@Override
									public void onResultSuccessful(LocationDTO result) {
										requireActivity().runOnUiThread(new Runnable() {
											@Override
											public void run() {
												onModifyInstanceResultListener.onResultModifiedThisInstance(newEventId,
														newEventValues.getAsLong(CalendarContract.Events.DTSTART));
												getParentFragmentManager().popBackStackImmediate();
											}
										});
									}

									@Override
									public void onResultNoData() {

									}
								});
							}

							@Override
							public void onResultNoData() {
								requireActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										onModifyInstanceResultListener.onResultModifiedThisInstance(newEventId,
												newEventValues.getAsLong(CalendarContract.Events.DTSTART));
										getParentFragmentManager().popBackStackImmediate();
									}
								});

							}
						});
			} else {
				//위치를 변경함
				locationDTO.setEventId(newEventId);
				locationViewModel.addLocation(locationDTO, new DbQueryCallback<LocationDTO>() {
					@Override
					public void onResultSuccessful(LocationDTO result) {
						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								onModifyInstanceResultListener.onResultModifiedThisInstance(newEventId,
										newEventValues.getAsLong(CalendarContract.Events.DTSTART));
								getParentFragmentManager().popBackStackImmediate();
							}
						});

					}

					@Override
					public void onResultNoData() {

					}
				});
			}
		}
	}


	//이번 일정을 포함한 이후 모든 일정 변경
	protected void updateFollowingEvents() {
		/*
		이벤트의 반복 종료일을 수정한 인스턴스의 일정 종료일로 설정
		수정한 인스턴스를 새로운 인스턴스로 추가
		 */
		ContentValues newEvent = eventDataViewModel.getNEW_EVENT();
		List<ContentValues> newReminderList = eventDataViewModel.getREMINDERS();
		List<ContentValues> newAttendeeList = eventDataViewModel.getATTENDEES();

		ContentValues newEventValues = new ContentValues();
		/*
			title, calendarId, allDay, dtStart, dtEnd, eventTimeZone,
		rrule, reminders, description, eventLocation, attendees,
		guestCan~~ 3개, availability, accessLevel
		 */
		setNewEventValues(CalendarContract.Events.TITLE, newEventValues, newEvent);
		setNewEventValues(CalendarContract.Events.EVENT_COLOR_KEY, newEventValues, newEvent);
		setNewEventValues(CalendarContract.Events.EVENT_COLOR, newEventValues, newEvent);
		setNewEventValues(CalendarContract.Events.CALENDAR_ID, newEventValues, newEvent);
		setNewEventValues(CalendarContract.Events.ALL_DAY, newEventValues, newEvent);
		setNewEventValues(CalendarContract.Events.EVENT_TIMEZONE, newEventValues, newEvent);
		setNewEventValues(CalendarContract.Events.DESCRIPTION, newEventValues, newEvent);
		setNewEventValues(CalendarContract.Events.EVENT_LOCATION, newEventValues, newEvent);
		setNewEventValues(CalendarContract.Events.AVAILABILITY, newEventValues, newEvent);
		setNewEventValues(CalendarContract.Events.ACCESS_LEVEL, newEventValues, newEvent);
		setNewEventValues(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, newEventValues, newEvent);
		setNewEventValues(CalendarContract.Events.GUESTS_CAN_MODIFY, newEventValues, newEvent);
		setNewEventValues(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, newEventValues, newEvent);
		setNewEventValues(CalendarContract.Events.RRULE, newEventValues, newEvent);
		setNewEventValues(CalendarContract.Events.IS_ORGANIZER, newEventValues, newEvent);
		newEventValues.put(CalendarContract.Events._ID, originalInstance.getAsLong(CalendarContract.Instances.EVENT_ID));

		if (newEvent.containsKey(CalendarContract.Events.DTSTART)) {
			newEventValues.put(CalendarContract.Events.DTSTART, newEvent.getAsLong(CalendarContract.Events.DTSTART));
			newEventValues.put(CalendarContract.Events.DTEND, newEvent.getAsLong(CalendarContract.Events.DTEND));
		} else {
			newEventValues.put(CalendarContract.Events.DTSTART, originalInstanceBeginDate);
			newEventValues.put(CalendarContract.Events.DTEND, originalInstanceEndDate);
		}

		if (newEventValues.getAsInteger(CalendarContract.Events.ALL_DAY) == 1) {
			convertDtEndForAllDay(newEventValues);
		}

		EventHelper eventHelper = new EventHelper(getAsyncQueryService());
		eventHelper.saveFollowingEvents(originalInstance, newEventValues, originalReminderList
				, originalAttendeeList, newReminderList, newAttendeeList, selectedCalendarValues);

		/*
		if (newEventValues.containsKey(CalendarContract.Events.EVENT_LOCATION)) {
			if (locationDTO != null) {
				//위치를 변경함
				locationDTO.setEventId(newEventId);
				locationViewModel.addLocation(locationDTO, new DbQueryCallback<LocationDTO>() {
					@Override
					public void onResultSuccessful(LocationDTO result) {
						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								onModifyInstanceResultListener.onResultModifiedAfterAllInstancesIncludingThisInstance(newEventId,
										newEventValues.getAsLong(CalendarContract.Events.DTSTART));
								getParentFragmentManager().popBackStackImmediate();
							}
						});
					}

					@Override
					public void onResultNoData() {

					}
				});
			} else {
				onModifyInstanceResultListener.onResultModifiedAfterAllInstancesIncludingThisInstance(newEventId,
						newEventValues.getAsLong(CalendarContract.Events.DTSTART));
				getParentFragmentManager().popBackStackImmediate();
			}
		} else {
			onModifyInstanceResultListener.onResultModifiedAfterAllInstancesIncludingThisInstance(newEventId,
					newEventValues.getAsLong(CalendarContract.Events.DTSTART));
			getParentFragmentManager().popBackStackImmediate();
		}

		 */
	}


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
			EventRecurrence eventRecurrence = new EventRecurrence();


			if (originalInstance.getAsString(CalendarContract.Instances.RRULE) != null) {
				eventRecurrence.parse(originalInstance.getAsString(CalendarContract.Instances.RRULE));

				if (eventRecurrence.until == null && eventRecurrence.count == 0) {
					modifiedEvent.remove(CalendarContract.Events.DTEND);
				}
			} else if (modifiedEvent.containsKey(CalendarContract.Events.RRULE)) {
				eventRecurrence.parse(modifiedEvent.getAsString(CalendarContract.Instances.RRULE));

				if (eventRecurrence.until == null && eventRecurrence.count == 0) {
					modifiedEvent.remove(CalendarContract.Events.DTEND);
				}
			}
		}

		if (modifiedEvent.containsKey(CalendarContract.Events.ALL_DAY)) {
			if (modifiedEvent.getAsInteger(CalendarContract.Events.ALL_DAY) == 1) {
				convertDtEndForAllDay(modifiedEvent);
			}
		}

		final long eventId = originalInstance.getAsInteger(CalendarContract.Instances.EVENT_ID);
		modifiedEvent.put(CalendarContract.Events._ID, eventId);
		modifiedEvent.remove(CalendarContract.Events.DURATION);

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
			if (originalInstance.getAsString(CalendarContract.Events.EVENT_LOCATION) == null) {

			} else {
				locationViewModel.removeLocation(eventId, null);
			}

			locationDTO.setEventId(eventId);
			locationViewModel.addLocation(locationDTO, new DbQueryCallback<LocationDTO>() {
				@Override
				public void onResultSuccessful(LocationDTO result) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							onModifyInstanceResultListener.onResultModifiedEvent(modifiedEvent.getAsLong(CalendarContract.Events.DTSTART));
							getParentFragmentManager().popBackStackImmediate();
						}
					});

				}

				@Override
				public void onResultNoData() {

				}
			});
		} else {
			onModifyInstanceResultListener.onResultModifiedEvent(modifiedEvent.getAsLong(CalendarContract.Events.DTSTART));
			getParentFragmentManager().popBackStackImmediate();
		}
	}

	private void setNewEventValues(String key, ContentValues newEventValues, ContentValues modifiedInstance) {
		if (eventDataViewModel.getAddedValueSet().contains(key)) {
			newEventValues.put(key, modifiedInstance.getAsString(key));
		} else if (eventDataViewModel.getRemovedValueSet().contains(key)) {

		} else {
			newEventValues.put(key, originalInstance.getAsString(key));
		}
	}


	public interface OnModifyInstanceResultListener {

		void onResultModifiedEvent(long begin);

		void onResultModifiedThisInstance(long eventId, long begin);

		void onResultModifiedAfterAllInstancesIncludingThisInstance(long eventId, long begin);
	}

	public interface OnModifiedDateTimeCallback {
		void onModified();
	}
}
