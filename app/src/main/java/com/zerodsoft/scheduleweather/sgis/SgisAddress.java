package com.zerodsoft.scheduleweather.sgis;

import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.SgisAuthParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.address.ReverseGeoCodingParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.address.reversegeocoding.ReverseGeoCodingResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.auth.SgisAuthResponse;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class SgisAddress extends JsonDownloader<ReverseGeoCodingResponse>
{
    public static void reverseGeoCoding(ReverseGeoCodingParameter parameter, CarrierMessagingService.ResultCallback<DataWrapper<ReverseGeoCodingResponse>> callback)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.SGIS_ADDRESS);

        Call<ReverseGeoCodingResponse> call = querys.reverseGeoCoding(parameter.toMap());
        call.enqueue(new Callback<ReverseGeoCodingResponse>()
        {
            @SneakyThrows
            @Override
            public void onResponse(Call<ReverseGeoCodingResponse> call, Response<ReverseGeoCodingResponse> response)
            {
                callback.onReceiveResult(new DataWrapper<ReverseGeoCodingResponse>(response.body()));
            }

            @SneakyThrows
            @Override
            public void onFailure(Call<ReverseGeoCodingResponse> call, Throwable t)
            {
                callback.onReceiveResult(new DataWrapper<ReverseGeoCodingResponse>(new Exception(t)));
            }
        });

    }
}
