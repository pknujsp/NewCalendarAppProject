package com.zerodsoft.scheduleweather.sgis;

import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.SgisAuthParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.auth.SgisAuthResponse;

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
		if (sgisAuthResponse == null) {
			return null;
		} else {
			return sgisAuthResponse.getResult().getAccessToken();
		}
	}

	public static boolean hasAccessToken() {
		if (sgisAuthResponse == null) {
			return false;
		} else {
			return true;
		}
	}

}
