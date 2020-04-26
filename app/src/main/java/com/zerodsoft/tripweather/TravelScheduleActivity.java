package com.zerodsoft.tripweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.zerodsoft.tripweather.ScheduleData.TravelData;
import com.zerodsoft.tripweather.ScheduleData.TravelSchedule;
import com.zerodsoft.tripweather.ScheduleList.ScheduleListAdapter;
import com.zerodsoft.tripweather.ScheduleList.TravelScheduleListAdapter;
import com.zerodsoft.tripweather.ScheduleList.ViewItemDecoration;

import java.util.ArrayList;

public class TravelScheduleActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_schedule);

        ArrayList<TravelSchedule> travelData = (ArrayList<TravelSchedule>) getIntent().getSerializableExtra("scheduleList");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_travel_schedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        ScheduleListAdapter adapter = new ScheduleListAdapter(travelData);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new ViewItemDecoration(8));
    }
}
