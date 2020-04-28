package com.zerodsoft.tripweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import com.zerodsoft.tripweather.Room.DTO.Schedule;
import com.zerodsoft.tripweather.ScheduleList.ScheduleListAdapter;
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
        List<Schedule> fixedScheduleList = new ArrayList<>();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_travel_schedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        for (Schedule schedule : travelData)
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Calendar startDay = Calendar.getInstance();
            Calendar endDay = Calendar.getInstance();

            String[] separatedStartDate = schedule.getStartDate().split("/");
            String[] separatedEndDate = schedule.getEndDate().split("/");

            startDay.set(Integer.parseInt(separatedStartDate[0]), Integer.parseInt(separatedStartDate[1]) - 1, Integer.parseInt(separatedStartDate[2]));
            endDay.set(Integer.parseInt(separatedEndDate[0]), Integer.parseInt(separatedEndDate[1]) - 1, Integer.parseInt(separatedEndDate[2]));

            while (startDay.before(endDay) || startDay.equals(endDay))
            {
                Schedule editedSchedule = new Schedule();

                editedSchedule.setParentId(schedule.getParentId());
                editedSchedule.setAreaName(schedule.getAreaName());
                editedSchedule.setAreaX(schedule.getAreaX());
                editedSchedule.setAreaY(schedule.getAreaY());
                editedSchedule.setDate(simpleDateFormat.format(startDay.getTime()));

                fixedScheduleList.add(editedSchedule);

                startDay.add(Calendar.DATE, 1);
            }
        }

        ScheduleListAdapter adapter = new ScheduleListAdapter(fixedScheduleList);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new ViewItemDecoration(16));
    }
}
