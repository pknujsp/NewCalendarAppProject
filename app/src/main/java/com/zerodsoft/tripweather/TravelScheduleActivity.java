package com.zerodsoft.tripweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zerodsoft.tripweather.DataCommunication.DataCommunicationClient;
import com.zerodsoft.tripweather.DataCommunication.DataDownloadService;
import com.zerodsoft.tripweather.DataCommunication.DownloadData;
import com.zerodsoft.tripweather.RequestResponse.WeatherResponse;
import com.zerodsoft.tripweather.RequestResponse.WeatherResponseItem;
import com.zerodsoft.tripweather.Room.DTO.Nforecast;
import com.zerodsoft.tripweather.Room.DTO.Schedule;
import com.zerodsoft.tripweather.Room.DTO.ScheduleNForecast;
import com.zerodsoft.tripweather.Room.WeatherDataThread;
import com.zerodsoft.tripweather.ScheduleList.ScheduleListAdapter;
import com.zerodsoft.tripweather.ScheduleList.ScheduleTable;
import com.zerodsoft.tripweather.ScheduleList.ViewItemDecoration;
import com.zerodsoft.tripweather.Utility.Actions;
import com.zerodsoft.tripweather.Utility.Clock;
import com.zerodsoft.tripweather.Utility.ResponseDataClassifier;
import com.zerodsoft.tripweather.WeatherData.ForecastAreaData;
import com.zerodsoft.tripweather.WeatherData.WeatherData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                    break;
                case Actions.INSERT_NFORECAST_DATA:
                    refreshBtn.setText(bundle.getString("updatedTime"));
                    ArrayList<ForecastAreaData> nForecastDataList = (ArrayList<ForecastAreaData>) bundle.getSerializable("nForecastDataList");
                    WeatherDataThread thread = new WeatherDataThread(nForecastDataList, handler, getApplicationContext(), Actions.INSERT_NFORECAST_DATA);
                    thread.start();
                    break;
            }
        }
    };

    RecyclerView recyclerView;
    ScheduleTable scheduleTable;
    TextView textViewTravelName;
    ArrayList<Schedule> scheduleList;

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
        textViewTravelName.setText(bundle.getString("travelName"));

        scheduleTable = new ScheduleTable(scheduleList);

        if (bundle.getInt("download") == Actions.DOWNLOAD_NFORECAST_DATA)
        {
            DownloadData.getNForecastData(scheduleList, getApplicationContext(), handler);
        }

        refreshBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                DownloadData.getNForecastData(scheduleList, getApplicationContext(), handler);
            }
        });


    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
