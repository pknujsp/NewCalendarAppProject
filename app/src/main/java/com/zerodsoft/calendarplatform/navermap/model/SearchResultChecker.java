package com.zerodsoft.calendarplatform.navermap.model;

import com.zerodsoft.calendarplatform.common.classes.JsonDownloader;
import com.zerodsoft.calendarplatform.retrofit.HttpCommunicationClient;
import com.zerodsoft.calendarplatform.retrofit.Querys;
import com.zerodsoft.calendarplatform.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.KakaoLocalResponse;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.addressresponse.AddressKakaoLocalResponse;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultChecker {
	private SearchResultChecker() {
	}

	public static void checkAddress(LocalApiPlaceParameter localApiPlaceParameter, JsonDownloader<KakaoLocalResponse> callback) {
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
		Map<String, String> queryMap = localApiPlaceParameter.getParameterMap();
		Call<AddressKakaoLocalResponse> call = querys.getAddress(queryMap);

		call.enqueue(new Callback<AddressKakaoLocalResponse>() {
			@Override
			public void onResponse(Call<AddressKakaoLocalResponse> call, Response<AddressKakaoLocalResponse> response) {
				callback.processResult(response);
			}

			@Override
			public void onFailure(Call<AddressKakaoLocalResponse> call, Throwable t) {
				callback.processResult(t);
			}
		});
	}

	public static void checkPlace(LocalApiPlaceParameter localApiPlaceParameter, JsonDownloader<KakaoLocalResponse> callback) {
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
				callback.processResult(response);
			}

			@Override
			public void onFailure(Call<PlaceKakaoLocalResponse> call, Throwable t) {
				callback.processResult(t);
			}
		});
	}


	public static void checkExisting(LocalApiPlaceParameter addressParameter, LocalApiPlaceParameter placeParameter
			, JsonDownloader<List<KakaoLocalResponse>> callback) {

		final int requestCount = 2;
		JsonDownloader<KakaoLocalResponse> primaryCallback = new JsonDownloader<KakaoLocalResponse>() {
			int responseCount = 0;
			List<KakaoLocalResponse> kakaoLocalResponseList = new ArrayList<>();
			List<Exception> exceptionList = new ArrayList<>();

			@Override
			public void onResponseSuccessful(KakaoLocalResponse result) {
				++responseCount;
				kakaoLocalResponseList.add(result);
				onCompleted();
			}

			@Override
			public void onResponseFailed(Exception e) {
				++responseCount;
				exceptionList.add(e);
				onCompleted();
			}

			private void onCompleted() {
				if (requestCount == responseCount) {
					if (!exceptionList.isEmpty()) {
						callback.onResponseFailed(new Exception());
					} else {
						int succeed = 0;

						for (KakaoLocalResponse kakaoLocalResponse : kakaoLocalResponseList) {
							if (!kakaoLocalResponse.isEmpty()) {
								succeed++;
							}
						}

						if (succeed >= 1) {
							callback.onResponseSuccessful(kakaoLocalResponseList);
						} else {
							callback.onResponseFailed(new Exception());
						}
					}
				}
			}
		};

		checkAddress(addressParameter, primaryCallback);
		checkPlace(placeParameter, primaryCallback);
	}

}
