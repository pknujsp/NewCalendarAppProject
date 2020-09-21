package com.zerodsoft.scheduleweather.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.MapController;
import com.zerodsoft.scheduleweather.activity.mapactivity.MapActivity;
import com.zerodsoft.scheduleweather.etc.SelectedNotificationTime;
import com.zerodsoft.scheduleweather.fragment.DatePickerFragment;
import com.zerodsoft.scheduleweather.fragment.NotificationFragment;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.utility.Clock;
import com.zerodsoft.scheduleweather.viewmodel.ScheduleViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduleInfoActivity extends AppCompatActivity implements NotificationFragment.OnNotificationTimeListener
{
    /*
       - 수정해야 하는 것
        일정 클릭 후 읽어 왔을때 제목, 하루종일 시간이 안나옴
        데이터 수정 클릭 한 뒤 edittext가 작동하지 않는 문제
        하단 버튼의 삭제 버튼 클릭 시 다이얼로그를 띄워서 재 확인을 하도록 해야함
     */
    public static final int REQUEST_NEW_SCHEDULE = 0;
    public static final int REQUEST_SHOW_SCHEDULE = 10;
    public static final int ADD_LOCATION = 20;
    public static final int DELETED_SCHEDULE = 30;
    public static final int EDITED_SCHEDULE = 40;
    public static final int SHOW_SCHEDULE = 50;
    public static final int EDIT_SCHEDULE = 60;
    public static final int EDIT_LOCATION = 70;

    public static final int RESULT_DELETED = 80;
    public static final int RESULT_SELECTED = 90;
    public static final int RESULT_RESELECTED = 100;

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
                activityBinding.startdateValue.setText(Clock.dateFormat2.format(date));
                break;

            case DatePickerFragment.END:
                activityBinding.getScheduleDto().setEndDate(date);
                activityBinding.enddateValue.setText(Clock.dateFormat2.format(date));
                break;

            case DatePickerFragment.ALL_DAY:
                activityBinding.getScheduleDto().setStartDate(date);
                activityBinding.getScheduleDto().setEndDate(date);
                activityBinding.alldayValue.setText(Clock.dateFormat3.format(date));
                break;
        }

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

        if (scheduleDTO.getNotiMainType() == ScheduleDTO.NOT_SELECTED)
        {
            activityBinding.notificationValue.setText("");
            activityBinding.notificationValue.setHint(getString(R.string.noti_time_not_selected));
        } else
        {
            activityBinding.notificationValue.setText(selectedNotificationTime.getResultStr());
        }
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

        EditTextWatcher editTextWatcher = new EditTextWatcher();
        activityBinding.subject.addTextChangedListener(editTextWatcher);
        activityBinding.subject.setOnFocusChangeListener(editTextWatcher);
        activityBinding.content.addTextChangedListener(editTextWatcher);
        activityBinding.content.setOnFocusChangeListener(editTextWatcher);

        setAccountSpinner();
        setAllDaySwitch();
        setDateTextView();
        setLocationButton();
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
                activityBinding.setNotification(new SelectedNotificationTime());
                activityBinding.getNotification().setResultStr();

                if (!scheduleDTO.isEmpty())
                {
                    // 계정, 제목, 날짜, 내용, 위치 ,알림 내용을 화면에 표시하는 코드

                    //날짜
                    if (scheduleDTO.getDateType() == ScheduleDTO.DATE_ALLDAY)
                    {
                        activityBinding.scheduleAlldaySwitch.setChecked(true);
                    } else
                    {
                        activityBinding.scheduleAlldaySwitch.setChecked(false);
                    }

                    //위치
                    if (viewModel.getPlace() != null)
                    {
                        activityBinding.setPlaceDto(viewModel.getPlace().getValue());
                    } else if (viewModel.getAddress() != null)
                    {
                        activityBinding.setAddressDto(viewModel.getAddress().getValue());
                    }

                    //알림
                    if (scheduleDTO.getNotiMainType() != ScheduleDTO.NOT_SELECTED)
                    {
                        SelectedNotificationTime selectedNotificationTime = new SelectedNotificationTime().setMainType(scheduleDTO.getNotiMainType());

                        switch (scheduleDTO.getNotiMainType())
                        {
                            case ScheduleDTO.NOT_SELECTED:
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

        viewModel.getAddress().observe(this, new Observer<AddressDTO>()
        {
            @Override
            public void onChanged(AddressDTO addressDTO)
            {
                if (addressDTO != null)
                {
                    activityBinding.setAddressDto(addressDTO);
                } else
                {
                    activityBinding.setAddressDto(null);
                }
            }
        });

        viewModel.getPlace().observe(this, new Observer<PlaceDTO>()
        {
            @Override
            public void onChanged(PlaceDTO placeDTO)
            {
                if (placeDTO != null)
                {
                    activityBinding.setPlaceDto(placeDTO);
                } else
                {
                    activityBinding.setPlaceDto(null);
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
                // 제목, 날짜 필수 입력
                if (activityBinding.getScheduleDto().getSubject().isEmpty())
                {
                    Toast.makeText(ScheduleInfoActivity.this, "제목을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (activityBinding.getScheduleDto().getStartDate().getTime() == ScheduleDTO.NOT_SELECTED
                        || activityBinding.getScheduleDto().getEndDate().getTime() == ScheduleDTO.NOT_SELECTED)
                {
                    Toast.makeText(ScheduleInfoActivity.this, "날짜를 지정해주세요", Toast.LENGTH_SHORT).show();
                } else
                {
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

        activityBinding.subject.setEnabled(isClickable);

        activityBinding.scheduleAlldaySwitch.setClickable(isClickable);
        activityBinding.scheduleAlldaySwitch.setFocusable(isClickable);

        activityBinding.alldayValue.setClickable(isClickable);
        activityBinding.alldayValue.setFocusable(isClickable);

        activityBinding.startdateValue.setClickable(isClickable);
        activityBinding.startdateValue.setFocusable(isClickable);

        activityBinding.enddateValue.setClickable(isClickable);
        activityBinding.enddateValue.setFocusable(isClickable);

        activityBinding.content.setEnabled(isClickable);

        activityBinding.location.setClickable(isClickable);
        activityBinding.location.setFocusable(isClickable);

        activityBinding.notificationValue.setClickable(isClickable);
        activityBinding.notificationValue.setFocusable(isClickable);
    }

    private void setAccountSpinner()
    {
        List<String> accountList = new ArrayList<>();
        accountList.add("구글 계정");
        accountList.add("로컬");

        SpinnerAdapter adapter = new ArrayAdapter<>(ScheduleInfoActivity.this, android.R.layout.simple_spinner_dropdown_item, accountList);
        activityBinding.accountSpinner.setAdapter(adapter);
        activityBinding.accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l)
            {
                activityBinding.getScheduleDto().setCategory(index);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });
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
                    activityBinding.getScheduleDto().setDateType(ScheduleDTO.DATE_ALLDAY);
                    activityBinding.alldayLayout.setVisibility(View.VISIBLE);
                    activityBinding.startdateLayout.setVisibility(View.GONE);
                    activityBinding.enddateLayout.setVisibility(View.GONE);
                } else
                {
                    activityBinding.getScheduleDto().setDateType(ScheduleDTO.DATE_NOT_ALLDAY);
                    activityBinding.alldayLayout.setVisibility(View.GONE);
                    activityBinding.startdateLayout.setVisibility(View.VISIBLE);
                    activityBinding.enddateLayout.setVisibility(View.VISIBLE);
                }

                activityBinding.getScheduleDto().getStartDate().setTime(ScheduleDTO.NOT_SELECTED);
                activityBinding.getScheduleDto().getEndDate().setTime(ScheduleDTO.NOT_SELECTED);

                activityBinding.alldayValue.setText("");
                activityBinding.startdateValue.setText("");
                activityBinding.enddateValue.setText("");

                activityBinding.alldayValue.setHint(getString(R.string.date_picker_category_end));
                activityBinding.startdateValue.setHint(getString(R.string.date_picker_category_end));
                activityBinding.enddateValue.setHint(getString(R.string.date_picker_category_end));
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

    private void setLocationButton()
    {
        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //위치를 설정하는 액티비티 표시
                Intent intent = new Intent(ScheduleInfoActivity.this, MapActivity.class);
                int requestCode = 0;

                if (activityBinding.getAddressDto() != null || activityBinding.getPlaceDto() != null)
                {
                    requestCode = EDIT_LOCATION;
                    Bundle bundle = new Bundle();

                    if (activityBinding.getPlaceDto() != null)
                    {
                        bundle.putParcelable("place", activityBinding.getPlaceDto());
                    } else if (activityBinding.getAddressDto() != null)
                    {
                        bundle.putParcelable("address", activityBinding.getAddressDto());
                    }
                    intent.putExtras(bundle);
                } else
                {
                    requestCode = ADD_LOCATION;
                }
                intent.putExtra("requestCode", requestCode);
                startActivityForResult(intent, requestCode);
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

        if (resultCode == RESULT_SELECTED || resultCode == RESULT_RESELECTED)
        {
            Bundle bundle = data.getExtras();

            activityBinding.setPlaceDto(null);
            activityBinding.setAddressDto(null);


            switch (bundle.getInt("dataType"))
            {
                case MapController.TYPE_ADDRESS:
                    activityBinding.setAddressDto(bundle.getParcelable("address"));
                    break;

                case MapController.TYPE_PLACE_KEYWORD:
                case MapController.TYPE_PLACE_CATEGORY:
                    activityBinding.setPlaceDto(bundle.getParcelable("place"));
                    break;
            }
        } else if (resultCode == RESULT_DELETED)
        {
            activityBinding.setPlaceDto(null);
            activityBinding.setAddressDto(null);
        } else if (requestCode == RESULT_CANCELED)
        {

        }
    }

    class EditTextWatcher implements TextWatcher, View.OnFocusChangeListener
    {
        int focusedViewId = View.NO_ID;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {
            // 텍스트가 변경될 때 마다 수행

        }

        @Override
        public void afterTextChanged(Editable editable)
        {
            // 텍스트가 변경된 후 수행
            switch (focusedViewId)
            {
                case R.id.subject:
                    activityBinding.getScheduleDto().setSubject(editable.toString());
                    break;
                case R.id.content:
                    activityBinding.getScheduleDto().setContent(editable.toString());
                    break;
            }
        }

        @Override
        public void onFocusChange(View view, boolean b)
        {
            if (b)
            {
                // focusing
                focusedViewId = view.getId();
            }
        }
    }
}
