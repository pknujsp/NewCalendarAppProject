package com.zerodsoft.calendarplatform.sgis;

import com.zerodsoft.calendarplatform.common.classes.JsonDownloader;
import com.zerodsoft.calendarplatform.retrofit.HttpCommunicationClient;
import com.zerodsoft.calendarplatform.retrofit.Querys;
import com.zerodsoft.calendarplatform.retrofit.paremeters.sgis.address.ReverseGeoCodingParameter;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.sgis.address.reversegeocoding.ReverseGeoCodingResponse;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.sgis.auth.SgisAuthResponse;

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
