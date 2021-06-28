package com.zerodsoft.scheduleweather.activity.editevent.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.content.ContentValues;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.activity.editevent.adapter.CalendarListAdapter;
import com.zerodsoft.scheduleweather.activity.editevent.fragments.TimeZoneFragment;
import com.zerodsoft.scheduleweather.activity.editevent.interfaces.IEventRepeat;
import com.zerodsoft.scheduleweather.activity.preferences.ColorListAdapter;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.common.enums.EventIntentCode;
import com.zerodsoft.scheduleweather.common.enums.LocationIntentCode;
import com.zerodsoft.scheduleweather.databinding.FragmentBaseEventBinding;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.event.common.DetailLocationSelectorKey;
import com.zerodsoft.scheduleweather.event.common.SelectionDetailLocationFragment;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.utility.NetworkStatus;
import com.zerodsoft.scheduleweather.utility.RecurrenceRule;
import com.zerodsoft.scheduleweather.utility.model.ReminderDto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public abstract class EventBaseFragment extends Fragment implements IEventRepeat {
	protected FragmentBaseEventBinding binding;
	protected NetworkStatus networkStatus;
	protected CalendarViewModel calendarViewModel;
	protected EventDataViewModel eventDataViewModel;
	protected LocationViewModel locationViewModel;
	protected boolean initializing = true;

	protected AlertDialog eventColorDialog;
	protected AlertDialog accessLevelDialog;
	protected AlertDialog availabilityDialog;
	protected AlertDialog calendarDialog;

	protected MaterialTimePicker timePicker;
	protected MaterialDatePicker<Pair<Long, Long>> datePicker;

	protected ContentValues selectedCalendarValues;
	protected LocationDTO locationDTO;


	protected enum DateTimeType {
		START,
		END
	}


	@Override
	public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		binding = FragmentBaseEventBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		networkStatus = new NetworkStatus(getContext(), new ConnectivityManager.NetworkCallback());
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		binding.titleLayout.title.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (!initializing) {
					eventDataViewModel.setTitle(s.toString());
				}
			}
		});

		binding.descriptionLayout.descriptionEdittext.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (!initializing) {
					eventDataViewModel.setDescription(s.toString());
				}
			}
		});

		binding.timeLayout.startDate.setOnClickListener(dateTimeOnClickListener);
		binding.timeLayout.startTime.setOnClickListener(dateTimeOnClickListener);
		binding.timeLayout.endDate.setOnClickListener(dateTimeOnClickListener);
		binding.timeLayout.endTime.setOnClickListener(dateTimeOnClickListener);
	}

	protected final View.OnClickListener dateTimeOnClickListener = view ->
	{
		switch (view.getId()) {
			case R.id.start_date:
			case R.id.end_date:
				initDatePicker();
				break;
			case R.id.start_time:
				initTimePicker(DateTimeType.START);
				break;
			case R.id.end_time:
				initTimePicker(DateTimeType.END);
				break;
		}
	};


	protected final void onClickedEventColor(String currentColorKey, List<ContentValues> colors) {
		GridView gridView = new GridView(getContext());
		gridView.setAdapter(new ColorListAdapter(currentColorKey, colors,
				getContext()));
		gridView.setNumColumns(5);
		gridView.setGravity(Gravity.CENTER);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final int color = colors.get(position).getAsInteger(CalendarContract.Colors.COLOR);
				final String colorKey = colors.get(position).getAsString(CalendarContract.Colors.COLOR_KEY);

				eventDataViewModel.setEventColor(color, colorKey);
				binding.titleLayout.eventColor.setBackgroundColor(EventUtil.getColor(color));

				eventColorDialog.dismiss();
			}
		});

		MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
		builder.setView(gridView);
		builder.setCancelable(false);
		builder.setTitle(R.string.title_select_event_color);

		eventColorDialog = builder.create();
		eventColorDialog.show();
	}

	protected final void onCheckedAllDaySwitch(boolean isChecked) {
		if (isChecked) {
			binding.timeLayout.startTime.setVisibility(View.GONE);
			binding.timeLayout.endTime.setVisibility(View.GONE);
			binding.timeLayout.eventTimezoneLayout.setVisibility(View.GONE);
		} else {
			binding.timeLayout.startTime.setVisibility(View.VISIBLE);
			binding.timeLayout.endTime.setVisibility(View.VISIBLE);
			binding.timeLayout.eventTimezoneLayout.setVisibility(View.VISIBLE);
		}

		if (!initializing) {
			eventDataViewModel.setIsAllDay(isChecked);
		}
	}

	protected final void onClickedTimeZone() {
		TimeZoneFragment timeZoneFragment = new TimeZoneFragment(new TimeZoneFragment.OnTimeZoneResultListener() {
			@Override
			public void onResult(TimeZone timeZone) {
				eventDataViewModel.setTimezone(timeZone.getID());
				setTimeZoneText(timeZone.getID());
				getParentFragmentManager().popBackStackImmediate();
			}
		});

		getParentFragmentManager().beginTransaction().hide(this)
				.add(R.id.fragment_container, timeZoneFragment, getString(R.string.tag_timezone_fragment))
				.addToBackStack(getString(R.string.tag_timezone_fragment)).commit();
	}

	protected final void setTimeZoneText(String eventTimeZone) {
		TimeZone timeZone = TimeZone.getTimeZone(eventTimeZone);
		binding.timeLayout.eventTimezone.setText(timeZone.getDisplayName(Locale.KOREAN));
	}

	protected final void onClickedRecurrence(String rRule, long dtStart) {
		Bundle bundle = new Bundle();
		bundle.putString(CalendarContract.Events.RRULE, rRule);
		bundle.putLong(CalendarContract.Events.DTSTART, dtStart);

		EventRecurrenceFragment eventRecurrenceFragment = new EventRecurrenceFragment(new EventRecurrenceFragment.OnEventRecurrenceResultListener() {
			@Override
			public void onResult(String rRule) {
				eventDataViewModel.setRecurrence(rRule);
				setRecurrenceText(rRule);
			}
		});
		eventRecurrenceFragment.setArguments(bundle);

		getParentFragmentManager().beginTransaction().hide(this)
				.add(R.id.fragment_container, eventRecurrenceFragment, getString(R.string.tag_event_recurrence_fragment))
				.addToBackStack(getString(R.string.tag_event_recurrence_fragment)).commit();
	}

	protected final void setRecurrenceText(String rRule) {
		if (!rRule.isEmpty()) {
			RecurrenceRule recurrenceRule = new RecurrenceRule();
			recurrenceRule.separateValues(rRule);
			binding.recurrenceLayout.eventRecurrence.setText(recurrenceRule.interpret(getContext()));
		} else {
			binding.recurrenceLayout.eventRecurrence.setText("");
		}
	}

	protected final void onClickedAccessLevel(int currentAccessLevel) {
		int checkedItem = currentAccessLevel;

		if (checkedItem == 3) {
			checkedItem = 1;
		}

		MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
		dialogBuilder.setSingleChoiceItems(EventUtil.getAccessLevelItems(requireContext()), checkedItem, (dialogInterface, item) ->
		{
			int accessLevel = 0;

			switch (item) {
				case 0:
					accessLevel = CalendarContract.Events.ACCESS_DEFAULT;
					break;
				case 1:
					accessLevel = CalendarContract.Events.ACCESS_PUBLIC;
					break;
				case 2:
					accessLevel = CalendarContract.Events.ACCESS_PRIVATE;
					break;
			}

			eventDataViewModel.setAccessLevel(accessLevel);
			setAccessLevelText(accessLevel);
			accessLevelDialog.dismiss();
		}).setTitle(getString(R.string.accesslevel));

		accessLevelDialog = dialogBuilder.create();
		accessLevelDialog.show();
	}

	protected final void setAccessLevelText(int accessLevel) {
		binding.accesslevelLayout.eventAccessLevel.setText(EventUtil.convertAccessLevel(accessLevel, getContext()));
	}

	protected final void onClickedAvailable(int currentAvailable) {
		MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
		dialogBuilder.setSingleChoiceItems(EventUtil.getAvailabilityItems(requireContext()), currentAvailable, (dialogInterface, item) ->
		{
			int availability = 0;

			switch (item) {
				case 0:
					availability = CalendarContract.Events.AVAILABILITY_BUSY;
					break;
				case 1:
					availability = CalendarContract.Events.AVAILABILITY_FREE;
					break;
			}

			eventDataViewModel.setAvailability(availability);
			setAvailabilityText(availability);
			availabilityDialog.dismiss();

		}).setTitle(getString(R.string.availability));

		availabilityDialog = dialogBuilder.create();
		availabilityDialog.show();
	}

	protected final void setAvailabilityText(int availability) {
		binding.availabilityLayout.eventAvailability.setText(EventUtil.convertAvailability(availability, getContext()));
	}

	protected final void onClickedCalendar() {
		MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
		dialogBuilder
				.setTitle(getString(R.string.calendar))
				.setAdapter(new CalendarListAdapter(getContext(), calendarViewModel.getCalendars())
						, (dialogInterface, position) ->
						{
							selectedCalendarValues = (ContentValues) calendarDialog.getListView().getAdapter().getItem(position);
							eventDataViewModel.setCalendar(selectedCalendarValues.getAsInteger(CalendarContract.Calendars._ID));

							setCalendarText(selectedCalendarValues.getAsInteger(CalendarContract.Calendars.CALENDAR_COLOR),
									selectedCalendarValues.getAsString(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME),
									selectedCalendarValues.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));

							String accountName = selectedCalendarValues.getAsString(CalendarContract.Calendars.ACCOUNT_NAME);
							List<ContentValues> colors = calendarViewModel.getEventColors(accountName);

							int newEventColor = colors.get(0).getAsInteger(CalendarContract.Colors.COLOR);
							String newEventColorKey = colors.get(0).getAsString(CalendarContract.Colors.COLOR_KEY);

							eventDataViewModel.setEventColor(newEventColor, newEventColorKey);
							binding.titleLayout.eventColor.setBackgroundColor(EventUtil.getColor(newEventColor));

							if (!eventDataViewModel.getATTENDEES().isEmpty()) {
								eventDataViewModel.removeAttendee(accountName);
								createAttendeeListView();
							}
						});

		calendarDialog = dialogBuilder.create();
		calendarDialog.show();
	}

	protected final void setCalendarText(int calendarColor, String calendarDisplayName, String accountName) {
		binding.calendarLayout.calendarColor.setBackgroundColor(EventUtil.getColor(calendarColor));
		binding.calendarLayout.calendarDisplayName.setText(calendarDisplayName);
		binding.calendarLayout.calendarAccountName.setText(accountName);
	}

	protected final void createAttendeeListView() {
		if (binding.attendeeLayout.eventAttendeesTable.getChildCount() > 0) {
			binding.attendeeLayout.eventAttendeesTable.removeAllViews();
		}

		List<ContentValues> attendeeList = eventDataViewModel.getATTENDEES();

		if (attendeeList.isEmpty()) {
			// 참석자 버튼 텍스트 수정
			binding.attendeeLayout.showAttendeesDetail.setText(getString(R.string.add_attendee));
		} else {
			binding.attendeeLayout.showAttendeesDetail.setText(getString(R.string.show_attendees));

			for (ContentValues attendee : attendeeList) {
				addAttendeeItemView(attendee);
			}
		}

	}

	protected final void addAttendeeItemView(ContentValues attendee) {
		TableRow tableRow = new TableRow(getContext());
		LinearLayout row = (LinearLayout) getLayoutInflater().inflate(R.layout.event_attendee_item, null);

		// add row to table
		LinearLayout attendeeInfoLayout = row.findViewById(R.id.attendee_info_layout);
		TextView attendeeEmailView = (TextView) row.findViewById(R.id.attendee_name);
		ImageButton removeButton = (ImageButton) row.findViewById(R.id.remove_attendee_button);

		((LinearLayout) attendeeInfoLayout.findViewById(R.id.attendee_relationship_status_layout)).setVisibility(View.GONE);

		final AttendeeItemHolder holder = new AttendeeItemHolder(attendee.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL));
		tableRow.setTag(holder);
		removeButton.setTag(holder);

		attendeeInfoLayout.setClickable(true);
		attendeeInfoLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// logic for communications with attendee
			}
		});

		removeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				AttendeeItemHolder holder = (AttendeeItemHolder) view.getTag();
				eventDataViewModel.removeAttendee(holder.email);
				removeAttendeeItemView(holder.email);
			}
		});
		// 이름, 메일 주소, 상태
		// 조직자 - attendeeName, 그 외 - email
		// email값을 항상 존재
		// 요약 - 캘린더명과 나
		// 상세 - 나(이메일), 캘린더(이메일)
        /*
        참석자 : a(organizer), b

        <구글 캘린더>
        주최자의 캘린더에서 이벤트를 볼때 : 참석자 1명, a(주최자), b
        수정 할때 : b

        참석자의 캘린더에서 이벤트를 볼때 : 참석자 1명, a(주최자), b
        수정 할때 : a, b(나)
         */
		String attendeeName = attendee.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL);
		attendeeEmailView.setText(attendeeName);

		tableRow.addView(row);
		binding.attendeeLayout.eventAttendeesTable.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
	}


	protected final void removeAttendeeItemView(String email) {
		// 아이템 삭제
		final int rowCount = binding.attendeeLayout.eventAttendeesTable.getChildCount();

		for (int row = 0; row < rowCount; row++) {
			AttendeeItemHolder holder = (AttendeeItemHolder) binding.attendeeLayout.eventAttendeesTable.getChildAt(row).getTag();

			if (holder.email.equals(email)) {
				binding.attendeeLayout.eventAttendeesTable.removeViewAt(row);
				break;
			}
		}
		if (eventDataViewModel.getATTENDEES().isEmpty()) {
			binding.attendeeLayout.showAttendeesDetail.setText(getString(R.string.add_attendee));
		}
	}

	protected final void onClickedNewReminder() {
		Bundle bundle = new Bundle();
		bundle.putInt("requestCode", EventIntentCode.REQUEST_ADD_REMINDER.value());

		EventReminderFragment eventReminderFragment = new EventReminderFragment(new EventReminderFragment.OnEventReminderResultListener() {
			@Override
			public void onResultModifiedReminder(ContentValues reminder, int previousMinutes) {

			}

			@Override
			public void onResultAddedReminder(ContentValues reminder) {
				// reminder values는 분, 메소드값을 담고 있어야 한다
				// 수정된 minutes, method가 기존 값과 중복되는 경우 진행하지 않음
				if (eventDataViewModel.addReminder(reminder.getAsInteger(CalendarContract.Reminders.MINUTES),
						reminder.getAsInteger(CalendarContract.Reminders.METHOD))) {
					addReminderItemView(reminder);
				} else {
					Toast.makeText(getContext(), R.string.duplicate_value, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onResultRemovedReminder(int previousMinutes) {

			}
		});
		eventReminderFragment.setArguments(bundle);
		getParentFragmentManager().beginTransaction().hide(this)
				.add(R.id.fragment_container, eventReminderFragment, getString(R.string.tag_event_reminder_fragment))
				.addToBackStack(getString(R.string.tag_event_reminder_fragment)).commit();
	}

	protected final void addReminderItemView(ContentValues reminder) {
		final int minutes = reminder.getAsInteger(CalendarContract.Reminders.MINUTES);
		final int method = reminder.getAsInteger(CalendarContract.Reminders.METHOD);

		TableRow tableRow = new TableRow(getContext());
		LinearLayout row = (LinearLayout) getLayoutInflater().inflate(R.layout.event_reminder_item, null);

		TextView reminderValueTextView = ((TextView) row.findViewById(R.id.reminder_value));
		ImageButton removeButton = ((ImageButton) row.findViewById(R.id.remove_reminder_button));

		reminderValueTextView.setOnClickListener(reminderItemOnClickListener);
		removeButton.setOnClickListener(removeReminderOnClickListener);

		final ReminderItemHolder holder = new ReminderItemHolder(minutes, method);
		tableRow.setTag(holder);
		reminderValueTextView.setTag(holder);
		removeButton.setTag(holder);

		ReminderDto reminderDto = EventUtil.convertAlarmMinutes(minutes);
		String alarmValueText = EventUtil.makeAlarmText(reminderDto, getContext());

		String text = alarmValueText + "(" + EventUtil.getReminderMethod(getContext(), method) + ")";
		reminderValueTextView.setText(text);

		tableRow.addView(row);
		binding.reminderLayout.remindersTable.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
	}


	private final void removeReminderItemView(int minutes) {
		final int rowCount = binding.reminderLayout.remindersTable.getChildCount();
		eventDataViewModel.removeReminder(minutes);

		// 아이템 삭제
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			ReminderItemHolder holder = (ReminderItemHolder) binding.reminderLayout.remindersTable.getChildAt(rowIndex).getTag();
			if (holder.minutes == minutes) {
				binding.reminderLayout.remindersTable.removeViewAt(rowIndex);
				break;
			}
		}
	}

	protected final void modifyReminderItemView(int previousMinutes, int newMinutes, int newMethod) {
		final int rowCount = binding.reminderLayout.remindersTable.getChildCount();

		// 아이템 수정
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			ReminderItemHolder holder = (ReminderItemHolder) binding.reminderLayout.remindersTable.getChildAt(rowIndex).getTag();

			if (holder.minutes == previousMinutes) {
				holder.minutes = newMinutes;
				holder.method = newMethod;

				ReminderDto reminderDto = EventUtil.convertAlarmMinutes(newMinutes);
				String alarmValueText = EventUtil.makeAlarmText(reminderDto, getContext());

				String text = alarmValueText + "(" + EventUtil.getReminderMethod(getContext(), newMethod) + ")";

				View row = binding.reminderLayout.remindersTable.getChildAt(rowIndex);
				((TextView) row.findViewById(R.id.reminder_value)).setText(text);

				break;
			}
		}
	}

	protected final View.OnClickListener reminderItemOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			ReminderItemHolder holder = (ReminderItemHolder) view.getTag();
			// modify
			Bundle bundle = new Bundle();
			bundle.putInt("previousMinutes", holder.minutes);
			bundle.putInt("previousMethod", holder.method);
			bundle.putInt("requestCode", EventIntentCode.REQUEST_MODIFY_REMINDER.value());

			EventReminderFragment eventReminderFragment = new EventReminderFragment(new EventReminderFragment.OnEventReminderResultListener() {
				@Override
				public void onResultModifiedReminder(ContentValues reminder, int previousMinutes) {
					// 수정된 minutes, method가 기존 값과 중복되는 경우 진행하지 않음
					int newMinutes = reminder.getAsInteger(CalendarContract.Reminders.MINUTES);
					int newMethod = reminder.getAsInteger(CalendarContract.Reminders.METHOD);

					eventDataViewModel.modifyReminder(previousMinutes, newMinutes, newMethod);
					modifyReminderItemView(previousMinutes, newMinutes, newMethod);
				}

				@Override
				public void onResultAddedReminder(ContentValues reminder) {

				}

				@Override
				public void onResultRemovedReminder(int previousMinutes) {
					eventDataViewModel.removeReminder(previousMinutes);
					removeReminderItemView(previousMinutes);
				}
			});
			eventReminderFragment.setArguments(bundle);
			getParentFragmentManager().beginTransaction().hide(EventBaseFragment.this)
					.add(R.id.fragment_container, eventReminderFragment, getString(R.string.tag_event_reminder_fragment))
					.addToBackStack(getString(R.string.tag_event_reminder_fragment)).commit();
		}
	};

	protected final View.OnClickListener removeReminderOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			ReminderItemHolder holder = (ReminderItemHolder) view.getTag();
			removeReminderItemView(holder.minutes);
		}
	};

	protected final void showDatePicker(long finalDtStart, long finalDtEnd,
	                                    @Nullable ModifyInstanceFragment.OnModifiedDateTimeCallback onModifiedDateTimeCallback) {
		MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
		datePicker = builder.setTitleText(R.string.datepicker)
				.setSelection(new Pair<>(finalDtStart, finalDtEnd))
				.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
				.build();
		datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
			@Override
			public void onPositiveButtonClick(Pair<Long, Long> selection) {
				Calendar calendar = Calendar.getInstance();
				int previousHour = 0;
				int previousMinute = 0;

				Calendar startDate = Calendar.getInstance();
				Calendar endDate = Calendar.getInstance();

				if (selection.first != null) {
					calendar.setTimeInMillis(finalDtStart);
					previousHour = calendar.get(Calendar.HOUR_OF_DAY);
					previousMinute = calendar.get(Calendar.MINUTE);

					calendar.setTimeInMillis(selection.first);
					calendar.set(Calendar.HOUR_OF_DAY, previousHour);
					calendar.set(Calendar.MINUTE, previousMinute);

					eventDataViewModel.setDtStart(calendar.getTime());
					setDateText(DateTimeType.START, calendar.getTimeInMillis());

					startDate.setTime(calendar.getTime());
				}
				if (selection.second != null) {
					calendar.setTimeInMillis(finalDtEnd);
					previousHour = calendar.get(Calendar.HOUR_OF_DAY);
					previousMinute = calendar.get(Calendar.MINUTE);

					calendar.setTimeInMillis(selection.second);
					calendar.set(Calendar.HOUR_OF_DAY, previousHour);
					calendar.set(Calendar.MINUTE, previousMinute);

					eventDataViewModel.setDtEnd(calendar.getTime());
					setDateText(DateTimeType.END, calendar.getTimeInMillis());

					endDate.setTime(calendar.getTime());
				}

				if (startDate.get(Calendar.HOUR_OF_DAY) >= endDate.get(Calendar.HOUR_OF_DAY)) {
					//시작 날짜가 종료 날짜보다 이후이면 시각을 조정한다
					if (startDate.get(Calendar.HOUR_OF_DAY) > endDate.get(Calendar.HOUR_OF_DAY)) {
						startDate.set(Calendar.HOUR_OF_DAY, 9);
						startDate.set(Calendar.MINUTE, 0);

						endDate.set(Calendar.HOUR_OF_DAY, 10);
						endDate.set(Calendar.MINUTE, 0);
					} else if (startDate.get(Calendar.MINUTE) > endDate.get(Calendar.MINUTE)) {
						startDate.set(Calendar.MINUTE, 0);
						endDate.set(Calendar.MINUTE, 30);
					}
					eventDataViewModel.setDtStart(startDate.getTime());
					eventDataViewModel.setDtEnd(endDate.getTime());
					setTimeText(DateTimeType.START, startDate.getTime().getTime());
					setTimeText(DateTimeType.END, endDate.getTime().getTime());
				}

				if (onModifiedDateTimeCallback != null) {
					onModifiedDateTimeCallback.onModified();
				}
				datePicker.dismiss();
			}
		});
		datePicker.addOnNegativeButtonClickListener(view ->
		{
			datePicker.dismiss();
		});

		datePicker.show(getChildFragmentManager(), datePicker.toString());
	}

	protected final void setDateText(DateTimeType dateType, long date) {
		if (dateType == DateTimeType.START) {
			binding.timeLayout.startDate.setText(EventUtil.convertDate(date));
		} else {
			binding.timeLayout.endDate.setText(EventUtil.convertDate(date));
		}
	}

	protected final void showTimePicker(DateTimeType dateType, Calendar calendar, Calendar compareCalendar,
	                                    @Nullable ModifyInstanceFragment.OnModifiedDateTimeCallback onModifiedDateTimeCallback) {
		MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder();
		timePicker =
				builder.setTitleText((dateType == DateTimeType.START ? getString(R.string.start) : getString(R.string.end)) + getString(R.string.timepicker))
						.setTimeFormat(App.isPreference_key_using_24_hour_system() ? TimeFormat.CLOCK_24H : TimeFormat.CLOCK_12H)
						.setHour(calendar.get(Calendar.HOUR_OF_DAY))
						.setMinute(calendar.get(Calendar.MINUTE))
						.setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK).build();

		timePicker.addOnPositiveButtonClickListener(view ->
		{
			calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
			calendar.set(Calendar.MINUTE, timePicker.getMinute());

			if (dateType == DateTimeType.START) {
				if (calendar.getTimeInMillis() <= compareCalendar.getTimeInMillis()) {
					eventDataViewModel.setDtStart(calendar.getTime());
					setTimeText(dateType, calendar.getTimeInMillis());
				} else {
					String msg =
							EventUtil.convertTime(calendar.getTimeInMillis(), App.isPreference_key_using_24_hour_system()) + " " + getString(R.string.plz_set_time_before_specific_time);
					Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
				}
			} else if (dateType == DateTimeType.END) {
				if (calendar.getTimeInMillis() >= compareCalendar.getTimeInMillis()) {
					eventDataViewModel.setDtEnd(calendar.getTime());
					setTimeText(dateType, calendar.getTimeInMillis());
				} else {
					String msg =
							EventUtil.convertTime(calendar.getTimeInMillis(), App.isPreference_key_using_24_hour_system()) + " " + getString(R.string.plz_set_time_after_specific_time);
					Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
				}
			}

			if (onModifiedDateTimeCallback != null) {
				onModifiedDateTimeCallback.onModified();
			}
		});
		timePicker.addOnNegativeButtonClickListener(view ->
		{
			timePicker.dismiss();
		});
		timePicker.show(getChildFragmentManager(), timePicker.toString());
	}

	protected final void setTimeText(DateTimeType dateType, long time) {
		// 설정에 12시간, 24시간 단위 변경 가능
		if (dateType == DateTimeType.START) {
			binding.timeLayout.startTime.setText(EventUtil.convertTime(time, App.isPreference_key_using_24_hour_system()));
		} else {
			binding.timeLayout.endTime.setText(EventUtil.convertTime(time, App.isPreference_key_using_24_hour_system()));
		}
	}

	protected abstract void initDatePicker();

	protected abstract void initTimePicker(DateTimeType dateType);

	protected final void convertDtEndForAllDay(ContentValues contentValues) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(contentValues.getAsLong(CalendarContract.Events.DTEND));
		calendar.add(Calendar.DAY_OF_YEAR, 1);

		contentValues.put(CalendarContract.Events.DTEND, calendar.getTimeInMillis());
	}

	protected final void onClickedLocation(@Nullable String eventLocation) {
		//위치를 설정하는 액티비티 표시
		if (networkStatus.networkAvailable()) {
			SelectionDetailLocationFragment selectionDetailLocationFragment = new SelectionDetailLocationFragment(new SelectionDetailLocationFragment.OnDetailLocationSelectionResultListener() {
				@Override
				public void onResultChangedLocation(LocationDTO newLocation) {
					locationDTO = newLocation;
					String resultLocation;

					if (newLocation.getLocationType() == LocationType.ADDRESS) {
						resultLocation = newLocation.getAddressName();
					} else {
						resultLocation = newLocation.getPlaceName();
					}

					eventDataViewModel.setEventLocation(resultLocation);
					binding.locationLayout.eventLocation.setText(resultLocation);
				}

				@Override
				public void onResultSelectedLocation(LocationDTO newLocation) {
					locationDTO = newLocation;
					String resultLocation;

					if (newLocation.getLocationType() == LocationType.ADDRESS) {
						resultLocation = newLocation.getAddressName();
					} else {
						resultLocation = newLocation.getPlaceName();
					}

					eventDataViewModel.setEventLocation(resultLocation);
					binding.locationLayout.eventLocation.setText(resultLocation);
				}

				@Override
				public void onResultUnselectedLocation() {
					eventDataViewModel.setEventLocation("");
					locationDTO = null;
					binding.locationLayout.eventLocation.setText("");
				}

			});

			/*
1 .EVENT_LOCATION 값
2. 상세 위치 데이터

- EVENT_LOCATION O
상세 위치 O:
상세 위치 데이터를 지도에 표시
(위치 수정)

- EVENT_LOCATION O
상세 위치 X:
해당 값을 주소/장소 검색
(위치 추가)

- EVENT_LOCATION X
상세 위치 O:
상세 위치를 삭제하고
새로운 위치를 선택
(위치 추가)

- EVENT_LOCATION X
상세 위치 X:
(위치 추가)
			 */
			Bundle bundle = new Bundle();

			if (eventLocation != null) {
				if (locationDTO != null) {
					bundle.putParcelable(DetailLocationSelectorKey.SELECTED_LOCATION_DTO_IN_EVENT.value(), locationDTO);
					bundle.putInt("requestCode", LocationIntentCode.REQUEST_CODE_CHANGE_LOCATION.value());
				} else {
					bundle.putString(DetailLocationSelectorKey.LOCATION_NAME_IN_EVENT.value(), eventLocation);
					bundle.putInt("requestCode", LocationIntentCode.REQUEST_CODE_SELECT_LOCATION_BY_QUERY.value());
				}
			} else {
				if (locationDTO != null) {
					locationViewModel.removeLocation(locationDTO.getEventId(), null);
					locationDTO = null;
					bundle.putInt("requestCode", LocationIntentCode.REQUEST_CODE_SELECT_LOCATION_EMPTY_QUERY.value());
				} else {
					bundle.putInt("requestCode", LocationIntentCode.REQUEST_CODE_SELECT_LOCATION_EMPTY_QUERY.value());
				}
			}

			selectionDetailLocationFragment.setArguments(bundle);
			getParentFragmentManager().beginTransaction().hide(this).add(R.id.fragment_container
					, selectionDetailLocationFragment, getString(R.string.tag_detail_location_selection_fragment))
					.addToBackStack(getString(R.string.tag_detail_location_selection_fragment)).commit();
		} else {
			networkStatus.showToastDisconnected();
		}
	}

	protected final void onClickedAttendeeList(Bundle bundle) {
		ContentValues organizer = new ContentValues();

		organizer.put(CalendarContract.Attendees.ATTENDEE_NAME, selectedCalendarValues.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));
		organizer.put(CalendarContract.Attendees.ATTENDEE_EMAIL, selectedCalendarValues.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));
		organizer.put(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP, CalendarContract.Attendees.RELATIONSHIP_ORGANIZER);

		bundle.putParcelableArrayList("attendeeList", (ArrayList<? extends Parcelable>) eventDataViewModel.getATTENDEES());
		bundle.putParcelable("organizer", organizer);

		AttendeesFragment attendeesFragment = new AttendeesFragment(new AttendeesFragment.OnAttendeesResultListener() {
			@Override
			public void onResult(List<ContentValues> newAttendeeList, boolean guestsCanModify, boolean guestsCanInviteOthers, boolean guestsCanSeeGuests) {
				eventDataViewModel.setAttendees(newAttendeeList, guestsCanModify, guestsCanInviteOthers, guestsCanSeeGuests);
				createAttendeeListView();
			}
		});

		attendeesFragment.setArguments(bundle);
		getParentFragmentManager().beginTransaction().hide(this)
				.add(R.id.fragment_container, attendeesFragment, getString(R.string.tag_attendees_fragment))
				.addToBackStack(getString(R.string.tag_attendees_fragment)).commit();
	}

	protected abstract void setViewOnClickListeners();

	static final class ReminderItemHolder {
		int minutes;
		int method;

		protected ReminderItemHolder(int minutes, int method) {
			this.minutes = minutes;
			this.method = method;
		}
	}

	static final class AttendeeItemHolder {
		String email;

		public AttendeeItemHolder(String email) {
			this.email = email;
		}
	}
}

