package com.zerodsoft.scheduleweather.navermap.model;

import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.RetrofitCallback;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;

import java.util.Map;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class CoordToAddressUtilByKakao {
	public static void coordToAddress(LocalApiPlaceParameter parameter, JsonDownloader<CoordToAddress> callback) {
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
		Map<String, String> queryMap = parameter.getParameterMap();
		Call<CoordToAddress> call = querys.getCoordToAddress(queryMap);

		call.enqueue(new Callback<CoordToAddress>() {
			@Override
			public void onResponse(Call<CoordToAddress> call, Response<CoordToAddress> response) {
				callback.processResult(response);
			}

			@Override
			public void onFailure(Call<CoordToAddress> call, Throwable t) {
				callback.processResult(t);
			}
		});

	}

}
