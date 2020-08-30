package com.zerodsoft.scheduleweather.fragment;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.zerodsoft.scheduleweather.activity.ScheduleInfoActivity;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.DatepickerLayoutBinding;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment implements NumberPicker.OnValueChangeListener
{
    public static final String TAG = "DATE_PICKER_DIALOG";
    public static final int START = 10;
    public static final int END = 20;
    public static final int ALL_DAY = 30;

    private static DatePickerFragment datePickerFragment = new DatePickerFragment();
    private DatepickerLayoutBinding binding;
    private Calendar calendar = Calendar.getInstance();
    private String[] dayList;
    private int dateType;
    private Calendar selectedDate = Calendar.getInstance();

    private static final String[] days = new String[]{" 일", " 월", " 화", " 수", " 목", " 금", " 토"};

    public void setDateType(int type)
    {
        dateType = type;
    }

    private DatePickerFragment()
    {
    }

    public static DatePickerFragment getInstance()
    {
        return datePickerFragment;
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.datepicker_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
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
                int hourOfDay = 0;

                if (dateType != ALL_DAY)
                {
                    // not all day
                    if (binding.meridiemPicker.getValue() == Calendar.AM)
                    {
                        hourOfDay = binding.hourPicker.getValue();
                        if (hourOfDay == 12)
                        {
                            hourOfDay = 0;
                        }
                    } else
                    {
                        hourOfDay = binding.hourPicker.getValue() + 12;
                        if (hourOfDay == 24)
                        {
                            hourOfDay = 12;
                        }
                    }
                    calendar.set(binding.yearPicker.getValue(), binding.monthPicker.getValue() - 1, binding.dayPicker.getValue() + 1, hourOfDay, binding.minutePicker.getValue());
                } else
                {
                    // all day
                    calendar.set(binding.yearPicker.getValue(), binding.monthPicker.getValue() - 1, binding.dayPicker.getValue() + 1);
                }

                // 시작/종료 날짜를 비교한다
                // 시작 > 종료인 경우 날짜 설정 불가
                switch (dateType)
                {
                    case ALL_DAY:
                        break;
                    case START:
                        if (((ScheduleInfoActivity) getActivity()).getDate(END) != null)
                        {
                            // null, 시작<종료, 시작>종료, 시작==종료 인 경우로 나뉨
                            if (((ScheduleInfoActivity) getActivity()).getDate(END).before(calendar.getTime()))
                            {
                                //시작 > 종료 인 경우
                                Toast.makeText(getActivity(), getString(R.string.date_picker_date_error), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        break;
                    case END:
                        if (((ScheduleInfoActivity) getActivity()).getDate(START) != null)
                        {
                            if (((ScheduleInfoActivity) getActivity()).getDate(START).after(calendar.getTime()))
                            {
                                //시작 > 종료 인 경우
                                Toast.makeText(getActivity(), getString(R.string.date_picker_date_error), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        break;
                }
                ((ScheduleInfoActivity) getActivity()).onDateSelected(calendar.getTime(), dateType);
                dismiss();
            }
        });

        selectedDate.clear(Calendar.YEAR);
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), 0, 0);


        super.onViewCreated(view, savedInstanceState);
    }

    private void setDatePicker()
    {
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);

        binding.yearPicker.setMinValue(calendar.get(Calendar.YEAR) - 5);
        binding.yearPicker.setMaxValue(calendar.get(Calendar.YEAR) + 5);
        binding.yearPicker.setValue(calendar.get(Calendar.YEAR));
        binding.yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        binding.yearPicker.setOnValueChangedListener(this);

        binding.monthPicker.setMinValue(1);
        binding.monthPicker.setMaxValue(12);
        binding.monthPicker.setValue(calendar.get(Calendar.MONTH) + 1);
        binding.monthPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        binding.monthPicker.setOnValueChangedListener(this);

        setDayList();

        binding.dayPicker.setMinValue(0);
        binding.dayPicker.setMaxValue(dayList.length - 1);
        binding.dayPicker.setDisplayedValues(dayList);

        calendar.setTimeInMillis(System.currentTimeMillis());
        binding.dayPicker.setValue(calendar.get(Calendar.DAY_OF_MONTH) - 1);
        binding.dayPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        binding.meridiemPicker.setMinValue(0);
        binding.meridiemPicker.setMaxValue(1);
        binding.meridiemPicker.setDisplayedValues(new String[]{"오전", "오후"});
        binding.meridiemPicker.setValue(calendar.get(Calendar.AM_PM));
        binding.meridiemPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        binding.hourPicker.setMinValue(1);
        binding.hourPicker.setMaxValue(12);

        if (calendar.get(Calendar.HOUR) == 0)
        {
            binding.hourPicker.setValue(12);
        } else
        {
            binding.hourPicker.setValue(calendar.get(Calendar.HOUR));
        }
        binding.hourPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        binding.hourPicker.setOnValueChangedListener(this);

        binding.minutePicker.setMinValue(0);
        binding.minutePicker.setMaxValue(59);
        binding.minutePicker.setValue(calendar.get(Calendar.MINUTE));
        binding.minutePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    @Override
    public void onStart()
    {
        Point point = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getRealSize(point);
        int width = point.x;

        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.width = width;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(layoutParams);

        //프래그먼트 헤더의 제목 설정
        switch (dateType)
        {
            case START:
                binding.setHeaderSubject(getString(R.string.date_picker_category_start));
                break;
            case END:
                binding.setHeaderSubject(getString(R.string.date_picker_category_end));
                break;
            case ALL_DAY:
                binding.setHeaderSubject(getString(R.string.date_picker_category_all_day));

                binding.meridiemTextview.setVisibility(View.GONE);
                binding.hourTextview.setVisibility(View.GONE);
                binding.minuteTextview.setVisibility(View.GONE);

                binding.meridiemTextview.setVisibility(View.GONE);
                binding.hourTextview.setVisibility(View.GONE);
                binding.minuteTextview.setVisibility(View.GONE);
                break;
        }

        if (selectedDate.isSet(Calendar.YEAR))
        {
            setDatePicker();
        }

        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onStop()
    {
        // 선택된 날짜 초기화
        selectedDate.clear(Calendar.YEAR);
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

        calendar.set(binding.yearPicker.getValue(), binding.monthPicker.getValue() - 1, 1);
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
        dayList = new String[calendar.getActualMaximum(Calendar.DAY_OF_MONTH)];
        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        for (int i = 1; i <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++)
        {
            dayList[i - 1] = i + days[dayIndex++];

            if (dayIndex == days.length)
            {
                dayIndex = 0;
            }
        }
    }

    public void setSelectedDate(Date date)
    {
        if (date != null)
        {
            selectedDate.setTime(date);
            calendar.setTime(selectedDate.getTime());
        } else
        {
            selectedDate.clear(Calendar.YEAR);
        }
    }
}
