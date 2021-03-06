package com.zerodsoft.scheduleweather.activity.editevent.fragments;

import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.adapter.TimeZoneRecyclerViewAdapter;
import com.zerodsoft.scheduleweather.activity.editevent.interfaces.ITimeZone;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TimeZoneFragment extends Fragment
{
    public TimeZoneRecyclerViewAdapter adapter;
    public RecyclerView recyclerView;
    public EditText searchEditText;
    public ITimeZone iTimeZone;
    public Long startTime;

    public TimeZoneFragment()
    {

    }

    public void setiTimeZone(ITimeZone iTimeZone)
    {
        this.iTimeZone = iTimeZone;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        startTime = System.currentTimeMillis();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.timezone_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.timezone_list);
        searchEditText = (EditText) view.findViewById(R.id.search_timezone);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        final String[] timeZones = TimeZone.getAvailableIDs();
        final List<TimeZone> timeZoneList = new ArrayList<>();

        for (String v : timeZones)
        {
            timeZoneList.add(TimeZone.getTimeZone(v));
        }

        Date startDate = new Date(startTime);

        adapter = new TimeZoneRecyclerViewAdapter(iTimeZone, timeZoneList, startDate);
        recyclerView.setAdapter(adapter);

        searchEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                // 실시간 검색
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });
    }
}
