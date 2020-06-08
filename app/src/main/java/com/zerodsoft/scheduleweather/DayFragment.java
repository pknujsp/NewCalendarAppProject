package com.zerodsoft.scheduleweather;

import android.graphics.RectF;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zerodsoft.scheduleweather.CalendarView.WeekDatesView;
import com.zerodsoft.scheduleweather.CalendarView.WeekDayView;
import com.zerodsoft.scheduleweather.CalendarView.WeekHeaderView;
import com.zerodsoft.scheduleweather.Utility.DateTimeInterpreter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class DayFragment extends Fragment
{
    //view
    private WeekDayView mWeekView;
    private WeekHeaderView mWeekHeaderView;
    private WeekDatesView mWeekDatesView;

    public DayFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_day, container, false);
        assignViews(view);
        return view;
    }


    private void assignViews(View view)
    {
        mWeekView = (WeekDayView) view.findViewById(R.id.weekdayview);
        mWeekHeaderView = (WeekHeaderView) view.findViewById(R.id.weekheaderview);
        mWeekDatesView = (WeekDatesView) view.findViewById(R.id.weekdatesview);

        mWeekHeaderView.setOnUpdateWeekDatesListener(mWeekDatesView);
    }


}