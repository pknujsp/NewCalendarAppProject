package com.zerodsoft.tripweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.zerodsoft.tripweather.ScheduleData.TravelData;
import com.zerodsoft.tripweather.ScheduleData.TravelSchedule;
import com.zerodsoft.tripweather.ScheduleList.TravelScheduleListAdapter;
import com.zerodsoft.tripweather.ScheduleList.ViewItemDecoration;

import java.util.ArrayList;

public class TravelScheduleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_schedule);

        ArrayList<TravelData> travelData = new ArrayList<>(10);

        for (int index = 0; index < 10; ++index) {
            travelData.add(new TravelData().setDestination("서울").setDate("2020/04/05"));
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_travel_schedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        TravelScheduleListAdapter adapter = new TravelScheduleListAdapter(travelData);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new ViewItemDecoration(8));
    }
}
