package com.zerodsoft.tripweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.zerodsoft.tripweather.Room.DTO.Schedule;
import com.zerodsoft.tripweather.ScheduleList.ScheduleListAdapter;
import com.zerodsoft.tripweather.ScheduleList.ScheduleTable;
import com.zerodsoft.tripweather.ScheduleList.ViewItemDecoration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TravelScheduleActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_schedule);

        List<Schedule> travelData = (List<Schedule>) getIntent().getSerializableExtra("scheduleList");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_travel_schedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        ScheduleTable scheduleTable = new ScheduleTable((ArrayList<Schedule>) travelData);

        ScheduleListAdapter adapter = new ScheduleListAdapter(scheduleTable);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new ViewItemDecoration(16));
    }
}
