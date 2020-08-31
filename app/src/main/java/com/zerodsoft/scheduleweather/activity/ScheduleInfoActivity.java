package com.zerodsoft.scheduleweather.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.SpinnerAdapter;

import com.zerodsoft.scheduleweather.activity.mapactivity.MapActivity;
import com.zerodsoft.scheduleweather.etc.SelectedNotificationTime;
import com.zerodsoft.scheduleweather.fragment.DatePickerFragment;
import com.zerodsoft.scheduleweather.fragment.NotificationFragment;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.retrofit.DownloadData;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.viewmodel.ScheduleViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduleInfoActivity extends AppCompatActivity implements NotificationFragment.OnNotificationTimeListener
{
    /*
      -수정해야 하는 것
        데이터 수정 클릭 한 뒤 데이터 수정 완료 후 저장할 때 바뀐 데이터가 적용되지 않는 문제
        (추가가 되어버림)
        데이터 수정 클릭 한 뒤 edittext가 작동하지 않는 문제
        하단 버튼의 삭제 버튼 클릭 시 다이얼로그를 띄워서 재 확인을 하도록 해야함

        일정 보기, 수정, 추가, 삭제

     */
    public static final int REQUEST_NEW_SCHEDULE = 0;
    public static final int REQUEST_SHOW_SCHEDULE = 10;
    public static final int ADD_LOCATION = 20;
    public static final int DELETED_SCHEDULE = 30;
    public static final int EDITED_SCHEDULE = 40;
    public static final int SHOW_SCHEDULE = 50;
    public static final int EDIT_SCHEDULE = 60;
    public static final int EDIT_LOCATION = 70;

    private com.zerodsoft.scheduleweather.databinding.ActivityScheduleBinding activityBinding;
    private ScheduleViewModel viewModel;
    private DatePickerFragment datePickerFragment;
    private NotificationFragment notificationFragment;

    public static int scheduleId = 0;

    private int requestCode;
    private int activityState;

    public void onDateSelected(Date date, int dateType)
    {
        switch (dateType)
        {
            case DatePickerFragment.START:
                activityBinding.getScheduleDto().setStartDate(date);
                break;

            case DatePickerFragment.END:
                activityBinding.getScheduleDto().setEndDate(date);
                break;

            case DatePickerFragment.ALL_DAY:
                activityBinding.getScheduleDto().setStartDate(date);
                activityBinding.getScheduleDto().setEndDate(date);
                break;
        }
        activityBinding.setScheduleDto(activityBinding.getScheduleDto());
    }

    @Override
    public void onNotiTimeSelected(SelectedNotificationTime selectedNotificationTime)
    {
        activityBinding.setNotification(selectedNotificationTime);
        ScheduleDTO scheduleDTO = activityBinding.getScheduleDto();

        scheduleDTO.setNotiMainType(selectedNotificationTime.getMainType());
        scheduleDTO.setNotiDay(selectedNotificationTime.getDay());
        scheduleDTO.setNotiHour(selectedNotificationTime.getHour());
        scheduleDTO.setNotiMinute(selectedNotificationTime.getMinute());
        scheduleDTO.setNotiTime(selectedNotificationTime.getTime());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        activityBinding = com.zerodsoft.scheduleweather.databinding.ActivityScheduleBinding.inflate(getLayoutInflater());
        setContentView(activityBinding.getRoot());

        activityBinding.alldayLayout.setVisibility(View.GONE);
        activityBinding.startdateLayout.setVisibility(View.VISIBLE);
        activityBinding.enddateLayout.setVisibility(View.VISIBLE);

        activityBinding.setScheduleDto(null);
        activityBinding.setPlaceDto(null);
        activityBinding.setNotification(null);
        activityBinding.setAddressDto(null);

        setAccountSpinner();
        setAllDaySwitch();
        setDateTextView();
        setAddLocationButton();
        setNotiValue();
        setBottomButtons();

        requestCode = getIntent().getIntExtra("requestCode", 0);

        switch (requestCode)
        {
            case REQUEST_NEW_SCHEDULE:
                activityState = EDIT_SCHEDULE;
                break;
            case REQUEST_SHOW_SCHEDULE:
                activityState = SHOW_SCHEDULE;
                break;
        }
        setViewState();

        scheduleId = getIntent().getIntExtra("scheduleId", -1);
        viewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        viewModel.getSchedule().observe(this, new Observer<ScheduleDTO>()
        {
            @Override
            public void onChanged(ScheduleDTO scheduleDTO)
            {
                activityBinding.setScheduleDto(scheduleDTO);
                activityBinding.setAddressDto(null);
                activityBinding.setPlaceDto(null);
                activityBinding.setNotification(new SelectedNotificationTime());
                activityBinding.getNotification().setResultStr();

                if (!scheduleDTO.isEmpty())
                {
                    // 계정, 제목, 날짜, 내용, 위치 ,알림 내용을 화면에 표시하는 코드

                    //날짜
                    if (scheduleDTO.getStartDate().compareTo(scheduleDTO.getEndDate()) == 0)
                    {
                        activityBinding.scheduleAlldaySwitch.setChecked(true);
                    } else
                    {
                        activityBinding.scheduleAlldaySwitch.setChecked(false);
                    }

                    //위치
                    if (scheduleDTO.getPlace() != -1)
                    {
                        activityBinding.setPlaceDto(viewModel.getPlace().getValue());
                    } else if (scheduleDTO.getAddress() != -1)
                    {
                        activityBinding.setAddressDto(viewModel.getAddress().getValue());
                    }

                    //알림
                    if (scheduleDTO.getNotiMainType() != ScheduleDTO.NOT_NOTI)
                    {
                        SelectedNotificationTime selectedNotificationTime = new SelectedNotificationTime().setMainType(scheduleDTO.getNotiMainType());

                        switch (scheduleDTO.getNotiMainType())
                        {
                            case ScheduleDTO.NOT_NOTI:
                                selectedNotificationTime.setResultStr();
                            case ScheduleDTO.MAIN_DAY:
                                selectedNotificationTime.setDay(scheduleDTO.getNotiDay()).setHour(scheduleDTO.getNotiHour())
                                        .setMinute(scheduleDTO.getNotiMinute()).setResultStr();
                                break;
                            case ScheduleDTO.MAIN_HOUR:
                                selectedNotificationTime.setHour(scheduleDTO.getNotiHour())
                                        .setMinute(scheduleDTO.getNotiMinute()).setResultStr();
                                break;
                            case ScheduleDTO.MAIN_MINUTE:
                                selectedNotificationTime.setMinute(scheduleDTO.getNotiMinute()).setResultStr();
                                break;
                        }
                        activityBinding.setNotification(selectedNotificationTime);
                    }
                }
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }


    private void setBottomButtons()
    {
        activityBinding.editScheduleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                activityState = EDIT_SCHEDULE;
                setViewState();
            }
        });

        activityBinding.shareScheduleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });

        activityBinding.deleteScheduleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                viewModel.deleteSchedule();
            }
        });

        activityBinding.cancelEditScheduleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                switch (requestCode)
                {
                    case REQUEST_SHOW_SCHEDULE:
                        activityState = SHOW_SCHEDULE;
                        setViewState();
                        break;
                    case REQUEST_NEW_SCHEDULE:
                        setResult(RESULT_CANCELED);
                        finish();
                        break;
                }
            }
        });

        activityBinding.saveScheduleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                activityBinding.getScheduleDto().setCategory(ScheduleDTO.LOCAL_CATEGORY);

                ScheduleDTO scheduleDTO = activityBinding.getScheduleDto();
                scheduleDTO.setSubject(activityBinding.subject.getText().toString());
                scheduleDTO.setContent(activityBinding.content.getText().toString());

                viewModel.setScheduleDTO(activityBinding.getScheduleDto());
                viewModel.setAddressDTO(activityBinding.getAddressDto());
                viewModel.setPlaceDTO(activityBinding.getPlaceDto());

                if (requestCode == REQUEST_SHOW_SCHEDULE)
                {
                    viewModel.updateSchedule();
                } else if (requestCode == REQUEST_NEW_SCHEDULE)
                {
                    viewModel.insertSchedule();
                }
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void setViewState()
    {
        // 하단 버튼 상태, 뷰의 수정 여부 설정
        switch (activityState)
        {
            case SHOW_SCHEDULE:
                activityBinding.scheduleInfoButtons.setVisibility(View.VISIBLE);
                activityBinding.scheduleEditButtons.setVisibility(View.GONE);
                setEnableButtons(false);
                break;
            case EDIT_SCHEDULE:
                activityBinding.scheduleInfoButtons.setVisibility(View.GONE);
                activityBinding.scheduleEditButtons.setVisibility(View.VISIBLE);
                setEnableButtons(true);
                break;
        }
    }

    private void setEnableButtons(boolean isClickable)
    {
        activityBinding.accountSpinner.setClickable(isClickable);
        activityBinding.accountSpinner.setFocusable(isClickable);
        activityBinding.accountSpinner.setEnabled(isClickable);

        activityBinding.subject.setClickable(isClickable);
        activityBinding.subject.setFocusable(isClickable);

        activityBinding.scheduleAlldaySwitch.setClickable(isClickable);
        activityBinding.scheduleAlldaySwitch.setFocusable(isClickable);

        activityBinding.alldayValue.setClickable(isClickable);
        activityBinding.alldayValue.setFocusable(isClickable);

        activityBinding.startdateValue.setClickable(isClickable);
        activityBinding.startdateValue.setFocusable(isClickable);

        activityBinding.enddateValue.setClickable(isClickable);
        activityBinding.enddateValue.setFocusable(isClickable);

        activityBinding.content.setClickable(isClickable);
        activityBinding.content.setFocusable(isClickable);

        activityBinding.location.setClickable(isClickable);
        activityBinding.location.setFocusable(isClickable);

        activityBinding.notificationValue.setClickable(isClickable);
        activityBinding.notificationValue.setFocusable(isClickable);
    }

    private void setAccountSpinner()
    {
        List<String> accountList = new ArrayList<>();
        accountList.add("GOOGLE");
        accountList.add("LOCAL");

        SpinnerAdapter adapter = new ArrayAdapter<>(ScheduleInfoActivity.this, android.R.layout.simple_spinner_dropdown_item, accountList);
        activityBinding.accountSpinner.setAdapter(adapter);
    }

    private void setAllDaySwitch()
    {
        activityBinding.scheduleAlldaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if (isChecked)
                {
                    // 하루 종일
                    activityBinding.alldayLayout.setVisibility(View.VISIBLE);
                    activityBinding.startdateLayout.setVisibility(View.GONE);
                    activityBinding.enddateLayout.setVisibility(View.GONE);
                } else
                {
                    activityBinding.alldayLayout.setVisibility(View.GONE);
                    activityBinding.startdateLayout.setVisibility(View.VISIBLE);
                    activityBinding.enddateLayout.setVisibility(View.VISIBLE);
                }

                activityBinding.getScheduleDto().setStartDate(null);
                activityBinding.getScheduleDto().setEndDate(null);
                activityBinding.setScheduleDto(activityBinding.getScheduleDto());
            }
        });
    }

    private void setDateTextView()
    {
        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //날짜 설정 다이얼로그 표시
                //하루종일인 경우 : 연월일, 아닌 경우 : 연월일시분
                if (datePickerFragment == null)
                {
                    datePickerFragment = DatePickerFragment.getInstance();
                }
                switch (view.getId())
                {
                    case R.id.startdate_value:
                        datePickerFragment.setDateType(DatePickerFragment.START);
                        datePickerFragment.setSelectedDate(activityBinding.getScheduleDto().getStartDate());
                        break;
                    case R.id.enddate_value:
                        datePickerFragment.setDateType(DatePickerFragment.END);
                        datePickerFragment.setSelectedDate(activityBinding.getScheduleDto().getEndDate());
                        break;
                    case R.id.allday_value:
                        datePickerFragment.setDateType(DatePickerFragment.ALL_DAY);
                        datePickerFragment.setSelectedDate(activityBinding.getScheduleDto().getStartDate());
                        break;
                }
                datePickerFragment.show(getSupportFragmentManager(), DatePickerFragment.TAG);
            }
        };

        activityBinding.alldayValue.setOnClickListener(onClickListener);
        activityBinding.startdateValue.setOnClickListener(onClickListener);
        activityBinding.enddateValue.setOnClickListener(onClickListener);
    }

    private void setNotiValue()
    {
        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //알람 시각을 설정하는 다이얼로그 표시
                //하루종일 인 경우와 아닌 경우 내용이 다르다
                if (notificationFragment == null)
                {
                    notificationFragment = NotificationFragment.getInstance();
                }
                notificationFragment.setSelectedNotificationTime(activityBinding.getNotification());
                notificationFragment.show(getSupportFragmentManager(), NotificationFragment.TAG);
            }
        };

        activityBinding.notificationValue.setOnClickListener(onClickListener);
    }

    private void setAddLocationButton()
    {
        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //위치를 설정하는 액티비티 표시
                Intent intent = new Intent(ScheduleInfoActivity.this, MapActivity.class);
                int request = 0;

                if (activityBinding.getPlaceDto() != null || activityBinding.getAddressDto() != null)
                {
                    request = EDIT_LOCATION;
                } else
                {
                    request = ADD_LOCATION;
                }
                startActivityForResult(intent, request);
            }
        };
        activityBinding.location.setOnClickListener(onClickListener);
    }

    public Date getDate(int type)
    {
        if (type == DatePickerFragment.START)
        {
            return activityBinding.getScheduleDto().getStartDate();
        } else
        {
            //END
            return activityBinding.getScheduleDto().getEndDate();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            if (requestCode == ADD_LOCATION || requestCode == EDIT_LOCATION)
            {
                Bundle bundle = data.getExtras();

                activityBinding.setPlaceDto(null);
                activityBinding.setAddressDto(null);
                activityBinding.getScheduleDto().setPlace(ScheduleDTO.NOT_LOCATION);
                activityBinding.getScheduleDto().setAddress(ScheduleDTO.NOT_LOCATION);

                switch (bundle.getInt("type"))
                {
                    case DownloadData.ADDRESS:
                        activityBinding.setAddressDto(bundle.getParcelable("addressDTO"));
                        activityBinding.getScheduleDto().setAddress(ScheduleDTO.SELECTED_LOCATION);
                        break;

                    case DownloadData.PLACE_KEYWORD:
                    case DownloadData.PLACE_CATEGORY:
                        activityBinding.setPlaceDto(bundle.getParcelable("placeDTO"));
                        activityBinding.getScheduleDto().setPlace(ScheduleDTO.SELECTED_LOCATION);
                        break;
                }
            }
        } else if (resultCode == RESULT_CANCELED)
        {

        } else
        {
            //delete
            activityBinding.setPlaceDto(null);
            activityBinding.setAddressDto(null);
            activityBinding.getScheduleDto().setPlace(ScheduleDTO.NOT_LOCATION);
            activityBinding.getScheduleDto().setAddress(ScheduleDTO.NOT_LOCATION);
        }
    }
}
