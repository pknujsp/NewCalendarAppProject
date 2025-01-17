package com.zerodsoft.calendarplatform.navermap.model.datasource;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import com.zerodsoft.calendarplatform.retrofit.HttpCommunicationClient;
import com.zerodsoft.calendarplatform.retrofit.Querys;
import com.zerodsoft.calendarplatform.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceMeta;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PlaceItemDataSource extends PositionalDataSource<PlaceDocuments> {
	private Querys querys;
	private PlaceMeta placeMeta;
	private LocalApiPlaceParameter localApiPlaceParameter;

	public PlaceItemDataSource(LocalApiPlaceParameter localApiParameter) {
		this.localApiPlaceParameter = localApiParameter;
	}

	@Override
	public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<PlaceDocuments> callback) {
		querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
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
				List<PlaceDocuments> placeDocuments = null;
				if (response.body() == null) {
					placeDocuments = new ArrayList<>();
					placeMeta = new PlaceMeta();
				} else {
					placeDocuments = response.body().getPlaceDocuments();
					placeMeta = response.body().getPlaceMeta();
				}
				callback.onResult(placeDocuments, 0, placeDocuments.size());
			}

			@Override
			public void onFailure(Call<PlaceKakaoLocalResponse> call, Throwable t) {
				List<PlaceDocuments> placeDocuments = new ArrayList<>();
				placeMeta = new PlaceMeta();
				callback.onResult(placeDocuments, 0, placeDocuments.size());
			}
		});
	}

	@Override
	public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<PlaceDocuments> callback) {
		querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);

		if (!placeMeta.isEnd()) {
			localApiPlaceParameter.setPage(Integer.toString(Integer.parseInt(localApiPlaceParameter.getPage()) + 1));
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
					List<PlaceDocuments> placeDocuments = response.body().getPlaceDocuments();
					placeMeta = response.body().getPlaceMeta();

					callback.onResult(placeDocuments);
				}

				@Override
				public void onFailure(Call<PlaceKakaoLocalResponse> call, Throwable t) {
					List<PlaceDocuments> placeDocuments = new ArrayList<>();
					callback.onResult(placeDocuments);
				}
			});
		} else {
			callback.onResult(new ArrayList<PlaceDocuments>(0));
		}
	}

}
