package com.zerodsoft.scheduleweather;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.DataCommunication.DownloadData;
import com.zerodsoft.scheduleweather.Room.DTO.Schedule;
import com.zerodsoft.scheduleweather.Room.DTO.ScheduleNForecast;
import com.zerodsoft.scheduleweather.Room.WeatherDataThread;
import com.zerodsoft.scheduleweather.ScheduleList.ScheduleListAdapter;
import com.zerodsoft.scheduleweather.ScheduleList.ScheduleTable;
import com.zerodsoft.scheduleweather.ScheduleList.ViewItemDecoration;
import com.zerodsoft.scheduleweather.Utility.Actions;
import com.zerodsoft.scheduleweather.WeatherData.ForecastAreaData;
import com.zerodsoft.scheduleweather.R;

import java.util.ArrayList;

public class TravelScheduleActivity extends AppCompatActivity
{
    Button refreshBtn;
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Bundle bundle = msg.getData();

            switch (msg.what)
            {
                case Actions.REFRESH_ADAPTER:
                    ArrayList<ScheduleNForecast> savedNForecastDataList = (ArrayList<ScheduleNForecast>) bundle.getSerializable("savedNForecastDataList");
                    ScheduleListAdapter adapter = new ScheduleListAdapter(scheduleTable, savedNForecastDataList);
                    recyclerView.setAdapter(adapter);
                    refreshBtn.setText(bundle.getString("updatedDate") + " " + bundle.getString("updatedTime"));
                    break;

                case Actions.INSERT_NFORECAST_DATA:
                    ArrayList<ForecastAreaData> nForecastDataList = (ArrayList<ForecastAreaData>) bundle.getSerializable("nForecastDataList");
                    WeatherDataThread thread = new WeatherDataThread(nForecastDataList, bundle, handler, getApplicationContext(), new ProcessingType().setAction(Actions.INSERT_NFORECAST_DATA).setProcessingType(Actions.INSERT));
                    thread.start();
                    break;

                case Actions.UPDATE_NFORECAST_DATA:
                    ArrayList<ForecastAreaData> nForecastDataList2 = (ArrayList<ForecastAreaData>) bundle.getSerializable("nForecastDataList");
                    WeatherDataThread thread2 = new WeatherDataThread(nForecastDataList2, bundle, handler, getApplicationContext(), new ProcessingType().setAction(Actions.INSERT_NFORECAST_DATA).setProcessingType(Actions.UPDATE));
                    thread2.start();
                    break;

                case Actions.FAILED_DOWNLOAD:
                    Toast.makeText(TravelScheduleActivity.this, "FAILED", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    RecyclerView recyclerView;
    ScheduleTable scheduleTable;
    TextView textViewTravelName;
    ArrayList<Schedule> scheduleList;
    int travelId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_schedule);

        refreshBtn = (Button) findViewById(R.id.button_refresh_data);
        textViewTravelName = (TextView) findViewById(R.id.text_view_curr_travel_name);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_travel_schedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new ViewItemDecoration(16));

        Bundle bundle = getIntent().getExtras();

        scheduleList = (ArrayList<Schedule>) bundle.getSerializable("scheduleList");
        travelId = bundle.getInt("travelId");
        textViewTravelName.setText(bundle.getString("travelName"));

        scheduleTable = new ScheduleTable(scheduleList);

        if (bundle.getBoolean("isNewTravel"))
        {
            DownloadData.getNForecastData(scheduleList, getApplicationContext(), handler, new ProcessingType().setProcessingType(Actions.INSERT));
        } else
        {
            WeatherDataThread thread = new WeatherDataThread(bundle, handler, getApplicationContext(), new ProcessingType().setAction(Actions.SELECT_NFORECAST_DATA).setProcessingType(Actions.SELECT));
            thread.start();
        }

        refreshBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (!DownloadData.getNForecastData(scheduleList, getApplicationContext(), handler, new ProcessingType().setProcessingType(Actions.UPDATE)))
                {
                    // 모든 일정이 과거인 경우
                    Toast.makeText(getApplicationContext(), "모든 일정이 지났습니다\n갱신을 취소하였습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
