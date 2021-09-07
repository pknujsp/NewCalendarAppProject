package com.zerodsoft.calendarplatform.event.event.fragments;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.calendar.CalendarViewModel;
import com.zerodsoft.calendarplatform.calendar.calendarcommon2.EventRecurrence;
import com.zerodsoft.calendarplatform.common.interfaces.DialogController;
import com.zerodsoft.calendarplatform.databinding.EventFragmentBinding;
import com.zerodsoft.calendarplatform.event.util.EventUtil;
import com.zerodsoft.calendarplatform.utility.NetworkStatus;
import com.zerodsoft.calendarplatform.utility.model.ReminderDto;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class EventFragment extends BottomSheetDialogFragment implements DialogController {
	// 참석자가 있는 경우 참석 여부 표시
	// 알림 값을 클릭하면 알림표시를 하는 시각을 보여준다
    /*
    공휴일인 경우 : 제목, 날짜, 이벤트 색상, 캘린더 정보만 출력
     */
	private final int VIEW_HEIGHT;
	private OnEventEditCallback onEventEditCallback;

	private long eventId;
	private long instanceId;
	private long originalBegin;
	private long originalEnd;

	private Boolean isOrganizer = null;
	private Long attendeeIdForThisAccount = null;
	public boolean editing;

	private EventFragmentBinding binding;
	private ContentValues instanceValues;
	private List<ContentValues> attendeeList;
	private CalendarViewModel calendarViewModel;
	private ArrayAdapter<CharSequence> answerForInviteSpinnerAdapter;

	private AlertDialog attendeeDialog;
	private BottomSheetBehavior<FrameLayout> bottomSheetBehavior;

	private NetworkStatus networkStatus;

	public EventFragment(OnEventEditCallback onEventEditCallback, int VIEW_HEIGHT) {
		this.onEventEditCallback = onEventEditCallback;
		this.VIEW_HEIGHT = VIEW_HEIGHT;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		calendarViewModel = new ViewModelProvider(requireActivity()).get(CalendarViewModel.class);

		Bundle arguments = getArguments();
		eventId = arguments.getLong(CalendarContract.Instances.EVENT_ID);
		instanceId = arguments.getLong(CalendarContract.Instances._ID);
		originalBegin = arguments.getLong(CalendarContract.Instances.BEGIN);
		originalEnd = arguments.getLong(CalendarContract.Instances.END);

		networkStatus = new NetworkStatus(getContext(), new ConnectivityManager.NetworkCallback() {
			@Override
			public void onAvailable(Network network) {
				super.onAvailable(network);
			}

			@Override
			public void onLost(Network network) {
				super.onLost(network);
			}
		});
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		bottomSheetBehavior = ((BottomSheetDialog) dialog).getBehavior();
		bottomSheetBehavior.setDraggable(false);
		bottomSheetBehavior.setHideable(false);
		bottomSheetBehavior.setPeekHeight(0);
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

		return dialog;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		networkStatus.unregisterNetworkCallback();
	}

	@Override
	public void onCancel(@NonNull @NotNull DialogInterface dialog) {
		dismiss();
	}

	@Override
	public void onDismiss(@NonNull @NotNull DialogInterface dialog) {
		super.onDismiss(dialog);
	}

	public void dismissForEdit() {
		editing = true;
		dismiss();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = EventFragmentBinding.inflate(inflater, container, false);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		View bottomSheet = getDialog().findViewById(R.id.design_bottom_sheet);
		bottomSheet.getLayoutParams().height = VIEW_HEIGHT;

		binding.eventRemindersView.addReminderButton.setVisibility(View.GONE);
		binding.eventAttendeesView.showAttendeesDetail.setVisibility(View.GONE);
		binding.eventDatetimeView.allDaySwitchLayout.setVisibility(View.GONE);

		binding.eventFab.setOnClickListener(new View.OnClickListener() {
			boolean isExpanded = true;

			@Override
			public void onClick(View view) {
				if (isExpanded) {
					binding.eventFab.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.more_icon));
					binding.fabsContainer.setVisibility(View.GONE);
				} else {
					binding.eventFab.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.close_icon));
					binding.fabsContainer.setVisibility(View.VISIBLE);
				}
				isExpanded = !isExpanded;
			}
		});

		binding.selectDetailLocationFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (networkStatus.networkAvailable()) {
					onEventEditCallback.onProcess(OnEventEditCallback.ProcessType.DETAIL_LOCATION);
				}
			}
		});

		binding.modifyEventFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onEventEditCallback.onProcess(OnEventEditCallback.ProcessType.MODIFY_EVENT);
				dismissForEdit();
			}
		});

		binding.removeEventFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onEventEditCallback.onProcess(OnEventEditCallback.ProcessType.REMOVE_EVENT);
			}
		});

		setInstanceData();
	}


	private void addAttendeeRow(String attendeeEmail, String attendeeRelationshipStr, String attendeeStatusStr) {
		TableRow tableRow = new TableRow(getContext());
		View row = getLayoutInflater().inflate(R.layout.event_attendee_item, null);
		LinearLayout attendeeInfoLayout = (LinearLayout) row.findViewById(R.id.attendee_info_layout);

		TextView attendeeEmailView = (TextView) row.findViewById(R.id.attendee_name);
		TextView attendeeRelationshipView = (TextView) row.findViewById(R.id.attendee_relationship);
		TextView attendeeStatusView = (TextView) row.findViewById(R.id.attendee_status);
		// 삭제버튼 숨기기
		row.findViewById(R.id.remove_attendee_button).setVisibility(View.GONE);

		attendeeInfoLayout.setClickable(true);
		attendeeInfoLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// logics for communications with attendee
				if (attendeeDialog == null) {
					final String[] itemList = {"기능 구성중"};
					MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext())
							.setTitle(attendeeEmail + "(" + attendeeRelationshipStr + ", " + attendeeStatusStr + ")")
							.setItems(itemList, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {

								}
							});
					attendeeDialog = builder.create();
				}
				attendeeDialog.setTitle(attendeeEmail + "(" + attendeeRelationshipStr + ", " + attendeeStatusStr + ")");
				attendeeDialog.show();
				// 기능목록 파악중
			}
		});

		attendeeEmailView.setText(attendeeEmail);
		attendeeRelationshipView.setText(attendeeRelationshipStr);
		attendeeStatusView.setText(attendeeStatusStr);

		tableRow.addView(row);
		binding.eventAttendeesView.eventAttendeesTable.addView(tableRow,
				new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
	}

	private void setAnswerForInviteSpinner() {
		if (attendeeList.isEmpty()) {
			return;
		}

		//이벤트 캘린더 계정의 응답값 확인
		int attendeeStatus = -1;
		int selectionIndex = -1;
		final String accountOfEvent = instanceValues.getAsString(Events.ACCOUNT_NAME);

		if (isOrganizer) {
			attendeeStatus = instanceValues.getAsInteger(Events.SELF_ATTENDEE_STATUS);
		} else {
			for (ContentValues attendee : attendeeList) {
				if (attendee.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL).equals(accountOfEvent)) {
					attendeeIdForThisAccount = attendee.getAsLong(CalendarContract.Attendees._ID);
					attendeeStatus = attendee.getAsInteger(CalendarContract.Attendees.ATTENDEE_STATUS);
					break;
				}
			}
		}

		switch (attendeeStatus) {
			case CalendarContract.Attendees.ATTENDEE_STATUS_ACCEPTED:
				selectionIndex = 0;
				break;
			case CalendarContract.Attendees.ATTENDEE_STATUS_DECLINED:
				selectionIndex = 1;
				break;
			case CalendarContract.Attendees.ATTENDEE_STATUS_INVITED:
				selectionIndex = 2;
				break;
			case CalendarContract.Attendees.ATTENDEE_STATUS_NONE:
				selectionIndex = 3;
				break;
		}

		answerForInviteSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
				isOrganizer ? R.array.answer_for_invite_organizer_spinner :
						R.array.answer_for_invite_attendee_spinner, android.R.layout.simple_spinner_item);
		answerForInviteSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		binding.eventAttendeesView.answerSpinner.setAdapter(answerForInviteSpinnerAdapter);
		binding.eventAttendeesView.answerSpinner.setSelection(selectionIndex);
		binding.eventAttendeesView.answerSpinner.setOnItemSelectedListener(answerSpinnerOnItemSelectedListener);
	}

	private final AdapterView.OnItemSelectedListener answerSpinnerOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
		boolean initializing = true;

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (initializing) {
				initializing = false;
				return;
			}
			ContentValues updateContentValues = new ContentValues();
			Uri uri = null;
			String where = CalendarContract.Attendees._ID + "=?";
			String[] selectionArgs = {attendeeIdForThisAccount.toString()};

			if (isOrganizer) {
				uri = ContentUris.withAppendedId(Events.CONTENT_URI, instanceValues.getAsLong(CalendarContract.Instances.EVENT_ID));
			} else {
				uri = CalendarContract.Attendees.CONTENT_URI;
			}

			switch (position) {
				case 0:
					updateContentValues.put(CalendarContract.Attendees.ATTENDEE_STATUS, CalendarContract.Attendees.ATTENDEE_STATUS_ACCEPTED);
					break;
				case 1:
					updateContentValues.put(CalendarContract.Attendees.ATTENDEE_STATUS, CalendarContract.Attendees.ATTENDEE_STATUS_DECLINED);
					break;
				case 2:
					updateContentValues.put(CalendarContract.Attendees.ATTENDEE_STATUS, CalendarContract.Attendees.ATTENDEE_STATUS_INVITED);
					break;
				case 3:
					updateContentValues.put(CalendarContract.Attendees.ATTENDEE_STATUS, CalendarContract.Attendees.ATTENDEE_STATUS_NONE);
					break;
			}

			getContext().getContentResolver().update(uri, updateContentValues, where, selectionArgs);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};


	private void setInstanceData() {
		// 제목, 캘린더, 시간, 시간대, 반복, 알림, 설명, 위치, 공개범위, 유효성, 참석자
		// 캘린더, 시간대, 참석자 정보는 따로 불러온다.
		// 제목
		instanceValues = calendarViewModel.getInstance(instanceId, originalBegin, originalEnd);

		if (instanceValues.containsKey(Instances.CALENDAR_ACCESS_LEVEL)) {
			if (instanceValues.getAsInteger(Instances.CALENDAR_ACCESS_LEVEL) == Instances.CAL_ACCESS_READ) {
				binding.fabsLayout.setVisibility(View.GONE);
			}
		}

		if (instanceValues.getAsString(CalendarContract.Instances.TITLE) != null) {
			if (!instanceValues.getAsString(CalendarContract.Instances.TITLE).isEmpty()) {
				binding.eventTitle.setText(instanceValues.getAsString(CalendarContract.Instances.TITLE));
			} else {
				binding.eventTitle.setText(getString(R.string.empty_title));
			}
		} else {
			binding.eventTitle.setText(getString(R.string.empty_title));
		}
		//캘린더
		setCalendarText();

		final long begin = instanceValues.getAsLong(Instances.BEGIN);
		final long end = instanceValues.getAsLong(Instances.END);

		// 시간대
		final boolean isAllDay = instanceValues.getAsInteger(Instances.ALL_DAY) == 1;
		if (isAllDay) {
			// allday이면 시간대 뷰를 숨긴다
			binding.eventDatetimeView.eventTimezoneLayout.setVisibility(View.GONE);
			binding.eventDatetimeView.startTime.setVisibility(View.GONE);
			binding.eventDatetimeView.endTime.setVisibility(View.GONE);

			final int startDay = instanceValues.getAsInteger(Instances.START_DAY);
			final int endDay = instanceValues.getAsInteger(Instances.END_DAY);
			final int dayDifference = endDay - startDay;

			if (dayDifference == 0) {
				binding.eventDatetimeView.eventStartdatetimeLabel.setText(R.string.date);
				binding.eventDatetimeView.enddatetimeLayout.setVisibility(View.GONE);
				binding.eventDatetimeView.startDate.setText(EventUtil.convertDate(begin));
			} else {
				binding.eventDatetimeView.startDate.setText(EventUtil.convertDate(begin));

				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(end);
				calendar.add(Calendar.DATE, -1);
				binding.eventDatetimeView.endDate.setText(EventUtil.convertDate(calendar.getTimeInMillis()));
			}
		} else {
			TimeZone calendarTimeZone =
					TimeZone.getTimeZone(instanceValues.containsKey(Events.CALENDAR_TIME_ZONE) ?
							instanceValues.getAsString(Events.CALENDAR_TIME_ZONE) : instanceValues.getAsString(Events.EVENT_TIMEZONE));
			TimeZone eventTimeZone = TimeZone.getTimeZone(instanceValues.getAsString(Events.EVENT_TIMEZONE));

			setTimeZoneText(eventTimeZone, calendarTimeZone, begin, end);

			Calendar calendar = Calendar.getInstance(calendarTimeZone);
			calendar.setTimeZone(calendarTimeZone);
			calendar.setTimeInMillis(begin);
			long beginByCalendarTimeZone = calendar.getTimeInMillis();
			calendar.setTimeInMillis(end);
			long endByCalendarTimeZone = calendar.getTimeInMillis();


			binding.eventDatetimeView.startDate.setText(EventUtil.convertDate(beginByCalendarTimeZone));
			binding.eventDatetimeView.startTime.setText(EventUtil.convertTime(beginByCalendarTimeZone));
			binding.eventDatetimeView.endDate.setText(EventUtil.convertDate(endByCalendarTimeZone));
			binding.eventDatetimeView.endTime.setText(EventUtil.convertTime(endByCalendarTimeZone));
		}

		// 반복
		if (instanceValues.getAsString(Instances.RRULE) != null) {
			setRecurrenceText(instanceValues.getAsString(Instances.RRULE));
			binding.eventRecurrenceView.getRoot().setVisibility(View.VISIBLE);
		} else {
			binding.eventRecurrenceView.getRoot().setVisibility(View.GONE);
		}

		// 알람
		if (instanceValues.getAsBoolean(CalendarContract.Instances.HAS_ALARM)) {
			List<ContentValues> reminderList = calendarViewModel.getReminders(eventId);
			setReminderText(reminderList);
			binding.eventRemindersView.notReminder.setVisibility(View.GONE);
			binding.eventRemindersView.remindersTable.setVisibility(View.VISIBLE);
			binding.eventRemindersView.getRoot().setVisibility(View.VISIBLE);
		} else {
			// 알람이 없으면 알람 테이블을 숨기고, 알람 없음 텍스트를 표시한다.
			binding.eventRemindersView.notReminder.setVisibility(View.VISIBLE);
			binding.eventRemindersView.remindersTable.setVisibility(View.GONE);
			binding.eventRemindersView.getRoot().setVisibility(View.GONE);
		}

		// 설명
		binding.eventDescriptionView.descriptionEdittext.setVisibility(View.GONE);
		if (instanceValues.getAsString(CalendarContract.Instances.DESCRIPTION) != null) {
			if (!instanceValues.getAsString(CalendarContract.Instances.DESCRIPTION).isEmpty()) {
				binding.eventDescriptionView.notDescription.setText(instanceValues.getAsString(CalendarContract.Instances.DESCRIPTION));
				binding.eventDescriptionView.getRoot().setVisibility(View.VISIBLE);
			} else {
				binding.eventDescriptionView.getRoot().setVisibility(View.GONE);
			}
		} else {
			binding.eventDescriptionView.getRoot().setVisibility(View.GONE);
		}

		// 위치
		if (instanceValues.getAsString(CalendarContract.Instances.EVENT_LOCATION) != null) {
			if (!instanceValues.getAsString(CalendarContract.Instances.EVENT_LOCATION).isEmpty()) {
				binding.eventLocationView.eventLocation.setText(instanceValues.getAsString(CalendarContract.Instances.EVENT_LOCATION));
				binding.eventLocationView.getRoot().setVisibility(View.VISIBLE);
				binding.selectDetailLocationFab.setVisibility(View.VISIBLE);
			} else {
				binding.eventLocationView.getRoot().setVisibility(View.GONE);
				binding.selectDetailLocationFab.setVisibility(View.GONE);
			}
		} else {
			binding.eventLocationView.getRoot().setVisibility(View.GONE);
			binding.selectDetailLocationFab.setVisibility(View.GONE);
		}

		// 참석자
		attendeeList = calendarViewModel.getAttendees(eventId);

		isOrganizer = instanceValues.containsKey(Events.IS_ORGANIZER) ?
				(instanceValues.getAsString(Events.IS_ORGANIZER).equals("1") ? true : false) : false;

		// 참석자가 없는 경우 - 테이블 숨김, 참석자 없음 텍스트 표시
		if (attendeeList.isEmpty()) {
			binding.modifyEventFab.setVisibility(View.VISIBLE);
			binding.eventAttendeesView.notAttendees.setVisibility(View.VISIBLE);
			binding.eventAttendeesView.eventAttendeesTable.setVisibility(View.GONE);
			binding.eventAttendeesView.answerLayout.setVisibility(View.GONE);
			binding.eventAttendeesView.getRoot().setVisibility(View.GONE);
		} else {
			final boolean guestsCanModify = instanceValues.getAsInteger(Events.GUESTS_CAN_MODIFY) == 1;
			final boolean guestsCanSeeGuests = instanceValues.getAsInteger(Events.GUESTS_CAN_SEE_GUESTS) == 1;
			final boolean guestsCanInviteOthers = instanceValues.getAsInteger(Events.GUESTS_CAN_INVITE_OTHERS) == 1;

			if (guestsCanInviteOthers) {

			} else {

			}

			binding.eventAttendeesView.notAttendees.setVisibility(View.GONE);
			binding.eventAttendeesView.eventAttendeesTable.setVisibility(View.VISIBLE);
			binding.eventAttendeesView.getRoot().setVisibility(View.VISIBLE);

			if (isOrganizer) {
				binding.eventAttendeesView.answerLayout.setVisibility(View.GONE);
			} else {
				binding.eventAttendeesView.answerLayout.setVisibility(View.VISIBLE);
				setAnswerForInviteSpinner();
			}

			if (binding.eventAttendeesView.eventAttendeesTable.getChildCount() > 0) {
				binding.eventAttendeesView.eventAttendeesTable.removeAllViews();
			}

			final String accountNameOfEvent = instanceValues.getAsString(Events.ACCOUNT_NAME);
			final String organizerEmail = attendeeList.get(0).getAsString(CalendarContract.Attendees.ORGANIZER);
			List<ContentValues> finalAttendeeList = new ArrayList<>();

			addAttendeeRow(organizerEmail, EventUtil.convertAttendeeRelationship(CalendarContract.Attendees.RELATIONSHIP_ORGANIZER, getContext()),
					"");

			for (ContentValues attendee : attendeeList) {
				if (attendee.getAsInteger(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP) ==
						CalendarContract.Attendees.RELATIONSHIP_ORGANIZER) {
					continue;
				}
				finalAttendeeList.add(attendee);
			}

			for (ContentValues attendee : finalAttendeeList) {
				// 이름, 메일 주소, 상태
				final String attendeeEmail = attendee.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL);
				final int attendeeStatus = attendee.getAsInteger(CalendarContract.Attendees.ATTENDEE_STATUS);
				final int attendeeRelationship = attendee.getAsInteger(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP);

				final String attendeeStatusStr = EventUtil.convertAttendeeStatus(attendeeStatus, getContext());
				final String attendeeRelationshipStr = EventUtil.convertAttendeeRelationship(attendeeRelationship, getContext());

				addAttendeeRow(attendeeEmail, attendeeRelationshipStr, attendeeStatusStr);
			}
		}

		// 공개 범위 표시
		if (instanceValues.getAsInteger(CalendarContract.Instances.ACCESS_LEVEL) != null) {
			setAccessLevelText();
			binding.eventAccessLevelView.getRoot().setVisibility(View.VISIBLE);
		} else {
			binding.eventAccessLevelView.getRoot().setVisibility(View.GONE);
		}

		// 유효성 표시
		if (instanceValues.getAsInteger(CalendarContract.Instances.AVAILABILITY) != null) {
			setAvailabilityText();
			binding.eventAvailabilityView.getRoot().setVisibility(View.VISIBLE);
		} else {
			binding.eventAvailabilityView.getRoot().setVisibility(View.GONE);
		}
	}

	private void setAvailabilityText() {
		binding.eventAvailabilityView.eventAvailability.setText(EventUtil.convertAvailability(instanceValues.getAsInteger(CalendarContract.Instances.AVAILABILITY), getContext()));
	}

	private void setAccessLevelText() {
		binding.eventAccessLevelView.eventAccessLevel.setText(EventUtil.convertAccessLevel(instanceValues.getAsInteger(CalendarContract.Instances.ACCESS_LEVEL), getContext()));
	}

	private void setTimeZoneText(TimeZone eventTimeZone, TimeZone calendarTimeZone, Long begin, Long end) {
		if (eventTimeZone.equals(calendarTimeZone)) {
			binding.eventDatetimeView.eventTimezone.setText(eventTimeZone.getDisplayName(Locale.KOREAN));
		} else {
			Calendar beginCalendarByCalendarTimeZone = Calendar.getInstance(eventTimeZone);
			Calendar endCalendarByCalendarTimeZone = Calendar.getInstance(eventTimeZone);
			beginCalendarByCalendarTimeZone.setTimeInMillis(begin);
			endCalendarByCalendarTimeZone.setTimeInMillis(end);

			String beginDateStr = EventUtil.getDateTimeStr(beginCalendarByCalendarTimeZone, false);
			String endDateStr = EventUtil.getDateTimeStr(endCalendarByCalendarTimeZone, false);

			SimpleDateFormat timeZoneFormat = new SimpleDateFormat("z", Locale.KOREAN);
			timeZoneFormat.setTimeZone(eventTimeZone);

			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(eventTimeZone.getDisplayName(Locale.KOREAN));
			stringBuilder.append("(");
			stringBuilder.append(timeZoneFormat.format(beginCalendarByCalendarTimeZone.getTime()));
			stringBuilder.append(")");
			stringBuilder.append("\n(");
			stringBuilder.append(beginDateStr);
			stringBuilder.append(" -> ");
			stringBuilder.append(endDateStr);
			stringBuilder.append(")");

			binding.eventDatetimeView.eventTimezone.setText(stringBuilder.toString());
		}
	}

	private void setReminderText(List<ContentValues> reminders) {
		LayoutInflater layoutInflater = getLayoutInflater();
		if (binding.eventRemindersView.remindersTable.getChildCount() > 0) {
			binding.eventRemindersView.remindersTable.removeAllViews();
		}

		for (ContentValues reminder : reminders) {
			ReminderDto reminderDto = EventUtil.convertAlarmMinutes(reminder.getAsInteger(CalendarContract.Reminders.MINUTES));
			String alarmValueText = EventUtil.makeAlarmText(reminderDto, getContext());

			TableRow tableRow = new TableRow(getContext());
			View row = layoutInflater.inflate(R.layout.event_reminder_item, null);

			// 삭제 버튼 숨기기
			row.findViewById(R.id.remove_reminder_button).setVisibility(View.GONE);
			((TextView) row.findViewById(R.id.reminder_value)).setText(alarmValueText);
			row.findViewById(R.id.reminder_value).setClickable(false);

			tableRow.addView(row);
			binding.eventRemindersView.remindersTable.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
		}
	}

	private void setRecurrenceText(String rRule) {
		EventRecurrence eventRecurrence = new EventRecurrence();
		eventRecurrence.parse(rRule);
		binding.eventRecurrenceView.eventRecurrence.setText(eventRecurrence.toSimple());
	}

	private void setCalendarText() {
		binding.eventCalendarView.calendarColor.setBackgroundColor(EventUtil.getColor(instanceValues.getAsInteger(CalendarContract.Instances.CALENDAR_COLOR)));
		binding.eventCalendarView.calendarDisplayName.setText(instanceValues.getAsString(CalendarContract.Instances.CALENDAR_DISPLAY_NAME));
		binding.eventCalendarView.calendarAccountName.setText(instanceValues.getAsString(CalendarContract.Instances.OWNER_ACCOUNT));
	}

	public ContentValues getInstanceValues() {
		return instanceValues;
	}

	public List<ContentValues> getAttendeeList() {
		return attendeeList;
	}

	@Override
	public void hideDialog() {

	}

	@Override
	public void showDialog() {
	}


	public interface OnEventEditCallback {
		enum ProcessType {
			DETAIL_LOCATION,
			MODIFY_EVENT,
			REMOVE_EVENT
		}

		void onResult();

		void onProcess(ProcessType processType);
	}
}
