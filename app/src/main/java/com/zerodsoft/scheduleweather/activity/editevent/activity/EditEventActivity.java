package com.zerodsoft.scheduleweather.activity.editevent.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.util.ArraySet;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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
import com.zerodsoft.scheduleweather.activity.editevent.value.EventData;
import com.zerodsoft.scheduleweather.activity.editevent.interfaces.IEventRepeat;
import com.zerodsoft.scheduleweather.activity.preferences.ColorListAdapter;
import com.zerodsoft.scheduleweather.common.enums.EventIntentCode;
import com.zerodsoft.scheduleweather.common.enums.LocationIntentCode;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.databinding.ActivityEditEventBinding;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.event.common.DetailLocationSelectorKey;
import com.zerodsoft.scheduleweather.event.common.SelectionDetailLocationActivity;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.utility.NetworkStatus;
import com.zerodsoft.scheduleweather.utility.RecurrenceRule;
import com.zerodsoft.scheduleweather.utility.model.ReminderDto;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

public abstract class EditEventActivity extends AppCompatActivity implements IEventRepeat {
	protected ActivityEditEventBinding binding;
	protected CalendarViewModel calendarViewModel;
	protected EventDataController dataController;
	protected LocationViewModel locationViewModel;
	protected EventDataViewModel eventDataViewModel;

	protected AlertDialog accessLevelDialog;
	protected AlertDialog availabilityDialog;
	protected AlertDialog calendarDialog;

	protected MaterialTimePicker timePicker;
	protected MaterialDatePicker<Pair<Long, Long>> datePicker;

	protected EventIntentCode requestCode;
	protected List<ContentValues> calendarList;
	protected LocationDTO locationDTO;
	protected NetworkStatus networkStatus;

	protected ContentValues contentValues = new ContentValues();

	protected enum DateTimeType {
		START, END
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.schuedule_edit_menu, menu);
		return true;
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				setResult(RESULT_CANCELED);
				finish();
				break;
		}
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_event);

		locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
		calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
		eventDataViewModel = new ViewModelProvider(this).get(EventDataViewModel.class);
		requestCode = EventIntentCode.enumOf(getIntent().getIntExtra("requestCode", 0));
		networkStatus = new NetworkStatus(getApplicationContext(), new ConnectivityManager.NetworkCallback());

		dataController = new EventDataController(getApplicationContext(), requestCode);

		Toolbar toolbar = binding.eventToolbar;
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		binding.reminderLayout.notReminder.setVisibility(View.GONE);
		binding.descriptionLayout.descriptionTextview.setVisibility(View.GONE);
		binding.attendeeLayout.notAttendees.setVisibility(View.GONE);
	}

	@Override
	protected void onDestroy() {
		attendeesActivityResultLauncher.unregister();
		recurrentActivityResultLauncher.unregister();
		remindersActivityResultLauncher.unregister();
		timeZoneActivityResultLauncher.unregister();

		super.onDestroy();
	}

	protected abstract void loadInitData();

	protected void setOnClickListeners() {
        /*
        event color
         */
		binding.titleLayout.eventColor.setOnClickListener(new View.OnClickListener() {
			private AlertDialog dialog;

			@Override
			public void onClick(View view) {
				String accountName = dataController.getEventDefaultValue().getDefaultCalendar().getAsString(CalendarContract.Calendars.ACCOUNT_NAME);
				List<ContentValues> colors = calendarViewModel.getEventColors(accountName);

				GridView gridView = new GridView(getApplicationContext());
				gridView.setAdapter(new ColorListAdapter(dataController.getEventValueAsString(CalendarContract.Events.EVENT_COLOR_KEY), colors, getApplicationContext()));
				gridView.setNumColumns(5);
				gridView.setGravity(Gravity.CENTER);
				gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						final int color = colors.get(position).getAsInteger(CalendarContract.Colors.COLOR);
						final String colorKey = colors.get(position).getAsString(CalendarContract.Colors.COLOR_KEY);

						dataController.putEventValue(CalendarContract.Events.EVENT_COLOR, color);
						dataController.putEventValue(CalendarContract.Events.EVENT_COLOR_KEY, colorKey);
						binding.titleLayout.eventColor.setBackgroundColor(EventUtil.getColor(color));

						dialog.dismiss();
					}
				});

				MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(EditEventActivity.this);
				builder.setView(gridView);
				builder.setCancelable(false);
				builder.setTitle(R.string.title_select_event_color);
				dialog = builder.create();
				dialog.show();
			}
		});

        /*
        시간 allday 스위치
         */
		binding.timeLayout.timeAlldaySwitch.setOnCheckedChangeListener((compoundButton, isChecked) ->
		{
			if (isChecked) {
				binding.timeLayout.startTime.setVisibility(View.GONE);
				binding.timeLayout.endTime.setVisibility(View.GONE);
				binding.timeLayout.eventTimezoneLayout.setVisibility(View.GONE);
			} else {
				binding.timeLayout.startTime.setVisibility(View.VISIBLE);
				binding.timeLayout.endTime.setVisibility(View.VISIBLE);
				binding.timeLayout.eventTimezoneLayout.setVisibility(View.VISIBLE);
			}
			dataController.putEventValue(CalendarContract.Events.ALL_DAY, isChecked ? 1 : 0);
		});

        /*
        시간대
         */
		binding.timeLayout.eventTimezone.setOnClickListener(view ->
		{
			Intent intent = new Intent(EditEventActivity.this, TimeZoneActivity.class);
			intent.putExtra("startTime", dataController.getEventValueAsLong(CalendarContract.Events.DTSTART));
			startActivityForResult(intent, REQUEST_TIMEZONE);
		});

        /*
        반복
         */
		binding.recurrenceLayout.eventRecurrence.setOnClickListener(view ->
		{
			Intent intent = new Intent(EditEventActivity.this, RecurrenceActivity.class);
			// 반복 룰과 이벤트의 시작 시간 전달
			String recurrenceRule = dataController.getEventValueAsString(CalendarContract.Events.RRULE) != null
					? dataController.getEventValueAsString(CalendarContract.Events.RRULE) : "";

			intent.putExtra(CalendarContract.Events.RRULE, recurrenceRule);
			intent.putExtra(CalendarContract.Events.DTSTART, dataController.getEventValueAsLong(CalendarContract.Events.DTSTART));
			startActivityForResult(intent, REQUEST_RECURRENCE);
		});

        /*
        접근수준
         */
		binding.accesslevelLayout.eventAccessLevel.setOnClickListener(view ->
		{
			int checkedItem = dataController.getEventValueAsInt(CalendarContract.Events.ACCESS_LEVEL) != null ? dataController.getEventValueAsInt(CalendarContract.Events.ACCESS_LEVEL)
					: 0;

			if (checkedItem == 3) {
				checkedItem = 1;
			}

			MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(EditEventActivity.this);
			dialogBuilder.setSingleChoiceItems(EventUtil.getAccessLevelItems(getApplicationContext()), checkedItem, (dialogInterface, item) ->
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

				dataController.putEventValue(CalendarContract.Events.ACCESS_LEVEL, accessLevel);
				setAccessLevelText(accessLevel);
				accessLevelDialog.dismiss();

			}).setTitle(getString(R.string.accesslevel));
			accessLevelDialog = dialogBuilder.create();
			accessLevelDialog.show();
		});

        /*
        유효성
         */
		binding.availabilityLayout.eventAvailability.setOnClickListener(view ->
		{
			int checkedItem = dataController.getEventValueAsInt(CalendarContract.Events.AVAILABILITY) != null ?
					dataController.getEventValueAsInt(CalendarContract.Events.AVAILABILITY)
					: 1;

			MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(EditEventActivity.this);
			dialogBuilder.setSingleChoiceItems(EventUtil.getAvailabilityItems(getApplicationContext()), checkedItem, (dialogInterface, item) ->
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

				dataController.putEventValue(CalendarContract.Events.AVAILABILITY, availability);
				setAvailabilityText(availability);
				availabilityDialog.dismiss();

			}).setTitle(getString(R.string.availability));
			availabilityDialog = dialogBuilder.create();
			availabilityDialog.show();
		});

        /*
        캘린더 선택
         */
		binding.calendarLayout.eventCalendarValueView.setOnClickListener(view ->
		{
			MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(EditEventActivity.this);
			dialogBuilder
					.setTitle(getString(R.string.calendar))
					.setAdapter(new CalendarListAdapter(getApplicationContext(), calendarList)
							, (dialogInterface, position) ->
							{
								ContentValues calendar = (ContentValues) calendarDialog.getListView().getAdapter().getItem(position);
								dataController.setCalendarValue(calendar);
								setCalendarText(calendar.getAsInteger(CalendarContract.Calendars.CALENDAR_COLOR),
										calendar.getAsString(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME),
										calendar.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));

								setDefaultEventColor(calendar.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));
							});
			calendarDialog = dialogBuilder.create();
			calendarDialog.show();
		});

        /*
        알람
         */
		binding.reminderLayout.addReminderButton.setOnClickListener(view ->
		{
			Intent intent = new Intent(EditEventActivity.this, ReminderActivity.class);
			intent.putExtra("requestCode", ReminderActivity.ADD_REMINDER);
			startActivityForResult(intent, ReminderActivity.ADD_REMINDER);
		});

		@SuppressLint("NonConstantResourceId") View.OnClickListener dateTimeOnClickListener = view ->
		{
			switch (view.getId()) {
				case R.id.start_date:
				case R.id.end_date:
					showDatePicker();
					break;
				case R.id.start_time:
					showTimePicker(START_DATETIME);
					break;
				case R.id.end_time:
					showTimePicker(END_DATETIME);
					break;
			}
		};

		binding.timeLayout.startDate.setOnClickListener(dateTimeOnClickListener);
		binding.timeLayout.startTime.setOnClickListener(dateTimeOnClickListener);
		binding.timeLayout.endDate.setOnClickListener(dateTimeOnClickListener);
		binding.timeLayout.endTime.setOnClickListener(dateTimeOnClickListener);

        /*
        위치
         */
		binding.locationLayout.eventLocation.setOnClickListener(view ->
		{
			//위치를 설정하는 액티비티 표시
			if (networkStatus.networkAvailable()) {
				Intent intent = new Intent(EditEventActivity.this, SelectionDetailLocationActivity.class);
				Bundle bundle = new Bundle();

				if (locationDTO != null) {
					bundle.putParcelable(DetailLocationSelectorKey.SELECTED_LOCATION_DTO_IN_EVENT.name(), locationDTO);
					bundle.putInt("requestCode", LocationIntentCode.REQUEST_CODE_CHANGE_LOCATION.value());
				} else {
					bundle.putInt("requestCode", LocationIntentCode.REQUEST_CODE_SELECT_LOCATION_EMPTY_QUERY.value());
				}

				intent.putExtras(bundle);
				selectLocationActivityResultLauncher.launch(intent);
			} else {
				networkStatus.showToastDisconnected();
			}

		});

        /*
        참석자 상세정보 버튼
         */
		binding.attendeeLayout.showAttendeesDetail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ContentValues organizer = new ContentValues();
				ContentValues selectedCalendar = dataController.getSelectedCalendar();

				organizer.put(CalendarContract.Attendees.ATTENDEE_NAME, selectedCalendar.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));
				organizer.put(CalendarContract.Attendees.ATTENDEE_EMAIL, selectedCalendar.getAsString(CalendarContract.Calendars.OWNER_ACCOUNT));
				organizer.put(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP, CalendarContract.Attendees.RELATIONSHIP_ORGANIZER);

				Intent intent = new Intent(EditEventActivity.this, AttendeesActivity.class);

				intent.putParcelableArrayListExtra("attendeeList", (ArrayList<? extends Parcelable>) dataController.getAttendees());
				intent.putExtra("selectedCalendar", organizer);
				intent.putExtra(CalendarContract.Events.GUESTS_CAN_MODIFY, dataController.getEventValueAsBoolean(CalendarContract.Events.GUESTS_CAN_MODIFY));
				intent.putExtra(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, dataController.getEventValueAsBoolean(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS));
				intent.putExtra(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, dataController.getEventValueAsBoolean(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS));

				startActivityForResult(intent, AttendeesActivity.SHOW_DETAILS_FOR_ATTENDEES);
			}
		});
	}

	protected void setDefaultEventColor(String accountName) {
		List<ContentValues> colors = calendarViewModel.getEventColors(accountName);

		dataController.putEventValue(CalendarContract.Events.EVENT_COLOR, colors.get(0).getAsInteger(CalendarContract.Colors.COLOR));
		dataController.putEventValue(CalendarContract.Events.EVENT_COLOR_KEY, colors.get(0).getAsInteger(CalendarContract.Colors.COLOR_KEY));
		binding.titleLayout.eventColor.setBackgroundColor(EventUtil.getColor(dataController.getEventValueAsInt(CalendarContract.Events.EVENT_COLOR)));
	}


	protected void setRecurrenceText(String rRule) {
		if (rRule != null) {
			RecurrenceRule recurrenceRule = new RecurrenceRule();
			recurrenceRule.separateValues(rRule);
			binding.recurrenceLayout.eventRecurrence.setText(recurrenceRule.interpret(getApplicationContext()));
		} else {
			binding.recurrenceLayout.eventRecurrence.setText("");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case REQUEST_RECURRENCE: {
				if (resultCode == RESULT_OK) {
					String rRule = data.getStringExtra(CalendarContract.Events.RRULE);
					dataController.putEventValue(CalendarContract.Events.RRULE, rRule);
					setRecurrenceText(rRule);
				} else {

				}
				break;
			}


			case AttendeesActivity.SHOW_DETAILS_FOR_ATTENDEES: {
				List<ContentValues> resultAttendeeList = data.getParcelableArrayListExtra("attendeeList");

				if (resultAttendeeList.isEmpty()) {
					// 리스트가 비어있으면 참석자 삭제 | 취소
					if (!dataController.getAttendees().isEmpty()) {
						dataController.removeAttendees();
					}
				} else {
					// 참석자 추가 | 변경
					dataController.putAttendees(resultAttendeeList,
							data.getBooleanExtra(CalendarContract.Events.GUESTS_CAN_MODIFY, false),
							data.getBooleanExtra(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, false)
							, data.getBooleanExtra(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, false));

				}
				setAttendeesText(dataController.getAttendees());
				break;
			}

			case REQUEST_TIMEZONE: {
				if (resultCode == RESULT_OK) {
					TimeZone timeZone = (TimeZone) data.getSerializableExtra(CalendarContract.Events.EVENT_TIMEZONE);
					dataController.putEventValue(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
					setTimeZoneText(timeZone.getID());
				}
				break;
			}

			case ReminderActivity.ADD_REMINDER: {
				if (resultCode == ReminderActivity.RESULT_ADDED_REMINDER) {
					ContentValues reminder = (ContentValues) data.getParcelableExtra("reminder");

					// reminder values는 분, 메소드값을 담고 있어야 한다
					// 수정된 minutes, method가 기존 값과 중복되는 경우 진행하지 않음
					if (!isDuplicateReminder(reminder.getAsInteger(CalendarContract.Reminders.MINUTES),
							reminder.getAsInteger(CalendarContract.Reminders.METHOD))) {
						dataController.putReminder(reminder);
						addReminder(reminder);
					}

				} else if (resultCode == RESULT_CANCELED) {

				}
				break;
			}

			case ReminderActivity.MODIFY_REMINDER: {
				final int previousMinutes = data.getIntExtra("previousMinutes", 0);

				if (resultCode == ReminderActivity.RESULT_MODIFIED_REMINDER) {
					ContentValues reminder = data.getParcelableExtra("reminder");
					// 수정된 minutes, method가 기존 값과 중복되는 경우 진행하지 않음
					if (!isDuplicateReminder(reminder.getAsInteger(CalendarContract.Reminders.MINUTES),
							reminder.getAsInteger(CalendarContract.Reminders.METHOD))) {
						modifyReminder(reminder, previousMinutes);
					}
				} else if (resultCode == ReminderActivity.RESULT_REMOVED_REMINDER) {
					removeReminder(previousMinutes);

				}
				break;

			}
		}

	}

	protected boolean isDuplicateReminder(int minutes, int method) {
		List<ContentValues> reminders = dataController.getReminders();

		for (ContentValues reminder : reminders) {
			if (reminder.getAsInteger(CalendarContract.Reminders.MINUTES) == minutes
					&& reminder.getAsInteger(CalendarContract.Reminders.METHOD) == method) {
				return true;
			}
		}
		return false;
	}

	protected final View.OnClickListener reminderItemOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			ReminderItemHolder holder = (ReminderItemHolder) view.getTag();
			// modify
			Intent intent = new Intent(EditEventActivity.this, ReminderActivity.class);
			intent.putExtra("previousMinutes", holder.minutes);
			intent.putExtra("previousMethod", holder.method);
			intent.putExtra("requestCode", ReminderActivity.MODIFY_REMINDER);
			startActivityForResult(intent, ReminderActivity.MODIFY_REMINDER);
		}
	};

	protected final View.OnClickListener removeReminderOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			ReminderItemHolder holder = (ReminderItemHolder) view.getTag();
			removeReminder(holder.minutes);
		}
	};

	static class ReminderItemHolder {
		protected int minutes;
		protected int method;

		protected ReminderItemHolder(int minutes, int method) {
			this.minutes = minutes;
			this.method = method;
		}
	}


	protected void addReminder(ContentValues reminder) {
		final int minutes = reminder.getAsInteger(CalendarContract.Reminders.MINUTES);
		final int method = reminder.getAsInteger(CalendarContract.Reminders.METHOD);

		TableRow tableRow = new TableRow(getApplicationContext());
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
		String alarmValueText = EventUtil.makeAlarmText(reminderDto, getApplicationContext());

		String text = alarmValueText + "(" + EventUtil.getReminderMethod(getApplicationContext(), method) + ")";
		reminderValueTextView.setText(text);

		tableRow.addView(row);
		binding.reminderLayout.remindersTable.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
	}


	protected void removeReminder(int minutes) {
		dataController.removeReminder(minutes);
		final int rowCount = binding.reminderLayout.remindersTable.getChildCount();

		// 아이템 삭제
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			ReminderItemHolder holder = (ReminderItemHolder) binding.reminderLayout.remindersTable.getChildAt(rowIndex).getTag();
			if (holder.minutes == minutes) {
				binding.reminderLayout.remindersTable.removeViewAt(rowIndex);
				break;
			}
		}
	}

	protected void modifyReminder(ContentValues reminder, int previousMinutes) {
		dataController.modifyReminder(reminder, previousMinutes);
		final int rowCount = binding.reminderLayout.remindersTable.getChildCount();

		// 아이템 수정
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			ReminderItemHolder holder = (ReminderItemHolder) binding.reminderLayout.remindersTable.getChildAt(rowIndex).getTag();

			if (holder.minutes == previousMinutes) {
				final int newMinutes = reminder.getAsInteger(CalendarContract.Reminders.MINUTES);
				final int newMethod = reminder.getAsInteger(CalendarContract.Reminders.METHOD);

				holder.minutes = newMinutes;
				holder.method = newMethod;

				ReminderDto reminderDto = EventUtil.convertAlarmMinutes(newMinutes);
				String alarmValueText = EventUtil.makeAlarmText(reminderDto, getApplicationContext());

				String text = alarmValueText + "(" + EventUtil.getReminderMethod(getApplicationContext(), newMethod) + ")";

				View row = binding.reminderLayout.remindersTable.getChildAt(rowIndex);
				((TextView) row.findViewById(R.id.reminder_value)).setText(text);

				break;
			}
		}
	}


	protected void setReminderText(List<ContentValues> reminders) {
		for (ContentValues reminder : reminders) {
			addReminder(reminder);
		}
	}

	static class AttendeeItemHolder {
		protected final String email;

		public AttendeeItemHolder(String email) {
			this.email = email;
		}

	}

	protected void addAttendee(ContentValues attendee) {
		TableRow tableRow = new TableRow(getApplicationContext());
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
				removeAttendee(holder.email);
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
		final String selectedCalendarName = dataController.getEventValueAsString(CalendarContract.Events.CALENDAR_DISPLAY_NAME);
		final String selectedCalendarOwnerAccount = dataController.getEventValueAsString(CalendarContract.Events.OWNER_ACCOUNT);
		String attendeeName = null;

		if (attendee.getAsInteger(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP) == CalendarContract.Attendees.RELATIONSHIP_ORGANIZER) {
			// 조직자인 경우
			removeButton.setVisibility(View.GONE);
			attendeeName = attendee.getAsString(CalendarContract.Attendees.ATTENDEE_NAME);

			if (attendeeName.equals(selectedCalendarName)) {
				attendeeName += "(ME)";
			}
		} else {
			// 참석자인 경우
			attendeeName = attendee.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL);
			if (attendeeName.equals(selectedCalendarOwnerAccount)) {
				attendeeName += "(ME)";
			}
		}
		attendeeEmailView.setText(attendeeName);

		tableRow.addView(row);
		binding.attendeeLayout.eventAttendeesTable.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
	}


	protected void removeAttendee(String email) {
		dataController.removeAttendee(email);

		// 아이템 삭제
		final int rowCount = binding.attendeeLayout.eventAttendeesTable.getChildCount();

		if (rowCount == 2) {
			binding.attendeeLayout.eventAttendeesTable.removeAllViews();
			binding.attendeeLayout.showAttendeesDetail.setText(getString(R.string.add_attendee));
		} else if (rowCount >= 3) {
			for (int row = 0; row < rowCount; row++) {
				AttendeeItemHolder holder = (AttendeeItemHolder) binding.attendeeLayout.eventAttendeesTable.getChildAt(row).getTag();

				if (holder.email.equals(email)) {
					binding.attendeeLayout.eventAttendeesTable.removeViewAt(row);
					break;
				}
			}
		}
	}

	protected void setAttendeesText(List<ContentValues> attendees) {
		if (binding.attendeeLayout.eventAttendeesTable.getChildCount() > 0) {
			binding.attendeeLayout.eventAttendeesTable.removeAllViews();
		}

		if (attendees.isEmpty()) {
			// 참석자 버튼 텍스트 수정
			binding.attendeeLayout.showAttendeesDetail.setText(getString(R.string.add_attendee));
		} else {
			binding.attendeeLayout.showAttendeesDetail.setText(getString(R.string.show_attendees));

			for (ContentValues attendee : attendees) {
				addAttendee(attendee);
			}
		}

	}

	protected void setDateText(DateTimeType dateType, long date) {
		if (dateType == DateTimeType.START) {
			binding.timeLayout.startDate.setText(EventUtil.convertDate(date));
		} else {
			binding.timeLayout.endDate.setText(EventUtil.convertDate(date));
		}
	}

	protected void setTimeText(DateTimeType dateType, long time) {
		// 설정에 12시간, 24시간 단위 변경 가능
		if (dateType == DateTimeType.START) {
			binding.timeLayout.startTime.setText(EventUtil.convertTime(time, App.isPreference_key_using_24_hour_system()));
		} else {
			binding.timeLayout.endTime.setText(EventUtil.convertTime(time, App.isPreference_key_using_24_hour_system()));
		}
	}

	protected void setCalendarText(int calendarColor, String calendarDisplayName, String accountName) {
		binding.calendarLayout.calendarColor.setBackgroundColor(EventUtil.getColor(calendarColor));
		binding.calendarLayout.calendarDisplayName.setText(calendarDisplayName);
		binding.calendarLayout.calendarAccountName.setText(accountName);
	}

	protected void setTimeZoneText(String eventTimeZone) {
		TimeZone timeZone = TimeZone.getTimeZone(eventTimeZone);
		binding.timeLayout.eventTimezone.setText(timeZone.getDisplayName(Locale.KOREAN));
	}


	protected void saveNewEvent() {
		// 시간이 바뀌는 경우, 알림 데이터도 변경해야함.
		// 알림 재설정
		EventData newEventData = dataController.getNewEventData();
		ContentValues event = newEventData.getEVENT();
		List<ContentValues> reminderList = newEventData.getREMINDERS();
		List<ContentValues> attendeeList = newEventData.getATTENDEES();

		final int CALENDAR_ID = event.getAsInteger(CalendarContract.Events.CALENDAR_ID);
		final long NEW_EVENT_ID = calendarViewModel.addEvent(event);

		getIntent().putExtra(CalendarContract.Events._ID, NEW_EVENT_ID);
		getIntent().putExtra(CalendarContract.Instances.BEGIN, event.getAsLong(CalendarContract.Events.DTSTART));
		setResult(EventIntentCode.RESULT_SAVED.value(), getIntent());

		if (!reminderList.isEmpty()) {
			for (ContentValues reminder : reminderList) {
				reminder.put(CalendarContract.Reminders.EVENT_ID, NEW_EVENT_ID);
			}
			calendarViewModel.addReminders(reminderList);
		}

		if (!attendeeList.isEmpty()) {
			for (ContentValues attendee : attendeeList) {
				attendee.put(CalendarContract.Attendees.EVENT_ID, NEW_EVENT_ID);
			}
			calendarViewModel.addAttendees(attendeeList);
		}

		if (event.containsKey(CalendarContract.Events.EVENT_LOCATION)) {
			locationDTO.setCalendarId(CALENDAR_ID);
			locationDTO.setEventId(NEW_EVENT_ID);
			locationViewModel.addLocation(locationDTO, new DbQueryCallback<LocationDTO>() {
				@Override
				public void onResultSuccessful(LocationDTO result) {
					finish();
				}

				@Override
				public void onResultNoData() {

				}
			});
		} else {
			finish();
		}
	}

	protected void updateThisInstance() {
		calendarViewModel.updateOneInstance(dataController.getModifiedEventData().getEVENT(),
				dataController.getSavedEventData().getEVENT());
        /*

        // 알람 갱신
        // 알람 데이터가 수정된 경우 이벤트ID를 넣는다
        if (!modifiedEventData.getREMINDERS().isEmpty())
        {
            List<ContentValues> reminders = modifiedEventData.getREMINDERS();

            for (ContentValues reminder : reminders)
            {
                reminder.put(CalendarContract.Reminders.EVENT_ID, action == UPDATE_AFTER_INSTANCE_INCLUDING_THIS_INSTANCE
                        ? newEventId : ORIGINAL_EVENT_ID);
            }
        }

        if (modifiedEventData.getEVENT().getAsBoolean(CalendarContract.Events.HAS_ALARM))
        {
            if (action != UPDATE_AFTER_INSTANCE_INCLUDING_THIS_INSTANCE)
            {
                if (savedEventData.getEVENT().getAsBoolean(CalendarContract.Events.HAS_ALARM))
                {
                    //기존의 알람데이터가 수정된 경우
                    //기존 값 모두 지우고, 새로운 값 저장
                    viewModel.deleteAllReminders(CALENDAR_ID, ORIGINAL_EVENT_ID);
                }
                viewModel.addReminders(modifiedEventData.getREMINDERS());
            }
        } else
        {
            if (savedEventData.getEVENT().getAsBoolean(CalendarContract.Events.HAS_ALARM))
            {
                //원래 알림을 가졌으나, 수정하면서 알림을 모두 삭제함
                viewModel.deleteAllReminders(CALENDAR_ID, ORIGINAL_EVENT_ID);
            }
        }


        // 참석자
        if (!modifiedEventData.getATTENDEES().isEmpty())
        {
            if (!savedEventData.getATTENDEES().isEmpty())
            {
                //참석자 리스트가 수정된 경우
                // 수정된 부분만 변경
                Set<ContentValues> savedAttendees = new ArraySet<>();
                Set<ContentValues> modifiedAttendees = new ArraySet<>();

                savedAttendees.addAll(dataController.getSavedEventData().getATTENDEES());
                modifiedAttendees.addAll(dataController.getModifiedEventData().getATTENDEES());

                AttendeeSet addedAttendees = new AttendeeSet();
                AttendeeSet removedAttendees = new AttendeeSet();

                // 추가된 참석자들만 남긴다.
                addedAttendees.addAll(modifiedAttendees);
                addedAttendees.removeAll(savedAttendees);

                // 삭제된 참석자들만 남긴다.
                removedAttendees.addAll(savedAttendees);
                removedAttendees.removeAll(modifiedAttendees);

                if (!addedAttendees.isEmpty())
                {
                    // 추가된 참석자들을 DB에 모두 추가한다.
                    for (ContentValues addedAttendee : addedAttendees)
                    {
                        addedAttendee.put(CalendarContract.Attendees.EVENT_ID, ORIGINAL_EVENT_ID);
                    }
                    viewModel.addAttendees(new ArrayList<>(addedAttendees));
                }
                if (!removedAttendees.isEmpty())
                {
                    // 삭제된 참석자들을 DB에서 모두 제거한다.
                    long[] ids = new long[removedAttendees.size()];
                    int i = 0;

                    for (ContentValues removedAttendee : removedAttendees)
                    {
                        ids[i] = removedAttendee.getAsLong(CalendarContract.Attendees._ID);
                        i++;
                    }
                    viewModel.deleteAttendees(CALENDAR_ID, ORIGINAL_EVENT_ID, ids);
                }
            } else
            {
                //참석자가 없었다가 새롭게 추가된 경우
                List<ContentValues> addedAttendees = modifiedEventData.getATTENDEES();

                for (ContentValues addedAttendee : addedAttendees)
                {
                    addedAttendee.put(CalendarContract.Attendees.EVENT_ID, ORIGINAL_EVENT_ID);
                }
                viewModel.addAttendees(new ArrayList<>(addedAttendees));
            }
        } else
        {
            if (!savedEventData.getATTENDEES().isEmpty())
            {
                //참석자를 모두 제거한 경우
                viewModel.deleteAllAttendees(CALENDAR_ID, ORIGINAL_EVENT_ID);
            }
        }

         */
	}

	protected void updateAfterInstanceIncludingThisInstance() {
        /*
        final long NEW_EVENT_ID = viewModel.updateAllFutureInstances(dataController.getModifiedEventData().getEVENT(),
                dataController.getSavedEventData().getEVENT());

         */
	}

	protected void updateEvent() {
		calendarViewModel.updateEvent(dataController.getModifiedEventData().getEVENT());
		getIntent().putExtra(CalendarContract.Events._ID,
				dataController.getModifiedEventData().getEVENT().getAsLong(CalendarContract.Events._ID));
		getIntent().putExtra(CalendarContract.Instances.BEGIN,
				dataController.getModifiedEventData().getEVENT().getAsLong(CalendarContract.Events.DTSTART));
		setResult(EventIntentCode.RESULT_MODIFIED_EVENT.value());
		finish();
	}


	protected void modifyEvent(int action) {

/*
        if (modifiedEventData.getEVENT().getAsString(CalendarContract.Events.EVENT_LOCATION) != null)
        {
            // 위치가 추가 | 변경된 경우
            locationDTO.setCalendarId(CALENDAR_ID);
            locationDTO.setEventId(ORIGINAL_EVENT_ID);

            //상세 위치가 지정되어 있는지 확인
            locationViewModel.hasDetailLocation(CALENDAR_ID, ORIGINAL_EVENT_ID, new CarrierMessagingService.ResultCallback<Boolean>()
            {
                @Override
                public void onReceiveResult(@NonNull Boolean aBoolean)
                {
                    if (aBoolean)
                    {
                        // 상세위치가 지정되어 있고, 현재 위치를 변경하려는 상태
                        locationViewModel.modifyLocation(locationDTO, new CarrierMessagingService.ResultCallback<Boolean>()
                        {
                            @Override
                            public void onReceiveResult(@NonNull Boolean aBoolean)
                            {
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                    } else
                    {
                        // 상세위치를 추가하는 경우
                        locationViewModel.addLocation(locationDTO, new CarrierMessagingService.ResultCallback<Boolean>()
                        {
                            @Override
                            public void onReceiveResult(@NonNull Boolean aBoolean)
                            {
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                    }
                }
            });

        } else
        {
            if (savedEventData.getEVENT().getAsString(CalendarContract.Events.EVENT_LOCATION) != null)
            {
                // 현재 위치를 삭제하려고 하는 상태
                locationViewModel.hasDetailLocation(CALENDAR_ID, ORIGINAL_EVENT_ID, new CarrierMessagingService.ResultCallback<Boolean>()
                {
                    @Override
                    public void onReceiveResult(@NonNull Boolean aBoolean)
                    {
                        if (aBoolean)
                        {
                            // 기존의 상세 위치를 제거
                            locationViewModel.removeLocation(CALENDAR_ID, ORIGINAL_EVENT_ID, new CarrierMessagingService.ResultCallback<Boolean>()
                            {
                                @Override
                                public void onReceiveResult(@NonNull Boolean aBoolean)
                                {
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            });
                        } else
                        {
                            // 상세 위치가 지정되어 있지 않음
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                });

            } else
            {
                //위치를 원래 설정하지 않은 경우
                setResult(RESULT_OK);
                finish();
            }
        }

 */
	}

	protected void showDatePicker() {
		MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();

		datePicker = builder.setTitleText(R.string.datepicker)
				.setSelection(new Pair<>(dataController.getEventValueAsLong(CalendarContract.Events.DTSTART).longValue()
						, dataController.getEventValueAsLong(CalendarContract.Events.DTEND).longValue()))
				.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
				.build();
		datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
			@Override
			public void onPositiveButtonClick(Pair<Long, Long> selection) {
				Calendar calendar = Calendar.getInstance();
				int previousHour = 0;
				int previousMinute = 0;

				if (selection.first != null) {
					calendar.setTimeInMillis(dataController.getEventValueAsLong(CalendarContract.Events.DTSTART));
					previousHour = calendar.get(Calendar.HOUR_OF_DAY);
					previousMinute = calendar.get(Calendar.MINUTE);

					calendar.setTimeInMillis(selection.first);
					calendar.set(Calendar.HOUR_OF_DAY, previousHour);
					calendar.set(Calendar.MINUTE, previousMinute);

					dataController.putEventValue(CalendarContract.Events.DTSTART, calendar.getTimeInMillis());
					setDateText(DateTimeType.START, calendar.getTimeInMillis());
				}
				if (selection.second != null) {
					calendar.setTimeInMillis(dataController.getEventValueAsLong(CalendarContract.Events.DTEND));
					previousHour = calendar.get(Calendar.HOUR_OF_DAY);
					previousMinute = calendar.get(Calendar.MINUTE);

					calendar.setTimeInMillis(selection.second);
					calendar.set(Calendar.HOUR_OF_DAY, previousHour);
					calendar.set(Calendar.MINUTE, previousMinute);

					dataController.putEventValue(CalendarContract.Events.DTEND, calendar.getTimeInMillis());
					setDateText(DateTimeType.END, calendar.getTimeInMillis());
				}

				datePicker.dismiss();
			}
		});
		datePicker.addOnNegativeButtonClickListener(view ->
		{
			datePicker.dismiss();
		});

		datePicker.show(getSupportFragmentManager(), datePicker.toString());
	}

	protected void showTimePicker(DateTimeType dateType) {
		Calendar calendar = Calendar.getInstance();

		if (dateType == DateTimeType.START) {
			calendar.setTimeInMillis(dataController.getEventValueAsLong(CalendarContract.Events.DTSTART));
		} else if (dateType == DateTimeType.END) {
			calendar.setTimeInMillis(dataController.getEventValueAsLong(CalendarContract.Events.DTEND));
		}

		MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder();
		timePicker =
				builder.setTitleText((dateType == DateTimeType.START ? getString(R.string.start) : getString(R.string.end)) + getString(R.string.timepicker))
						.setTimeFormat(App.isPreference_key_using_24_hour_system() ? TimeFormat.CLOCK_24H : TimeFormat.CLOCK_12H)
						.setHour(calendar.get(Calendar.HOUR_OF_DAY))
						.setMinute(calendar.get(Calendar.MINUTE))
						.setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK).build();

		timePicker.addOnPositiveButtonClickListener(view ->
		{
			Calendar newCalendar = Calendar.getInstance();
			newCalendar.setTimeInMillis(dateType == DateTimeType.START ? dataController.getEventValueAsLong(CalendarContract.Events.DTSTART)
					: dataController.getEventValueAsLong(CalendarContract.Events.DTEND));
			newCalendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
			newCalendar.set(Calendar.MINUTE, timePicker.getMinute());

			if (dateType == DateTimeType.START) {
				if (newCalendar.getTimeInMillis() <= dataController.getEventValueAsLong(CalendarContract.Events.DTEND)) {
					dataController.putEventValue(CalendarContract.Events.DTSTART, newCalendar.getTimeInMillis());
					setTimeText(dateType, newCalendar.getTimeInMillis());
				} else {
					String msg =
							EventUtil.convertTime(calendar.getTimeInMillis(), App.isPreference_key_using_24_hour_system()) + " " + getString(R.string.plz_set_time_before_specific_time);
					Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
				}
			} else if (dateType == DateTimeType.END) {
				if (newCalendar.getTimeInMillis() >= dataController.getEventValueAsLong(CalendarContract.Events.DTSTART)) {
					dataController.putEventValue(CalendarContract.Events.DTEND, newCalendar.getTimeInMillis());
					setTimeText(dateType, newCalendar.getTimeInMillis());
				} else {
					String msg =
							EventUtil.convertTime(calendar.getTimeInMillis(), App.isPreference_key_using_24_hour_system()) + " " + getString(R.string.plz_set_time_after_specific_time);
					Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
				}
			}

		});
		timePicker.addOnNegativeButtonClickListener(view ->
		{
			timePicker.dismiss();
		});
		timePicker.show(getSupportFragmentManager(), timePicker.toString());
	}


	protected void setAccessLevelText(int accessLevel) {
		binding.accesslevelLayout.eventAccessLevel.setText(EventUtil.convertAccessLevel(accessLevel, getApplicationContext()));
	}

	protected void setAvailabilityText(int availability) {
		binding.availabilityLayout.eventAvailability.setText(EventUtil.convertAvailability(availability, getApplicationContext()));
	}

	protected final ActivityResultLauncher<String> permissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
			new ActivityResultCallback<Boolean>() {
				@Override
				public void onActivityResult(Boolean result) {
					if (result) {
						loadInitData();
					} else {
						Toast.makeText(EditEventActivity.this, getString(R.string.message_needs_calendar_permission), Toast.LENGTH_SHORT).show();
						return;
					}
				}
			});

	protected final ActivityResultLauncher<Intent> selectLocationActivityResultLauncher =
			registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
				@Override
				public void onActivityResult(ActivityResult result) {
					switch (LocationIntentCode.enumOf(result.getResultCode())) {
						case RESULT_CODE_CHANGED_LOCATION:
						case RESULT_CODE_SELECTED_LOCATION: {
							Bundle bundle = result.getData().getExtras();
							locationDTO = (LocationDTO) bundle.getParcelable(DetailLocationSelectorKey.SELECTED_LOCATION_DTO_IN_MAP.value());
							// parcelable object는 형변환을 해줘야 한다.
							String resultLocation = null;

							if (locationDTO.getLocationType() == LocationType.ADDRESS) {
								resultLocation = locationDTO.getAddressName();
							} else {
								resultLocation = locationDTO.getPlaceName();
							}

							dataController.putEventValue(CalendarContract.Events.EVENT_LOCATION, resultLocation);
							binding.locationLayout.eventLocation.setText(resultLocation);

							break;
						}

						case RESULT_CODE_REMOVED_LOCATION: {
							dataController.removeEventValue(CalendarContract.Events.EVENT_LOCATION);
							locationDTO = null;
							binding.locationLayout.eventLocation.setText("");

							break;
						}
					}

				}
			});

	protected final ActivityResultLauncher<Intent> attendeesActivityResultLauncher =
			registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
					, new ActivityResultCallback<ActivityResult>() {
						@Override
						public void onActivityResult(ActivityResult result) {

						}
					});

	protected final ActivityResultLauncher<Intent> recurrentActivityResultLauncher =
			registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
					, new ActivityResultCallback<ActivityResult>() {
						@Override
						public void onActivityResult(ActivityResult result) {

						}
					});

	protected final ActivityResultLauncher<Intent> remindersActivityResultLauncher =
			registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
					, new ActivityResultCallback<ActivityResult>() {
						@Override
						public void onActivityResult(ActivityResult result) {

						}
					});

	protected final ActivityResultLauncher<Intent> timeZoneActivityResultLauncher =
			registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
					, new ActivityResultCallback<ActivityResult>() {
						@Override
						public void onActivityResult(ActivityResult result) {

						}
					});

	static class AttendeeSet extends HashSet<ContentValues> {
		public boolean removeAll(Set<ContentValues> collection) {
			Iterator<ContentValues> itr = this.iterator();
			Set<ContentValues> removeAttendee = new ArraySet<>();

			while (itr.hasNext()) {
				ContentValues attendee = itr.next();

				for (ContentValues attendee2 : collection) {
					if (attendee.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL).equals(attendee2.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL))) {
						removeAttendee.add(attendee);
						break;
					}
				}
			}

			return removeAll(removeAttendee);
		}
	}
}

