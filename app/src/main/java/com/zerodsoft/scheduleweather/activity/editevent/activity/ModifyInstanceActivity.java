package com.zerodsoft.scheduleweather.activity.editevent.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.value.EventData;
import com.zerodsoft.scheduleweather.common.enums.EventIntentCode;

import java.util.List;

public class ModifyInstanceActivity extends EditEventActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadInitData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void loadInitData() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(R.string.modify_event);

		Intent intent = getIntent();
		final int CALENDAR_ID = intent.getIntExtra(CalendarContract.Instances.CALENDAR_ID, 0);
		final long EVENT_ID = intent.getLongExtra(CalendarContract.Instances.EVENT_ID, 0);
		final long INSTANCE_ID = intent.getLongExtra(CalendarContract.Instances._ID, 0);
		final long BEGIN = intent.getLongExtra(CalendarContract.Instances.BEGIN, 0);
		final long END = intent.getLongExtra(CalendarContract.Instances.END, 0);

		dataController.putEventValue(CalendarContract.Instances._ID, EVENT_ID);

		// 이벤트, 알림을 가져온다
		ContentValues instance = calendarViewModel.getInstance(CALENDAR_ID, INSTANCE_ID, BEGIN, END);
		ContentValues event = calendarViewModel.getEvent(CALENDAR_ID, EVENT_ID);
		List<ContentValues> attendeeList = calendarViewModel.getAttendees(CALENDAR_ID, EVENT_ID);
		// 이벤트, 알림을 가져온다

		dataController.getSavedEventData().getEVENT().putAll(instance);
		// 제목, 캘린더, 시간, 시간대, 반복, 알림, 설명, 위치, 공개범위, 유효성, 참석자
		// 알림, 참석자 정보는 따로 불러온다.

		EventData savedEventData = dataController.getSavedEventData();
		ContentValues savedEvent = savedEventData.getEVENT();

		setDefaultEventColor(event.getAsString(CalendarContract.Events.ACCOUNT_NAME));
		//제목
		binding.titleLayout.title.setText(savedEvent.getAsString(CalendarContract.Instances.TITLE));

		//캘린더 수정 불가
		binding.calendarLayout.getRoot().setVisibility(View.GONE);

		// allday switch
		binding.timeLayout.timeAlldaySwitch.setChecked(savedEvent.getAsBoolean(CalendarContract.Instances.ALL_DAY));

		final long begin = savedEvent.getAsLong(CalendarContract.Instances.BEGIN);
		final long end = savedEvent.getAsLong(CalendarContract.Instances.END);
		dataController.putEventValue(CalendarContract.Instances.BEGIN, begin);
		dataController.putEventValue(CalendarContract.Instances.END, end);

		//시각
		setDateText(DateTimeType.START, begin);
		setDateText(DateTimeType.END, end);
		setTimeText(DateTimeType.START, begin);
		setTimeText(DateTimeType.END, end);

		// 시간대
		setTimeZoneText(savedEvent.getAsString(CalendarContract.Instances.EVENT_TIMEZONE));

		// 반복
		setRecurrenceText(savedEvent.getAsString(CalendarContract.Instances.RRULE));

		// 알림
		if (savedEvent.getAsBoolean(CalendarContract.Instances.HAS_ALARM)) {
			List<ContentValues> reminderList = calendarViewModel.getReminders(CALENDAR_ID, EVENT_ID);
			dataController.getSavedEventData().getREMINDERS().addAll(reminderList);
			dataController.getModifiedEventData().getREMINDERS().addAll(reminderList);
			setReminderText(reminderList);
		}

		// 설명
		binding.descriptionLayout.descriptionEdittext.setText(savedEvent.getAsString(CalendarContract.Instances.DESCRIPTION));

		// 위치
		binding.locationLayout.eventLocation.setText(savedEvent.getAsString(CalendarContract.Instances.EVENT_LOCATION));

		// 접근 범위
		setAccessLevelText(savedEvent.getAsInteger(CalendarContract.Instances.ACCESS_LEVEL));

		// 유효성
		setAvailabilityText(savedEvent.getAsInteger(CalendarContract.Instances.AVAILABILITY));

		if (!attendeeList.isEmpty()) {
			dataController.getSavedEventData().getATTENDEES().addAll(attendeeList);
			dataController.getModifiedEventData().getATTENDEES().addAll(attendeeList);
			setAttendeesText(attendeeList);
		} else {
			// 참석자 버튼 텍스트 수정
			binding.attendeeLayout.showAttendeesDetail.setText(getString(R.string.add_attendee));
		}
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.save_schedule_button:
				if (dataController.getSavedEventData().getEVENT().getAsString(CalendarContract.Events.RRULE) != null) {
					String[] dialogMenus = {
							getString(R.string.save_only_current_event),
							getString(R.string.save_all_future_events_including_current_event),
							getString(R.string.save_all_events)
					};

					new MaterialAlertDialogBuilder(getApplicationContext()).setTitle(R.string.save_event_title)
							.setItems(dialogMenus, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int index) {
									switch (index) {
										case 0:
											//현재 인스턴스만 변경
											//  updateThisInstance();
											break;
										case 1:
											//현재 인스턴스 이후의 모든 인스턴스 변경
											//  updateAfterInstanceIncludingThisInstance();
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
		return super.onOptionsItemSelected(item);
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
}
