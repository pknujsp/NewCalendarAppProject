package com.zerodsoft.tripweather;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.tripweather.Calendar.CalendarAdapter;
import com.zerodsoft.tripweather.Calendar.SelectedDate;
import com.zerodsoft.tripweather.Utility.Clock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class DatePickerFragment extends DialogFragment implements CalendarAdapter.OnDaySelectedListener
{
    TextView textViewStartDate, textViewEndDate, textViewCurrentDate;
    ImageButton btnBefore, btnNext;
    GridView gridView;
    int selectedYear, selectedMonth;
    SelectedDate startDate = new SelectedDate();
    SelectedDate endDate = new SelectedDate();
    boolean range = false;

    private OnPositiveListener onPositiveListener;

    public interface OnPositiveListener
    {
        void onPositiveSelected(SelectedDate startDate, SelectedDate endDate);
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

        String yearMonth = Clock.getYearMonth();

        selectedYear = Integer.valueOf(yearMonth.substring(0, 4)).intValue();
        selectedMonth = Integer.valueOf(yearMonth.substring(4)).intValue();

        Map<String, ArrayList<Integer>> calendarData = getCalendar();

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

    public Map<String, ArrayList<Integer>> getCalendar()
    {
        ArrayList<Integer> previousMonthArr = new ArrayList<>();
        ArrayList<Integer> thisMonthArr = new ArrayList<>();
        ArrayList<Integer> nextMonthArr = new ArrayList<>();
        Map<String, ArrayList<Integer>> calendar = new HashMap<>();

        Calendar startDay = Calendar.getInstance();
        Calendar endDay = Calendar.getInstance();
        Calendar thisMonth = Calendar.getInstance();
        Calendar thisMonthLastDay = Calendar.getInstance();

        thisMonth.set(selectedYear, selectedMonth - 1, 1);
        thisMonthLastDay.set(selectedYear, selectedMonth - 1, thisMonth.getActualMaximum(Calendar.DATE));

        // 2020년 4월 1일로 설정
        startDay.set(selectedYear, selectedMonth - 1, 1);
        // 2020년 4월 말일(30일)로 설정
        endDay.set(selectedYear, selectedMonth - 1, startDay.getActualMaximum(Calendar.DATE));

        // 1일이 속한 주의 일요일로 날짜를 설정
        startDay.add(Calendar.DATE, -startDay.get(Calendar.DAY_OF_WEEK) + 1);
        // 말일이 속한 주의 토요일로 날짜를 설정
        endDay.add(Calendar.DATE, 7 - endDay.get(Calendar.DAY_OF_WEEK));

        while (startDay.before(thisMonth) && !startDay.equals(thisMonth))
        {
            previousMonthArr.add(startDay.get(Calendar.DATE));
            startDay.add(Calendar.DATE, 1);
        }

        while (thisMonth.before(thisMonthLastDay) || thisMonth.equals(thisMonthLastDay))
        {
            thisMonthArr.add(thisMonth.get(Calendar.DATE));
            thisMonth.add(Calendar.DATE, 1);
        }

        thisMonthLastDay.add(Calendar.DATE, 1);

        while (thisMonthLastDay.before(endDay) || thisMonthLastDay.equals(endDay))
        {
            nextMonthArr.add(thisMonthLastDay.get(Calendar.DATE));
            thisMonthLastDay.add(Calendar.DATE, 1);
        }

        calendar.put("previous_month", previousMonthArr);
        calendar.put("this_month", thisMonthArr);
        calendar.put("next_month", nextMonthArr);

        return calendar;
    }

    public void moveMonth(int amount)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(selectedYear, selectedMonth - 1, 1);

        calendar.add(Calendar.MONTH, amount);

        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH) + 1;
    }

    private void setCurrentDate()
    {
        textViewCurrentDate.setText(Integer.toString(selectedYear) + "/" + Integer.toString(selectedMonth));
    }

    public void setDate(SelectedDate date, int type)
    {
        switch (type)
        {
            case CalendarAdapter.START_DATE:
                textViewStartDate.setText(date.getYear() + "/" + date.getMonth() + "/" + date.getDay());
                break;
            case CalendarAdapter.END_DATE:
                textViewEndDate.setText(date.getYear() + "/" + date.getMonth() + "/" + date.getDay());
                break;
        }
    }

    @Override
    public void onDaySelected(String day)
    {
        SelectedDate date = new SelectedDate().setYear(Integer.toString(selectedYear)).setMonth(Integer.toString(selectedMonth)).setDay(day);

        if (range)
        {
            textViewStartDate.setText(date.getYear() + "/" + date.getMonth() + "/" + date.getDay());
            startDate.setYear(Integer.toString(selectedYear)).setMonth(Integer.toString(selectedMonth)).setDay(day);
            textViewEndDate.setText("");
            range = false;

            return;
        }

        if (textViewStartDate.getText().toString().equals(""))
        {
            textViewStartDate.setText(date.getYear() + "/" + date.getMonth() + "/" + date.getDay());
            startDate.setYear(Integer.toString(selectedYear)).setMonth(Integer.toString(selectedMonth)).setDay(day);
        } else if (textViewEndDate.getText().toString().equals(""))
        {
            if (date.toInt() < startDate.toInt())
            {
                Toast.makeText(getContext(), "마지막 날을 재 선택 해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            textViewEndDate.setText(date.getYear() + "/" + date.getMonth() + "/" + date.getDay());
            endDate.setYear(Integer.toString(selectedYear)).setMonth(Integer.toString(selectedMonth)).setDay(day);
            range = true;
        }


    }
}
