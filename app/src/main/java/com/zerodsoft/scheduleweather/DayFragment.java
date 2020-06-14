package com.zerodsoft.scheduleweather;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zerodsoft.scheduleweather.CalendarView.WeekDatesView;
import com.zerodsoft.scheduleweather.CalendarView.WeekView;
import com.zerodsoft.scheduleweather.CalendarView.WeekHeaderView;


public class DayFragment extends Fragment
{
    //view
    private WeekHeaderView mWeekHeaderView;
    private WeekDatesView mWeekDatesView;
    private LinearLayout headerLayout;
    private WeekView weekView;

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
        headerLayout = (LinearLayout) view.findViewById(R.id.headerview_layout);
        mWeekHeaderView = (WeekHeaderView) view.findViewById(R.id.weekheaderview);
        headerLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mWeekHeaderView.getMHeaderHeight()));
        headerLayout.invalidate();
        mWeekDatesView = (WeekDatesView) view.findViewById(R.id.weekdatesview);
        mWeekHeaderView.setOnUpdateWeekDatesListener(mWeekDatesView);
        weekView = (WeekView) view.findViewById(R.id.weekview);
        weekView.setMoveWeekListener(mWeekHeaderView);
    }


}