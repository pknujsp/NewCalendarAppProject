package com.zerodsoft.scheduleweather.retrofit;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placecategoryresponse.PlaceCategory;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placekeywordresponse.PlaceKeyword;

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

    @GET("geo/coord2address.json")
    Call<CoordToAddress> getCoordToAddress(@QueryMap(encoded = true) Map<String, String> queryMap);
}
