package com.zerodsoft.scheduleweather.weather.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.paremeters.CtprvnRltmMesureDnstyParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.MsrstnAcctoRltmMesureDnstyParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.AirConditionRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.CtprvnRltmMesureDnsty.CtprvnRltmMesureDnstyRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyRoot;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class AirConditionDownloader {
	/*
	관측소 별 실시간 측정정보 조회
	 */
	public void getMsrstnAcctoRltmMesureDnsty(MsrstnAcctoRltmMesureDnstyParameter parameter, JsonDownloader<JsonObject> callback) {
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.AIR_CONDITION);

		Call<JsonObject> call = querys.getMsrstnAcctoRltmMesureDnstyStr(parameter.getMap());
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

	/*
	시도 별 실시간 측정정보 조회
	 */
	public void getCtprvnRltmMesureDnsty(CtprvnRltmMesureDnstyParameter parameter, JsonDownloader<JsonObject> callback) {
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.AIR_CONDITION);

		Call<JsonObject> call = querys.getCtprvnRltmMesureDnstyStr(parameter.getMap());
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
