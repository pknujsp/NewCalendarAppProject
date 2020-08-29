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
import androidx.fragment.app.DialogFragment;

import com.zerodsoft.scheduleweather.activity.ScheduleInfoActivity;
import com.zerodsoft.scheduleweather.R;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements NumberPicker.OnValueChangeListener
{
    public static final String TAG = "DATE_PICKER_DIALOG";
    public static final int START = 10;
    public static final int END = 20;
    public static final int ALL_DAY = 30;

    private static DatePickerFragment datePickerFragment;
    private Calendar calendar = Calendar.getInstance();
    private String[] dayList;
    private int dateType;

    private NumberPicker yearPicker;
    private NumberPicker monthPicker;
    private NumberPicker dayPicker;
    private NumberPicker meridiemPicker;
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private Button cancelButton;
    private Button okButton;
    private TextView headerTextView;

    private Calendar date = Calendar.getInstance();
    private Calendar startDate;
    private Calendar endDate;

    private final String[] days = new String[]{" 일", " 월", " 화", " 수", " 목", " 금", " 토"};

    public void setDateType(int type)
    {
        dateType = type;
    }


    public DatePickerFragment()
    {

    }

    public static DatePickerFragment getInstance()
    {
        if (datePickerFragment == null)
        {
            datePickerFragment = new DatePickerFragment();
        }
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
        return inflater.inflate(R.layout.datepicker_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        yearPicker = (NumberPicker) view.findViewById(R.id.year_picker);
        monthPicker = (NumberPicker) view.findViewById(R.id.month_picker);
        dayPicker = (NumberPicker) view.findViewById(R.id.day_picker);
        meridiemPicker = (NumberPicker) view.findViewById(R.id.meridiem_picker);
        hourPicker = (NumberPicker) view.findViewById(R.id.hour_picker);
        minutePicker = (NumberPicker) view.findViewById(R.id.minute_picker);
        headerTextView = (TextView) view.findViewById(R.id.datepicker_header_textview);
        cancelButton = (Button) view.findViewById(R.id.cancel_button);
        okButton = (Button) view.findViewById(R.id.ok_button);

        //프래그먼트 헤더의 제목 설정
        switch (dateType)
        {
            case START:
                headerTextView.setText(getString(R.string.date_picker_category_start));
                break;
            case END:
                headerTextView.setText(getString(R.string.date_picker_category_end));
                break;
            case ALL_DAY:
                headerTextView.setText(getString(R.string.date_picker_category_all_day));

                ((TextView) view.findViewById(R.id.meridiem_textview)).setVisibility(View.GONE);
                ((TextView) view.findViewById(R.id.hour_textview)).setVisibility(View.GONE);
                ((TextView) view.findViewById(R.id.minute_textview)).setVisibility(View.GONE);

                meridiemPicker.setVisibility(View.GONE);
                hourPicker.setVisibility(View.GONE);
                minutePicker.setVisibility(View.GONE);
                break;
        }

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int hourOfDay = 0;

                if (dateType != ALL_DAY)
                {
                    // not all day
                    if (meridiemPicker.getValue() == Calendar.AM)
                    {
                        hourOfDay = hourPicker.getValue();
                        if (hourOfDay == 12)
                        {
                            hourOfDay = 0;
                        }
                    } else
                    {
                        hourOfDay = hourPicker.getValue() + 12;
                        if (hourOfDay == 24)
                        {
                            hourOfDay = 12;
                        }
                    }
                    calendar.set(yearPicker.getValue(), monthPicker.getValue() - 1, dayPicker.getValue() + 1, hourOfDay, minutePicker.getValue());

                    if (dateType == START)
                    {
                        if (calendar.after(endDate))
                        {
                            // ERROR
                            Toast.makeText(getActivity(), getString(R.string.date_picker_date_error), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else if (dateType == END)
                    {
                        if (calendar.before(startDate))
                        {
                            // ERROR
                            Toast.makeText(getActivity(), getString(R.string.date_picker_date_error), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } else
                {
                    // all day
                    calendar.set(yearPicker.getValue(), monthPicker.getValue() - 1, dayPicker.getValue() + 1);
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
        super.onViewCreated(view, savedInstanceState);
    }

    private void setDatePicker()
    {
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);

        yearPicker.setMinValue(calendar.get(Calendar.YEAR) - 5);
        yearPicker.setMaxValue(calendar.get(Calendar.YEAR) + 5);
        yearPicker.setValue(calendar.get(Calendar.YEAR));
        yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        yearPicker.setOnValueChangedListener(this);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(calendar.get(Calendar.MONTH) + 1);
        monthPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        monthPicker.setOnValueChangedListener(this);

        setDayList();

        dayPicker.setMinValue(0);
        dayPicker.setMaxValue(dayList.length - 1);
        dayPicker.setDisplayedValues(dayList);

        calendar.setTimeInMillis(System.currentTimeMillis());
        dayPicker.setValue(calendar.get(Calendar.DAY_OF_MONTH) - 1);
        dayPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        meridiemPicker.setMinValue(0);
        meridiemPicker.setMaxValue(1);
        meridiemPicker.setDisplayedValues(new String[]{"오전", "오후"});
        meridiemPicker.setValue(calendar.get(Calendar.AM_PM));
        meridiemPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        hourPicker.setMinValue(1);
        hourPicker.setMaxValue(12);

        if (calendar.get(Calendar.HOUR) == 0)
        {
            hourPicker.setValue(12);
        } else
        {
            hourPicker.setValue(calendar.get(Calendar.HOUR));
        }
        hourPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        hourPicker.setOnValueChangedListener(this);

        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(calendar.get(Calendar.MINUTE));
        minutePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
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
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), 0, 0);
        setDatePicker();

        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
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
                if (meridiemPicker.getValue() == 0)
                {
                    meridiemPicker.setValue(1);
                } else
                {
                    meridiemPicker.setValue(0);
                }
            }
        }
    }

    private void changeDayPicker()
    {
        int selectedDateIndex = dayPicker.getValue();

        calendar.set(yearPicker.getValue(), monthPicker.getValue() - 1, 1);
        setDayList();

        dayPicker.setMinValue(0);
        dayPicker.setMaxValue(1);

        dayPicker.setDisplayedValues(dayList);
        dayPicker.setMaxValue(dayList.length - 1);


        if (selectedDateIndex > dayPicker.getMaxValue())
        {
            dayPicker.setValue(dayPicker.getMaxValue());
        } else
        {
            dayPicker.setValue(selectedDateIndex);
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
}
