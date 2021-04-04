package com.zerodsoft.scheduleweather.kakaoplace;

import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.kakaoplace.KakaoPlaceJsonRoot;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KakaoPlace
{
    public static void getKakaoPlaceData(String placeId, CarrierMessagingService.ResultCallback<DataWrapper<KakaoPlaceJsonRoot>> callback)
    {
        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO_PLACE);
        Call<KakaoPlaceJsonRoot> call = querys.getPlaceData(placeId);

        call.enqueue(new Callback<KakaoPlaceJsonRoot>()
        {
            @SneakyThrows
            @Override
            public void onResponse(Call<KakaoPlaceJsonRoot> call, Response<KakaoPlaceJsonRoot> response)
            {
                DataWrapper<KakaoPlaceJsonRoot> dataWrapper = new DataWrapper<>(response.body());
                callback.onReceiveResult(dataWrapper);
            }

            @SneakyThrows
            @Override
            public void onFailure(Call<KakaoPlaceJsonRoot> call, Throwable t)
            {
                DataWrapper<KakaoPlaceJsonRoot> dataWrapper = new DataWrapper<>(new Exception(t));
                callback.onReceiveResult(dataWrapper);
            }
        });
    }
}
