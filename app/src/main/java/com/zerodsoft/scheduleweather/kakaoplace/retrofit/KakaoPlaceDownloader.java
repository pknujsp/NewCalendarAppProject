package com.zerodsoft.scheduleweather.kakaoplace.retrofit;

import android.service.carrier.CarrierMessagingService;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.common.interfaces.OnProgressBarListener;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class KakaoPlaceDownloader extends JsonDownloader<KakaoLocalResponse>
{
    private final OnProgressBarListener onProgressBarListener;

    public KakaoPlaceDownloader(OnProgressBarListener onProgressBarListener)
    {
        this.onProgressBarListener = onProgressBarListener;
    }

    public void getPlaces(LocalApiPlaceParameter parameter)
    {
        onProgressBarListener.setProgressBarVisibility(View.VISIBLE);

        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
        Map<String, String> queryMap = parameter.getParameterMap();
        Call<PlaceKakaoLocalResponse> call = null;

        if (parameter.getQuery() == null)
        {
            call = querys.getPlaceCategory(queryMap);
        } else
        {
            call = querys.getPlaceKeyword(queryMap);
        }

        call.enqueue(new Callback<PlaceKakaoLocalResponse>()
        {
            @Override
            public void onResponse(Call<PlaceKakaoLocalResponse> call, Response<PlaceKakaoLocalResponse> response)
            {
                processResult(response);
                onProgressBarListener.setProgressBarVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<PlaceKakaoLocalResponse> call, Throwable t)
            {
                processResult(t);
                onProgressBarListener.setProgressBarVisibility(View.GONE);
            }
        });
    }

    public void getPlacesForSpecific(LocalApiPlaceParameter parameter, JsonDownloader<PlaceKakaoLocalResponse> jsonDownloaderCallback)
    {
        onProgressBarListener.setProgressBarVisibility(View.VISIBLE);

        Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
        Map<String, String> queryMap = parameter.getParameterMap();
        Call<PlaceKakaoLocalResponse> call = null;

        if (parameter.getQuery() == null)
        {
            call = querys.getPlaceCategory(queryMap);
        } else
        {
            call = querys.getPlaceKeyword(queryMap);
        }

        call.enqueue(new Callback<PlaceKakaoLocalResponse>()
        {
            @SneakyThrows
            @Override
            public void onResponse(Call<PlaceKakaoLocalResponse> call, Response<PlaceKakaoLocalResponse> response)
            {
                jsonDownloaderCallback.processResult(response);
                onProgressBarListener.setProgressBarVisibility(View.GONE);
            }

            @SneakyThrows
            @Override
            public void onFailure(Call<PlaceKakaoLocalResponse> call, Throwable t)
            {
                jsonDownloaderCallback.processResult(t);
                onProgressBarListener.setProgressBarVisibility(View.GONE);
            }
        });
    }

}