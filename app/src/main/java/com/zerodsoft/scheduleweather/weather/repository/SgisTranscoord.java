package com.zerodsoft.scheduleweather.weather.repository;

import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.TransCoordParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.transcoord.TransCoordResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class SgisTranscoord extends JsonDownloader<TransCoordResponse>
{

    public void transcoord(TransCoordParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.SGIS_TRANSFORMATION);

        Call<TransCoordResponse> call = querys.transcoord(parameter.toMap());
        call.enqueue(new Callback<TransCoordResponse>()
        {
            @Override
            public void onResponse(Call<TransCoordResponse> call, Response<TransCoordResponse> response)
            {
                processResult(response);
            }

            @Override
            public void onFailure(Call<TransCoordResponse> call, Throwable t)
            {
                processResult(t);
            }
        });

    }
}
