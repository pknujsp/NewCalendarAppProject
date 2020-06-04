package com.zerodsoft.scheduleweather;

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
import com.zerodsoft.scheduleweather.Room.AppDb;
import com.zerodsoft.scheduleweather.Room.DTO.Area;
import com.zerodsoft.scheduleweather.Room.DTO.Schedule;
import com.zerodsoft.scheduleweather.Room.DTO.Travel;
import com.zerodsoft.scheduleweather.Room.DTO.TravelScheduleCountTuple;
import com.zerodsoft.scheduleweather.Room.TravelScheduleThread;
import com.zerodsoft.scheduleweather.ScheduleList.AddScheduleAdapter;
import com.zerodsoft.scheduleweather.Utility.Actions;
import com.zerodsoft.scheduleweather.Utility.Clock;
import com.zerodsoft.scheduleweather.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddScheduleActivity extends AppCompatActivity implements Runnable
{
    private RecyclerView recyclerView;
    private AddScheduleAdapter adapter;
    private FloatingActionButton fabBtn;
    private Toolbar toolbar;
    String originalTravelName;
    int action;
    int travelId;
    EditText editTravelName;
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Bundle bundle = msg.getData();
            switch (msg.what)
            {
                case Actions.SET_TRAVEL_NAME:
                    String count = Integer.toString((Integer) msg.obj);
                    editTravelName.setText(count + "번째 여행");
                    break;
                case Actions.DOWNLOAD_NFORECAST_DATA:
                    int travelId = bundle.getInt("travelId");

                    TravelScheduleThread travelScheduleThread = new TravelScheduleThread(AddScheduleActivity.this);
                    travelScheduleThread.setTravelId(travelId);
                    travelScheduleThread.setAddScheduleActivityHandler(handler);
                    travelScheduleThread.setAction(Actions.CLICKED_TRAVEL_ITEM);
                    travelScheduleThread.start();

                    break;
                case Actions.START_SCHEDULE_ACTIVITY:
                    Intent scheduleActivityIntent = new Intent(getApplicationContext(), TravelScheduleActivity.class);
                    bundle.putBoolean("isNewTravel", true);
                    scheduleActivityIntent.putExtras(bundle);
                    startActivity(scheduleActivityIntent);
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_add_schedule);
        fabBtn = (FloatingActionButton) findViewById(R.id.fab_add_schedule);
        toolbar = (Toolbar) findViewById(R.id.toolBar_add_schedule);
        editTravelName = (EditText) findViewById(R.id.edit_travel_name);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AddScheduleActivity.this));
        adapter = new AddScheduleAdapter(AddScheduleActivity.this);
        recyclerView.setAdapter(adapter);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        this.action = bundle.getInt("action");

        if (action == Actions.START_ADD_SCHEDULE_ACTIVITY)
        {
            Thread thread = new Thread(AddScheduleActivity.this);
            thread.start();
        } else
        {
            // EDIT
            ArrayList<Schedule> scheduleList = (ArrayList<Schedule>) bundle.getSerializable("scheduleList");
            Travel travel = (Travel) bundle.getSerializable("travel");
            editTravelName.setText(travel.getName());
            this.travelId = travel.getId();
            this.originalTravelName = travel.getName();

            for (Schedule original : scheduleList)
            {
                String[] separatedStartDate = original.getStartDate().split("/");
                String[] separatedEndDate = original.getEndDate().split("/");
                String[] separatedArea = original.getAreaName().split(" ");
                Area modifiedArea = new Area();

                Calendar calendar = Calendar.getInstance(Clock.timeZone);

                // startDate
                calendar.set(Integer.parseInt(separatedStartDate[0]), Integer.parseInt(separatedStartDate[1]) - 1, Integer.parseInt(separatedStartDate[2]));
                original.setStartDateObj(calendar.getTime());
                // endDate
                calendar.set(Integer.parseInt(separatedEndDate[0]), Integer.parseInt(separatedEndDate[1]) - 1, Integer.parseInt(separatedEndDate[2]));
                original.setEndDateObj(calendar.getTime());

                modifiedArea.setPhase1(separatedArea[0]);

                if (separatedArea.length == 1)
                {
                    modifiedArea.setPhase2("");
                    modifiedArea.setPhase3("");
                } else if (separatedArea.length == 2)
                {
                    modifiedArea.setPhase2(separatedArea[1]);
                    modifiedArea.setPhase3("");
                } else if (separatedArea.length == 3)
                {
                    modifiedArea.setPhase2(separatedArea[1]);
                    modifiedArea.setPhase3(separatedArea[2]);
                }

                modifiedArea.setArea_id(original.getAreaId());
                modifiedArea.setX(original.getAreaX());
                modifiedArea.setY(original.getAreaY());

                original.setArea(modifiedArea);
            }

            adapter.setTravelSchedules(scheduleList);
            adapter.notifyDataSetChanged();
        }


        fabBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), NewScheduleActivity.class);
                intent.setAction("ADD_SCHEDULE");
                startActivityForResult(intent, Actions.INSERT_SCHEDULE);
            }
        });
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
                case Actions.INSERT_SCHEDULE:
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

                case Actions.UPDATE_SCHEDULE:
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

                    ArrayList<Schedule> travelSchedules = (ArrayList<Schedule>) adapter.getTravelSchedules();
                    String travelName = editTravelName.getText().toString();

                    TravelScheduleThread travelListThread = new TravelScheduleThread(AddScheduleActivity.this);
                    travelListThread.setTravelSchedules(travelSchedules);
                    travelListThread.setAddScheduleActivityHandler(handler);

                    if (action == Actions.START_ADD_SCHEDULE_ACTIVITY)
                    {
                        travelListThread.setTravel(travelName);
                        travelListThread.setAction(Actions.INSERT_TRAVEL);
                    } else
                    {
                        if (!originalTravelName.equals(travelName))
                        {
                            travelListThread.setTravel(travelName);
                        }
                        travelListThread.setAction(Actions.UPDATE_TRAVEL);
                        travelListThread.setTravelId(travelId);
                    }
                    travelListThread.start();
                } else
                {
                    Toast.makeText(getApplicationContext(), "일정을 추가하십시오", Toast.LENGTH_SHORT).show();
                }
                return true;
            case android.R.id.home:
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
        msg.what = Actions.SET_TRAVEL_NAME;
        msg.obj = countTuple.getCount() + 1;
        handler.sendMessage(msg);
    }
}
