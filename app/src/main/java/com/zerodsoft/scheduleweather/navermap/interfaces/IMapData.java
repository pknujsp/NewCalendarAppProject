package com.zerodsoft.scheduleweather.navermap.interfaces;

import com.zerodsoft.scheduleweather.navermap.LocationItemViewPagerAdapter;
import com.zerodsoft.scheduleweather.navermap.MarkerType;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;

import java.util.List;

public interface IMapData {
	void createMarkers(List<? extends KakaoLocalDocument> kakaoLocalDocuments, MarkerType markerType);

	void addMarkers(List<? extends KakaoLocalDocument> kakaoLocalDocuments, MarkerType markerType);

	void removeMarkers(MarkerType... markerTypes);

	void removeMarker(MarkerType markerType, int index);

	void removeAllMarkers();

	void showMarkers(MarkerType... markerTypes);

	void showMarkers(MarkerType markerType, boolean isShow);

	void deselectMarker();

	void setLocationItemViewPagerAdapter(LocationItemViewPagerAdapter adapter, MarkerType markerType);
}
