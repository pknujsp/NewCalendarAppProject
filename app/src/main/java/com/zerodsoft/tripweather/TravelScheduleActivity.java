package com.zerodsoft.tripweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zerodsoft.tripweather.DataCommunication.DataCommunicationClient;
import com.zerodsoft.tripweather.DataCommunication.DataDownloadService;
import com.zerodsoft.tripweather.RequestResponse.WeatherResponse;
import com.zerodsoft.tripweather.RequestResponse.WeatherResponseItem;
import com.zerodsoft.tripweather.Room.DTO.Schedule;
import com.zerodsoft.tripweather.ScheduleList.ScheduleListAdapter;
import com.zerodsoft.tripweather.ScheduleList.ScheduleTable;
import com.zerodsoft.tripweather.ScheduleList.ViewItemDecoration;
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

            if (bundle.getInt("action") == REFRESH_ADAPTER)
            {
                Toast.makeText(getApplicationContext(), "COMPLETED", Toast.LENGTH_SHORT).show();

                ScheduleListAdapter adapter = new ScheduleListAdapter(scheduleTable, nForecastDataList);
                recyclerView.setAdapter(adapter);
                recyclerView.addItemDecoration(new ViewItemDecoration(16));
            }
        }
    };

    private DataDownloadService dataDownloadService = null;

    private final String serviceKey = "T2nJm9zlOA0Z7Dut%2BThT6Jp0Itn0zZw80AUP3uMdOWlZJR1gVPkx9p1t8etuSW1kWsSNrGGHKdxbwr1IUlt%2Baw%3D%3D";
    private final String numOfRows = "250";
    private final String dataType = "JSON";
    private final String pageNo = "1";
    private final static int REFRESH_ADAPTER = 0;
    RecyclerView recyclerView;
    ScheduleTable scheduleTable;
    ArrayList<ForecastAreaData> nForecastDataList = new ArrayList<>();
    ArrayList<Schedule> travelData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_schedule);

        refreshBtn = (Button) findViewById(R.id.button_refresh_data);

        travelData = (ArrayList<Schedule>) getIntent().getSerializableExtra("scheduleList");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_travel_schedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        scheduleTable = new ScheduleTable((ArrayList<Schedule>) travelData);

        refreshBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                refreshData();
            }
        });


    }

    private boolean refreshData()
    {
        dataDownloadService = DataCommunicationClient.getApiService();
        Map<String, Object> currentDate = Clock.getCurrentDateTime();
        final String today = (String) currentDate.get("baseDateSlash");
        Clock.convertBaseDateTime(currentDate, Clock.N_FORECAST);

        for (int i = 0; i < travelData.size(); i++)
        {
            if (travelData.get(i).getEndDate().compareTo(today) < 0)
            {
                continue;
            }

            Map<String, String> queryMap = new HashMap<>();

            queryMap.put("serviceKey", serviceKey);
            queryMap.put("numOfRows", numOfRows);
            queryMap.put("dataType", dataType);
            queryMap.put("pageNo", pageNo);
            queryMap.put("base_date", (String) currentDate.get("baseDate"));
            queryMap.put("base_time", (String) currentDate.get("baseTime"));
            queryMap.put("nx", travelData.get(i).getAreaX());
            queryMap.put("ny", travelData.get(i).getAreaY());

            final int idx = i;

            Call<WeatherResponse> call = dataDownloadService.downloadNForecastData(queryMap);
            call.enqueue(new Callback<WeatherResponse>()
            {
                @Override
                public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response)
                {
                    List<WeatherResponseItem> weatherResponseItems = response.body().getWeatherResponseResponse().
                            getWeatherResponseBody().getWeatherResponseItems().getWeatherResponseItemList();

                    ArrayList<WeatherData> dataList = ResponseDataClassifier.classifyWeatherResponseItem(weatherResponseItems, getApplicationContext());

                    nForecastDataList.add(new ForecastAreaData().setAreaX(dataList.get(0).getAreaX()).setAreaY(dataList.get(0).getAreaY())
                            .setForecastData(dataList));

                    if (idx == travelData.size() - 1)
                    {
                        Message message = handler.obtainMessage();

                        Bundle bundle = new Bundle();
                        bundle.putInt("action", REFRESH_ADAPTER);

                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                }

                @Override
                public void onFailure(Call<WeatherResponse> call, Throwable t)
                {

                }
            });
        }
        return true;
    }

}
