package com.zerodsoft.calendarplatform.retrofit;

import com.google.gson.JsonObject;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.aircondition.CtprvnRltmMesureDnsty.CtprvnRltmMesureDnstyRoot;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyRoot;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.aircondition.NearbyMsrstnList.NearbyMsrstnListRoot;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.kakaoplace.KakaoPlaceJsonRoot;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.addressresponse.AddressKakaoLocalResponse;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.coordtoregioncoderesponse.CoordToRegionCode;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.sgis.address.reversegeocoding.ReverseGeoCodingResponse;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.sgis.auth.SgisAuthResponse;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.sgis.building.buildingarea.BuildingAreaResponse;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.sgis.building.buildingattribute.BuildingAttributeResponse;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.sgis.building.floorcompanyinfo.FloorCompanyInfoResponse;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.sgis.building.flooretcfacility.FloorEtcFacilityResponse;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.sgis.transcoord.TransCoordResponse;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.midlandfcstresponse.MidLandFcstRoot;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.midtaresponse.MidTaRoot;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.ultrasrtfcstresponse.UltraSrtFcstRoot;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.ultrasrtncstresponse.UltraSrtNcstRoot;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface Querys {
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

	@Headers({"Authorization: " + HttpCommunicationClient.KAKAO_APP_KEY})
	@GET("geo/coord2regioncode.json")
	Call<CoordToRegionCode> getCoordToRegionCode(@QueryMap(encoded = true) Map<String, String> queryMap);

	// weather
	//초단기 실황
	@GET("getUltraSrtNcst")
	Call<UltraSrtNcstRoot> getUltraSrtNcstData(@QueryMap(encoded = true) Map<String, String> queryMap);

	@GET("getUltraSrtNcst")
	Call<JsonObject> getUltraSrtNcstDataStr(@QueryMap(encoded = true) Map<String, String> queryMap);

	//초단기 예보
	@GET("getUltraSrtFcst")
	Call<UltraSrtFcstRoot> getUltraSrtFcstData(@QueryMap(encoded = true) Map<String, String> queryMap);

	@GET("getUltraSrtFcst")
	Call<JsonObject> getUltraSrtFcstDataStr(@QueryMap(encoded = true) Map<String, String> queryMap);

	//동네 예보
	@GET("getVilageFcst")
	Call<com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.vilagefcstresponse.VilageFcstRoot> getVilageFcstData(@QueryMap(encoded = true) Map<String, String> queryMap);

	@GET("getVilageFcst")
	Call<JsonObject> getVilageFcstDataStr(@QueryMap(encoded = true) Map<String, String> queryMap);

	//중기육상예보
	@GET("getMidLandFcst")
	Call<MidLandFcstRoot> getMidLandFcstData(@QueryMap(encoded = true) Map<String, String> queryMap);

	@GET("getMidLandFcst")
	Call<JsonObject> getMidLandFcstDataStr(@QueryMap(encoded = true) Map<String, String> queryMap);

	//중기기온조회
	@GET("getMidTa")
	Call<MidTaRoot> getMidTaData(@QueryMap(encoded = true) Map<String, String> queryMap);

	@GET("getMidTa")
	Call<JsonObject> getMidTaDataStr(@QueryMap(encoded = true) Map<String, String> queryMap);

	//미세먼지
	@GET("getMsrstnAcctoRltmMesureDnsty")
	Call<MsrstnAcctoRltmMesureDnstyRoot> getMsrstnAcctoRltmMesureDnsty(@QueryMap(encoded = true) Map<String, String> queryMap);

	//string test
	@GET("getMsrstnAcctoRltmMesureDnsty")
	Call<JsonObject> getMsrstnAcctoRltmMesureDnstyStr(@QueryMap(encoded = true) Map<String, String> queryMap);

	@GET("getCtprvnRltmMesureDnsty")
	Call<CtprvnRltmMesureDnstyRoot> getCtprvnRltmMesureDnsty(@QueryMap(encoded = true) Map<String, String> queryMap);

	@GET("getCtprvnRltmMesureDnsty")
	Call<JsonObject> getCtprvnRltmMesureDnstyStr(@QueryMap(encoded = true) Map<String, String> queryMap);

	//공기 측정소
	@GET("getNearbyMsrstnList")
	Call<NearbyMsrstnListRoot> getNearbyMsrstnList(@QueryMap(encoded = true) Map<String, String> queryMap);

	//근접 측정소 목록
	@GET("getNearbyMsrstnList")
	Call<JsonObject> getNearbyMsrstnListStr(@QueryMap(encoded = true) Map<String, String> queryMap);

	//측정소 목록 getMsrstnList
	@GET("getMsrstnList")
	Call<JsonObject> getMsrstnListStr(@QueryMap(encoded = true) Map<String, String> queryMap);

	//정부 SGIS 인증
	@GET("authentication.json")
	Call<SgisAuthResponse> auth(@QueryMap(encoded = true) Map<String, String> queryMap);

	//정부 SGIS 좌표변환
	@GET("transcoord.json")
	Call<TransCoordResponse> transcoord(@QueryMap(encoded = true) Map<String, String> queryMap);

	//정부 SGIS 건물정보
	@GET("buildingarea.json")
	Call<BuildingAreaResponse> buildingArea(@QueryMap(encoded = true) Map<String, String> queryMap);

	@GET("buildingattribute.json")
	Call<BuildingAttributeResponse> buildingAttribute(@QueryMap(encoded = true) Map<String, String> queryMap);

	@GET("flooretcfacility.json")
	Call<FloorEtcFacilityResponse> floorEtcFacility(@QueryMap(encoded = true) Map<String, String> queryMap);

	@GET("floorcompanyinfo.json")
	Call<FloorCompanyInfoResponse> floorCompanyInfo(@QueryMap(encoded = true) Map<String, String> queryMap);

	//정부 SGIS 주소
	@GET("rgeocode.json")
	Call<ReverseGeoCodingResponse> reverseGeoCoding(@QueryMap(encoded = true) Map<String, String> queryMap);

	// kakao place
	@GET("v/{id}")
	Call<KakaoPlaceJsonRoot> getPlaceData(@Path("id") String id);
}
