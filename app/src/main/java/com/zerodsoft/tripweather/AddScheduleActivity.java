package com.zerodsoft.tripweather;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zerodsoft.tripweather.ScheduleData.TravelSchedule;
import com.zerodsoft.tripweather.ScheduleList.AddScheduleAdapter;

import java.util.ArrayList;

public class AddScheduleActivity extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private AddScheduleAdapter adapter;
    private FloatingActionButton fabBtn;
    public static final int ADD_SCHEDULE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_add_schedule);
        fabBtn = (FloatingActionButton) findViewById(R.id.fab_add_schedule);

        fabBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), NewScheduleActivity.class);
                startActivityForResult(intent, ADD_SCHEDULE);
            }
        });
        recyclerView.setHasFixedSize(true);

        ArrayList<TravelSchedule> travelSchedules = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(AddScheduleActivity.this));
        adapter = new AddScheduleAdapter(travelSchedules);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case ADD_SCHEDULE:
                    break;
            }
        }
    }
}
