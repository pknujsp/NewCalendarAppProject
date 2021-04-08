package com.zerodsoft.scheduleweather.kakaomap.model.datasource;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import com.zerodsoft.scheduleweather.common.interfaces.OnProgressBarListener;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressKakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressItemDataSource extends PositionalDataSource<AddressResponseDocuments>
{
    private Querys querys;
    private AddressResponseMeta addressMeta;
    private LocalApiPlaceParameter localApiPlaceParameter;
    private OnProgressBarListener onProgressBarListener;

    public AddressItemDataSource(LocalApiPlaceParameter localApiParameter, OnProgressBarListener onProgressBarListener)
    {
        this.localApiPlaceParameter = localApiParameter;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<AddressResponseDocuments> callback)
    {
        onProgressBarListener.setProgressBarVisibility(View.VISIBLE);
        querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
        Map<String, String> queryMap = localApiPlaceParameter.getParameterMap();
        Call<AddressKakaoLocalResponse> call = querys.getAddress(queryMap);

        call.enqueue(new Callback<AddressKakaoLocalResponse>()
        {
            @Override
            public void onResponse(Call<AddressKakaoLocalResponse> call, Response<AddressKakaoLocalResponse> response)
            {
                List<AddressResponseDocuments> addressDocuments = null;

                if (response.body() == null)
                {
                    addressDocuments = new ArrayList<>();
                    addressMeta = new AddressResponseMeta();
                } else
                {
                    addressDocuments = response.body().getAddressResponseDocumentsList();
                    addressMeta = response.body().getAddressResponseMeta();
                }
                callback.onResult(addressDocuments, 0, addressDocuments.size());
                onProgressBarListener.setProgressBarVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<AddressKakaoLocalResponse> call, Throwable t)
            {
                List<AddressResponseDocuments> addressDocuments = new ArrayList<>();
                addressMeta = new AddressResponseMeta();
                callback.onResult(addressDocuments, 0, addressDocuments.size());
                onProgressBarListener.setProgressBarVisibility(View.GONE);
            }
        });
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<AddressResponseDocuments> callback)
    {
        onProgressBarListener.setProgressBarVisibility(View.VISIBLE);
        querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);

        if (!addressMeta.isEnd())
        {
            localApiPlaceParameter.setPage(Integer.toString(Integer.parseInt(localApiPlaceParameter.getPage()) + 1));
            Map<String, String> queryMap = localApiPlaceParameter.getParameterMap();
            Call<AddressKakaoLocalResponse> call = querys.getAddress(queryMap);

            call.enqueue(new Callback<AddressKakaoLocalResponse>()
            {
                @Override
                public void onResponse(Call<AddressKakaoLocalResponse> call, Response<AddressKakaoLocalResponse> response)
                {
                    List<AddressResponseDocuments> addressDocuments = response.body().getAddressResponseDocumentsList();
                    addressMeta = response.body().getAddressResponseMeta();
                    callback.onResult(addressDocuments);
                    onProgressBarListener.setProgressBarVisibility(View.GONE);

                }

                @Override
                public void onFailure(Call<AddressKakaoLocalResponse> call, Throwable t)
                {
                    List<AddressResponseDocuments> addressDocuments = new ArrayList<>();
                    callback.onResult(addressDocuments);
                    onProgressBarListener.setProgressBarVisibility(View.GONE);

                }
            });
        } else
        {
            callback.onResult(new ArrayList<AddressResponseDocuments>(0));
        }
    }
}
