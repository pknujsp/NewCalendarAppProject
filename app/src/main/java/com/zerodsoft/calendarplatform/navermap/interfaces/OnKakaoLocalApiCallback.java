package com.zerodsoft.calendarplatform.navermap.interfaces;

import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.KakaoLocalResponse;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.addressresponse.AddressKakaoLocalResponse;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.calendarplatform.room.dto.FavoriteLocationDTO;

import retrofit2.Response;

public interface OnKakaoLocalApiCallback {
	void onResultSuccessful(int type, KakaoLocalResponse result);

	void onResultNoData();

	default void processResult(Response<? extends KakaoLocalResponse> result) {
		if (result.body() == null) {
			onResultNoData();
		} else if (result.body().isEmpty()) {
			onResultNoData();
		} else {
			int type = 0;
			if (result.body() instanceof PlaceKakaoLocalResponse) {
				type = FavoriteLocationDTO.PLACE;
			} else if (result.body() instanceof AddressKakaoLocalResponse) {
				type = FavoriteLocationDTO.ADDRESS;
			} else if (result.body() instanceof CoordToAddress) {
				type = FavoriteLocationDTO.ADDRESS;
			}
			onResultSuccessful(type, result.body());
		}
	}
}
