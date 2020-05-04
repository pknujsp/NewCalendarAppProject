package com.zerodsoft.tripweather.DataCommunication;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.zerodsoft.tripweather.RequestResponse.WeatherResponse;
import com.zerodsoft.tripweather.RequestResponse.WeatherResponseItem;
import com.zerodsoft.tripweather.Room.DTO.Schedule;
import com.zerodsoft.tripweather.Room.OnDataListener;
import com.zerodsoft.tripweather.TravelScheduleActivity;
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

public class DownloadData
{
    private static final String serviceKey = "T2nJm9zlOA0Z7Dut%2BThT6Jp0Itn0zZw80AUP3uMdOWlZJR1gVPkx9p1t8etuSW1kWsSNrGGHKdxbwr1IUlt%2Baw%3D%3D";
    private static final String numOfRows = "250";
    private static final String dataType = "JSON";
    private static final String pageNo = "1";

    public static boolean getNForecastData(ArrayList<Schedule> scheduleList, Context context, Handler handler)
    {
        DataDownloadService dataDownloadService = DataCommunicationClient.getApiService();
        ArrayList<ForecastAreaData> nForecastDataList = new ArrayList<>();
        Map<String, Object> currentDate = Clock.getCurrentDateTime();
        final String today = (String) currentDate.get("baseDateSlash");
        final String updatedTime = (String) currentDate.get("updatedTime");
        Clock.convertBaseDateTime(currentDate, Clock.N_FORECAST);

        int i = 1;

        for (Schedule schedule : scheduleList)
        {
            if (schedule.getEndDate().compareTo(today) < 0)
            {
                continue;
            }
            int scheduleId = schedule.getSchedule_id();
            int travelId = schedule.getParentId();

            Map<String, String> queryMap = new HashMap<>();

            queryMap.put("serviceKey", serviceKey);
            queryMap.put("numOfRows", numOfRows);
            queryMap.put("dataType", dataType);
            queryMap.put("pageNo", pageNo);
            queryMap.put("base_date", (String) currentDate.get("baseDate"));
            queryMap.put("base_time", (String) currentDate.get("baseTime"));
            queryMap.put("nx", schedule.getAreaX());
            queryMap.put("ny", schedule.getAreaY());

            final int count = i++;

            Call<WeatherResponse> call = dataDownloadService.downloadNForecastData(queryMap);
            call.enqueue(new Callback<WeatherResponse>()
            {
                @Override
                public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response)
                {
                    List<WeatherResponseItem> weatherResponseItems = response.body().getWeatherResponseResponse().
                            getWeatherResponseBody().getWeatherResponseItems().getWeatherResponseItemList();

                    ArrayList<WeatherData> dataList = ResponseDataClassifier.classifyWeatherResponseItem(weatherResponseItems, context);

                    nForecastDataList.add(new ForecastAreaData().setTravelId(travelId).setScheduleId(scheduleId).setAreaX(schedule.getAreaX()).setAreaY(schedule.getAreaY())
                            .setForecastData(dataList));

                    if (count == scheduleList.size())
                    {
                        Message message = handler.obtainMessage();

                        Bundle bundle = new Bundle();
                        bundle.putString("updatedTime", updatedTime);
                        bundle.putSerializable("nForecastDataList", nForecastDataList);
                        message.what = Actions.INSERT_NFORECAST_DATA;

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
