package com.zerodsoft.scheduleweather.kakaomap.model;

import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.RetrofitCallback;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressKakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.coordtoaddressresponse.CoordToAddress;

import java.util.Map;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class CoordToAddressUtil
{
    public static void coordToAddress(LocalApiPlaceParameter parameter, CarrierMessagingService.ResultCallback<DataWrapper<CoordToAddress>> retrofitCallback)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
        Map<String, String> queryMap = parameter.getParameterMap();
        Call<CoordToAddress> call = querys.getCoordToAddress(queryMap);

        call.enqueue(new RetrofitCallback<CoordToAddress>()
        {
            @SneakyThrows
            @Override
            protected void handleResponse(CoordToAddress data)
            {
                retrofitCallback.onReceiveResult(new DataWrapper<>(data));
            }

            @SneakyThrows
            @Override
            protected void handleError(Response<CoordToAddress> response)
            {
                retrofitCallback.onReceiveResult(new DataWrapper<>(new Exception(response.message())));
            }

            @SneakyThrows
            @Override
            protected void handleFailure(Exception e)
            {
                retrofitCallback.onReceiveResult(new DataWrapper<>(e));
            }
        });

    }
}
