package com.zerodsoft.scheduleweather.calendarview.month.EventsInfoFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.utility.Clock;

import java.util.Date;
import java.util.List;

public class MonthEventsInfoFragment extends Fragment
{
    public static final String TAG = "MonthEventsInfoFragment";

    private EventsInfoViewModel viewModel;

    private Date startDate;
    private Date endDate;
    private EventsInfoRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    public MonthEventsInfoFragment(Date startDate, Date endDate)
    {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EventsInfoViewModel.class);
        viewModel.selectSchedules(startDate, endDate);
        viewModel.getSchedulesMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<ScheduleDTO>>()
        {
            @Override
            public void onChanged(List<ScheduleDTO> schedules)
            {
                adapter.setSchedulesList(schedules);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_month_events_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.events_info_events_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        adapter = new EventsInfoRecyclerViewAdapter(startDate, endDate);
        recyclerView.setAdapter(adapter);
        ((TextView) view.findViewById(R.id.events_info_day)).setText(Clock.dateFormat3.format(startDate));
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }
}