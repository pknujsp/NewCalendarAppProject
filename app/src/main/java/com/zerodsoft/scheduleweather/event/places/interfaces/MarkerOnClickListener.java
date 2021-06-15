package com.zerodsoft.scheduleweather.event.places.interfaces;

import com.zerodsoft.scheduleweather.navermap.MarkerType;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

public interface MarkerOnClickListener {

	void onPOIItemSelectedByList(KakaoLocalDocument kakaoLocalDocument, MarkerType markerType);

	void onFavoritePOIItemSelectedByList(FavoriteLocationDTO favoriteLocationDTO);

	void onPOIItemSelectedByBottomSheet(int position, MarkerType markerType);
}
