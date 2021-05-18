package com.zerodsoft.scheduleweather.weather.repository;

import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.TransCoordParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.auth.SgisAuthResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.transcoord.TransCoordResponse;
import com.zerodsoft.scheduleweather.sgis.SgisAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class SgisTranscoord extends JsonDownloader<TransCoordResponse> {
	private TransCoordParameter transCoordParameter;
	
	private final SgisAuth sgisAuth = new SgisAuth() {
		@Override
		public void onResponseSuccessful(SgisAuthResponse result) {
			SgisAuth.setSgisAuthResponse(result);
			transCoordParameter.setAccessToken(result.getResult().getAccessToken());
			transcoord(transCoordParameter);
		}
		
		@Override
		public void onResponseFailed(Exception e) {
			SgisTranscoord.this.onResponseFailed(e);
		}
	};
	
	public void transcoord(TransCoordParameter parameter) {
		this.transCoordParameter = parameter;
		
		if (SgisAuth.getSgisAuthResponse() != null) {
			parameter.setAccessToken(SgisAuth.getSgisAuthResponse().getResult().getAccessToken());
		} else {
			sgisAuth.auth();
			return;
		}
		
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.SGIS_TRANSFORMATION);
		
		Call<TransCoordResponse> call = querys.transcoord(parameter.toMap());
		call.enqueue(new Callback<TransCoordResponse>() {
			@Override
			public void onResponse(Call<TransCoordResponse> call, Response<TransCoordResponse> response) {
				processResult(response);
			}
			
			@Override
			public void onFailure(Call<TransCoordResponse> call, Throwable t) {
				processResult(t);
			}
		});
		
	}
}
