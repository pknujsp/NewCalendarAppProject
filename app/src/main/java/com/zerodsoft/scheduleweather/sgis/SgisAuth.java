package com.zerodsoft.scheduleweather.sgis;

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

public abstract class SgisAuth extends JsonDownloader<SgisAuthResponse>
{
    private static SgisAuthResponse sgisAuthResponse = null;
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
                SgisAuth.this.onResponse(new DataWrapper<SgisAuthResponse>(response.body()));
            }

            @Override
            public void onFailure(Call<SgisAuthResponse> call, Throwable t)
            {
                SgisAuth.this.onResponse(new DataWrapper<SgisAuthResponse>(new Exception(t)));
            }
        });

    }

    public static void setSgisAuthResponse(SgisAuthResponse sgisAuthResponse)
    {
        SgisAuth.sgisAuthResponse = sgisAuthResponse;
    }

    public static SgisAuthResponse getSgisAuthResponse()
    {
        return sgisAuthResponse;
    }
}
