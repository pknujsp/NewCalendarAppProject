package com.zerodsoft.scheduleweather.activity.editevent.activity;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.zerodsoft.scheduleweather.activity.editevent.fragments.RecurrenceFragment;
import com.zerodsoft.scheduleweather.activity.editevent.fragments.TimeZoneFragment;
import com.zerodsoft.scheduleweather.activity.editevent.interfaces.IEventRepeat;
import com.zerodsoft.scheduleweather.activity.preferences.ColorListAdapter;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendar.calendarcommon2.EventRecurrence;
import com.zerodsoft.scheduleweather.calendar.dto.DateTimeObj;
import com.zerodsoft.scheduleweather.common.enums.EventIntentCode;
import com.zerodsoft.scheduleweather.common.enums.LocationIntentCode;
import com.zerodsoft.scheduleweather.common.interfaces.IFragmentTitle;
import com.zerodsoft.scheduleweather.databinding.FragmentBaseEventBinding;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.event.common.DetailLocationSelectorKey;
import com.zerodsoft.scheduleweather.event.common.SelectionDetailLocationFragment;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.NetworkStatus;
import com.zerodsoft.scheduleweather.utility.model.ReminderDto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public abstract class EventBaseFragment extends Fragment implements IEventRepeat, IFragmentTitle {
	protected FragmentBaseEventBinding binding;
	protected NetworkStatus networkStatus;
	protected CalendarViewModel calendarViewModel;
	protected EventModel eventModel;
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
	protected LocationIntentCode locationIntentCode;

	protected enum DateTimeType {
		BEGIN,
		END
	}

	protected final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
		@Override
		public void handleOnBackPressed() {
			if (!getChildFragmentManager().popBackStackImmediate()) {
				getParentFragmentManager().popBackStackImmediate();
			}
		}
	};

	@Override
	public void setTitle(String title) {
		binding.fragmentTitle.setText(title);
	}

	@Override
	public void onAttach(@NonNull @NotNull Context context) {
		super.onAttach(context);
		requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
	}

	private final FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
		@Override
		public void onFragmentAttached(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f, @NonNull @NotNull Context context) {
			super.onFragmentAttached(fm, f, context);

			if (f instanceof TimeZoneFragment || f instanceof EventReminderFragment
					|| f instanceof SelectionDetailLocationFragment || f instanceof AttendeesFragment || f instanceof RecurrenceFragment) {

				if (f instanceof TimeZoneFragment) {
					setTitle(getString(R.string.preference_title_custom_timezone));
				} else if (f instanceof EventReminderFragment) {
					setTitle(getString(R.string.reminder));
				} else if (f instanceof SelectionDetailLocationFragment) {
					getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
					setTitle(getString(R.string.location));
				} else if (f instanceof AttendeesFragment) {
					setTitle(getString(R.string.attendee));
				} else if (f instanceof RecurrenceFragment) {
					setTitle(getString(R.string.recurrence));
				}

				binding.fragmentContainer.setVisibility(View.VISIBLE);
				binding.scheduleScrollView.setVisibility(View.GONE);
				binding.saveBtn.setVisibility(View.GONE);
			}
		}

		@Override
		public void onFragmentDestroyed(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f) {
			super.onFragmentDestroyed(fm, f);
			setOriginalMainFragmentTitle();

			if (f instanceof TimeZoneFragment || f instanceof EventReminderFragment
					|| f instanceof SelectionDetailLocationFragment || f instanceof AttendeesFragment || f instanceof RecurrenceFragment) {

				binding.fragmentContainer.setVisibility(View.GONE);
				binding.scheduleScrollView.setVisibility(View.VISIBLE);
				binding.saveBtn.setVisibility(View.VISIBLE);

				if (f instanceof SelectionDetailLocationFragment) {
					getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
				}
			}
		}
	};

	protected abstract void setOriginalMainFragmentTitle();

	@Override
	public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		binding = FragmentBaseEventBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		networkStatus = new NetworkStatus(getContext(), new ConnectivityManager.NetworkCallback() {
			@Override
			public void onLost(Network network) {
				super.onLost(network);

				if (getActivity() != null) {
					FragmentManager fragmentManager = getChildFragmentManager();

					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (fragmentManager.findFragmentByTag(getString(R.string.tag_detail_location_selection_fragment)) != null) {
								fragmentManager.popBackStackImmediate();
							}
						}
					});
				}

			}
		});
		getChildFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, false);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
		onBackPressedCallback.remove();
		getChildFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		binding.backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressedCallback.handleOnBackPressed();
			}
		});

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
					eventModel.setTitle(s.length() > 0 ? s.toString() : "");
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
					eventModel.setDescription(s.length() > 0 ? s.toString() : "");
				}
			}
		});

		binding.timeLayout.startDate.setOnClickListener(dateTimeOnClickListener);
		binding.timeLayout.startTime.setOnClickListener(dateTimeOnClickListener);
		binding.timeLayout.endDate.setOnClickListener(dateTimeOnClickListener);
		binding.timeLayout.endTime.setOnClickListener(dateTimeOnClickListener);

		binding.attendeeLayout.answerLayout.setVisibility(View.GONE);
	}

	protected final View.OnClickListener dateTimeOnClickListener = view ->
	{
		switch (view.getId()) {
			case R.id.start_date:
			case R.id.end_date:
				initDatePicker();
				break;
			case R.id.start_time:
				initTimePicker(DateTimeType.BEGIN);
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

		int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, getResources().getDisplayMetrics());
		gridView.setPadding(padding, padding, padding, padding);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final int color = colors.get(position).getAsInteger(CalendarContract.Colors.COLOR);
				final String colorKey = colors.get(position).getAsString(CalendarContract.Colors.COLOR_KEY);

				eventModel.setEventColor(color, colorKey);
				binding.titleLayout.eventColor.setBackgroundColor(EventUtil.getColor(color));

				eventColorDialog.dismiss();
			}
		});

		MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
		builder.setView(gridView);
		builder.setTitle(R.string.title_select_event_color);

		eventColorDialog = builder.create();
		eventColorDialog.show();
	}

	protected final void onCheckedAllDaySwitch(boolean isChecked) {
		binding.timeLayout.startTime.setVisibility(isChecked ? View.GONE : View.VISIBLE);
		binding.timeLayout.endTime.setVisibility(isChecked ? View.GONE : View.VISIBLE);
		binding.timeLayout.eventTimezoneLayout.setVisibility(isChecked ? View.GONE : View.VISIBLE);

		DateTimeObj beginDateTimeObj = eventModel.getBeginDateTimeObj();
		DateTimeObj endDateTimeObj = eventModel.getEndDateTimeObj();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		beginDateTimeObj.setTimeMillis(calendar.getTimeInMillis());
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		endDateTimeObj.setTimeMillis(calendar.getTimeInMillis());

		setDateText(DateTimeType.BEGIN, beginDateTimeObj, false);
		setDateText(DateTimeType.END, endDateTimeObj, false);
		setTimeText(DateTimeType.BEGIN, beginDateTimeObj);
		setTimeText(DateTimeType.END, endDateTimeObj);

		if (!initializing) {
			eventModel.setIsAllDay(isChecked);
		} else {
		}
	}

	protected final void onClickedTimeZone() {
		TimeZoneFragment timeZoneFragment = new TimeZoneFragment(new TimeZoneFragment.OnTimeZoneResultListener() {
			@Override
			public void onResult(TimeZone timeZone) {
				eventModel.setEventTimeZone(timeZone);
				eventModel.setTimezone(timeZone.getID());
				setTimeZoneText();
				getChildFragmentManager().popBackStackImmediate();
			}
		});

		getChildFragmentManager().beginTransaction()
				.add(R.id.fragment_container, timeZoneFragment, getString(R.string.tag_timezone_fragment))
				.addToBackStack(getString(R.string.tag_timezone_fragment)).commit();
	}

	protected final void setTimeZoneText() {
		TimeZone eventTimeZone = eventModel.getEventTimeZone();

		if (this instanceof ModifyInstanceFragment) {
			binding.timeLayout.eventTimezone.setText(eventTimeZone.getDisplayName(Locale.KOREAN));
			return;
		}
		TimeZone calendarTimeZone = eventModel.getCalendarTimeZone();

		if (eventTimeZone.equals(calendarTimeZone)) {
			binding.timeLayout.eventTimezone.setText(eventTimeZone.getDisplayName(Locale.KOREAN));
		} else {
			DateTimeObj beginDateTimeObj = eventModel.getBeginDateTimeObj();
			DateTimeObj endDateTimeObj = eventModel.getEndDateTimeObj();

			String beginDateStr = EventUtil.getDateTimeStr(beginDateTimeObj.getCalendar(), false);
			String endDateStr = EventUtil.getDateTimeStr(endDateTimeObj.getCalendar(), false);

			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(eventTimeZone.getDisplayName(Locale.KOREAN));
			stringBuilder.append("\n(");
			stringBuilder.append(beginDateStr);
			stringBuilder.append(" -> ");
			stringBuilder.append(endDateStr);
			stringBuilder.append(")");

			binding.timeLayout.eventTimezone.setText(stringBuilder.toString());
		}
	}


	protected final void onClickedRecurrence(String rRule, long dtStart) {
		Bundle bundle = new Bundle();
		bundle.putString(Events.RRULE, rRule);
		bundle.putLong(Events.DTSTART, dtStart);

		RecurrenceFragment recurrenceFragment = new RecurrenceFragment(new RecurrenceFragment.OnResultRecurrence() {
			@Override
			public void onResult(String rrule) {
				EventRecurrence eventRecurrence = new EventRecurrence();

				if (!rrule.equals(EventRecurrence.EMPTY)) {
					eventRecurrence.parse(rrule);
				}
				eventModel.setEventRecurrence(eventRecurrence);
				setRecurrenceText(rrule);
			}
		});

		recurrenceFragment.setArguments(bundle);
		getChildFragmentManager().beginTransaction()
				.add(R.id.fragment_container, recurrenceFragment, getString(R.string.tag_event_recurrence_fragment))
				.addToBackStack(getString(R.string.tag_event_recurrence_fragment)).commit();
	}

	protected final void setRecurrenceText(String rRule) {
		if (!rRule.equals(EventRecurrence.EMPTY)) {
			EventRecurrence eventRecurrence = new EventRecurrence();
			eventRecurrence.parse(rRule);

			binding.recurrenceLayout.eventRecurrence.setText(eventRecurrence.toSimple());
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

			eventModel.setAccessLevel(accessLevel);
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

			eventModel.setAvailability(availability);
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
							eventModel.setCalendar(selectedCalendarValues.getAsInteger(CalendarContract.Calendars._ID));

							setCalendarText(selectedCalendarValues.getAsInteger(CalendarContract.Calendars.CALENDAR_COLOR),
									selectedCalendarValues.getAsString(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME),
									selectedCalendarValues.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));

							String accountName = selectedCalendarValues.getAsString(CalendarContract.Calendars.ACCOUNT_NAME);
							List<ContentValues> colors = calendarViewModel.getEventColors(accountName);

							int newEventColor = colors.get(0).getAsInteger(CalendarContract.Colors.COLOR);
							String newEventColorKey = colors.get(0).getAsString(CalendarContract.Colors.COLOR_KEY);

							eventModel.setEventColor(newEventColor, newEventColorKey);
							binding.titleLayout.eventColor.setBackgroundColor(EventUtil.getColor(newEventColor));

							if (!eventModel.getNEW_ATTENDEES().isEmpty()) {
								eventModel.removeAttendee(accountName);
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

		List<ContentValues> attendeeList = eventModel.getNEW_ATTENDEES();

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
		if (attendee.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL).equals(selectedCalendarValues.getAsString(CalendarContract.Calendars.ACCOUNT_NAME))) {
			return;
		}

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
				eventModel.removeAttendee(holder.email);
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
		if (eventModel.getNEW_ATTENDEES().size() == 1) {
			ContentValues attendee = eventModel.getNEW_ATTENDEES().get(0);
			if (attendee.containsKey(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP)) {
				if (attendee.getAsInteger(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP) ==
						CalendarContract.Attendees.RELATIONSHIP_ORGANIZER) {
					eventModel.clearAttendees();
				}
			}
		}

		if (eventModel.getNEW_ATTENDEES().isEmpty()) {
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
				if (eventModel.addReminder(reminder.getAsInteger(CalendarContract.Reminders.MINUTES),
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
		getChildFragmentManager().beginTransaction()
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


	private void removeReminderItemView(int minutes) {
		final int rowCount = binding.reminderLayout.remindersTable.getChildCount();
		eventModel.removeReminder(minutes);

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

					eventModel.modifyReminder(previousMinutes, newMinutes, newMethod);
					modifyReminderItemView(previousMinutes, newMinutes, newMethod);
				}

				@Override
				public void onResultAddedReminder(ContentValues reminder) {

				}

				@Override
				public void onResultRemovedReminder(int previousMinutes) {
					eventModel.removeReminder(previousMinutes);
					removeReminderItemView(previousMinutes);
				}
			});
			eventReminderFragment.setArguments(bundle);
			getChildFragmentManager().beginTransaction()
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

	protected final void showDatePicker(@Nullable ModifyInstanceFragment.OnModifiedDateTimeCallback onModifiedDateTimeCallback, DateTimeObj endDateTimeObj) {
		DateTimeObj beginDateTimeObj = eventModel.getBeginDateTimeObj();

		Calendar calendar = Calendar.getInstance(ClockUtil.UTC_TIME_ZONE);
		calendar.set(beginDateTimeObj.getYear(), beginDateTimeObj.getMonth() - 1, beginDateTimeObj.getDay());

		final long begin = calendar.getTimeInMillis();
		calendar.set(endDateTimeObj.getYear(), endDateTimeObj.getMonth() - 1, endDateTimeObj.getDay());
		final long end = calendar.getTimeInMillis();

		datePicker = MaterialDatePicker.Builder.dateRangePicker()
				.setTitleText(R.string.datepicker)
				.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
				.setSelection(new Pair<>(begin, end))
				.build();

		datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
			@Override
			public void onPositiveButtonClick(Pair<Long, Long> selection) {
				Calendar utcCalendar = Calendar.getInstance(ClockUtil.UTC_TIME_ZONE);
				int newYear = 0;
				int newMonth = 0;
				int newDay = 0;

				final DateTimeObj modifiedBeginDateTimeObj = eventModel.getBeginDateTimeObj();
				final DateTimeObj modifiedEndDateTimeObj = eventModel.getEndDateTimeObj();

				if (selection.first != null) {
					utcCalendar.setTimeInMillis(selection.first);
					newYear = utcCalendar.get(Calendar.YEAR);
					newMonth = utcCalendar.get(Calendar.MONTH) + 1;
					newDay = utcCalendar.get(Calendar.DAY_OF_MONTH);

					modifiedBeginDateTimeObj.setYear(newYear).setMonth(newMonth).setDay(newDay);
				}
				if (selection.second != null) {
					utcCalendar.setTimeInMillis(selection.second);
					newYear = utcCalendar.get(Calendar.YEAR);
					newMonth = utcCalendar.get(Calendar.MONTH) + 1;
					newDay = utcCalendar.get(Calendar.DAY_OF_MONTH);

					modifiedEndDateTimeObj.setYear(newYear).setMonth(newMonth).setDay(newDay);
				}

				if (binding.timeLayout.timeAlldaySwitch.isChecked()) {
					modifiedBeginDateTimeObj.setHour(0).setMinute(0);
					modifiedEndDateTimeObj.setHour(0).setMinute(0);
				}

				setDateText(DateTimeType.BEGIN, modifiedBeginDateTimeObj, false);
				setDateText(DateTimeType.END, modifiedEndDateTimeObj, false);
				eventModel.getModifiedValueSet().add(CalendarContract.Events.DTSTART);
				eventModel.getModifiedValueSet().add(CalendarContract.Events.DTEND);

				if (onModifiedDateTimeCallback != null) {
					onModifiedDateTimeCallback.onModified();
				}

				final long beginTimeMillis = modifiedBeginDateTimeObj.getUtcCalendar().getTimeInMillis();
				modifyRruleIfFreqMonthly(beginTimeMillis);
				modifyRruleIfFreqWeekly(beginTimeMillis);

				setTimeZoneText();
			}
		});
		datePicker.addOnNegativeButtonClickListener(view ->
		{
			datePicker.dismiss();
		});

		datePicker.show(getChildFragmentManager(), datePicker.toString());
	}

	protected final void modifyRruleIfFreqMonthly(long startDate) {
		if (!eventModel.getEventRecurrence().toString().equals(EventRecurrence.EMPTY)) {
			EventRecurrence eventRecurrence = eventModel.getEventRecurrence();

			if (eventRecurrence.freq == EventRecurrence.MONTHLY) {
				Calendar calendar = Calendar.getInstance(ClockUtil.UTC_TIME_ZONE);
				calendar.setTimeInMillis(startDate);

				if (eventRecurrence.bymonthdayCount > 0) {
					int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

					eventRecurrence.bymonthday = new int[]{dayOfMonth};
					eventRecurrence.bymonthdayCount = 1;
				} else {
					int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
					int dayOfWeek = EventRecurrence.calendarDay2Day(calendar.get(Calendar.DAY_OF_WEEK));

					eventRecurrence.byday = new int[]{dayOfWeek};
					eventRecurrence.bydayNum = new int[]{weekOfMonth};
					eventRecurrence.bydayCount = 1;
					// byday는 요일
					// bydaynum은 weekofmonth 값
					// byday의 문자열 변환 값이 4SU 이면 byday는 EventRecurrence.SU값 하나를 가지고
					// bydaynum은 4값 하나를 가짐
				}
				eventModel.setEventRecurrence(eventRecurrence);
				setRecurrenceText(eventRecurrence.toString());
			}
		}
	}

	protected final void modifyRruleIfFreqWeekly(long startDate) {
		if (!eventModel.getEventRecurrence().toString().equals(EventRecurrence.EMPTY)) {
			EventRecurrence eventRecurrence = eventModel.getEventRecurrence();

			if (eventRecurrence.freq == EventRecurrence.WEEKLY) {
				Calendar calendar = Calendar.getInstance(ClockUtil.UTC_TIME_ZONE);
				calendar.setTimeInMillis(startDate);

				if (eventRecurrence.bydayCount > 0) {
					int dayOfWeek = EventRecurrence.calendarDay2Day(calendar.get(Calendar.DAY_OF_WEEK));

					eventRecurrence.byday = new int[]{dayOfWeek};
					eventRecurrence.bydayNum = new int[]{0};
					eventRecurrence.bydayCount = 1;
				}

				eventModel.setEventRecurrence(eventRecurrence);
				setRecurrenceText(eventRecurrence.toString());
			}
		}
	}

	protected final void setDateText(DateTimeType dateType, DateTimeObj dateTimeObj, boolean init) {
		if (dateType == DateTimeType.BEGIN) {
			binding.timeLayout.startDate.setText(EventUtil.convertDate(dateTimeObj.getTimeMillis()));
		} else if (dateType == DateTimeType.END) {
			long timeMillis = dateTimeObj.getTimeMillis();

			if (init) {
				if (binding.timeLayout.timeAlldaySwitch.isChecked()) {
					if (dateTimeObj.getHour() == 0 && dateTimeObj.getMinute() == 0) {
						Calendar calendar = dateTimeObj.getCalendar();
						calendar.add(Calendar.DATE, -1);
						timeMillis = calendar.getTimeInMillis();
					}
				}
			}
			binding.timeLayout.endDate.setText(EventUtil.convertDate(timeMillis));
		}
	}

	protected final void showTimePicker(DateTimeType dateType,
	                                    @Nullable ModifyInstanceFragment.OnModifiedDateTimeCallback onModifiedDateTimeCallback) {
		final DateTimeObj beginDateTimeObj = eventModel.getBeginDateTimeObj();
		final DateTimeObj endDateTimeObj = eventModel.getEndDateTimeObj();

		MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder();
		timePicker =
				builder.setTitleText((dateType == DateTimeType.BEGIN ? getString(R.string.start) : getString(R.string.end)) + getString(R.string.timepicker))
						.setTimeFormat(App.isPreference_key_using_24_hour_system() ? TimeFormat.CLOCK_24H : TimeFormat.CLOCK_12H)
						.setHour(dateType == DateTimeType.BEGIN ? beginDateTimeObj.getHour() : endDateTimeObj.getHour())
						.setMinute(dateType == DateTimeType.BEGIN ? beginDateTimeObj.getMinute() : endDateTimeObj.getMinute())
						.setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK).build();

		timePicker.addOnPositiveButtonClickListener(view ->
		{
			final int newHour = timePicker.getHour();
			final int newMinute = timePicker.getMinute();

			if (dateType == DateTimeType.BEGIN) {
				beginDateTimeObj.setHour(newHour).setMinute(newMinute);
				setTimeText(dateType, beginDateTimeObj);
			} else if (dateType == DateTimeType.END) {
				endDateTimeObj.setHour(newHour).setMinute(newMinute);
				setTimeText(dateType, endDateTimeObj);
			}

			if (dateType == DateTimeType.BEGIN) {
				if (ClockUtil.areSameDate(beginDateTimeObj.getTimeMillis(), endDateTimeObj.getTimeMillis())) {
					if (beginDateTimeObj.getHour() > endDateTimeObj.getHour()) {
						endDateTimeObj.setHour(newHour).setMinute(0);
						endDateTimeObj.add(Calendar.HOUR_OF_DAY, 1);
					} else if (beginDateTimeObj.getHour() == endDateTimeObj.getHour()
							&& beginDateTimeObj.getMinute() > endDateTimeObj.getMinute()) {
						endDateTimeObj.setMinute(newMinute);
					}

					setTimeText(DateTimeType.END, endDateTimeObj);
				}
			} else if (dateType == DateTimeType.END) {
				if (ClockUtil.areSameDate(beginDateTimeObj.getTimeMillis(), endDateTimeObj.getTimeMillis())) {
					if (beginDateTimeObj.getHour() > endDateTimeObj.getHour()) {
						beginDateTimeObj.setHour(newHour).setMinute(0);
						beginDateTimeObj.add(Calendar.HOUR_OF_DAY, -1);
					} else if (beginDateTimeObj.getHour() == endDateTimeObj.getHour()
							&& beginDateTimeObj.getMinute() > endDateTimeObj.getMinute()) {
						beginDateTimeObj.setMinute(newMinute);
					}
				}
				setTimeText(DateTimeType.BEGIN, beginDateTimeObj);
			}

			if (onModifiedDateTimeCallback != null) {
				onModifiedDateTimeCallback.onModified();
			}

			eventModel.getModifiedValueSet().add(CalendarContract.Events.DTSTART);
			eventModel.getModifiedValueSet().add(CalendarContract.Events.DTEND);
			setTimeZoneText();
		});
		timePicker.addOnNegativeButtonClickListener(view ->
		{
			timePicker.dismiss();
		});
		timePicker.show(getChildFragmentManager(), timePicker.toString());
	}

	protected final void setTimeText(DateTimeType dateType, DateTimeObj dateTimeObj) {
		// 설정에 12시간, 24시간 단위 변경 가능
		if (dateType == DateTimeType.BEGIN) {
			binding.timeLayout.startTime.setText(EventUtil.convertTime(dateTimeObj.getTimeMillis()));
		} else {
			binding.timeLayout.endTime.setText(EventUtil.convertTime(dateTimeObj.getTimeMillis()));
		}
	}

	protected abstract void initDatePicker();

	protected abstract void initTimePicker(DateTimeType dateType);

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

					eventModel.setEventLocation(resultLocation);
					binding.locationLayout.eventLocation.setText(resultLocation);
					locationIntentCode = LocationIntentCode.RESULT_CODE_CHANGED_LOCATION;
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

					eventModel.setEventLocation(resultLocation);
					binding.locationLayout.eventLocation.setText(resultLocation);
					locationIntentCode = LocationIntentCode.RESULT_CODE_SELECTED_LOCATION;

				}

				@Override
				public void onResultUnselectedLocation() {
					eventModel.setEventLocation("");
					binding.locationLayout.eventLocation.setText("");
					locationDTO = null;
					locationIntentCode = LocationIntentCode.RESULT_CODE_REMOVED_LOCATION;
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
				}
				bundle.putInt("requestCode", LocationIntentCode.REQUEST_CODE_SELECT_LOCATION_EMPTY_QUERY.value());

			}

			selectionDetailLocationFragment.setArguments(bundle);
			getChildFragmentManager().beginTransaction().add(R.id.fragment_container
					, selectionDetailLocationFragment, getString(R.string.tag_detail_location_selection_fragment))
					.addToBackStack(getString(R.string.tag_detail_location_selection_fragment)).commit();
		} else {
			networkStatus.showToastDisconnected();
		}
	}

	protected final void onClickedAttendeeList(Bundle bundle) {
		ContentValues selectedAccount = new ContentValues();
		selectedAccount.put(CalendarContract.Attendees.ATTENDEE_NAME, selectedCalendarValues.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));
		selectedAccount.put(CalendarContract.Attendees.ATTENDEE_EMAIL, selectedCalendarValues.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));
		selectedAccount.put(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP, CalendarContract.Attendees.RELATIONSHIP_ORGANIZER);

		bundle.putParcelableArrayList("attendeeList", (ArrayList<? extends Parcelable>) eventModel.getNEW_ATTENDEES());
		bundle.putParcelable("selectedAccount", selectedAccount);

		AttendeesFragment attendeesFragment = new AttendeesFragment(new AttendeesFragment.OnAttendeesResultListener() {
			@Override
			public void onResult(List<ContentValues> newAttendeeList, boolean guestsCanModify, boolean guestsCanInviteOthers, boolean guestsCanSeeGuests) {
				eventModel.setAttendees(newAttendeeList, guestsCanModify, guestsCanInviteOthers, guestsCanSeeGuests);
				createAttendeeListView();
			}
		});

		attendeesFragment.setArguments(bundle);
		getChildFragmentManager().beginTransaction()
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

