package com.zerodsoft.tripweather;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zerodsoft.tripweather.Room.AppDb;
import com.zerodsoft.tripweather.Room.DTO.Area;
import com.zerodsoft.tripweather.Room.DTO.Schedule;
import com.zerodsoft.tripweather.Room.DTO.TravelScheduleCountTuple;
import com.zerodsoft.tripweather.ScheduleList.AddScheduleAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddScheduleActivity extends AppCompatActivity implements Runnable
{
    private RecyclerView recyclerView;
    private AddScheduleAdapter adapter;
    private FloatingActionButton fabBtn;
    private Toolbar toolbar;
    EditText editTravelName;
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            String count = Integer.toString((Integer) msg.obj);
            editTravelName.setText(count + "번째 여행");
        }
    };
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
        editTravelName = (EditText) findViewById(R.id.edit_travel_name);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Thread thread = new Thread(AddScheduleActivity.this);
        thread.start();

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
            Date startDate;
            Date endDate;
            Area area;

            switch (requestCode)
            {
                case ADD_SCHEDULE:
                    startDate = (Date) data.getSerializableExtra("startDate");
                    endDate = (Date) data.getSerializableExtra("endDate");
                    area = (Area) data.getSerializableExtra("area");

                    travelSchedule = new Schedule();
                    travelSchedule.setStartDateObj(startDate);
                    travelSchedule.setEndDateObj(endDate);
                    travelSchedule.setArea(area);

                    adapter.addItem(travelSchedule);
                    adapter.notifyDataSetChanged();

                    break;

                case EDIT_SCHEDULE:
                    startDate = (Date) data.getSerializableExtra("startDate");
                    endDate = (Date) data.getSerializableExtra("endDate");
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
                if (!adapter.getTravelSchedules().isEmpty())
                {
                    if (editTravelName.getText().toString().isEmpty())
                    {
                        Toast.makeText(getApplicationContext(), "여행 이름을 입력하세요", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    Intent intent = getIntent();

                    Bundle bundle = new Bundle();
                    bundle.putString("travelName", editTravelName.getText().toString());
                    bundle.putSerializable("schedules", (Serializable) adapter.getTravelSchedules());
                    intent.putExtras(bundle);

                    setResult(RESULT_OK, intent);
                    finish();
                } else
                {
                    Toast.makeText(getApplicationContext(), "일정을 추가하십시오", Toast.LENGTH_SHORT).show();
                }
                return true;
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void run()
    {
        AppDb appDb = AppDb.getInstance(getApplicationContext());
        TravelScheduleCountTuple countTuple = appDb.travelDao().getTravelCount();

        Message msg = handler.obtainMessage();
        msg.obj = countTuple.getCount() + 1;
        handler.sendMessage(msg);
    }
}
