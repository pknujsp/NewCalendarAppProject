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

public abstract class SgisAuth extends JsonDownloader<SgisAuthResponse> {
	private static SgisAuthResponse sgisAuthResponse = null;
	
	/*
	sgis 인증
	 */
	public void auth() {
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.SGIS_AUTH);
		
		SgisAuthParameter parameter = new SgisAuthParameter();
		Call<SgisAuthResponse> call = querys.auth(parameter.toMap());
		call.enqueue(new Callback<SgisAuthResponse>() {
			@Override
			public void onResponse(Call<SgisAuthResponse> call, Response<SgisAuthResponse> response) {
				processResult(response);
			}
			
			@Override
			public void onFailure(Call<SgisAuthResponse> call, Throwable t) {
				processResult(t);
			}
		});
		
	}
	
	public static void setSgisAuthResponse(SgisAuthResponse sgisAuthResponse) {
		SgisAuth.sgisAuthResponse = sgisAuthResponse;
	}
	
	public static SgisAuthResponse getSgisAuthResponse() {
		return sgisAuthResponse;
	}
}
