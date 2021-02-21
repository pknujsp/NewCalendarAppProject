package com.zerodsoft.scheduleweather.calendarview.assistantcalendar.monthlistassistant;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;

import java.util.Date;

public class MonthListAssistantCalendarFragment extends Fragment
{
    public static final String TAG = "MonthListAssistantCalendarFragment";
    private final IConnectedCalendars iConnectedCalendars;

    public MonthListAssistantCalendarFragment(Activity activity)
    {
        this.iConnectedCalendars = (IConnectedCalendars) activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setCurrentMonth(Date currentCalendarDate)
    {
    }
}
