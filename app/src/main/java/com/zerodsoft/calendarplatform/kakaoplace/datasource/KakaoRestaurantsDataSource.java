package com.zerodsoft.calendarplatform.kakaoplace.datasource;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import com.zerodsoft.calendarplatform.retrofit.HttpCommunicationClient;
import com.zerodsoft.calendarplatform.retrofit.Querys;
import com.zerodsoft.calendarplatform.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class KakaoRestaurantsDataSource extends PositionalDataSource<PlaceDocuments> {
	private final String NECESSARY_CATEGORY_NAME = "음식점";

	private Querys querys;
	private PlaceMeta placeMeta;
	private LocalApiPlaceParameter localApiPlaceParameter;
	private String categoryName;

	public KakaoRestaurantsDataSource(LocalApiPlaceParameter localApiParameter) {
		this.localApiPlaceParameter = localApiParameter;
	}

	@Override
	public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<PlaceDocuments> callback) {
		querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
		Map<String, String> queryMap = localApiPlaceParameter.getParameterMap();
		Call<PlaceKakaoLocalResponse> call = querys.getPlaceKeyword(queryMap);

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
				List<PlaceDocuments> classifiedList = classifyRestaurants(placeDocuments);
				callback.onResult(classifiedList, 0, classifiedList.size());

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
			Call<PlaceKakaoLocalResponse> call = querys.getPlaceKeyword(queryMap);

			call.enqueue(new Callback<PlaceKakaoLocalResponse>() {
				@Override
				public void onResponse(Call<PlaceKakaoLocalResponse> call, Response<PlaceKakaoLocalResponse> response) {
					List<PlaceDocuments> placeDocuments = response.body().getPlaceDocuments();
					placeMeta = response.body().getPlaceMeta();

					List<PlaceDocuments> classifiedList = classifyRestaurants(placeDocuments);
					callback.onResult(classifiedList);
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

	private List<PlaceDocuments> classifyRestaurants(List<PlaceDocuments> placeDocumentsList) {
		List<PlaceDocuments> classifiedList = new ArrayList<>();

		for (PlaceDocuments placeDocument : placeDocumentsList) {
			categoryName = placeDocument.getCategoryName().split(" > ")[0];
			if (categoryName.equals(NECESSARY_CATEGORY_NAME)) {
				classifiedList.add(placeDocument);
			}
		}

		return classifiedList;
	}
}
