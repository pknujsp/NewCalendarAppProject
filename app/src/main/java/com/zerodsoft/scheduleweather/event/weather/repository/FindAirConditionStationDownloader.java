package com.zerodsoft.scheduleweather.event.weather.repository;

import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.RetrofitCallback;
import com.zerodsoft.scheduleweather.retrofit.paremeters.NearbyMsrstnListParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.FindStationRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.NearbyMsrstnList.NearbyMsrstnListBody;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.NearbyMsrstnList.NearbyMsrstnListRoot;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class FindAirConditionStationDownloader extends JsonDownloader<FindStationRoot>
{

    /*
    가장 가까운 관측소 검색
     */
    public void getNearbyMsrstnList(NearbyMsrstnListParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.FIND_STATION_FOR_AIR_CONDITION);

        Call<NearbyMsrstnListRoot> call = querys.getNearbyMsrstnList(parameter.getMap());
        call.enqueue(new Callback<NearbyMsrstnListRoot>()
        {
            @Override
            public void onResponse(Call<NearbyMsrstnListRoot> call, Response<NearbyMsrstnListRoot> response)
            {
                processResult(response);
            }

            @Override
            public void onFailure(Call<NearbyMsrstnListRoot> call, Throwable t)
            {
                processResult(t);
            }
        });

    }
}