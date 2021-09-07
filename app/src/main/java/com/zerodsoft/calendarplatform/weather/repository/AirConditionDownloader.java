package com.zerodsoft.calendarplatform.weather.repository;

import com.google.gson.JsonObject;
import com.zerodsoft.calendarplatform.common.classes.JsonDownloader;
import com.zerodsoft.calendarplatform.common.classes.RetrofitCallListManager;
import com.zerodsoft.calendarplatform.retrofit.HttpCommunicationClient;
import com.zerodsoft.calendarplatform.retrofit.Querys;
import com.zerodsoft.calendarplatform.retrofit.paremeters.CtprvnRltmMesureDnstyParameter;
import com.zerodsoft.calendarplatform.retrofit.paremeters.MsrstnAcctoRltmMesureDnstyParameter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AirConditionDownloader implements RetrofitCallListManager.CallManagerListener {
	private static RetrofitCallListManager retrofitCallListManager = new RetrofitCallListManager();
	private static AirConditionDownloader instance = new AirConditionDownloader();

	public AirConditionDownloader() {
	}


	public static AirConditionDownloader getInstance() {
		return instance;
	}

	public static void close() {
		retrofitCallListManager.clear();
	}

	/*
		관측소 별 실시간 측정정보 조회
		 */
	public void getMsrstnAcctoRltmMesureDnsty(MsrstnAcctoRltmMesureDnstyParameter parameter, JsonDownloader<JsonObject> callback) {
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.AIR_CONDITION);

		Call<JsonObject> call = querys.getMsrstnAcctoRltmMesureDnstyStr(parameter.getMap());
		retrofitCallListManager.add(call);
		call.enqueue(new Callback<JsonObject>() {
			@Override
			public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
				callback.processResult(response);
				retrofitCallListManager.remove(call);
			}

			@Override
			public void onFailure(Call<JsonObject> call, Throwable t) {
				callback.processResult(t);
				retrofitCallListManager.remove(call);

			}
		});
	}

	/*
	시도 별 실시간 측정정보 조회
	 */
	public void getCtprvnRltmMesureDnsty(CtprvnRltmMesureDnstyParameter parameter, JsonDownloader<JsonObject> callback) {
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.AIR_CONDITION);

		Call<JsonObject> call = querys.getCtprvnRltmMesureDnstyStr(parameter.getMap());
		retrofitCallListManager.add(call);

		call.enqueue(new Callback<JsonObject>() {
			@Override
			public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
				callback.processResult(response);
				retrofitCallListManager.remove(call);

			}

			@Override
			public void onFailure(Call<JsonObject> call, Throwable t) {
				callback.processResult(t);
				retrofitCallListManager.remove(call);

			}
		});

	}

	@Override
	public void clear() {
		retrofitCallListManager.clear();
	}
}
