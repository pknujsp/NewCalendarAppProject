package com.zerodsoft.tripweather.DataCommunication;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.zerodsoft.tripweather.ProcessingType;
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
    private static boolean isCompleted = false;
    private static TimeOutThread thread;

    public static boolean getNForecastData(ArrayList<Schedule> scheduleList, Context context, Handler handler, ProcessingType processingType)
    {
        DataDownloadService dataDownloadService = DataCommunicationClient.getApiService();
        ArrayList<ForecastAreaData> nForecastDataList = new ArrayList<>();
        Map<String, Object> currentDate = Clock.getCurrentDateTime();

        String[] separatedUpdateInfo = ((String) currentDate.get("updatedTime")).split(" ");
        final String today = (String) currentDate.get("baseDateSlash");
        final String updatedDate = separatedUpdateInfo[0];
        final String updatedTime = separatedUpdateInfo[1];

        Clock.convertBaseDateTime(currentDate, Clock.N_FORECAST);

        int count = 1;
        int lastScheduleCount = 0;
        thread = new TimeOutThread().setHandler(handler);
        thread.start();

        for (Schedule schedule : scheduleList)
        {
            if (schedule.getEndDate().compareTo(today) < 0)
            {
                lastScheduleCount++;
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

            final int finalCount = count++;
            final int finalLastScheduleCount = lastScheduleCount;

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

                    if (finalCount == scheduleList.size() - finalLastScheduleCount)
                    {
                        thread.interrupt();
                        Message message = handler.obtainMessage();

                        Bundle bundle = new Bundle();
                        bundle.putString("updatedTime", updatedTime);
                        bundle.putString("updatedDate", updatedDate);
                        bundle.putString("baseDate", (String) currentDate.get("baseDate"));
                        bundle.putString("baseTime", (String) currentDate.get("baseTime"));
                        bundle.putSerializable("nForecastDataList", nForecastDataList);

                        if (processingType.getProcessingType() == Actions.INSERT)
                        {
                            message.what = Actions.INSERT_NFORECAST_DATA;
                        } else if (processingType.getProcessingType() == Actions.UPDATE)
                        {
                            message.what = Actions.UPDATE_NFORECAST_DATA;
                        }

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
        if (lastScheduleCount == scheduleList.size())
        {
            return false;
        }
        return true;
    }
}

class TimeOutThread extends Thread
{
    private Handler handler;

    public TimeOutThread setHandler(Handler handler)
    {
        this.handler = handler;
        return this;
    }

    @Override
    public void run()
    {
        try
        {
            Thread.sleep(3000);
            Message msg = handler.obtainMessage();
            msg.what = Actions.FAILED_DOWNLOAD;
            handler.sendMessage(msg);
        } catch (InterruptedException e)
        {
        }
    }
}
