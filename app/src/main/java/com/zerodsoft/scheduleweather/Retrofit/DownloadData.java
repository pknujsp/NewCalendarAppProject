package com.zerodsoft.scheduleweather.Retrofit;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

import com.zerodsoft.scheduleweather.RecyclerVIewAdapter.SearchResultViewPagerAdapter;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponse.AddressResponse;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponse.AddressResponseMeta;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceCategoryResponse.PlaceCategory;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceCategoryResponse.PlaceCategoryDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceCategoryResponse.PlaceCategoryMeta;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceKeywordResponse.PlaceKeyword;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceKeywordResponse.PlaceKeywordDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceKeywordResponse.PlaceKeywordMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadData
{
    public static final int ADDRESS = 0;
    public static final int PLACE_KEYWORD = 1;
    public static final int PLACE_CATEGORY = 2;
    public static final int ADDRESS_AND_PLACE_KEYWORD = 3;
    public static final int PLACE = 4;

    public static void searchAddress(Handler handler, LocalApiPlaceParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("query", parameter.getQuery());
        queryMap.put("page", parameter.getPage());
        queryMap.put("AddressSize", "15");

        Call<AddressResponse> call = querys.getAddress(queryMap);
        call.enqueue(new Callback<AddressResponse>()
        {
            @Override
            public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response)
            {
                AddressResponseMeta meta = response.body().getAddressResponseMeta();
                List<AddressResponseDocuments> documents = (ArrayList<AddressResponseDocuments>) response.body().getAddressResponseDocumentsList();

                Bundle bundle = new Bundle();
                bundle.putParcelable("meta", meta);
                bundle.putParcelableArrayList("documents", (ArrayList<? extends Parcelable>) documents);

                Message message = handler.obtainMessage();
                message.what = ADDRESS;
                message.setData(bundle);
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(Call<AddressResponse> call, Throwable t)
            {
            }
        });
    }

    public static void searchPlaceKeyWord(Handler handler, LocalApiPlaceParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService();
        Map<String, String> queryMap = parameter.getParameterMap();

        Call<PlaceKeyword> call = querys.getPlaceKeyword(queryMap);
        call.enqueue(new Callback<PlaceKeyword>()
        {
            @Override
            public void onResponse(Call<PlaceKeyword> call, Response<PlaceKeyword> response)
            {
                PlaceKeywordMeta meta = response.body().getPlaceKeywordMeta();
                List<PlaceKeywordDocuments> documents = (ArrayList<PlaceKeywordDocuments>) response.body().getPlaceKeywordDocuments();

                Bundle bundle = new Bundle();
                bundle.putParcelable("meta", meta);
                bundle.putParcelableArrayList("documents", (ArrayList<? extends Parcelable>) documents);

                Message message = handler.obtainMessage();
                message.setData(bundle);
                message.what = PLACE_KEYWORD;
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(Call<PlaceKeyword> call, Throwable t)
            {
            }
        });
    }

    public static void searchPlaceCategory(Handler handler, LocalApiPlaceParameter parameter)
    {
        Querys querys = HttpCommunicationClient.getApiService();
        Map<String, String> queryMap = parameter.getParameterMap();
        Call<PlaceCategory> call = querys.getPlaceCategory(queryMap);
        call.enqueue(new Callback<PlaceCategory>()
        {
            @Override
            public void onResponse(Call<PlaceCategory> call, Response<PlaceCategory> response)
            {
                PlaceCategoryMeta meta = response.body().getPlaceCategoryMeta();
                List<PlaceCategoryDocuments> documents = (ArrayList<PlaceCategoryDocuments>) response.body().getPlaceCategoryDocuments();

                Bundle bundle = new Bundle();
                bundle.putParcelable("meta", meta);
                bundle.putParcelableArrayList("documents", (ArrayList<? extends Parcelable>) documents);

                Message message = handler.obtainMessage();
                message.setData(bundle);
                message.what = PLACE_CATEGORY;
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(Call<PlaceCategory> call, Throwable t)
            {
            }
        });
    }
}
