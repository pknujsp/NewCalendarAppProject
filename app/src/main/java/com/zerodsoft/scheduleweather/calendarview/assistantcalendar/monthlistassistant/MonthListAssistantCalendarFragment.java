package com.zerodsoft.scheduleweather.calendarview.assistantcalendar.monthlistassistant;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.callback.EventCallback;
import com.zerodsoft.scheduleweather.calendarview.interfaces.CalendarDateOnClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.databinding.FragmentMonthlistAssistantBinding;

import java.util.Date;
import java.util.List;

public class MonthListAssistantCalendarFragment extends Fragment implements IControlEvent
{
    public static final String TAG = "MonthListAssistantCalendarFragment";

    private final IConnectedCalendars iConnectedCalendars;
    private final CalendarDateOnClickListener calendarDateOnClickListener;
    private FragmentMonthlistAssistantBinding binding;
    private MonthListAssistantCalendarAdapter adapter;
    private CalendarViewModel calendarViewModel;

    public MonthListAssistantCalendarFragment(Activity activity)
    {
        this.iConnectedCalendars = (IConnectedCalendars) activity;
        this.calendarDateOnClickListener = (CalendarDateOnClickListener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = FragmentMonthlistAssistantBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        calendarViewModel.init(getContext());

        adapter = new MonthListAssistantCalendarAdapter(this, calendarDateOnClickListener);
        binding.monthList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.monthList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        binding.monthList.setAdapter(adapter);
        binding.monthList.scrollToPosition(adapter.FIRST_POSITION);
    }

    public void setCurrentMonth(Date currentCalendarDate)
    {
    }

    @Override
    public void getInstances(int viewPosition, long begin, long end, EventCallback<List<CalendarInstance>> callback)
    {
        calendarViewModel.getInstanceList(iConnectedCalendars.getConnectedCalendars(), begin, end, callback);
    }


}
