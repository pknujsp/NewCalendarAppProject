package com.zerodsoft.calendarplatform.weather.repository;

import com.google.gson.JsonObject;
import com.zerodsoft.calendarplatform.common.classes.JsonDownloader;
import com.zerodsoft.calendarplatform.retrofit.HttpCommunicationClient;
import com.zerodsoft.calendarplatform.retrofit.Querys;
import com.zerodsoft.calendarplatform.retrofit.paremeters.NearbyMsrstnListParameter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindAirConditionStationDownloader {
	/*
	가장 가까운 관측소 검색
	 */
	public void getNearbyMsrstnList(NearbyMsrstnListParameter parameter, JsonDownloader<JsonObject> callback) {
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.FIND_STATION_FOR_AIR_CONDITION);

		Call<JsonObject> call = querys.getNearbyMsrstnListStr(parameter.getMap());
		call.enqueue(new Callback<JsonObject>() {
			@Override
			public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
				callback.processResult(response);
			}

			@Override
			public void onFailure(Call<JsonObject> call, Throwable t) {
				callback.processResult(t);
			}
		});

	}
}