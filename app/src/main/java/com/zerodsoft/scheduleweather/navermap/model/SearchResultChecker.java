package com.zerodsoft.scheduleweather.navermap.model;

import com.google.gson.JsonObject;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.navermap.model.callback.CheckerCallback;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.MidLandFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.MidTaParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressKakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.weather.mid.MidFcstRoot;

import java.util.Calendar;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultChecker {
	private SearchResultChecker() {
	}

	public static void checkAddress(LocalApiPlaceParameter localApiPlaceParameter, CheckerCallback<DataWrapper<KakaoLocalResponse>> callback) {
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
		Map<String, String> queryMap = localApiPlaceParameter.getParameterMap();
		Call<AddressKakaoLocalResponse> call = querys.getAddress(queryMap);

		call.enqueue(new Callback<AddressKakaoLocalResponse>() {
			@Override
			public void onResponse(Call<AddressKakaoLocalResponse> call, Response<AddressKakaoLocalResponse> response) {
				DataWrapper<KakaoLocalResponse> dataWrapper = null;

				if (response.body() == null) {
					dataWrapper = new DataWrapper<>(new NullPointerException());
				} else {
					dataWrapper = new DataWrapper<>(response.body());
				}

				callback.add(dataWrapper);
				if (callback.getResponseCount() == callback.getTotalRequestCount()) {
					callback.onResult();
				}
			}

			@Override
			public void onFailure(Call<AddressKakaoLocalResponse> call, Throwable t) {
				Exception exception = new Exception(t);
				DataWrapper<KakaoLocalResponse> dataWrapper = new DataWrapper<>(exception);

				callback.add(dataWrapper);
				if (callback.getResponseCount() == callback.getTotalRequestCount()) {
					callback.onResult();
				}
			}
		});
	}

	public static void checkPlace(LocalApiPlaceParameter localApiPlaceParameter, CheckerCallback<DataWrapper<KakaoLocalResponse>> callback) {
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
		Map<String, String> queryMap = localApiPlaceParameter.getParameterMap();
		Call<PlaceKakaoLocalResponse> call = null;

		if (localApiPlaceParameter.getQuery() == null) {
			call = querys.getPlaceCategory(queryMap);
		} else {
			call = querys.getPlaceKeyword(queryMap);
		}

		call.enqueue(new Callback<PlaceKakaoLocalResponse>() {
			@Override
			public void onResponse(Call<PlaceKakaoLocalResponse> call, Response<PlaceKakaoLocalResponse> response) {
				DataWrapper<KakaoLocalResponse> dataWrapper = null;

				if (response.body() == null) {
					dataWrapper = new DataWrapper<>(new NullPointerException());
				} else {
					dataWrapper = new DataWrapper<>(response.body());
				}

				callback.add(dataWrapper);
				if (callback.getResponseCount() == callback.getTotalRequestCount()) {
					callback.onResult();
				}
			}

			@Override
			public void onFailure(Call<PlaceKakaoLocalResponse> call, Throwable t) {
				Exception exception = new Exception(t);
				DataWrapper<KakaoLocalResponse> dataWrapper = new DataWrapper<>(exception);

				callback.add(dataWrapper);
				if (callback.getResponseCount() == callback.getTotalRequestCount()) {
					callback.onResult();
				}
			}
		});
	}


	public static void checkExisting(LocalApiPlaceParameter addressParameter, LocalApiPlaceParameter placeParameter
			, CheckerCallback<DataWrapper<KakaoLocalResponse>> callback) {
		callback.setTotalRequestCount(2);

		checkAddress(addressParameter, callback);
		checkPlace(placeParameter, callback);
	}
	
}
