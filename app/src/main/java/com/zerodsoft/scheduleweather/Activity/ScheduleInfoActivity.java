package com.zerodsoft.scheduleweather.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.zerodsoft.scheduleweather.Activity.MapActivity.MapActivity;
import com.zerodsoft.scheduleweather.Etc.SelectedNotificationTime;
import com.zerodsoft.scheduleweather.Fragment.DatePickerFragment;
import com.zerodsoft.scheduleweather.Fragment.NotificationFragment;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Retrofit.DownloadData;
import com.zerodsoft.scheduleweather.Room.AppDb;
import com.zerodsoft.scheduleweather.Room.DAO.LocationDAO;
import com.zerodsoft.scheduleweather.Room.DAO.ScheduleDAO;
import com.zerodsoft.scheduleweather.Room.DTO.AddressDTO;
import com.zerodsoft.scheduleweather.Room.DTO.PlaceDTO;
import com.zerodsoft.scheduleweather.Room.DTO.ScheduleDTO;
import com.zerodsoft.scheduleweather.Utility.Clock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class ScheduleInfoActivity extends AppCompatActivity implements NotificationFragment.OnNotificationTimeListener
{
    /*
      -수정해야 하는 것
        데이터 수정 클릭 한 뒤 데이터 수정 완료 후 저장할 때 바뀐 데이터가 적용되지 않는 문제
        (추가가 되어버림)
        데이터 수정 클릭 한 뒤 edittext가 작동하지 않는 문제
        하단 버튼의 삭제 버튼 클릭 시 다이얼로그를 띄워서 재 확인을 하도록 해야함

     */
    public static final int ADD_SCHEDULE_REQUEST = 0;
    public static final int SHOW_SCHEDULE_REQUEST = 1;
    public static final int ADD_LOCATION_ACTIVITY = 2;
    public static final int DELETE_SCHEDULE = 3;
    public static final int EDIT_SCHEDULE = 4;

    private Spinner accountSpinner;
    private EditText subjectEditText;
    private Switch allDaySwitch;

    private TextView allDayValueTextView;
    private TextView startDateValueTextView;
    private TextView endDateValueTextView;
    private EditText contentEditText;

    private TextView locationTextView;
    private TextView notiValueTextView;
    private LinearLayout allDayLayout;
    private LinearLayout startDateLayout;
    private LinearLayout endDateLayout;

    private LinearLayout bottomEditButtons;
    private LinearLayout bottomInfoButtons;

    private Button editButton;
    private Button shareButton;
    private Button deleteButton;
    private Button cancelButton;
    private Button saveButton;

    private ScheduleDTO scheduleDTO;
    private PlaceDTO placeDTO;
    private AddressDTO addressDTO;
    private int locType;

    private DatePickerFragment datePickerFragment;

    private Date allDay = new Date();
    private Date startDate = new Date();
    private Date endDate = new Date();

    private int requestCode;

    private SelectedNotificationTime selectedNotificationTime;

    private boolean isAllDay = false;

    private boolean isEdit = false;
    private boolean isNew = false;
    private boolean editedLocation = false;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case ADD_SCHEDULE_REQUEST:
                    getIntent().putExtras(msg.getData());
                    setResult(RESULT_OK, getIntent());
                    finish();
                    break;

                case EDIT_SCHEDULE:
                    bottomInfoButtons.setVisibility(View.VISIBLE);
                    bottomEditButtons.setVisibility(View.GONE);
                    setEnableButtons(false);
                    break;

                case SHOW_SCHEDULE_REQUEST:
                    // 계정, 제목, 날짜, 내용(메모), 위치, 알림
                    accountSpinner.setSelection(scheduleDTO.getCategory());

                    subjectEditText.setText(scheduleDTO.getSubject());

                    if (scheduleDTO.getStartDate().compareTo(scheduleDTO.getEndDate()) == 0)
                    {
                        // all day
                        allDaySwitch.setChecked(true);
                        allDayValueTextView.setText(Clock.dateFormat3.format(scheduleDTO.getStartDate()));
                        allDay = scheduleDTO.getStartDate();
                    } else
                    {
                        allDaySwitch.setChecked(false);
                        startDateValueTextView.setText(Clock.dateFormat2.format(scheduleDTO.getStartDate()));
                        endDateValueTextView.setText(Clock.dateFormat2.format(scheduleDTO.getEndDate()));
                        startDate = scheduleDTO.getStartDate();
                        endDate = scheduleDTO.getEndDate();
                    }

                    if (!scheduleDTO.getContent().isEmpty())
                    {
                        contentEditText.setText(scheduleDTO.getContent());
                    } else
                    {
                        contentEditText.setText(getString(R.string.content_not_inputted));
                    }

                    if (scheduleDTO.getPlaceId() != -1)
                    {
                        locationTextView.setText(placeDTO.getPlaceName());
                    } else if (scheduleDTO.getAddressId() != -1)
                    {
                        locationTextView.setText(addressDTO.getAddressName());
                    } else
                    {
                        locationTextView.setText(getString(R.string.location_not_selected));
                    }

                    selectedNotificationTime = new SelectedNotificationTime();
                    selectedNotificationTime.setMainType(scheduleDTO.getNotiMainType());
                    selectedNotificationTime.setDay(scheduleDTO.getNotiDay());
                    selectedNotificationTime.setHour(scheduleDTO.getNotiHour());
                    selectedNotificationTime.setMinute(scheduleDTO.getNotiMinute());
                    selectedNotificationTime.setResultStr();

                    if (selectedNotificationTime.getMainType() == ScheduleDTO.NOT_NOTI)
                    {
                        notiValueTextView.setText(getString(R.string.notification_type_disable_radio));
                    } else
                    {
                        notiValueTextView.setText(selectedNotificationTime.getResultStr());
                    }

                    break;
                case DELETE_SCHEDULE:
                    setResult(RESULT_OK);
                    finish();
                    break;
            }
        }
    };


    public void clickedOkButton(Date date, DATE_PICKER_CATEGORY datePickerCategory)
    {
        switch (datePickerCategory)
        {
            case START:
                startDate = date;
                startDateValueTextView.setText(Clock.dateFormat2.format(startDate));
                break;

            case END:
                endDate = date;
                endDateValueTextView.setText(Clock.dateFormat2.format(endDate));
                break;

            case ALL_DAY:
                allDay = date;
                allDayValueTextView.setText(Clock.dateFormat3.format(allDay));
                break;
        }
    }

    @Override
    public void onNotiTimeSelected(SelectedNotificationTime selectedNotificationTime)
    {
        this.selectedNotificationTime = selectedNotificationTime;
        if (selectedNotificationTime.getMainType() == ScheduleDTO.NOT_NOTI)
        {
            notiValueTextView.setText("");
            notiValueTextView.setHint(getString(R.string.noti_time_not_selected));
        } else
        {
            notiValueTextView.setText(selectedNotificationTime.getResultStr());
        }
    }

    public enum DATE_PICKER_CATEGORY
    {
        START, END, ALL_DAY
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        accountSpinner = (Spinner) findViewById(R.id.account_spinner);
        subjectEditText = (EditText) findViewById(R.id.subject_edittext);
        allDaySwitch = (Switch) findViewById(R.id.schedule_allday_switch);

        allDayValueTextView = (TextView) findViewById(R.id.allday_value_textview);
        startDateValueTextView = (TextView) findViewById(R.id.startdate_value_textview);
        endDateValueTextView = (TextView) findViewById(R.id.enddate_value_textview);

        contentEditText = (EditText) findViewById(R.id.content_multiline);
        locationTextView = (TextView) findViewById(R.id.location_right_textview);
        notiValueTextView = (TextView) findViewById(R.id.alarm_value_textview);

        allDayLayout = (LinearLayout) findViewById(R.id.allday_layout);
        startDateLayout = (LinearLayout) findViewById(R.id.startdate_layout);
        endDateLayout = (LinearLayout) findViewById(R.id.enddate_layout);

        bottomEditButtons = (LinearLayout) findViewById(R.id.schedule_edit_buttons);
        bottomInfoButtons = (LinearLayout) findViewById(R.id.schedule_info_buttons);

        editButton = (Button) findViewById(R.id.edit_schedule_button);
        shareButton = (Button) findViewById(R.id.share_schedule_button);
        deleteButton = (Button) findViewById(R.id.delete_schedule_button);
        cancelButton = (Button) findViewById(R.id.cancel_edit_schedule_button);
        saveButton = (Button) findViewById(R.id.save_schedule_button);

        allDayLayout.setVisibility(View.GONE);
        startDateLayout.setVisibility(View.VISIBLE);
        endDateLayout.setVisibility(View.VISIBLE);

        setAccountSpinner();
        setAllDaySwitch();
        setDateTextView();
        setAddLocationButton();
        setNotiValue();
        setBottomButtons();

        requestCode = getIntent().getIntExtra("requestCode", 0);
    }


    @Override
    protected void onStart()
    {
        boolean isClickable = true;
        scheduleDTO = null;
        addressDTO = null;
        placeDTO = null;

        switch (requestCode)
        {
            case SHOW_SCHEDULE_REQUEST:
                isClickable = false;
                bottomInfoButtons.setVisibility(View.VISIBLE);
                bottomEditButtons.setVisibility(View.GONE);

                int scheduleId = getIntent().getIntExtra("scheduleId", 0);

                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        AppDb appDb = AppDb.getInstance(ScheduleInfoActivity.this);
                        ScheduleDAO scheduleDAO = appDb.scheduleDAO();
                        LocationDAO locationDAO = appDb.locationDAO();

                        scheduleDTO = scheduleDAO.selectSchedule(scheduleId);

                        if (scheduleDTO.getPlaceId() != -1)
                        {
                            placeDTO = locationDAO.selectPlace(scheduleId);
                        } else if (scheduleDTO.getAddressId() != -1)
                        {
                            addressDTO = locationDAO.selectAddress(scheduleId);
                        }

                        Message msg = handler.obtainMessage();
                        msg.what = SHOW_SCHEDULE_REQUEST;
                        handler.sendMessage(msg);
                    }
                }).start();
                break;

            case ADD_SCHEDULE_REQUEST:
                isClickable = true;
                isNew = true;
                bottomEditButtons.setVisibility(View.VISIBLE);
                bottomInfoButtons.setVisibility(View.GONE);

                break;
        }
        setEnableButtons(isClickable);

        super.onStart();
    }

    private void setEnableButtons(boolean isClickable)
    {
        accountSpinner.setClickable(isClickable);
        accountSpinner.setFocusable(isClickable);
        accountSpinner.setEnabled(isClickable);

        subjectEditText.setClickable(isClickable);
        subjectEditText.setFocusable(isClickable);

        allDaySwitch.setClickable(isClickable);
        allDaySwitch.setFocusable(isClickable);

        allDayValueTextView.setClickable(isClickable);
        allDayValueTextView.setFocusable(isClickable);

        startDateValueTextView.setClickable(isClickable);
        startDateValueTextView.setFocusable(isClickable);

        endDateValueTextView.setClickable(isClickable);
        endDateValueTextView.setFocusable(isClickable);

        contentEditText.setClickable(isClickable);
        contentEditText.setFocusable(isClickable);

        locationTextView.setClickable(isClickable);
        locationTextView.setFocusable(isClickable);

        notiValueTextView.setClickable(isClickable);
        notiValueTextView.setFocusable(isClickable);
    }

    private void setBottomButtons()
    {
        editButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                isEdit = true;
                bottomEditButtons.setVisibility(View.VISIBLE);
                bottomInfoButtons.setVisibility(View.GONE);
                setEnableButtons(true);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        AppDb appDb = AppDb.getInstance(ScheduleInfoActivity.this);
                        ScheduleDAO scheduleDAO = appDb.scheduleDAO();
                        LocationDAO locationDAO = appDb.locationDAO();

                        if (placeDTO != null)
                        {
                            locationDAO.deletePlace(scheduleDTO.getPlaceId());
                        } else if (addressDTO != null)
                        {
                            locationDAO.deleteAddress(scheduleDTO.getAddressId());
                        }
                        scheduleDAO.deleteSchedule(scheduleDTO.getId());

                        handler.sendEmptyMessage(DELETE_SCHEDULE);
                    }
                }).start();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (isNew)
                {
                    scheduleDTO = new ScheduleDTO();

                    if (accountSpinner.getSelectedItemPosition() == 0)
                    {
                        scheduleDTO.setCategory(ScheduleDTO.GOOGLE_CATEGORY);
                    } else
                    {
                        scheduleDTO.setCategory(ScheduleDTO.LOCAL_CATEGORY);
                    }

                    scheduleDTO.setSubject(subjectEditText.getText().toString());
                    scheduleDTO.setContent(contentEditText.getText().toString());

                    if (isAllDay)
                    {
                        scheduleDTO.setStartDate(allDay);
                        scheduleDTO.setEndDate(allDay);
                    } else
                    {
                        scheduleDTO.setStartDate(startDate);
                        scheduleDTO.setEndDate(endDate);
                    }

                    if (selectedNotificationTime == null)
                    {
                        selectedNotificationTime = new SelectedNotificationTime();
                        selectedNotificationTime.setMainType(ScheduleDTO.NOT_NOTI);
                    }

                    if (selectedNotificationTime.getMainType() == ScheduleDTO.NOT_NOTI)
                    {
                        scheduleDTO.setNotiTime(null);
                    } else
                    {
                        scheduleDTO.setNotiTime(selectedNotificationTime.getTime());
                        scheduleDTO.setNotiMainType(selectedNotificationTime.getMainType());
                        scheduleDTO.setNotiDay(selectedNotificationTime.getDay());
                        scheduleDTO.setNotiHour(selectedNotificationTime.getHour());
                        scheduleDTO.setNotiMinute(selectedNotificationTime.getMinute());
                    }

                    Calendar calendar = Calendar.getInstance();

                    scheduleDTO.setInsertedDate(calendar.getTime());
                    scheduleDTO.setUpdatedDate(calendar.getTime());

                    scheduleDTO.setPlaceId(-1);
                    scheduleDTO.setAddressId(-1);

                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            AppDb appDb = AppDb.getInstance(ScheduleInfoActivity.this);
                            ScheduleDAO scheduleDAO = appDb.scheduleDAO();

                            long scheduleId = scheduleDAO.insertNewSchedule(scheduleDTO);

                            if (placeDTO != null)
                            {
                                LocationDAO locationDAO = appDb.locationDAO();

                                placeDTO.setScheduleId((int) scheduleId);
                                long placeId = locationDAO.insertPlace(placeDTO);
                                scheduleDAO.updatePlaceId((int) scheduleId, (int) placeId);
                            } else if (addressDTO != null)
                            {
                                LocationDAO locationDAO = appDb.locationDAO();

                                addressDTO.setScheduleId((int) scheduleId);
                                long addressId = locationDAO.insertAddress(addressDTO);
                                scheduleDAO.updateAddressId((int) scheduleId, (int) addressId);
                            }

                            Message msg = handler.obtainMessage();
                            msg.what = ADD_SCHEDULE_REQUEST;
                            handler.sendMessage(msg);
                        }
                    }).start();
                } else if (isEdit)
                {
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            AppDb appDb = AppDb.getInstance(ScheduleInfoActivity.this);
                            ScheduleDAO scheduleDAO = appDb.scheduleDAO();

                            if (editedLocation)
                            {
                                LocationDAO locationDAO = appDb.locationDAO();
                                scheduleDTO.setAddressId(-1);
                                scheduleDTO.setPlaceId(-1);

                                if (scheduleDTO.getAddressId() != -1)
                                {
                                    locationDAO.deleteAddress(scheduleDTO.getAddressId());
                                } else if (scheduleDTO.getPlaceId() != -1)
                                {
                                    locationDAO.deletePlace(scheduleDTO.getPlaceId());
                                }

                                if (placeDTO != null)
                                {
                                    placeDTO.setScheduleId((int) scheduleDTO.getId());
                                    long placeId = locationDAO.insertPlace(placeDTO);
                                    scheduleDTO.setPlaceId((int) placeId);
                                } else if (addressDTO != null)
                                {
                                    addressDTO.setScheduleId((int) scheduleDTO.getId());
                                    long addressId = locationDAO.insertAddress(addressDTO);
                                    scheduleDTO.setAddressId((int) addressId);
                                }
                            }
                            Calendar calendar = Calendar.getInstance();
                            scheduleDTO.setUpdatedDate(calendar.getTime());
                            scheduleDAO.updateSchedule(scheduleDTO);

                            Message msg = handler.obtainMessage();
                            msg.what = EDIT_SCHEDULE;
                            handler.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });
    }

    private void setAccountSpinner()
    {
        List<String> accountList = new ArrayList<>();
        accountList.add("GOOGLE");
        accountList.add("LOCAL");

        SpinnerAdapter adapter = new ArrayAdapter<>(ScheduleInfoActivity.this, android.R.layout.simple_spinner_dropdown_item, accountList);
        accountSpinner.setAdapter(adapter);
    }

    private void setAllDaySwitch()
    {
        allDaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                isAllDay = isChecked;

                if (isAllDay)
                {
                    // 하루 종일
                    allDayLayout.setVisibility(View.VISIBLE);
                    startDateLayout.setVisibility(View.GONE);
                    endDateLayout.setVisibility(View.GONE);
                } else
                {
                    allDayLayout.setVisibility(View.GONE);
                    startDateLayout.setVisibility(View.VISIBLE);
                    endDateLayout.setVisibility(View.VISIBLE);
                }

                startDateValueTextView.setText("시작");
                endDateValueTextView.setText("종료");
                allDayValueTextView.setText("시작/종료");

                startDate = new Date();
                endDate = new Date();
                allDay = new Date();

                datePickerFragment = DatePickerFragment.getInstance();
                datePickerFragment.clearAllDate();
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
                datePickerFragment = DatePickerFragment.getInstance();

                switch (view.getId())
                {
                    case R.id.startdate_value_textview:
                        datePickerFragment.setDatePickerCategory(DATE_PICKER_CATEGORY.START);
                        break;
                    case R.id.enddate_value_textview:
                        datePickerFragment.setDatePickerCategory(DATE_PICKER_CATEGORY.END);
                        break;
                    case R.id.allday_value_textview:
                        datePickerFragment.setDatePickerCategory(DATE_PICKER_CATEGORY.ALL_DAY);
                        break;
                }

                datePickerFragment.show(getSupportFragmentManager(), DatePickerFragment.TAG);
            }
        };

        allDayValueTextView.setOnClickListener(onClickListener);
        startDateValueTextView.setOnClickListener(onClickListener);
        endDateValueTextView.setOnClickListener(onClickListener);
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
                NotificationFragment notificationFragment = NotificationFragment.getInstance();
                if (selectedNotificationTime != null)
                {
                    notificationFragment.setSelectedNotificationTime(selectedNotificationTime);
                }
                notificationFragment.show(getSupportFragmentManager(), NotificationFragment.TAG);
            }
        };

        notiValueTextView.setOnClickListener(onClickListener);
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
                startActivityForResult(intent, ADD_LOCATION_ACTIVITY);
            }
        };
        locationTextView.setOnClickListener(onClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_LOCATION_ACTIVITY)
        {
            if (resultCode == RESULT_OK)
            {
                Bundle bundle = data.getExtras();
                locType = bundle.getInt("type");
                String locName = null;

                placeDTO = null;
                addressDTO = null;
                editedLocation = true;

                switch (locType)
                {
                    case DownloadData.ADDRESS:
                        addressDTO = bundle.getParcelable("addressDTO");
                        locName = addressDTO.getAddressName();
                        break;

                    case DownloadData.PLACE_KEYWORD:
                    case DownloadData.PLACE_CATEGORY:
                        placeDTO = bundle.getParcelable("placeDTO");
                        locName = placeDTO.getPlaceName();
                        break;
                }
                locationTextView.setText(locName);
                locationTextView.setVisibility(View.VISIBLE);

            } else if (resultCode == RESULT_CANCELED)
            {

            }
        }
    }
}