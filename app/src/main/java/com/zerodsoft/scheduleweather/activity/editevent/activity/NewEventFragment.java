package com.zerodsoft.scheduleweather.activity.editevent.activity;

import android.content.ContentValues;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.activity.editevent.interfaces.OnEditEventResultListener;
import com.zerodsoft.scheduleweather.calendar.AsyncQueryService;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendar.EventHelper;
import com.zerodsoft.scheduleweather.calendar.calendarcommon2.EventRecurrence;
import com.zerodsoft.scheduleweather.calendar.dto.DateTimeObj;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class NewEventFragment extends EventBaseFragment {
	private OnEditEventResultListener onEditEventResultListener;

	public NewEventFragment(OnEditEventResultListener onEditEventResultListener) {
		this.onEditEventResultListener = onEditEventResultListener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		eventModel = new EventModel();
		calendarViewModel = new ViewModelProvider(requireActivity()).get(CalendarViewModel.class);
		locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
	}

	@Override
	protected void setOriginalMainFragmentTitle() {
		binding.fragmentTitle.setText(R.string.new_event);
	}

	@Override
	public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.fragmentTitle.setText(R.string.new_event);

		binding.reminderLayout.notReminder.setVisibility(View.GONE);
		binding.descriptionLayout.notDescription.setVisibility(View.GONE);
		binding.attendeeLayout.notAttendees.setVisibility(View.GONE);

		setViewOnClickListeners();
		loadInitData();

		binding.saveBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				saveNewEvent();
				onEditEventResultListener.onSavedNewEvent(0L);
			}
		});

		initializing = false;
	}

	@Override
	protected void setViewOnClickListeners() {
        /*
        event color
         */
		binding.titleLayout.eventColor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String currentColorKey = eventModel.getNEW_EVENT().getAsString(CalendarContract.Events.EVENT_COLOR_KEY);
				String accountName = selectedCalendarValues.getAsString(CalendarContract.Calendars.ACCOUNT_NAME);
				List<ContentValues> colors = calendarViewModel.getEventColors(accountName);
				onClickedEventColor(currentColorKey, colors);
			}
		});

        /*
        시간 allday 스위치
         */
		binding.timeLayout.timeAlldaySwitch.setOnCheckedChangeListener((compoundButton, isChecked) ->
		{
			onCheckedAllDaySwitch(isChecked);
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
		binding.accesslevelLayout.eventAccessLevel.setOnClickListener(view ->
		{
			onClickedAccessLevel(eventModel.getNEW_EVENT().getAsInteger(CalendarContract.Events.ACCESS_LEVEL));
		});

        /*
        유효성
         */
		binding.availabilityLayout.eventAvailability.setOnClickListener(view ->
		{
			onClickedAvailable(eventModel.getNEW_EVENT().getAsInteger(CalendarContract.Events.AVAILABILITY));
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
			onClickedLocation(eventModel.getNEW_EVENT().containsKey(CalendarContract.Events.EVENT_LOCATION) ?
					eventModel.getNEW_EVENT().getAsString(CalendarContract.Events.EVENT_LOCATION) : null);
		});

        /*
        참석자 상세정보 버튼
         */
		binding.attendeeLayout.showAttendeesDetail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Bundle bundle = new Bundle();

				bundle.putBoolean(CalendarContract.Events.GUESTS_CAN_MODIFY,
						eventModel.getNEW_EVENT().containsKey(Events.GUESTS_CAN_MODIFY)
								&& (eventModel.getNEW_EVENT().getAsInteger(Events.GUESTS_CAN_MODIFY) == 1));

				bundle.putBoolean(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS,
						eventModel.getNEW_EVENT().containsKey(Events.GUESTS_CAN_INVITE_OTHERS)
								&& (eventModel.getNEW_EVENT().getAsInteger(Events.GUESTS_CAN_INVITE_OTHERS) == 1));

				bundle.putBoolean(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS,
						eventModel.getNEW_EVENT().containsKey(Events.GUESTS_CAN_SEE_GUESTS)
								&& (eventModel.getNEW_EVENT().getAsInteger(Events.GUESTS_CAN_SEE_GUESTS) == 1));
				onClickedAttendeeList(bundle);
			}
		});
	}


	@Override
	protected void initDatePicker() {
		showDatePicker(null, eventModel.getEndDateTimeObj());
	}

	@Override
	protected void initTimePicker(DateTimeType dateType) {
		showTimePicker(dateType, null);
	}


	private void loadInitData() {
		//캘린더도 기본 값 설정
		List<ContentValues> calendarList = calendarViewModel.getCalendars();
		//기본 캘린더 확인
		ContentValues defaultCalendar = calendarList.get(0);
		selectedCalendarValues = defaultCalendar;
		eventModel.setCalendar(defaultCalendar.getAsInteger(CalendarContract.Calendars._ID));

		setCalendarText(defaultCalendar.getAsInteger(CalendarContract.Calendars.CALENDAR_COLOR),
				defaultCalendar.getAsString(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME),
				defaultCalendar.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));

		//event color
		List<ContentValues> colors = calendarViewModel.getEventColors(defaultCalendar.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));
		int color = colors.get(0).getAsInteger(CalendarContract.Colors.COLOR);
		String colorKey = colors.get(0).getAsString(CalendarContract.Colors.COLOR_KEY);

		eventModel.setEventColor(color, colorKey);
		binding.titleLayout.eventColor.setBackgroundColor(EventUtil.getColor(color));

		// 기기 시각으로 설정
		// 설정에서 기본 일정 시간 길이 설정가능
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		final int defaultHourRange = 60;

		DateTimeObj beginDateTimeObj = new DateTimeObj();
		DateTimeObj endDateTimeObj = new DateTimeObj();

		beginDateTimeObj.setTimeMillis(calendar.getTime().getTime());
		calendar.add(Calendar.MINUTE, defaultHourRange);

		endDateTimeObj.setTimeMillis(calendar.getTime().getTime());

		eventModel.setBeginDateTimeObj(beginDateTimeObj);
		eventModel.setEndDateTimeObj(endDateTimeObj);

		setDateText(DateTimeType.BEGIN, beginDateTimeObj, true);
		setDateText(DateTimeType.END, endDateTimeObj, true);
		setTimeText(DateTimeType.BEGIN, beginDateTimeObj);
		setTimeText(DateTimeType.END, endDateTimeObj);

		eventModel.setIsAllDay(false);
		binding.timeLayout.timeAlldaySwitch.setChecked(false);

		// 기기 시간대로 설정
		TimeZone eventTimeZone = null;
		if (App.isPreference_key_using_timezone_of_device()) {
			eventTimeZone = TimeZone.getDefault();
		} else {
			eventTimeZone = App.getPreference_key_custom_timezone();
		}
		eventModel.setEventTimeZone(eventTimeZone);
		eventModel.setCalendarTimeZone(
				selectedCalendarValues.containsKey(CalendarContract.Calendars.CALENDAR_TIME_ZONE) ?
						TimeZone.getTimeZone(selectedCalendarValues.getAsString(CalendarContract.Calendars.CALENDAR_TIME_ZONE))
						: TimeZone.getDefault());
		eventModel.setTimezone(eventTimeZone.getID());
		setTimeZoneText();

		// 접근 범위(기본)
		eventModel.setAccessLevel(CalendarContract.Events.ACCESS_DEFAULT);
		setAccessLevelText(CalendarContract.Events.ACCESS_DEFAULT);

		// 유효성(바쁨)
		eventModel.setAvailability(CalendarContract.Events.AVAILABILITY_BUSY);
		setAvailabilityText(CalendarContract.Events.AVAILABILITY_BUSY);

		// 참석자 버튼 텍스트 수정
		binding.attendeeLayout.showAttendeesDetail.setText(getString(R.string.add_attendee));
	}


	private void saveNewEvent() {
		// 시간이 바뀌는 경우, 알림 데이터도 변경해야함.
		// 알림 재설정
		ContentValues newEvent = eventModel.getNEW_EVENT();
		List<ContentValues> newReminderList = eventModel.getNEW_REMINDERS();
		List<ContentValues> newAttendeeList = eventModel.getNEW_ATTENDEES();

		if (!eventModel.getEventRecurrence().equals(EventRecurrence.EMPTY)) {
			newEvent.put(Events.RRULE, eventModel.getEventRecurrence().toString());
		}
		final boolean newIsAllDay = binding.timeLayout.timeAlldaySwitch.isChecked();

		DateTimeObj beginDateTimeObj = eventModel.getBeginDateTimeObj();
		DateTimeObj endDateTimeObj = eventModel.getEndDateTimeObj();

		long beginTimeMillis = 0L;
		long endTimeMillis = 0L;

		if (newIsAllDay) {
			Calendar utcCalendar = Calendar.getInstance(ClockUtil.UTC_TIME_ZONE);
			utcCalendar.set(beginDateTimeObj.getYear(), beginDateTimeObj.getMonth() - 1, beginDateTimeObj.getDay());
			beginTimeMillis = utcCalendar.getTimeInMillis();

			utcCalendar.set(endDateTimeObj.getYear(), endDateTimeObj.getMonth() - 1, endDateTimeObj.getDay());
			utcCalendar.add(Calendar.DATE, 1);
			endTimeMillis = utcCalendar.getTimeInMillis();
		} else {
			beginTimeMillis = beginDateTimeObj.getTimeMillis();
			endTimeMillis = endDateTimeObj.getTimeMillis();
		}

		newEvent.put(Events.DTSTART, beginTimeMillis);
		newEvent.put(Events.DTEND, endTimeMillis);

		EventHelper eventHelper = new EventHelper(new AsyncQueryService(getActivity(), (OnEditEventResultListener) calendarViewModel));
		eventHelper.saveNewEvent(newEvent, locationDTO, newReminderList, newAttendeeList, locationIntentCode);
	}


	public interface OnNewEventResultListener {
		void onSavedNewEvent(long eventId, long begin);
	}
}