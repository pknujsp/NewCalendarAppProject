package com.zerodsoft.calendarplatform.activity.editevent.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.activity.editevent.interfaces.OnEditEventResultListener;
import com.zerodsoft.calendarplatform.calendar.AsyncQueryService;
import com.zerodsoft.calendarplatform.calendar.CalendarViewModel;
import com.zerodsoft.calendarplatform.calendar.EventHelper;
import com.zerodsoft.calendarplatform.calendar.calendarcommon2.EventRecurrence;
import com.zerodsoft.calendarplatform.calendar.dto.DateTimeObj;
import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.calendarplatform.event.util.EventUtil;
import com.zerodsoft.calendarplatform.room.dto.LocationDTO;
import com.zerodsoft.calendarplatform.utility.ClockUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class ModifyInstanceFragment extends EventBaseFragment {
	private ContentValues originalEvent;
	private OnEditEventResultListener onEditEventResultListener;
	private List<ContentValues> originalReminderList = new ArrayList<>();
	private List<ContentValues> originalAttendeeList = new ArrayList<>();
	private EventRecurrence originalEventRecurrence = new EventRecurrence();

	private DateTimeObj originalBeginDateTimeObj;
	private DateTimeObj originalEndDateTimeObj;

	protected AsyncQueryService mService;

	public boolean edited = false;

	public synchronized AsyncQueryService getAsyncQueryService() {
		if (mService == null) {
			mService = new AsyncQueryService(getActivity(), (OnEditEventResultListener) calendarViewModel);
		}
		return mService;
	}

	public ModifyInstanceFragment(OnEditEventResultListener onEditEventResultListener) {
		this.onEditEventResultListener = onEditEventResultListener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
		calendarViewModel = new ViewModelProvider(requireActivity()).get(CalendarViewModel.class);
		eventModel = new EventModel();
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
				if (eventModel.getModifiedValueSet().isEmpty() && !eventModel.isModifiedAttendees()
						&& !eventModel.isModifiedReminders() && originalEventRecurrence.equals(eventModel.getEventRecurrence())) {
					Toast.makeText(getContext(), R.string.not_edited, Toast.LENGTH_SHORT).show();
					return;
				}
				/*
				반복값을 수정하지 않았을 경우 : update this/following events/all events
				반복값을 수정한 경우 : update all/following events
				반복값을 삭제한 경우 : remove all events and save new event
				 */
				if (!originalEventRecurrence.toString().equals(EventRecurrence.EMPTY)) {
					String[] dialogMenus = null;
					int[] dialogMenusIndexArr = null;

					final int UPDATE_THIS_EVENT = 0;
					final int UPDATE_FOLLOWING_EVENTS = 1;
					final int UPDATE_ALL_EVENTS = 2;

					if (!originalEventRecurrence.equals(eventModel.getEventRecurrence())) {
						if (eventModel.getEventRecurrence().toString().equals(EventRecurrence.EMPTY)) {
							//반복값을 삭제한 경우 : remove all events and save new event
							EventHelper eventRemoveHelper = new EventHelper(new AsyncQueryService(getActivity(),
									(OnEditEventResultListener) calendarViewModel));
							eventRemoveHelper.removeEvent(EventHelper.EventEditType.REMOVE_ALL_EVENTS, originalEvent);

							ContentValues modifiedEvent = eventModel.getNEW_EVENT();
							ContentValues newEventValues = new ContentValues();
							List<ContentValues> newReminderList = eventModel.getNEW_REMINDERS();
							List<ContentValues> newAttendeeList = eventModel.getNEW_ATTENDEES();

							setNewEventValues(Events.TITLE, newEventValues, modifiedEvent);
							setNewEventValues(Events.EVENT_COLOR_KEY, newEventValues, modifiedEvent);
							setNewEventValues(Events.EVENT_COLOR, newEventValues, modifiedEvent);
							setNewEventValues(Events.CALENDAR_ID, newEventValues, modifiedEvent);
							setNewEventValues(Events.EVENT_TIMEZONE, newEventValues, modifiedEvent);
							setNewEventValues(Events.DESCRIPTION, newEventValues, modifiedEvent);
							setNewEventValues(Events.EVENT_LOCATION, newEventValues, modifiedEvent);
							setNewEventValues(Events.AVAILABILITY, newEventValues, modifiedEvent);
							setNewEventValues(Events.ACCESS_LEVEL, newEventValues, modifiedEvent);
							setNewEventValues(Events.GUESTS_CAN_INVITE_OTHERS, newEventValues, modifiedEvent);
							setNewEventValues(Events.GUESTS_CAN_MODIFY, newEventValues, modifiedEvent);
							setNewEventValues(Events.GUESTS_CAN_SEE_GUESTS, newEventValues, modifiedEvent);
							setNewEventValues(Events.IS_ORGANIZER, newEventValues, modifiedEvent);
							newEventValues.putNull(Events.RRULE);

							if (!setIfModifiedDateTimeAllDay(newEventValues)) {
								applyModifiedDateTime(newEventValues, binding.timeLayout.timeAlldaySwitch.isChecked());
							}

							edited = true;
							EventHelper eventHelper = new EventHelper(getAsyncQueryService());
							eventHelper.saveNewEvent(newEventValues, locationDTO, newReminderList, newAttendeeList, locationIntentCode);
							onEditEventResultListener.onSavedNewEvent(0L);
							return;
						} else {
							//	반복값을 수정한 경우 : update all/following events
							dialogMenus = new String[]{
									getString(R.string.save_all_future_events_including_current_event),
									getString(R.string.save_all_events)};

							dialogMenusIndexArr = new int[]{UPDATE_FOLLOWING_EVENTS, UPDATE_ALL_EVENTS};
						}
					} else {
						//반복값을 수정하지 않았을 경우 : update this/following events/all events
						dialogMenus = new String[]{
								getString(R.string.save_only_current_event),
								getString(R.string.save_all_future_events_including_current_event),
								getString(R.string.save_all_events)};

						dialogMenusIndexArr = new int[]{UPDATE_THIS_EVENT, UPDATE_FOLLOWING_EVENTS, UPDATE_ALL_EVENTS};
					}

					int[] finalDialogMenusIndexArr = dialogMenusIndexArr;
					new MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.save_event_title)
							.setItems(dialogMenus, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int index) {
									final int clickedType = finalDialogMenusIndexArr[index];

									switch (clickedType) {
										case UPDATE_THIS_EVENT:
											updateThisEvent();
											break;
										case UPDATE_FOLLOWING_EVENTS:
											updateFollowingEvents();
											break;
										case UPDATE_ALL_EVENTS:
											updateAllEvents();
											break;
									}
								}
							}).create().show();
				} else {
					updateAllEvents();
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
				String accountType = selectedCalendarValues.getAsString(CalendarContract.Calendars.ACCOUNT_TYPE);
				List<ContentValues> colors = calendarViewModel.getEventColors(accountName, accountType);

				String colorKey;

				if (firstClicked) {
					firstClicked = false;
					colorKey = originalEvent.getAsString(CalendarContract.Instances.EVENT_COLOR_KEY);
				} else {
					colorKey = eventModel.getNEW_EVENT().getAsString(CalendarContract.Events.EVENT_COLOR_KEY);
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
					modifiedEnd = true;

					if (originalEvent.getAsInteger(Events.ALL_DAY) == 1) {
						TimeZone timeZone = TimeZone.getTimeZone(originalEvent.getAsString(Events.EVENT_TIMEZONE));
						eventModel.setTimezone(timeZone.getID());
						setTimeZoneText();
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
			String rRule = eventModel.getEventRecurrence().toString();
			onClickedRecurrence(rRule, eventModel.getBeginDateTimeObj().getUtcCalendar().getTimeInMillis());
		});

        /*
        접근수준
         */
		binding.accesslevelLayout.eventAccessLevel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int accessLevel = 0;

				if (eventModel.getNEW_EVENT().containsKey(CalendarContract.Events.ACCESS_LEVEL)) {
					accessLevel = eventModel.getNEW_EVENT().getAsInteger(CalendarContract.Events.ACCESS_LEVEL);
				} else {
					accessLevel = originalEvent.getAsInteger(CalendarContract.Instances.ACCESS_LEVEL);
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

				if (eventModel.getNEW_EVENT().containsKey(CalendarContract.Events.AVAILABILITY)) {
					available = eventModel.getNEW_EVENT().getAsInteger(CalendarContract.Events.AVAILABILITY);
				} else {
					available = originalEvent.getAsInteger(CalendarContract.Instances.AVAILABILITY);
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
			if (eventModel.getNEW_EVENT().containsKey(CalendarContract.Events.EVENT_LOCATION)) {
				eventLocation = eventModel.getNEW_EVENT().getAsString(CalendarContract.Events.EVENT_LOCATION);
			} else {
				if (originalEvent.getAsString(CalendarContract.Instances.EVENT_LOCATION) != null) {
					eventLocation = originalEvent.getAsString(CalendarContract.Instances.EVENT_LOCATION);
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
					guestsCanModify = originalEvent.getAsInteger(CalendarContract.Instances.GUESTS_CAN_MODIFY) == 1;
					guestsCanInviteOthers = originalEvent.getAsInteger(CalendarContract.Instances.GUESTS_CAN_INVITE_OTHERS) == 1;
					guestsCanSeeGuests = originalEvent.getAsInteger(CalendarContract.Instances.GUESTS_CAN_SEE_GUESTS) == 1;
				} else {
					guestsCanModify = eventModel.getNEW_EVENT().getAsInteger(CalendarContract.Instances.GUESTS_CAN_MODIFY) == 1;
					guestsCanInviteOthers = eventModel.getNEW_EVENT().getAsInteger(CalendarContract.Instances.GUESTS_CAN_INVITE_OTHERS) == 1;
					guestsCanSeeGuests = eventModel.getNEW_EVENT().getAsInteger(CalendarContract.Instances.GUESTS_CAN_SEE_GUESTS) == 1;
				}

				Bundle bundle = new Bundle();
				bundle.putBoolean(CalendarContract.Events.GUESTS_CAN_MODIFY, guestsCanModify);
				bundle.putBoolean(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, guestsCanInviteOthers);
				bundle.putBoolean(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, guestsCanSeeGuests);

				onClickedAttendeeList(bundle);
			}
		});
	}

	private boolean modifiedEnd = false;
	private boolean modifiedDateTime = false;

	@Override
	protected final void initDatePicker() {
		DateTimeObj endDateTimeObj = null;
		if (modifiedEnd) {
			endDateTimeObj = eventModel.getEndDateTimeObj();
		} else {
			try {
				endDateTimeObj = eventModel.getEndDateTimeObj().clone();
				if (binding.timeLayout.timeAlldaySwitch.isChecked()) {
					endDateTimeObj.add(Calendar.DATE, -1);
				}
			} catch (Exception e) {

			}
		}

		showDatePicker(new OnModifiedDateTimeCallback() {
			@Override
			public void onModified() {
				modifiedEnd = true;
				modifiedDateTime = true;
			}
		}, endDateTimeObj);
	}

	@Override
	protected void initTimePicker(DateTimeType dateType) {
		showTimePicker(dateType, new OnModifiedDateTimeCallback() {
			@Override
			public void onModified() {
				modifiedDateTime = true;
			}
		});
	}

	private void loadInitData() {
		Bundle arguments = getArguments();

		final long eventId = arguments.getLong(CalendarContract.Instances.EVENT_ID, 0);
		final long instanceId = arguments.getLong(CalendarContract.Instances._ID, 0);
		final long begin = arguments.getLong(CalendarContract.Instances.BEGIN, 0);
		final long end = arguments.getLong(CalendarContract.Instances.END, 0);

		// 인스턴스, 알림을 가져온다
		originalEvent = calendarViewModel.getInstance(instanceId, begin, end);
		selectedCalendarValues =
				calendarViewModel.getCalendar(originalEvent.getAsInteger(Events.CALENDAR_ID));

		final List<ContentValues> attendeeList = calendarViewModel.getAttendeeListForEdit(eventId);

		eventModel.getNEW_REMINDERS().addAll(calendarViewModel.getReminders(eventId));
		eventModel.getNEW_ATTENDEES().addAll(attendeeList);

		originalAttendeeList.addAll(attendeeList);
		originalReminderList.addAll(eventModel.getNEW_REMINDERS());

		//제목, 캘린더, 시간, 시간대, 반복, 알림, 설명, 위치, 공개범위, 유효성, 참석자
		//알림, 참석자 정보는 따로 불러온다.
		if (originalEvent.containsKey(Events.EVENT_COLOR)) {
			binding.titleLayout.eventColor.setBackgroundColor(EventUtil.getColor(originalEvent.getAsInteger(Instances.EVENT_COLOR)));
		} else {
			binding.titleLayout.eventColor.setVisibility(View.GONE);
		}
		//제목
		binding.titleLayout.title.setText(originalEvent.getAsString(Instances.TITLE));

		// allday switch
		final boolean isAllDay = originalEvent.getAsInteger(CalendarContract.Instances.ALL_DAY) == 1;
		binding.timeLayout.timeAlldaySwitch.setChecked(isAllDay);

		Calendar calendar = null;

		if (isAllDay) {
			calendar = Calendar.getInstance(ClockUtil.UTC_TIME_ZONE);
		} else {
			calendar = Calendar.getInstance(TimeZone.getTimeZone(originalEvent.getAsString(Events.EVENT_TIMEZONE)));
		}
		DateTimeObj beginDateTimeObj = eventModel.getBeginDateTimeObj();
		DateTimeObj endDateTimeObj = eventModel.getEndDateTimeObj();

		calendar.setTimeInMillis(originalEvent.getAsLong(Instances.BEGIN));
		beginDateTimeObj.setYear(calendar.get(Calendar.YEAR)).setMonth(calendar.get(Calendar.MONTH) + 1)
				.setDay(calendar.get(Calendar.DAY_OF_MONTH)).setHour(calendar.get(Calendar.HOUR_OF_DAY))
				.setMinute(calendar.get(Calendar.MINUTE));

		calendar.setTimeInMillis(originalEvent.getAsLong(Instances.END));
		endDateTimeObj.setYear(calendar.get(Calendar.YEAR)).setMonth(calendar.get(Calendar.MONTH) + 1)
				.setDay(calendar.get(Calendar.DAY_OF_MONTH)).setHour(calendar.get(Calendar.HOUR_OF_DAY))
				.setMinute(calendar.get(Calendar.MINUTE));

		try {
			originalBeginDateTimeObj = beginDateTimeObj.clone();
			originalEndDateTimeObj = endDateTimeObj.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		setDateText(DateTimeType.BEGIN, beginDateTimeObj, true);
		setTimeText(DateTimeType.BEGIN, beginDateTimeObj);
		setDateText(DateTimeType.END, endDateTimeObj, true);
		setTimeText(DateTimeType.END, endDateTimeObj);

		TimeZone eventTimeZone = null;
		TimeZone calendarTimeZone = null;

		if (isAllDay) {
			if (originalEvent.containsKey(Events.CALENDAR_TIME_ZONE)) {
				eventTimeZone = TimeZone.getTimeZone(originalEvent.getAsString(Events.CALENDAR_TIME_ZONE));
			} else {
				eventTimeZone = TimeZone.getDefault();
			}
		} else {
			eventTimeZone = TimeZone.getTimeZone(originalEvent.getAsString(Events.EVENT_TIMEZONE));
		}

		if (originalEvent.containsKey(Events.CALENDAR_TIME_ZONE)) {
			calendarTimeZone = TimeZone.getTimeZone(originalEvent.getAsString(Events.CALENDAR_TIME_ZONE));
		} else {
			calendarTimeZone = TimeZone.getDefault();
		}

		eventModel.setEventTimeZone(eventTimeZone);
		eventModel.setCalendarTimeZone(calendarTimeZone);
		setTimeZoneText();

		//캘린더
		setCalendarText(originalEvent.getAsInteger(Instances.CALENDAR_COLOR),
				originalEvent.getAsString(Instances.CALENDAR_DISPLAY_NAME),
				selectedCalendarValues.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));

		// 반복
		if (originalEvent.getAsString(Instances.RRULE) != null) {
			EventRecurrence eventRecurrence = new EventRecurrence();
			eventRecurrence.parse(originalEvent.getAsString(Instances.RRULE));

			originalEventRecurrence = new EventRecurrence();
			originalEventRecurrence.parse(originalEvent.getAsString(Instances.RRULE));
			eventModel.setEventRecurrence(eventRecurrence);
			setRecurrenceText(originalEvent.getAsString(Instances.RRULE));
		}

		// 알림
		if (originalEvent.getAsInteger(CalendarContract.Instances.HAS_ALARM) == 1) {
			List<ContentValues> originalReminderList = eventModel.getNEW_REMINDERS();

			for (ContentValues reminder : originalReminderList) {
				addReminderItemView(reminder);
			}
		}

		// 설명
		binding.descriptionLayout.descriptionEdittext.setText(originalEvent.getAsString(CalendarContract.Instances.DESCRIPTION));

		// 위치
		binding.locationLayout.eventLocation.setText(originalEvent.getAsString(CalendarContract.Instances.EVENT_LOCATION));
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
		setAccessLevelText(originalEvent.getAsInteger(CalendarContract.Instances.ACCESS_LEVEL));

		// 유효성
		setAvailabilityText(originalEvent.getAsInteger(CalendarContract.Instances.AVAILABILITY));

		// 참석자
		if (attendeeList.size() > 0) {
			final boolean guestsCanModify = originalEvent.getAsInteger(Events.GUESTS_CAN_MODIFY) == 1;
			final boolean guestsCanSeeGuests = originalEvent.getAsInteger(Events.GUESTS_CAN_SEE_GUESTS) == 1;
			final boolean guestsCanInviteOthers = originalEvent.getAsInteger(Events.GUESTS_CAN_INVITE_OTHERS) == 1;
			final boolean isOrganizer = originalEvent.getAsString(Events.IS_ORGANIZER).equals("1");

			if (isOrganizer) {
				createAttendeeListView();
			} else {
				if (guestsCanModify) {
					createAttendeeListView();
				} else {
					//알림외에는 수정불가
					binding.titleLayout.getRoot().setVisibility(View.GONE);
					binding.calendarLayout.getRoot().setVisibility(View.GONE);
					binding.timeLayout.getRoot().setVisibility(View.GONE);
					binding.recurrenceLayout.getRoot().setVisibility(View.GONE);
					binding.descriptionLayout.getRoot().setVisibility(View.GONE);
					binding.locationLayout.getRoot().setVisibility(View.GONE);
					binding.attendeeLayout.getRoot().setVisibility(View.GONE);
					binding.accesslevelLayout.getRoot().setVisibility(View.GONE);
					binding.availabilityLayout.getRoot().setVisibility(View.GONE);

					if (guestsCanInviteOthers) {
						createAttendeeListView();
						binding.attendeeLayout.getRoot().setVisibility(View.VISIBLE);
					} else {
						binding.attendeeLayout.getRoot().setVisibility(View.GONE);
					}
				}
			}

		} else {
			binding.attendeeLayout.showAttendeesDetail.setText(getString(R.string.add_attendee));
		}
	}

	private boolean setIfModifiedDateTimeAllDay(ContentValues contentValues) {
		final boolean originalIsAllDay = originalEvent.getAsInteger(Events.ALL_DAY) == 1;
		final boolean newIsAllDay = binding.timeLayout.timeAlldaySwitch.isChecked();
		boolean appliedModifiedDateTime = false;

		if (originalIsAllDay != newIsAllDay) {
			applyModifiedDateTime(contentValues, newIsAllDay);
			appliedModifiedDateTime = true;
		} else {
			if (modifiedDateTime) {
				applyModifiedDateTime(contentValues, newIsAllDay);
				appliedModifiedDateTime = true;
			}
		}

		contentValues.put(Events.ALL_DAY, newIsAllDay ? 1 : 0);
		return appliedModifiedDateTime;
	}

	private void applyModifiedDateTime(ContentValues contentValues, boolean allDay) {
		long beginTimeMillis = 0L;
		long endTimeMillis = 0L;
		DateTimeObj newBeginDateTimeObj = eventModel.getBeginDateTimeObj();
		DateTimeObj newEndDateTimeObj = eventModel.getEndDateTimeObj();

		if (allDay) {
			Calendar utcCalendar = Calendar.getInstance(ClockUtil.UTC_TIME_ZONE);
			utcCalendar.set(newBeginDateTimeObj.getYear(), newBeginDateTimeObj.getMonth() - 1, newBeginDateTimeObj.getDay(), 0, 0, 0);
			beginTimeMillis = utcCalendar.getTimeInMillis();

			utcCalendar.set(newEndDateTimeObj.getYear(), newEndDateTimeObj.getMonth() - 1, newEndDateTimeObj.getDay());
			if (modifiedEnd) {
				utcCalendar.add(Calendar.DATE, 1);
			}
			endTimeMillis = utcCalendar.getTimeInMillis();
		} else {
			TimeZone eventTimeZone = eventModel.getEventTimeZone();
			beginTimeMillis = newBeginDateTimeObj.getCalendar(eventTimeZone).getTimeInMillis();
			endTimeMillis = newEndDateTimeObj.getCalendar(eventTimeZone).getTimeInMillis();
		}

		contentValues.put(Events.DTSTART, beginTimeMillis);
		contentValues.put(Events.DTEND, endTimeMillis);
	}

	//이번 일정만 변경
	protected void updateThisEvent() {
		//인스턴스를 이벤트에서 제외
		ContentValues modifiedEvent = eventModel.getNEW_EVENT();
		ContentValues newEventValues = new ContentValues();
		List<ContentValues> newReminderList = eventModel.getNEW_REMINDERS();
		List<ContentValues> newAttendeeList = eventModel.getNEW_ATTENDEES();

		setNewEventValues(Events.TITLE, newEventValues, modifiedEvent);
		setNewEventValues(Events.EVENT_COLOR_KEY, newEventValues, modifiedEvent);
		setNewEventValues(Events.EVENT_COLOR, newEventValues, modifiedEvent);
		setNewEventValues(Events.CALENDAR_ID, newEventValues, modifiedEvent);
		setNewEventValues(Events.EVENT_TIMEZONE, newEventValues, modifiedEvent);
		setNewEventValues(Events.DESCRIPTION, newEventValues, modifiedEvent);
		setNewEventValues(Events.EVENT_LOCATION, newEventValues, modifiedEvent);
		setNewEventValues(Events.AVAILABILITY, newEventValues, modifiedEvent);
		setNewEventValues(Events.ACCESS_LEVEL, newEventValues, modifiedEvent);
		setNewEventValues(Events.GUESTS_CAN_INVITE_OTHERS, newEventValues, modifiedEvent);
		setNewEventValues(Events.GUESTS_CAN_MODIFY, newEventValues, modifiedEvent);
		setNewEventValues(Events.GUESTS_CAN_SEE_GUESTS, newEventValues, modifiedEvent);

		setDateTimeIfChangedTimeZone(newEventValues);

		newEventValues.putNull(Events.RRULE);
		newEventValues.put(Events.ALL_DAY, binding.timeLayout.timeAlldaySwitch.isChecked() ? 1 : 0);
		applyModifiedDateTime(newEventValues, binding.timeLayout.timeAlldaySwitch.isChecked());

		EventHelper eventHelper = new EventHelper(getAsyncQueryService());
		eventHelper.updateEvent(EventHelper.EventEditType.UPDATE_ONLY_THIS_EVENT, originalEvent, newEventValues, originalReminderList
				, originalAttendeeList, newReminderList, newAttendeeList, selectedCalendarValues, locationDTO, locationIntentCode);

		edited = true;
		onEditEventResultListener.onUpdatedOnlyThisEvent(0L);
	}


	//이번 일정을 포함한 이후 모든 일정 변경
	protected void updateFollowingEvents() {
		/*
		이벤트의 반복 종료일을 수정한 인스턴스의 일정 종료일로 설정
		수정한 인스턴스를 새로운 인스턴스로 추가
		 */
		ContentValues newEvent = eventModel.getNEW_EVENT();
		List<ContentValues> newReminderList = eventModel.getNEW_REMINDERS();
		List<ContentValues> newAttendeeList = eventModel.getNEW_ATTENDEES();

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
		setNewEventValues(CalendarContract.Events.EVENT_TIMEZONE, newEventValues, newEvent);
		setNewEventValues(CalendarContract.Events.DESCRIPTION, newEventValues, newEvent);
		setNewEventValues(CalendarContract.Events.EVENT_LOCATION, newEventValues, newEvent);
		setNewEventValues(CalendarContract.Events.AVAILABILITY, newEventValues, newEvent);
		setNewEventValues(CalendarContract.Events.ACCESS_LEVEL, newEventValues, newEvent);
		setNewEventValues(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, newEventValues, newEvent);
		setNewEventValues(CalendarContract.Events.GUESTS_CAN_MODIFY, newEventValues, newEvent);
		setNewEventValues(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, newEventValues, newEvent);
		setNewEventValues(CalendarContract.Events.IS_ORGANIZER, newEventValues, newEvent);
		newEventValues.put(CalendarContract.Events._ID, originalEvent.getAsLong(CalendarContract.Instances.EVENT_ID));

		setDateTimeIfChangedTimeZone(newEventValues);

		String rrule = null;
		if (!originalEventRecurrence.equals(eventModel.getEventRecurrence())) {
			rrule = eventModel.getEventRecurrence().toString();
			if (rrule.equals(EventRecurrence.EMPTY)) {
				rrule = null;
			} else {
				applyModifiedDateTime(newEventValues, binding.timeLayout.timeAlldaySwitch.isChecked());
			}
		} else {
			rrule = originalEvent.getAsString(Events.RRULE);
		}

		newEventValues.put(Events.RRULE, rrule);
		setIfModifiedDateTimeAllDay(newEventValues);

		EventHelper eventHelper = new EventHelper(getAsyncQueryService());
		eventHelper.updateEvent(EventHelper.EventEditType.UPDATE_FOLLOWING_EVENTS, originalEvent, newEventValues, originalReminderList
				, originalAttendeeList, newReminderList, newAttendeeList, selectedCalendarValues, locationDTO, locationIntentCode);

		edited = true;
		onEditEventResultListener.onUpdatedFollowingEvents(0L);

	}


	//모든 일정 변경
	protected void updateAllEvents() {
		ContentValues modifiedEvent = eventModel.getNEW_EVENT();
		List<ContentValues> newReminderList = eventModel.getNEW_REMINDERS();
		List<ContentValues> newAttendeeList = eventModel.getNEW_ATTENDEES();

		boolean appliedModifiedDateTime = setIfModifiedDateTimeAllDay(modifiedEvent);

		setDateTimeIfChangedTimeZone(modifiedEvent);

		if (!appliedModifiedDateTime) {
			if (!originalEventRecurrence.equals(eventModel.getEventRecurrence())) {
				applyModifiedDateTime(modifiedEvent, binding.timeLayout.timeAlldaySwitch.isChecked());
			}
		}

		if (modifiedEnd) {
			if (originalEventRecurrence.equals(eventModel.getEventRecurrence())) {
				final Long newDtStart = modifiedEvent.getAsLong(Events.DTSTART);
				final Long newDtEnd = modifiedEvent.getAsLong(Events.DTEND);

				Long dtStartDifference = null;
				Long dtEndDifference = null;

				if (binding.timeLayout.timeAlldaySwitch.isChecked()) {
					dtStartDifference = newDtStart - originalBeginDateTimeObj.getUtcCalendar().getTimeInMillis();
					dtEndDifference = newDtEnd - originalEndDateTimeObj.getUtcCalendar().getTimeInMillis();
					modifiedEvent.put(Events.DTSTART, originalBeginDateTimeObj.getUtcCalendar().getTimeInMillis() + dtStartDifference);
					modifiedEvent.put(Events.DTEND, originalEndDateTimeObj.getUtcCalendar().getTimeInMillis() + dtEndDifference);
				} else {
					dtStartDifference = newDtStart - originalBeginDateTimeObj.getCalendar(eventModel.getEventTimeZone()).getTimeInMillis();
					dtEndDifference = newDtEnd - originalEndDateTimeObj.getCalendar(eventModel.getEventTimeZone()).getTimeInMillis();
					modifiedEvent.put(Events.DTSTART, originalBeginDateTimeObj.getCalendar(eventModel.getEventTimeZone()).getTimeInMillis()
							+ dtStartDifference);
					modifiedEvent.put(Events.DTEND, originalEndDateTimeObj.getCalendar(eventModel.getEventTimeZone()).getTimeInMillis()
							+ dtEndDifference);
				}
			}
		}


		if (!originalEventRecurrence.equals(eventModel.getEventRecurrence())) {
			String rrule = eventModel.getEventRecurrence().toString();
			if (rrule.equals(EventRecurrence.EMPTY)) {
				rrule = null;
			}
			modifiedEvent.put(Events.RRULE, rrule);
		} else {
		}


		EventHelper eventHelper = new EventHelper(getAsyncQueryService());
		eventHelper.updateEvent(EventHelper.EventEditType.UPDATE_ALL_EVENTS, originalEvent, modifiedEvent, originalReminderList
				, originalAttendeeList, newReminderList, newAttendeeList, selectedCalendarValues, locationDTO, locationIntentCode);

		edited = true;
		onEditEventResultListener.onUpdatedAllEvents(0L);
	}

	private void setDateTimeIfChangedTimeZone(ContentValues contentValues) {
		if (modifiedDateTime || modifiedEnd) {
			return;
		}

		if (!binding.timeLayout.timeAlldaySwitch.isChecked()) {
			if (originalEvent.containsKey(Events.EVENT_TIMEZONE)) {
				if (!originalEvent.getAsString(Events.EVENT_TIMEZONE).equals(eventModel.getEventTimeZone().getID())) {
					TimeZone eventTimeZone = eventModel.getEventTimeZone();
					long beginTimeMillis = eventModel.getBeginDateTimeObj().getCalendar(eventTimeZone).getTimeInMillis();
					long endTimeMillis = eventModel.getEndDateTimeObj().getCalendar(eventTimeZone).getTimeInMillis();

					contentValues.put(Events.DTSTART, beginTimeMillis);
					contentValues.put(Events.DTEND, endTimeMillis);
				}
			}
		}
	}

	private void setNewEventValues(String key, ContentValues newEventValues, ContentValues modifiedInstance) {
		if (eventModel.isModified(key)) {
			newEventValues.put(key, modifiedInstance.getAsString(key));
		} else {
			newEventValues.put(key, originalEvent.getAsString(key));
		}
	}


	public interface OnModifiedDateTimeCallback {
		void onModified();
	}


}
