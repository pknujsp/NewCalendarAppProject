package com.zerodsoft.scheduleweather.Retrofit;

import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponse.AddressResponse;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceCategoryResponse.PlaceCategory;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceKeywordResponse.PlaceKeyword;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface Querys
{
    @GET("search/address.json")
    Call<AddressResponse> getAddress(@QueryMap(encoded = true) Map<String, String> queryMap);

    @GET("search/keyword.json")
    Call<PlaceKeyword> getPlaceKeyword(@QueryMap(encoded = true) Map<String, String> queryMap);

    @GET("search/category.json")
    Call<PlaceCategory> getPlaceCategory(@QueryMap(encoded = true) Map<String, String> queryMap);
}
