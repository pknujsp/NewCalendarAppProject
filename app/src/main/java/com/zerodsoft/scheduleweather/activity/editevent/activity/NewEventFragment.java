package com.zerodsoft.scheduleweather.activity.editevent.activity;

import android.content.ContentValues;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
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

public class NewEventFragment extends EventBaseFragment {
	private OnNewEventResultListener onNewEventResultListener;

	public NewEventFragment(OnNewEventResultListener onNewEventResultListener) {
		this.onNewEventResultListener = onNewEventResultListener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		eventDataViewModel = new ViewModelProvider(this).get(EventDataViewModel.class);
		calendarViewModel = new ViewModelProvider(requireActivity()).get(CalendarViewModel.class);
		locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
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
				saveNewEvent();
			}
		});
	}

	@Override
	protected void loadInitData() {
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

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		final int defaultHourRange = 60;

		Date[] defaultDateTimes = new Date[2];
		defaultDateTimes[0] = calendar.getTime();
		calendar.add(Calendar.MINUTE, defaultHourRange);
		defaultDateTimes[1] = calendar.getTime();

		eventDataViewModel.setDtStart(defaultDateTimes[0]);
		eventDataViewModel.setDtEnd(defaultDateTimes[1]);

		setDateText(DateTimeType.START, defaultDateTimes[0].getTime());
		setDateText(DateTimeType.END, defaultDateTimes[1].getTime());
		setTimeText(DateTimeType.START, defaultDateTimes[0].getTime());
		setTimeText(DateTimeType.END, defaultDateTimes[1].getTime());

		eventDataViewModel.setIsAllDay(false);

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


	protected void saveNewEvent() {
		// 시간이 바뀌는 경우, 알림 데이터도 변경해야함.
		// 알림 재설정
		ContentValues newEvent = eventDataViewModel.getNEW_EVENT();
		List<ContentValues> newReminderList = eventDataViewModel.getREMINDERS();
		List<ContentValues> newAttendeeList = eventDataViewModel.getATTENDEES();

		//allday이면 dtEnd를 다음 날로 설정
		if (newEvent.getAsInteger(CalendarContract.Events.ALL_DAY) == 1) {
			convertDtEndForAllDay(newEvent);
		}

		final long NEW_EVENT_ID = calendarViewModel.addEvent(newEvent);

		if (!newReminderList.isEmpty()) {
			for (ContentValues reminder : newReminderList) {
				reminder.put(CalendarContract.Reminders.EVENT_ID, NEW_EVENT_ID);
			}
			calendarViewModel.addReminders(newReminderList);
		}

		if (!newAttendeeList.isEmpty()) {
			for (ContentValues attendee : newAttendeeList) {
				attendee.put(CalendarContract.Attendees.EVENT_ID, NEW_EVENT_ID);
			}
			calendarViewModel.addAttendees(newAttendeeList);
		}

		if (newEvent.containsKey(CalendarContract.Events.EVENT_LOCATION)) {
			locationDTO.setEventId(NEW_EVENT_ID);
			locationViewModel.addLocation(locationDTO, new DbQueryCallback<LocationDTO>() {
				@Override
				public void onResultSuccessful(LocationDTO result) {
					onNewEventResultListener.onSavedNewEvent(NEW_EVENT_ID, newEvent.getAsLong(CalendarContract.Events.DTSTART));
					getParentFragmentManager().popBackStack();
				}

				@Override
				public void onResultNoData() {

				}
			});
		} else {
			onNewEventResultListener.onSavedNewEvent(NEW_EVENT_ID, newEvent.getAsLong(CalendarContract.Events.DTSTART));
			getParentFragmentManager().popBackStack();
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

	@Override
	protected void onCheckedAllDaySwitch() {

	}

	@Override
	protected long showingDatePicker(DateTimeType dateTimeType) {
		return 0;
	}

	@Override
	protected void selectedDate() {

	}

	@Override
	protected long showingTimePicker(DateTimeType dateTimeType) {
		return 0;
	}

	@Override
	protected void selectedTime(DateTimeType dateTimeType) {

	}

	public interface OnNewEventResultListener {
		void onSavedNewEvent(long eventId, long begin);
	}
}
