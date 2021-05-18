package com.zerodsoft.scheduleweather.weather.repository;

import com.google.gson.JsonObject;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.paremeters.NearbyMsrstnListParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.FindStationRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.NearbyMsrstnList.NearbyMsrstnListRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.auth.SgisAuthResponse;
import com.zerodsoft.scheduleweather.sgis.SgisAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class FindAirConditionStationDownloader extends JsonDownloader<JsonObject> {
	/*
	가장 가까운 관측소 검색
	 */
	public void getNearbyMsrstnList(NearbyMsrstnListParameter parameter) {
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.FIND_STATION_FOR_AIR_CONDITION);
		
		Call<JsonObject> call = querys.getNearbyMsrstnListStr(parameter.getMap());
		call.enqueue(new Callback<JsonObject>() {
			@Override
			public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
				processResult(response);
			}
			
			@Override
			public void onFailure(Call<JsonObject> call, Throwable t) {
				processResult(t);
			}
		});
		
	}
}