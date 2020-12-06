package com.zerodsoft.scheduleweather.fragment;

import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.zerodsoft.scheduleweather.activity.editschedule.ScheduleEditActivity;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.DatepickerLayoutBinding;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.Calendar;
import java.util.Date;

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

    private Calendar mainCalendar = Calendar.getInstance(ClockUtil.TIME_ZONE);
    private Calendar date = Calendar.getInstance(ClockUtil.TIME_ZONE);
    private Calendar selectedDate = Calendar.getInstance(ClockUtil.TIME_ZONE);

    private String[] dayList;
    private String[] days;

    private int dateType;


    public void setDateType(int type)
    {
        dateType = type;
    }

    public DatePickerFragment()
    {

    }

    public static DatePickerFragment getInstance()
    {
        if (instance == null)
        {
            instance = new DatePickerFragment();
        }
        return instance;
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
                    date.set(binding.yearPicker.getValue(), binding.monthPicker.getValue() - 1, binding.dayPicker.getValue() + 1, 0, 0, 0);
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
                    date.set(binding.yearPicker.getValue(), binding.monthPicker.getValue() - 1, binding.dayPicker.getValue() + 1, hourOfDay, binding.minutePicker.getValue(), 0);
                }

                // 시작/종료 날짜를 비교한다
                // 시작 > 종료인 경우 날짜 설정 불가
                switch (dateType)
                {
                    case START:
                        if (((ScheduleEditActivity) getActivity()).getDate(END) != null)
                        {
                            // null, 시작<종료, 시작>종료, 시작==종료 인 경우로 나뉨
                            if (((ScheduleEditActivity) getActivity()).getDate(END).getTime() < date.getTimeInMillis())
                            {
                                //시작 >= 종료 인 경우
                                Toast.makeText(getActivity(), getString(R.string.date_picker_date_error), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        break;
                    case END:
                        if (((ScheduleEditActivity) getActivity()).getDate(START) != null)
                        {
                            if (((ScheduleEditActivity) getActivity()).getDate(START).getTime() > date.getTimeInMillis())
                            {
                                //시작 >= 종료 인 경우
                                Toast.makeText(getActivity(), getString(R.string.date_picker_date_error), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        break;
                }
                ((ScheduleEditActivity) getActivity()).onDateSelected(date.getTime(), dateType);
                dismiss();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    private void setDatePicker()
    {
        // year, month, date, meridiem, hour, minute

        // 연도 설정
        // 2010 ~ 2030년 범위로 설정
        binding.yearPicker.setMinValue(2010);
        binding.yearPicker.setMaxValue(2030);
        binding.yearPicker.setValue(mainCalendar.get(Calendar.YEAR));
        binding.yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        binding.yearPicker.setOnValueChangedListener(this);

        binding.monthPicker.setMinValue(1);
        binding.monthPicker.setMaxValue(12);
        binding.monthPicker.setValue(mainCalendar.get(Calendar.MONTH) + 1);
        binding.monthPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        binding.monthPicker.setOnValueChangedListener(this);

        setDayList();

        binding.dayPicker.setMinValue(0);
        binding.dayPicker.setMaxValue(dayList.length - 1);
        binding.dayPicker.setDisplayedValues(dayList);
        binding.dayPicker.setValue(mainCalendar.get(Calendar.DAY_OF_MONTH) - 1);
        binding.dayPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        binding.meridiemPicker.setMinValue(0);
        binding.meridiemPicker.setMaxValue(1);
        binding.meridiemPicker.setDisplayedValues(new String[]{getString(R.string.am), getString(R.string.pm)});
        binding.meridiemPicker.setValue(mainCalendar.get(Calendar.AM_PM));
        binding.meridiemPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        binding.hourPicker.setMinValue(1);
        binding.hourPicker.setMaxValue(12);

        if (mainCalendar.get(Calendar.HOUR) == 0)
        {
            binding.hourPicker.setValue(12);
        } else
        {
            binding.hourPicker.setValue(mainCalendar.get(Calendar.HOUR));
        }
        binding.hourPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        binding.hourPicker.setOnValueChangedListener(this);

        binding.minutePicker.setMinValue(0);
        binding.minutePicker.setMaxValue(59);
        binding.minutePicker.setValue(0);
        binding.minutePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

        private void setSelectedDate ()
        {
            // year, month, date, meridiem, hour, minute
            binding.yearPicker.setValue(selectedDate.get(Calendar.YEAR));
            binding.monthPicker.setValue(selectedDate.get(Calendar.MONTH) + 1);
            changeDayPicker();
            binding.dayPicker.setValue(selectedDate.get(Calendar.DAY_OF_MONTH) - 1);
            binding.meridiemPicker.setValue(selectedDate.get(Calendar.AM_PM));

            if (selectedDate.get(Calendar.HOUR) == 0)
            {
                binding.hourPicker.setValue(12);
            } else
            {
                binding.hourPicker.setValue(selectedDate.get(Calendar.HOUR));
            }

            binding.minutePicker.setValue(selectedDate.get(Calendar.MINUTE));
        }

        @Override
        public void onStart ()
        {
            Point point = new Point();
            getActivity().getWindowManager().getDefaultDisplay().getRealSize(point);

            WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
            layoutParams.width = point.x;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes(layoutParams);

            mainCalendar.setTimeInMillis(System.currentTimeMillis());
            mainCalendar.set(Calendar.MINUTE, 0);
            mainCalendar.set(Calendar.SECOND, 0);
            date.setTimeInMillis(mainCalendar.getTimeInMillis());
            setDatePicker();

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

                    binding.meridiemPicker.setVisibility(View.GONE);
                    binding.hourPicker.setVisibility(View.GONE);
                    binding.minutePicker.setVisibility(View.GONE);
                    break;
            }

            if (selectedDate != null)
            {
                // 선택된 날짜가 있는 경우
                setSelectedDate();
            }

            super.onStart();
        }

        @Override
        public void onResume ()
        {
            super.onResume();
        }

        @Override
        public void onStop ()
        {
            super.onStop();
        }

        @Override
        public void onValueChange (NumberPicker numberPicker,int i, int i1)
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

        private void changeDayPicker ()
        {
            int selectedDateIndex = binding.dayPicker.getValue();

            date.set(binding.yearPicker.getValue(), binding.monthPicker.getValue() - 1, 1);
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

        private void setDayList ()
        {
            // 해당 월의 마지막 날짜를 구함
            dayList = new String[date.getActualMaximum(Calendar.DAY_OF_MONTH)];
            int dayIndex = date.get(Calendar.DAY_OF_WEEK) - 1;
            // Calendar.DAY_OF_WEEK : 1 = sunday, 7 = saturday

            for (int i = 1; i <= date.getActualMaximum(Calendar.DAY_OF_MONTH); i++)
            {
                dayList[i - 1] = i + " " + days[dayIndex++];

                if (dayIndex == days.length)
                {
                    dayIndex = 0;
                }
            }
        }

        public void setSelectedDate (Date date)
        {
            if (date == null)
            {
                selectedDate = null;
            } else
            {
                // 선택된 날짜가 있는 경우
                selectedDate = (Calendar) mainCalendar.clone();
                selectedDate.setTime(date);
            }
        }
    }
