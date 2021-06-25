package com.zerodsoft.scheduleweather.activity.editevent.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
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
import com.zerodsoft.scheduleweather.common.enums.EventIntentCode;
import com.zerodsoft.scheduleweather.common.enums.LocationIntentCode;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
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
	protected CalendarViewModel calendarViewModel;
	protected LocationViewModel locationViewModel;
	protected EventDataViewModel eventDataViewModel;

	protected AlertDialog accessLevelDialog;
	protected AlertDialog availabilityDialog;
	protected AlertDialog calendarDialog;

	protected MaterialTimePicker timePicker;
	protected MaterialDatePicker<Pair<Long, Long>> datePicker;

	protected LocationDTO locationDTO;
	protected NetworkStatus networkStatus;

	protected boolean initializing = true;
	protected ContentValues selectedCalendarValues;

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

		binding.reminderLayout.notReminder.setVisibility(View.GONE);
		binding.descriptionLayout.notDescription.setVisibility(View.GONE);
		binding.attendeeLayout.notAttendees.setVisibility(View.GONE);

		setViewListeners();
		setViewOnClickListeners();
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

				GridView gridView = new GridView(getContext());
				gridView.setAdapter(new ColorListAdapter(eventDataViewModel.getNEW_EVENT().getAsString(CalendarContract.Events.EVENT_COLOR_KEY), colors,
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

						dialog.dismiss();
					}
				});

				MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
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
			onCheckedAllDaySwitch();
		});

        /*
        시간대
         */
		binding.timeLayout.eventTimezone.setOnClickListener(view ->
		{
			TimeZoneFragment timeZoneFragment = new TimeZoneFragment(new TimeZoneFragment.OnTimeZoneResultListener() {
				@Override
				public void onResult(TimeZone timeZone) {
					eventDataViewModel.setTimezone(timeZone.getID());
					setTimeZoneText(timeZone.getID());
					getParentFragmentManager().popBackStackImmediate();
				}
			});

			getParentFragmentManager().beginTransaction().hide(EventBaseFragment.this)
					.add(R.id.fragment_container, timeZoneFragment, getString(R.string.tag_timezone_fragment))
					.addToBackStack(getString(R.string.tag_timezone_fragment)).commit();
		});

        /*
        반복
         */
		binding.recurrenceLayout.eventRecurrence.setOnClickListener(view ->
		{
			Bundle bundle = new Bundle();
			// 반복 룰과 이벤트의 시작 시간 전달
			String rRule = eventDataViewModel.getNEW_EVENT().containsKey(CalendarContract.Events.RRULE)
					? eventDataViewModel.getNEW_EVENT().getAsString(CalendarContract.Events.RRULE) : "";

			bundle.putString(CalendarContract.Events.RRULE, rRule);
			bundle.putLong(CalendarContract.Events.DTSTART, eventDataViewModel.getNEW_EVENT().getAsLong(CalendarContract.Events.DTSTART));

			EventRecurrenceFragment eventRecurrenceFragment = new EventRecurrenceFragment(new EventRecurrenceFragment.OnEventRecurrenceResultListener() {
				@Override
				public void onResult(String rRule) {
					eventDataViewModel.setRecurrence(rRule);
					setRecurrenceText(rRule);
				}
			});
			eventRecurrenceFragment.setArguments(bundle);

			getParentFragmentManager().beginTransaction().hide(EventBaseFragment.this)
					.add(R.id.fragment_container, eventRecurrenceFragment, getString(R.string.tag_event_recurrence_fragment))
					.addToBackStack(getString(R.string.tag_event_recurrence_fragment)).commit();
		});

        /*
        접근수준
         */
		binding.accesslevelLayout.eventAccessLevel.setOnClickListener(view ->
		{
			int checkedItem = eventDataViewModel.getNEW_EVENT().getAsInteger(CalendarContract.Events.ACCESS_LEVEL);

			if (checkedItem == 3) {
				checkedItem = 1;
			}

			MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
			dialogBuilder.setSingleChoiceItems(EventUtil.getAccessLevelItems(getContext()), checkedItem, (dialogInterface, item) ->
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
			int checkedItem = eventDataViewModel.getNEW_EVENT().getAsInteger(CalendarContract.Events.AVAILABILITY);

			MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
			dialogBuilder.setSingleChoiceItems(EventUtil.getAvailabilityItems(getContext()), checkedItem, (dialogInterface, item) ->
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
			MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
			dialogBuilder
					.setTitle(getString(R.string.calendar))
					.setAdapter(new CalendarListAdapter(getContext(), calendarViewModel.getCalendars())
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
			getParentFragmentManager().beginTransaction().hide(EventBaseFragment.this)
					.add(R.id.fragment_container, eventReminderFragment, getString(R.string.tag_event_reminder_fragment))
					.addToBackStack(getString(R.string.tag_event_reminder_fragment)).commit();
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
				SelectionDetailLocationFragment selectionDetailLocationFragment = new SelectionDetailLocationFragment(new SelectionDetailLocationFragment.OnDetailLocationSelectionResultListener() {
					@Override
					public void onResultChangedLocation(LocationDTO newLocation) {
						locationDTO = newLocation;
						String resultLocation = null;

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
						String resultLocation = null;

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
				Bundle bundle = new Bundle();

				if (locationDTO != null) {
					bundle.putParcelable(DetailLocationSelectorKey.SELECTED_LOCATION_DTO_IN_EVENT.value(), locationDTO);
					bundle.putInt("requestCode", LocationIntentCode.REQUEST_CODE_CHANGE_LOCATION.value());
				} else {
					bundle.putInt("requestCode", LocationIntentCode.REQUEST_CODE_SELECT_LOCATION_EMPTY_QUERY.value());
				}

				selectionDetailLocationFragment.setArguments(bundle);
				getParentFragmentManager().beginTransaction().hide(EventBaseFragment.this).add(R.id.fragment_container
						, selectionDetailLocationFragment, getString(R.string.tag_detail_location_selection_fragment))
						.addToBackStack(getString(R.string.tag_detail_location_selection_fragment)).commit();
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

				Bundle bundle = new Bundle();
				bundle.putParcelableArrayList("attendeeList", (ArrayList<? extends Parcelable>) eventDataViewModel.getATTENDEES());
				bundle.putParcelable("organizer", organizer);
				bundle.putBoolean(CalendarContract.Events.GUESTS_CAN_MODIFY,
						eventDataViewModel.getNEW_EVENT().getAsBoolean(CalendarContract.Events.GUESTS_CAN_MODIFY) != null
								&& (eventDataViewModel.getNEW_EVENT().getAsInteger(CalendarContract.Events.GUESTS_CAN_MODIFY) == 1));

				bundle.putBoolean(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS,
						eventDataViewModel.getNEW_EVENT().getAsBoolean(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS)
								!= null && (eventDataViewModel.getNEW_EVENT().getAsInteger(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS) == 1));

				bundle.putBoolean(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS,
						eventDataViewModel.getNEW_EVENT().getAsBoolean(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS)
								!= null && (eventDataViewModel.getNEW_EVENT().getAsInteger(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS) == 1));

				AttendeesFragment attendeesFragment = new AttendeesFragment(new AttendeesFragment.OnAttendeesResultListener() {
					@Override
					public void onResult(List<ContentValues> newAttendeeList, boolean guestsCanModify, boolean guestsCanInviteOthers, boolean guestsCanSeeGuests) {
						eventDataViewModel.setAttendees(newAttendeeList, guestsCanSeeGuests, guestsCanInviteOthers, guestsCanSeeGuests);
						setAttendeesText(newAttendeeList);
					}
				});
				attendeesFragment.setArguments(bundle);
				getParentFragmentManager().beginTransaction().hide(EventBaseFragment.this)
						.add(R.id.fragment_container, attendeesFragment, getString(R.string.tag_attendees_fragment))
						.addToBackStack(getString(R.string.tag_attendees_fragment)).commit();
			}
		});
	}


	protected void setRecurrenceText(String rRule) {
		if (!rRule.isEmpty()) {
			RecurrenceRule recurrenceRule = new RecurrenceRule();
			recurrenceRule.separateValues(rRule);
			binding.recurrenceLayout.eventRecurrence.setText(recurrenceRule.interpret(getContext()));
		} else {
			binding.recurrenceLayout.eventRecurrence.setText("");
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
				String alarmValueText = EventUtil.makeAlarmText(reminderDto, getContext());

				String text = alarmValueText + "(" + EventUtil.getReminderMethod(getContext(), newMethod) + ")";

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


	protected final void showDatePicker() {
		long dtStart = 0L;
		long dtEnd = 0L;

		if (eventDataViewModel.getNEW_EVENT().containsKey(CalendarContract.Events.DTSTART)) {
			dtStart = eventDataViewModel.getNEW_EVENT().getAsLong(CalendarContract.Events.DTSTART);
		} else {
			dtStart = showingDatePicker(DateTimeType.START);
		}

		if (eventDataViewModel.getNEW_EVENT().containsKey(CalendarContract.Events.DTEND)) {
			dtEnd = eventDataViewModel.getNEW_EVENT().getAsLong(CalendarContract.Events.DTEND);
		} else {
			dtEnd = showingDatePicker(DateTimeType.END);
		}

		final long finalDtStart = dtStart;
		final long finalDtEnd = dtEnd;

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

				if (selection.first != null) {
					calendar.setTimeInMillis(finalDtStart);
					previousHour = calendar.get(Calendar.HOUR_OF_DAY);
					previousMinute = calendar.get(Calendar.MINUTE);

					calendar.setTimeInMillis(selection.first);
					calendar.set(Calendar.HOUR_OF_DAY, previousHour);
					calendar.set(Calendar.MINUTE, previousMinute);

					eventDataViewModel.setDtStart(calendar.getTime());
					setDateText(DateTimeType.START, calendar.getTimeInMillis());
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
				}

				selectedDate();
				datePicker.dismiss();
			}
		});
		datePicker.addOnNegativeButtonClickListener(view ->
		{
			datePicker.dismiss();
		});

		datePicker.show(getParentFragmentManager(), datePicker.toString());
	}

	protected void showTimePicker(DateTimeType dateType) {
		Calendar calendar = Calendar.getInstance();
		Calendar compareCalendar = Calendar.getInstance();

		if (dateType == DateTimeType.START) {
			if (eventDataViewModel.getNEW_EVENT().containsKey(CalendarContract.Events.DTSTART)) {
				calendar.setTimeInMillis(eventDataViewModel.getNEW_EVENT().getAsLong(CalendarContract.Events.DTSTART));
			} else {
				calendar.setTimeInMillis(showingTimePicker(dateType));
			}
		} else if (dateType == DateTimeType.END) {
			if (eventDataViewModel.getNEW_EVENT().containsKey(CalendarContract.Events.DTEND)) {
				calendar.setTimeInMillis(eventDataViewModel.getNEW_EVENT().getAsLong(CalendarContract.Events.DTEND));
			} else {
				calendar.setTimeInMillis(showingTimePicker(dateType));
			}
		}

		if (dateType == DateTimeType.START) {
			if (eventDataViewModel.getNEW_EVENT().containsKey(CalendarContract.Events.DTEND)) {
				compareCalendar.setTimeInMillis(eventDataViewModel.getNEW_EVENT().getAsLong(CalendarContract.Events.DTEND));
			} else {
				compareCalendar.setTimeInMillis(showingTimePicker(DateTimeType.END));
			}
		} else if (dateType == DateTimeType.END) {
			if (eventDataViewModel.getNEW_EVENT().containsKey(CalendarContract.Events.DTSTART)) {
				compareCalendar.setTimeInMillis(eventDataViewModel.getNEW_EVENT().getAsLong(CalendarContract.Events.DTSTART));
			} else {
				compareCalendar.setTimeInMillis(showingTimePicker(DateTimeType.START));
			}
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
			Calendar newCalendar = calendar;
			newCalendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
			newCalendar.set(Calendar.MINUTE, timePicker.getMinute());

			if (dateType == DateTimeType.START) {
				if (newCalendar.getTimeInMillis() <= compareCalendar.getTimeInMillis()) {
					eventDataViewModel.setDtStart(newCalendar.getTime());
					setTimeText(dateType, newCalendar.getTimeInMillis());
				} else {
					String msg =
							EventUtil.convertTime(calendar.getTimeInMillis(), App.isPreference_key_using_24_hour_system()) + " " + getString(R.string.plz_set_time_before_specific_time);
					Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
				}
			} else if (dateType == DateTimeType.END) {
				if (newCalendar.getTimeInMillis() >= compareCalendar.getTimeInMillis()) {
					eventDataViewModel.setDtEnd(newCalendar.getTime());
					setTimeText(dateType, newCalendar.getTimeInMillis());
				} else {
					String msg =
							EventUtil.convertTime(calendar.getTimeInMillis(), App.isPreference_key_using_24_hour_system()) + " " + getString(R.string.plz_set_time_after_specific_time);
					Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
				}
			}

			selectedTime(dateType);

		});
		timePicker.addOnNegativeButtonClickListener(view ->
		{
			timePicker.dismiss();
		});
		timePicker.show(getParentFragmentManager(), timePicker.toString());
	}


	protected void setAccessLevelText(int accessLevel) {
		binding.accesslevelLayout.eventAccessLevel.setText(EventUtil.convertAccessLevel(accessLevel, getContext()));
	}

	protected void setAvailabilityText(int availability) {
		binding.availabilityLayout.eventAvailability.setText(EventUtil.convertAvailability(availability, getContext()));
	}

	protected final void convertDtEndForAllDay(ContentValues contentValues) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(contentValues.getAsLong(CalendarContract.Events.DTEND));
		calendar.add(Calendar.DAY_OF_YEAR, 1);

		contentValues.put(CalendarContract.Events.DTEND, calendar.getTimeInMillis());
	}

	protected abstract void onCheckedAllDaySwitch();

	protected abstract long showingDatePicker(DateTimeType dateTimeType);

	protected abstract void selectedDate();

	protected abstract long showingTimePicker(DateTimeType dateTimeType);

	protected abstract void selectedTime(DateTimeType dateTimeType);

}

