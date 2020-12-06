package com.zerodsoft.scheduleweather.kakaomap.model;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceMeta;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceResponse;

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

    public PlaceItemDataSource(LocalApiPlaceParameter localApiParameter)
    {
        this.localApiPlaceParameter = localApiParameter;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<PlaceDocuments> callback)
    {
        querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
        Map<String, String> queryMap = localApiPlaceParameter.getParameterMap();
        Call<PlaceResponse> call = null;

        if (localApiPlaceParameter.getQuery() == null)
        {
            call = querys.getPlaceCategory(queryMap);
        } else
        {
            call = querys.getPlaceKeyword(queryMap);
        }

        call.enqueue(new Callback<PlaceResponse>()
        {
            @Override
            public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response)
            {
                List<PlaceDocuments> placeDocuments = response.body().getPlaceDocuments();
                placeMeta = response.body().getPlaceMeta();

                callback.onResult(placeDocuments, 0, placeDocuments.size());
            }

            @Override
            public void onFailure(Call<PlaceResponse> call, Throwable t)
            {
            }
        });
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<PlaceDocuments> callback)
    {
        querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);

        if (!placeMeta.isEnd())
        {
            localApiPlaceParameter.setPage(Integer.toString(Integer.parseInt(localApiPlaceParameter.getPage()) + 1));
            Map<String, String> queryMap = localApiPlaceParameter.getParameterMap();
            Call<PlaceResponse> call = null;

            if (localApiPlaceParameter.getQuery() == null)
            {
                call = querys.getPlaceCategory(queryMap);
            } else
            {
                call = querys.getPlaceKeyword(queryMap);
            }

            call.enqueue(new Callback<PlaceResponse>()
            {
                @Override
                public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response)
                {
                    List<PlaceDocuments> placeDocuments = response.body().getPlaceDocuments();
                    placeMeta = response.body().getPlaceMeta();

                    callback.onResult(placeDocuments);
                }

                @Override
                public void onFailure(Call<PlaceResponse> call, Throwable t)
                {
                }
            });
        } else
        {
            callback.onResult(new ArrayList<PlaceDocuments>(0));
        }
    }
}
