package com.zerodsoft.scheduleweather.navermap.interfaces;

import com.zerodsoft.scheduleweather.navermap.LocationItemViewPagerAdapter;
import com.zerodsoft.scheduleweather.navermap.MarkerType;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;

import java.util.List;

public interface IMapData {
	void createPoiItems(List<? extends KakaoLocalDocument> kakaoLocalDocuments, MarkerType markerType);

	void addPoiItems(List<? extends KakaoLocalDocument> kakaoLocalDocuments, MarkerType markerType);

	void removePoiItems(MarkerType... markerTypes);

	void removePoiItem(MarkerType markerType, int index);

	void removeAllPoiItems();

	void showPoiItems(MarkerType... markerTypes);

	void showPoiItems(MarkerType markerType, boolean isShow);

	void deselectPoiItem();

	int getPoiItemSize(MarkerType... markerTypes);

	void setLocationItemViewPagerAdapter(LocationItemViewPagerAdapter adapter, MarkerType markerType);
}
