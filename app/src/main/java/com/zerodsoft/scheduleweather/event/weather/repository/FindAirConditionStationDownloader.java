package com.zerodsoft.scheduleweather.event.weather.repository;

import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.RetrofitCallback;
import com.zerodsoft.scheduleweather.retrofit.paremeters.NearbyMsrstnListParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.FindStationRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.NearbyMsrstnList.NearbyMsrstnListBody;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.NearbyMsrstnList.NearbyMsrstnListRoot;

import retrofit2.Call;
import retrofit2.Response;

public abstract class FindAirConditionStationDownloader
{
    public abstract void onResponse(DataWrapper<? extends FindStationRoot> result);

    /*
    가장 가까운 관측소 검색
     */
    public void getNearbyMsrstnList(NearbyMsrstnListParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.FIND_STATION_FOR_AIR_CONDITION);

        Call<NearbyMsrstnListRoot> call = querys.getNearbyMsrstnList(parameter.getMap());
        call.enqueue(new RetrofitCallback<NearbyMsrstnListRoot>()
        {
            @Override
            protected void handleResponse(NearbyMsrstnListRoot data)
            {
                FindAirConditionStationDownloader.this.onResponse(new DataWrapper<NearbyMsrstnListBody>(data.getResponse().getBody()));
            }

            @Override
            protected void handleError(Response<NearbyMsrstnListRoot> response)
            {
                FindAirConditionStationDownloader.this.onResponse(new DataWrapper<NearbyMsrstnListBody>(new Exception(response.message())));
            }

            @Override
            protected void handleFailure(Exception e)
            {
                FindAirConditionStationDownloader.this.onResponse(new DataWrapper<NearbyMsrstnListBody>(e));
            }
        });

    }
}