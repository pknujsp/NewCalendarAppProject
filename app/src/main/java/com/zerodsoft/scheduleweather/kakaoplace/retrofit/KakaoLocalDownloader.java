package com.zerodsoft.scheduleweather.kakaoplace.retrofit;

import android.service.carrier.CarrierMessagingService;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.common.interfaces.OnProgressBarListener;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnKakaoLocalApiCallback;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoregioncoderesponse.CoordToRegionCode;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KakaoLocalDownloader {
	private KakaoLocalDownloader() {
	}


	public static void getPlaces(LocalApiPlaceParameter parameter, OnKakaoLocalApiCallback callback) {
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
		Map<String, String> queryMap = parameter.getParameterMap();
		Call<PlaceKakaoLocalResponse> call = null;

		if (parameter.getQuery() == null) {
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
				callback.onResultNoData();
			}
		});
	}

	public static void coordToAddress(LocalApiPlaceParameter parameter, OnKakaoLocalApiCallback callback) {
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
				callback.onResultNoData();
			}
		});

	}

	public static void coordToRegionCode(LocalApiPlaceParameter parameter, OnKakaoLocalApiCallback callback) {
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
		Map<String, String> queryMap = parameter.getParameterMap();
		Call<CoordToRegionCode> call = querys.getCoordToRegionCode(queryMap);

		call.enqueue(new Callback<CoordToRegionCode>() {
			@Override
			public void onResponse(Call<CoordToRegionCode> call, Response<CoordToRegionCode> response) {
				callback.processResult(response);
			}

			@Override
			public void onFailure(Call<CoordToRegionCode> call, Throwable t) {
				callback.onResultNoData();
			}
		});

	}

}