package com.zerodsoft.tripweather;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zerodsoft.tripweather.Calendar.SelectedDate;
import com.zerodsoft.tripweather.Room.DTO.Area;
import com.zerodsoft.tripweather.Room.DTO.Schedule;
import com.zerodsoft.tripweather.ScheduleList.AddScheduleAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddScheduleActivity extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private AddScheduleAdapter adapter;
    private FloatingActionButton fabBtn;
    private Toolbar toolbar;
    public static final int ADD_SCHEDULE = 0;
    public static final int EDIT_SCHEDULE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_add_schedule);
        fabBtn = (FloatingActionButton) findViewById(R.id.fab_add_schedule);
        toolbar = (Toolbar) findViewById(R.id.toolBar_add_schedule);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        fabBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), NewScheduleActivity.class);
                intent.setAction("ADD_SCHEDULE");
                startActivityForResult(intent, ADD_SCHEDULE);
            }
        });
        recyclerView.setHasFixedSize(true);

        List<Schedule> travelSchedules = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(AddScheduleActivity.this));
        adapter = new AddScheduleAdapter(travelSchedules, AddScheduleActivity.this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            Schedule travelSchedule;
            SelectedDate startDate;
            SelectedDate endDate;
            Area area;

            switch (requestCode)
            {
                case ADD_SCHEDULE:
                    startDate = (SelectedDate) data.getSerializableExtra("startDate");
                    endDate = (SelectedDate) data.getSerializableExtra("endDate");
                    area = (Area) data.getSerializableExtra("area");

                    travelSchedule = new Schedule();
                    travelSchedule.setStartDateObj(startDate);
                    travelSchedule.setEndDateObj(endDate);
                    travelSchedule.setArea(area);

                    adapter.addItem(travelSchedule);
                    adapter.notifyDataSetChanged();

                    break;

                case EDIT_SCHEDULE:
                    startDate = (SelectedDate) data.getSerializableExtra("startDate");
                    endDate = (SelectedDate) data.getSerializableExtra("endDate");
                    area = (Area) data.getSerializableExtra("area");
                    int position = data.getIntExtra("position", 0);

                    travelSchedule = new Schedule();
                    travelSchedule.setStartDateObj(startDate);
                    travelSchedule.setEndDateObj(endDate);
                    travelSchedule.setArea(area);

                    adapter.replaceItem(travelSchedule, position);
                    adapter.notifyDataSetChanged();

                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_schedule_toolbar_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_check:
                Toast.makeText(getApplicationContext(), "CHECK", Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();

                Bundle bundle = new Bundle();
                bundle.putSerializable("schedules", (Serializable) adapter.getTravelSchedules());
                intent.putExtras(bundle);
                intent.putExtra("travelName", "My travel");

                setResult(RESULT_OK, intent);
                finish();
                return true;
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
