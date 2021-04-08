package com.zerodsoft.scheduleweather.kakaomap.model.datasource;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import com.zerodsoft.scheduleweather.common.interfaces.OnProgressBarListener;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceMeta;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PlaceItemDataSource extends PositionalDataSource<PlaceDocuments>
{
    private Querys querys;
    private PlaceMeta placeMeta;
    private LocalApiPlaceParameter localApiPlaceParameter;
    private OnProgressBarListener onProgressBarListener;

    public PlaceItemDataSource(LocalApiPlaceParameter localApiParameter, OnProgressBarListener onProgressBarListener)
    {
        this.localApiPlaceParameter = localApiParameter;
        this.onProgressBarListener = onProgressBarListener;
    }


    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<PlaceDocuments> callback)
    {
        onProgressBarListener.setProgressBarVisibility(View.VISIBLE);
        querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
        Map<String, String> queryMap = localApiPlaceParameter.getParameterMap();
        Call<PlaceKakaoLocalResponse> call = null;

        if (localApiPlaceParameter.getQuery() == null)
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
                List<PlaceDocuments> placeDocuments = null;
                if (response.body() == null)
                {
                    placeDocuments = new ArrayList<>();
                    placeMeta = new PlaceMeta();
                } else
                {
                    placeDocuments = response.body().getPlaceDocuments();
                    placeMeta = response.body().getPlaceMeta();
                }
                callback.onResult(placeDocuments, 0, placeDocuments.size());
                onProgressBarListener.setProgressBarVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<PlaceKakaoLocalResponse> call, Throwable t)
            {
                List<PlaceDocuments> placeDocuments = new ArrayList<>();
                placeMeta = new PlaceMeta();
                callback.onResult(placeDocuments, 0, placeDocuments.size());
                onProgressBarListener.setProgressBarVisibility(View.GONE);
            }
        });
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<PlaceDocuments> callback)
    {
        onProgressBarListener.setProgressBarVisibility(View.VISIBLE);

        querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);

        if (!placeMeta.isEnd())
        {
            localApiPlaceParameter.setPage(Integer.toString(Integer.parseInt(localApiPlaceParameter.getPage()) + 1));
            Map<String, String> queryMap = localApiPlaceParameter.getParameterMap();
            Call<PlaceKakaoLocalResponse> call = null;

            if (localApiPlaceParameter.getQuery() == null)
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
                    List<PlaceDocuments> placeDocuments = response.body().getPlaceDocuments();
                    placeMeta = response.body().getPlaceMeta();

                    callback.onResult(placeDocuments);
                    onProgressBarListener.setProgressBarVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<PlaceKakaoLocalResponse> call, Throwable t)
                {
                    List<PlaceDocuments> placeDocuments = new ArrayList<>();
                    callback.onResult(placeDocuments);
                    onProgressBarListener.setProgressBarVisibility(View.GONE);
                }
            });
        } else
        {
            callback.onResult(new ArrayList<PlaceDocuments>(0));
            onProgressBarListener.setProgressBarVisibility(View.GONE);
        }
    }

}
