package com.zerodsoft.scheduleweather.Retrofit;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponse;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponseMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadData
{


    public static void searchAddress(String address, Handler handler)
    {
        Querys querys = HttpCommunicationClient.getApiService();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("query", address);

        Call<AddressResponse> call = querys.getAddress(queryMap);
        call.enqueue(new Callback<AddressResponse>()
        {
            @Override
            public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response)
            {
                AddressResponseMeta meta = response.body().getAddressResponseMeta();
                List<AddressResponseDocuments> documents = (ArrayList<AddressResponseDocuments>) response.body().getAddressResponseDocumentsList();

                Bundle bundle = new Bundle();
                bundle.putSerializable("meta", meta);
                bundle.putSerializable("documents", (ArrayList<AddressResponseDocuments>) documents);

                Message message = handler.obtainMessage();
                message.setData(bundle);
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(Call<AddressResponse> call, Throwable t)
            {
            }
        });
    }
}
