package com.zerodsoft.scheduleweather.sgis;

import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.SgisAuthParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.address.ReverseGeoCodingParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.address.reversegeocoding.ReverseGeoCodingResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.auth.SgisAuthResponse;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SgisAddress {
	public static void reverseGeoCoding(ReverseGeoCodingParameter parameter, JsonDownloader<ReverseGeoCodingResponse> callback) {
		if (SgisAuth.hasAccessToken()) {
			parameter.setAccessToken(SgisAuth.getAccessToken());
		} else {
			SgisAuth.auth(new JsonDownloader<SgisAuthResponse>() {
				@Override
				public void onResponseSuccessful(SgisAuthResponse result) {
					SgisAuth.setSgisAuthResponse(result);
					parameter.setAccessToken(result.getResult().getAccessToken());
					reverseGeoCoding(parameter, callback);
				}

				@Override
				public void onResponseFailed(Exception e) {
					callback.onResponseFailed(e);
				}
			});
			return;
		}
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.SGIS_ADDRESS);

		Call<ReverseGeoCodingResponse> call = querys.reverseGeoCoding(parameter.toMap());
		call.enqueue(new Callback<ReverseGeoCodingResponse>() {
			@Override
			public void onResponse(Call<ReverseGeoCodingResponse> call, Response<ReverseGeoCodingResponse> response) {
				callback.processResult(response);
			}

			@Override
			public void onFailure(Call<ReverseGeoCodingResponse> call, Throwable t) {
				callback.processResult(t);
			}
		});

	}

}
