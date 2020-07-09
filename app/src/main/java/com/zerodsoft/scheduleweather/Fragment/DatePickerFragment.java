package com.zerodsoft.scheduleweather.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.zerodsoft.scheduleweather.R;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements NumberPicker.OnValueChangeListener
{
    public static final String TAG = "DATE_PICKER_DIALOG";
    private static DatePickerFragment datePickerFragment;
    private Calendar calendar = Calendar.getInstance();

    private NumberPicker yearPicker;
    private NumberPicker monthPicker;
    private NumberPicker dayPicker;
    private NumberPicker meridiemPicker;
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;

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
        View view = inflater.inflate(R.layout.datepicker_layout, container);
        LinearLayout pickersLayout = (LinearLayout) view.findViewById(R.id.pickers_layout);
        yearPicker = (NumberPicker) view.findViewById(R.id.year_picker);
        monthPicker = (NumberPicker) view.findViewById(R.id.month_picker);
        dayPicker = (NumberPicker) view.findViewById(R.id.day_picker);
        meridiemPicker = (NumberPicker) view.findViewById(R.id.meridiem_picker);
        hourPicker = (NumberPicker) view.findViewById(R.id.hour_picker);
        minutePicker = (NumberPicker) view.findViewById(R.id.minute_picker);

        setDatePicker();

        return view;
    }

    private void setDatePicker()
    {
        yearPicker.setMinValue(calendar.get(Calendar.YEAR) - 5);
        yearPicker.setMaxValue(calendar.get(Calendar.YEAR) + 5);
        yearPicker.setValue(calendar.get(Calendar.YEAR));
        yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(calendar.get(Calendar.MONTH) + 1);
        monthPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        dayPicker.setValue(calendar.get(Calendar.DAY_OF_MONTH));
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
    public void onResume()
    {
        Point point = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getRealSize(point);
        int width = point.x;

        // getDialog().getWindow().setLayout(width, height);

        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.width = width;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        getDialog().getWindow().setAttributes(layoutParams);
        calendar.setTimeInMillis(System.currentTimeMillis());

        super.onResume();
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1)
    {
        switch (numberPicker.getId())
        {
            case R.id.hour_picker:
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
                break;
        }
    }
}
