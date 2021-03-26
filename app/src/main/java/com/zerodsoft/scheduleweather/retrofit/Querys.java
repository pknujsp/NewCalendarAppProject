package com.zerodsoft.scheduleweather.retrofit;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.CtprvnRltmMesureDnsty.CtprvnRltmMesureDnstyResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.CtprvnRltmMesureDnsty.CtprvnRltmMesureDnstyRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.NearbyMsrstnList.NearbyMsrstnListResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.NearbyMsrstnList.NearbyMsrstnListRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressKakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.auth.SgisAuthResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.transcoord.TransCoordResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.midlandfcstresponse.MidLandFcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.midtaresponse.MidTaRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtfcstresponse.UltraSrtFcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtncstresponse.UltraSrtNcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.vilagefcstresponse.VilageFcstRoot;

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

    //미세먼지
    @GET("getMsrstnAcctoRltmMesureDnsty")
    Call<MsrstnAcctoRltmMesureDnstyRoot> getMsrstnAcctoRltmMesureDnsty(@QueryMap(encoded = true) Map<String, String> queryMap);

    @GET("getCtprvnRltmMesureDnsty")
    Call<CtprvnRltmMesureDnstyRoot> getCtprvnRltmMesureDnsty(@QueryMap(encoded = true) Map<String, String> queryMap);

    //공기 측정소
    @GET("getNearbyMsrstnList")
    Call<NearbyMsrstnListRoot> getNearbyMsrstnList(@QueryMap(encoded = true) Map<String, String> queryMap);

    //정부 SGIS
    @GET("auth/authentication.json")
    Call<SgisAuthResponse> auth(@QueryMap(encoded = true) Map<String, String> queryMap);

    @GET("transformation/transcoord.json")
    Call<TransCoordResponse> transcoord(@QueryMap(encoded = true) Map<String, String> queryMap);
}
