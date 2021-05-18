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

public class SgisTranscoord {

	public void transcoord(TransCoordParameter parameter, JsonDownloader<TransCoordResponse> callback) {
		if (SgisAuth.hasAccessToken()) {
			parameter.setAccessToken(SgisAuth.getSgisAuthResponse().getResult().getAccessToken());
		} else {
			SgisAuth.auth(new JsonDownloader<SgisAuthResponse>() {
				@Override
				public void onResponseSuccessful(SgisAuthResponse result) {
					SgisAuth.setSgisAuthResponse(result);
					parameter.setAccessToken(result.getResult().getAccessToken());
					transcoord(parameter, callback);
				}

				@Override
				public void onResponseFailed(Exception e) {
					callback.onResponseFailed(e);
				}
			});
			return;
		}
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.SGIS_TRANSFORMATION);

		Call<TransCoordResponse> call = querys.transcoord(parameter.toMap());
		call.enqueue(new Callback<TransCoordResponse>() {
			@Override
			public void onResponse(Call<TransCoordResponse> call, Response<TransCoordResponse> response) {
				callback.processResult(response);
			}

			@Override
			public void onFailure(Call<TransCoordResponse> call, Throwable t) {
				callback.processResult(t);
			}
		});

	}
}
