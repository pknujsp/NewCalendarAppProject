package com.zerodsoft.scheduleweather.event.weather.repository;

import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.SgisAuthParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.TransCoordParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.SgisRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.auth.SgisAuthResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.auth.SgisAuthResult;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.transcoord.TransCoordResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.transcoord.TransCoordResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class SgisTranscoord extends JsonDownloader<TransCoordResponse>
{

    public void transcoord(TransCoordParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.SGIS_AUTH);

        Call<TransCoordResponse> call = querys.transcoord(parameter.toMap());
        call.enqueue(new Callback<TransCoordResponse>()
        {
            @Override
            public void onResponse(Call<TransCoordResponse> call, Response<TransCoordResponse> response)
            {
                SgisTranscoord.this.onResponse(new DataWrapper<TransCoordResponse>(response.body()));
            }

            @Override
            public void onFailure(Call<TransCoordResponse> call, Throwable t)
            {
                SgisTranscoord.this.onResponse(new DataWrapper<TransCoordResponse>(new Exception(t)));
            }
        });

    }
}
