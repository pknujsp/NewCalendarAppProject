package com.zerodsoft.calendarplatform.navermap.places.interfaces;

import com.zerodsoft.calendarplatform.navermap.MarkerType;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.calendarplatform.room.dto.FavoriteLocationDTO;

public interface MarkerOnClickListener {

	void onPOIItemSelectedByList(KakaoLocalDocument kakaoLocalDocument, MarkerType markerType);

	void onFavoritePOIItemSelectedByList(FavoriteLocationDTO favoriteLocationDTO);

	void onPOIItemSelectedByBottomSheet(int position, MarkerType markerType);
}
