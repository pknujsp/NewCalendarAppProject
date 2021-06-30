package com.zerodsoft.scheduleweather.activity.editevent.activity;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.text.util.Rfc822Token;
import android.text.util.Rfc822Tokenizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Colors;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;

import com.android.calendarcommon2.DateException;
import com.android.calendarcommon2.EventRecurrence;
import com.android.calendarcommon2.RecurrenceProcessor;
import com.android.calendarcommon2.RecurrenceSet;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import biweekly.property.Attendee;

public class ModifyInstanceFragment extends EventBaseFragment {
	private OnModifyInstanceResultListener onModifyInstanceResultListener;
	private ContentValues originalInstance;

	private long originalInstanceBeginDate;
	private long originalInstanceEndDate;
	private boolean firstModifiedDateTime = true;

	private long modifiedDtStart;
	private long modifiedDtEnd;

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
		binding.fragmentTitle.setText(R.string.modify_event);
		binding.backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getParentFragmentManager().popBackStackImmediate();
			}
		});

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
											updateAfterInstanceIncludingThisInstance();
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
					if (originalInstance.getAsInteger(CalendarContract.Instances.ALL_DAY) == 1) {
						TimeZone timeZone = TimeZone.getTimeZone(originalInstance.getAsString(CalendarContract.Instances.CALENDAR_TIME_ZONE));
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

		if (!eventDataViewModel.getATTENDEES().isEmpty()) {
			createAttendeeListView();
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

	private void checkTimeDependentFields() {
		long oldBegin = originalInstanceBeginDate;
		long oldEnd = originalInstanceEndDate;
		boolean oldAllDay = originalInstance.getAsInteger(CalendarContract.Events.ALL_DAY) == 1;
		String oldRrule = originalInstance.getAsString(CalendarContract.Events.RRULE);
		String oldTimezone = originalInstance.getAsString(CalendarContract.Events.EVENT_TIMEZONE);

		long newBegin = model.mStart;
		long newEnd = model.mEnd;
		boolean newAllDay = model.mAllDay;
		String newRrule = model.mRrule;
		String newTimezone = model.mTimezone;

		// If none of the time-dependent fields changed, then remove them.
		if (oldBegin == newBegin && oldEnd == newEnd && oldAllDay == newAllDay
				&& TextUtils.equals(oldRrule, newRrule)
				&& TextUtils.equals(oldTimezone, newTimezone)) {
			values.remove(Events.DTSTART);
			values.remove(Events.DTEND);
			values.remove(Events.DURATION);
			values.remove(Events.ALL_DAY);
			values.remove(Events.RRULE);
			values.remove(Events.EVENT_TIMEZONE);
			return;
		}

		if (TextUtils.isEmpty(oldRrule) || TextUtils.isEmpty(newRrule)) {
			return;
		}

		// If we are modifying all events then we need to set DTSTART to the
		// start time of the first event in the series, not the current
		// date and time. If the start time of the event was changed
		// (from, say, 3pm to 4pm), then we want to add the time difference
		// to the start time of the first event in the series (the DTSTART
		// value). If we are modifying one instance or all following instances,
		// then we leave the DTSTART field alone.
		if (modifyWhich == MODIFY_ALL) {
			long oldStartMillis = originalModel.mStart;
			if (oldBegin != newBegin) {
				// The user changed the start time of this event
				long offset = newBegin - oldBegin;
				oldStartMillis += offset;
			}
			if (newAllDay) {
				Time time = new Time(Time.TIMEZONE_UTC);
				time.set(oldStartMillis);
				time.hour = 0;
				time.minute = 0;
				time.second = 0;
				oldStartMillis = time.toMillis(false);
			}
			values.put(Events.DTSTART, oldStartMillis);
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

		final long eventId = originalInstance.getAsLong(CalendarContract.Instances.EVENT_ID);

		EventRecurrence eventRecurrence = new EventRecurrence();

		if (modifiedInstance.containsKey(CalendarContract.Events.RRULE)) {
			if (modifiedInstance.getAsString(CalendarContract.Events.RRULE).isEmpty()) {
				// We've changed a recurring event to a non-recurring event.
				// If the event we are editing is the first in the series,
				// then delete the whole series. Otherwise, update the series
				// to end at the new start time.
				if (originalInstance.getAsLong(CalendarContract.Instances.DTSTART).equals(originalInstance.getAsLong(CalendarContract.Instances.BEGIN))) {
					//기존이벤트 삭제
					calendarViewModel.deleteEvent(eventId);
				} else {
					// Update the current repeating event to end at the new start time.  We
					// ignore the RRULE returned because the exception event doesn't want one.
					updateEvent();
					return;
				}
			}
		} else {
			if (originalInstance.getAsLong(CalendarContract.Instances.DTSTART).equals(originalInstance.getAsLong(CalendarContract.Instances.BEGIN))) {
				checkTimeDependentFields(originalModel, model, values, modifyWhich);
				ContentProviderOperation.Builder b = ContentProviderOperation.newUpdate(uri)
						.withValues(values);
				ops.add(b.build());
			} else {
				// We need to update the existing recurrence to end before the exception
				// event starts.  If the recurrence rule has a COUNT, we need to adjust
				// that in the original and in the exception.  This call rewrites the
				// original event's recurrence rule (in "ops"), and returns a new rule
				// for the exception.  If the exception explicitly set a new rule, however,
				// we don't want to overwrite it.
				String newRrule = updatePastEvents(ops, originalModel, model.mOriginalStart);
				if (model.mRrule.equals(originalModel.mRrule)) {
					values.put(Events.RRULE, newRrule);
				}

				// Create a new event with the user-modified fields
				eventIdIndex = ops.size();
				values.put(Events.STATUS, originalModel.mEventStatus);
				ops.add(ContentProviderOperation.newInsert(Events.CONTENT_URI).withValues(
						values).build());
			}
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
		setNewEventValues(CalendarContract.Events.EVENT_TIMEZONE, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.DESCRIPTION, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.EVENT_LOCATION, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.AVAILABILITY, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.ACCESS_LEVEL, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.GUESTS_CAN_MODIFY, newEventValues, modifiedInstance);
		setNewEventValues(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, newEventValues, modifiedInstance);

		if (modifiedInstance.containsKey(CalendarContract.Events.DTSTART)) {
			newEventValues.put(CalendarContract.Events.DTSTART, modifiedInstance.getAsLong(CalendarContract.Events.DTSTART));
			newEventValues.put(CalendarContract.Events.DTEND, modifiedInstance.getAsLong(CalendarContract.Events.DTEND));
		} else {
			newEventValues.put(CalendarContract.Events.DTSTART, originalInstanceBeginDate);
			newEventValues.put(CalendarContract.Events.DTEND, originalInstanceEndDate);
		}

		if (newEventValues.getAsInteger(CalendarContract.Events.ALL_DAY) == 1) {
			convertDtEndForAllDay(newEventValues);
		}
		//rrule 수정

		RecurrenceRule newEventRrule = new RecurrenceRule();

		if (modifiedInstance.containsKey(CalendarContract.Events.RRULE)) {
			newEventValues.put(CalendarContract.Events.RRULE, modifiedInstance.getAsString(CalendarContract.Events.RRULE));
		} else {
			//기존 이벤트의 rrule을 수정적용
			newEventRrule.separateValues(originalInstance.getAsString(CalendarContract.Instances.RRULE));

			//until이 이벤트의 종료 날짜 이전인 경우 - 반복 삭제
			if (newEventRrule.containsKey(RecurrenceRule.UNTIL)) {
				String until = newEventRrule.getValue(RecurrenceRule.UNTIL);

				Calendar untilCalendar = EventRecurrenceFragment.convertDate(until);
				if (untilCalendar.getTime().before(new Date(newEventValues.getAsLong(CalendarContract.Events.DTEND)))) {
					recurrenceRule.clear();
				}
			} else {
				if (originalCount == 0) {
					recurrenceRule.removeValue(RecurrenceRule.COUNT);
				} else {
					int count = originalCount - instanceCount;

					if (count == 0) {
						//remove recurrence
						recurrenceRule.clear();
					} else {
						recurrenceRule.putValue(RecurrenceRule.COUNT, count);
					}
				}
			}

			if (!recurrenceRule.isEmpty()) {
				newEventValues.put(CalendarContract.Events.RRULE, recurrenceRule.getRule());
			}
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
		if (modifiedInstance.containsKey(key)) {
			newEventValues.put(key, modifiedInstance.getAsString(key));
		} else if (originalInstance.getAsString(key) != null) {
			newEventValues.put(key, originalInstance.getAsString(key));
		}
	}


	public boolean updateFollowingEvents(ContentValues originalEvent, ContentValues modifiedEvent) {
		boolean forceSaveReminders = false;

		boolean rruleIsEmptyInModifiedEvent = false;
		if (modifiedEvent.containsKey(Events.RRULE)) {
			rruleIsEmptyInModifiedEvent = TextUtils.isEmpty(modifiedEvent.getAsString(Events.RRULE));
		}

		if (rruleIsEmptyInModifiedEvent) {
			if (isFirstEventInSeries(modifiedEvent, originalEvent)) {
				calendarViewModel.deleteEvent(originalEvent.getAsLong(CalendarContract.Instances.EVENT_ID));
			} else {
				updatePastEvents(originalEvent, modifiedDtStart);
			}
			modifiedEvent.put(Events.STATUS, originalEvent.getAsInteger(CalendarContract.Instances.STATUS));

		} else {
			if (isFirstEventInSeries(modifiedEvent, originalEvent)) {
				checkTimeDependentFields(originalEvent, modifiedEvent);
			} else {
				String newRrule = updatePastEvents(originalEvent, modifiedDtStart);
				if (modifiedEvent.getAsString(Events.RRULE).equals(originalEvent.getAsString(Events.RRULE))) {
					modifiedEvent.put(Events.RRULE, newRrule);
				}
				modifiedEvent.put(Events.STATUS, originalEvent.getAsInteger(CalendarContract.Instances.STATUS));
			}
		}
		forceSaveReminders = true;

		boolean hasAttendeeData = model.mHasAttendeeData;

		if (hasAttendeeData && model.mOwnerAttendeeId == -1) {
			// Organizer is not an attendee

			String ownerEmail = model.mOwnerAccount;
			if (model.mAttendeesList.size() != 0 && Utils.isValidEmail(ownerEmail)) {
				// Add organizer as attendee since we got some attendees

				values.clear();
				values.put(Attendees.ATTENDEE_EMAIL, ownerEmail);
				values.put(Attendees.ATTENDEE_RELATIONSHIP, Attendees.RELATIONSHIP_ORGANIZER);
				values.put(Attendees.ATTENDEE_TYPE, Attendees.TYPE_REQUIRED);
				values.put(Attendees.ATTENDEE_STATUS, Attendees.ATTENDEE_STATUS_ACCEPTED);

				if (newEvent) {
					b = ContentProviderOperation.newInsert(Attendees.CONTENT_URI)
							.withValues(values);
					b.withValueBackReference(Attendees.EVENT_ID, eventIdIndex);
				} else {
					values.put(Attendees.EVENT_ID, model.mId);
					b = ContentProviderOperation.newInsert(Attendees.CONTENT_URI)
							.withValues(values);
				}
				ops.add(b.build());
			}
		} else if (hasAttendeeData &&
				model.mSelfAttendeeStatus != originalModel.mSelfAttendeeStatus &&
				model.mOwnerAttendeeId != -1) {

			Uri attUri = ContentUris.withAppendedId(Attendees.CONTENT_URI, model.mOwnerAttendeeId);

			values.clear();
			values.put(Attendees.ATTENDEE_STATUS, model.mSelfAttendeeStatus);
			values.put(Attendees.EVENT_ID, model.mId);
			b = ContentProviderOperation.newUpdate(attUri).withValues(values);
			ops.add(b.build());
		}

		if (hasAttendeeData && (newEvent || uri != null)) {
			String attendees = model.getAttendeesString();
			String originalAttendeesString;
			if (originalModel != null) {
				originalAttendeesString = originalModel.getAttendeesString();
			} else {
				originalAttendeesString = "";
			}
			// Hit the content provider only if this is a new event or the user
			// has changed it
			if (newEvent || !TextUtils.equals(originalAttendeesString, attendees)) {
				// figure out which attendees need to be added and which ones
				// need to be deleted. use a linked hash set, so we maintain
				// order (but also remove duplicates).
				HashMap<String, Attendee> newAttendees = model.mAttendeesList;
				LinkedList<String> removedAttendees = new LinkedList<String>();

				// the eventId is only used if eventIdIndex is -1.
				// TODO: clean up this code.
				long eventId = uri != null ? ContentUris.parseId(uri) : -1;

				// only compute deltas if this is an existing event.
				// new events (being inserted into the Events table) won't
				// have any existing attendees.
				if (!newEvent) {
					removedAttendees.clear();
					HashMap<String, Attendee> originalAttendees = originalModel.mAttendeesList;
					for (String originalEmail : originalAttendees.keySet()) {
						if (newAttendees.containsKey(originalEmail)) {
							// existing attendee. remove from new attendees set.
							newAttendees.remove(originalEmail);
						} else {
							// no longer in attendees. mark as removed.
							removedAttendees.add(originalEmail);
						}
					}

					// delete removed attendees if necessary
					if (removedAttendees.size() > 0) {
						b = ContentProviderOperation.newDelete(Attendees.CONTENT_URI);

						String[] args = new String[removedAttendees.size() + 1];
						args[0] = Long.toString(eventId);
						int i = 1;
						StringBuilder deleteWhere = new StringBuilder(ATTENDEES_DELETE_PREFIX);
						for (String removedAttendee : removedAttendees) {
							if (i > 1) {
								deleteWhere.append(",");
							}
							deleteWhere.append("?");
							args[i++] = removedAttendee;
						}
						deleteWhere.append(")");
						b.withSelection(deleteWhere.toString(), args);
						ops.add(b.build());
					}
				}

				if (newAttendees.size() > 0) {
					// Insert the new attendees
					for (Attendee attendee : newAttendees.values()) {
						values.clear();
						values.put(Attendees.ATTENDEE_NAME, attendee.mName);
						values.put(Attendees.ATTENDEE_EMAIL, attendee.mEmail);
						values.put(Attendees.ATTENDEE_RELATIONSHIP,
								Attendees.RELATIONSHIP_ATTENDEE);
						values.put(Attendees.ATTENDEE_TYPE, Attendees.TYPE_REQUIRED);
						values.put(Attendees.ATTENDEE_STATUS, Attendees.ATTENDEE_STATUS_NONE);

						if (newEvent) {
							b = ContentProviderOperation.newInsert(Attendees.CONTENT_URI)
									.withValues(values);
							b.withValueBackReference(Attendees.EVENT_ID, eventIdIndex);
						} else {
							values.put(Attendees.EVENT_ID, eventId);
							b = ContentProviderOperation.newInsert(Attendees.CONTENT_URI)
									.withValues(values);
						}
						ops.add(b.build());
					}
				}
			}
		}


		mService.startBatch(mService.getNextToken(), null, CalendarContract.AUTHORITY, ops,
				Utils.UNDO_DELAY);


		return true;
	}

	public LinkedHashSet<Rfc822Token> getAddressesFromList(String list,
	                                                       Rfc822Validator validator) {
		LinkedHashSet<Rfc822Token> addresses = new LinkedHashSet<Rfc822Token>();
		Rfc822Tokenizer.tokenize(list, addresses);
		if (validator == null) {
			return addresses;
		}

		// validate the emails, out of paranoia. they should already be
		// validated on input, but drop any invalid emails just to be safe.
		Iterator<Rfc822Token> addressIterator = addresses.iterator();
		while (addressIterator.hasNext()) {
			Rfc822Token address = addressIterator.next();
			if (!validator.isValid(address.getAddress())) {
				Log.v(TAG, "Dropping invalid attendee email address: " + address.getAddress());
				addressIterator.remove();
			}
		}
		return addresses;
	}


	public void checkTimeDependentFields(ContentValues originalEvent,
	                                     ContentValues modifiedEvent) {
		long oldBegin = originalEvent.getAsLong(Events.DTSTART);
		long oldEnd = originalEvent.getAsLong(Events.DTEND);
		boolean oldAllDay = originalEvent.getAsInteger(Events.ALL_DAY) == 1;
		String oldRrule = originalEvent.getAsString(Events.RRULE);
		String oldTimezone = originalEvent.getAsString(Events.EVENT_TIMEZONE);

		long newBegin = modifiedEvent.getAsLong(Events.DTSTART);
		long newEnd = modifiedEvent.getAsLong(Events.DTEND);
		boolean newAllDay = modifiedEvent.getAsInteger(Events.ALL_DAY) == 1;
		String newRrule = modifiedEvent.getAsString(Events.RRULE);
		String newTimezone = modifiedEvent.getAsString(Events.EVENT_TIMEZONE);

		if (oldBegin == newBegin && oldEnd == newEnd && oldAllDay == newAllDay
				&& TextUtils.equals(oldRrule, newRrule)
				&& TextUtils.equals(oldTimezone, newTimezone)) {
			modifiedEvent.remove(Events.DTSTART);
			modifiedEvent.remove(Events.DTEND);
			modifiedEvent.remove(Events.DURATION);
			modifiedEvent.remove(Events.ALL_DAY);
			modifiedEvent.remove(Events.RRULE);
			modifiedEvent.remove(Events.EVENT_TIMEZONE);
			return;
		}

		if (TextUtils.isEmpty(oldRrule) || TextUtils.isEmpty(newRrule)) {
			return;
		}
	}


	public String updatePastEvents(ContentValues originalEvent, long endTimeMillis) {
		boolean origAllDay = originalEvent.getAsInteger(Events.ALL_DAY) == 1;
		String origRrule = originalEvent.getAsString(Events.RRULE);
		String newRrule = origRrule;

		EventRecurrence origRecurrence = new EventRecurrence();
		origRecurrence.parse(origRrule);

		// Get the start time of the first instance in the original recurrence.
		long startTimeMillis = originalEvent.getAsLong(Events.DTSTART);
		Time dtStartCalendar = new Time();
		dtStartCalendar.timezone = originalEvent.getAsString(Events.EVENT_TIMEZONE);
		dtStartCalendar.set(startTimeMillis);

		ContentValues updateValues = new ContentValues();

		if (origRecurrence.count > 0) {
			RecurrenceSet recurSet = new RecurrenceSet(origRrule, null, null, null);
			RecurrenceProcessor recurProc = new RecurrenceProcessor();
			long[] recurrences;
			try {
				recurrences = recurProc.expand(dtStartCalendar, recurSet, startTimeMillis, endTimeMillis);
			} catch (DateException de) {
				throw new RuntimeException(de);
			}

			if (recurrences.length == 0) {
				throw new RuntimeException("can't use this method on first instance");
			}

			EventRecurrence exceptRecurrence = new EventRecurrence();
			exceptRecurrence.parse(origRrule);  // TODO: add+use a copy constructor instead
			exceptRecurrence.count -= recurrences.length;
			newRrule = exceptRecurrence.toString();

			origRecurrence.count = recurrences.length;

		} else {
			Time untilTime = new Time();
			untilTime.timezone = Time.TIMEZONE_UTC;

			// Subtract one second from the old begin time to get the new
			// "until" time.
			untilTime.set(endTimeMillis - 1000); // subtract one second (1000 millis)
			if (origAllDay) {
				untilTime.hour = 0;
				untilTime.minute = 0;
				untilTime.second = 0;
				untilTime.allDay = true;
				untilTime.normalize(false);

				// This should no longer be necessary -- DTSTART should already be in the correct
				// format for an all-day event.
				dtStartCalendar.hour = 0;
				dtStartCalendar.minute = 0;
				dtStartCalendar.second = 0;
				dtStartCalendar.allDay = true;
				dtStartCalendar.timezone = Time.TIMEZONE_UTC;
			}
			origRecurrence.until = untilTime.format2445();
		}

		updateValues.put(Events.RRULE, origRecurrence.toString());
		updateValues.put(Events.DTSTART, dtStartCalendar.normalize(true));

		if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, originalEvent.getAsLong(CalendarContract.Instances.EVENT_ID));
			int result = getContext().getContentResolver().update(uri, updateValues, null, null);
		}

		return newRrule;
	}


	public boolean isSameEvent(ContentValues modifiedEvent, ContentValues originalEvent) {
		if (originalEvent == null) {
			return true;
		}

		if (!modifiedEvent.getAsInteger(Events.CALENDAR_ID).equals(originalEvent.getAsInteger(Events.CALENDAR_ID))) {
			return false;
		}
		if (!modifiedEvent.getAsLong(Events._ID).equals(originalEvent.getAsLong(CalendarContract.Instances.EVENT_ID))) {
			return false;
		}

		return true;
	}


	public boolean isFirstEventInSeries(ContentValues originalEvent, ContentValues modifiedEvent) {
		return originalEvent.getAsLong(Events.DTSTART) == modifiedDtStart;
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
