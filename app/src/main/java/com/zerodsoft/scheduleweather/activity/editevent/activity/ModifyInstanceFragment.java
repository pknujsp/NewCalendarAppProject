package com.zerodsoft.scheduleweather.activity.editevent.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.activity.editevent.adapter.CalendarListAdapter;
import com.zerodsoft.scheduleweather.activity.editevent.fragments.TimeZoneFragment;
import com.zerodsoft.scheduleweather.activity.preferences.ColorListAdapter;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.common.enums.EventIntentCode;
import com.zerodsoft.scheduleweather.common.enums.LocationIntentCode;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.event.common.DetailLocationSelectorKey;
import com.zerodsoft.scheduleweather.event.common.SelectionDetailLocationFragment;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.NetworkStatus;
import com.zerodsoft.scheduleweather.utility.RecurrenceRule;
import com.zerodsoft.scheduleweather.utility.model.ReminderDto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ModifyInstanceFragment extends EventBaseFragment {
	private OnModifyInstanceResultListener onModifyInstanceResultListener;

	private CalendarViewModel calendarViewModel;
	private LocationViewModel locationViewModel;
	private EventDataViewModel eventDataViewModel;

	private AlertDialog accessLevelDialog;
	private AlertDialog availabilityDialog;
	private AlertDialog calendarDialog;

	private MaterialTimePicker timePicker;
	private MaterialDatePicker<Pair<Long, Long>> datePicker;

	private LocationDTO locationDTO;
	private NetworkStatus networkStatus;

	private boolean initializing = true;
	private ContentValues selectedCalendarValues;
	private ContentValues originalInstance;
	private boolean firstClickedTimeZone = false;

	private long originalInstanceBeginDate;
	private long originalInstanceEndDate;

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

		setViewListeners();
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
											//현재 인스턴스만 변경
											updateThisInstance();
											break;
										case 1:
											//현재 인스턴스 이후의 모든 인스턴스 변경
											updateAfterInstanceIncludingThisInstance();
											break;
										case 2:
											//모든 일정이면 event를 변경
											updateEvent();
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
			AlertDialog dialog;
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

				GridView gridView = new GridView(getContext());
				gridView.setAdapter(new ColorListAdapter(colorKey, colors,
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

		binding.timeLayout.timeAlldaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			boolean firstChecked = true;

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

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

				if (firstChecked) {
					firstChecked = false;
					if (originalInstance.getAsInteger(CalendarContract.Instances.ALL_DAY) == 1) {
						TimeZone timeZone = TimeZone.getTimeZone(originalInstance.getAsString(CalendarContract.Instances.CALENDAR_TIME_ZONE));
						eventDataViewModel.setTimezone(timeZone.getID());
						setTimeZoneText(timeZone.getID());
					}

					if (!eventDataViewModel.getNEW_EVENT().containsKey(CalendarContract.Events.DTSTART)) {
						eventDataViewModel.setDtStart(new Date(originalInstanceBeginDate));
						eventDataViewModel.setDtEnd(new Date(originalInstanceEndDate));
					}
				}
			}
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

			getParentFragmentManager().beginTransaction().hide(this)
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
			String rRule = null;

			if (eventDataViewModel.getNEW_EVENT().containsKey(CalendarContract.Events.RRULE)) {
				rRule = eventDataViewModel.getNEW_EVENT().getAsString(CalendarContract.Events.RRULE);
			} else if (originalInstance.getAsString(CalendarContract.Instances.RRULE) != null) {
				rRule = originalInstance.getAsString(CalendarContract.Instances.RRULE);
			} else {
				rRule = "";
			}

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
			getParentFragmentManager().beginTransaction().hide(this)
					.add(R.id.fragment_container, eventRecurrenceFragment, getString(R.string.tag_event_recurrence_fragment))
					.addToBackStack(getString(R.string.tag_event_recurrence_fragment)).commit();
		});

        /*
        접근수준
         */
		binding.accesslevelLayout.eventAccessLevel.setOnClickListener(new View.OnClickListener() {
			boolean firstClicked = true;

			@Override
			public void onClick(View v) {
				int checkedItem;

				if (firstClicked) {
					firstClicked = false;
					checkedItem = originalInstance.getAsInteger(CalendarContract.Instances.ACCESS_LEVEL);
				} else {
					checkedItem = eventDataViewModel.getNEW_EVENT().getAsInteger(CalendarContract.Events.ACCESS_LEVEL);
				}

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
		});

        /*
        유효성
         */
		binding.availabilityLayout.eventAvailability.setOnClickListener(new View.OnClickListener() {
			boolean firstClicked = true;

			@Override
			public void onClick(View v) {
				int checkedItem;

				if (firstClicked) {
					firstClicked = false;
					checkedItem = originalInstance.getAsInteger(CalendarContract.Instances.AVAILABILITY);
				} else {
					checkedItem = eventDataViewModel.getNEW_EVENT().getAsInteger(CalendarContract.Events.AVAILABILITY);
				}

				MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
				dialogBuilder.setSingleChoiceItems(EventUtil.getAvailabilityItems(requireContext()), checkedItem, (dialogInterface, item) ->
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
		});

        /*
        캘린더 선택
         */
		binding.calendarLayout.eventCalendarValueView.setOnClickListener(view ->
		{
			MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
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

								if (!eventDataViewModel.getATTENDEES().isEmpty()) {
									eventDataViewModel.removeAttendee(accountName);
									setAttendeesText(eventDataViewModel.getATTENDEES());
								}
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
			getParentFragmentManager().beginTransaction().hide(this)
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
						eventDataViewModel.getNEW_EVENT().put(CalendarContract.Events.EVENT_LOCATION, "");
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
				getParentFragmentManager().beginTransaction().hide(this).add(R.id.fragment_container
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
			boolean firstClicked = true;

			@Override
			public void onClick(View view) {
				ContentValues organizer = new ContentValues();

				organizer.put(CalendarContract.Attendees.ATTENDEE_NAME, selectedCalendarValues.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));
				organizer.put(CalendarContract.Attendees.ATTENDEE_EMAIL, selectedCalendarValues.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));
				organizer.put(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP, CalendarContract.Attendees.RELATIONSHIP_ORGANIZER);

				Bundle bundle = new Bundle();
				bundle.putParcelableArrayList("attendeeList", (ArrayList<? extends Parcelable>) eventDataViewModel.getATTENDEES());
				bundle.putParcelable("organizer", organizer);

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

				bundle.putBoolean(CalendarContract.Events.GUESTS_CAN_MODIFY, guestsCanModify);
				bundle.putBoolean(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, guestsCanInviteOthers);
				bundle.putBoolean(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, guestsCanSeeGuests);

				AttendeesFragment attendeesFragment = new AttendeesFragment(new AttendeesFragment.OnAttendeesResultListener() {
					@Override
					public void onResult(List<ContentValues> newAttendeeList, boolean guestsCanModify, boolean guestsCanInviteOthers, boolean guestsCanSeeGuests) {
						eventDataViewModel.setAttendees(newAttendeeList, guestsCanModify, guestsCanInviteOthers, guestsCanSeeGuests);
						setAttendeesText(newAttendeeList);
					}
				});

				attendeesFragment.setArguments(bundle);
				getParentFragmentManager().beginTransaction().hide(ModifyInstanceFragment.this)
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
			getParentFragmentManager().beginTransaction().hide(ModifyInstanceFragment.this)
					.add(R.id.fragment_container, eventReminderFragment, getString(R.string.tag_event_reminder_fragment))
					.addToBackStack(getString(R.string.tag_event_reminder_fragment)).commit();
		}
	};

	protected final View.OnClickListener removeReminderOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			ReminderItemHolder holder = (ReminderItemHolder) view.getTag();
			removeReminderItemView(holder.minutes);
			eventDataViewModel.removeReminder(holder.minutes);
		}
	};


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


	private void removeReminderItemView(int minutes) {
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
				eventDataViewModel.removeAttendee(holder.email);
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
			dtStart = originalInstanceBeginDate;
		}

		if (eventDataViewModel.getNEW_EVENT().containsKey(CalendarContract.Events.DTEND)) {
			dtEnd = eventDataViewModel.getNEW_EVENT().getAsLong(CalendarContract.Events.DTEND);
		} else {
			dtEnd = originalInstanceEndDate;
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

				datePicker.dismiss();
			}
		});
		datePicker.addOnNegativeButtonClickListener(view ->
		{
			datePicker.dismiss();
		});

		datePicker.show(getChildFragmentManager(), datePicker.toString());
	}

	protected void showTimePicker(DateTimeType dateType) {
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
			setAttendeesText(eventDataViewModel.getATTENDEES());
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
			int dayDifference = endDay - startDay;

			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			calendar.setTimeInMillis(originalInstance.getAsLong(CalendarContract.Instances.BEGIN));

			setTimeZoneText(originalInstance.getAsString(CalendarContract.Events.CALENDAR_TIME_ZONE));

			setDateText(DateTimeType.START, calendar.getTime().getTime());
			setTimeText(DateTimeType.START, calendar.getTime().getTime());
			originalInstanceBeginDate = calendar.getTimeInMillis();

			calendar.add(Calendar.DAY_OF_YEAR, dayDifference);

			setDateText(DateTimeType.END, calendar.getTime().getTime());
			setTimeText(DateTimeType.END, calendar.getTime().getTime());
			originalInstanceEndDate = calendar.getTimeInMillis();
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
		exceptionEvent.put(CalendarContract.Events.STATUS, CalendarContract.Events.STATUS_CANCELED);

		Uri exceptionUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_EXCEPTION_URI,
				originalInstance.getAsLong(CalendarContract.Instances.EVENT_ID));
		ContentResolver contentResolver = getContext().getContentResolver();
		Uri result = contentResolver.insert(exceptionUri, exceptionEvent);

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
			setNewEventValues(CalendarContract.Events.RRULE, newEventValues, modifiedInstance);
		}

		if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
			return;
		}
		Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, newEventValues);
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
								locationViewModel.addLocation(savedLocationDto, locationDbQueryCallback);
							}

							@Override
							public void onResultNoData() {
								getParentFragmentManager().popBackStack();
							}
						});
			} else {
				//위치를 변경함
				locationDTO.setEventId(newEventId);
				locationViewModel.addLocation(locationDTO, locationDbQueryCallback);
			}
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

		final long eventId = originalInstance.getAsInteger(CalendarContract.Instances.EVENT_ID);
		RecurrenceRule recurrenceRule = new RecurrenceRule();
		recurrenceRule.separateValues(originalInstance.getAsString(CalendarContract.Instances.RRULE));

		final int originalCount = Integer.parseInt(recurrenceRule.getValue(RecurrenceRule.COUNT));
		final long instanceId = originalInstance.getAsLong(CalendarContract.Instances._ID);
		int instanceCount = 0;

		String[] projection = {CalendarContract.Instances._ID, CalendarContract.Instances.EVENT_ID};
		Cursor cursor = CalendarContract.Instances.query(getContext().getContentResolver(), projection,
				originalInstance.getAsLong(CalendarContract.Instances.DTSTART),
				originalInstance.getAsLong(CalendarContract.Instances.END));

		while (cursor.moveToNext()) {
			if (cursor.getLong(1) == eventId) {
				instanceCount++;
			}
			if (cursor.getLong(0) == instanceId) {
				break;
			}
		}

		cursor.close();

		//특정 날짜까지 반복인 경우
		if (recurrenceRule.containsKey(RecurrenceRule.UNTIL)) {
			//수정한 인스턴스의 종료일 가져오기
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(modifiedInstance.getAsLong(CalendarContract.Instances.BEGIN));
			final Date beginOfModifiedInstance = calendar.getTime();

			//기존 이벤트의 반복 종료일을 수정한 인스턴스의 종료일로 설정
			//기존 이벤트의 rrule을 수정
			recurrenceRule.putValue(RecurrenceRule.UNTIL, ClockUtil.yyyyMMdd.format(beginOfModifiedInstance));
		}//n회 반복 이벤트인 경우 or 계속 반복인 경우
		else {
			int newCount = instanceCount - 1;
			if (newCount == 0) {
				//remove recurrence
				recurrenceRule.clear();
			} else {
				recurrenceRule.putValue(RecurrenceRule.COUNT, newCount);
			}
		}

		ContentValues originalEventValues = new ContentValues();
		if (!recurrenceRule.isEmpty()) {
			originalEventValues.put(CalendarContract.Events.RRULE, recurrenceRule.getRule());
		}
		getContext().getContentResolver().update(ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId),
				originalEventValues, null, null);

		//수정된 인스턴스를 새로운 이벤트로 저장
		if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			return;
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
		//rrule 수정
		if (originalInstance.getAsString(CalendarContract.Instances.RRULE) != null && modifiedInstance.containsKey(CalendarContract.Events.RRULE)) {
			RecurrenceRule newEventRrule = new RecurrenceRule();
			newEventRrule.separateValues(originalInstance.getAsString(CalendarContract.Instances.RRULE));

			//until이 이벤트의 종료 날짜 이전인 경우 - 반복 삭제
			if (newEventRrule.containsKey(RecurrenceRule.UNTIL)) {
				String until = newEventRrule.getValue(RecurrenceRule.UNTIL);

				Calendar untilCalendar = EventRecurrenceFragment.convertDate(until);
				if (untilCalendar.getTime().before(new Date(newEventValues.getAsLong(CalendarContract.Events.DTEND)))) {
					recurrenceRule.clear();
				}
			} else if (newEventRrule.containsKey(RecurrenceRule.COUNT)) {
				int count = originalCount - instanceCount;
				if (count == 0) {
					//remove recurrence
					recurrenceRule.clear();
				} else {
					recurrenceRule.putValue(RecurrenceRule.COUNT, count);
				}
			}

			if (!recurrenceRule.isEmpty()) {
				newEventValues.put(CalendarContract.Events.RRULE, recurrenceRule.getRule());
			} else {
				newEventValues.remove(CalendarContract.Events.RRULE);
			}
		} else if (modifiedInstance.containsKey(CalendarContract.Events.RRULE)) {
			newEventValues.put(CalendarContract.Events.RRULE, modifiedInstance.getAsString(CalendarContract.Events.RRULE));

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
			if (locationDTO == null) {
				//위치를 바꾸지 않고, 기존 이벤트의 값을 그대로 유지
				locationViewModel.getLocation(eventId, new DbQueryCallback<LocationDTO>() {
					@Override
					public void onResultSuccessful(LocationDTO savedLocationDto) {
						savedLocationDto.setEventId(newEventId);
						locationViewModel.addLocation(savedLocationDto, locationDbQueryCallback);
					}

					@Override
					public void onResultNoData() {
						getParentFragmentManager().popBackStack();
					}
				});
			} else {
				//위치를 변경함
				locationDTO.setEventId(newEventId);
				locationViewModel.addLocation(locationDTO, locationDbQueryCallback);
			}
		}
	}

	private final DbQueryCallback<LocationDTO> locationDbQueryCallback = new DbQueryCallback<LocationDTO>() {
		@Override
		public void onResultSuccessful(LocationDTO result) {
			getParentFragmentManager().popBackStack();
		}

		@Override
		public void onResultNoData() {

		}
	};

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
			if (!modifiedEvent.getAsString(CalendarContract.Events.EVENT_LOCATION).isEmpty()) {
				locationDTO.setEventId(eventId);
				locationViewModel.addLocation(locationDTO, new DbQueryCallback<LocationDTO>() {
					@Override
					public void onResultSuccessful(LocationDTO result) {
						getParentFragmentManager().popBackStack();
					}

					@Override
					public void onResultNoData() {

					}
				});
			} else {
				locationViewModel.removeLocation(eventId, null);
			}
		} else {
			getParentFragmentManager().popBackStack();
		}
	}

	private void setNewEventValues(String key, ContentValues newEventValues, ContentValues modifiedInstance) {
		if (modifiedInstance.containsKey(key)) {
			newEventValues.put(key, modifiedInstance.getAsString(key));
		} else if (originalInstance.containsKey(key)) {
			newEventValues.put(key, originalInstance.getAsString(key));
		}
	}

	public interface OnModifyInstanceResultListener {

		void onResultModifiedEvent(long eventId, long begin);

		void onResultModifiedThisInstance();

		void onResultModifiedAfterAllInstancesIncludingThisInstance();
	}
}
