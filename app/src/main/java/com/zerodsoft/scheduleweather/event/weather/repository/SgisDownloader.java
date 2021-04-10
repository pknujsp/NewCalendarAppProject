package com.zerodsoft.scheduleweather.event.weather.repository;

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

public abstract class SgisDownloader
{
    public abstract void onResponse(DataWrapper<? extends SgisRoot> result);

    /*
    sgis 인증
     */
    public void auth(SgisAuthParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.SGIS_AUTH);

        Call<SgisAuthResponse> call = querys.auth(parameter.toMap());
        call.enqueue(new Callback<SgisAuthResponse>()
        {
            @Override
            public void onResponse(Call<SgisAuthResponse> call, Response<SgisAuthResponse> response)
            {
                SgisDownloader.this.onResponse(new DataWrapper<SgisAuthResult>(response.body().getResult()));
            }

            @Override
            public void onFailure(Call<SgisAuthResponse> call, Throwable t)
            {
                SgisDownloader.this.onResponse(new DataWrapper<SgisAuthResult>(new Exception(t)));
            }
        });

    }

    /*
    sgis 인증
     */
    public void transcood(TransCoordParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.SGIS_AUTH);

        Call<TransCoordResponse> call = querys.transcoord(parameter.toMap());
        call.enqueue(new Callback<TransCoordResponse>()
        {
            @Override
            public void onResponse(Call<TransCoordResponse> call, Response<TransCoordResponse> response)
            {
                SgisDownloader.this.onResponse(new DataWrapper<TransCoordResult>(response.body().getResult()));
            }

            @Override
            public void onFailure(Call<TransCoordResponse> call, Throwable t)
            {
                SgisDownloader.this.onResponse(new DataWrapper<TransCoordResult>(new Exception(t)));
            }
        });

    }
}
