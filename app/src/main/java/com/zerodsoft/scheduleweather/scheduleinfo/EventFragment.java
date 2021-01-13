package com.zerodsoft.scheduleweather.scheduleinfo;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.databinding.EventFragmentBinding;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;

public class EventFragment extends Fragment
{
    private EventFragmentBinding binding;
    private CalendarViewModel viewModel;

    public EventFragment()
    {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = EventFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        viewModel.init(getContext());

        Bundle arguments = getArguments();

        int calendarId = arguments.getInt("calendarId");
        int eventId = arguments.getInt("eventId");
        String accountName = arguments.getString("accountName");

        viewModel.getEvent(calendarId, eventId, accountName);
        viewModel.getEventLiveData().observe(getViewLifecycleOwner(), new Observer<DataWrapper<ContentValues>>()
        {
            @Override
            public void onChanged(DataWrapper<ContentValues> contentValuesDataWrapper)
            {
                if (contentValuesDataWrapper.getData() != null)
                {
                    // 값 설정
                }
            }
        });
    }
}
