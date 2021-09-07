package com.zerodsoft.calendarplatform.sgis;

import com.zerodsoft.calendarplatform.common.classes.JsonDownloader;
import com.zerodsoft.calendarplatform.retrofit.HttpCommunicationClient;
import com.zerodsoft.calendarplatform.retrofit.Querys;
import com.zerodsoft.calendarplatform.retrofit.paremeters.sgis.SgisAuthParameter;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.sgis.auth.SgisAuthResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SgisAuth {
	private static SgisAuthResponse sgisAuthResponse = null;

	private SgisAuth() {
	}

	/*
	sgis 인증
	 */
	public static void auth(JsonDownloader<SgisAuthResponse> callback) {
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.SGIS_AUTH);

		SgisAuthParameter parameter = new SgisAuthParameter();
		Call<SgisAuthResponse> call = querys.auth(parameter.toMap());
		call.enqueue(new Callback<SgisAuthResponse>() {
			@Override
			public void onResponse(Call<SgisAuthResponse> call, Response<SgisAuthResponse> response) {
				callback.processResult(response);
			}

			@Override
			public void onFailure(Call<SgisAuthResponse> call, Throwable t) {
				callback.processResult(t);
			}
		});

	}

	public static void setSgisAuthResponse(SgisAuthResponse sgisAuthResponse) {
		SgisAuth.sgisAuthResponse = sgisAuthResponse;
	}

	public static SgisAuthResponse getSgisAuthResponse() {
		return sgisAuthResponse;
	}


	public static String getAccessToken() {
		return sgisAuthResponse.getResult().getAccessToken();
	}

	public static boolean hasAccessToken() {
		return sgisAuthResponse != null;
	}
}
