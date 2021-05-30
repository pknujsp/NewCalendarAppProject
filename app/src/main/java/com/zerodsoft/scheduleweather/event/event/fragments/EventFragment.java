package com.zerodsoft.scheduleweather.event.event.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.activity.editevent.activity.ModifyInstanceActivity;
import com.zerodsoft.scheduleweather.calendar.CalendarInstanceUtil;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IRefreshView;
import com.zerodsoft.scheduleweather.common.enums.EventIntentCode;
import com.zerodsoft.scheduleweather.common.enums.LocationIntentCode;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.databinding.EventFragmentBinding;
import com.zerodsoft.scheduleweather.event.common.DetailLocationSelectorKey;
import com.zerodsoft.scheduleweather.event.common.SelectionDetailLocationActivity;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationHistoryViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.utility.NetworkStatus;
import com.zerodsoft.scheduleweather.utility.RecurrenceRule;
import com.zerodsoft.scheduleweather.utility.model.ReminderDto;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import lombok.SneakyThrows;

public class EventFragment extends BottomSheetDialogFragment {
	public static final String TAG = "EventFragment";
	// 참석자가 있는 경우 참석 여부 표시
	// 알림 값을 클릭하면 알림표시를 하는 시각을 보여준다
    /*
    공휴일인 경우 : 제목, 날짜, 이벤트 색상, 캘린더 정보만 출력
     */
	private EventFragmentBinding binding;
	private ContentValues instanceValues;
	private List<ContentValues> attendeeList;
	private CalendarViewModel calendarViewModel;
	private LocationViewModel locationViewModel;

	private final int VIEW_HEIGHT;
	private final IRefreshView iRefreshView;

	private final int CALENDAR_ID;
	private final long EVENT_ID;
	private final long INSTANCE_ID;
	private final long ORIGINAL_BEGIN;
	private final long ORIGINAL_END;

	private AlertDialog attendeeDialog;

	private int resultCode = Activity.RESULT_CANCELED;

	private FoodCriteriaLocationInfoViewModel foodCriteriaLocationInfoViewModel;
	private FoodCriteriaLocationHistoryViewModel foodCriteriaLocationHistoryViewModel;
	private NetworkStatus networkStatus;

	public void showSetLocationDialog() {
		MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity())
				.setTitle(getString(R.string.request_select_location_title))
				.setMessage(getString(R.string.request_select_location_description))
				.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.cancel();
					}
				})
				.setPositiveButton(getString(R.string.check), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						Intent intent = new Intent(getActivity(), SelectionDetailLocationActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString(DetailLocationSelectorKey.LOCATION_NAME_IN_EVENT.value(),
								instanceValues.getAsString(CalendarContract.Instances.EVENT_LOCATION));
						bundle.putInt("requestCode", LocationIntentCode.REQUEST_CODE_SELECT_LOCATION_BY_QUERY.value());

						intent.putExtras(bundle);
						setLocationActivityResultLauncher.launch(intent);
						dialogInterface.dismiss();
					}
				});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public void startEditLocationActivity(LocationDTO locationDTO) {
		//naver
		Intent intent = new Intent(getActivity(), SelectionDetailLocationActivity.class);
		Bundle bundle = new Bundle();
		bundle.putParcelable(DetailLocationSelectorKey.SELECTED_LOCATION_DTO_IN_EVENT.value(), locationDTO);
		bundle.putInt("requestCode", LocationIntentCode.REQUEST_CODE_CHANGE_LOCATION.value());

		intent.putExtras(bundle);
		editLocationActivityResultLauncher.launch(intent);
	}

	public EventFragment(IRefreshView iRefreshView, int VIEW_HEIGHT, int CALENDAR_ID, long EVENT_ID, long INSTANCE_ID, long ORIGINAL_BEGIN, long ORIGINAL_END) {
		this.iRefreshView = iRefreshView;
		this.VIEW_HEIGHT = VIEW_HEIGHT;
		this.CALENDAR_ID = CALENDAR_ID;
		this.EVENT_ID = EVENT_ID;
		this.INSTANCE_ID = INSTANCE_ID;
		this.ORIGINAL_BEGIN = ORIGINAL_BEGIN;
		this.ORIGINAL_END = ORIGINAL_END;
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		networkStatus = new NetworkStatus(getContext(), new ConnectivityManager.NetworkCallback());
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		BottomSheetBehavior bottomSheetBehavior = ((BottomSheetDialog) dialog).getBehavior();
		bottomSheetBehavior.setDraggable(false);
		bottomSheetBehavior.setPeekHeight(0);
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

		return dialog;
	}


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = EventFragmentBinding.inflate(inflater, container, false);
		return binding.getRoot();
	}

	@SneakyThrows
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		View bottomSheet = getDialog().findViewById(R.id.design_bottom_sheet);
		bottomSheet.getLayoutParams().height = VIEW_HEIGHT;

		locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
		foodCriteriaLocationHistoryViewModel = new ViewModelProvider(this).get(FoodCriteriaLocationHistoryViewModel.class);
		foodCriteriaLocationInfoViewModel = new ViewModelProvider(this).get(FoodCriteriaLocationInfoViewModel.class);

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
				locationViewModel.hasDetailLocation(EVENT_ID, new DbQueryCallback<Boolean>() {
					@Override
					public void onResultSuccessful(Boolean hasDetailLocation) {
						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (hasDetailLocation) {
									locationViewModel.getLocation(EVENT_ID, new DbQueryCallback<LocationDTO>() {
										@Override
										public void onResultSuccessful(LocationDTO locationResultDto) {
											if (!locationResultDto.isEmpty()) {
												startEditLocationActivity(locationResultDto);
											}
										}

										@Override
										public void onResultNoData() {

										}
									});
								} else {
									showSetLocationDialog();
								}
							}
						});
					}

					@Override
					public void onResultNoData() {

					}
				});

			}
		});

		binding.modifyEventFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				/*
				Intent intent = new Intent(getContext(), ModifyInstanceActivity.class);
				intent.putExtra("requestCode", EventIntentCode.REQUEST_MODIFY_EVENT.value());
				intent.putExtra(CalendarContract.Instances.CALENDAR_ID, CALENDAR_ID);
				intent.putExtra(CalendarContract.Instances.EVENT_ID, EVENT_ID);
				intent.putExtra(CalendarContract.Instances._ID, INSTANCE_ID);
				intent.putExtra(CalendarContract.Instances.BEGIN, instanceValues.getAsLong(CalendarContract.Instances.BEGIN));
				intent.putExtra(CalendarContract.Instances.END, instanceValues.getAsLong(CalendarContract.Instances.END));
				intent.putExtra(CalendarContract.Instances.RRULE, instanceValues.getAsString(CalendarContract.Instances.RRULE));

				editInstanceActivityResultLauncher.launch(intent);
				 */
				Toast.makeText(getContext(), "Working", Toast.LENGTH_SHORT).show();
			}
		});

		binding.removeEventFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String[] items = null;
				//이번 일정만 삭제, 향후 모든 일정 삭제, 모든 일정 삭제
                /*
                반복없는 이벤트 인 경우 : 일정 삭제
                반복있는 이벤트 인 경우 : 이번 일정만 삭제, 향후 모든 일정 삭제, 모든 일정 삭제
                 */
				if (instanceValues.getAsString(CalendarContract.Instances.RRULE) != null) {
					items = new String[]{getString(R.string.remove_this_instance), getString(R.string.remove_all_future_instance_including_current_instance)
							, getString(R.string.remove_event)};
				} else {
					items = new String[]{getString(R.string.remove_event)};
				}
				new MaterialAlertDialogBuilder(getActivity()).setTitle(getString(R.string.remove_event))
						.setItems(items, new DialogInterface.OnClickListener() {
							@SneakyThrows
							@Override
							public void onClick(DialogInterface dialogInterface, int index) {
								if (instanceValues.getAsString(CalendarContract.Instances.RRULE) != null) {
									switch (index) {
										case 0:
											// 이번 일정만 삭제
											// 완성
											showExceptThisInstanceDialog();
											break;
										case 1:
											// 향후 모든 일정만 삭제
											deleteSubsequentIncludingThis();
											break;
										case 2:
											// 모든 일정 삭제
											showDeleteEventDialog();
											break;
									}
								} else {
									switch (index) {
										case 0:
											// 모든 일정 삭제
											showDeleteEventDialog();
											break;
									}
								}
							}
						}).create().show();
			}
		});

		calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
		setInstanceData();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		setLocationActivityResultLauncher.unregister();
		editLocationActivityResultLauncher.unregister();
		editInstanceActivityResultLauncher.unregister();
	}

	private void showDeleteEventDialog() {
		new MaterialAlertDialogBuilder(requireActivity())
				.setTitle(R.string.remove_event)
				.setPositiveButton(R.string.check, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						CalendarInstanceUtil.deleteEvent(calendarViewModel, locationViewModel, foodCriteriaLocationInfoViewModel, foodCriteriaLocationHistoryViewModel,
								CALENDAR_ID, EVENT_ID);
						resultCode = EventIntentCode.RESULT_DELETED.value();
						dialog.dismiss();
						dismiss();
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
	}


	private void deleteSubsequentIncludingThis() {
        /*
        // 이벤트의 반복 UNTIL을 현재 인스턴스의 시작날짜로 수정
        ContentValues recurrenceData = viewModel.getRecurrence(calendarId, eventId);
        RecurrenceRule recurrenceRule = new RecurrenceRule();
        recurrenceRule.separateValues(recurrenceData.getAsString(CalendarContract.Events.RRULE));

        GregorianCalendar calendar = new GregorianCalendar();
        final long thisInstanceBegin = instance.getAsLong(CalendarContract.Instances.BEGIN);
        calendar.setTimeInMillis(thisInstanceBegin);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        recurrenceRule.putValue(RecurrenceRule.UNTIL, ClockUtil.yyyyMMdd.format(calendar.getTime()));
        recurrenceRule.removeValue(RecurrenceRule.INTERVAL);

        recurrenceData.put(CalendarContract.Events.RRULE, recurrenceRule.getRule());
        viewModel.updateEvent(recurrenceData);

         */
		Toast.makeText(getContext(), "작성 중", Toast.LENGTH_SHORT).show();
	}

	private void showExceptThisInstanceDialog() {
		new MaterialAlertDialogBuilder(requireActivity())
				.setTitle(R.string.remove_this_instance)
				.setPositiveButton(R.string.check, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						CalendarInstanceUtil.exceptThisInstance(calendarViewModel, instanceValues.getAsLong(CalendarContract.Instances.BEGIN), EVENT_ID);
						resultCode = EventIntentCode.RESULT_EXCEPTED_INSTANCE.value();
						dialog.dismiss();
						dismiss();
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
	}

	private void setAttendeesText(List<ContentValues> attendees) {
		// 참석자 수, 참석 여부
		LayoutInflater layoutInflater = getLayoutInflater();
		if (binding.eventAttendeesView.eventAttendeesTable.getChildCount() > 0) {
			binding.eventAttendeesView.eventAttendeesTable.removeAllViews();
		}

		for (ContentValues attendee : attendees) {
			TableRow tableRow = new TableRow(getContext());
			View row = layoutInflater.inflate(R.layout.event_attendee_item, null);
			// add row to table
			// 이름, 메일 주소, 상태
			// 조직자 - attendeeName, 그 외 - email
			final String attendeeName = attendee.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL);
			final int attendeeStatus = attendee.getAsInteger(CalendarContract.Attendees.ATTENDEE_STATUS);
			final int attendeeRelationship = attendee.getAsInteger(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP);

			final String attendeeStatusStr = EventUtil.convertAttendeeStatus(attendeeStatus, getContext());
			final String attendeeRelationshipStr = EventUtil.convertAttendeeRelationship(attendeeRelationship, getContext());

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
					// logic for communications with attendee
					if (attendeeDialog == null) {
						final String[] itemList = {"기능 구성중"};
						MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext())
								.setTitle(attendeeName + "(" + attendeeRelationshipStr + ", " + attendeeStatusStr + ")")
								.setItems(itemList, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialogInterface, int i) {

									}
								});
						attendeeDialog = builder.create();
					}
					attendeeDialog.setTitle(attendeeName + "(" + attendeeRelationshipStr + ", " + attendeeStatusStr + ")");
					attendeeDialog.show();
					// 기능목록 파악중
				}
			});

			attendeeEmailView.setText(attendeeName);
			attendeeRelationshipView.setText(attendeeRelationshipStr);
			attendeeStatusView.setText(attendeeStatusStr);

			tableRow.addView(row);
			binding.eventAttendeesView.eventAttendeesTable.addView(tableRow,
					new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		}

	}


	private void setInstanceData() {
		// 제목, 캘린더, 시간, 시간대, 반복, 알림, 설명, 위치, 공개범위, 유효성, 참석자
		// 캘린더, 시간대, 참석자 정보는 따로 불러온다.
		//제목
		instanceValues = calendarViewModel.getInstance(INSTANCE_ID, ORIGINAL_BEGIN, ORIGINAL_END);

		if (instanceValues.getAsInteger(CalendarContract.Instances.CALENDAR_ACCESS_LEVEL) == CalendarContract.Instances.CAL_ACCESS_READ) {
			binding.fabsLayout.setVisibility(View.GONE);
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

		// 시간대
		boolean isAllDay = instanceValues.getAsInteger(CalendarContract.Instances.ALL_DAY) == 1;
		if (isAllDay) {
			// allday이면 시간대 뷰를 숨긴다
			binding.eventDatetimeView.eventTimezoneLayout.setVisibility(View.GONE);
			binding.eventDatetimeView.startTime.setVisibility(View.GONE);
			binding.eventDatetimeView.endTime.setVisibility(View.GONE);

			binding.eventDatetimeView.eventTimezoneLayout.setVisibility(View.GONE);
			int startDay = instanceValues.getAsInteger(CalendarContract.Instances.START_DAY);
			int endDay = instanceValues.getAsInteger(CalendarContract.Instances.END_DAY);
			int dayDifference = endDay - startDay;

			if (startDay == endDay) {
				binding.eventDatetimeView.eventStartdatetimeLabel.setText(R.string.date);
				binding.eventDatetimeView.enddatetimeLayout.setVisibility(View.GONE);

				binding.eventDatetimeView.startDate.setText(EventUtil.convertDate(instanceValues.getAsLong(CalendarContract.Instances.BEGIN)));
			} else {
				Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
				calendar.setTimeInMillis(instanceValues.getAsLong(CalendarContract.Instances.BEGIN));
				binding.eventDatetimeView.startDate.setText(EventUtil.convertDate(calendar.getTime().getTime()));
				calendar.add(Calendar.DAY_OF_YEAR, dayDifference);
				binding.eventDatetimeView.endDate.setText(EventUtil.convertDate(calendar.getTime().getTime()));
			}
		} else {
			String timeZoneStr = instanceValues.getAsString(CalendarContract.Instances.EVENT_TIMEZONE);
			TimeZone timeZone = TimeZone.getTimeZone(timeZoneStr);
			setTimeZoneText(timeZone);

			Date beginDate = new Date(instanceValues.getAsLong(CalendarContract.Instances.BEGIN));
			Date endDate = new Date(instanceValues.getAsLong(CalendarContract.Instances.END));

			binding.eventDatetimeView.startDate.setText(EventUtil.convertDate(beginDate.getTime()));
			binding.eventDatetimeView.startTime.setText(EventUtil.convertTime(beginDate.getTime(), App.isPreference_key_using_24_hour_system()));
			binding.eventDatetimeView.endDate.setText(EventUtil.convertDate(endDate.getTime()));
			binding.eventDatetimeView.endTime.setText(EventUtil.convertTime(endDate.getTime(), App.isPreference_key_using_24_hour_system()));
		}

		// 반복
		if (instanceValues.getAsString(CalendarContract.Instances.RRULE) != null) {
			setRecurrenceText(instanceValues.getAsString(CalendarContract.Instances.RRULE));
			binding.eventRecurrenceView.getRoot().setVisibility(View.VISIBLE);
		} else {
			binding.eventRecurrenceView.getRoot().setVisibility(View.GONE);
		}

		// 알람
		if (instanceValues.getAsBoolean(CalendarContract.Instances.HAS_ALARM)) {
			List<ContentValues> reminderList = calendarViewModel.getReminders(EVENT_ID);
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
		attendeeList = calendarViewModel.getAttendees(EVENT_ID);

		// 참석자가 없는 경우 - 테이블 숨김, 참석자 없음 텍스트 표시
		if (attendeeList.isEmpty()) {
			binding.eventAttendeesView.notAttendees.setVisibility(View.VISIBLE);
			binding.eventAttendeesView.eventAttendeesTable.setVisibility(View.GONE);
			binding.eventAttendeesView.getRoot().setVisibility(View.GONE);
		} else {
			binding.eventAttendeesView.notAttendees.setVisibility(View.GONE);
			binding.eventAttendeesView.eventAttendeesTable.setVisibility(View.VISIBLE);
			binding.eventAttendeesView.getRoot().setVisibility(View.VISIBLE);
			setAttendeesText(attendeeList);
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

	private void setTimeZoneText(TimeZone timeZone) {
		binding.eventDatetimeView.eventTimezone.setText(timeZone.getDisplayName(Locale.KOREAN));
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
		RecurrenceRule recurrenceRule = new RecurrenceRule();
		recurrenceRule.separateValues(rRule);
		binding.eventRecurrenceView.eventRecurrence.setText(recurrenceRule.interpret(getContext()));
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

	private final ActivityResultLauncher<Intent> setLocationActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
			new ActivityResultCallback<ActivityResult>() {
				@Override
				public void onActivityResult(ActivityResult result) {

					try {
						resultCode = result.getResultCode();
						switch (LocationIntentCode.enumOf(resultCode)) {
							case RESULT_CODE_SELECTED_LOCATION:
								LocationDTO locationDTO =
										result.getData().getExtras().getParcelable(DetailLocationSelectorKey.SELECTED_LOCATION_DTO_IN_MAP.value());
								saveDetailLocation(locationDTO);
								break;
						}
					} catch (IllegalArgumentException e) {
					}
				}
			});

	private final ActivityResultLauncher<Intent> editLocationActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
			new ActivityResultCallback<ActivityResult>() {
				@Override
				public void onActivityResult(ActivityResult result) {

					try {
						resultCode = result.getResultCode();
						switch (LocationIntentCode.enumOf(result.getResultCode())) {
							case RESULT_CODE_CHANGED_LOCATION:
								LocationDTO locationDTO =
										result.getData().getExtras().getParcelable(DetailLocationSelectorKey.SELECTED_LOCATION_DTO_IN_MAP.value());
								changedDetailLocation(locationDTO);
								break;
							case RESULT_CODE_REMOVED_LOCATION:
								deletedDetailLocation();
								break;
						}
					} catch (IllegalArgumentException e) {
					}


				}
			});

	private final ActivityResultLauncher<Intent> editInstanceActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
			new ActivityResultCallback<ActivityResult>() {
				@Override
				public void onActivityResult(ActivityResult result) {
					try {
						resultCode = result.getResultCode();
						switch (EventIntentCode.enumOf(resultCode)) {
							case RESULT_MODIFIED_THIS_INSTANCE:
							case RESULT_MODIFIED_EVENT:
								//데이터 갱신
								setInstanceData();
								requireActivity().getIntent().putExtras(result.getData());
								break;
						}
					} catch (IllegalArgumentException e) {

					}


				}
			});

	private void saveDetailLocation(LocationDTO locationDTO) {
		// 지정이 완료된 경우 - DB에 등록하고 이벤트 액티비티로 넘어가서 날씨 또는 주변 정보 프래그먼트를 실행한다.
		locationDTO.setEventId(EVENT_ID);

		//선택된 위치를 DB에 등록
		locationViewModel.addLocation(locationDTO, new DbQueryCallback<LocationDTO>() {
			@Override
			public void onResultSuccessful(LocationDTO result) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						iRefreshView.refreshView();
						dismiss();
					}
				});

			}

			@Override
			public void onResultNoData() {

			}
		});
	}

	private void changedDetailLocation(LocationDTO locationDTO) {
		// 지정이 완료된 경우 - DB에 등록하고 이벤트 액티비티로 넘어가서 날씨 또는 주변 정보 프래그먼트를 실행한다.
		// 선택된 위치를 DB에 등록
		locationDTO.setEventId(EVENT_ID);
		locationViewModel.addLocation(locationDTO, new DbQueryCallback<LocationDTO>() {
			@Override
			public void onResultSuccessful(LocationDTO result) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						iRefreshView.refreshView();
						dismiss();

					}
				});
			}

			@Override
			public void onResultNoData() {

			}
		});
	}

	private void deletedDetailLocation() {
		locationViewModel.removeLocation(EVENT_ID, new DbQueryCallback<Boolean>() {
			@Override
			public void onResultSuccessful(Boolean result) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						iRefreshView.refreshView();
						dismiss();
					}
				});

			}

			@Override
			public void onResultNoData() {

			}
		});
	}
}
