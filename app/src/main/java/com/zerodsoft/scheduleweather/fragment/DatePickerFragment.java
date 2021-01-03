package com.zerodsoft.scheduleweather.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.IEventTime;
import com.zerodsoft.scheduleweather.databinding.DatepickerLayoutBinding;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements NumberPicker.OnValueChangeListener
{
    /*
    Schedule액티비티에서 날짜 선택 - 날짜 유형과 선택된 날짜를 전달 -
    1. 날짜가 선택되어 있지 않은 경우 : 시스템 시각을 화면에 표시
    2. 날짜가 선택된 경우 : 선택된 날짜를 화면에 표시
     */
    public static final String TAG = "DatePickerFragment";
    public static final int START = 10;
    public static final int END = 20;
    public static final int ALL_DAY = 30;

    private static DatePickerFragment instance;
    private DatepickerLayoutBinding binding;

    private final Calendar FIXED_DATETIME;
    private final Calendar MODIFIED_DATETIME;
    private final Calendar SELECTED_DATETIME;

    private String[] dayList;
    private String[] days;

    private int dateType;
    private boolean isAllDay;
    private IEventTime iEventTime;

    public DatePickerFragment(IEventTime iEventTime)
    {
        FIXED_DATETIME = Calendar.getInstance(ClockUtil.TIME_ZONE);
        FIXED_DATETIME.set(Calendar.MINUTE, 0);
        FIXED_DATETIME.set(Calendar.SECOND, 0);

        MODIFIED_DATETIME = (Calendar) FIXED_DATETIME.clone();
        SELECTED_DATETIME = (Calendar) FIXED_DATETIME.clone();
        this.iEventTime = iEventTime;
    }

    public static DatePickerFragment getInstance()
    {
        return instance;
    }

    public static DatePickerFragment newInstance(IEventTime iEventTime)
    {
        instance = new DatePickerFragment(iEventTime);
        return instance;
    }

    public static void close()
    {
        instance = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        days = getResources().getStringArray(R.array.day_list);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = DatepickerLayoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        binding.cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dismiss();
            }
        });

        binding.okButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (dateType == ALL_DAY)
                {
                    // all day
                    MODIFIED_DATETIME.set(binding.yearPicker.getValue(), binding.monthPicker.getValue() - 1, binding.dayPicker.getValue() + 1);
                } else
                {
                    // not all day
                    int hourOfDay = 0;

                    if (binding.meridiemPicker.getValue() == Calendar.AM)
                    {
                        hourOfDay = binding.hourPicker.getValue();
                        if (hourOfDay == 12)
                        {
                            hourOfDay = 0;
                        }
                    } else if (binding.meridiemPicker.getValue() == Calendar.PM)
                    {
                        hourOfDay = binding.hourPicker.getValue() + 12;
                        if (hourOfDay == 24)
                        {
                            hourOfDay = 12;
                        }
                    }
                    MODIFIED_DATETIME.set(binding.yearPicker.getValue(), binding.monthPicker.getValue() - 1, binding.dayPicker.getValue() + 1, hourOfDay, binding.minutePicker.getValue(), 0);
                }

                // 시작/종료 날짜를 비교한다
                // 시작 > 종료인 경우 날짜 설정 불가
                switch (dateType)
                {
                    case START:
                        if (iEventTime.getDateTime(START) > 0)
                        {
                            // null, 시작<종료, 시작>종료, 시작==종료 인 경우로 나뉨
                            if (iEventTime.getDateTime(END) < MODIFIED_DATETIME.getTimeInMillis())
                            {
                                //시작 >= 종료 인 경우
                                // Toast.makeText(getActivity(), getString(R.string.date_picker_date_error), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        break;
                    case END:
                        if (iEventTime.getDateTime(START) > 0)
                        {
                            if (iEventTime.getDateTime(START) > MODIFIED_DATETIME.getTimeInMillis())
                            {
                                //시작 >= 종료 인 경우
                                //  Toast.makeText(getActivity(), getString(R.string.date_picker_date_error), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        break;
                }
                iEventTime.onSelectedTime(MODIFIED_DATETIME.getTimeInMillis(), dateType);
                dismiss();
            }
        });

        setDatePicker();

    }

    private void setDatePicker()
    {
        // year, month, date, meridiem, hour, minute

        // 연도 설정
        // 2005 ~ 2035년 범위로 설정
        binding.yearPicker.setMinValue(getResources().getInteger(R.integer.min_year));
        binding.yearPicker.setMaxValue(getResources().getInteger(R.integer.max_year));
        binding.yearPicker.setValue(FIXED_DATETIME.get(Calendar.YEAR));
        binding.yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        binding.yearPicker.setOnValueChangedListener(this);

        binding.monthPicker.setMinValue(1);
        binding.monthPicker.setMaxValue(12);
        binding.monthPicker.setValue(FIXED_DATETIME.get(Calendar.MONTH) + 1);
        binding.monthPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        binding.monthPicker.setOnValueChangedListener(this);

        setDayList();

        binding.dayPicker.setMinValue(0);
        binding.dayPicker.setMaxValue(dayList.length - 1);
        binding.dayPicker.setDisplayedValues(dayList);
        binding.dayPicker.setValue(FIXED_DATETIME.get(Calendar.DAY_OF_MONTH) - 1);
        binding.dayPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        binding.meridiemPicker.setMinValue(0);
        binding.meridiemPicker.setMaxValue(1);
        binding.meridiemPicker.setDisplayedValues(new String[]{getString(R.string.am), getString(R.string.pm)});
        binding.meridiemPicker.setValue(FIXED_DATETIME.get(Calendar.AM_PM));
        binding.meridiemPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        binding.hourPicker.setMinValue(1);
        binding.hourPicker.setMaxValue(12);

        if (FIXED_DATETIME.get(Calendar.HOUR) == 0)
        {
            binding.hourPicker.setValue(12);
        } else
        {
            binding.hourPicker.setValue(FIXED_DATETIME.get(Calendar.HOUR));
        }
        binding.hourPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        binding.hourPicker.setOnValueChangedListener(this);

        binding.minutePicker.setMinValue(0);
        binding.minutePicker.setMaxValue(59);
        binding.minutePicker.setValue(0);
        binding.minutePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }


    @Override
    public void onStart()
    {
        super.onStart();
        //프래그먼트 헤더의 제목 설정
        binding.datepickerHeaderTextview.setText(dateType == START ? getString(R.string.startdate) : getString(R.string.enddate));
        setDateTime();
    }

    private void setDateTime()
    {
        // year, month, date, meridiem, hour, minute
        binding.yearPicker.setValue(SELECTED_DATETIME.get(Calendar.YEAR));
        binding.monthPicker.setValue(SELECTED_DATETIME.get(Calendar.MONTH) + 1);
        changeDayPicker();
        binding.dayPicker.setValue(SELECTED_DATETIME.get(Calendar.DAY_OF_MONTH) - 1);
        binding.meridiemPicker.setValue(SELECTED_DATETIME.get(Calendar.AM_PM));

        if (SELECTED_DATETIME.get(Calendar.HOUR) == 0)
        {
            binding.hourPicker.setValue(12);
        } else
        {
            binding.hourPicker.setValue(SELECTED_DATETIME.get(Calendar.HOUR));
        }

        binding.minutePicker.setValue(SELECTED_DATETIME.get(Calendar.MINUTE));


        binding.numberpickerMeridiem.setVisibility(isAllDay ? View.GONE : View.VISIBLE);
        binding.numberpickerHour.setVisibility(isAllDay ? View.GONE : View.VISIBLE);
        binding.numberpickerMinute.setVisibility(isAllDay ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1)
    {
        if (numberPicker.getId() == R.id.year_picker)
        {
            changeDayPicker();
        } else if (numberPicker.getId() == R.id.month_picker)
        {
            changeDayPicker();
        } else if (numberPicker.getId() == R.id.hour_picker)
        {
            if (numberPicker.getValue() == 12)
            {
                if (binding.meridiemPicker.getValue() == 0)
                {
                    binding.meridiemPicker.setValue(1);
                } else
                {
                    binding.meridiemPicker.setValue(0);
                }
            }
        }
    }

    private void changeDayPicker()
    {
        int selectedDateIndex = binding.dayPicker.getValue();

        MODIFIED_DATETIME.set(binding.yearPicker.getValue(), binding.monthPicker.getValue() - 1, 1);
        setDayList();

        binding.dayPicker.setMinValue(0);
        binding.dayPicker.setMaxValue(1);

        binding.dayPicker.setDisplayedValues(dayList);
        binding.dayPicker.setMaxValue(dayList.length - 1);

        if (selectedDateIndex > binding.dayPicker.getMaxValue())
        {
            binding.dayPicker.setValue(binding.dayPicker.getMaxValue());
        } else
        {
            binding.dayPicker.setValue(selectedDateIndex);
        }
    }

    private void setDayList()
    {
        // 해당 월의 마지막 날짜를 구함
        dayList = new String[MODIFIED_DATETIME.getActualMaximum(Calendar.DAY_OF_MONTH)];
        int dayIndex = MODIFIED_DATETIME.get(Calendar.DAY_OF_WEEK) - 1;
        // Calendar.DAY_OF_WEEK : 1 = sunday, 7 = saturday

        for (int i = 1; i <= MODIFIED_DATETIME.getActualMaximum(Calendar.DAY_OF_MONTH); i++)
        {
            dayList[i - 1] = i + " " + days[dayIndex++];

            if (dayIndex == days.length)
            {
                dayIndex = 0;
            }
        }
    }

    public void initData(long date, int dateType, boolean isAllDay)
    {
        SELECTED_DATETIME.setTimeInMillis(date);
        this.isAllDay = isAllDay;
        this.dateType = dateType;
    }
}
