package com.zerodsoft.tripweather.DataCommunication;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.zerodsoft.tripweather.RequestResponse.WeatherResponse;
import com.zerodsoft.tripweather.RequestResponse.WeatherResponseItem;
import com.zerodsoft.tripweather.Room.DTO.Schedule;
import com.zerodsoft.tripweather.TravelScheduleActivity;
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

    public static boolean getNForecastData(ArrayList<Schedule> travelData, Context context, Handler handler)
    {
        DataDownloadService dataDownloadService = DataCommunicationClient.getApiService();
        ArrayList<ForecastAreaData> nForecastDataList = new ArrayList<>();
        Map<String, Object> currentDate = Clock.getCurrentDateTime();
        final String today = (String) currentDate.get("baseDateSlash");
        final String updatedTime = (String) currentDate.get("updatedTime");
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

                    ArrayList<WeatherData> dataList = ResponseDataClassifier.classifyWeatherResponseItem(weatherResponseItems, context);

                    nForecastDataList.add(new ForecastAreaData().setAreaX(dataList.get(0).getAreaX()).setAreaY(dataList.get(0).getAreaY())
                            .setForecastData(dataList));

                    if (idx == travelData.size() - 1)
                    {
                        Message message = handler.obtainMessage();

                        Bundle bundle = new Bundle();
                        bundle.putInt("action", TravelScheduleActivity.REFRESH_ADAPTER);
                        bundle.putString("updatedTime", updatedTime);
                        bundle.putSerializable("nForecastDataList", nForecastDataList);

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
