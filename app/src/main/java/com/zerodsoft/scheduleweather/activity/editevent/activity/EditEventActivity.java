package com.zerodsoft.scheduleweather.activity.editevent.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.text.Editable;
import android.text.TextWatcher;
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
import com.zerodsoft.scheduleweather.activity.editevent.interfaces.IEventRepeat;
import com.zerodsoft.scheduleweather.activity.preferences.ColorListAdapter;
import com.zerodsoft.scheduleweather.common.enums.EventIntentCode;
import com.zerodsoft.scheduleweather.common.enums.LocationIntentCode;
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

	protected boolean initializing = true;

	protected ContentValues selectedCalendarValues;

	protected enum DateTimeType {
		START,
		END
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

		binding.reminderLayout.notReminder.setVisibility(View.GONE);
		binding.descriptionLayout.notDescription.setVisibility(View.GONE);
		binding.attendeeLayout.notAttendees.setVisibility(View.GONE);

		Toolbar toolbar = binding.eventToolbar;
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		setViewListeners();
		setViewOnClickListeners();

		calendarList = calendarViewModel.getCalendars();
	}

	@Override
	protected void onDestroy() {
		attendeesActivityResultLauncher.unregister();
		recurrenceActivityResultLauncher.unregister();
		remindersActivityResultLauncher.unregister();
		timeZoneActivityResultLauncher.unregister();
		selectLocationActivityResultLauncher.unregister();
		permissionResultLauncher.unregister();

		super.onDestroy();
	}

	protected abstract void loadInitData();

	protected void setViewListeners() {
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
	}

	protected void setViewOnClickListeners() {
        /*
        event color
         */
		binding.titleLayout.eventColor.setOnClickListener(new View.OnClickListener() {
			private AlertDialog dialog;

			@Override
			public void onClick(View view) {
				String accountName = selectedCalendarValues.getAsString(CalendarContract.Calendars.ACCOUNT_NAME);
				List<ContentValues> colors = calendarViewModel.getEventColors(accountName);

				GridView gridView = new GridView(getApplicationContext());
				gridView.setAdapter(new ColorListAdapter(eventDataViewModel.getEVENT().getAsString(CalendarContract.Events.EVENT_COLOR_KEY), colors,
						getApplicationContext()));
				gridView.setNumColumns(5);
				gridView.setGravity(Gravity.CENTER);
				gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						final int color = colors.get(position).getAsInteger(CalendarContract.Colors.COLOR);
						final String colorKey = colors.get(position).getAsString(CalendarContract.Colors.COLOR_KEY);

						eventDataViewModel.setEventColor(color, colorKey);
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

			if (!initializing) {
				eventDataViewModel.setIsAllDay(isChecked);
			}
		});

        /*
        시간대
         */
		binding.timeLayout.eventTimezone.setOnClickListener(view ->
		{
			Intent intent = new Intent(EditEventActivity.this, TimeZoneActivity.class);
			timeZoneActivityResultLauncher.launch(intent);
		});

        /*
        반복
         */
		binding.recurrenceLayout.eventRecurrence.setOnClickListener(view ->
		{
			Intent intent = new Intent(EditEventActivity.this, RecurrenceActivity.class);
			// 반복 룰과 이벤트의 시작 시간 전달
			String rRule = eventDataViewModel.getEVENT().containsKey(CalendarContract.Events.RRULE)
					? eventDataViewModel.getEVENT().getAsString(CalendarContract.Events.RRULE) : "";

			intent.putExtra(CalendarContract.Events.RRULE, rRule);
			intent.putExtra(CalendarContract.Events.DTSTART, eventDataViewModel.getEVENT().getAsLong(CalendarContract.Events.DTSTART));

			recurrenceActivityResultLauncher.launch(intent);
		});

        /*
        접근수준
         */
		binding.accesslevelLayout.eventAccessLevel.setOnClickListener(view ->
		{
			int checkedItem = eventDataViewModel.getEVENT().getAsInteger(CalendarContract.Events.ACCESS_LEVEL);

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

				eventDataViewModel.setAccessLevel(accessLevel);
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
			int checkedItem = eventDataViewModel.getEVENT().getAsInteger(CalendarContract.Events.AVAILABILITY);

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

				eventDataViewModel.setAvailability(availability);
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
								selectedCalendarValues = calendar;
								eventDataViewModel.setCalendar(calendar.getAsInteger(CalendarContract.Calendars._ID));
								setCalendarText(calendar.getAsInteger(CalendarContract.Calendars.CALENDAR_COLOR),
										calendar.getAsString(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME),
										calendar.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));

								String accountName = selectedCalendarValues.getAsString(CalendarContract.Calendars.ACCOUNT_NAME);
								List<ContentValues> colors = calendarViewModel.getEventColors(accountName);

								int color = colors.get(0).getAsInteger(CalendarContract.Colors.COLOR);
								String colorKey = colors.get(0).getAsString(CalendarContract.Colors.COLOR_KEY);

								eventDataViewModel.setEventColor(color, colorKey);
								binding.titleLayout.eventColor.setBackgroundColor(EventUtil.getColor(color));
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
			intent.putExtra("requestCode", EventIntentCode.REQUEST_ADD_REMINDER.value());
			remindersActivityResultLauncher.launch(intent);
		});

		@SuppressLint("NonConstantResourceId") View.OnClickListener dateTimeOnClickListener = view ->
		{
			switch (view.getId()) {
				case R.id.start_date:
				case R.id.end_date:
					showDatePicker();
					break;
				case R.id.start_time:
					showTimePicker(DateTimeType.START);
					break;
				case R.id.end_time:
					showTimePicker(DateTimeType.END);
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

				organizer.put(CalendarContract.Attendees.ATTENDEE_NAME, selectedCalendarValues.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));
				organizer.put(CalendarContract.Attendees.ATTENDEE_EMAIL, selectedCalendarValues.getAsString(CalendarContract.Calendars.OWNER_ACCOUNT));
				organizer.put(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP, CalendarContract.Attendees.RELATIONSHIP_ORGANIZER);

				Intent intent = new Intent(EditEventActivity.this, AttendeesActivity.class);
				intent.putParcelableArrayListExtra("attendeeList", (ArrayList<? extends Parcelable>) eventDataViewModel.getATTENDEES());
				intent.putExtra("organizer", organizer);
				intent.putExtra(CalendarContract.Events.GUESTS_CAN_MODIFY,
						eventDataViewModel.getEVENT().getAsBoolean(CalendarContract.Events.GUESTS_CAN_MODIFY));
				intent.putExtra(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, eventDataViewModel.getEVENT().getAsBoolean(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS));
				intent.putExtra(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, eventDataViewModel.getEVENT().getAsBoolean(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS));

				attendeesActivityResultLauncher.launch(intent);
			}
		});
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


	protected final View.OnClickListener reminderItemOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			ReminderItemHolder holder = (ReminderItemHolder) view.getTag();
			// modify
			Intent intent = new Intent(EditEventActivity.this, ReminderActivity.class);
			intent.putExtra("previousMinutes", holder.minutes);
			intent.putExtra("previousMethod", holder.method);
			intent.putExtra("requestCode", EventIntentCode.REQUEST_MODIFY_REMINDER.value());
			remindersActivityResultLauncher.launch(intent);
		}
	};

	protected final View.OnClickListener removeReminderOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			ReminderItemHolder holder = (ReminderItemHolder) view.getTag();
			removeReminderItemView(holder.minutes);
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


	protected void addReminderItemView(ContentValues reminder) {
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


	protected void removeReminderItemView(int minutes) {
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

	protected void modifyReminderItemView(int previousMinutes, int newMinutes, int newMethod) {
		final int rowCount = binding.reminderLayout.remindersTable.getChildCount();

		// 아이템 수정
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			ReminderItemHolder holder = (ReminderItemHolder) binding.reminderLayout.remindersTable.getChildAt(rowIndex).getTag();

			if (holder.minutes == previousMinutes) {
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
		final String selectedCalendarName = selectedCalendarValues.getAsString(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME);
		final String selectedCalendarOwnerAccount = selectedCalendarValues.getAsString(CalendarContract.Calendars.OWNER_ACCOUNT);
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


	protected void showDatePicker() {
		MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();

		datePicker = builder.setTitleText(R.string.datepicker)
				.setSelection(new Pair<>(eventDataViewModel.getEVENT().getAsLong(CalendarContract.Events.DTSTART)
						, eventDataViewModel.getEVENT().getAsLong(CalendarContract.Events.DTEND)))
				.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
				.build();
		datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
			@Override
			public void onPositiveButtonClick(Pair<Long, Long> selection) {
				Calendar calendar = Calendar.getInstance();
				int previousHour = 0;
				int previousMinute = 0;

				if (selection.first != null) {
					calendar.setTimeInMillis(eventDataViewModel.getEVENT().getAsLong(CalendarContract.Events.DTSTART));
					previousHour = calendar.get(Calendar.HOUR_OF_DAY);
					previousMinute = calendar.get(Calendar.MINUTE);

					calendar.setTimeInMillis(selection.first);
					calendar.set(Calendar.HOUR_OF_DAY, previousHour);
					calendar.set(Calendar.MINUTE, previousMinute);

					eventDataViewModel.setDtStart(calendar.getTime());
					setDateText(DateTimeType.START, calendar.getTimeInMillis());
				}
				if (selection.second != null) {
					calendar.setTimeInMillis(eventDataViewModel.getEVENT().getAsLong(CalendarContract.Events.DTEND));
					previousHour = calendar.get(Calendar.HOUR_OF_DAY);
					previousMinute = calendar.get(Calendar.MINUTE);

					calendar.setTimeInMillis(selection.second);
					calendar.set(Calendar.HOUR_OF_DAY, previousHour);
					calendar.set(Calendar.MINUTE, previousMinute);

					eventDataViewModel.setDtEnd(calendar.getTime());
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
			calendar.setTimeInMillis(eventDataViewModel.getEVENT().getAsLong(CalendarContract.Events.DTSTART));
		} else if (dateType == DateTimeType.END) {
			calendar.setTimeInMillis(eventDataViewModel.getEVENT().getAsLong(CalendarContract.Events.DTEND));
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
			newCalendar.setTimeInMillis(dateType == DateTimeType.START ? eventDataViewModel.getEVENT().getAsLong(CalendarContract.Events.DTSTART)
					: eventDataViewModel.getEVENT().getAsLong(CalendarContract.Events.DTEND));
			newCalendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
			newCalendar.set(Calendar.MINUTE, timePicker.getMinute());

			if (dateType == DateTimeType.START) {
				if (newCalendar.getTimeInMillis() <= eventDataViewModel.getEVENT().getAsLong(CalendarContract.Events.DTEND)) {
					eventDataViewModel.setDtStart(newCalendar.getTime());
					setTimeText(dateType, newCalendar.getTimeInMillis());
				} else {
					String msg =
							EventUtil.convertTime(calendar.getTimeInMillis(), App.isPreference_key_using_24_hour_system()) + " " + getString(R.string.plz_set_time_before_specific_time);
					Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
				}
			} else if (dateType == DateTimeType.END) {
				if (newCalendar.getTimeInMillis() >= eventDataViewModel.getEVENT().getAsLong(CalendarContract.Events.DTSTART)) {
					eventDataViewModel.setDtEnd(newCalendar.getTime());
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

							eventDataViewModel.setEventLocation(resultLocation);
							binding.locationLayout.eventLocation.setText(resultLocation);

							break;
						}

						case RESULT_CODE_REMOVED_LOCATION: {
							eventDataViewModel.setEventLocation(null);
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
							Intent intent = result.getData();
							List<ContentValues> resultAttendeeList = intent.getParcelableArrayListExtra("attendeeList");

							eventDataViewModel.setAttendees(resultAttendeeList,
									intent.getBooleanExtra(CalendarContract.Events.GUESTS_CAN_MODIFY, false),
									intent.getBooleanExtra(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, false)
									, intent.getBooleanExtra(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, false));
							setAttendeesText(resultAttendeeList);
						}
					});

	protected final ActivityResultLauncher<Intent> recurrenceActivityResultLauncher =
			registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
					, new ActivityResultCallback<ActivityResult>() {
						@Override
						public void onActivityResult(ActivityResult result) {
							if (result.getResultCode() == RESULT_OK) {
								String rRule = result.getData().getStringExtra(CalendarContract.Events.RRULE);
								eventDataViewModel.setRecurrence(rRule);
								setRecurrenceText(rRule);
							} else {

							}
						}
					});

	protected final ActivityResultLauncher<Intent> remindersActivityResultLauncher =
			registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
					, new ActivityResultCallback<ActivityResult>() {
						@Override
						public void onActivityResult(ActivityResult result) {
							ContentValues reminder = (ContentValues) result.getData().getParcelableExtra("reminder");
							final int previousMinutes = result.getData().getIntExtra("previousMinutes", 0);

							try {
								switch (EventIntentCode.enumOf(result.getResultCode())) {
									case RESULT_ADDED_REMINDER:
										// reminder values는 분, 메소드값을 담고 있어야 한다
										// 수정된 minutes, method가 기존 값과 중복되는 경우 진행하지 않음
										if (eventDataViewModel.addReminder(reminder.getAsInteger(CalendarContract.Reminders.MINUTES),
												reminder.getAsInteger(CalendarContract.Reminders.METHOD))) {
											addReminderItemView(reminder);
										} else {
											Toast.makeText(EditEventActivity.this, R.string.duplicate_value, Toast.LENGTH_SHORT).show();
										}
										break;

									case RESULT_MODIFIED_REMINDER:
										// 수정된 minutes, method가 기존 값과 중복되는 경우 진행하지 않음
										int newMinutes = reminder.getAsInteger(CalendarContract.Reminders.MINUTES);
										int newMethod = reminder.getAsInteger(CalendarContract.Reminders.METHOD);

										eventDataViewModel.modifyReminder(previousMinutes, newMinutes, newMethod);
										modifyReminderItemView(previousMinutes, newMinutes, newMethod);
										break;

									case RESULT_REMOVED_REMINDER:
										eventDataViewModel.removeReminder(previousMinutes);
										removeReminderItemView(previousMinutes);
										break;
								}

							} catch (IllegalArgumentException e) {

							}

						}
					});

	protected final ActivityResultLauncher<Intent> timeZoneActivityResultLauncher =
			registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
					, new ActivityResultCallback<ActivityResult>() {
						@Override
						public void onActivityResult(ActivityResult result) {
							if (result.getResultCode() == RESULT_OK) {
								TimeZone timeZone = (TimeZone) result.getData().getSerializableExtra(CalendarContract.Events.EVENT_TIMEZONE);
								eventDataViewModel.setTimezone(timeZone.getID());
								setTimeZoneText(timeZone.getID());
							}
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

