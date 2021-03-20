package com.zerodsoft.scheduleweather.event.event.activity.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.service.carrier.CarrierMessagingService;
import android.util.TypedValue;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.databinding.EventFragmentBinding;
import com.zerodsoft.scheduleweather.event.main.InstanceMainActivity;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.utility.RecurrenceRule;
import com.zerodsoft.scheduleweather.utility.model.ReminderDto;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import lombok.SneakyThrows;

public class EventFragment extends Fragment
{
    public static final String TAG = "EventFragment";
    // 참석자가 있는 경우 참석 여부 표시
    // 알림 값을 클릭하면 알림표시를 하는 시각을 보여준다
    /*
    공휴일인 경우 : 제목, 날짜, 이벤트 색상, 캘린더 정보만 출력
     */
    private EventFragmentBinding binding;
    private ContentValues instance;
    private List<ContentValues> attendeeList;
    private CalendarViewModel viewModel;

    private Integer calendarId;
    private Long instanceId;
    private Long eventId;
    private Long begin;
    private Long end;

    private AlertDialog attendeeDialog;

    private LocationViewModel locationViewModel;

    public EventFragment(Activity activity)
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        calendarId = arguments.getInt("calendarId");
        instanceId = arguments.getLong("instanceId");
        eventId = arguments.getLong("eventId");
        begin = arguments.getLong("begin");
        end = arguments.getLong("end");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = EventFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SneakyThrows
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        binding.eventRemindersView.addReminderButton.setVisibility(View.GONE);
        binding.eventAttendeesView.showAttendeesDetail.setVisibility(View.GONE);
        binding.eventDatetimeView.allDaySwitchLayout.setVisibility(View.GONE);
        binding.eventDatetimeView.startTime.setVisibility(View.GONE);
        binding.eventDatetimeView.endTime.setVisibility(View.GONE);

        binding.eventFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (binding.eventFab.isExpanded())
                {
                    binding.eventFab.setExpanded(false);
                    collapseFabs();
                } else
                {
                    binding.eventFab.setExpanded(true);
                    expandFabs();
                }
            }
        });

        binding.selectDetailLocationFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                locationViewModel.hasDetailLocation(calendarId, eventId, new CarrierMessagingService.ResultCallback<Boolean>()
                {
                    @Override
                    public void onReceiveResult(@NonNull Boolean hasDetailLocation) throws RemoteException
                    {
                        getActivity().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (hasDetailLocation)
                                {
                                    locationViewModel.getLocation(calendarId, eventId, new CarrierMessagingService.ResultCallback<LocationDTO>()
                                    {
                                        @Override
                                        public void onReceiveResult(@NonNull LocationDTO locationDTO) throws RemoteException
                                        {
                                            if (!locationDTO.isEmpty())
                                            {
                                                InstanceMainActivity.startEditLocationActivity(getActivity(), editLocationActivityResultLauncher,
                                                        locationDTO);
                                            }
                                        }
                                    });
                                } else
                                {
                                    InstanceMainActivity.showSetLocationDialog(getActivity(), setLocationActivityResultLauncher, instance);
                                }
                            }
                        });
                    }
                });


            }
        });

        binding.modifyEventFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
          /*
                Intent intent = new Intent(EventActivity.this, EditEventActivity.class);
                intent.putExtra("requestCode", EventDataController.MODIFY_EVENT);
                intent.putExtra("calendarId", calendarId.intValue());
                intent.putExtra("eventId", eventId.longValue());
                editInstanceActivityResultLauncher.launch(intent);
          */
                Toast.makeText(getActivity(), "작성 중", Toast.LENGTH_SHORT).show();
            }
        });

        binding.removeEventFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String[] items = null;
                //이번 일정만 삭제, 향후 모든 일정 삭제, 모든 일정 삭제
                /*
                반복없는 이벤트 인 경우 : 일정 삭제
                반복있는 이벤트 인 경우 : 이번 일정만 삭제, 향후 모든 일정 삭제, 모든 일정 삭제
                 */
                if (instance.getAsString(CalendarContract.Instances.RRULE) != null)
                {
                    items = new String[]{getString(R.string.remove_this_instance), getString(R.string.remove_all_future_instance_including_current_instance)
                            , getString(R.string.remove_event)};
                } else
                {
                    items = new String[]{getString(R.string.remove_event)};
                }
                new MaterialAlertDialogBuilder(getActivity()).setTitle(getString(R.string.remove_event))
                        .setItems(items, new DialogInterface.OnClickListener()
                        {
                            @SneakyThrows
                            @Override
                            public void onClick(DialogInterface dialogInterface, int index)
                            {
                                if (instance.getAsString(CalendarContract.Instances.RRULE) != null)
                                {
                                    switch (index)
                                    {
                                        case 0:
                                            // 이번 일정만 삭제
                                            // 완성
                                            exceptThisInstance();
                                            break;
                                        case 1:
                                            // 향후 모든 일정만 삭제
                                            deleteSubsequentIncludingThis();
                                            break;
                                        case 2:
                                            // 모든 일정 삭제
                                            deleteEvent();
                                            break;
                                    }
                                } else
                                {
                                    switch (index)
                                    {
                                        case 0:
                                            // 모든 일정 삭제
                                            deleteEvent();
                                            break;
                                    }
                                }
                            }
                        }).create().show();
            }
        });

        viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);

        instance = viewModel.getInstance(calendarId, instanceId, begin, end);
        setInstanceData();
    }


    private void deleteEvent()
    {
        // 참석자 - 알림 - 이벤트 순으로 삭제 (외래키 때문)
        // db column error
        viewModel.deleteEvent(calendarId, eventId);
        // 삭제 완료 후 캘린더 화면으로 나가고, 새로고침한다.
        getActivity().setResult(InstanceMainActivity.RESULT_REMOVED_EVENT);
        getActivity().finish();
    }

    private void deleteSubsequentIncludingThis()
    {
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
        Toast.makeText(getActivity(), "작성 중", Toast.LENGTH_SHORT).show();
    }

    private void exceptThisInstance()
    {
        viewModel.deleteInstance(instance.getAsLong(CalendarContract.Instances.BEGIN), eventId);

        getActivity().setResult(InstanceMainActivity.RESULT_EXCEPTED_INSTANCE);
        getActivity().finish();
    }


    private void collapseFabs()
    {
        binding.eventFab.setImageDrawable(getContext().getDrawable(R.drawable.more_icon));

        binding.removeEventFab.animate().translationY(0);
        binding.modifyEventFab.animate().translationY(0);
        binding.selectDetailLocationFab.animate().translationY(0);
    }


    private void expandFabs()
    {
        binding.eventFab.setImageDrawable(getContext().getDrawable(R.drawable.close_icon));

        final float y = binding.eventFab.getTranslationY();
        final float margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, getResources().getDisplayMetrics());
        final float fabHeight = binding.eventFab.getHeight();

        binding.removeEventFab.animate().translationY(y - (fabHeight + margin));
        binding.modifyEventFab.animate().translationY(y - (fabHeight + margin) * 2);
        binding.selectDetailLocationFab.animate().translationY(y - (fabHeight + margin) * 3);
    }

    private void setAttendeesText(List<ContentValues> attendees)
    {
        // 참석자 수, 참석 여부
        LayoutInflater layoutInflater = getLayoutInflater();
        if (binding.eventAttendeesView.eventAttendeesTable.getChildCount() > 0)
        {
            binding.eventAttendeesView.eventAttendeesTable.removeAllViews();
        }

        for (ContentValues attendee : attendees)
        {
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
            attendeeInfoLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    // logic for communications with attendee
                    if (attendeeDialog == null)
                    {
                        final String[] itemList = {"기능 구성중"};
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext())
                                .setTitle(attendeeName + "(" + attendeeRelationshipStr + ", " + attendeeStatusStr + ")")
                                .setItems(itemList, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {

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


    private void setInstanceData()
    {
        // 제목, 캘린더, 시간, 시간대, 반복, 알림, 설명, 위치, 공개범위, 유효성, 참석자
        // 캘린더, 시간대, 참석자 정보는 따로 불러온다.
        //제목
        if (instance.getAsString(CalendarContract.Instances.TITLE) != null)
        {
            if (!instance.getAsString(CalendarContract.Instances.TITLE).isEmpty())
            {
                binding.eventTitle.setText(instance.getAsString(CalendarContract.Instances.TITLE));
            } else
            {
                binding.eventTitle.setText(getString(R.string.empty_title));
            }
        } else
        {
            binding.eventTitle.setText(getString(R.string.empty_title));
        }
        //캘린더
        setCalendarText();

        //시간 , allday구분
        setDateTimeText(instance.getAsLong(CalendarContract.Instances.BEGIN), instance.getAsLong(CalendarContract.Instances.END));

        // 시간대
        if (!instance.getAsBoolean(CalendarContract.Instances.ALL_DAY))
        {
            String timeZoneStr = instance.getAsString(CalendarContract.Instances.EVENT_TIMEZONE);
            TimeZone timeZone = TimeZone.getTimeZone(timeZoneStr);
            setTimeZoneText(timeZone);
            binding.eventDatetimeView.eventTimezoneLayout.setVisibility(View.VISIBLE);
        } else
        {
            // allday이면 시간대 뷰를 숨긴다
            binding.eventDatetimeView.eventTimezoneLayout.setVisibility(View.GONE);
        }

        // 반복
        if (instance.getAsString(CalendarContract.Instances.RRULE) != null)
        {
            setRecurrenceText(instance.getAsString(CalendarContract.Instances.RRULE));
            binding.eventRecurrenceView.getRoot().setVisibility(View.VISIBLE);
        } else
        {
            binding.eventRecurrenceView.getRoot().setVisibility(View.GONE);
        }

        // 알람
        if (instance.getAsBoolean(CalendarContract.Instances.HAS_ALARM))
        {
            List<ContentValues> reminderList = viewModel.getReminders(calendarId, eventId);
            setReminderText(reminderList);
            binding.eventRemindersView.notReminder.setVisibility(View.GONE);
            binding.eventRemindersView.remindersTable.setVisibility(View.VISIBLE);
            binding.eventRemindersView.getRoot().setVisibility(View.VISIBLE);
        } else
        {
            // 알람이 없으면 알람 테이블을 숨기고, 알람 없음 텍스트를 표시한다.
            binding.eventRemindersView.notReminder.setVisibility(View.VISIBLE);
            binding.eventRemindersView.remindersTable.setVisibility(View.GONE);
            binding.eventRemindersView.getRoot().setVisibility(View.GONE);
        }

        // 설명
        binding.eventDescriptionView.descriptionEdittext.setVisibility(View.GONE);
        if (instance.getAsString(CalendarContract.Instances.DESCRIPTION) != null)
        {
            if (!instance.getAsString(CalendarContract.Instances.DESCRIPTION).isEmpty())
            {
                binding.eventDescriptionView.descriptionTextview.setText(instance.getAsString(CalendarContract.Instances.DESCRIPTION));
                binding.eventDescriptionView.getRoot().setVisibility(View.VISIBLE);
            } else
            {
                binding.eventDescriptionView.getRoot().setVisibility(View.GONE);
            }
        } else
        {
            binding.eventDescriptionView.getRoot().setVisibility(View.GONE);
        }

        // 위치
        if (instance.getAsString(CalendarContract.Instances.EVENT_LOCATION) != null)
        {
            if (!instance.getAsString(CalendarContract.Instances.EVENT_LOCATION).isEmpty())
            {
                binding.eventLocationView.eventLocation.setText(instance.getAsString(CalendarContract.Instances.EVENT_LOCATION));
                binding.eventLocationView.getRoot().setVisibility(View.VISIBLE);
                binding.selectDetailLocationFab.setVisibility(View.VISIBLE);
            } else
            {
                binding.eventLocationView.getRoot().setVisibility(View.GONE);
                binding.selectDetailLocationFab.setVisibility(View.GONE);
            }
        } else
        {
            binding.eventLocationView.getRoot().setVisibility(View.GONE);
            binding.selectDetailLocationFab.setVisibility(View.GONE);
        }

        // 참석자
        attendeeList = viewModel.getAttendees(calendarId, eventId);

        // 참석자가 없는 경우 - 테이블 숨김, 참석자 없음 텍스트 표시
        if (attendeeList.isEmpty())
        {
            binding.eventAttendeesView.notAttendees.setVisibility(View.VISIBLE);
            binding.eventAttendeesView.eventAttendeesTable.setVisibility(View.GONE);
            binding.eventAttendeesView.getRoot().setVisibility(View.GONE);
        } else
        {
            binding.eventAttendeesView.notAttendees.setVisibility(View.GONE);
            binding.eventAttendeesView.eventAttendeesTable.setVisibility(View.VISIBLE);
            binding.eventAttendeesView.getRoot().setVisibility(View.VISIBLE);
            setAttendeesText(attendeeList);
        }

        // 공개 범위 표시
        if (instance.getAsInteger(CalendarContract.Instances.ACCESS_LEVEL) != null)
        {
            setAccessLevelText();
            binding.eventAccessLevelView.getRoot().setVisibility(View.VISIBLE);
        } else
        {
            binding.eventAccessLevelView.getRoot().setVisibility(View.GONE);
        }

        // 유효성 표시
        if (instance.getAsInteger(CalendarContract.Instances.AVAILABILITY) != null)
        {
            setAvailabilityText();
            binding.eventAvailabilityView.getRoot().setVisibility(View.VISIBLE);
        } else
        {
            binding.eventAvailabilityView.getRoot().setVisibility(View.GONE);
        }
    }

    private void setAvailabilityText()
    {
        binding.eventAvailabilityView.eventAvailability.setText(EventUtil.convertAvailability(instance.getAsInteger(CalendarContract.Instances.AVAILABILITY), getContext()));
    }

    private void setAccessLevelText()
    {
        binding.eventAccessLevelView.eventAccessLevel.setText(EventUtil.convertAccessLevel(instance.getAsInteger(CalendarContract.Instances.ACCESS_LEVEL), getContext()));
    }


    private void setDateTimeText(long begin, long end)
    {
        final boolean allDay = instance.getAsBoolean(CalendarContract.Instances.ALL_DAY);
        if (allDay)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(begin);
            calendar.add(Calendar.HOUR_OF_DAY, -9);
            begin = calendar.getTimeInMillis();

            calendar.setTimeInMillis(end);
            calendar.add(Calendar.HOUR_OF_DAY, -9);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            end = calendar.getTimeInMillis();
        }
        String beginStr = EventUtil.convertDateTime(begin, allDay, App.isPreference_key_using_24_hour_system());
        String endStr = EventUtil.convertDateTime(end, allDay, App.isPreference_key_using_24_hour_system());

        binding.eventDatetimeView.startDate.setText(beginStr);
        binding.eventDatetimeView.endDate.setText(endStr);
    }

    private void setTimeZoneText(TimeZone timeZone)
    {
        binding.eventDatetimeView.eventTimezone.setText(timeZone.getDisplayName(Locale.KOREAN));
    }

    private void setReminderText(List<ContentValues> reminders)
    {
        LayoutInflater layoutInflater = getLayoutInflater();
        if (binding.eventRemindersView.remindersTable.getChildCount() > 0)
        {
            binding.eventRemindersView.remindersTable.removeAllViews();
        }

        for (ContentValues reminder : reminders)
        {
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

    private void setRecurrenceText(String rRule)
    {
        RecurrenceRule recurrenceRule = new RecurrenceRule();
        recurrenceRule.separateValues(rRule);
        binding.eventRecurrenceView.eventRecurrence.setText(recurrenceRule.interpret(getContext()));
    }

    private void setCalendarText()
    {
        binding.eventCalendarView.calendarColor.setBackgroundColor(EventUtil.getColor(instance.getAsInteger(CalendarContract.Instances.CALENDAR_COLOR)));
        binding.eventCalendarView.calendarDisplayName.setText(instance.getAsString(CalendarContract.Instances.CALENDAR_DISPLAY_NAME));
        binding.eventCalendarView.calendarAccountName.setText(instance.getAsString(CalendarContract.Instances.OWNER_ACCOUNT));
    }

    public ContentValues getInstance()
    {
        return instance;
    }

    public List<ContentValues> getAttendeeList()
    {
        return attendeeList;
    }

    private final ActivityResultLauncher<Intent> setLocationActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() == InstanceMainActivity.RESULT_SELECTED_LOCATION)
                    {
                        Toast.makeText(getActivity(), result.getData().getStringExtra("selectedLocationName"), Toast.LENGTH_SHORT).show();
                    } else
                    {
                        // 취소, 이벤트 정보 프래그먼트로 돌아감
                    }
                }
            });

    private final ActivityResultLauncher<Intent> editLocationActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {

                }
            });

    private final ActivityResultLauncher<Intent> editInstanceActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    switch (result.getResultCode())
                    {
                        case InstanceMainActivity.RESULT_UPDATED_INSTANCE:
                            //데이터 갱신
                            setInstanceData();
                            break;
                    }
                }
            });
}
