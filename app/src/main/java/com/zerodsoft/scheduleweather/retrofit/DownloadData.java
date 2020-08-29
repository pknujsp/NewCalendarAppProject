package com.zerodsoft.scheduleweather.retrofit;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseMeta;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placecategoryresponse.PlaceCategory;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placecategoryresponse.PlaceCategoryDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placecategoryresponse.PlaceCategoryMeta;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placekeywordresponse.PlaceKeyword;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placekeywordresponse.PlaceKeywordDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placekeywordresponse.PlaceKeywordMeta;

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

                Message message = handler.obtainMessage();
                message.what = ADDRESS;
                Bundle bundle = new Bundle();

                if (documents.isEmpty())
                {
                    bundle.putBoolean("isEmpty", true);
                } else
                {
                    bundle.putParcelable("meta", meta);
                    bundle.putParcelableArrayList("documents", (ArrayList<? extends Parcelable>) documents);
                    bundle.putBoolean("isEmpty", false);
                }
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

                Message message = handler.obtainMessage();
                message.what = PLACE_KEYWORD;
                Bundle bundle = new Bundle();

                if (documents.isEmpty())
                {
                    bundle.putBoolean("isEmpty", true);
                } else
                {
                    bundle.putParcelable("meta", meta);
                    bundle.putParcelableArrayList("documents", (ArrayList<? extends Parcelable>) documents);
                    bundle.putBoolean("isEmpty", false);
                }
                message.setData(bundle);
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

                Message message = handler.obtainMessage();
                message.what = PLACE_CATEGORY;
                Bundle bundle = new Bundle();

                if (documents.isEmpty())
                {
                    bundle.putBoolean("isEmpty", true);
                } else
                {
                    bundle.putParcelable("meta", meta);
                    bundle.putParcelableArrayList("documents", (ArrayList<? extends Parcelable>) documents);
                    bundle.putBoolean("isEmpty", false);
                }
                message.setData(bundle);
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(Call<PlaceCategory> call, Throwable t)
            {
            }
        });
    }
}
