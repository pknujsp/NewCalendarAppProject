package com.zerodsoft.scheduleweather.Retrofit;

import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface Querys
{
    @GET("search/address.json")
    Call<AddressResponse> getAddress(@QueryMap(encoded = true) Map<String, String> queryMap);
}
