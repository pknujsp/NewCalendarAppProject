package com.zerodsoft.scheduleweather.kakaomap.model;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseMeta;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceMeta;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceResponse;

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

    public AddressItemDataSource(LocalApiPlaceParameter localApiParameter)
    {
        this.localApiPlaceParameter = localApiParameter;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<AddressResponseDocuments> callback)
    {
        querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
        Map<String, String> queryMap = localApiPlaceParameter.getParameterMap();
        Call<AddressResponse> call = querys.getAddress(queryMap);

        call.enqueue(new Callback<AddressResponse>()
        {
            @Override
            public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response)
            {
                List<AddressResponseDocuments> addressDocuments = response.body().getAddressResponseDocumentsList();
                addressMeta = response.body().getAddressResponseMeta();
                callback.onResult(addressDocuments, 0, addressDocuments.size());
            }

            @Override
            public void onFailure(Call<AddressResponse> call, Throwable t)
            {

            }
        });
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<AddressResponseDocuments> callback)
    {
        querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);

        if (!addressMeta.isEnd())
        {
            localApiPlaceParameter.setPage(Integer.toString(Integer.parseInt(localApiPlaceParameter.getPage()) + 1));
            Map<String, String> queryMap = localApiPlaceParameter.getParameterMap();
            Call<AddressResponse> call = querys.getAddress(queryMap);

            call.enqueue(new Callback<AddressResponse>()
            {
                @Override
                public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response)
                {
                    List<AddressResponseDocuments> addressDocuments = response.body().getAddressResponseDocumentsList();
                    addressMeta = response.body().getAddressResponseMeta();
                    callback.onResult(addressDocuments);
                }

                @Override
                public void onFailure(Call<AddressResponse> call, Throwable t)
                {

                }
            });
        } else
        {
            callback.onResult(new ArrayList<AddressResponseDocuments>(0));
        }
    }
}
