package com.zerodsoft.tripweather;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zerodsoft.tripweather.Calendar.CalendarAdapter;
import com.zerodsoft.tripweather.Utility.Clock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class DatePickerFragment extends DialogFragment implements CalendarAdapter.OnDaySelectedListener
{
    TextView textViewStartDate, textViewEndDate, textViewCurrentDate;
    ImageButton btnBefore, btnNext;
    GridView gridView;
    Calendar currentDate;
    Date startDate;
    Date endDate;
    boolean range = false;

    private OnPositiveListener onPositiveListener;

    public interface OnPositiveListener
    {
        void onPositiveSelected(Date startDate, Date endDate);
    }

    public DatePickerFragment()
    {

    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        onPositiveListener = (OnPositiveListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View dialog = layoutInflater.inflate(R.layout.fragment_date_picker, null);

        gridView = (GridView) dialog.findViewById(R.id.grid_view_date);
        btnBefore = (ImageButton) dialog.findViewById(R.id.date_picker_before);
        btnNext = (ImageButton) dialog.findViewById(R.id.date_picker_next);
        textViewCurrentDate = (TextView) dialog.findViewById(R.id.date_picker_current_date);
        textViewStartDate = (TextView) dialog.findViewById(R.id.date_picker_start_date);
        textViewEndDate = (TextView) dialog.findViewById(R.id.date_picker_end_date);

        currentDate = Calendar.getInstance(Clock.timeZone);

        Map<String, ArrayList<Date>> calendarData = getCalendar();

        CalendarAdapter calendarAdapter = new CalendarAdapter(calendarData, getContext(), dialog, DatePickerFragment.this);
        gridView.setAdapter(calendarAdapter);
        setCurrentDate();

        btnBefore.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                moveMonth(-1);
                gridView.setAdapter(new CalendarAdapter(getCalendar(), getContext(), dialog, DatePickerFragment.this));
                setCurrentDate();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                moveMonth(1);
                gridView.setAdapter(new CalendarAdapter(getCalendar(), getContext(), dialog, DatePickerFragment.this));
                setCurrentDate();
            }
        });

        builder.setView(dialog).setPositiveButton("확인", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if (startDate == null || endDate == null)
                {
                    Toast.makeText(getContext(), "날짜를 선택해주세요", Toast.LENGTH_SHORT).show();
                } else
                {
                    onPositiveListener.onPositiveSelected(startDate, endDate);
                }
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dismiss();
            }
        });

        return builder.create();
    }

    public Map<String, ArrayList<Date>> getCalendar()
    {
        ArrayList<Date> previousMonthArr = new ArrayList<>();
        ArrayList<Date> thisMonthArr = new ArrayList<>();
        ArrayList<Date> nextMonthArr = new ArrayList<>();
        Map<String, ArrayList<Date>> calendarMap = new HashMap<>();

        Calendar startDay = (Calendar) currentDate.clone();
        Calendar endDay = (Calendar) currentDate.clone();
        Calendar thisMonth = (Calendar) currentDate.clone();
        Calendar thisMonthLastDay = (Calendar) currentDate.clone();

        thisMonth.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), 1);
        thisMonthLastDay.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.getActualMaximum(Calendar.DATE));

        // 2020년 4월 1일로 설정
        startDay.set(thisMonth.get(Calendar.YEAR), thisMonth.get(Calendar.MONTH), 1);
        // 2020년 4월 말일(30일)로 설정
        endDay.set(thisMonth.get(Calendar.YEAR), thisMonth.get(Calendar.MONTH), thisMonth.getActualMaximum(Calendar.DATE));

        // 1일이 속한 주의 일요일로 날짜를 설정
        startDay.add(Calendar.DATE, -startDay.get(Calendar.DAY_OF_WEEK) + 1);
        // 말일이 속한 주의 토요일로 날짜를 설정
        endDay.add(Calendar.DATE, 7 - endDay.get(Calendar.DAY_OF_WEEK));

        while (startDay.before(thisMonth))
        {
            previousMonthArr.add(startDay.getTime());
            startDay.add(Calendar.DATE, 1);
        }

        while (!thisMonth.after(thisMonthLastDay))
        {
            thisMonthArr.add(thisMonth.getTime());
            thisMonth.add(Calendar.DATE, 1);
        }

        thisMonthLastDay.add(Calendar.DATE, 1);

        while (thisMonthLastDay.before(endDay) || thisMonthLastDay.equals(endDay))
        {
            nextMonthArr.add(thisMonthLastDay.getTime());
            thisMonthLastDay.add(Calendar.DATE, 1);
        }

        calendarMap.put("previous_month", previousMonthArr);
        calendarMap.put("this_month", thisMonthArr);
        calendarMap.put("next_month", nextMonthArr);

        return calendarMap;
    }

    public void moveMonth(int amount)
    {
        currentDate.add(Calendar.MONTH, amount);
    }

    private void setCurrentDate()
    {
        textViewCurrentDate.setText(Clock.yearMonthFormat.format(currentDate.getTime()));
    }

    public void setDate(Calendar date, int type)
    {
        switch (type)
        {
            case CalendarAdapter.START_DATE:
                textViewStartDate.setText(Clock.dateFormatSlash.format(date.getTime()));
                break;
            case CalendarAdapter.END_DATE:
                textViewEndDate.setText(Clock.dateFormatSlash.format(date.getTime()));
                break;
        }
    }

    @Override
    public void onDaySelected(Date date)
    {
        if (range)
        {
            textViewStartDate.setText(Clock.dateDayNameFormatSlash.format(date.getTime()));
            startDate = date;
            textViewEndDate.setText("");
            range = false;

            return;
        }

        if (textViewStartDate.getText().toString().equals(""))
        {
            textViewStartDate.setText(Clock.dateDayNameFormatSlash.format(date.getTime()));
            startDate = date;
        } else if (textViewEndDate.getText().toString().equals(""))
        {
            if (date.before(startDate))
            {
                Toast.makeText(getContext(), "마지막 날을 재 선택 해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            textViewEndDate.setText(Clock.dateDayNameFormatSlash.format(date.getTime()));
            endDate = date;
            range = true;
        }
    }
}
