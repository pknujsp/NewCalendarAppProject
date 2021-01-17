package com.zerodsoft.scheduleweather.retrofit;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressKakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midlandfcstresponse.MidLandFcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midtaresponse.MidTaRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtfcstresponse.UltraSrtFcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtncstresponse.UltraSrtNcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.vilagefcstresponse.VilageFcstRoot;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.QueryMap;

public interface Querys
{
    // kakao map
    @Headers({"Authorization: " + HttpCommunicationClient.KAKAO_APP_KEY})
    @GET("search/address.json")
    Call<AddressKakaoLocalResponse> getAddress(@QueryMap(encoded = true) Map<String, String> queryMap);

    @Headers({"Authorization: " + HttpCommunicationClient.KAKAO_APP_KEY})
    @GET("search/keyword.json")
    Call<PlaceKakaoLocalResponse> getPlaceKeyword(@QueryMap(encoded = true) Map<String, String> queryMap);

    @Headers({"Authorization: " + HttpCommunicationClient.KAKAO_APP_KEY})
    @GET("search/category.json")
    Call<PlaceKakaoLocalResponse> getPlaceCategory(@QueryMap(encoded = true) Map<String, String> queryMap);

    @Headers({"Authorization: " + HttpCommunicationClient.KAKAO_APP_KEY})
    @GET("geo/coord2address.json")
    Call<CoordToAddress> getCoordToAddress(@QueryMap(encoded = true) Map<String, String> queryMap);

    // weather
    //초단기 실황
    @GET("getUltraSrtNcst")
    Call<UltraSrtNcstRoot> getUltraSrtNcstData(@QueryMap(encoded = true) Map<String, String> queryMap);

    //초단기 예보
    @GET("getUltraSrtFcst")
    Call<UltraSrtFcstRoot> getUltraSrtFcstData(@QueryMap(encoded = true) Map<String, String> queryMap);

    //동네 예보
    @GET("getVilageFcst")
    Call<VilageFcstRoot> getVilageFcstData(@QueryMap(encoded = true) Map<String, String> queryMap);

    //중기육상예보
    @GET("getMidLandFcst")
    Call<MidLandFcstRoot> getMidLandFcstData(@QueryMap(encoded = true) Map<String, String> queryMap);

    //중기기온조회
    @GET("getMidTa")
    Call<MidTaRoot> getMidTaData(@QueryMap(encoded = true) Map<String, String> queryMap);
}
